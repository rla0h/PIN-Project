OpenDDS with Ubuntu 20.04
=
- ***Warning***
  - **라즈비안은 ARM 계열의 OS 이기 때문에 후에 Docker Image를 받을때에도 Arm 기반의 OS가 들어간 Docker Image를 받아야합니다.**
  - Docker File 에서 Arm 기반의 OS를 받을 때에도 Host OS 도 ARM 기반의 OS 이어야 합니다.
  - Windows 11 Vmware 에서는 ARM 기반의 OS를 올리지 못한다.
  - **즉 라즈베리에 Docker Image를 받겠다? -> ARM 기반의 OS에서 도커를 설치하고 도커파일에 ARM 기반의 OS를 받고 다시 이미지로 푸쉬한후 라즈베리에서 불러와 사용**
  - 
# How to Install OpenDDS on Ubuntu 20.04
## [Reference Link](https://github.com/OpenDDS/OpenDDS/blob/branch-DDS-3.23/java/INSTALL)
## Spec & Prepare to Install
* Spec
  * OS
    * Ubuntu 20.04
    * Ubuntu 20.04/ARM
  * JDK
    * jdk17
  * DB jar
    * PostgreSQL 42.5.4
  * IDE
    * Eclipse IDE
  * OpenDDS Version
    * OpenDDS 3.23.1
* Prepare
  * Perl
    * Already install on Ubuntu
    * If Not install using -> apt install perl
  * ACE TAO
    * If you install opendds, ace_tao compiler will be install too
  * Jave Install
    * jdk17 install
    * sudo apt-get install openjdk-17-jdk
  * GCC Install & G++ install
    * apt install build-essential -y

## Install
* Install OpenDDS 3.23.1  
  * Using curl
    ```bash
    $ curl -LJO https://github.com/OpenDDS/OpenDDS/archive/DDS-3.23.1.tar.gz
    $ tar -xzvf OpenDDS-DDS-3.23.1.tar.gz
    ```
  * Start
    ```bash
    cd $DDS_ROOT
       sudo ./configure --verbose --java --doc-group3 --features=java_pre_jpms=0
       # if ace error
       sudo ./configure --verbose --java --doc-group3 --features=java_pre_jpms=0 --ace=download
       # make
       sudo make
       # apply setenv
       source ./setenv.sh
    cd $DDS_ROOT/java/tests/multirepo
                ./run_test.pl
    ```
## DDS Communication

### Practice Example using Messenger_idl
* copy messenger_idl_test.jar, libmessenger_idl_test.so, libmessenger_idl_test.so.3.23.1 (Three files) to $DDS_ROOT/lib
* copy $DDS_ROOT/bin/repo.ior file to $DDS_ROOT/java/tests/messenger/publisher
* copy $DDS_ROOT/bin/repo.ior file to $DDS_ROOT/java/tests/messenger/subscriber
* Start to Repository 
  ```bash
  $DDS_ROOT/java/tests/messenger/publisher/DCPSInfoRepo -o repo.ior
  ```
* Start to Publisher
  ```bash
  $ $JAVA_HOME/bin/java -ea -cp classes:$DDS_ROOT/lib/*:classes -Djava.library.path=$DDS_ROOT/lib TestPublisher -DCPSConfigFile ../tcp.ini -w
  ```
* start to Subscriber
  ```bash
  $JAVA_HOME/bin/java -ea -cp classes:$DDS_ROOT/lib/*:classes -Djava.library.path=$DDS_ROOT/lib TestSubscriber -r
  ```
* Then, Success to Communicate with Pub & Sub
* ***repo.ior must exist in folder***
  

### IDL build
* NWT_IDL
    ```c
    /* NetworkTopology_RecloserTopic */
    module NWT{

        enum UnitSymbol {
            UnitSymbol_none,
            UnitSymbol_m
        };

        struct IdentifiedObject {
            string description;
            string mRID;
            string name;
            string aliasName;
        };

        struct Recloser {
            IdentifiedObject io;
            UnitSymbol us;
        };

        /* Topic */

        @topic
        struct RecloserTopic {
            Recloser r;
            @key
            long topicCount;
        };

    };
    ```
* Build IDL 
  * [Refernece Link](https://opendds.readthedocs.io/en/dds-3.25/devguide/java_bindings.html)
    ```bash
    $ACE_ROOT/bin/generate_export_file.pl NWT > NWT_Export.h
    ```
  * Create an MPC file, NWT.mpc:
    ```
    project: dcps_test_java {
        idlflags     += -Wb,stub_export_include=NWT_Export.h \
                        -Wb,stub_export_macro=NWT_Export
        dcps_ts_flags+= -Wb,export_macro=NWT_Export
        idl2jniflags += -Wb,stub_export_include=NWT_Export.h \
                        -Wb,stub_export_macro=NWT_Export
        dynamicflags += NWT_BUILD_DLL

        specific {
            jarname = NWT
        }

        TypeSupport_Files {
            NWT.idl
        }
    }
    ```
  * Run MPC to generate platform-specific build files.
    ```bash
    $ACE_ROOT/bin/mwc.pl -type gnuace
    ```
  * Compile the generated C++ and Java code
    ```bash
    make
    ```
  * Copy .so file about NWT idl to lib folder
  * Start like Messenger_IDL
* To Connection Another IP
  * Using DCPSInfoRepo
    ```bash
    # Start at one Computer (Pub or Sub)
    $ DCPSInfoRepo -ORBListenEndpoints iiop://:12345
    
    # Publisher
    $ java -ea -cp classes:/home/pin/lib/*:/home/pin/eclipse-workspace/NWT/bin:classes -Djava.library.path=$DDS_ROOT/lib NWT_TestPublisher -DCPSInfoRepo localhost:12345 -w

    # Subscriber
    $ java -ea -cp classes:/home/pin/lib/*:/home/pin/eclipse-workspace/NWT/bin:classes -Djava.library.path=$DDS_ROOT/lib NWT_TestSubscriber -DCPSInfoRepo <Publisher_IP>:12345 -r
    ```
# Here is Dockerfile

```md
# This is ARM OS
FROM arm64v8/ubuntu:20.04
# This is AMD os
FROM ubuntu:20.04

RUN apt-get update && apt-get upgrade -y

RUN apt-get install openjdk-17-jdk -y && apt-get install curl -y
RUN apt install build-essential -y
WORKDIR /DDS
RUN curl -LJO https://github.com/OpenDDS/OpenDDS/archive/DDS-3.23.1.tar.gz
RUN tar -xzvf OpenDDS-DDS-3.23.1.tar.gz
RUN rm OpenDDS-DDS-3.23.1.tar.gz
COPY [".", "."]
WORKDIR /OpenDDS-DDS-3.23.1
```

## In Docker Hub..
### [My Dockerhub site](https://hub.docker.com/repository/docker/happykimyh/opendds/general)
#### OpenDDS:v1.1 - Ubuntu:20.04(AMD)
#### OpenDDS:v1.2 - Ubuntu:20.04(ARM) <- If you Using Rasbian OS

## Create Deployment Using My OpenDDS Image
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: opendds-<pub or sub>
spec:
  replicaset: 3
  selector:
    matchLabels:
      app: <pub or sub>
  template:
    metadata:
      labels:
        app: <pub or sub>
    spec:
      nodeName: <WORKERNODE_NAME>
      containers:
      - name: <PUB OR SUB>
      # this v1.2 version is latests version at 23/08/09
        image: happykimyh/opendds:v1.2
        imagePullPolicy: Always
        command: ['sh', '-c', "while :; do echo '.'; sleep 5 ; done"]
        apiVersion: apps/v1

## this real using
kind: StatefulSet
metadata:
    name: opendds-pub
spec:
  replicas: 3
  selector:
    matchLabels:
      app: pub
  template:
    metadata:
      labels:
        app: pub
    spec:
      nodeName: worker1
      containers:
      - name: pub
        image: happykimyh/opendds:v1.3
        imagePullPolicy: Always
        command: ['sh', '-c', "while :; do echo '.'; sleep 5 ; done"]
# repo-pod
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
    image: happykimyh/opendds:v1.3
    imagePullPolicy: Always
    securityContext:
      runAsUser: 0
    command: ['sh', '-c', "while :; do echo '.'; sleep 5 ; done"]
   # command: ['sh', '-c', "DCPSInfoRepo -ORBListenEndpoints iiop://$(hostname -i):3434"]
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
## Inside making your Pub or Sub pod
### ***open a three terminal***
* Terminal(Using DCPSInfoRepo)
  ```bash
  # Execute one of pods 
  $ sudo kubectl exec -it <Pod Name> /bin/bash
  # In pod..
  $ cd /DDS/NWT/
  # Execute DCPSInfoRepo
  $ cd /DDS/NWT: DCPSInfoRepo -ORBListenEndpoints iiop://<IP>:12345
  # using Service on repo pod so i have static IP addr and port 1212:3434
  $ DCPSInfoRepo -ORBListenEndPoints iiop://$(hostname -i):1212
  ```
* Terminal(Using Publisher Start)
  ```bash
  # Execute one of pods 
  $ sudo kubectl exec -it <Pod Name> /bin/bash
  # In pod..
  $ cd /DDS/NWT/bin
  # Start Publisher
  $ cd /DDS/NWT/bin: java -ea -cp classes:/DDS/NWT/lib/*:/DDS/NWT/bin:classes -Djava.library.path=$DDS_ROOT/lib NWT_TestPublisher -DCPSInfoRepo <repo_IP>:12345 -w
  # using Service on repo-pod so I specify port like 1212 because portforward 1212:3434
  $ java -ea -cp classes:/DDS/NWT/lib/*:/DDS/NWT/bin:classes -Djava.library.path=$DDS_ROOT/lib NWT_TestPublisher -DCPSInfoRepo 10.105.253.179:1212 -w

  # to use CI/CD I apply tcp.ini (편리하게 하기 위해서 CI/CD)
  $ cat > tcp.ini
  [common]
  DCPSInfoRepo=10.99.229.105:1212 (Repo_service_IP:port)
  GlobalTransportConfig=$file
  DCPSBitTransportPort=1213 (Pub_service_port say to repo)

  [transport/1]
  transport_type=tcp
  # And start Publisher
  $ java -ea -cp classes:/DDS/NWT/lib/*:/DDS/NWT/bin:classes -Djava.library.path=$DDS_ROOT/lib NWT_TestPublisher -DCPSConfigFile tcp.ini -DCPSTransportDebugLevel 0 -w
  ```
* Terminal(Using Subscriber Start)
  ```bash
  # Execute one of pods 
  $ sudo kubectl exec -it <Pod Name> /bin/bash
  # In pod..
  $ cd /DDS/NWT/bin
  # Start Subscriber
  $ cd /DDS/NWT/bin: java -ea -cp classes:/DDS/NWT/lib/*:/DDS/NWT/bin:classes -Djava.library.path=$DDS_ROOT/lib NWT_TestSubscriber -DCPSInfoRepo <repo_IP>:12345 -r

  # to use CI/CD I apply tcp.ini (편리하게 하기 위해서 CI/CD)
  $ cat > tcp.ini
  [common]
i  DCPSInfoRepo=10.99.229.105:1212
  DCPSGlobalTransportConfig=$file
  #DCPSBitTransportIPAddress=10.98.132.8
  DCPSBitTransportPort=1223 (Sub_Service_port say to repo 리포지토리에게 자신의 트를 알리기 위해)

  [transport/1]
  transport_type=tcp
  $ java -ea -cp classes:/DDS/NWT/lib/*:/DDS/NWT/bin:classes -Djava.library.path=$DDS_ROOT/lib NWT_TestSubscriber -DCPSConfigFile tcp.ini -DCPSTransportDebugLevel 0 -r
  ```

* If you change DB_HOST_IP
  ```bash
  # you should change DataReaderListernImpl code
  $ cd /DDS/NWT/src: javac -cp classes:/DDS/NWT/lib/*:/DDS/NWT/bin:classes NWT_DataReaderListenerImpl.java
  # and remove /DDS/NWT/bin/NWT_DataReaderListenerImpl.class and move new class file
  ```
* My DataReaderListnerImpl.java code with Rasberry PI & MAC os connection 
  ```java
  // postgresql url, username, password input
        String url = "jdbc:postgresql://192.168.86.167:30000/postgres";
        String username = "postgres";
        String password = "1234";
  ```
  * And you should uyse small size spelling query in Java code when you use PostgreSQL!!!!!
  * 10.42.1.27

## add hosts name
```bash
# 서로 netcat 통신, cni 통신 등 Pod 끼리의 통신은 확인 되었지만 어째선지, DCPSInfoRepo에 찍먹(?)만 하고 바로 TCPConnection:Close 가 발생하였다. 알고보니 서로의 HOST Name(?)을 몰라서 일어나게 된 현상이었따.

$ vim /etc/hosts
# ADD all Pods Namespace LIKE this
# add..
172.16.235.135  repo-pod
172.16.189.71   opendds-sub-6964d4bc7b-jl7g6
172.16.235.134  opendds-pub-974857f77-w2lz

# 서로의 POD 를 각 Pod (Pub, sub, repo)에다 추가해준 뒤 통신해보니 잘되었따.
```

## to use CI/CD
```
txtCI/CD를 사용하기 위해 Pods terminal에 접속하지 않고 OpenDDS 통신을 하는 방법을 수없이 찾아 보았지만 결국 완벽한 방법을 찾지 못했다.
그나마 가능한 방법은 Pub, Sub, Repo Pod를 모두 Service로 만들어 IP를 주는 것이다.

Pub과 Sub의 Pod Name를 고정 시키기 위해 Statefulset으로 만들었으며,
ReplicaSet의 개수를 1개로 줄였다.(결국 통신하는건 하나 뿐이니까...)

Pub과 Sub은 고정된 Repository 의 Service IP로 통신을 시도하고..
Repository 에서는 /etc/hosts 에 pub과 Sub의 Servicce IP와 DomainName(pod name)을 명시해 준다.

Pub과 sub은 서로의 Pod IP!! 와 Pod name을 알아 주어야한다.

따라서 Repository는 고정된 IP만 이제 적용해주거나 설정을 해주면 되는데 Pub과 Sub은 따로 설정을 해주어야 할거 같다.. 

따라서 Jenkins 파일에서 Pod를 설정할 수 잇는 방법을 시도해보려고 한다.(230815 밤.)
```

## RTPS in Pod to Pod Transport
* RTPS 통신에 사용한 ini 파일
```ini
[common]
# DCPSInfoRepo=file://repo.ior
DCPSDefaultDiscovery=DEFAULT_RTPS
DCPSGlobalTransportConfig=$file

[domain/4]
DiscoveryConfig=uni_rtps

[rtps_discovery/uni_rtps]
SedpMulticast=0
ResendPeriod=2
SpdpSendAddrs=Peer_IP:Port

[transport/the_rtps_transport]
transport_type=rtps_udp
use_multicast=0
local_address=Host_IP:
```
* 위 ini 파일에서 **Port** 는 직접 계산을 하여 Port Number를 알아야한다. 
  * [참고문헌1](https://www.omg.org/spec/DDSI-RTPS/2.3/PDF#page=165&view=FitH,261.68)
  * [참고문헌2](https://opendds.readthedocs.io/en/latest/devguide/run_time_configuration.html#run-time-configuration-configuring-for-multiple-dcpsinforepo-instances)
  * 간단히 말해 Unicast 일땐 PB + DG * domainId + d1 + PG * participandId
  * Multicast 일땐 PB + DG * domainId + d0
    * PB = PortBaseNumber (default = 7400)
    * DB = Domain Gain (default = 250)
    * multicast에 쓰이는 d0 (default = 0)
    * unicast에 쓰이는 d1 (default = 10)
    * participantId 및 DomainID는 소스코드에서 찾아볼 수 있음
    * participantId = 0 (default)