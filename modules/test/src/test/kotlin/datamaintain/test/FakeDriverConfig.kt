package datamaintain.test

import datamaintain.core.db.driver.ConnectionStringBuilder
import datamaintain.core.db.driver.DatamaintainDriver
import datamaintain.core.db.driver.DatamaintainDriverConfig
import datamaintain.core.exception.DatamaintainBaseException

/**
 * Duplicated, needs refactoring
 * TODO: https://github.com/4sh/datamaintain/issues/213
 */
class FakeDriverConfig : 
        DatamaintainDriverConfig( "fake","",true, false, false,
                ConnectionStringBuilder(".*") { DatamaintainBaseException("") }) {
    override fun log() {
    }

    override fun toDriver(connectionString: String): DatamaintainDriver = FakeDatamaintainDriver()
}
