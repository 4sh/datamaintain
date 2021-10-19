# How to ensure safe delete ?

Deletion is only possible when it is to remove an unused property. Thus, the main strategy to handle this type of operations will be to first ensure 
the non-usage of what we want to remove, and then simply remove it.

## Delete a property or column

### Unused property or column

In V+1 :
  - update the code to no longer read nor update the property/column.
  - create an AFTER script that removes the property/column.

### Used property or column

- In V+1 :
  - update the code to mark this property/column as deprecated.
  - update the code to stop the reads on this property/column.
    - no code must use this property/column value.
    - keep writing this property/column for backward compatibility with version **V**
- In V+2, the property is no longer used because you made sure of it in version **V+1**. You may then follow the [deletion process for an unused property/column](delete.md/#unused-property-or-column)

## Delete an enum value

### Unused enum value

In V+1 :
  - update the code to remove the value.
  - if your db has schema create an AFTER script that removes the value.

### Used enum value

- In V+1 :
  - update the code to mark this value as deprecated.
  - update the code to stop the usage of this value in your code.
- In V+2, the enum value is no longer used because you made sure of it in version **V+1**. You may then follow the [deletion process for an unused enum value](delete.md/#unused-enum-value)


## Delete a collection/table/view

## Unused collection or table or view

In V+1 :
  - remove all code relative to the collection/table/view.
  - create an AFTER script that removes the collection/table/view.

## Used collection or table or view

- In V+1 :
  - mark the relative code to the collection/table/view as deprecated.
  - update the code to stop the reads from this collection/table/view.
    - keep writing in this collection/table/view for backward compatibility with version **V**
- In V+2, the collection/table/view is no longer used because you made sure of it in version **V+1**. You may then follow the [deletion process for an unused collection/table/view](delete.md/#unused-collection-or-table-or-view)