# LazyGit for IntelliJ

<!-- Plugin description -->
Bring the speed and elegance of **Lazygit** directly into your JetBrains IDE. No more context switching between your terminal and your editor.

### ✨ Zero-Config Native File Editing
Forget about writing custom bash scripts or fighting with complex `$EDITOR` configurations. This plugin features a custom, high-performance IPC (Inter-Process Communication) bridge. **Simply press `e` on any file in Lazygit**, and it will instantly open right in your IDE's editor. It works completely out of the box in *any* IntelliJ-based IDE without a single line of extra configuration.

Native integration of [LazyGit](https://github.com/jesseduffield/lazygit) directly in IntelliJ IDEs (such as IDEA, Rider, WebStorm, etc.).

This plugin is inspired by the `lazygit-vscode` extension. It allows you to toggle LazyGit in a full-screen editor tab, and use the `e` shortcut to open a file directly in the current IDE instance without any manual configuration.

## Installation
- Install the plugin from the [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/30919-lazygit).
- Alternatively, search for **"Lazygit"** (by ckob) directly in your IDE via `Settings` -> `Plugins` -> `Marketplace`.

## Usage
- Search for the actions `Lazygit: Toggle (Project Root)` and `Lazygit: Toggle (Current File Directory)` in the IDE (Double Shift or `Cmd+Shift+A`).
- **Default Shortcut (Project Root):** `Ctrl+Alt+G` (or `Cmd+Option+G` on macOS).
- You can assign custom keyboard shortcuts in `Settings -> Keymap`.

### IdeaVim Configuration
If you use IdeaVim, you can map the toggle actions in your `.ideavimrc` file. For example, to match LazyVim's default keybindings:

```vim
" Map <leader>gg to open LazyGit at the project root
nmap <leader>gg <Action>(Lazygit.Toggle)

" Map <leader>gG to open LazyGit at the current file's directory
nmap <leader>gG <Action>(Lazygit.ToggleCurrentDir)
```

## Features
- **Zero Configuration:** Works out of the box without any manual setup of `$EDITOR` or Lazygit config files.
- **Smart Routing:** Files always open in the same IDE instance where Lazygit is running, even with multiple projects or IDEs open.
- **Immersive UI:** Toggles LazyGit in a dedicated, full-screen editor tab rather than a tool window.
- **Re-uses existing tab:** Automatically focuses the existing LazyGit tab if it's already open.
- **Native Edit:** Use `e` to open a file (or multiple selected files via visual/select mode) in new tabs from the LazyGit window.
- **Automatic Cleanup:** The editor tab closes automatically when you exit LazyGit (`q`).
- **Custom Configuration:** Specify a custom Lazygit configuration file and executable path in `Settings -> Tools -> Lazygit`.

<!-- Plugin description end -->

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
2. Select the `lazygit-intellij-<version>.zip` file generated in `build/distributions/`.
3. Restart your IDE.

## Acknowledgments
- [LazyGit](https://github.com/jesseduffield/lazygit) by Jesse Duffield.
- The [lazygit-vscode](https://github.com/tom-pollak/lazygit-vscode) extension by Tom Pollak for the original inspiration.
