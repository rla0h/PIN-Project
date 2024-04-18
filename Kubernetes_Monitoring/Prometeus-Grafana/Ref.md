Prometheus
---
https://xinet.kr/?p=3805
https://teichae.tistory.com/entry/Kubernetes-Monitoring-Grafana%EC%97%90-Prometheus-Data-Source-%EC%B6%94%EA%B0%80%ED%95%98%EA%B8%
---
# Prometheus Install
## Helm install
* [Helm install](https://helm.sh/docs/intro/install/)
* Kubernetes Opensource Package Manager
* Kubernetes용으로 구축된 소프트웨어를 제공, 공유 및 사용할 수 있는 기능을 제공

## Install Prometheus
* installed Prometheus Version = 57.0.2 tgz
  * wget install ..
### Modify Password in values yaml
  ```yaml
  adminPassword : 123!!@@##
  ```
### monitoring namespace create
```bash
kubectl create namespace monitoring
```
### helm install prometheus
```bash
helm install prometheus . -n monitoring -f values.yaml
```
### check prometheus 
```bash
kubectl get pod -n monitoring
kubectl get service -n monitoring
```
### Edit prometheus-grafana & prometheus-kube-prometheus-prometheus
* Change ClusterIP to NodePort
```bash
kubectl edit serivce -n monitoring prometheus-kube-prometheus-prometheus
~
ports:
  - name: http-web
    nodePort: 30090 // ADD
    port: 9090
    protocol: TCP
    targetPort: 9090
  - appProtocol: http
    name: reloader-web
    nodePort: 31474
    port: 8080
    protocol: TCP
    targetPort: reloader-web
  selector:
    app.kubernetes.io/name: prometheus
    operator.prometheus.io/name: prometheus-kube-prometheus-prometheus
  sessionAffinity: None
  type: NodePort // Change
status:
  loadBalancer: {}
```
```bash
kubectl edit service -n monitoring prometheus-grafana
~
spec:
  clusterIP: 10.103.190.169
  clusterIPs:
  - 10.103.190.169
  externalTrafficPolicy: Cluster
  internalTrafficPolicy: Cluster
  ipFamilies:
  - IPv4
  ipFamilyPolicy: SingleStack
  ports:
  - name: http-web
    nodePort: 31000 // ADD
    port: 80
    protocol: TCP
    targetPort: 3000
  selector:
    app.kubernetes.io/instance: prometheus
    app.kubernetes.io/name: grafana
  sessionAffinity: None
  type: NodePort // Change
status:
  loadBalancer: {}
```
* 외부에서 http://ip:30090 으로 접속시 Promethus UI 확인 가능
* http://ip:31000으로 접속하여 Grafana 접속
  * New Connnection (New data source0) => Prometheus 선택 => http://ip:30090 입력 후 Grafana와 연동

### Edit Grafana.ini
* /kube-prometheus-stack/charts/grafana 에 values.yaml 보면 grafana.ini 파일 설정할 수 있음
* 예를 들어 dashboard refresh rate가 1초가 최소인데, 250ms로 바꾸고 싶다? 아래처럼
```yaml
grafana.ini:
dashboards:  # add
  min_refresh_interval: 250ms # add
paths:
  data: /var/lib/grafana/
  logs: /var/log/grafana
  plugins: /var/lib/grafana/plugins
  provisioning: /etc/grafana/provisioning
analytics:
  check_for_updates: true
log:
  mode: console
grafana_net:
  url: https://grafana.net
server:
  domain: "{{ if (and .Values.ingress.enabled .Values.ingress.hosts) }}{{ .Values.ingress.hosts | first }}{{ else }}''{{ end }}"
```
