package datamaintain.core.exception

class DatamaintainCheckRuleNotFoundException(ruleName: String) :
    DatamaintainBaseException("Aborting - Check rule `${ruleName}` not found")
