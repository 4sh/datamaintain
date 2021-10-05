# How to ensure safe update ?

To manage update you need to split this operation in two. An update is a composition of an adding and a deletion.

## Rename property/column

Here the scenario if you want to rename property/column `name` into `lastname` :
- In V+1 :
  - create a BEFORE script that add a property/column `lastname` with null value/constraint.
  - update the code to write both on `name` and `lastname`.
    - do not read the new property/column `lastname`. Keep reading for `name`.
- In V+2 :
  - create a BEFORE script that copy `name` value on `lastname` for entries having `lastName` null.
  - update the code to read and write only `lastname`.
    - so `name` becomes unnecessary.
  - create an AFTER script that remove the property/column `name`.
  - if your db has schema and `lastname` is mandatory, create an AFTER script to add non-null constraint.
  
**Note :** if your db manages triggers, you can use them to ensure both column are synchronized.

## Rename value

Here the scenario if you want to rename possible value from `ENDED` into `DONE` :
- In V+1 :
  - create a BEFORE script that add `DONE` as value in your schema.
  - add the value `DONE` as a possible value in your code (you maybe have an enum to manage these possible values).
    - do not use the `DONE` value for now.
- In V+2 :
  - Update your code to write only `DONE` in db
    - you need to continue to manage `ENDED` as possible value on read
- In V+3 :
  - create a BEFORE script to update all entries using `ENDED` to replace it with `DONE`.
  - update the code to remove `ENDED` value.
  - if your db has schema, create an AFTER script to remove `ENDED` as possible value.
  

## Update type

To proceed to an update type we can perform :
- add a new temporary property from the one we want to rename
- remove the original property
- rename the temporary property with the good name 

Here the scenario if you want to change type of property `age` from A to B :
- In V+1 :
  - create a BEFORE script that add a new field `ageTmp` (choose the name you want) of type B.
  - update the code to write both on `age` and `ageTmp`.
    - do not read the new property/column `ageTmp`. Keep reading for `age`.
- In V+2 :
  - create a BEFORE script that copy `age` value on `ageTmp` for entries having `ageTmp` null.
  - update the code to read and write only `ageTmp`.
    - so `age` becomes unnecessary.
  - create an AFTER script that remove the property/column `age`.
- In V+3 :
  - create a BEFORE script that add a new field `age` of type B.
  - update the code to write both on `age` and `ageTmp`.
    - do not read the new property/column `age`. Keep reading for `ageTmp`.
- In V+4 :
  - create a BEFORE script that copy `ageTmp` value on `age` for entries having `age` null.
  - update the code to read and write only `age`.
    - so `ageTmp` becomes unnecessary.
  - create an AFTER script that remove the property/column `ageTmp`.
  - if your db has schema and `age` is mandatory, create an AFTER script to add non-null constraint.

This scenario can be simplified if you have a schema-less database :
- In V+1 :
  - create a BEFORE script that add a new field `ageTmp` (choose the name you want) of type B.
  - update the code to write both on `age` and `ageTmp`.
    - do not read the new property/column `ageTmp`. Keep reading for `age`.
- In V+2 :
  - create a BEFORE script that copy `age` value on `ageTmp` for entries having `ageTmp` null.
  - update the code to read only `ageTmp`. Keep writing both `ageTmp` and `age`.
  - create an AFTER script that copy `ageTmp` onto `age`.
- In V+3 :
  - Update the code to read and write only `age`.
    - so `ageTmp` becomes unnecessary.
  - create an AFTER script that remove the property/column `ageTmp`.
  - if your db has schema and `age` is mandatory, create an AFTER script to add non-null constraint.
  
## Rename collection/table

Here the scenario if you want to rename collection/table/view `users` into `persons` :
- In V+1 :
  - create a BEFORE script that add a collection/table `persons`.
  - update the code to write both on `users` and `persons`.
    - keep reading from `users` only.
- In V+2 :
  - create a BEFORE script that copy all none existing entries from `users` into `persons`.
  - update the code to read and write to `persons` and remove all code relative to `users`.
  - create an AFTER script that remove the collection/table `users`.

**Note :** if your db manages triggers, you can use them to ensure both column are synchronized.
