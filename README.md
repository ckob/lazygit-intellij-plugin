# LazyGit for IntelliJ

Native integration of LazyGit directly in IntelliJ IDEs (such as IDEA, Rider, WebStorm, etc.).

This plugin is inspired by the `lazygit-vscode` extension. It allows you to toggle LazyGit in a full-screen editor tab, and use the `e` shortcut to open a file directly in the current IDE instance without any manual configuration.

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
- **Zero Configuration:** Works out of the box without any manual setup of `$EDITOR` or Lazygit config files.
- **Smart Routing:** Files always open in the same IDE instance where Lazygit is running, even with multiple projects or IDEs open.
- **Immersive UI:** Toggles LazyGit in a dedicated, full-screen editor tab rather than a tool window.
- **Re-uses existing tab:** Automatically focuses the existing LazyGit tab if it's already open.
- **Native Edit:** Use `e` to open a file (or multiple selected files via visual/select mode) in new tabs from the LazyGit window.
- **Automatic Cleanup:** The editor tab closes automatically when you exit LazyGit (`q`).

## How it works
This plugin automatically configures LazyGit's edit commands using an overlay config and a custom IPC bridge. This ensures that opening a file from LazyGit always targets the specific IDE project window where you are currently working.

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
