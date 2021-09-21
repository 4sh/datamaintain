# How to ensure safe delete ?

Deletion is only possible when what we want to remove is unused. So the main strategy will be to first ensure 
the non-usage of what we want to remove, and then simply remove it.

## Delete a property/column

## Unused 

In V+1 :
  - update the code to remove the property/column.
  - create an AFTER script that remove the property/column.

## Used

- In V+1 :
  - update the code to mark this property/column as deprecated.
  - update the code to stop the reads on this property/column.
    - no code must use this property/column for treatment.
    - keep writes because version V use this property/column for treatment.
- In V+2 (note it is the same steps as unused scenario) :
  - update the code to remove the property/column.
    - you can do it because you ensured the property/column is not used in reading in V+1.
    - So writes became unnecessary and the property/column can be safely removed.
  - create an AFTER script that remove the property/column.


## Delete a value

## Unused

In V+1 :
  - update the code to remove the value.
  - if your db has schema create an AFTER script that remove the value.

## Used

- In V+1 :
  - update the code to mark this value as deprecated.
  - update the code to stop the usage on this value in your code.
    - no code must use this value.
    - keep writes because version V use this property/column for treatment.
- In V+2 (note it is the same steps as unused scenario) :
  - update the code to remove the value.
  - if your db has schema create an AFTER script that remove the value.


## Delete a collection/table/view

## Unused

In V+1 :
  - remove all code relative to the collection/table/view.
  - create an AFTER script that remove the collection/table/view.

## Used

- In V+1 :
  - mark the relative code as deprecated.
  - update the code to stop the reads from this collection/table/view.
    - keep writes because version V use this property/column for treatment.
- In V+2 (note it is the same steps as unused scenario) :
  - remove all code relative to the collection/table/view.
  - create an AFTER script that remove the collection/table/view.