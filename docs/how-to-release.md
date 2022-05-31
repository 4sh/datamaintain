# Release Datamaintain

At the moment, there is no CI to release Datamaintain. We release 3 modules :
* JitPack
* CLI
* CLI images

## JitPack
TODO

## CLI
TODO

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
