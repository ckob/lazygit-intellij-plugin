# Lazygit Changelog

## [0.2.3]
### Added
- Integrated official JetBrains plugin template CI/CD workflows and automated changelog generation.

## [0.2.2]
### Fixed
- Resolved 'scheduled for removal' API warnings in the settings panel to ensure full compatibility with IntelliJ 2026.1 and future releases.

## [0.2.1]
### Fixed
- Resolved deprecation warnings related to the UI DSL v1 in the settings panel to ensure compatibility with IntelliJ 2026.1 and future releases.

## [0.2.0]
### Added
- Added a settings page under `Settings -> Tools -> Lazygit` to specify custom config file and executable paths.

### Fixed
- Improved default configuration path resolution, especially for macOS users using `~/.config/lazygit`.

## [0.1.3]
### Added
- Added IdeaVim integration. Exposes `g:loaded_lazygit = 1` to Vimscript for conditional keybindings.

### Changed
- Bumped minimum required IDE version to 2024.1 to support the modern IdeaVim API.

### Fixed
- Disabled dynamic plugin unloading to prevent IDE freezes when updating or disabling the plugin. A restart is now required when changing plugin state.

## [0.1.2]
### Added
- Added support for opening multiple selected files at once (using visual/select mode and pressing 'e').

## [0.1.1]
### Fixed
- Correctly isolate lazygit instances per project window to fix file opening in multiple IDE instances.

## [0.1.0]
### Added
- Toggle LazyGit in a full-screen editor tab.
- Native file opening (press 'e' in LazyGit to open in IDE).
- Automatic closing of editor tab when exiting LazyGit.
- IdeaVim support via :action Lazygit.Toggle.