# Datamaintain
[![CircleCI](https://circleci.com/gh/4sh/datamaintain.svg?style=shield)](https://circleci.com/gh/4sh/datamaintain) ![GitHub](https://img.shields.io/github/license/4sh/datamaintain)

Your colleague added a column in the database without telling it to you and now nothing works ? Classic. To avoid this, use Datamaintain! This Kotlin library  helps maintaining consistency between data and code : it runs the new scripts added and tracks the scripts runned. According to your needs you may :
- [Use it as a dependency in your Java or Kotlin server](docs/as-dependency.md) : really handy to collaborate,
- [And/or use the Command Line Interface (CLI)](docs/with-cli.md) : enables more control when deploying.

It is also a complete toolbox, [discover all the features !](README.md#Overview-of-the-features)

For the moment Datamaintains supports only the DBMS MongoDB, but other configurations will soon be added (JDBC very soon)! If you want to contribute, you can  write a driver for your DBMS, let's have a look : ["how to write a driver for your DBMS"](docs/how-to-write-a-driver.md). There is no need to deeply know Datamaintain and it would help a lot 🙏.

## Benefits
During a project lifetime, you will often have to run scripts to update your database scheme or even add some data in it. The hard part comes when you have to share it with your colleagues or to use another environment. You will have to ensure that all your scripts were executed and in the right order, which is exactly what Datamaintain is for!

- **Transparent**, thanks to the script execution reports.
- **Light**, with few dependencies,
- **Customizable**, it is possible to run the scripts in different manners : with the server or independently, ordered with tags, with your own program...
- **Idiot-proof**, Datamaintain has an identification based on the content that enables to manage homonyms scripts. You will never get angry with your colleagues again. We also have unit tests for the Datamaintain contributors. 
- **Evolutive and open-source**, the architecture does not matter to the DBMS so that we can easily create new DBMS drivers and finally have a common framework between all our projects.  We are a team of developers that come mainly from the [4SH agency](https://www.4sh.fr/), where Datamaintain is  used in several big projects. We works regularly on it according to the feedbacks of the users (our colleagues). We would be really happy to work with other people. If you are interested in this project don't hesitate to contact us.

## What is the best use for me ?

### As a dependency
-  **👌 The simplest :**  The scripts are launched automatically when the server starts. This is simple and really handy to collaborate. You can also use it in a project with already executed scripts. 
-  **👾 Enable personalisation :**  You can program it with Java. 

### With the Command Line Interface
- **💪 Enables more control when deploying :**  You can master the order of your scripts and launch them independently to the server thanks to executable file. You can also use Datamaintain in a project with already executed scripts. 

### Can I use both ? 
Yes ! And that is often the case. You can develop your project with Datamaintain as a dependency and then deploy in production with the Command Line Interface (CLI). 

## Quick links
### Getting started
**1. Install :** you can [install it as a dependency](docs/as-dependency.md) and/or [use the Command Line Interface](docs/with-cli.md). If needed, you can [install in a a project with already executed scripts](docs/already-executed-scripts.md).

**2. Configure :** [How to configure Datamaintain](docs/configuration.md),

### Overview of the features
- [The execution reports](docs/executed-scripts.md)
- The tag feature,
- Whitelist and blacklist (via tags)
- Marks a script as executed
- Override an executed script 
- Dry run : allow to run datamaintain without performing action on db. It a way to ensure what datamaintain will do if you have doubts
- Possibility to activate some check rules (work in progress) : the only one now is the one that detect that some executed scripts was removed.

### Contribute
- How does Datamaintains work (work in progress),
- [How to write a driver](docs/how-to-write-a-driver.md). 
