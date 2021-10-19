# How to ensure safe update ?

To manage update you need to split this operation in two. An update is a composition of an add and a deletion.

## Rename property/column

Say you want to rename the property/column `name` `lastname`
- In V+1 :
  - create a BEFORE script that adds a property/column `lastname` with null value/constraint.
  - update the code to **write** to also update `lastname` everytime `name` is updated.
    :warning: do not read the new property/column `lastname`. Keep reading `name`.
- In V+2 :
  - create a BEFORE script that copies `name` value in `lastname` for entries that have a null `lastName`.
  - update the code to read and write only `lastname`. `name` now becomes unnecessary
  - create an AFTER script that removes the property/column `name`.
  - if your db has schema and `lastname` is mandatory, create an AFTER script to add non-null constraint.
  
**Note :** if your db manages triggers, you can use them to ensure both column are synchronized.

## Rename enum value

Say you want to rename enum value `ENDED` `DONE` :
- In V+1 :
  - create a BEFORE script that adds `DONE` as a possible value in your schema.
  - add the value `DONE` as a possible value in your code (you maybe have an enum to manage these possible values). :warning: Do not use the `DONE` value for now.
- In V+2 :
  - Update your code to write `DONE` instead of `ENDED` in db. :warning: You need to continue to consider `ENDED` as a possible value
- In V+3 :
  - create a BEFORE script to replace `ENDED` with `DONE` everywhere
  - update the code to remove `ENDED` value.
  - if your db has schema, create an AFTER script to remove `ENDED` as possible value.
  

## Update type or format

To change a property type or format:
- add a new temporary property with the value corresponding to the new type or format
- remove the original property
- rename the temporary property with the good name 

Say you want to change of property `age` to store it as a number instead of a string:
- In V+1 :
  - create a BEFORE script that adds a new field, for example `ageTmp`, of type `int`.
  - update the code to write both `age`, with the string value, and `ageTmp`, with the number value. :warning: Do not read the new property/column `ageTmp`. Keep reading `age`.
- In V+2 :
  - create a BEFORE script that copies `age` value as a number in `ageTmp` for entries with null `ageTmp`.
  - update the code to read and write only `ageTmp`.
    `age` now becomes unnecessary.
  - create an AFTER script that removes the property/column `age`.
- In V+3 :
  - create a BEFORE script that adds a new nullable field `age` of type `int`.
  - update the code to write both `age` and `ageTmp` with the number value. :warning: Do not read the new property/column `age`. Keep reading `ageTmp`.
- In V+4 :
  - create a BEFORE script that copies `ageTmp` value in `age` for entries having `age` null.
  - update the code to read and write only `age`.
    `ageTmp` now becomes unnecessary.
  - create an AFTER script that removes the property/column `ageTmp`.
  - if your db has schema and `age` is mandatory, create an AFTER script to add non-null constraint.

This scenario can be simplified if you have a schema-less database :
- In V+1 :
  - create a BEFORE script that adds a new field, for example `ageTmp`.
  - update the code to write both `age`, with the string value, and `ageTmp`, with the int value. :warning: Do not read the new property/column `ageTmp`. Keep reading `age`.
- In V+2 :
  - create a BEFORE script that copies `age` value as a number in `ageTmp` for entries having null `ageTmp`.
  - update the code to only read `ageTmp`. Keep writing both `ageTmp` and `age` to ensure backward compatibility with **V+1**
  - create an AFTER script that copies `ageTmp` onto `age`.
- In V+3 :
  - Update the code to read and write only `age`.
    `ageTmp` now becomes unnecessary.
  - create an AFTER script that removes the property/column `ageTmp`.
  - if your db has schema and `age` is mandatory, create an AFTER script to add non-null constraint.
  
## Rename collection/table

Say you want to rename collection/table/view `users` into `persons` :
- In V+1 :
  - create a BEFORE script that adds a collection/table `persons`.
  - update the code to write both on `users` and `persons`. 
    keep reading from `users` only.
- In V+2 :
  - create a BEFORE script that copies in `persons` all missing entries from `users`
  - update the code to read and write to `persons` and remove all code relative to `users`.
  - create an AFTER script that removes the collection/table `users`.

**Note :** if your db manages triggers, you can use them to ensure both column are synchronized.
