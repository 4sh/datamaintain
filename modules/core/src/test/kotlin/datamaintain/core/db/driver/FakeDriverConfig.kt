package datamaintain.core.db.driver

import datamaintain.core.exception.DatamaintainBaseException

class FakeDriverConfig : 
        DatamaintainDriverConfig(
            dbType = "fake",
            uri = "",
            trustUri = true,
            printOutput = false,
            saveOutput = false,
            connectionStringBuilder = ConnectionStringBuilder(".*") { DatamaintainBaseException("") },
            executedScriptsStorageName = "fakeExecutedScripts"
        ) {
    override fun log() {
    }

    override fun toDriver(connectionString: String): DatamaintainDriver = FakeDatamaintainDriver()
}
