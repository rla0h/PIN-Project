[책제목] 쿠버네티스 완벽 가이드 
======================
> 저자 마사야 아오야마
> 박상욱 옮김
# 쿠버네티스 리소스 카테고리

* 워크로드 API 카테고리 : 컨테이너 실행에 관련된 리소스
    * 파드(Pod)
    * 레플리케이션 컨트롤러(ReplicationController)
    * 레플리카셋(ReplicaSet)
    * 디플로이먼트(Deployment)
    * 데몬셋(DaemonSet)
    * 스테이트풀셋(StatefulSet)
    * 잡(Job)
    * 크론잡(CronJob)

* 서비스 API 카테고리 : 컨테이너를 외부에 공개하는 엔드포인트를 제공하는 리소스
    * 서비스
        * ClusterIP
        * ExternalIP(ClusterIP의 한 종류)
        * NodePort
        * LoadBalancer
        * Headless(None)
        * ExternalName
        * None-Selector
    * 인그레스

* 컨피그 & 스토리지 API 카테고리 : 설정/기밀 정보/영구 볼륨 등에 관련된 리소스
    * 시크릿(Secrete)
    * 컨피그맵(ConfigMap)
    * 영구 볼륨 클레임(PersistentVolumeClaim)

* 클러스터 API 카테고리 : 보안이나 쿼터 등에 관련된 리소스, 클러스터 자체 동작을 정의하는 리소스
    * 노드(Node)
    * 네임스페이스(Namespace)
    * 영구 볼륨(PersistentVolume)
    * 리소스 쿼터(ResourceQuota)
    * 서비스 어카운트(ServiceAccount)
    * 롤(Role)
    * 클러스터 롤(ClusterRole)
    * 롤 바인딩(RoleBinding)
    * 클러스터롤바인딩
    * 네트워크 정책

* 메타데이터 API 카테고리 : 클러스터 내부의 다른 리소스를 관리하기 위한 리소스
    * LimitRange
    * HorizontalPodAutoscaler
    * PodDisturptionBudget
    * 커스텀 리소스 데피니션
    
* 워크로드 API 카테고리로 분류된 리소스는 클러스터에 컨테이너를 기동시키기 위해 사용되는 리소스
    * 사용자가 직접 사용하는 리소스
    1. 파드
    2. 레플리케이션 컨트롤러 (추천하지 않음)
    3. 레플리카셋
    4. 디플로이먼트
    5. 데몬셋
    6. 스테이풀셋
    7. 잡
    8. 크론잡

# 파드
* 워크로드 리소스의 최소 단위
* 한 개 이상의 컨테이너로 구성되며, 같은 파드에 포함된 컨테이너끼리는 네트워크적으로 격리되어 있지 않고 IP주소를 공유
    * 컨테이너가 두개 들어 있는 파드를 생성한 경우 이 두 컨테이너는 같은 IP 주소를 가짐
    * 파드의 내부 컨테이너는 서로 localhost로 통신할 수 있다.
## 파드 생성
```yaml
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
```bash
# 파드생성
$ kubectl apply -f sample-pod.yaml
```

* 기동한 파드 확인
```bash
# 파드 목록을 표시
$ kubectl get pods

# 파드 상세 정보 표시
$ kubectl get pods --output wide
```

## 두 개의 컨테이너를 포함한 파드 생성
```yaml
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
```yaml
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
```bash
# 레플리카셋 생성
$ kubectl apply -f sample-rs.yaml

# 레플리카셋 확인
$ kubectl get replicasets -o wide
```

## 파드 정지와 자동화된 복구
* 레클리카셋에서는 노드나 파드에 장애가 발생했을 때, 지정한 파드 수를 유지하기 위해 다른 노드에서 파드를 기동시켜 주기 때문에 장애 시에도 많은 영향을 받지 않음
* 쿠버네티스의 컨셉 중 하나 '자동화된 복구'
```bash
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
```yaml
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
```bash
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
```bash
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

# 서비스 API 카테고리

```
워크로드 API 카테고리 : 컨테이너 실행에 관련된 리소스
서비스 API 카테고리 : 컨테이너를 외부에 공개하는 엔드포인트를 제공하는 리소스
컨피그 & 스토리지 API 카테고리 : 설정/기밀 정보/영구 볼륨 등에 관련된 리소스
클러스터 API 카테고리 : 보안이나 쿼터 등에 관련된 리소스
메타데이터 API 카테고리 : 클러스터 내부의 다른 리소스를 관리하기 위한 리소스
```

* 서비스 API 카테고리로 분류된 리소스는 클러스터상의 컨테이너에 대한 엔드포인트를 제공하거나 레이블과 일치하는 컨테이너의 디스커버리에 사용되는 리소스
* 서비스
    * ClusterIP
    * ExternalIP(ClusterIP의 한 종류)
    * NodePort
    * LoadBalancer
    * Headless(None)
    * ExternalName
    * None-Selector
* 인그레스

# 쿠버네티스 클러스터 네트워크와 서비스
* 파드 내부에는 여러 컨테이너가 존재할 수 있는데, 같은 파드 내에 있는 컨테이너는 동일한 IP 주소를 할당 받게 된다.
* 같은 파드의 컨테이너로 통신하려면 localhost 로 통신하고, 다른 파드에 있는 컨테이너와 통신하려면 파드의 IP 주소로 통신

* 쿠버네티스 클러스터의 내부 네트워크
    * 클러스터를 생성하면 노드상에 파드를 위한 내부 네트워크가 자동으로 구성
    * 서비스를 사용하면 두 가지 큰 장점을 얻음
        1. 파드에 트래픽 로드 밸런싱
        2. 서비스 디스커버리와 클러스터 내부 DNS
    
# 파드에 트래픽 로드 밸런싱
* 서비스는 수신한 트래픽을 여러 파드에 로드 밸런싱(부하 분산) 하는 기능을 제공
    > 디플로이먼트를 사용하여 여러 파드를 기동할 수 있는데, 파드는 기동될 때마다 각각 다른 IP 주소를 할당받기 때문에 로드 밸런싱 하는 구조를 자체적으로 구현하려면 각 파드의 IP 주소를 매ㅓㄴ 조회하거나 전송 대상의 목적지를 설정해야한다.
* 그러나 서비스를 사용하면 여러 파드에 대한 로드 밸런싱을 자동을 구성 할 수 있다.
* 또한, 서비스는 로드 밸런싱의 접속 창구가 되는 엔드포인트도 제공

* 엔드포인트 설정을 위한 예제
```yaml
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
              image: amsy810/echo-nginx:v2.0
```
> 위 매니페스트에선 디플로이먼트가 생성할 파드에는 app: sample-app 레이블과 자동으로 부여되는 pod-template-hash:7c67dd9675(해시값) 레이블이 설정되어있음

```bash
# 출력 시 특정 JSON Path(예제에서는 레이블) 값만 출력
$ kubectl get pods sample-deployment-7c67dd9675-87v5d -o jsonpath='{.metadata.labels}'
```

* 다음은 엔드포인트의 서비스 종류의 ClusterIP를 사용하는 서비스를 생성해보겠따
> ClusterIP는 클러스터 내부에서만 사용 가능한 가상 IP를 가진 엔드포인트를 제공하는 로드 밸런서를 구성
* 서비스는 spec.selector에 정의할 셀렉터 조건에 따라 트래픽을 전송
* 아래 예는 app: sample-app 레이블을 가진 파드에 트래픽 로드 밸런싱하여 전송
```yaml
apiVersion: v1
kind: Service
metadata:
    name: sample-clusterip
spec:
    type: ClusterIP
    ports:
    - name: "http-port"
      protocol: "TCP"
      port: 8080
      targetPort: 80
    selector:
        app: sample-app
```

* 생성된 로드 밸런서의 트래픽이 전송되는 파드를 확인
> 먼저 app:sample-app 레이블을 가진 파드 IP 주소를 확인

```bash
# 지정한 레이블을 가진 파드 정보 중 특정 JSON Path를 컬럼으로 출력
$ kubectl get pods -l app=sample-app -o custom-columns="NAME:{metadata.name}, IP:{status.podIP}"
```
* 다음으로 서비스 상세 정보를 출력하면 Endpoints 항목에서 트래픽의 목적지 IP주소(그리고 포트)를 확인
* 방금 확인한 app: sample-app 레이블을 가진 파드 IP 주소가 같기 때문에 트래픽 전송이 셀렉터 조건에 따라 선택된 것을 확인
* 또한 로드 밸런싱을 위한 엔드포인트의 가상 IP는 CLUSTER-IP 항목이나 IP 항목에서 <해당 IP> 할당된 것을 확인

```bash
# 서비스 생성
$ kubectl apply -f sample-clusterip.yaml

# 서비스 정보 확인
$ kubectl get services sample-clusterip

# 서비스 상세 정보 확인
$ kubectl describe service sample-clusterip
```

* 생성한 로드 밸런서가 각 파드에 트래픽을 로드 밸런싱하여 전송하고 있는지를 확인
> 이 예제에서는 서비스 종류에 ClusterIP를 지정했기 때문에 엔드포인트 가상 IP는 클러스터 내부에서만 접속 가능한 IP 주소
> 그러므로 클러스터 내 별도 컨테이너를 기동하고 그 컨테이너에서 엔드포인트로 HTTP 요청을 보내 동작을 확인
> HTTP 요청은 로드 밸런싱 되어 어느 하나의 파드에서 요청을 처리하고 처리한 파드의 호스트 명을 포함한 HTTP 응답이 반환

```bash
# 일시적으로 파드를 시작하여 서비스 엔드포인트로 요청
$ kubectl run --image=amsy810/tools:v2.0 --restart=Never --rm -i testpod --command -- curl -s http:<SERVICE_IP:PORT>
```

* 하나의 서비스에 여러 포트를 할당 할 수도 있다.
```yaml
apiVersion: v1
kind: Service
metadata:
    name: sample-clusterip-multi
spec:
    type: ClusterIP
    ports:
    - name: "http-port"
      protocol: "TCP"
      port: 8080
      targetPort: 80
    - name: "https-port"
      protocol: "TCP"
      port: 8443
      targetPort: 443
    selector:
        app: sample-app
```

* 파드의 포트 정의에 이름을 지정해 놓으면 이름을 사용하여 참조 할 수 있다.
> 디플로이먼트의 포트 정의에서 80번 포트를 http라 명명하고, 서비스에서 참조할 때는 targetPort에ㅓ http로 참조

* 이름이 붙여진 포트를 가진 두개의 파드 예제
```yaml
apiVersion: v1
kind: Pod
metadata:
    name: sample-named-port-pod-80
    labels:
        app: sample-app
spec:
    containers:
    - name: nginx-container
    image: amsy810/echo-nginx:v2.0
    ports:
    - name: http # 포트에 이름 지정
    containerPort: 80
```
```yaml
apiVersion: v1
kind: Pod
metadata:
    name: sample-named-port-pod-81
    labels:
        app: sample-app
spec:
    containers:
    - name: nginx-container
      image: amsy810/echo-nginx:v2.0
      env:
      - name: NGINX_PORT
        value: "81"
      ports:
      - name: http #포트에 이름 지정
        containerPort: 81
```

* 이름이 붙여진 포트를 참조하는 서비스 예제
```yaml
apiVersion: v1
kind: Service
metadata:
    name: sample-named-port-service
spec:
    type: ClusterIP
    ports:
    - name: "http-port"
      protocol: "TCP"
      port: 8080
      targetPort: http # 이름으로 포트를 참조
    selector:
        app: sample-app
```

# ClusterIP 서비스
* ClusterIP 서비스는 쿠버네티스의 가장 기본이 되는 type: ClusterIP 서비스
* type: ClusterIP서비스를 생성하면 쿠버네티스 클러스터 내부에서만 통신 가능한, Internal Network에 생성되는 가상 IP가 할당
* ClusterIP와 통신은 각 노드상에서 실행 중인 시스템 구성 요소 kube-proxy가 파드로 전송을 실시
* ClusterIP는 쿠버네티스 클러스터 외부에서 트래픽을 수신할 필요가 없는 환경에서 클러스터 내부 로드 밸런서로 사용
* 기본적으로 쿠버네티스 API 에 접속하기 위한 Kubernetes 서비스가 생성되어 있고, ClusterIP가 발급되어 있다.

```bash
# 쿠버네티스 API에 접속할 수 있도록 쿠버네티스 서비스가 생성됨
$ kubectl get services
```

* ClusterIP 서비스 생성
> ClusterIP 서비스는 매니페스트로 생성
```yaml
apiVersion: v1
kind: Service
metadata:
    name: sample-clusterip
spec:
    type: ClusterIP
    ports:
    - name: "http-port"
      protocol: "TCP"
      prot: 8080
      targetPort: 80
    selector:
        app: sample-app
```
* spec.ports[].port 에는 ClusterIP에서 수신할 포트 번호를 지정
* spec.ports[].targetPort는 목적지 컨테이너 포트 번호를 지정

# ExternalIP 서비스
* ExternalIP 서비스는 특정 쿠버네티스 노드 IP 주소:포트에서 수신한 트래픽을 컨테이너로 전송하는 형태로 외부와 통신 할 수 있도록 하는 서비스

# NodePort 서비스
* NodePort 서비스는 ExternalIP 서비스와 비슷
* ExternalIP는 지정한 쿠버네티스 노드의 IP 주소:포트에서 수신한 트래픽을 컨테이너로 전송하는 형태로 외부와 통신할 수 있음
* 반면 NodePort는 모든 쿠버네티스 노드의 IP 주소:포트에서 수신한 트래픽을 컨테이너에 전송하는 형태로 외부와 통신할 수 있음

# NodePort 주의점
* Nodeport에서 사용할 수 있는 포트 범위는 GKE를 포함하여 많은 쿠버네티스 환경에서 30000~32767(쿠버네티스 기본값)
* 범위 외의 포트를 지정하려고 하면 에러 발생

# LoadBalancer 서비스
* LoadBalancer 서비스는 서비스 환경에서 클러스터 외부로부터 트래픽을 수신 할 때 가장 실용적이고 사용하기 편리한 서비스
* LoadBalancer 서비스는 쿠버네티스 클러스터 외부의 로드 밸런서에 외부 통신이 가능한 가상 IP를 할당할 수 있다.

* NodePort나 ExternalIP 에서는 결국 하나의 쿠버네티스 노드에 할당된 IP 주소로 통신을 하기 때문에 그 노드가 단일 장애점(Single Point of Failure, SPoF)이 되어 버린다.
* type: LoadBalancer에서는 쿠버네티스 노드와 별도로 외부 로드 밸런서를 사용하기 때문에 노드 장애가 발생해도 크게 문제되지 않음.