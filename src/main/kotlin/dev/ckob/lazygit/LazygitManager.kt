package dev.ckob.lazygit

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFile
import com.intellij.util.EnvironmentUtil
import org.jetbrains.plugins.terminal.ShellStartupOptions
import org.jetbrains.plugins.terminal.TerminalToolWindowManager
import java.beans.PropertyChangeListener
import java.io.File
import javax.swing.JComponent
import kotlin.concurrent.thread
import kotlin.math.max

// --- MANAGER ---

object LazygitManager {
    private val IPC_FILE_KEY = com.intellij.openapi.util.Key.create<File>("lazygit.ipcFile")
    private val OVERLAY_FILE_KEY = com.intellij.openapi.util.Key.create<File>("lazygit.overlayFile")
    private val WATCH_THREAD_KEY = com.intellij.openapi.util.Key.create<Thread>("lazygit.watchThread")
    
    fun toggleLazygit(project: Project) {
        val editorManager = FileEditorManager.getInstance(project)
        val existingFile = editorManager.openFiles.find { it is LazygitVirtualFile }
        if (existingFile != null) {
            editorManager.openFile(existingFile, true)
            return
        }

        setupIpc(project)
        
        val lazygitConfig = getLazygitConfigPath()
        val overlayFile = project.getUserData(OVERLAY_FILE_KEY)
        
        // Create our custom virtual file and tell the IDE to open it
        val file = LazygitVirtualFile(lazygitConfig, overlayFile!!.absolutePath)
        editorManager.openFile(file, true)
    }
    
    private fun setupIpc(project: Project) {
        var ipcFile = project.getUserData(IPC_FILE_KEY)
        if (ipcFile != null && ipcFile.exists()) return
        
        val suffix = "${System.currentTimeMillis()}-${ProcessHandle.current().pid()}"
        val tmpDir = System.getProperty("java.io.tmpdir")
        
        ipcFile = File(tmpDir, "lazygit-intellij-ipc-$suffix.tmp")
        ipcFile.createNewFile()
        project.putUserData(IPC_FILE_KEY, ipcFile)
        
        val overlayYaml = """
            os:
              edit: 'printf "%s\t0\n" {{filename}} > "${ipcFile.absolutePath.replace("\\", "\\\\")}"'
              editAtLine: 'printf "%s\t%s\n" {{filename}} "{{line}}" > "${ipcFile.absolutePath.replace("\\", "\\\\")}"'
            notARepository: skip
            promptToReturnFromSubprocess: false
        """.trimIndent()
        
        val overlayFile = File(tmpDir, "lazygit-intellij-config-$suffix.yml")
        overlayFile.writeText(overlayYaml)
        project.putUserData(OVERLAY_FILE_KEY, overlayFile)
        
        startIpcWatcher(project, ipcFile)
    }
    
    private fun startIpcWatcher(project: Project, file: File) {
        var watchThread = project.getUserData(WATCH_THREAD_KEY)
        watchThread?.interrupt()
        
        watchThread = thread(start = true, isDaemon = true) {
            try {
                var lastModified = file.lastModified()
                while (!Thread.currentThread().isInterrupted && !project.isDisposed) {
                    val currentModified = file.lastModified()
                    if (currentModified > lastModified) {
                        val content = file.readText().trim()
                        if (content.isNotEmpty()) {
                            handleIpcMessage(project, content)
                            file.writeText("")
                            lastModified = file.lastModified()
                        } else {
                            lastModified = currentModified
                        }
                    }
                    Thread.sleep(50)
                }
            } catch (_: InterruptedException) {
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        project.putUserData(WATCH_THREAD_KEY, watchThread)
    }
    
    private fun handleIpcMessage(project: Project, content: String) {
        content.lines().filter { it.isNotBlank() }.forEach { line ->
            val parts = line.split("\t")
            val filePath = parts.getOrNull(0)?.trim() ?: return@forEach
            val lineNum = parts.getOrNull(1)?.toIntOrNull() ?: 0

            ApplicationManager.getApplication().invokeLater {
                val file = File(if (File(filePath).isAbsolute) filePath else "${project.basePath}/$filePath")
                val virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)

                if (virtualFile != null) {
                    val descriptor = OpenFileDescriptor(project, virtualFile, max(0, lineNum - 1), 0)
                    FileEditorManager.getInstance(project).openTextEditor(descriptor, true)
                }
            }
        }
    }
    
    private fun getLazygitConfigPath(): String {
        val os = System.getProperty("os.name").lowercase()
        val home = System.getProperty("user.home")
        
        return when {
            os.contains("mac") -> {
                System.getenv("XDG_CONFIG_HOME")?.let { "$it/lazygit/config.yml" }
                    ?: "$home/Library/Application Support/lazygit/config.yml"
            }
            os.contains("win") -> {
                System.getenv("APPDATA")?.let { "$it/lazygit/config.yml" }
                    ?: "$home/AppData/Roaming/lazygit/config.yml"
            }
            else -> {
                System.getenv("XDG_CONFIG_HOME")?.let { "$it/lazygit/config.yml" }
                    ?: "$home/.config/lazygit/config.yml"
            }
        }
    }
}

// --- VIRTUAL FILE ---

class LazygitVirtualFile(
    val lazygitConfigPath: String,
    val overlayFilePath: String
) : LightVirtualFile("LazyGit")

// --- FILE EDITOR ---

class LazygitEditor(
    private val project: Project,
    private val virtualFile: LazygitVirtualFile
) : UserDataHolderBase(), FileEditor {

    private val terminalWidget: com.intellij.terminal.ui.TerminalWidget

    init {
        val terminalRunner = TerminalToolWindowManager.getInstance(project).terminalRunner
        
        // Use EnvironmentUtil to get the robust system PATH (handles Homebrew on Mac)
        val envs = EnvironmentUtil.getEnvironmentMap().toMutableMap()
        
        // Find lazygit executable robustly
        val lazygitPath = PathEnvironmentVariableUtil.findInPath("lazygit", envs["PATH"], null)?.absolutePath ?: "lazygit"
        
        val shellCommand = if (SystemInfo.isWindows) {
            listOf("cmd.exe", "/c", "\"$lazygitPath\" --use-config-file=\"${virtualFile.lazygitConfigPath},${virtualFile.overlayFilePath}\"")
        } else {
            listOf("/bin/sh", "-c", "\"$lazygitPath\" --use-config-file=\"${virtualFile.lazygitConfigPath},${virtualFile.overlayFilePath}\"; exit")
        }
        
        val startupOptions = ShellStartupOptions.Builder()
            .workingDirectory(project.basePath)
            .shellCommand(shellCommand)
            .envVariables(envs)
            .build()
            
        terminalWidget = terminalRunner.startShellTerminalWidget(this, startupOptions, true)
        
        terminalWidget.addTerminationCallback({
            ApplicationManager.getApplication().invokeLater {
                FileEditorManager.getInstance(project).closeFile(virtualFile)
            }
        }, this)
    }

    override fun getComponent(): JComponent = terminalWidget.component
    override fun getPreferredFocusedComponent(): JComponent = terminalWidget.preferredFocusableComponent
    override fun getName(): String = "LazyGit"
    override fun getFile(): VirtualFile = virtualFile
    override fun isModified(): Boolean = false
    override fun isValid(): Boolean = true
    override fun addPropertyChangeListener(listener: PropertyChangeListener) {}
    override fun removePropertyChangeListener(listener: PropertyChangeListener) {}
    override fun dispose() {}
    override fun setState(state: FileEditorState) {}
}

// --- EDITOR PROVIDER ---

class LazygitEditorProvider : FileEditorProvider, DumbAware {
    override fun accept(project: Project, file: VirtualFile): Boolean {
        return file is LazygitVirtualFile
    }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        return LazygitEditor(project, file as LazygitVirtualFile)
    }

    override fun getEditorTypeId(): String = "LazygitEditor"
    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.HIDE_DEFAULT_EDITOR
}