# SYNOPSIS
Auto-completion scripts to be used with bash and zsh.

# BUILD
Run 
```./gradlew rebuildAutocompletion```
in the project's root directory. Afterwards, you'll find the generated scripts in this directory.

# USAGE
Source the script for your shell, e.g. for Bash:

```source bash-autocomplete.sh```

Afterwards, you can use 
```datamaintain <TAB>```
to get a list of available commands.