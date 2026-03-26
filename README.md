# LazyGit for IntelliJ

Native integration of LazyGit directly in IntelliJ IDEs (such as IDEA, Rider, WebStorm, etc.).

This plugin is inspired by the `lazygit-vscode` extension. It allows you to toggle LazyGit in the IntelliJ Terminal tool window, and use the `e` or `o` shortcut to open a file directly in a new editor tab from the LazyGit window without any manual configuration.

## Usage
- Search for the action `Lazygit: Toggle` in the IDE (Double Shift or `Cmd+Shift+A`).
- You can assign a custom keyboard shortcut in `Settings -> Keymap`.

### IdeaVim Configuration
If you use IdeaVim, you can map the toggle action in your `.ideavimrc` file:

```vim
" Map <leader>gg to toggle LazyGit
nmap <leader>gg <Action>(Lazygit.Toggle)
```

## Features
- Toggle LazyGit in a full-screen editor tab.
- Re-uses existing LazyGit tab if already open.
- Use `e` or `o` to open a file in a new tab from the LazyGit window.
- Automatic closing of the editor tab when exiting LazyGit (`q`).

## How it works
This plugin automatically configures LazyGit's edit commands using an overlay config and IPC to open files through the IntelliJ API instead of an external process. 

## Development

This project uses the standard Gradle-based IntelliJ Platform Plugin structure.

1. Open this repository in IntelliJ IDEA.
2. Wait for Gradle to sync and download dependencies.
3. Run the `Run Plugin` run configuration (or execute `./gradlew runIde` from the terminal) to launch a sandbox IDE with the plugin installed.
4. Build the plugin for distribution using the `Build Plugin` task (or `./gradlew buildPlugin`). The resulting ZIP file will be generated in `build/distributions/`.

## Manual Installation
1. Go to `Settings` -> `Plugins` -> ⚙️ (Gear Icon) -> `Install Plugin from Disk...`
2. Select the `lazygit-intellij-1.0-SNAPSHOT.zip` file generated in `build/distributions/`.
3. Restart your IDE.
