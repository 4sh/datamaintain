package datamaintain.core.db.driver

class FakeDriverConfig : DatamaintainDriverConfig {
    override fun log() {
    }

    override fun toDriver(): DatamaintainDriver = FakeDatamaintainDriver()
}