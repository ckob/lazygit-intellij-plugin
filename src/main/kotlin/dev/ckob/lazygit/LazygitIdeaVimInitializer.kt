package dev.ckob.lazygit

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimInt

class LazygitIdeaVimInitializer : ProjectActivity {
    override suspend fun execute(project: Project) {
        injector.variableService.storeGlobalVariable("loaded_lazygit", VimInt.ONE)
    }
}