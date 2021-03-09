## Installation in a project with already executed scripts

When you already have executed scripts on your project and you want to start using Datamaintain, please follow those steps:
- Add Datamaintain as a dependency to your project, as described [here](../README.md#installation). 
- Download the CLI from the version you are aiming for. The CLI is released as an asset in every Datamaintain release, you may find it in the [releases](https://github.com/4sh/datamaintain/releases).
- Execute the CLI using the following command replacing the arguments with the values you want. An explanation about each configuration key is provided here.
```bash

./datamaintain-cli --db-type $DB_TYPE --mongo-uri $MONGO_URI update-db --path $PATH --identifier-regex $REGEX --execution-mode FORCE_MARK_AS_EXECUTED
```
