# spring-cloud-deployer-kubernetes

## Building

Build the project without running tests using:

```
./mvnw clean install -DskipTests
```

## Integration tests

### Minikube

[Minikube](https://github.com/kubernetes/minikube) is a tool that makes it easy to run Kubernetes locally. Minikube runs a single-node Kubernetes cluster inside a VM on your laptop for users looking to try out Kubernetes or develop with it day-to-day.

Follow the instructions for installing Minikube [here](https://github.com/kubernetes/minikube#installation).

To start the Minikube cluster run:

```
minikube start
```

You should see a message saying 

```
Starting local Kubernetes cluster...
Kubectl is now configured to use the cluster.
``` 

#### Running the tests

Once the Minikube is up and running, you can run all integration tests against it with:

```
export KUBERNETES_MASTER=$(kubectl cluster-info | grep master | cut -d' ' -f6)
export KUBERNETES_NAMESPACE=default
$ ./mvnw test
```

### Google Container Engine

Create a test cluster and target it using something like (use your own project name, substitute --zone if needed):

```
gcloud container --project {your-project-name} clusters create "spring-test" --zone "us-central1-b" --machine-type "n1-highcpu-2" --scope "https://www.googleapis.com/auth/compute","https://www.googleapis.com/auth/devstorage.read_only","https://www.googleapis.com/auth/logging.write" --network "default" --enable-cloud-logging --enable-cloud-monitoring
gcloud config set container/cluster spring-test
gcloud config set compute/zone us-central1-b
gcloud container clusters get-credentials spring-test
```

#### Running the tests

Run the tests using:

```
export KUBERNETES_MASTER=$(kubectl cluster-info | grep master | cut -d' ' -f6)
export KUBERNETES_NAMESPACE=default
$ ./mvnw test
```
