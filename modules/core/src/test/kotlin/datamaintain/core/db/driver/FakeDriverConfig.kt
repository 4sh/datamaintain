package datamaintain.core.db.driver

class FakeDriverConfig : DatamaintainDriverConfig {
    override fun toDriver(): DatamaintainDriver = FakeDatamaintainDriver()
}