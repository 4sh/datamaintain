# FAQ

## I already have executed scripts on my project. Can I use Datamaintain ?

Yes of course you can :-). It is a very common use case we have. 

- Download the CLI [here](https://github.com/4sh/datamaintain/releases).
- Mark the script as executed :
```bash

./datamaintain-cli --db-type $DB_TYPE --mongo-uri $MONGO_URI update-db --path $PATH --identifier-regex $REGEX --action MARK_AS_EXECUTED
```

An explanation about each CLI configuration key is provided [here](./cli-configuration.md).

