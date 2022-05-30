package datamaintain.core.exception

class DatamaintainBuilderMandatoryException(builder: String, field: String) :
    DatamaintainBaseException("Cannot build $builder : $field is mandatory")
