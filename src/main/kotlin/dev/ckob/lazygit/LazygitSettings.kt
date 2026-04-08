package dev.ckob.lazygit

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

@State(
    name = "dev.ckob.lazygit.LazygitSettingsState",
    storages = [Storage("LazygitPlugin.xml")]
)
class LazygitSettingsState : PersistentStateComponent<LazygitSettingsState> {
    var customConfigPath: String = ""
    var customExecutablePath: String = ""

    override fun getState(): LazygitSettingsState = this
    override fun loadState(state: LazygitSettingsState) {
        this.customConfigPath = state.customConfigPath
        this.customExecutablePath = state.customExecutablePath
    }

    companion object {
        val instance: LazygitSettingsState
            get() = ApplicationManager.getApplication().getService(LazygitSettingsState::class.java)
    }
}

class LazygitSettingsConfigurable : Configurable {
    private var panel: DialogPanel? = null
    private val state = LazygitSettingsState.instance

    override fun createComponent(): JComponent {
        panel = panel {
            row("Lazygit executable path:") {
                cell(TextFieldWithBrowseButton())
                    .applyToComponent {
                        addBrowseFolderListener(
                            com.intellij.openapi.ui.TextBrowseFolderListener(
                                FileChooserDescriptor(true, false, false, false, false, false).withTitle("Select Lazygit Executable"),
                                null
                            )
                        )
                    }
                    .bindText(state::customExecutablePath)
                    .align(AlignX.FILL)
                    .comment("Optional: Absolute path to the lazygit executable (e.g. /usr/local/bin/lazygit). If left empty, the plugin will search your system PATH.")
            }
            row("Custom config path:") {
                cell(TextFieldWithBrowseButton())
                    .applyToComponent {
                        addBrowseFolderListener(
                            com.intellij.openapi.ui.TextBrowseFolderListener(
                                FileChooserDescriptor(true, false, false, false, false, false).withTitle("Select Lazygit Configuration File"),
                                null
                            )
                        )
                    }
                    .bindText(state::customConfigPath)
                    .align(AlignX.FILL)
                    .comment("Optional: Absolute path to a custom lazygit config file (e.g. ~/.config/lazygit/config.yml). Multiple files can be separated by a comma. If left empty, the plugin will attempt to load configuration from default OS locations.")
            }
        }
        return panel!!
    }

    override fun isModified(): Boolean {
        return panel?.isModified() ?: false
    }

    override fun apply() {
        panel?.apply()
    }

    override fun reset() {
        panel?.reset()
    }

    override fun getDisplayName(): String = "Lazygit"
}
