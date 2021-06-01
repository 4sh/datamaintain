# Datamaintain
![GitHub](https://img.shields.io/github/license/4sh/datamaintain)

Your colleague added a column in the database without letting you know and now everything is broken? Classic. 
To avoid this, use **Datamaintain**! 

This Kotlin library helps to maintain consistency between data and code: it runs scripts on your database and tracks them.

## Benefits
As your project grows, you will often have to run scripts to update your database scheme or even add some data in it. 
The hard part comes when you have to share it with your colleagues or deploy those changes on another environment. 
You will have to ensure that all your scripts were executed and in the right order, *which is exactly what Datamaintain is for*!

- **Transparent**: Datamaintain lets you know what it is doing and stores scripts execution reports in your database
- **Light**: with few dependencies
- **Customizable**: you can embed Datamaintain in your server app or use the CLI. Many configuration options are available to meet your needs.
- **Name independent**: you may rename or move your script at will, Datamaintain recognizes them using their MD5 
- **Evolutive**: the core of Datamaintain is independent from any database management system thus it can be plugged with any database management system, if you write a driver for it
- **Lasting**: this project is maintained by a team of developers from [4SH](https://www.4sh.fr/), a French software company, as part of our R&D.
- **Reliable**: high code coverage by unit tests. Each version spends at least two weeks in beta test on one of our projects before release

## Overview of the features
- Execution reports stored in your database
- Scripts sorted depending on an identifier of your choice, extracted from their names
- Whitelist or blacklist scripts
- Run scripts again
- Mark script(s) as executed
- Override an executed script 
- Dry run: run Datamaintain without performing action on your db as a way to ensure what Datamaintain will do
- Check if executed scripts disappeared from your files

## Available drivers

For the moment Datamaintain supports only the DBMS MongoDB, but other configurations will soon be added (JDBC very soon)!

You want to help us? Go [here](./README.md#contribute)

## How to use Datamaintain?

Datamaintain can be used in 2 ways. Both are compatible and often used together.

### As a dependency

*This mode is particularly useful on dev, when you get changes from the team.*

-  **Simple:** The scripts are launched automatically when the server starts.
-  **Embedded:** It is just a project dependency, so no installation needed.

### With the Command Line Interface

*This mode is particularly useful on deploy, when you want to control Datamaintain.*

- **Independent:** You can launch Datamaintain independently 
  to the server thanks to executable file. 
- **Powerful:** You also have access to specific actions like 'mark a script as executed'.

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
