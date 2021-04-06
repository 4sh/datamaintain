package datamaintain.db.driver.mongo

import datamaintain.core.db.driver.ConnectionStringBuilder
import datamaintain.db.driver.mongo.exception.DatamaintainMongoUriException

class MongoConnectionStringBuilder: ConnectionStringBuilder(
        "^mongodb(?:\\+srv)?://(?:([-._\\w]+):(?:.+)@)?(?:[-.\\w]+)(?::(?:[0-9]+))?(?:/([\\w-_]+)(?:\\?(?:[\\w_.]+=[\\w_]+)(?:&[\\w]+=[\\w-\\d,:]+)*)?)",
        {uri -> DatamaintainMongoUriException(uri) })