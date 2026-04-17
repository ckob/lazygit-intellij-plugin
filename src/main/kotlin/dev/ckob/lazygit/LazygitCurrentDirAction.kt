package dev.ckob.lazygit

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class LazygitCurrentDirAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val workingDirectory = virtualFile?.let {
            if (it.isDirectory) it.path else it.parent?.path
        }
        LazygitManager.toggleLazygit(project, workingDirectory)
    }
}
