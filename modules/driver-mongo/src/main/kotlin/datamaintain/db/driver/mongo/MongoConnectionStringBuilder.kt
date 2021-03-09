package datamaintain.db.driver.mongo

import datamaintain.core.db.driver.ConnectionStringBuilder

class MongoConnectionStringBuilder: ConnectionStringBuilder(
        "^mongodb(?:\\+srv)?://(?:([-._\\w]+):(.+)@)?([-.\\w]+)(?::([0-9]+))?(?:/([\\w-_]+)(?:\\?([\\w_.]+=[\\w_]+))?)",
        "MongoUri is not correct. The expected format is: mongodb://[username:password@]host[:port]/databasename[?options]")