# Datamaintain
![GitHub](https://img.shields.io/github/license/4sh/datamaintain)

Your colleague added a column in the database without telling it to you and now nothing works ? Classic. 
To avoid this, use ***Datamaintain***! 

This Kotlin library helps maintaining consistency between data and code : it runs db scripts and tracks them. 

[comment]: <> (According to your needs you may :)

[comment]: <> (- [Use it as a dependency in your Java or Kotlin server]&#40;docs/as-dependency.md&#41; : really handy to collaborate,)

[comment]: <> (- [And/or use the Command Line Interface &#40;CLI&#41;]&#40;docs/with-cli.md&#41; : enables more control when deploying.)

## Benefits
During a project lifetime, you will often have to run scripts to update your database scheme or even add some data in it. 
The hard part comes when you have to share it with your colleagues or to use another environment. 
You will have to ensure that all your scripts were executed and in the right order, which is exactly what Datamaintain is for!

- **Transparent**: thanks to the script execution reports.
- **Light**: with few dependencies,
- **Customizable**: it is possible to run the scripts in different manners : with the server or independently, 
  ordered with tags, with your own program...
- **Idiot-proof**: Datamaintain has an identification based on the content that enables to manage homonyms scripts. 
  You will never get angry with your colleagues again. We also have unit tests for the Datamaintain contributors. 
- **Evolutive**: the architecture does not matter to the DBMS so that we can easily create new DBMS drivers and 
  finally have a common framework between all our projects.
- **Open source**: we are a team of developers that come mainly from the [4SH agency](https://www.4sh.fr/), 
  where Datamaintain is used in several big projects. 
  We works regularly on it according to the feedbacks of the users (our colleagues). 
  We would be really happy to work with other people. 
  If you are interested in this project don't hesitate to contact us. 

## Overview of the features
- Execution reports
- Tag feature (like a set of files)
- Whitelist and blacklist (via tags)
- Mark script as executed
- Override an executed script
- Dry run : allow to run datamaintain without performing action on db. It a way to ensure what datamaintain will do if you have doubts
- Possibility to activate some check rules (work in progress) : the only one now is the one that detect that some executed scripts was removed.

## Db drivers

For the moment Datamaintain supports only the DBMS MongoDB, but other configurations will soon be added (JDBC very soon)!

You want to help us ? Go [here](./README.md#contribute)

## How to ?

Datamaintain can be used in 2 ways. Both are compatible and often used together.

### As a dependency

*This mode is particularly useful on dev, when you get changes from the team.*

-  **Simple :** The scripts are launched automatically when the server starts.
-  **Embedded :** It is just a project dependency, so no installation needed.

### With the Command Line Interface

*This mode is particularly useful on deploy, when you want to control Datamaintain.*

- **Independent :** You can launch Datamaintain independently 
  to the server thanks to executable file. 
- **Powerful :** You also have access to specific actions like 'mark a script as executed'.

## FAQ

Please find some frequently asked question [here](./docs/faq.md)
  
## Contribute

There are many ways to help us. Feel free to contact us.

Here some things you can do to help us :
- Improve documentation
- Make code reviews and give us some tips/advice about conception/kotlin/gradle...
- Request improvements/Give us ideas : [go here](https://github.com/4sh/datamaintain/issues)
- Make PR to fix issues or to implement full new feature
- [Write a driver](docs/how-to-write-a-driver.md)
- ...
