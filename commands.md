## vim
* dw - Delete the rest of the word
* D - Delete the rest of the line
* w - Jump to the next word
* b - Jump to the beginning of the current word
* dd - Delete the entire current line
* {x}dd - Delete {x} lines
* gg - Jump to the beginning of the file
* G - Jump to the end of the file
* \>\> - Indent forward. Press '.' to keep indenting.
* << - Indent backward. Press '.' to keep indenting.
* ctrl-n; ctrl-p - Bring up word completion and cycle between options.
* ctrl-i - Retrace your movements in a file forwards.
* ctrl-o - Retrace your movements in a file backwards.
* ctrl-o (in insert mode) - Temporarily return to normal mode to execute a single command. After command is executed, return to insert mode with the cursor placed wherever the command left it.
* /{pattern} - Search
* %s/{OLD_STRING}/{NEW_STRING}/gc - Find and replace
* ctrl-] - Jump to defintion
* ctrl-\ a|c|d|e|f|g|i|s|t - Find usages of the current symbol 
* ctrl-t - Go backwards in the tag stack
* :e - Edit the current file. Useful to re-edit the current file when it has been changed outside of Vim.
* \% - Move cursor to the matching bracket.
* gq - Reformat the paragraph
* V - Enter visual line mode (different from visual block mode)
* ctrl-v - Enter visual block mode (different from visual line mode)
* :help {command} - Get information about what the command does.
* cw - Delete the word from the current cursor position and enter insert mode
* ciw - Delete the whole word under the cursor and enter insert mode
* \s - Search and replace occurrences of the current word under the cursor (custom mapping)
* . - Repeat the last command
* :retab - Change all existing tab characters to match the current tab settings
* s - 
* S
* I - Insert at the beginning of the line
* :term - Open terminal in a new split window
* ctrl-w - Use to navigate windows
* ctrl-w ctrl-w - Switch to the next closest window
* :noh - Turn off highlighting until the next search
* \* - Search for the word currently under the cursor
* g* - Search for the partial word currently under the cursor
* [I - Show lines with matching word under the cursor
* n - Repeat forward search
* N - Repeat backward search
* :lopen
* :lclose
* Y (or yy) - Yank the current line, including the newline character at the end of the line
* copy to and paste from system clipboard
* \* - Search forward for the word under your cursor
* \# - Search backward for the word under your cursor
* yiw - Yank the entire word under your cursor

## tmux
* ctrl-b ( - Move to the previous session.
* ctrl-b ) - Move to the next session.
* ctrl-b s - Show all sessions.
* ctrl-b : break-pane -dP - Send current pane to a window in the background
* ctrl-b : join-pane -vs {identifier} - Bring back {identifier} pane to the foreground
* ctrl-b $ - Rename session
- ctrl-b z
- ctrl-b %
- ctrl-b <-
- ctrl-b ->

## command line
- ^{old text}^{new text} - Replace all occurrences of `old text` in the previous command with `new text` and run the new command.
* ctrl-a - Go to the beginning of the line.
* ctrl-e - Go to the end of the line.
* opt-arrow_keys - Move within line by skipping words.
* wc -l - Count the lines
