# Good practices

_We will try here to give you good practices about databases updates._

## Intro

If it is quite simple to update a database on a small project, it can be painful on a bigger project.
Some criteria need to be taken into account :
- Limit the downtime of your server. Sometimes this downtime is 0.
- Pay attention about performances

If you don't have downtime on new version deployment, it means you have two versions of your application launched in 
parallel. So during this time of cohabitation you need to ensure both versions can work without trouble.

In this documentation we will try to help you to manage different common cases.

## Definition

On this documentation you will find some shortcuts :
- **V** : means the current deployed version of your application 
- **V+x** : means `x` versions after the current version. So V+2 means 2 versions more than current version V.

## Steps

You can consider 3 steps to update a database :
- BEFORE : represents the time before your deployment.
- DOWNTIME : represents the downtime during the deployment. If your downtime is 0 (so you have two versions of your 
  application launched in parallel), then you cannot consider this step to execute scripts.
- AFTER : represents the time after the deployment. You consider in this step that only the last version of your 
  application is running.

## Step DOWNTIME

**Try to never consider step DOWNTIME.** Even if you have downtime, you will want to limit its duration at the maximum. 
So the best way is to consider you cannot execute script at this step, but it is more complex to manage.

## Scheme safety

Never break a data scheme between 2 versions of your application if you don't consider step DOWNTIME.

Here, an array listing common update operations that impact the scheme :

| Type | Operation                            | Safe change |
|---   |---                                   |---          |
| **Add**    | | |
|            | Add property/column            | :heavy_check_mark: |
|            | Add new value                  | :heavy_check_mark: |
|            | Add collection/table/view      | :heavy_check_mark: |
| **Update** | | |
|            | Rename property/column         | :x: |
|            | Rename value                   | :x: |
|            | Update type                    | :x: |
|            | Rename collection/table/view   | :x: |
| **Delete** | | |
|            | Delete used property/column    | :x: |
|            | Delete unused property/column  | :heavy_check_mark: |
|            | Delete used value              | :x: |
|            | Delete unused value            | :heavy_check_mark: |
|            | Delete used collection/table   | :x: |
|            | Delete unused collection/table | :heavy_check_mark: |

This simple array shows us that operations of type `update` always cause a breaking change.
**Never do operations of type `update` or `rename`.** We will see further how to manage them.

This array also shows us operations of type `delete` can be problematic. The only case where this operation is safe 
is when what you want to remove is unused/useless (in your code and/or in your data).
**Use operations of type `delete` only when safe.**

### How to manage add ?

Please see dedicated page [here](./add/add.md)

### How to ensure safe delete ?

Please see dedicated page [here](./delete/delete.md)

### How to ensure safe update ?

Please see dedicated page [here](./update/update.md)

### Version your scheme document (optional)

You can consider storing a scheme version on each document. It is only to help you to manage scheme version 
by knowing it explicitly.


## Other good pratices

### Common

### SQL

**Avoid SELECT * because of prepared statement** : 

### NoSQL

## References

- https://www.youtube.com/watch?v=OKT2GSUg3dk : Excellent presentation by Nelson Dionisi from Mirakl that explains us how 
Mirakl manage this problem.
  
- https://www.youtube.com/watch?v=zpM-lIRscXM : Thorben Janssen summarise strategies for each operations