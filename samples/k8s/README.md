# Datamaintain with docker and K8S
## Goal

Show an example of how to use Datamaintain with a Kubernetes Cluster.
Init a sample for start an init container that use Datamaintain.

## How does it work ?
### Docker

First `docker/DockerFile` create a docker image from Datamaintain's image. 
The image embark the script folder with one script `v1.0_init.js`, and an entrypoint script.

Right now Datamaintain does not have a "lock" feature, so the entrypoint is here for implements
a lock in sh (in case two pods start at the same time).
Here how the entrypoint work :
* Insert a document (with id `datamaintain`) on the `datamaintainLock` collection.
The lock has an expiration date in case the init container is kill by Kubernetes. 
* If a document already exist then wait and retry
* After the document is inserted then start Datamaintain
* Remove the lock
* If datamaintain crash, exit with code 1

### Kubernetes

Kubernetes sample is a simple yaml file `sample_k8s.yaml`. The file describes a pod named `busybox`,
the pod has no real purpose, it just show how to use Datamaintain in an init container.

The sample file use two init containers :
* `wait-mongo` that just check `mongodb://mongo-mongodb-replicaset-0.mongo-mongodb-replicaset/test` 
  is accessible and ready. In this sample we admit a mongo 4.2 is accessible.
* `datamaintain` that use the image in `docker/DockerFile` (with name `datamaintain-sample`)

## Try it
### Prerequisite
You need a Kubernetes cluster, or you can try with 
a local Kubernetes : [minikube](https://minikube.sigs.k8s.io/docs/) for example.

If you use a Kubernetes cluster you need to publish `datamaintain-sample`.
If you use `minikube` you can use a local image by executing in the `docker` folder :
```
eval $(minikube docker-env)
docker build -t datamaintain-sample .
```
### Install mongo
First we need to install a mongodb 4.2 :
```
helm upgrade --install mongo stable/mongodb-replicaset
```
Here we use Helm and the mongo stable/mongodb-replicaset chart. 
If you use another command or another chart you may need to change the mongo URI in `sample-k8s.yaml`.

### Start datamaintain-sample
Once mongo is ready you can add the pod `busybox` :
```
kubectl apply -f ./sample_k8s.yaml
```

You can follow busybox creation with
```
kubectl get pods
```

You can read logs of `wait-mongo` or `datamaintain` with
```
kubectl logs busybox -c wait-mongo
kubectl logs busybox -c datamaintain
```

Once busybox is started you can check that datamaintain start correctly by checking the `executedScripts` collection :
```
kubectl exec mongo-mongodb-replicaset-0 -- mongo --quiet --eval 'db.getCollection("executedScripts").find()'             ✔ ╱ minikube ⎈
```
You must find one entry : `v1.0_init.js`.
