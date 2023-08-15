KubeAdmin <!-- omit in toc -->
===
**Table of Contents**
- [Introduction](#introduction)
- [Installation](#installation)
  - [error](#error)
  - [install on rasbian](#install-on-rasbian)
- [Start Clustering](#start-clustering)
  - [Master](#master)
  - [Metrics-server install](#metrics-server-install)
  - [Error Like Node Not Ready and Node describe no plugin on /opt/cni/bin](#error-like-node-not-ready-and-node-describe-no-plugin-on-optcnibin)
  - [Worker](#worker)
- [Test](#test)
  - [Run HelloWorld Deployment](#run-helloworld-deployment)
  - [Check Pod IP](#check-pod-ip)
  - [Check Node IP](#check-node-ip)
  - [Check Worker Node IP (Worker Node 1)](#check-worker-node-ip-worker-node-1)
  - [따라서 10.244.3.2의 IP 주소를 가진 Pod는 워커 노드와 같은 네트워크 대역을 사용하고 있으므로 Pod는 워커노드에 생성되었다는 것을 확인할 수 있다.](#따라서-1024432의-ip-주소를-가진-pod는-워커-노드와-같은-네트워크-대역을-사용하고-있으므로-pod는-워커노드에-생성되었다는-것을-확인할-수-있다)
- [Apply Pod to Node](#apply-pod-to-node)
  - [Pub-Sub](#pub-sub)
  - [PostgreSQL](#postgresql)
  - [Check there is a Pod in Node](#check-there-is-a-pod-in-node)
  - [If Not Command Kubectl get nodes at Worker Node](#if-not-command-kubectl-get-nodes-at-worker-node)
- [Connect from Publisher to Subscriber](#connect-from-publisher-to-subscriber)
  - [Dockerfile 수정](#dockerfile-수정)
- [Connect from Subscriber to PostgreSQL](#connect-from-subscriber-to-postgresql)
  - [Subscriber 수정](#subscriber-수정)
- [Complete](#complete)
  - [ToDo](#todo)
- [Get Service(Cluster-IP) from repo-pod](#get-servicecluster-ip-from-repo-pod)



# Introduction 
Kubnernets_RTI_V1과 달리 이번에는 **Kubeadm**을 활용한 다중 노드를 구현해보려고한다.

# Installation
## error
```bash
# apt-get update 404 error after docker install
$ cd /etc/apt/sources.list.d/
$ sudo rm docker.list
```
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
## install on rasbian
```bash
# Set Up br_netfilter module
$ cat <<EOF | sudo tee /etc/modules-load.d/k8s.conf
overlay
br_netfilter
EOF

$ sudo modprobe overlay
$ sudo modprobe br_netfilter

# modify kernel parmameter
$ cat <<EOF | sudo tee /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-iptables = 1
net.bridge.bridge-nf-call-ip6tables = 1
net.ipv4.ip_forward = 1
EOF

$ sudo sysctl --system
```
* ***쿠버네티스 1.24부터는 Docker Container Runtime이 쿠버네티스와 호환되지 않아 cri-dockerd 설치가 추가로 필요하다.***
* Container Runtime Install

```bash
# Not USED THIS
# $ sudo apt-get update
# $ sudo apt-get install ca-certificates curl gnupg lsb-release
# $ sudo curl -fsSL https://download.docker.com/linux/debian/gpg | sudo apt-key add -
# $ sudo echo "deb [arch=arm64] https://download.docker.com/linux/debian $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker-ce.list

# $ sudo apt-get update
# $ sudo apt-get install docker-ce
# $ sudo apt-get install pip
# $ sudo pip3 install docker-compose
# check docker & must show client & server
$ sudo docker version

# Try This using git
$ sudo apt-get install git
$ mkdir Downloads
$ cd Downloads/
$ git clone https://github.com/novaspirit/pi-hosted
$ ls
$ cd pi-hosted/
$ sudo ./install_docker.sh

$ suod docker version
# install cri-dockerd
$ wget https://github.com/Mirantis/cri-dockerd/releases/download/v0.3.4/cri-dockerd-0.3.4.arm64.tgz
$ sudo tar -xf cri-dockerd-0.3.4.arm64.tgz
$ sudo cp cri-dockerd/cri-dockerd /usr/bin/
$ sudo chmod +x /usr/bin/cri-dockerd
# Setting systemctl daemon start file
$ sudo su
$ cat <<"EOF" > /usr/lib/systemd/system/cri-docker.service
[Unit]
Description=CRI Interface for Docker Application Container Engine
Documentation=https://docs.mirantis.com
After=network-online.target firewalld.service docker.service
Wants=network-online.target
Requires=cri-docker.socket

[Service]
Type=notify
ExecStart=/usr/bin/cri-dockerd --container-runtime-endpoint fd://
ExecReload=/bin/kill -s HUP $MAINPID
TimeoutSec=0
RestartSec=2
Restart=always

# Note that StartLimit* options were moved from "Service" to "Unit" in systemd 229.
# Both the old, and new location are accepted by systemd 229 and up, so using the old location
# to make them work for either version of systemd.
StartLimitBurst=3

# Note that StartLimitInterval was renamed to StartLimitIntervalSec in systemd 230.
# Both the old, and new name are accepted by systemd 230 and up, so using the old name to make
# this option work for either version of systemd.
StartLimitInterval=60s

# Having non-zero Limit*s causes performance problems due to accounting overhead
# in the kernel. We recommend using cgroups to do container-local accounting.
LimitNOFILE=infinity
LimitNPROC=infinity
LimitCORE=infinity

# Comment TasksMax if your systemd version does not support it.
# Only systemd 226 and above support this option.
TasksMax=infinity
Delegate=yes
KillMode=process

[Install]
WantedBy=multi-user.target
EOF

# socket file config
$ cat <<"EOF" > /usr/lib/systemd/system/cri-docker.socket
[Unit]
Description=CRI Docker Socket for the API
PartOf=cri-docker.service

[Socket]
ListenStream=%t/cri-dockerd.sock
SocketMode=0660
SocketUser=root
SocketGroup=docker

[Install]
WantedBy=sockets.target
EOF

$ start cri-dockerd
systemctl daemon-reload
systemctl enable --now cri-docker
systemctl status cri-docker

# install kube-adm & kubelet
$ sudo su
$ DOWNLOAD_DIR="/usr/local/bin"
$ sudo mkdir -p "$DOWNLOAD_DIR"
$ CRICTL_VERSION="v1.27.0"
$ ARCH="arm64"
$ curl -L "https://github.com/kubernetes-sigs/cri-tools/releases/download/${CRICTL_VERSION}/crictl-${CRICTL_VERSION}-linux-${ARCH}.tar.gz" | sudo tar -C $DOWNLOAD_DIR -xz

$ RELEASE="$(curl -sSL https://dl.k8s.io/release/stable.txt)"
$ ARCH="arm64"
$ cd $DOWNLOAD_DIR
$ sudo curl -L --remote-name-all https://dl.k8s.io/release/${RELEASE}/bin/linux/${ARCH}/{kubeadm,kubelet}
$ sudo chmod +x {kubeadm,kubelet}

$ RELEASE_VERSION="v0.15.1"
$ curl -sSL "https://raw.githubusercontent.com/kubernetes/release/${RELEASE_VERSION}/cmd/kubepkg/templates/latest/deb/kubelet/lib/systemd/system/kubelet.service" | sed "s:/usr/bin:${DOWNLOAD_DIR}:g" | sudo tee /etc/systemd/system/kubelet.service
$ sudo mkdir -p /etc/systemd/system/kubelet.service.d
$ curl -sSL "https://raw.githubusercontent.com/kubernetes/release/${RELEASE_VERSION}/cmd/kubepkg/templates/latest/deb/kubeadm/10-kubeadm.conf" | sed "s:/usr/bin:${DOWNLOAD_DIR}:g" | sudo tee /etc/systemd/system/kubelet.service.d/10-kubeadm.conf
$ sudo systemctl enable --now kubelet

# install kubectl
$ curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/arm64/kubectl"
$ curl -LO "https://dl.k8s.io/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/arm64/kubectl.sha256"
$ echo "$(cat kubectl.sha256)  kubectl" | sha256sum --check
$ sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
$ kubectl version --client --short

# you need to install conntrack
$ sudo apt-get install conntrack

# start kubeadm
# Runtime	Path to Unix domain socket
# containerd	unix:///var/run/containerd/containerd.sock
# CRI-O	unix:///var/run/crio/crio.sock
# Docker Engine (using cri-dockerd)	unix:///var/run/cri-dockerd.sock
$ sudo kubeadm init --pod-network-cidr=10.244.0.0/16 --cri-socket unix:///var/run/cri-dockerd.sock

# reset kubeadm
$ sudo kubeadm reset --cri-socket unix:///var/run/cri-dockerd.sock
```
  * If you have Error.. Try This..
  * Error Message is
  ```bash
  # if you have error Try this
  # Found multiple CRI endpoints on the host. Please define which one do you wish to use by setting the 'criSocket' field in the kubeadm configuration file: unix:///var/run/containerd/containerd.sock, unix:///var/run/cri-dockerd.sock
  # To see the stack trace of this error execute with --v=5 or higher
  $ sudo swapoff -a
  $ cd /etc/docker
  $ sudo nano daemon.json                         
  {
  "exec-opts": ["native.cgroupdriver=systemd"],
  "log-driver": "json-file",
  "log-opts": {
  "max-size": "100m"
  },
  "storage-driver": "overlay2"
  }
  $ sudo systemctl enable docker
  $ sudo systemctl daemon-reload
  $ sudo systemctl restart docker

  # start kubeadm at Master
  $ sudo kubeadm init --pod-network-cidr=10.244.0.0/16 --cri-socket unix:///var/run/cri-dockerd.sock --apiserver-advertise-address <Master_IP> --apiserver-cert-extra-sans <Master_IP>
  ```

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
    * what's mean --pod-network-cidr=<ip>
        1. 10.244.0.0/16 --> Flannel
        2. 192.168.0.0/16 --> Calico
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

## Metrics-server install
```bash
$ curl -LO https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
# edit file
spec:
  containers:
  - args:
    - --cert-dir=/tmp
    - --secure-port=4443
    - --kubelet-preferred-address-types=InternalIP,ExternalIP,Hostname
    - --kubelet-use-node-status-port
    - --metric-resolution=15s
    - --kubelet-insecure-tls # add this line
    
$ kubectl apply -f components.yaml
$ kubectl top node
```
## Error Like Node Not Ready and Node describe no plugin on /opt/cni/bin
[reference link](https://vqiu.cn/k8s-cni-failed-to-find-plugein-bridge/)
```bash
$ wget https://github.com/containernetworking/plugins/releases/download/v1.0.1/cni-plugins-linux-arm64-v1.0.1.tgz
$ sudo tar axvf ./cni-plugins-linux-arm64-v1.0.1.tgz  -C /opt/cni/bin/
$ ls -l /opt/cni/bin/bridge
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
  name: rti-pub
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
        - name: publisher
          image: happykimyh/rti_pub:v4
          securityContext:
             privileged: true
          command: ["/bin/sh", "-ec", "while :; do echo 'Hello World'; sleep 5 ; done"]
      nodeName: worker2


---


apiVersion: apps/v1
kind: Deployment
metadata:
  name: rti-sub
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
        - name: subscriber
          image: happykimyh/rti_sub:v4
          securityContext:
             privileged: true
          command: ["/bin/sh", "-ec", "while :; do echo 'Hello World'; sleep 5 ; done"]
      nodeName: worker2
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
* Kubernetes PV/PVC
* CI/CD


# Get Service(Cluster-IP) from repo-pod
* To use CI/CD so I have to make service about repo-pod
* my service yaml file is...
```yaml
apiVersion: v1
kind: Pod
metadata:
    name: reposvc-pod
    labels:
      app: reposvc
spec:
  nodeName: worker1
  containers:
  - name: reposvc
    image: happykimyh/opendds:v1.2
    imagePullPolicy: Always
    command: ['sh', '-c', "while :; do echo '.'; sleep 5 ; done"]
    ports:
      - containerPort: 3434

---

apiVersion: v1
kind: Service
metadata:
  name: repo-service
spec:
  selector:
    app: reposvc
  ports:
  - name: repo-port
    protocol: TCP
    port: 1212
    targetPort: 3434
  type: ClusterIP
```

* and Checking Service IP (kubectl get service)
* to start in repository cmd..
```bash
# DCPSInfoRepo -ORBListenEndpoints iiop://$(hostname -i):<target port in service>
$ DCPSInfoRepo -ORBListenEndpoints iiop://$(hostname -i):3434
```
* to check tcp connection in pub/sub pod
```bash
# java -ea -cp classes:/DDS/NWT/lib/*:/DDNWT/bin:classes -Djava.library.path=$DDS_ROOT/lib NWT_TestPublisher -DCPSInfoRepo <service_clusterIP>:<port in service> -w
$ java -ea -cp classes:/DDS/NWT/lib/*:/DDNWT/bin:classes -Djava.library.path=$DDS_ROOT/lib NWT_TestPublisher -DCPSInfoRepo 10.111.221.56:1212 -w
```
  * it can do it