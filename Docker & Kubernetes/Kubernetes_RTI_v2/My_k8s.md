KubeAdmin <!-- omit in toc -->
===
**Table of Contents**
- [Introduction](#introduction)
- [Start Clustering](#start-clustering)
    - [Master](#master)
    - [Worker](#worker)
- [Test](#test)
# Introduction
Kubnernets_RTI_V1과 달리 이번에는 **Kubeadm**을 활용한 다중 노드를 구현해보려고한다.

# Installation
* 아래 문서들을 참고하여 
- [Official Documentation](https://kubernetes.io/ko/docs/setup/production-environment/tools/kubeadm/install-kubeadm/)
* Install Container Runtime
* 공식문서에..
        ```
        파드에서 컨테이너를 실행하기 위해, 쿠버네티스는 컨테이너 런타임을 사용한다.기본적으로, 쿠버네티스는 컨테이너 런타임 인터페이스(CRI)를 사용하여 사용자가 선택한 컨테이너 런타임과 인터페이스한다. 런타임을 지정하지 않으면, kubeadm은 잘 알려진 엔드포인트를 스캐닝하여 설치된 컨테이너 런타임을 자동으로 감지하려고 한다. 컨테이너 런타임이 여러 개 감지되거나 하나도 감지되지 않은 경우, kubeadm은 에러를 반환하고 사용자가 어떤 것을 사용할지를 명시하도록 요청할 것이다.
        ```
- [Install containerd](https://github.com/containerd/containerd/blob/main/docs/getting-started.md) by [DevKimbob](https://github.com/DevKimbob/Study_2023/blob/master/Kubernetes/KubeAdm.md)★
    > 고마운 호중이..
* [install containerd](https://www.itzgeek.com/how-tos/linux/ubuntu-how-tos/install-containerd-on-ubuntu-22-04.html)

# Start Clustering
## Master
* [kubeadm init](https://medium.com/finda-tech/overview-8d169b2a54ff)
    * kubeadm init 하기 전에 **sudo swapoff -a** 를 해주어야한다.
* kubeadm init 중 오류 발생
    * init 중 오류 중 kubelet isn't running 이라는 오류가 있었는데, systemctl status kubelet 을 확인해보니 꺼져있음.
    * 해결방법
        1. kubeadm reset
        2. kubelet restart
        3. sudo kubeadm init --pod-network-cidr=10.244.0.0/16 --apiserver-advertise-address=($Ubuntu-address)
    * kubeadm pod networking settings
        * 세팅할 클러스터에서 Pod가 서로통신할 수 있도록 Pod 네트워크애드온을 설치해야함
        * kubeadm을 통해 만들어진 클러스터는 CNI(Container Network Interface)기반의 애드온이 필요
            * 기본적으로 kubernetes에서 제공해주는 kubenet 이라는 네트워크 플러그인이 있지만, 매우 기본적이고 간단한 기능만 제공하는 네트워크 플러그인임.
        * 따라서, kubeadm은 kubernetes가 기본적으로 지원해주는 네트워크 플러그인 kubenet을 지원하지 않고, CNI 기반 네트워크만 지원
        * [CNI 기반 Pod 네트워크 애드온 설치](https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/create-cluster-kubeadm/#pod-network)
        * **Flannel** Pod 네트워크 애드온
            * kubeadmin init --pod-network-cidr=10.244.0.0/16 으로 사용
                * 10.244.0.0./16은 Flannel 에서 기본적으로 권장하는 네트워크 대역
            * 호스트 네트워크에서 10.244.0.0/16 네트워크를 사용하고 있다면, --pod-network-cidr 인자 값으로 사용하고 있지 않은 다른 네트워크 대역을 넣어야 한다.
            * 
                ``` bash
                sysctl net.bridge.bridge-nf-call-iptables=1
                ``` 
            * 명령어를 실행하여 /proc/sys/net/bridge/bridge-nf-call-iptables 의 값을 1로 설정
                #### _[자세한내용 참조](https://kubernetes.io/docs/concepts/extend-kubernetes/compute-storage-net/network-plugins/#network-plugin-requirements)_
* API Server 주소 설정
    * 마스터 노드의 IPv4의 주소를 API Server 주소로 사용
        * --apiserver-advertise-address 라는 옵션을 사용
* 최종으로 위 해결방법 **3번**으로 사용
* **Flannel** 적용 (위 3개 명령어는 꼭 root 계정이 아닌 사용자 계정에서 진행해야함)
```bash
$ mkdir -p $HOME/.kube
$ sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
$ sudo chown $(id -u):$(id -g) $HOME/.kube/config
$ kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
```
* WorkerNode 작업을 하기 전에 Masternode의 token, ca-cert를 알아가자
``` bash
$ kubeadm token list
$ openssl x509 -pubkey -in /etc/kubernetes/pki/ca.crt | openssl rsa -pubin -outform der 2>/dev/null | openssl dgst -sha256 -hex | sed 's/^.* //'
```
* 만약 token이 24시간 지나서 만료 되었을때..
```bash
kubeadm token create
``` 

## Worker
```bash
# worker node 1
$ sudo kubeadm join 192.168.159.132:6443 --token 7qzh1c.ql2xpsurxjmhvb9a --discovery-token-ca-cert-hash sha256:43133ff004f43ba584f56520bb604bee5662d372716f6a646a3567db3306dda2 --node-name=worker1
# worker node 2
$ sudo kubeadm join 192.168.159.132:6443 --token 7qzh1c.ql2xpsurxjmhvb9a --discovery-token-ca-cert-hash sha256:43133ff004f43ba584f56520bb604bee5662d372716f6a646a3567db3306dda2 --node-name=worker2
```

# Test
## Run HelloWorld Deployment
* Google에서 제공하는 Hello World Sample Porject Example
* Master Node에서 실행
```bash
$ kubectl create deployment kubernetes-bootcamp --image=gcr.io/google-samples/kubernetes-bootcamp:v1
$ kubectl get deployments
$ kubectl get pods -o wide
```
## Check Pod IP
* My Hello World Pod IP = 10.244.3.2
## Check Node IP
```bash
$ ifconfig
```
* Check Flannel IP = 10.244.0.0
* Check cni0 IP = 10.244.0.1
    * Flannel의 10.244.0.xxx 대역을 통해 노드 내부에서 각 Pod 끼리 찾아 서로 통신할 수 있도록 네트워크가 구성
## Check Worker Node IP (Worker Node 1)
* Check Flannel IP = 10.244.3.0 
* Check cni0 IP = 10.244.3.1

## 따라서 10.244.3.2의 IP 주소를 가진 Pod는 워커 노드와 같은 네트워크 대역을 사용하고 있으므로 Pod는 워커노드에 생성되었다는 것을 확인할 수 있다.
* Pod 내부에 떠있는 Hello World 프로젝트 프로세스가 사용하고 있는 포트 번호는 8080 이므로 워커 노드에 접속하여 curl 명령어를 통해 Pod 내부에 떠있는 컨테이너로 통신을 시도
```bash
$ curl http://10.244.3.2:8080
Hello Kubernetes bootcamp! | Running on: kubernetes-bootcamp-69fbc6f4cf-wn7h2 | v=1
```
# Apply Pod to Node
> 원래 Local에 있는 도커 이미지를 사용하려 했지만 실패.. 그래서 도커 허브에 이미지를 올려서 Deployment에서 Pull 하여 적용하였다. (SSD의 소중함을 깨달았다)
## Pub-Sub
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: rti-pub-sub
spec:
  replicas: 3
  selector:
    matchLabels:
      app: rti
  template:
    metadata:
      labels:
        app: rti
    spec:
      containers:
        - name: publisher-subscriber
          image: happykimyh/rti_pub_sub:v3
          securityContext:
             privileged: true
          command: ["/bin/sh", "-ec", "while :; do echo 'Hello World'; sleep 5 ; 
done"]
      nodeName: worker1
```

## PostgreSQL
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: rti-db
spec:
  replicas: 1
  selector:
    matchLabels:
      app: rti
  template:
    metadata:
      labels:
        app: rti
    spec:
      containers:
        - name: database
          image: happykimyh/rti-db:v1
          securityContext:
             privileged: true
          command: ["/bin/sh", "-ec", "while :; do echo 'Hello World'; sleep 5 ; done"]
      nodeName: worker2
```

## Check there is a Pod in Node
```bash
kubectl get pods --field-selector spec.nodeName=worker1
kubectl get pods --field-selector spec.nodeName=worker2
```

## If Not Command Kubectl get nodes at Worker Node
* Worker Node에서 kubectl get nodes 하였는데 localhost:8080 이나 다른 오류가 발생할 때
    * Master Node에서 만들었던 $HOME/.kube/config 파일을 Worker Node의 .kube/config 으로 복사해준 뒤 kubectl get nodes
    * 잘 실행됨

# Connect from Publisher to Subscriber
## Dockerfile 수정
```docker
FROM ubuntu:20.04

RUN rm -rf /var/lib/apt/lists/*
RUN apt-get update && apt-get -y install openjdk-17-jdk

WORKDIR /test
COPY [".", "."]
WORKDIR /test/src
ENV NDDS_QOS_PROFILES=/test/src/k8s_v3_jar/USER_QOS_PROFILES.xml
RUN javac -cp ../lib/*:. RecloserTopicSubscriber.java
RUN javac -cp ../lib/*:. RecloserTopicPublisher.java
#RUN java -cp ../lib/*:. -Djava.library.path=../x64Linux4gcc7.3.0 RecloserTopicPublisher 
#172.17.0.2
```
* 다른 Node에 있는 Subscriber와 통신하기 위해서 **USER_QOS_PROFILES.xml** 을 수정하여야한다.
* 아래 discovery qos를 추가(pub, sub 둘다)
```xml
<discovery>
		<initial_peers>
			<element>(Pub or Sub IP Addr)</element>
		</initial_peers>
</discovery>
```
* 하지만 Pod 내에는 RTI관련 환경변수가 적용되어있지 않기때문에 **ENV** 명령어를 통해 환경변수를 적용시켜준다.

* 만약 도중에 Pod IP가 달라졌다면 다시 xml을 수정하여 재빌드 해준다.

# Connect from Subscriber to PostgreSQL
## Subscriber 수정
```java
String url = "jdbc:postgresql://192.168.159.134:30035/rti";
String username = "pin";
String password = "1234";
```
**NodePort로 30032를 처음에 열어주고 계속 30032 Port로 Database 연동을 시도했었다.
하지만 Nodeport도 30032, Postgres도 30032에서 Listen 하기 때문에 Port 충돌로 인해 연동이 되지 않았다.
Nodeport는 사실상 외부에서 접속을 하기 위한건데 나는 클러스터 내부에서 계속 같은 포트가 2개인 30032로 연동을 하려 했기때문에 연동이 되지 않은것이다
따라서 Postgres의 Listen Port를 30035로 변경하였더니 드디어 연동이 되었다.**

* 통신하기전
  * 방화벽 확인
    ```bash
    $ ufw allow <port>
    ```
  * ping 확인
    ```bash
    $ ping IP port
    ```
  * netcat 확인
    ```bash
    $ nc -l port
    $ nc ip port
    ```
# Complete
## ToDo
* Node Port 사용(외부 클러스터에서 접속)
* Kubernetes Volume 사용