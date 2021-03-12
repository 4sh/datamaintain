package datamaintain.db.driver.jdbc

import datamaintain.core.db.driver.ConnectionStringBuilder

class JdbcConnectionStringBuilder: ConnectionStringBuilder(
        "^jdbc:(\\w*:){1,2}(//)?(?:([-._\\w]+):(.+)@)?([-.\\w]+)(?::([0-9]+))?(?:/([\\w-_]+)(?:\\?([\\w_.]+=[\\w_]+))?)",
        { uri -> DatamaintainJdbcUriException(uri) })