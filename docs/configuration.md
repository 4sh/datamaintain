# Datamaintain configuration

## Core configuration

| Key | Description | Default value | Mandatory? | Values examples |
|---|---|---|---|---|
| name | Allow to name your config. For now will only be used to logging purpose | None | no |  |
| working.directory.path | Indicates the directory to use to find relative paths | Java default working directory path | no |  |
| parent.config.path | Path to an other configuration file to inherit properties. Can be absolute or relative to `working.directory.path` | None | no |  |
| default.script.action | The default script action | ```RUN``` | no | ```RUN``` or ```MARK_AS_EXECUTED``` |
| scan.path | Path to the folder containing all your scripts. Can be absolute or relative to `working.directory.path` | ```./scripts/``` | yes |  |
| scan.identifier.regex | Regex that will be used to determine an identifier for each file. It has to contain a capturing group. Identifiers are then used to sort the scripts before running them. | ```(.*)``` (with this regex, the script's whole name will be its identifier) | no | With the regex ```(.*?)_.*```, a script named ```1.23_my-script.js``` will have ```1.23``` as its identifier  |
| scan.tags.createFromFolder | If true, scripts will have their parent folders names as tags. Relative path to ```scan.path``` is used.  | ```false``` | no | ```false``` or ```true``` |
| tag.*your_tag* | Glob paths to your scripts that you want to apply the tag "your_tag" on. To declare multiple tags, you will have to add multiple properties in your settings. A tag ```my_tag``` will have as as property name ```tag.my_tag``` **WARNING:** ALWAYS declare your tags using absolute paths. Relative paths and even using a tilde (~) won't do the trick. |  | no | ```[data/*, script1.js, old/old_script1.js]``` |
| filter.filename.regex | Scripts that have a filename matching the regex will be considered | ```.*``` | yes | ```.*\.js$``` |
| filter.tags.whitelisted | Scripts that have these tags will be considered | None | no | ```DATA,tag``` |
| filter.tags.blacklisted | Scripts that have these tags will be ignored. A script having a whitelisted tag and a blacklisted tag will be ignored | None | no | ```DATA,tag``` |
| execution.mode | Execution mode. Possible values:<br />- ```NORMAL```: Regular execution: your scripts will be run on your database.<br />- ```DRY```: Scripts will not be executed. A full report of what would happen is you ran Datamaintain normally will be logged.<br /> | ```NORMAL``` | no | ```NORMAL```, ```DRY``` |
| verbose | If true, more logs will be printed. **WARNING:** Can't be used alongside with porcelain. If both are set, porcelain will prevail | ```false``` | no | ```true``` or ```false``` |
| prune.tags.to.run.again | Scripts that have these tags will be run, even they were already executed  | None | no | ```tag,again``` |
| prune.scripts.override.executed | Allow datamaintain to override a script if it detect a checksum change on a script already runned (assuming its filename) | ```false``` | no | ```true``` or ```false``` |
| db.trust.uri | Bypass all checks that could be done on your URI because you are very sure of it and think our checks are just liars | ```false``` | no | ```true``` or ```false``` |
| porcelain | For each executed script, print path relative to scan path **WARNING:** Can't be used alongside with verbose. If both are set, porcelain will prevail | ```false``` | no | ```true``` or ```false``` |

### Common driver configuration

| Key             | Description                                                                                                          | Default value     | Mandatory? | Values examples |
|-----------------|----------------------------------------------------------------------------------------------------------------------|-------------------|---|---|
| db.uri          | URI to your db server. **Database name is mandatory.**                                                               |                   | yes | ```mongodb://localhost/my-db```<br />```mongodb://localhost:8000/my-db```<br />```mongodb://username:password@localhost/my-db```<br />```mongodb+srv://server.example.com/my-db``` <br />```mongodb://my-db,my-db2:27018/my-db``` <br /> |
| db.trust.uri    | Bypass all checks that could be done on your URI because you are very sure of it and think our checks are just liars | ```false```       | no | ```true``` or ```false``` |
| db.print.output | If true, db output will be logged.                                                                                   | ```false```       | no | ```true``` or ```false``` |
| db.save.output  | If true, db output will be saved in script execution report.                                                         | ```false```       | no | ```true``` or ```false``` |
| db.executed.scripts.storage.name           | Name of the collection (mongo) or table (SQL) where the executed scripts will be stored                              | ``executedScripts`` | no | ``executedScripts`` |

### Specific mongodb driver configuration

Please, before see : [Common driver configuration](README.md#common-driver-configuration)
For ```db.uri```, please see the [mongo URI documentation](https://docs.mongodb.com/manual/reference/connection-string/) to learn about writing mongo URIs.

| Key | Description | Default value | Mandatory? | Values examples |
|---|---|---|---|---|
| db.mongo.tmp.path | Path where the driver will write temporary files. | ```/tmp/datamaintain.tmp``` | no |  |
| db.mongo.client.path | Path or alias to your mongo executable. | ```mongo``` | no |  |
| db.mongo.client.shell | Set if Datamaintain must use `mongo` or `mongosh` CLI. | ```mongo``` | no |  |


### Specific JDBC driver configuration
Please start by reading the [common driver configuration](README.md#common-driver-configuration)

For ```db.uri```, Please see the [Oracle JDBC URI documentation](https://docs.oracle.com/cd/E17952_01/connector-j-8.0-en/connector-j-reference-jdbc-url-format.html) to learn about JDBC URIs.

If you are using this driver with the CLI, make sure to put your driver jar in the folder ```drivers```.
