# How to manage add ?

## Add a property
### Nullable property

In V+1 :
  - if your db has schema, create a BEFORE script that add a property with null value.
  - update the code to write and read the new property.

### Mandatory property

This scenario is more complex :
- In V+1 :
  - if your db has schema, create a BEFORE script that add a property with null value.
  - update the code to write and read the new property
    - you need to manage the nullity of the value in your code.
  - create an AFTER script that fill the property with a default value.
  - if your db has schema, create an AFTER script to configure non-nullity on it.
- In V+2 :
  - you can remove code that manage nullity.
    - you have ensured all lines contains the property because you have a default value and a non-null constraint.


## Add a new value

In V+1 :
  - if your db has schema, create a BEFORE script that add the new value as possible value.
  - update the code to manage this new value.


## Add a new collection/table/view

In V+1 :
- if your db has schema, create a BEFORE script that add the new collection/table/view.
- update the code to manage this new collection/table/view.