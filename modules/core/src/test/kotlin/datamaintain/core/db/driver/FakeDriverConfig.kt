package datamaintain.core.db.driver

import datamaintain.core.exception.DatamaintainBaseException

class FakeDriverConfig : 
        DatamaintainDriverConfig( "fake","",true, false, false,
                ConnectionStringBuilder(".*") { DatamaintainBaseException("") }) {
    override fun log() {
    }

    override fun toDriver(connectionString: String): DatamaintainDriver = FakeDatamaintainDriver()
}
