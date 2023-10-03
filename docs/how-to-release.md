# Release Datamaintain

3 modules are released:
* artifacts on maven central
* CLI
* Docker images

## Maven central
- Tag your version
- Wait for the [publishing job](https://github.com/4sh/datamaintain/actions/workflows/publish_packages_to_maven_central.yaml) end
- Go to [the maven central repository manager](https://s01.oss.sonatype.org/#welcome)
- Log in using the [Datamaintain credentials](https://vaultwarden.quatre.app/#/vault?organizationId=1d80e58f-58bc-46d5-bd71-e0bd4f6a6a90&cipherId=08a0ee6d-1d60-48f2-a648-110112fd864f)
- You should see your release attempt in the staging repositories, close your staging repository. Once all the verifications were successful, you should receive a mail to tell you that the closing is done.
- Release your staging repository

## CLI
TODO

## Docker images
- Tag your version
- Wait for the [publishing job to end](https://github.com/4sh/datamaintain/actions/workflows/publish_docker_packages.yaml)
- You're done!
