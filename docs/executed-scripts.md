# Executed scripts in your database

Your scripts executions will be stored in your database. In Mongo, you will have a collection named 
```executedScripts``` that will contain executed scripts, as defined below:

| Attribute | Description |
|---|---|
| id | A technical identifier | 
| name | The filename of the script | 
| checksum | A checksum of the file | 
| identifier | The script identifier extracted by Datamaintain assuming the configured pattern | 
| executionStatus | The status relative to the action. Should always be OK. | 
| action | The last action done : RUN, MARK_AS_EXECUTED, OVERRIDE_EXECUTED | 
| executionDurationInMillis | Duration of your script execution, in milliseconds. | 
| executionOutput | Script/db output saved (if activated via config) | 
