# Good practice

_Some good practice about databases updates, based on the maintainer team experience, will be detailed here._

## Introduction

When your project will scale up, updating your database will become a challenge. You will need to consider these objectives:
- Scale down the downtime of your server on a new version deployment, eventually reach no downtime
- Ensure your scripts take as few time as possible

If you have no downtime on a new version deployment, you will have two versions of your application launched at the same time. During this period of cohabitation you need to ensure both versions can interact with your database without trouble.

In this documentation solutions will be suggested to help you manage different common cases.

## Definition

In this documentation you will find some shortcuts :
- **V** : means the version of your application that is currently deployed
- **V+x** : means `x` versions after the current version. Thus V+2 means 2 versions after than current version V.

## Steps

Your deployment can be split into 3 steps during which you may want to execute scripts to update your database:
- **BEFORE**: represents the period before your deployment. The running version of your application is **V**.
- **DOWNTIME**: represents the downtime during the deployment. If you have no downtime, this step does not exist in your deployment process, thus no scripts can be executed during it.
- **AFTER**: represents the time after the deployment. The running version of your application is **V+1**. If you have no downtime, 
the version **V** could be still running for a few moments.
  
## Step DOWNTIME

**Try to never consider step DOWNTIME.** Even if you have downtime, you want to reduce its duration as much as you can.
The best way to do it is not to execute any scripts during your downtime.

## Database scheme safety

**Never break a data scheme between 2 versions of your application if you don't consider step DOWNTIME.**

Various operations may endanger your database scheme safety. Here is a detail of how to handle them and references to more documentation about each operation type.

### Add

| Operation                     | Safe change        |
|---                            |---                 |
| Add nullable property/column  | :heavy_check_mark: |
| Add mandatory property/column | :x:                |
| Add new value in an enum      | :heavy_check_mark: |
| Add collection/table/view     | :heavy_check_mark: |

See [add operations documentation](./add/add.md) for advice on how to handle these operations.

### Update

| Operation                    | Safe change |
|---                           |---          |
| Rename property/column       | :x:         |
| Rename value                 | :x:         |
| Update type                  | :x:         |
| Rename collection/table/view | :x:         |

As you see, operations of type `update` always cause a breaking change.
**Never do operations of type `update` or `rename`.**

See [update operations documentation](./update/update.md) for advice on how to handle these operations.

### Delete

| Operation                      | Safe change        |
|---                             |---                 |
| Delete used property/column    | :x:                |
| Delete unused property/column  | :heavy_check_mark: |
| Delete used enum value         | :x:                |
| Delete unused enum value       | :heavy_check_mark: |
| Delete used collection/table   | :x:                |
| Delete unused collection/table | :heavy_check_mark: |

This array shows operations of type `delete` can be problematic. The only case where this operation is safe 
is when what you want to remove is unused/useless (in your code and/or in your data).
**Use operations of type `delete` only when safe.**

See [delete operations documentation](./delete/delete.md) for advice on how to handle these operations.

### Version your scheme document (optional)

You can consider storing a scheme version on each document. It could help you manage scheme version 
by knowing it explicitly.


## Other good pratices

### Common

#### Performances
A BEFORE script taking a while to be executed can delay the delivery, so try to anticipate that.

#### Replayability
Try to have scripts that can be played many times. Indeed, even if you have taken all the precautions, the execution 
of your script can fail (for many reasons). So it is more comfortable to have a replayable script.

### SQL

#### Avoid SELECT * in your code 
If requests having `SELECT *` use prepare statement then when the schema will change your request will fail. 
See [presentation by Nelson Dionisi](#nelson_dionisi_pres) for more details.

### NoSQL

## References

- <a name="nelson_dionisi_pres">https://www.youtube.com/watch?v=OKT2GSUg3dk</a> : Excellent presentation by Nelson Dionisi from Mirakl that explains how 
Mirakl manage this problem.
  
- https://www.youtube.com/watch?v=zpM-lIRscXM : Thorben Janssen summarises strategies for each operation

- https://cloud.google.com/architecture/devops/devops-tech-database-change-management : article from Google related to this issue