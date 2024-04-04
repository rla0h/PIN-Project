Thanos
---
## Ref site
* https://devocean.sk.com/blog/techBoardDetail.do?ID=163458


# required 
* prometheus
* helm

# Install
```bash
wget https://github.com/thanos-io/thanos/releases/download/v0.34.1/thanos-0.34.1.linux-amd64.tar.gz
```

## helm add
```bash
helm repo add bitnami
```
## Set secrete objstore.yml
```yaml
type: FILESYSTEM
config:
  directory: /home/pin/store
```
* 여기선 Local Filesystem을 이용하여 Test 용도로 하였지만, Objstore(S3, GCS, Azure 등) 계정 생성하여 인스턴스를 이용해 서비스와 연동이 가능
## Create Secret 
```bash
create secret generic pin-objstore-secret --from-file=objstore.yml 
```
## Set thanos value-override-thanos-sidecar.yaml
```yaml
prometheus:
  prometheusSpec:
    thanos:
      minTime: -3h
      objectStorageConfig:
        key: objstore.yml
        name: pin-objstore-secret
  thanosService:
    annotations: {}
    clusterIP: ""
    enabled: true
    labels: {}
    port: 10901
    portName: grpc
    targetPort: grpc
    type: ClusterIP
```

## Set upgrade prometheus
```bash
helm install upgrade -n monitoring -f value-override-thanos-sidecar.yaml
```
## Set thanos yaml
[yaml](./values.yaml)

## Install Thanos
```bash
helm install thanos -n monitoring bitnami/thanos -f values.yaml
```
## Set nordport thanos-query-frontend to 30030

