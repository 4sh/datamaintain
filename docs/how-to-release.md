# Release Datamaintain

At the moment, there is no CI to release Datamaintain. We release 3 modules :
* artifacts on maven central
* CLI
* CLI images

## Maven central
- Tag your version
- Wait for the [publishing job](https://github.com/4sh/datamaintain/actions/workflows/publish_packages_to_maven_central.yaml) end
- Go to [the maven central repository manager](https://s01.oss.sonatype.org/#welcome)
- Log in using the [Datamaintain credentials](https://vaultier.4sh.fr/#/workspaces/w/datamaintain/vaults/v/sonatype/cards/c/compte-sonatype/secrets)
- You should see your release attempt in the staging repositories, close your staging repository. Once all the verifications were successful, you should receive a mail to tell you that the closing is done.
- Release your staging repository

## CLI
### Rebuild the documentation
```
gradlew rebuildDocumentation
```
### Rebuild the CLI autocompletion scripts
```
gradlew rebuildAutoCompletion
```
This will generate auto-completion scripts for Bash and Zsh in the docs/auto-completion subdirectory.
### Rebuild the CLI application
todo

## CLI images
### Prerequisites
Your docker CLI must have access to Datamaintain project, follow the
[Github documentation](https://docs.github.com/en/free-pro-team@latest/packages/guides/configuring-docker-for-use-with-github-packages#authenticating-with-a-personal-access-token)

### Release
Build datamaintain with gradle :
```
./gradlew build -Denv=prod
```

Execute `modules/cli/docker-build-push.sh`, you need to pass the Datamaintain version
for example for release Datamaintain 1.2 :
```
./docker-build-push.sh 1.2
```

This script build and upload images to GitHub, go to 
[package page](https://github.com/orgs/4sh/packages?repo_name=datamaintain), 
you should see your release.
