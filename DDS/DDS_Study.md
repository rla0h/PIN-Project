___

- [Introduction DDS](#introduction-dds)
  - [Basic Concepts](#basic-concepts)
    - [Domain](#domain)
    - [Entity](#entity)
    - [Domain Participant](#domain-participant)
    - [Topic](#topic)
    - [Samplem, Instances, and Types](#samplem-instances-and-types)
    - [DataWriter](#datawriter)
    - [Publisher](#publisher)
    - [Subscriber](#subscriber)
  - [Discovery, Matching, and Association](#discovery-matching-and-association)
  - [QoS (Quality of Service) Policies](#qos-quality-of-service-policies)
  - [Listener](#listener)
  - [DDS Protocol](#dds-protocol)
# Introduction DDS
* DDS
  * Data Distribution Service의 준말
  * Object Management Group(OMG)사에서 게시한 publish-subscribe paradigm에 기반된 분산 시스템에 대한 사양
  * DDS 어플리케이션은 Topic 및 QoS 정책을 기반으로 하는 강력한 유형의 비동기 캐시 업데이트를 사용하여 네트워크 전체에서 데이터를 효율적으로 공유
  * DCPS
    * Data-Centric Publish-Subscribe
    * DDS 사양에 정의된 애플리케이션 모델
## Basic Concepts
* Overview of the DDS DCPS API
  
![Alt text](./image/DCPS.png)
### Domain
* Domain은 DCPS내의 기본 분할 단위(가장 작은 단위)
* 다른 각 Entity는 도메인에 속하며 동일한 도메인의 다른 Entity와만 상호 작용 할 수 있다.
  * 애플리케이션 코드는 여러 도메인과 자유롭게 상호 작용 할 수 있지만, 다른 도메인에 속하는 별도의 Entity를 통해 상호작용 해야함
* Domain은 Identifier에 의해 식별
### Entity
* Entity는 QoS 정책과 State가 있고, Listeners 및 waitsets과 함께 사용할 수 있는 Domain의 Entity.
* DCPS Domain의 다른 개념에 의해 구현되는 인터페이스
* QoS Policy는 해당 Entity에 맞게 specialized 된다.
### Domain Participant
* Domain Participant는 애플리케이션이 특정 도메인 내에서 상호작용하는 진입점 역할
* Discovery 과정을 통해 특정 도메인에 존재하는 같은 도메인에 존재하는 도멩니 참가자를 식별하고 통신 수행에 필요한 정보를 교환
* DataWriter와 DataReader가 존재하며 이들을 EndPoint라고 함
  * 실질적 통신은 이 EndPoint 사이에서 일어남
### Topic
* Topic은 Application Publisher와 Subscriber간의 기본 상호 작용 수단
* 각 Topic에는 Publisher와 Subscriber를 연결하는 Domain 내에서 고유한 이름 존재
* 여러 프로세스가 하나의 주제에 대해 Pub할 수 있고 여러 프로세스가 Many-To-Many 통신을 허용하는 Topic을 Subscription 할 수 있다.
* Publisher 프로세스는 Sample을 Publishing 할때 Topic을 지정하고 Subscrier 프로세스는 Topic을 통해 Sample을 요청
### Samplem, Instances, and Types
* Sample, Instance
  * DCPS 용어로 애플리케이션은 Topic에 대한 다양한 Instance에 대한 개별 데이터 샘플을 게시
    * 각 항목에는 샘플을 설명하는 특정 유형이 존재
    * 각 Topic 데이터 유형은 해당 키를 구성하는 0개 이상의 필드를 지정
    * 각 인스턴스는 키의 고유한 값과 연결
  * Publisher 프로세스 에서는 각 샘플에 대해 동일한 키 값을 사용하여 동일한 인스턴스에 여러 데이터 샘플을 Publishing
* Types
  * 컴파일 타임(Static Type) or 런타임(Dynamic Type)에 정의 (모두 사용 가능)
  * Static Type은 OMG Interface Definition Language를 사용하여 정의
  * Dynamic Type은 API를 통해 생성하거나 획득 (OpenDDS 지원 X)
### DataWriter
* Datawriter는 배포용 샘플을 도입하기 위해 Publishing application code에서 사용
* 각 Datawriter는 특정 Topic에 바인딩
* Application은 Datawriter의 유형별 interface를 사용하여 해당 Topic에 대한 샘플을 Publishing
* Datawriter는 Data를 DataReader에게 전달

### Publisher
* Publisher는 게시된 데이터를 가져와 도메인에 있는 모든 관련된 Subscriber에게 배포
* Participant는 여러 Publisher를 가질 수 있으며, 각 Publihser는 서로 다른 Topic에 관한 여러 Datawriter를 가질 수 있다.
### Subscriber
* Subscriber는 Publisher로부터 데이터를 받아 이에 속한 모든 관련된 DataReader에 전달
* DataReader는 특정 Topic에 바인딩되며, 애플리케이션은 Datareader의 유형별 Interface를 사용하여 Sample을 Subscriber 한다.

## Discovery, Matching, and Association
* Discovery는 Participant가 제공하는 Publications 및 Subscriptions에 대해 배우는 프로세스
* Matching
  * Remote Publisher 및 Subscription을 발견한 Participant는 Remote Entity와 비교하여 호환되는지 확인
  * Datawriter와 Datareader는 동일한 Topic에 있고, 호환가능한 유형이 있고, 호환 가능한 QoS가 있는경우 Matching
* Association
  * Local Entity가 Remote Entity와 일치하는 경우 Datawrtier에서 Datareader로 데이터가 흐르도록 구현이 구성
## QoS (Quality of Service) Policies
* DDS사양은 서비스에 대한 QoS 요구 사항을 지정하기 위해 애플리케이션에서 사용하는 다양한 QoS 정책을 정의
* Participant는 서비스에서 필요한 동작을 지정하고 서비스는 이러한 동작을 달성하는 방법을 결정
* 모든 정책이 모든 유형의 Entity에 유효한 것은 아니지만 이러한 정책은 다양한 DCPS Entity(Topic, Datawriter, Datareader, Publisher, Subscriber, Domain Participant)에 적용될 수 있다.
* Publisher와 Subscriber는 RxO(Request x Offered) 모델을 사용하여 일치
  * Subscriber는 최소한으로 필요한 정책 세트를 요청
  * Publisher는 잠재적인 Participant에게 일련의 QoS 정책을 제공
  * DDS 구현은 요청된 정책을 제공된 정책과 일치시키기 위해 시도
## Listener
* DCPS API는 애플리케이션이 해당 Entity와 관련된 특정 상태 변경 또는 이벤트를 수신할 수 있도록 하는 각 Entity에 대한 콜백 Interface를 정의
  * Ex) 읽을 수 있는 데이터 값이 있으면 DataReader Listener에 알림이 전송

## DDS Protocol
* DDS Protocol Stack

![Alt text](./image/DDS_Protocol_Stack.png)
* DDS는 세부 표준에 따라 DCPS와 RTPS 프로토콜로 구성
* DLRL
  * Data Local Reconstruction layer
  * DCPS 기능에 대한 인터페이스를 제공
  * IoT가 지원되는 장치 간에 분산된 데이터를 공유
* DCPS
  * Data-Centric Publish/Subscribe
  * 
  * 도메인 구성 객체, 데이터 발간/구독 인터페이스 등 Pub-Sub 하기 위한 인터페이스를 정의
  * QoS 항목과 QoS 항목들 사이의 관계와 적용 값의 우선순위를 정의
* RTPS
  * Real-Time Publish/Subscribe
  * 도메인을 구성하는 DomainParticipant 및 Endpoint에 대한 표준 검색 프로토콜을 정의
  * 데이터 발간 객체와 데이터 구독 객체의 동작, 데이터 송수신 상태 등을 정의
  * 데이터 Pub/Sub 모델을 지원하며 UDP/IP와 같이 신뢰성이 없는 전송계층 위에서도 동작 가능
  * DDS 상호 운용성을 위한 표준 유선 프로토콜
    * DDS를 구현한 제품들이 증가하게 되면서 이 제품들이 서로 만들어진 회사는 다르지만 서로 DDS간 통신을 하기 위해 RTPS Protocol을 이용