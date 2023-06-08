> 참고 https://m.blog.naver.com/love_tolty/222531413738


1. minikube install
```bash
$ minikube start
$ minikube kubectl -- get po - A
$ alias kubectl="minikube kubectl --"
$ kubectl get pods
```
2. create pod // just practice 안해도 될듯..?

# Create pod-manifest.yaml

## db-pod-manifest.yaml
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: db-pod
spec:
  containers:
  - name: db-container
    image: postgres
    imagePullPolicy: Never
    command: ["/bin/sh", "-ec", "while :; do echo 'Hello World'; sleep 5 ; done"]
```

## pub-pod-manifest.yaml
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: pub-pod
spec:
  containers:
  - name: pub-container
    image: pub-pod
    imagePullPolicy: Never
    command: ["/bin/sh", "-ec", "while :; do echo 'Hello World'; sleep 5 ; done"]
```

## sub-pod-manifest.yaml
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: sub-pod
spec:
  containers:
  - name: sub-container
    image: sub-pod
    imagePullPolicy: Never
    command: ["/bin/sh", "-ec", "while :; do echo 'Hello World'; sleep 5 ; done"]
```
## create pod using manifest
```bash
$ kubectl apply -f <manifestfilename>.yaml
```

3. deployment 생성 <파드 3개를 가진 레플리카세트 생성>
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
    name: rti-deployment
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
            - name: sub-container
              image: sub-pod
              imagePullPolicy: Never
              command: ["/bin/sh", "-ec", "while :; do echo 'Hello World'; sleep 5 ; done"]
            - name: pub-container
              image: pub-pod
              imagePullPolicy: Never
              command: ["/bin/sh", "-ec", "while :; do echo 'Hello World'; sleep 5 ; done"]
            - name: db-container
              image: postgres
              imagePullPolicy: Never
              command: ["/bin/sh", "-ec", "while :; do echo 'Hello World'; sleep 5 ; done"]

```
* 지정한 레이블을 가진 파드 정보 중 특정 JSON Path를 컬럼으로 출력
```bash
$ kubectl get pods -l app=rti -o custom-columns="NAME:{metadata.name}, IP:{status:podIP}"
```

4. Service 생성

* TODO list 23/05/30
1. Docker hub push 확인
2. hub에 들어간 image로 디플로이먼트 생성
3. 생성되면 서비스 생성 후 통신


* https://sweetcode.io/how-to-use-kubernetes-to-deploy-postgres/
> postgresql Volume 만들기 및 Service 생성

* TODO list 23/05/31
1. RTI certificate Expired
* 우분투 호스트 시간 바꾼 후 minikube start -> minikube 시작이 안됨
* 우분투 호스트 시간 현재로 한 뒤 minikube start 후 kubectl get pods 시 certificate has expired or is not valid 오류 발생
* 해결방법(gara) ->
1. 먼저 현재시간인 호스트상태에서 deployement or pod 올리기
2. pod에 접속하여 호스트의 시간 변경
3. 그후 java pub/sub 실행하면 시간 변경된채로 됨
* 꼭 pod 접속해서 시간을 변경해야함 그전에 시간 변경하고 pod 접속하려고 하면 안됨!!!!!
* app:rti 인 pod IP 출력
  * kubectl get pods -l app=rti -o custom-columns="NAME:{metadata.name}, IP:{status.podIP}"
* db pod : rti-deployment-db-6594f9494d-45pl8 -> ip: 10.244.1.70
* pub pod : rti-deployment-pub-5f846655f9-x92fb -> ip : 10.244.1.54
* sub pod : rti-deployment-sub-768fd89664-ghsmd -> ip : 10.244.1.60
* minikube IP 확인 : kubectl get nodes -o wide


* db connection
  * subscriber code 에서 postgres 연동 부분을 바꿔야한다.
  * 내 DB의 테이블명은 rti_table 이고 db명은 rti 사용자는 pin, pw = 1234 이다.
  * postgresql 은 도커 컨테이너를 볼륨으로 만들어 컨테이너가 삭제되도 데이터는 유지되도록 하였다.
  * 따라서 postgres 이미지로 컨테이너를 빌드할때, 아래와 같이 하였음
  ```cmd
  docker run -p 30032:5432 --name postgres -e POSTGRES_PASSWORD=1234 -d -v ~/pgdata:/var/lib/postgresql/data postgres
  ```

  * 여기서 30032는 쿠버네티스 Nodeport 서비스를 설정할때 nodePort의 값이다.
  * 그리고 ~/pgdata는 내가 만들어준 볼륨을 로컬 폴더에 저장하기위해 만든 폴더 명이다.

  * 이렇게 해서 postgresql을 컨테이너를 생성하였으면 Sub 파드와 연결을 해주어야 한다.

* Sub 코드는 아래와 같다.
  ```java
  String url = "jdbc:postgresql://192.168.136.128:30032/rti";
        String username = "pin";
        String password = "1234";
  .
  .
  .
  Connection conn = DriverManager.getConnection(url, username, password))
                 {
                         System.out.println("Connected to the PostgreSQL server successfully.");

                 String sql = "INSERT INTO rti_table" + "(description, name, mrid, aliasname, topiccount) VALUES" +
                 " (? ,?, ?, ?, ?);";
                 PreparedStatement pstmt = conn.prepareStatement(sql);
  ```
  
  * 여기서 중요한 것은 192.168.136.128 IP 이다.
  * 이 IP는 로컬 Ubuntu의 IP 번호이다.
  * Nodeport 서비스를 통해 포트포워딩을 파드로부터 로컬까지 해주었기 때문에 db에 * * 연결하기 위해서 local Ubuntu의 IP Addr로 연결을 해주어야 한다.
  * 아래 나의 yaml 파일들을 적어 놓겠다.

* MY rti-service.yaml 파일
```yaml
apiVersion: v1
kind: Service
metadata:
  name: rti-service
spec:
  type: NodePort
  selector:
    app: rti
  ports:
    - name: pub-sub-port
      protocol: TCP
      port: 5432
      targetPort: 32
      nodePort: 30032
```
* My rti-deployment.yaml 파일
> 여기서 db는 안해도 됨

> 필요한 pub/sub 마다 주석 지우고 apply -f deployment.yaml 해주면 됨.
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
    name: rti-deployment-sub
    #name: rti-deployment-pub
    #name: rti-deployment-db
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
            - name: sub-container
              image: sub-pod
              imagePullPolicy: Never
              securityContext:
                privileged: true
            #  command: ["java", "-cp", "../lib/*:.", "-Djava.library.path=../x64Linux4gcc7.3.0", "RecloserTopicSubscriber"]
              command: ["/bin/sh", "-ec", "while :; do echo 'Hello World'; sleep 5 ; done"]
            #- name: pub-container
            #  image: pub-pod
            #  imagePullPolicy: Never
            #  securityContext:
            #    privileged: true
            #  command: ["/bin/sh", "-ec", "while :; do echo 'Hello World'; sleep 5 ; done"]
            #- name: db-container
            #  image: postgres
            #  imagePullPolicy: Never
              #image: happykimyh/rti-db:v1
              #imagePullPolicy: Always
            #  command: ["/bin/sh", "-ec", "while :; do echo 'Hello World'; sleep 5 ; done"]
```


