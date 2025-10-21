# K8s 

## Core

Là 1 open source orchestration giúp tự động hóa deployment, scaling và quản lý các ứng dụng đã dc container hóa

## Node

A node is a virtual machine where Kubernetes is installed, acting as a worker in cluster to run containerized applications. 2 loại node:

  - Master Node là trái tim, quản lý toàn bộ cluster.

  - Các Worker Node là các machines để chạy các Containers (Pods)

### Master node

Responsible for control and monitor the cluster’s state

- API Server: handling commands from users

- etcd Key-Value Store: store cluster data

- Scheduler: Assigns containers to nodes based on resource availability and other scheduling policies

- Controllers: monitor the state of nodes, containers and make decisions such as restarting containers when failures occur

### Worker node

- Container Runtime: Software such as Docker that runs the containers

- kubelet: An agent that runs on each node, ensuring containers are running as expected

## Pod

Pod is the smallest deployable object.

A pod can host multiple containers. Every container within it shares the same network, storage spaces and communicate via localhost

Quick deploy
```
k run nginx --image nginx
```

Inspect details
```
k get pod
k get pod nginx -o yaml
k describe pod nginx
```

Declarative way
```
k apply -f nginx.yaml
```

nginx.yaml
```
apiVersion: v1
kind: Pod
metadata:
  name: nginx-pod
  labels:
    app: nginx
    type: proxy
spec:
  containers:
  - name: nginx-container
    image: nginx:latest
```

## Deployment

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      name: nginx-pod
      labels:
        app: nginx
        type: proxy
    spec:
      containers:
      - name: nginx-container
        image: nginx
```

### Deployment Strategies

- Recreate Strategy: all existing instances are terminated before the new instances are deployed => causes downtime
- Rolling Update Strategy: the new version gradually replaces the old version without impacting application availability

If no strategy is specified, Kubernetes defaults to the rolling update strategy

#### Rolling update

2 ways:

- Modifying parameters and apply yaml file

- Update the Container Image Directly:
  ```
  k set image deployment/NGINX-deployment nginx-container=nginx:1.9.1
  ```


#### Rolling back

```
k rollout undo deployment/nginx-deployment
```

When you create a deployment for the first time, Kubernetes automatically triggers a rollout, creating a new deployment revision (revision one). Later, if you update your application—for example, by changing the container image version—a new rollout generates another deployment revision (revision two)

rollout history
```
k rollout history deployment/nginx-deployment
```

## Service

Service K8s service used to expose pod for internal or external communication

### NodePort

Expose pod for external access

- targetPort: The port on the Pod where the web server is running (8080)
- port: port on the Service object itself 
- nodePort: externally exposed port, must be within the range 30000–32767

```
apiVersion: v1
kind: Service
metadata:
  name: nginx-service
spec:
  type: NodePort
  ports:
    - targetPort: 8080
      port: 80
      nodePort: 30008
  selector:
    app: nginx
```

The only mandatory field is port. default targetPort value is port and default nodePort is random

### ClusterIP (Default)

Because pods's IPs are dynamic, we need to use service name as host to create a unified endpoint for internal communication. Incoming requests are automatically load balanced across the available pods

```
apiVersion: v1
kind: Service
metadata:
  name: db
spec:
  type: ClusterIP
  ports:
    - targetPort: 5432
      port: 80
  selector:
    app: db
```

### LoadBalancer

LoadBalancer functionality only works on supported cloud platforms. In environments like VirtualBox or local setups, setting the service type to LoadBalancer will default to NodePort behavior

## Ingress

Ingress requires:

- Ingress Controller: NGINX, HAProxy, or Traefik
- Ingress Resources: defines the rules that instruct the Ingress controller on routing incoming requests

types of ingress resources

- Default Backend: Routes all traffic to a single backend service
  ```
  apiVersion: extensions/v1beta1
  kind: Ingress
  metadata:
    name: nginx-ingress
  spec:
    backend:
      serviceName: nginx-service
      servicePort: 80
  ```

- Path-Based Routing: Routes traffic based on URL path segments
  ```
  apiVersion: extensions/v1beta1
  kind: Ingress
  metadata:
    name: nginx-ingress
  spec:
    rules:
    - http:
        paths:
        - path: /wear
          backend:
            serviceName: wear-service
            servicePort: 80
        - path: /watch
          backend:
            serviceName: watch-service
            servicePort: 80
  ```

- Host-Based Routing: Routes traffic based on domain name
  ```
  apiVersion: extensions/v1beta1
  kind: Ingress
  metadata:
    name: nginx-ingress
  spec:
    rules:
    - host: wear.my-online-store.com
      http:
        paths:
        - backend:
            serviceName: wear-service
            servicePort: 80
    - host: watch.my-online-store.com
      http:
        paths:
        - backend:
            serviceName: watch-service
            servicePort: 80
  ```

## Volume

```
apiVersion: v1
kind: Pod
metadata:
  name: random-number-generator
spec:
  containers:
    - image: alpine
      name: alpine
      command: ["/bin/sh", "-c"]
      args: ["shuf -i 0-100 -n 1 >> /opt/number.out;"]
      volumeMounts:
        - mountPath: /opt
          name: data-volume
  volumes:
    - name: data-volume
      hostPath:
        path: /data
        type: Directory
```

## Persistent volume & Persistent volume claim

Administrators can create a large pool of storage (PV), and users can request specific portions from that pool using PVC.

Each PV can only be used by 1 PVC, based on a best-match algorithm (capacity, access modes, volume modes) or selector

```
apiVersion: v1
kind: PersistentVolume
metadata:
  name: nginx-pv
spec:
  accessModes:
    - ReadWriteOnce
  capacity:
    storage: 1Gi
  hostPath:
    path: /tmp/d
```

```
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: nginx-pvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 500Mi
```

### Reclaim Policies

The reclaim policy determines what happens to a PV when its associated PVC is deleted

- Retain: PV remains available until manually deleted by an administrator
  ```
  persistentVolumeReclaimPolicy: Retain
  ```

- Delete: delete the PV
  ```
  persistentVolumeReclaimPolicy: Delete
  ```

- Recycle (Deprecated)

## Configmap

Declarative
```
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  APP_COLOR: blue
  APP_MODE: prod
```

Inject
```
envFrom:
  - configMapRef:
      name: app-config
```

## Secret

Declarative
```
apiVersion: v1
kind: Secret
metadata:
  name: app-secret
data:
  DB_Host: bXlzcWw=
  DB_User: cm9vdA==
  DB_Password: cGFzd3Jk
```

Inject
```
envFrom:
  - secretRef:
      name: app-secret
```

## Kubeconfig

The kubeconfig file is organized into three primary sections: clusters, users and contexts (Link clusters and users together, Ex: kelvin@minikube)

```
k config current-context
k config get-context
k config use-context kelvin@minikube
```

## Others

Field	Dockerfile Equivalent	Purpose
command	ENTRYPOINT	Overrides the default entry point of the image
args	CMD	Replaces the default arguments passed to the entry point
