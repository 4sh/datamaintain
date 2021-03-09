package datamaintain.core.exception

class DatamaintainFileIdentifierPatternException(
    name: String,
    identifierRegex: Regex
) : DatamaintainBaseException("The file $name doesn't match the pattern $identifierRegex and so can't extract its identifier")
