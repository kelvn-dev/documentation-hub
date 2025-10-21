# Helm

## Core

Helm is package manager for Kubernetes. It treats a collection of objects (like PV, Deployment, ...) as one package

Manage a large number of k8s manifest files is error-prone and time-consuming. Helm is designed specifically to address these challenges.

Instead of editing multiple YAML files, use a single values file to set configuration parameters

## Components

- Charts: packages comprised of files that include all the instructions Helm needs to create the Kubernetes objects required by an application
- Releases: A release is created when a chart is deployed to cluster. Each time perform an action, such as an upgrade or configuration change, a new revision is generated

## Commands

Manage repo
```
helm repo --help
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo list
helm repo update 
helm repo remove
```

Search chart in Artifact Hub
```
helm search hub nginx
```

Search chart accross all locations:
```
helm search nginx
```

install the chart from the Bitnami repository
```
helm repo add bitnami https://charts.bitnami.com/bitnami
helm install nginx-release bitnami/nginx --version=7.1.0
```

custom
```
helm pull --untar bitnami/nginx --version=7.1.0
vi nginx/values.yaml
helm install nginx-release ./nginx
```

manage
```
helm list
helm uninstall nginx-release
```

history
```
helm upgrade nginx-release bitnami/nginx
helm history nginx-release
helm rollback nginx-release 1
```

## Chart

### Sample

./values.yaml
```
replicaCount: 2
image:
  repository: nginx
  pullPolicy: IfNotPresent
  tag: "1.16.0"

nginx:
  create: true
  labels: 
    app: nginx
    type: proxy
```

templates/_helpers.tpl
```
{{- define "nginx-pod-labels" }}
{{- range $key, $val := .Values.nginx.labels }}
  {{ $key }}: {{ $val | quote }}
{{- end }}
{{- end }}
```

templates/deployment.yaml
```
{{- if .Values.nginx.create }}
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ .Release.Name }}-deployment
spec:
  replicas: {{ .Values.replicaCount }}
  selector:
    matchLabels:
      {{- include "nginx-pod-labels" . | indent 2 }}
  template:
    metadata:
      name: nginx-pod
      labels:
        {{- include "nginx-pod-labels" . | indent 4 }}
    spec:
      containers:
        - name: "{{ .Values.image.repository }}-container"
          image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
          imagePullPolicy: {{ .Values.image.pullPolicy }}
          ports:
            - name: http
              containerPort: 80
              protocol: TCP
{{- end }}
```

### Verification

- Linting: catch formatting errors and typos
  ```
  helm lint ./nginx
  ```
- Template: print rendered template
  ```
  helm template nginx-release ./nginx-chart
  ``` 
- Dry Run: Simulate deployment
  ```
  helm install nginx-release ./nginx-chart --dry-run
  ```


### Structure

- apiVersion: chart API version. Helm 3 charts use v2 and Helm 2 charts use v1
- appVersion: version of the packaged application (e.g., nginx version 5.8.1)
- version: chart version
- dependencies: dependent charts

./Chart.yaml
```
apiVersion: v2
appVersion: 5.8.1
version: 12.1.27
name: nginx
description: Web publishing platform for building blogs and websites.
type: application
dependencies:
  - condition: mariadb.enabled
    name: mariadb
    repository: https://charts.bitnami.com/bitnami
    version: 9.x.x
```
