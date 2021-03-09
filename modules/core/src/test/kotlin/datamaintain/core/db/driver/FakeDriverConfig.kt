package datamaintain.core.db.driver

class FakeDriverConfig : DatamaintainDriverConfig(true, "", ConnectionStringBuilder(".*", "")) {
    override fun log() {
    }

    override fun toDriver(connectionString: String): DatamaintainDriver = FakeDatamaintainDriver()
}