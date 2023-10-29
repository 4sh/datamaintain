# Java Spring boot Postgres sample

This sample shows gives you a spring boot easy set up, highly inspired by the Flyway integration, you are provided with:
- `DatamaintainProperties`, a `ConfigurationProperties` that reads all configuration prefix by `datamaintain` into a configuration object. Here is the minimal configuration, you may see all the available options [here](../../docs/configuration.md).
- `DatamaintainAutoConfiguration`, a `Configuration` that builds a `Datamaintain` instance using all the configuration from `DatamaintainProperties`
- `DatamaintainMigrationInitializer`, an `InitializingBean` that performs the database migration with order `0` so that it's the first thing your application does upon start

The scripts given as an example insert two entries in the table ```starters```, a Charmander, with its [1G stats](https://www.smogon.com/dex/rb/pokemon/charmander/) and then updates its stats, splitting the special stat in special attack & special defence and finally updating the special attack to have the [2G stats](https://www.smogon.com/dex/gs/pokemon/charmander/) of Charmander.

Database was initialized with postgres docker image using trust auth

```bash 
docker run -e POSTGRES_HOST_AUTH_METHOD=trust -p 5432:5432 --name datamaintain_postgres postgres
```
