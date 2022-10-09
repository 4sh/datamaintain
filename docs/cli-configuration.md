# Options
|Names|Default value|Needs argument|Possible arguments|Description|Examples|
|---|---|---|---|---|---|
|--version, --v, -v, -version|No default value|✘|N/A|Show the version and exit||
|--generate-completion|No default value|✔|```bash``` or ```zsh``` or ```fish```|||
|--working-directory-path, --wd|No default value|✔| |path to the working directory. Can be relative but prefer absolute path. All relative paths configured will be relative to this path if set.||
|--config-file-path|No default value|✔| |Path to config file. File must exist.|```myProject/src/main/resources/config/datamaintain.properties```|
|--db-type|mongo|✔|```mongo``` or ```jdbc```|db type||
|--db-uri|No default value|✔|TEXT|mongo uri with at least database name. Ex: mongodb://localhost:27017/newName|```mongodb://localhost:27017/newName```|
|--trust-uri||✘|N/A|Deactivate all controls on the URI you provide Datamaintain||
|--mongo-tmp-path|/tmp/datamaintain.tmp|✔|TEXT|mongo tmp file path||
|--config||✘|N/A|Print the configuration without executing the subcommand||
|-h, --help|No default value|✘|N/A|Display command help and exit||
# Subcommands
## update-db
### Options

|Names|Default value|Needs argument|Possible arguments|Description|Examples|
|---|---|---|---|---|---|
|--path|./scripts/|✔|TEXT|path to directory containing scripts|```src/main/resources/scripts/```|
|--identifier-regex|(.*)|✔|TEXT|regex to extract identifier part from scripts|```v(.*)_.*```|
|--whitelisted-tags|No default value|✔|TEXT|tags to whitelist (separated by ',')|```WHITELISTED_TAG1,WHITELISTED_TAG2```|
|--blacklisted-tags|No default value|✔|TEXT|tags to blacklist (separated by ',')|```BLACKLISTED_TAG1,BLACKLISTED_TAG2```|
|--tags-to-play-again|No default value|✔|TEXT|tags to play again at each datamaintain execution (separated by ',')|```TAG_TO_PLAY_AGAIN1,TAG_TO_PLAY_AGAIN2```|
|--create-tags-from-folder||✘|N/A|create automatically tags from parent folders||
|--execution-mode|NORMAL|✔|```NORMAL``` or ```DRY```|execution mode||
|--action|RUN|✔|```RUN``` or ```MARK_AS_EXECUTED``` or ```OVERRIDE_EXECUTED```|script action||
|--allow-auto-override||✘|N/A|Allow datamaintain to automaticaly override scripts||
|--verbose||✘|N/A|verbose||
|--save-db-output||✘|N/A|save your script and db output||
|--print-db-output||✘|N/A|print your script and db output||
|--tag|No default value|✔| |Tag defined using glob path matchers. To define multiple tags, use option multiple times. Syntax example: MYTAG1=[pathMatcher1, pathMatcher2]|```MYTAG1=[pathMatcher1, pathMatcher2]```|
|--rule|No default value|✔|```SameScriptsAsExecutedCheck```|check rule to play. To define multiple rules, use option multiple times.||
|--mongo-shell|mongo|✔|```mongo``` or ```mongosh```|mongo binary, can be mongo or mongosh. mongo by default||
|--porcelain||✘|N/A|for each executed script, display relative path to scan path||
|--flags|No default value|✔|TEXT|add a flag on the executed scripts. To define multiple rules, use option multiple times.||
|-h, --help|No default value|✘|N/A|Display command help and exit||
## list
### Options

|Names|Default value|Needs argument|Possible arguments|Description|Examples|
|---|---|---|---|---|---|
|-h, --help|No default value|✘|N/A|Display command help and exit||
## mark-script-as-executed
### Options

|Names|Default value|Needs argument|Possible arguments|Description|Examples|
|---|---|---|---|---|---|
|--path|./scripts/|✔|TEXT|path to the script you want to mark as executed|```scripts/myScript1.js```|
|--verbose||✘|N/A|verbose||
|-h, --help|No default value|✘|N/A|Display command help and exit||
## generate-completion
### Options

|Names|Default value|Needs argument|Possible arguments|Description|Examples|
|---|---|---|---|---|---|
|-h, --help|No default value|✘|N/A|Display command help and exit||
