# Workflow with downtime

_Please, consider this sample as a way to guide you and give you ideas to manage your workflow. 
Of course, feel free to adjust according your needs._

## What is the problem ?

Assuming the size of your project and the techno, you have different needs to play scripts.
In this sample, we will consider the case :
- I need to stop my server to deploy a new version. So I have a downtime during X seconds.
- I have scripts that can take a long to be played.

Because you have a downtime, you want it to be the smallest as possible.

## What can we do ?

So consider these 3 steps :
- BEFORE : represents all the time before your deployment.
- DOWNTIME : represents all the downtime during the deployment.
- AFTER : represents all the time after the deployment.

Now imagine you will have an execution of Datamaintain for each step. So to minimise your downtime you have to 
think about what can be played during the steps BEFORE and AFTER. You also may consider splitting your script in order 
to play some parts on different steps.

Basically, some tips :
- BEFORE : all actions that don't break the current version db requirements
  - adding a new property on document
  - adding index
- DOWNTIME : all actions that break the requirements of the previous version and are needed for the new version.
  - updating the name of a property
  - update the value of a property (i.e. an enum renamed)
- AFTER : all actions that don't break the new version db requirements
  - removing unused property on document
  - removing unused collection
  - removing index

Please consider reading our section about best practices.

## Please, can you show me how to ?

_Yes, of course ! You can find a small sample showing you how you can implement this scenario._

### Create a db

_For convenience, we will demonstrate the workflow with mongo but you can use any db supported by datamaintain._

Create a db called `case-downtime` in your mongo.

### Init your db

Run Mongo script `init.js` to init a collection `users` with some documents.

### Export a variable environment

You need to export the absolute path of the datamaintain project, in order to be able to launch the sh scripts of the sample.
```
export PATH_TO_DATAMAINTAIN_PROJECT=/home/driccio/dev/wkspace/datamaintain/
```

### Launch BEFORE scripts

Launch the bash script `run-before.sh` that will execute Datamaintain with scripts suffixed with `_BEFORE`.

### Start deployment

In real world, you will be stopping your server in order to deploy the v2.

### Launch DOWNTIME scripts

Now the server is down, you can launch the bash script `run-downtime.sh` that will execute Datamaintain with scripts suffixed with `_DOWNTIME`.

### Deployment done

In real world, when your deployment is done, you can restart your server.

### Launch AFTER scripts

And then, you can launch the bash script `run-after.sh` that will execute Datamaintain with scripts suffixed with `_AFTER`.

