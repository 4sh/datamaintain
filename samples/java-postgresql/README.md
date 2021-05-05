# Java Postgres sample

This sample shows how to use the Datamaintain configuration and run Datamaintain when you are using Java and the Postgres driver.

The scripts given as an example insert two entries in the table ```starters```, a Charmander, with its [1G stats](https://www.smogon.com/dex/rb/pokemon/charmander/) and then updates its stats, splitting the special stat in special attack & special defence and finally updating the special attack to have the [2G stats](https://www.smogon.com/dex/gs/pokemon/charmander/) of Charmander.

Database was initialized with postgres docker image using trust auth

```docker run -e POSTGRES_HOST_AUTH_METHOD=trust -p 5432:5432 --name datamaintain_postgres postgres```
