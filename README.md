# Datamaintain
[![CircleCI](https://circleci.com/gh/4sh/datamaintain.svg?style=shield)](https://circleci.com/gh/4sh/datamaintain) ![GitHub](https://img.shields.io/github/license/4sh/datamaintain)

Your colleague added a column in the database without telling it to you? Now nothing works in your environment? Classic. To avoid this, use Datamaintain! 

Datamaintain is a Kotlin library that helps maintaining consistency between data and code : it runs the new scripts added and tracks the scripts runned. 

According to your needs you may :
- [Use it as a dependancy in your Java or Kotlin server](docs/asdependancy.md) : really handy to collaborate,
- [And/or use the Command Line Interface (CLI)](docs/withcli.md) : enables more control when deploying.

For the moment Datamaintains supports only the DBMS MongoDB, but other configurations will soon be added (JDBC very soon)! We have created a documentation for creating new drivers : ["how to write a driver for your DBMS"](docs/withcli.md). If you want to contribute, it is quite easy (no need to deeply know Datamaintain) and it would help a lot 🙏.

## Benefits
During a project lifetime, you will often have to run scripts to update your database scheme or even add some data in it. The hard part comes when you have to ensure that all your scripts were executed and in the right order, which is exactly what Datamaintain is for! 

- **Transparent**, thanks to the script execution reports.
- **Light**, with few dependencies,
- **Customizable**, it is possible to run the scripts in different manners : with the server or independantly, ordered with tags, with your own program...
- **Idiot-proof**, Datamaintain has an identification based on the content that enables to manage homonyms scripts. You will never get angry with your colleagues again. We also have unit tests for the Datamaintain contributors. 
- **Evolutive and open-source**, the architecture does not matter to the DBMS so that we can easily create new DBMS drivers and finally have a common framework between all our projects.  We are a team of developers that come mainly from the [4SH agency](https://www.4sh.fr/), where Datamaintain is  used in several big projects. We works regularly on it according to the feedbacks of the users (our colleagues). We would be really happy to work with other people. If you are interested in this project don't hesitate to contact us.

## What is the best configuration for me ?

### As a dependancy
-  **👌 The simplest :**  The scripts are launched automatically when the server starts. This is simple and really handy to collaborate.
-  **👾 Enable personalisation :**  You can program it with Java. 

### With the Command Line Interface
- **💪 Enables more control when deploying :**  You can master the order of your scripts and launch them independantly to the server thanks to executable file. You can also use Datamaintain in a project with already executed scripts. 

### Can I use both ? 
Yes ! And that is often the case. You can develop your project with Datamaintain as a dependancy and then deploy in production with the Command Line Interface (CLI). 

## Quick links
### Getting started
**1) Discover :** [Read the benefits of Datamaintain](README.md#Benefits), [Chose your configuration](README.md#What-is-the-best-configuration-for-me-?), [read script reports], 

**1) Install :** depending on your choice, either [as a dependacy](docs/asdependancy.md) and/or [with the Command Line Interface](docs/withcli.md). If needed : [installation in a a project with alrady executed scripts](docs/already-executed-scripts.md)

**2) Configure :** [How to configure Datamaintain]

### Overview of the features
- [The execution reports](docs/executedscripts.md)
- The tag feature,
- How to enhance performances with a synthesis of the scripts ? 
### Contribute
- How does Datamaintain (what Datamaintains does for you)
- How to write a driver. 
