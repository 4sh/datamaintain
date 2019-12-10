package datamaintain.core.db.driver

interface DatamaintainDriverConfig {

    fun toDriver(): DatamaintainDriver

    fun log()
}
