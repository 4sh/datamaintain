# Command Line Interface (CLI)

You will find the CLI for each release in its assets in the [releases](https://github.com/4sh/datamaintain/releases). 
To launch Datamaintain using the CLI, you just have to execute the bash script you will find in the archive. 
To give values to [the settings](./cli-configuration.md), you just have to add ```--setting $SETTING_VALUE``` after ```./datamaintain-cli```.

![](img/release-page-cli.png)

## Use docker
You can use the CLI via a docker image, the images are hosted on GitHub so you will need [docker to have access to GitHub](https://docs.github.com/en/packages/guides/configuring-docker-for-use-with-github-packages).
You just need to mount the script path to the container :
```
docker run --rm --volume $script_path:/scripts docker.pkg.github.com/4sh/datamaintain/datamaintain:1.2-mongo-4.4 --db-type mongo --db-uri mongodb://localhost:27017/sample update-db --path /scripts --identifier-regex "(.*)"
```

In this example :
* `$script_path` is a path to the script folder
* `--rm` will remove the container once Datamaintain is finish
* `--volume` mounts the script folder on your machine to the Datamaintain container with the path `/scripts`
* After the image name `datamaintain` you can pass arguments to the cli normally.
* On Mac OS you may need to replace `localhost` in the mongo URI 
  by `host.docker.internal`. See [docker documentation](https://docs.docker.com/docker-for-mac/networking/).

Datamaintain image use a mongo shell.

Image tag has form `<datamaintain version>-<db type>-<db version>` for example 
`docker.pkg.github.com/4sh/datamaintain/datamaintain:1.2-mongo-4.4` is a datamaintain 1.2 with a mongo shell 4.4.
For now, datamaintain only support mongo database.
You can see all images [here](https://github.com/orgs/4sh/packages?repo_name=datamaintain)

