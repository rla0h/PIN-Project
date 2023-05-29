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