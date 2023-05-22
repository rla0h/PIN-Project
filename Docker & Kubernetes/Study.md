[책제목] 쿠버네티스 완벽 가이드 
======================
> 저자 마사야 아오야마
> 박상욱 옮김

# 파드
* 워크로드 리소스의 최소 단위
* 한 개 이상의 컨테이너로 구성되며, 같은 파드에 포함된 컨테이너끼리는 네트워크적으로 격리되어 있지 않고 IP주소를 공유
    * 컨테이너가 두개 들어 있는 파드를 생성한 경우 이 두 컨테이너는 같은 IP 주소를 가짐
    * 파드의 내부 컨테이너는 서로 localhost로 통신할 수 있다.
## 파드 생성
```
apiVersion: v1
kind: Pod
metadata:
    name: sample-pod
spec:
    containers:
    - name: nginx-container
      image: nginx:1.16
```
* sample-pod 내부에 nginx:1.16 이미지를 사용한 컨테이너 하나를 기동하고 80/TCP 포트를 바인드 하는 단순한 파드.
* 매니패스트를 사용하여 파드 생성
```
# 파드생성
$ kubectl apply -f sample-pod.yaml
```

* 기동한 파드 확인
```
# 파드 목록을 표시
$ kubectl get pods

# 파드 상세 정보 표시
$ kubectl get pods --output wide
```

## 두 개의 컨테이너를 포함한 파드 생성
```
apiversion: v1
kind: pod
metadata:
    name: sample-2pod
spec:
    containers:
    - name: nginx-container
      image: nginx:1.16
    - name: redis-container
      image: redis:3.2
```
* 만약 같은 포트를 사용하는 두 개의 컨테이너를 가진 Pod를 생성하면 두 컨테이너중 하나만 기동한다.
    * 파드는 네트워크 네임스페이스를 공유하고 있으므로, 일반 VM 상에서 80/TCP 포트를 바인드하는 서비스를 하나 이상 사용할 수 없는 환경과 같다고 보면 됨.
    * 파드 내에서는 containerPort가 충돌하지 않도록 해야함

# 레플리카셋/레클리케이션 컨트롤러
* 파드의 레플리카를 생성하고 지정한 파드를 유지하는 리소스

## 레플리카셋 생성
* sample-rs.yamml
```
apiVersion: apps/v1
kind: ReplicaSet
metadata:
    name: sample-rs
spec:
    replicas: 3
    selector:
        matchLabels:
            app: sample-app
    template:
        metadata:
            labels:
                app: sample-app
        spec:
            containers:
            - name: nginx-container
              image: nginx:1.16
```
* 레플리카셋 생성 및 확인
```
# 레플리카셋 생성
$ kubectl apply -f sample-rs.yaml

# 레플리카셋 확인
$ kubectl get replicasets -o wide
```

## 파드 정지와 자동화된 복구
* 레클리카셋에서는 노드나 파드에 장애가 발생했을 때, 지정한 파드 수를 유지하기 위해 다른 노드에서 파드를 기동시켜 주기 때문에 장애 시에도 많은 영향을 받지 않음
* 쿠버네티스의 컨셉 중 하나 '자동화된 복구'
```
# 파드 하나를 정지 시켜서 확인해보자
# 실제 기동 중인 파드명을 지정
$ kubectl delete pod sample-rs-9f9kr

# 레플리카셋 목록 표시
$ kubectl get pods -o wide

#레플리카셋 상세 정보 표시
$ kubectl describe replicaset sample-rs
```

# 디플로이먼트
* Deployment는 여러 레플리카셋을 관리하여 롤링 업데이트나 롤백 등을 구현하는 리소스다.
* 디플로이먼트가 레플리카셋을 관리하고 레플리카셋이 파드를 관리하는 관계이다.
* 다음과 같은 순서로 동작
    1. 신규 레플리카셋을 생성
    2. 신규 레플리카셋의 레플리카 수(파드 수)를 단계적으로 늘림
    3. 이전 레플리카셋의 레플리카 수(파드 수)를 단계적으로 줄임
    4. (2, 3을 반복)
    5. 이전 레플리카셋은 레플리카 수를 0으로 유지

* 디플로이먼트를 사용하면 신규 레플리카셋에 컨테이너가 기동되었는지와 헬스 체크는 통과했는지를 확인하면서 전환 작업이 진행
* 레플리카셋의 이행 과정에서 파드 수에 대한 상세한 지정도 가능
    * 가령 하나의 파드만 기동하더라도 디플로이먼트 사용을 권장
* 파드로만 배포한 경우에는 파드에 장애가 발생하면 자동으로 파드가 다시 생성되지 않음
* 레플리카셋으로만 배포한 경우에는 롤링 업데이트 등의 기능을 사용할 수 없다.

## 디플로이먼트 생성
```
apiVersion: apps/v1
kind: Deployment
metadata:
    name: sample-deployment
spec:
    replicas: 3
    selector:
        matchLabels:
            app: sample-app
    template:
        metadata:
            labels:
                app: sample-app
        spec:
            containers:
            - name: nginx-container
              image: nginx:1.16
```

* 디플로이먼트 기동
```
# 업데이트 이력을 저장하는 옵션을 사용하여 디플로이먼트 기동
$ kubectl apply -f sample-deployment.yaml --record

# 레플리카셋 목록을 YAML 형식으로 표시
$ kubectl get replicasets -o yaml | head

# 디플로이먼트 확인
$ kubectl get deployments

# 레플리카셋 확인
$ kubectl get replicasets

# 파드 확인
$ kubectl get pods

# 컨테이너 이미지 업데이트
$ kubectl "set image" deployment sample-deployment nginx-container=nginx:1.17 --record

# 디플로이먼트 업데이트 상태 확인
$ kubectl rollout status deployment sample-deployment
```

* 업데이트 후 레플리카셋이 새롭게 생성 거기에 연결되는 형태로 파드도 다시 생성
* 이때 내부적으로는 롤링 업데이트가 되기 때문에 실제 서비스에는 영향이 없음

## 디플로이먼트 업데이트(레플리카셋 생성되는) 조건
* 디플로이먼트에서는 변경이 발생하면 레플리카셋이 생성
    * 이 '변경'에는 레플리카수의 변경 등은 포함되어 있지 않으며 '생성된 파드의 내용 변경'이 필요하다.
    * 즉, sepc.template에 변경이 있으면 생성된 파드의 설정이 변경되기 때문에 레플리카셋을 신규로 생성하고 롤링 업데이트를 하게 된다.
    * 위 에제에서는 파드 정의 내용에서 image를 nginx:1.16 에서 nginx:1.17로 변경했기 때문에 파드 템플릿 해시가 다시 계산되어 레플리카셋이 신규 생성됨

## 변경 롤백
* 디플로이먼트에는 변경 롤백 기능이 있다.
* 롤백 기능의 실체는 현재 사용 중인 레플리카셋의 전환과 같은 것
* 디플로이먼트가 생성한 기존 레플리카셋은 레플리카 수가 0인 상태로 남아있기 때문에 레플리카 수를 변경시켜 다시 사용할 수 있는 상태가 된다.
* 변경 이력을 확인할 때는 kubectl rollout history 명령어를 사용
```
# 변경 이력 확인
$ kubectl rollout history deployment sample-deployment

# 더 상세한 정보를 가져오려면 --revision 옵션 지정
# 초기 상태의 디플로이먼트
$ kubectl rollout history deployment sample-deployment --revision 1

# 한번 업데이트 된 후의 디플로이먼트
$ kubectl rollout history deployment sample-deployment --revision 2

# 롤백하려면 kubectl rollout undo를 사용
# 버전 번호를 지정하여 롤백하는 경우
$ kubectl rollout undo deployment sample-deployment --to-revisino 1

# 바로 이전 버전으로 롤백하는 경우(기본값인 --to-revision 0 이 지정되어 바로 이전 버전으로 롤백)
$ kubectl rollout undo deployment sample-deployment

# 롤백한 후 이전 레플리카셋의 파드가 기동됨
$ kubectl get replicasets
```

* 그러나 실제 환경에서는 롤백 기능 사용하는 경우가 많지 않음.
    * kubectl rollout 명령어보다 kubectl apply 명령어로 실행하여 적용하는 것이 호환성 면에서 더 좋기 때문

