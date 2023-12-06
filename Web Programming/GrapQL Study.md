GraphQL Study
===
- [GraphQL Study](#graphql-study)
  - [Reference Site](#reference-site)
  - [GraphQL Official Doc study](#graphql-official-doc-study)
    - [GraphQL Introduction](#graphql-introduction)
    - [Query \& Mutation](#query--mutation)
      - [Fields](#fields)
      - [Arguments](#arguments)
      - [Alias](#alias)
      - [Fragments](#fragments)
        - [Using variables inside fragments](#using-variables-inside-fragments)
      - [Operation Name](#operation-name)
      - [Variables](#variables)
        - [Variables definitions](#variables-definitions)
        - [Default variables](#default-variables)
      - [Directives](#directives)
      - [Mutation](#mutation)
        - [Multiple fields in mutations](#multiple-fields-in-mutations)
      - [Inline Fragments](#inline-fragments)
        - [Meta Fields](#meta-fields)
    - [Schemas \& Types](#schemas--types)
      - [Type system](#type-system)
      - [Object types and fields](#object-types-and-fields)
      - [Arguments](#arguments-1)
      - [The Query and Mutation Types](#the-query-and-mutation-types)
      - [Scalar Types](#scalar-types)
      - [Enumeration Types](#enumeration-types)
      - [Lists and Non-Null](#lists-and-non-null)
      - [Interfaces](#interfaces)
      - [Union Types](#union-types)
      - [Input Types](#input-types)
- [GraphQL in Action (Book) Ref site](#graphql-in-action-book-ref-site)
  - [GraphQL in Action (Book Study)](#graphql-in-action-book-study)
    - [GraphQL 경험해보기](#graphql-경험해보기)
      - [1. GraphQL 소개](#1-graphql-소개)
        - [1.1 GraphQL 이란?](#11-graphql-이란)
      - [2. GraphQL API](#2-graphql-api)
        - [2.2 기초 GraphQL 언어](#22-기초-graphql-언어)
      - [3.GraphQL 작업 수정 및 구성](#3graphql-작업-수정-및-구성)
      - [4. GraphQL Schema 설계](#4-graphql-schema-설계)
    - [잠깐!](#잠깐)
      - [이후 단원은 실습으로...](#이후-단원은-실습으로)
- [Web APP API 개발을 위한 GraphQL Book Study](#web-app-api-개발을-위한-graphql-book-study)
  - [Web APP API 개발을 위한 GraphQL](#web-app-api-개발을-위한-graphql)
    - [1. Welcome to GraphQL](#1-welcome-to-graphql)
      - [**GraphQL은 클라이언트와 서버 간의 통신 명세(스펙)**](#graphql은-클라이언트와-서버-간의-통신-명세스펙)
      - [**GraphQL 설계 원칙**](#graphql-설계-원칙)
      - [**GraphQL Client**](#graphql-client)
    - [3. GraphQL query](#3-graphql-query)
      - [GraphQL Query](#graphql-query)
        - [Scalar \& Object Type](#scalar--object-type)
        - [Fragment](#fragment)
        - [Union](#union)
        - [Interface](#interface)
        - [Mutation](#mutation-1)
        - [Subscription](#subscription)
        - [introspection](#introspection)
        - [추상 구문 쿼리](#추상-구문-쿼리)
    - [4. Schema](#4-schema)
      - [Type](#type)
      - [Scalar Type](#scalar-type)
      - [Enumeration Type](#enumeration-type)
    - [4.2 연결과 리스트](#42-연결과-리스트)
      - [일대일 연결](#일대일-연결)
      - [일대다 연결](#일대다-연결)
      - [다대다 연결](#다대다-연결)
      - [통과 타입](#통과-타입)
      - [여러 타입을 담는 리스트](#여러-타입을-담는-리스트)
        - [Union Type](#union-type)
        - [Interface Type](#interface-type)
        - [Arguments](#arguments-2)
        - [Mutation](#mutation-2)
        - [Input Type](#input-type)
        - [Retury Type](#retury-type)
        - [Subscription Type](#subscription-type)

***
## Reference Site
* [official Link](https://graphql.org/)
* [Official Link kr](https://graphql-kr.github.io/learn/)
***

## GraphQL Official Doc study 
### GraphQL Introduction
* GraphQL은 API를 위한 쿼리언어 이며 타입 시스템을 사용하여 쿼리를 실행하는 서버사이드 런타임
* 특정한 데이터베이스나 특정한 스토리지 엔진과 관계되어 있지 않으며 기존 코드와 데이터에 의해 대체된다.
* GraphQL 서비스는 타입과 필드를 정의하고, 각 타입의 필드에 대한 함수로 구현된다.
  ```GraphQL
  type Query {     #Type
    me: User       #Field
  }

  type User {      #Type
    id: ID         
    name: String   #Field
  }
  ```
  ```c
  function Query_me(request) { //각 필드에 대한 함수
    return request.auth.user;
  }

  fucntion User_name(user) {
    return user.getName();
  }
  ```
* GraphQL 서비스가 실행되면 (일반적으로는 웹 서비스의 URL) GraphQL 쿼리를 전송하여 유효성을 검사하고 실행할 수 있습니다.
* 수신된 쿼리는 먼저 정의된 타입과 필드만 참조하도록 검사한 다음, 함수를 실행하여 결과를 생성합니다.
 ```GraphQL
 {
    me {
        name
    }
 }
 ```
 ```json
 {
    "me": {
        "name": "Luke-Skywalker"
    }
 }
 ```

 ### Query & Mutation
 #### Fields
 * GraphQL은 객체에 대한 특정 필드를 요청하는 것을 쿼리를 실행하여 쉽게 가능
 * Example 1
    ```GraphQL
    // Query
    {
        hero {
            name
        }
    }
    ```
    ```json
    // Result
    {
        "data": {
            "hero": {
                "name": "R2-D2"
            }
        }
    }
    ```
* GraphQL은 요청한 쿼리에 대한 결과를 정확히 받아볼 수 있다. 서버에서 클라이언트가 요청하는 필드를 정확히 알고 있기 때문
  * name Field는 "String" Type을 반환
  * Field는 객체를 참조할 수 있다 객체에 대한 필드를 하위 선택할 수 있다.
* **REST 구조 처럼 여러번 요청을 수행하는 대신 한번의 요청으로 많은 데이터를 가져올 수 있다.**
* Example 2
  ```GraphQL
  { // Query
    hero {
        name
        friends {
            name
        }
    }
  }
  ```
  ```json
  { // Result
  "data": {
    "hero": {
      "name": "R2-D2",
      "friends": [
        {
          "name": "Luke Skywalker"
        },
        {
          "name": "Han Solo"
        },
        {
          "name": "Leia Organa"
        }
      ]
    }
  }
  }
  ``` 
#### Arguments
* REST와 같은 시스템에서는 요청에 쿼리 파라미터와 URL 세그먼트같은 단일 인자들만 전달 할 수 있지만, GraphQL에서는 모든 필드와 중첩된 객체가 인자를 가질 수 있으므로 여러번의 API fetch를 완벽하게 대체할 수 있다.
* **필드에 인자를 전달하면, 모든 클라이언트에서 개별적으로 처리하는 대신 서버에서 데이터 변환을 한 번만 구현할 수 있다.**

#### Alias
* 필드이름은 같지만 Alias 값을 다르게 하여 쿼리를 요청할 수 있다.
* Example 3
```GraphQL
// Query
{
    empireHero: hero(episode: EMPIRE) {
        name
    }
    jediHero: hero(episode: JEDI) {
        name
    }
}
```
```json
// Result
{
    "data": {
        "empireHero": {
            "name": "Luke Skywalker"
        },
        "jediHero": {
            "name": "R2-D2"
        }
    }
}
```
* 위와 같이 두 hero 필드는 동일하지만, 서로 다른 이름의 별칭을 지정할 수 있으므로 한 요청에서 두 결과를 모두 얻을 수 있다.

#### Fragments
* Fragments를 사용하면 필드셋을 구성한 다음 필요한 쿼리에 포함시킬 수 있다.
* Example 4
  ```GraphQL
  { // Query
    leftComparison: hero(episode: EMPIRE) {
        ...comparisonFields
    }
    rightComparison: hero(episode: JEDI) {
        ...comparisonFields
    }
  }
  
  fragment comparisonFields on Character {
    name
    appearsIn
    friends {
        name
    }
  }
  ```
  ```json
  // Result
  {
  "data": {
    "leftComparison": {
      "name": "Luke Skywalker",
      "appearsIn": [
        "NEWHOPE",
        "EMPIRE",
        "JEDI"
      ],
      "friends": [
        {
          "name": "Han Solo"
        },
        {
          "name": "Leia Organa"
        },
        {
          "name": "C-3PO"
        },
        {
          "name": "R2-D2"
        }
      ]
    },
    "rightComparison": {
      "name": "R2-D2",
      "appearsIn": [
        "NEWHOPE",
        "EMPIRE",
        "JEDI"
      ],
      "friends": [
        {
          "name": "Luke Skywalker"
        },
        {
          "name": "Han Solo"
        },
        {
          "name": "Leia Organa"
        }
      ]
    }
  }
  }
  ```
* 프래그먼트 개념은 복잡한 응용프로그램의 데이터 요구사항을 작은 단위로 분할하는데 사용된다.

##### Using variables inside fragments
* 쿼리나 뮤테이션에 선언된 변수는 프래그먼트에 접근할 수 있다.
* Example 5
  ```GraphQL
    query HeroComparison($first: Int = 3) {
        leftComparison: hero(episode: EMPIRE) {
            ...comparisonFields
        }
        rightComparison: hero(episode: JEDI) {
            ...comparisonFields
        }
    }

    fragment comparisonFields on Character {
        name
        friendsConnection(first: $first) {
            totalCount
            edges {
            node {
                name
            }
        }
    }
  }
  ```
  ```json
  // Result
  {
  "data": {
    "leftComparison": {
      "name": "Luke Skywalker",
      "friendsConnection": {
        "totalCount": 4, // ?? 3아닌가,,
        "edges": [
          {
            "node": {
              "name": "Han Solo"
            }
          },
          {
            "node": {
              "name": "Leia Organa"
            }
          },
          {
            "node": {
              "name": "C-3PO"
            }
          }
        ]
      }
    },
    "rightComparison": {
      "name": "R2-D2",
      "friendsConnection": {
        "totalCount": 3,
        "edges": [
          {
            "node": {
              "name": "Luke Skywalker"
            }
          },
          {
            "node": {
              "name": "Han Solo"
            }
          },
          {
            "node": {
              "name": "Leia Organa"
            }
          }
        ]
      }
    }
  }
  }
  ```
#### Operation Name
* Example 6
  ```GraphQL
  // Query
  query HeroNameAndFriends {
    hero {
        name
        friends {
            name
        }
    }
  }
  ```
  ```json
  // Result
  {
    "data": {
        "hero": {
        "name": "R2-D2",
        "friends": [
            {
            "name": "Luke Skywalker"
            },
            {
            "name": "Han Solo"
            },
            {
            "name": "Leia Organa"
            }
        ]
        }
    }
  }
  ```
* Operation Types은 Query, Mutation, Subscription 이 될 수 있으며, 어떤 작업의 타입인지를 기술
* Operation Name은 의미 있고 명시적인 이름이어야 한다.
* 디버깅이나 서버 측에서 로깅하는데에 매우 유용하다.
  * 네트워크 로그나 GraphQL 서버에 문제가 발생하면 내용을 확인하는 대신 코드에서 쿼리 이름을 찾아내는 것이 더 쉽다.

#### Variables
* 클라이언트 측 코드는 쿼리 문자열을 런타임에 동적으로 조작하고 이를 GraphQL의 특정한 포맷으로 직렬화해야 하기 때문에, 이러한 동적인자를 쿼리 문자열에 직접 전달하는 것은 좋은 방법이 아님
* GraphQL은 동적 값을 쿼리에서 없애고, 이를 별도로 전달하는 방법을 제공 --> **변수**
* 변수를 사용하기 위한 사전 작업
  1. **쿼리안의 정적 값을 ```$variableName```으로 변경**
  2. **```$variableName```을 쿼리에서 받는 변수로 선언**
  3. **별도의 전송규약(일반적으로 JSON) 변수에 ```variableName: value```을 전달**
* Example 7
  ```GraphQL
  # Query
  query HeroNameAndFriends($episode: Episode) {
    hero(episode: $episode) {
        name
        friends {
        name
        }
    }
  }

  # Variables
  {
    "episode": "JEDI"
  }
  ```
  ```json
  // Result
  {
    "data": {
        "hero": {
        "name": "R2-D2",
        "friends": [
            {
            "name": "Luke Skywalker"
            },
            {
            "name": "Han Solo"
            },
            {
            "name": "Leia Organa"
            }
        ]
        }
    }
  }
  ```
##### Variables definitions
* 변수 정의는 위 쿼리에서 ```($episode: Episode)``` 부분
* 선언된 모든 변수는 스칼라, 열거형, input object type 이어야 한다.
* 변수 정의는 optional 이거나 required
  * 변수 타입 옆에 ! 가 없을 경우 Optional
  * 변수를 전달할 필드에 null이 아닌 인자가 요구된다면 required

##### Default variables
* 타입 선언 다음에 기본값을 명시하여 쿼리의 변수에 기본값을 할당할 수도 있다.
* Example 8
  ```GraphQL
  query HeroNameAndFriends($episode: Episode = "JEDI") {
    hero(episode: $episode) {
        name
        friends {
            name
        }
    }
  }
  ```
* 기본값이 제공되면 변수를 전달하지 않고도 쿼리 호출이 가능

#### Directives
* 변수를 사용하여 쿼리의 구조와 형태를 동적으로 변경하는 방법
* Example 9
  ```GraphQL
  # Query
  query Hero($episode: Episode, $withFriends: Boolean!) {
    hero(episode: $episode) {
        name
        friends @include(if: $withFriends) {
        name
        }
    }
  }
   
  { # variables
    "episode": "JEDI",
    "withFriends": false
  }
  ```
  ```json
  {
    "data": {
        "hero": {
            "name": "R2-D2"
        }
    }
  }
  ```
* Directives는 필드나 프래그먼트 안에 삽입될 수 있으며, 서버가 원하는 방식으로 쿼리 실행에 영향을 줄 수 있다.
* **GraphQL의 두가지 지시어**
  * ```@include(if: Boolean)``` : 인자가 true인 경우에만 이 필드를 결과에 포함
  * ```@skip(if: Boolean)```: 인자가 true이면 이 필드를 생략

#### Mutation
* 지금까지는 데이터가져오기(Fetch)에 초점을 맞추었지만 완전한 데이터 플랫폼은 서버 측 데이터를 수정할 수도 있어야 한다.
* REST에서는 모든 요청이 서버에 몇가지 사이드이펙트를 일으킬 수 있지만 규칙에 따라 데이터 수정을 위해 GET 요청을 사용하지 않는다.
* GraphQL도 마찬가지로 어떠한 쿼리든 데이터를 수정할 수도 있다.
* 하지만, **변경을 발생시키는 작업이 명시적으로 뮤테이션을 통해 전송되어야 한다는 규칙을 정하는 것이 좋다.**
* Example 10
  ```GraphQL
  # Query
  mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {
    createReview(episode: $ep, review: $review) {
        stars
        commentary
    }
  }
  # Variables
  {
    "ep": "JEDI",
    "review": {
        "stars": 5,
        "commentary": "This is a great movie!"
    }
  }
  ```
  ```json
  {
    "data": {
        "createReview": {
        "stars": 5,
        "commentary": "This is a great movie!"
        }
    }
  }
  ```
* ```createReview``` 필드가 새로 생성된 review의 stars와 commentary 필드를 반환
  * review는 스칼라값이 아닌 특별한 종류의 객체타입인 input object type

##### Multiple fields in mutations
* 뮤테이션은 쿼리와 마찬가지로 여러 필드를 포함할 수 있지만,
* **쿼리 필드는 병렬로 실행되지만 뮤테이션 필드는 하나씩 차례대로 실행된다.**
* 즉, 하나의 요청에서 두개의 incrementCredits 뮤테이션을 보내면 첫번째는 두번째 요청 전에 완료되는 것이 보장

#### Inline Fragments
* GraphQL 스키마에는 인터페이스와 유니온 타입을 정의하는 기능이 포함
* Example 11
  ```GraphQL
  # Query
  query HeroForEpisode($ep: Episode!) {
    hero(episode: $ep) {
        name
        ... on Droid {
        primaryFunction
        }
        ... on Human {
        height
        }
    }
  }
  
  # Variables
  {
    "ep": "JEDI"
  }
  ```
  ```json
  {
    "data": {
        "hero": {
        "name": "R2-D2",
        "primaryFunction": "Astromech"
        }
    }
  }
  ```
* hero 필드는 Character를 반환하는데, episode 인자에 따라서 Human 이나 Droid 중 하나일 수 있다.
* 필드를 직접 선택할 때에는 name 과 같이 Character 인터페이스에 존재하는 필드만 요청할 수 있다.
* 위 쿼리에서는 프래그먼트로 ```... on Droid```라는 레이블이 붙어있기 때문에 primaryFunction 필드는 hero에서 반환된 Character가 Droid 타입인 경우에만 실행
* Human 타입의 height 필드도 마찬가지.

##### Meta Fields
* GraphQL 서비스에서 리턴될 타입을 모르는 상황이 발생하면 클라이언트에서 해당 데이터를 처리하는 방법
* 메타 필드인 ```__typename``` 을 요청하여 그 시점에서 객체 타입의 이름을 얻을 수 있다.
* Example 12
  ```GraphQL
  {
    search(text: "an") {
        __typename
        ... on Human {
        name
        }
        ... on Droid {
        name
        }
        ... on Starship {
        name
        }
    }
  }
  ```
  ```json
  {
    "data": {
        "search": [
        {
            "__typename": "Human",
            "name": "Han Solo"
        },
        {
            "__typename": "Human",
            "name": "Leia Organa"
        },
        {
            "__typename": "Starship",
            "name": "TIE Advanced x1"
        }
        ]
    }
  }
  ```
* ```search```는 3가지 중 하나인 UNION 타입을 반환, ```__typename```필드가 없으면 클라이언트가 다른 타입을 구별하는 것은 불가능하다.

### Schemas & Types
#### Type system
* Schemas가 필요한 이유
  * 서버에서 요청할 수 있는 데이터에 대한 정확한 표현을 갖는것이 좋기에 어떤 필드를 선택할 수 있는지, 어떤 종류의 객체를 반환할 수 있는지, 하위 객체에서 사용할 수 있는 필드는 무엇인지 정확한 표현을 하기 위해
* 모든 GraphQL 서비스는 해당 서비스에서 쿼리 가능한 데이터들을 완벽하게 설명할 수 있는 타입들을 정의하고, 쿼리가 들어오면 해당 스키마에 대해 유효성이 검사된 후 실행된다.

#### Object types and fields
* Schema의 가장 기본적인 구성 요소는 객체 타입이다.
* Example 13
  ```GraphQL
  type Character {
    name: String!
    appearsIn: [Epiosode]!
  }
  ```
* ```Character``` : GraphQL의 객체 타입
  * 필드가 있는 타입
  * 스키마의 대부분의 타입은 객체 타입이다.
* ```name```과 ```appearIn``` : Character Type의 필드
  * ```name```과 ```appearIn```은 GraphQL 쿼리의 Character 타입 어디서든 사용할 수 있는 필드
* ```String!``` : non-nullable(null 값을 가질 수 없음)을 의미
  * 이 필드를 쿼리할 때 GraphQL 서비스가 항상 값을 반환한다는 것을 의미
  * 타입 언어에서는 이것을 느낌표로 나타낸다.
* ```[Episode]!``` : Episode 객체의 배열(array)을 나타낸다.
  * non-nullable 이기 때문에 appearIn 필드를 쿼리할 때 항상(0개 이상의 아이템을 가진) 배열을 기대할 수 있다.

#### Arguments
* GraphQL 객체 타입의 모든 필든느 0개 이상의 인수를 가질 수 있다.
* Example 14
  ```GraphQL
  type Starship {
    id: ID!
    name: String!
    length(unit: LengthUnit = METER): Float
  }
  ```
* Arguments는 필수 이거나 Optional이다.
  * Optional 일 경우 default 값을 정의할 수 있다.
  * unit 인자가 전달 되지 않으면 METER로 설정

#### The Query and Mutation Types
* 모든 GraphQL 서비스는 query 타입을 가지며 mutation 타입은 선택적이다.
* 일반 객체 타입과 동일하지만 모든 GraphQL 쿼리의 진입점(entry point)을 정의한다.
* Example 15
  ```GraphQL
  query {
    hero {
      name
    }
    droid(id: "2000") {
      name
    }
  }
  ```
  ```json
  {
    "data": {
      "hero": {
        "name": "R2-D2"
      },
      "droid": {
        "name": "C-3PO"
      }
    }
  }
  ```
* 즉 GraphQL 서비스는 hero 및 droid 필드가 있는 Query 타입이 있어야 한다.
  ```GraphQL
  tyep Query {
    hero(episode: Episode): Character
    droid(id: ID!): Droid
  }
  ```
* Mutation 타입도 필드를 정의하면 쿼리에서 호출할 수 있는 root Mutation 필드로 사용할 수 있다.

#### Scalar Types
* GraphQL 객테 타입은 이름과 필드를 가지지만 어떤 시점에서 구체적인 데이터로 해석되어야 할때 Scalar Types을 사용
* Example 16
  ```GraphQL
  {
    hero {
      name
      appearsIn
    }
  }
  ```
  ```json
  {
    "data": {
      "hero": {
        "name": "R2-D2",
        "appearsIn": [
          "NEWHOPE",
          "EMPIRE",
          "JEDI"
        ]
      }
    }
  }
  ```
* Int: 부호가 있는 32비트 정수
* Float: 부호가 있는 부동소수잠 값
* String: UTF-8 문자열
* Boolean: true 또는 false
* ID: ID scalar type은 객체를 다시 요청하거나 캐시의 키로써 자주 사용되는 고유 식별자
  * String과 같은 방법으로 직렬화되지만, ID로 정의하는 것은 사람이 읽을 수 있도록 하는 의도가 아니라는것을 의미
* custom Scalary Type 지정 예
  ```GraphQL
  scalar Data
  ```
#### Enumeration Types
* Enums 라고도 하는 열거형 타입은 특정 값들로 제한되는 특별한 종류의 Scalar Type
1. 타입의 인자가 허용된 값 중 하나임을 검증
2. 필드가 항상 값의 열거형 집합 중 하나가 될 것임을 타입 시스템을 통해 의사소통
* Scalar Type Enumeration Example
  ```GraphQL
  enum Episode {
    NEWHOPE
    EMPIRE
    JEDI
  }
  ```
* Episode 타입을 사용할 때마다 NEWHOPE, EMPIRE, JEDI 중 하나일 것이다.

#### Lists and Non-Null
* 객체 타입, 스칼라 타입, 열거형 타입은 GraphQL에서 정의할 수 있는 유일한 타입
* 하지만 스키마의 다른 부분이나 쿼리 변수 선언에서 타입을 사용하면 해당 값의 유효성 검사를 할 수 있는 타입 수정자(type modifiers)를 적용할 수 있다.
* Example 17
  ```GraphQL
  type Character {
    name: String!
    appearsIn: [Episode]!
  }
  ```
* String 타입을 사용하고 타입 뒤에 느낌표 ! 를 추가하여 Non-Null로 표시
  * 서버는 항상 이 필드에 대해 null이 아닌 값을 반환
  * null 값이 발생 시 GraphQL 실행오류 발생 및 클라이언트에 오류 알림
* Non-Null type modifier는 Arguments에도 사용 가능
  * GraphQL 서버가 문자열이나 변수 상관없이 null 값이 해당 인자로 전달되는 경우, 유효성 검사 오류를 반환
* Example 18
  ```GraphQL
  query DroidById($id: ID!) {
    droid(id: $id) {
      name
    }
  }

  # variable
  {
    "id": null
  }
  ```
  ```json
  {
    "errors": [
      {
        "message": "Variable \"$id\" of required type \"ID!\" was not provided.",
        "locations": [
          {
            "line": 1,
            "column": 17
          }
        ]
      }
    ]
  }
  ```
* List 는 해당 타입의 배열을 반환한다.
  * 스키마 언어에서, 타입을 대괄호 []로 묶는 것으로 표현
  * 유효성 검사 단계에서 해당 값에 대한 배열이 필요한 인자에 대해서도 동일하게 작동
* Non-Null 및 List modifier를 결합 가능
  * Null이 아닌 문자열 리스트를 가질 수 있음
* Example 19
  ```GraphQL
  myField: [String!]
  ```
  ```GraphQL
  myField: null  // valid
  myField: []    // valid
  myField: ['a', 'b']  // valid
  myField: ['a', null, 'b'] // error
  ```
* Example 20
  ```GraphQL
  myField: [String]!
  ```
  ```GraphQL
  myField: null  // error
  myField: []    // valid
  myField: ['a', 'b']  // valid
  myField: ['a', null, 'b'] // valid
  ```
* 필요에 따라 여러개의 Null, List modifier를 중첩할 수 있다.

#### Interfaces
* Interface는 Type이 포함해야하는 특정 필드들을 포함하는 추상 타입이다.
* Example 20
  ```GraphQL
  interface Character {
    id: ID!
    name: String!
    friends: [Character]
    appearsIn: [Episode]!
  }
  ```
* Character interface와 같은 Arguments와 return Type을 가진 필드만이 interface를 이용하여 사용할 수 있다.
* Example 21
  ```GraphQL
  type Human implements Character {
    id: ID!
    name: String!
    friends: [Character]
    appearsIn: [Episode]!
    starships: [Starship]
    totalCredits: Int
  }

  type Droid implements Character {
    id: ID!
    name: String!
    friends: [Character]
    appearsIn: [Episode]!
    primaryFunction: String
  }
  ```
* interface에 존재하는 필드가 아닌 경우 오류를 발생
* Example 22
  ```GraphQL
  query HeroForEpisode($ep: Episode!) {
    hero(episode: $ep) {
      name
      ***primaryFunction***
    }
  }

  # Variable
  {
    "ep": "JEDI"
  }
  ```
  ```JSON
  {
    "errors": [
      {
        "message": "Cannot query field \"primaryFunction\" on type \"Character\". Did you mean to use an inline fragment on \"Droid\"?",
        "locations": [
          {
            "line": 4,
            "column": 5
          }
        ]
      }
    ]
  }
  ```

#### Union Types
* Interface와 유사하지만 타입 간에 공통 필드를 특정하지 않는다.
  ```GraphQL
  union SearchResult = Human | Droid | Starship
  ```
* SearchResult 타입을 반환 할때마다, Human, Droid, Starship을 얻을 수 있다.
* 유니온 타입의 멤버는 구체저인 객체 타입이어야 한다.
  * 인터페이스나 유니온 타입에서 다른 유니온 타입을 사용할 수 없다.

#### Input Types
* 열거형 타입이나 문자열과 같은 스칼라 값을 인자로 필드에 전달하는 방법 뿐만 아니라 좀더 복잡한 객체도 쉽게 전달할 수 있다.
  * Mutations 보다 유용
  * Mutation은 생성될 전체 객체를 전달하지만 type 대신 input을 사용하여 입력 객체 타입을 사용한다
* Example 23
  ```GraphQL
  input ReviewInput {
    stars: Int!
    commentary: String
  }
  ```
* Example 24
  ```GraphQL
  mutation CreateReviewForEpisode($ep: Episode!, $review: ReviewInput!) {
    createReview(episode: $ep, review: $review) {
      stars
      commentary
    }
  }

  # Variable
  {
    "ep": "JEDI",
    "review": {
      "stars": 5,
      "commentary": "This is a great movie!"
    }
  }
  ```
  ```JSON
  {
    "data": {
      "createReview": {
        "stars": 5,
        "commentary": "This is a great movie!"
      }
    }
  }
  ```
*** 
# GraphQL in Action (Book) Ref site
* [Example Codes](az.dev/gia)
* [RUN example code (GraphQL)](ax.dev/swapif-graphql) -> 스타워즈 API(GraphQL로 감싼 REST API)
* [Run GraphQL with github-api](az.dev/github-api)
***
## GraphQL in Action (Book Study)
### GraphQL 경험해보기

#### 1. GraphQL 소개
##### 1.1 GraphQL 이란?
* GraphQL 의 Graph는 현실 세계의 데이터를 표현하는 가장 적합한 방법이 그래프라는 사실에서 착안했다.
* GraphQL 언어는 선언적이며 유연하고 효율적으로 설계됐다.
  * 데이터 API를 사용하는 클라이언트 개발자는 서버에 저장된 데이터 구조나 데이터 관계를 이해할 필요가 없다.
* 백엔드에는 GraphQL 기반의 런타임이 필요하다.
  * 런타임은 API를 통해 제공될 데이터의 구조를 관리한다
  * GraphQL에서는 Schema라고 부른다.
* GraphQL의 API 요청 과정
  1. API 사용자는 GrpahQL 언어를 사용해서 필요한 데이터를 정확하게 요구하기 위한 텍스트 구성
  2. 클라이언트는 이 텍스트 요청을 전송채널 (ex HTTPS)을 통해 API 서비스에 전달
  3. GrpahQL 의 런타임 계층이 이 텍스트 요청을 받아서 백엔드에 있는 다른 서비스들과 커뮤니케이션하고 그 결과들을 모아서 적합한 데이터를 생성
  4. 만들어진 데이터를 JSON 과 같은 형식으로 API 사용자에게 반환
* GraphQL 에서 말하는 API는 데이터를 읽고 수정하기 위한 API로 이것을 데이터 API라고 한다.
* GraphQL은 데이터 읽기 뿐만 아니라 수정도 할 수 있다.
  * GraphQL을 사용해서 데이터를 읽을 때는 쿼리를 사용하고 수정할 때는 변경(Mutation)을 사용
  * Query : Read
  * Mutation : Read-Write
  * Subscription : 실시간으로 데이터를 모니터링하는 요청 타입
* GraphQL 서비스는 원하는 프로그래밍 언어를 사용해서 구현할 수 있으며, 개념적으로는 구조(Structure)와 행동(Behavior)로 나뉜다.
  * Structure : 강력한 타입의 스키마를 사용해서 정의한다. 
    * GraphQL의 스키마는 GraphQL API가 처리할 수 있는 모든 작업을 정리해 둔 것
    * Client는 이 스키마를 통해 어떤 질문을 서비스에게 할 수 있는지 알 수 있다.
    * 스키마는 기본적으로 타입을 가진 필드들을 그래프로 나타낸 것이며 이 그래프는 데이터 서비스를 통해 읽고 수정할 수 있는 모든 데이터 객체를 보여준다.
  * Behavior : 함수를 통해 구현하며 GraphQL에서는 이 함수를 Resolver function이라고 부른다.
    * resolver function은 GraphQL의 뒤에서 움직이는 대부분의 처리 로직으로 강력한 기능성과 유연성을 지니고 있다.
    * GraphQL Schema의 각 필드는 리졸버 함수와 연동되며, 리졸버 함수에는 각 필드가 어떤 값을 가져와야 하는지 정의
    * 리졸버함수는 어디서, 어떻게 데이터를 가져올지를 지시한다.
* GraphQL 커뮤니티에서는 서버측 언어를 클라이언트측 쿼리 언어처럼 GraphQL 스키마 객체 생성용으로 표준화 하고 있다.
  * 이 서버측 언어를 Schema Language라고 하며 줄여서 SDL(Schema deefinition languagae) 또는 IDL (interface definition language)라고 한다.
* **GraphQL을 왜 사용하나?**
  * GraphQL은 관리와 확장이 용이한 API 기능을 광범위한 표준과 구조를 사용해서 구현할 수 있다.
  * GraphQL에선 데이터 API의 기능 범위를 반드시 문서(Schema)를 통해 공개할 것을 규칙으로 정하고 있다.
  * 유일한 독창성으로 문서화가 API 서비스를 만드는 과정의 일부라는 점
    * 이렇게 만들어진 문서는 항상 최신 정보를 담으며, 특정 사용 용도에 대한 정보를 항상 기록하게 된다.
  * Schema가 노드 구조의 그래프로 구성돼 있어서 다양한 경로를 만들 수 있으며, 이는 스키마에게 유연성을 부여한다.
    * GrpahQL의 가장 큰 장점으로, Frontend와 Backend개발자가 서로 별도의 논의 없이도 각자 개발을 진행할 수 있도록 만든다.
  * Client-Server 구조에서 GraphQL을 선택하는 가장 중요한 기술적 이유는 효율성
* REST API 와의 차이점
  * REST API가 가진 가장 큰 문제점은 클라이언트가 여러 개의 데이터 API 엔드포인트와 커뮤니케이션 한다는 것
  * REST API는 클라이언트가 데이터를 받기 위해서 네트워크를 여러번 왕복해야한다.
  * REST API에는 클라이언트용 요청 언어가 없다.
  * 클라이언트가 필요로 하는 데이터와 상관없이 REST API 서비스는 항상 모든 필드를 반환
    * GraphQL에서는 이런 문제를 정보의 과다 추출(overfetching)이라고 부른다.
  * REST API의 다른 큰 문제는 버전 관리이다.
    * 여러버전을 제공해야한다면, 엔드포인트도 여러 개 준비해야 한다는 의미
  * REST API는 결국 여러 개의 REST 엔드포인트가 얽혀 있는 상태가 되며, 성능상의 문제로 임시 엔드포인트까지 추가되기도 한다.
  * REST API의 응답을 캐싱하는 편이 GraphQL API의 응답을 캐싱하기보다 쉽다
  * 여러 REST 엔드포인트의 코드를 최적화 하는 것이 그래프QL의 단일 엔드포인트를 최적화 하는것보다 쉽다.
* GraphQL의 방식
  * 타입이 정해진 그래프 스키마
  * 선언적 언어
  * 단일 엔드포인트와 클라이언트 언어
  * 간단한 버전 관리
* GraphQL 단점
  * GraphQL API 큰 위협이 되는 것은 DDoS(Distributed Denial of Service attack)
  * GraphQL 서버는 아주 복잡한 쿼리에 의해 공격받을 수 있으며, 이 경우 서버의 모든 자원을 잡아먹는다.

#### 2. GraphQL API
##### 2.2 기초 GraphQL 언어
* GraphQL의 두가지 핵심 개념이라고 할 수 있는 요청과 필드에 대해 알아보자
* GraphQL 커뮤니케이션의 핵심은 요청 객체이다.
* GraphQL 요청은 문서(document)라고 불린다.
  * 문서는 쿼리, 변경, 구독 같은 작업 요청을 텍스트로 가지고 이다.
  * 이런 작업 외에도 기타 작업을 구성할 때 사용하는 조각도 텍스트로 저장된다.
* GraphQL 세가지 종류 작업
  * query : 읽기 전용으로 데이터를 추출
  * mutation : 데이터를 변경한 후 추출
  * subscription : 실시간으로 데이터 변경 내용을 받는다.
* GraphQL 작업에서 핵심이 되는 요소 중 하나가 필드
  * 객체의 필드를 선택하는 일
* Scalar Type Field
  * 기본 leaf 값(자식이 없는 마지막 노드)을 나타내며 4개의 스칼라 타입인 Int, String, Float, Boolean을 지원
  * 기본 내장된 ID 스칼라 값은 고유성을 표현하는데 사용
* GraphQL API는 내향성(introspective) 쿼리를 제공해서 API 스키마에 대한 정보를 공개한다
  * __type 또는 __schema라는 루트 필드로 시작 --> Meta-Field 라고 함
    * __schema field : type 이나 지시문 등 API 스키마 정보를 확인할 때 사용
    * __type field : 단일 타입에 대한 정보를 확인할 때 사용

#### 3.GraphQL 작업 수정 및 구성
* GraphQL의 필드는 함수와 같다.
  * 함수는 입력과 출력을 매핑하며, 입력은 여러 개의 인수를 통해 받는다.
* API 필드는 단일 레코드를 표현해야 하며, 레코드를 식별하기 위한 인숫값이 반드시 데이터베이스상에 존재하는 고유한 값이어야 한다.
* Node Interface : 시스템 상에 존재하는 모든 객체를 단일 레코드 필드로 관리
  * Relay Framework에 의해 많이 알려짐
  * 시스템 전체에서 고유성을 지니고 있는 전역 ID를 통해 데이터 그래프에 있는 어떤 데이터든 찾아 낼 수 있다.
  * 찾아 낸 후에는 inline-fragment을 사용해서 노드의 종류에 따라 확인하고 싶은 속성을 지정하면 된다.
  ```GraphQL
  # 예제 3-2
  query Nodeinfo {
    node(id: "A-GLOBALLY-UNIQUE-ID-HERE") {
      ... on USER {
        firstName
        lastName
        username
        email
      }
    }
  }

  ```
* Alias
  * API 서버가 다른 이름으로 필드를 반환하도록 지시
  * 별칭명: 필드명
* 경우에 따라선 단순히 응답의 필드명을 바꾼다고 문제가 해결되지 않음
  * 응답 데이터의 일부를 조건에 따라 포함하거나 제외해야 할 수도 있다.
  * 이 경우 GraphQL 지시문(directive)를 사용하면 유용하다.
* GraphQL 요청에서 지시문은 GraphQL 서버에 추가 정보를 제공하기 위한 한가지 방법
  * 실행 지시나 타입 유효성 검사 등의 정보를 GraphQL 문서로 제공
  * 조건에 따라서 전체 필드를 추가 또는 제외 시킬 수 있으며, 필드 뿐만 아니라 조각과 최상위 작업에도 사용가능
  * @로 시작하는 문자열을 사용해 작성
    * @include
      * if를 인수로 사용
      * 필드 또는 조각 뒤에서만 사용, 작업의 최상위 계층에선 사용 불가
        ```GraphQL
        fieldName @include(if: $someTest)
        ``` 
    * @skip
      * if를 인수로 사용
      * 필드 또는 조각 뒤에서만 사용, 작업의 최상위 계층에선 사용 불가
        ```GraphQL
        fieldName @skip(if: $someTest)
        ```  
    * @deprecated
      * reason를 인수로 사용
      * GraphQL 서비스 스키마를 정의할 때 필드 정의 또는 ENUM 값 뒤에 사용
* 변수는 GraphQL 문서에서 $기호로 시작
  * GraphQL 작업을 재사용 하거나 값이나 문자열의 하드코딩을 방지하기 위해 사용
* 변수를 사용하려면 타입을 먼저 정의해야한다.
* GraphQL Fragment
  * 언어의 조합 단위로 큰 GrapQL 작업을 작은 부분으로 나눌 수 있게 해준다
  * Fragment(조각)은 그래프QL을 작은 부품으로 나누어 재사용할 수 있게 해줌
  ```GraphQL
  ...Fragment Name
  ```
  * 위와 같이 ... 3개 마침표로 Fragment를 Spread 한다.
    * Fragment Spread는 일반 필드를 사용한 곳이면 어디든지 적용가능
    * Fragment Type이 해당 조각을 사용하고자 하는 객체의 타입과 일치할 때만 사용가능
    * 조각을 정의했다면 반드시 사용해야 하며 정의했지만 사용되지 않은 조각은 서버로 보낼 수 없다.
* interface and union inline fragment
  * inline fragment는 이름이 없는 조각으로 정의한 위치에 바로 인라인으로 전개할 수 있다.
  * inline fragment는 interface 나 union을 요청할 때 타입 조건으로 사용할 수 있다.
* interface와 union은 GraphQL의 추상(abstract) 타입
  * interface는 shared 필드의 리스트를 정의하고 Union은 사용할 수 있는 객체 타입의 리스트를 정의한다.
  * GraphQL의 스키마의 객체 타입은 인터페이스를 구현할 수 있으며, 객테 타입이 구현한 대항 인터페이스가 정의한 필드 리스트도 함께 구현된다.
  * 객체 타입을 유니온으로 정의하면 객체가 반환하는 것이 해당 유니온 중 하나라는 것을 보장한다.
* union type은 기본적으로 OR 로직과 같다.
* inline fragment를 사용하면 union 객체 타입 이나 interface를 구현한 객체 타입으로 부터 정보를 선별해서 취할 수 있다.
#### 4. GraphQL Schema 설계
***
### 잠깐!
* PostgreSQL : 관계형 데이터 베이스
  * 관계에 특화되어있음.
  * 왜? PostgreSQL인가? 
    * PostgreSQL은 확장성이 좋은 오픈 소스, 객체 기반 관계형 데이터베이스로 설치 및 사용하기가 쉬운 무료 데이터 베이스
    * 필요할 때 쉽게 사용할 수 있는 아주 편리한 고급 기능들을 제공한다.
    * Opensource 데이터베이스 중 가장 많이 사용되는 것 중 하나.
* MongoDB : 문서형 데이터베이스
  * 스키마를 가지지 않아서 동적 데이터를 다루기 좋다.
  * 가장 인기있는 오픈소스 문서형(NoSQL) 데이터베이스
***
* GraphQL 스키마에서 테이브블은 객체 타입으로 맵핑 되며, 테이블 컬럼은 객체 타입으 ㅣfield로 맵핑된다.
* API의 개체(GraphQL 타입으로 표현된 table)는 model이라는 용어를 사용
* 모델에 속한 객체의 속성을 가리킬 때는 필드를 사용

#### 이후 단원은 실습으로...
</br>
</br>
</br>

*** 
# Web APP API 개발을 위한 GraphQL Book Study
***
## Web APP API 개발을 위한 GraphQL
### 1. Welcome to GraphQL
* GraphQL은 API를 만들 때 사용할 수 있는 쿼리 언어
  * 쿼리에 대한 데이터를 받을 수 있는 런타임
* GraphQL 서버에서는 쿼리가 실행될 때마다 타입 시스템에 기초해 쿼리가 유효한지 검사
  * 타입 시스템은 언어가 실행될 때 데이터의 유형을 추론하고 검증
  * 프로그램 오류를 방지하고 안정성을 높이는 데 도움을 줌
  * 정적 타입 시스템과 동적 타입 시스템으로 구분
    * 정적 타입 시스템에서는 변수와 표현식의 타입이 컴파일 시간에 결정
    * 타입 오류를 컴파일러가 감지
    * 동적 타입 시스템에서는 타입 검사가 런타임에 이루어짐
* GraphQL 서비스를 만들려면 GraphQL 스키마에서 사용할 타입을 정의

* GraphQL 은 선언형(declarative) 데이터 패칭(fetching)언어
* GraphQL 서버 라이브러리는 다양한 언어로 만들어져 있음
* 
#### **GraphQL은 클라이언트와 서버 간의 통신 명세(스펙)**
#### **GraphQL 설계 원칙**
* 위계적(hierarchical)
  * 필드 안에 다른 필드가 중첩 될 수 있으며, 쿼리와 그에 대한 반환 데이터는 형태가 서로 같다.
* 제품 중심적
  * GraphQL은 클라이언트가 요구하는 데이터와 클라이언트가 지원하는 언어 및 런타임에 맞춰 동작
* 엄격한 타입 제한
  * GraphQL 서버는 GraphQL 타입 시스템을 사용
  * 스키마의 데이터 포인트마다 특정 타입이 명시되며, 이를 기초로 유효성 검사를 받게됨
* 클라이언트 맞춤 쿼리
  * GraphQL 서버는 클라이언트 쪽에서 받아서 사용할 수 있는 데이터를 제공
* 인트로스펙티브(introspective)
  * GraphQL 언어를 사용해 GraphQL 서버가 사용하는 타입 시스템에 대한 쿼리를 작성 가능
</br></br>
* REST의 단점
  * overfetching
    * endpoint로 요청하여 응답 받은 정보가 사용하지 않을 불필요한 데이터를 담고 있는 경우
  * underfetching
    * 하나의 endpoint로 필요한 모든 데이터 요청을 처리하지 못하는 경우

#### **GraphQL Client**
* GraphQL Client 의 목적
  * 개발자가 빠르게 작업할 수 있는 환경을 만들어주고 애플리케이션의 성능과 효율성을 끌어올리는 것
  * 네트워크 요청, 데이터 캐싱, 사용자 화면에 데이터 주입 등의 일을 담당
  * Relay 와 Apollo를 많이 사용
### 3. GraphQL query
* GraphQL 과 SQL
  * SQL은 데이터베이스로 쿼리를 보냄
  * GraphQL은 API로 보냄
  * SQL 데이터는 데이터 테이블 안에 저장
  * GraphQL은 데이터의 저장 환경을 가리지 않음
    * 단일 데이터베이스, 여러 개의 데이터 베이스, 파일 시스템, REST API, Websocket, 다른 GraphQL API
* GraphQL은 명세에 따라 표준화 되어 있음
  * 프로그래밍 언어에 종속 되어 있지 않음
  * 프로젝트에서 사용하는 언어에 상관없이 쿼리문은 항상 똑같은 구문을 사용한 문자열
* GraphQL API Tool
  * [GraphiQL](https://github.com/graphql/graphiql)
    * 브라우저 안에서 사용하는 IDE, 페이스북 제작
  * [GraphQL Playgrouond](https://www.apollographql.com/docs/apollo-server/v2/testing/graphql-playground/)
  * 공용 GraphQL API
    * [SWAPI](https://graphql.github.io/swapi-graphql)
    * [GitHub API](https://developer.github.com/v4/explorer)
    * [Yelp](https://www.yelp.com/developers/graphiql)
      * Yelp 개발자 계정이 있어야함
#### GraphQL Query
* 쿼리 작업으로 API에 데이터를 요청
* 쿼리안에는 GraphQL 서버에서 받고 싶은 데이터 작성
* 쿼리를 보낼 때는 요청 데이터를 필드로 작성
  * 서버에서 받아오는 JSON 응답 데이터의 필드와 일치
* 쿼리 는 여러개 추가 가능 하지만 작업은 한번에 한 쿼리만 가능
* Query는 GraphQL Type
  * ROOT Type
  * 타입 하나가 곧 하나의 작업을 수행
  * 작업이 곧 쿼리 문서의 루트를 의미
* GraphQL API에서 query에 사용할 수 있는 필드는 API 스키마에 정의
* 쿼리를 작성할 때는 필요한 필드를 중괄호로 감싼다.
  * 셀렉션 세트(selection set)라고 부름
* Selection Set는 서로 중첩이 가능
* 요청으로 받은 데이터는 JSON 포맷으로 되어 있다
  * 쿼리와 같은 형태
* JSON 필드명은 쿼리의 필드명과 동일
  * 응답 객체의 필드명을 다르게 받고 싶으면, 필드명에 별칭을 부여
  * Alias : FieldName

* GraphQL 쿼리 결과에 대한 필터링 작업을 하고 싶다면 쿼리 인자(query arguments)를 넘긴다.
  * 쿼리 필드와 관련 있는 키-값 쌍을 하나 이상 인자로 넣을 수 있음
##### Scalar & Object Type
* **GraphQL query에서 field는 scalar(스칼라) type 과 object(객체) type 둘 중 하나에 속하게 된다.**
  * Scalar Type : query selection set의 잎(leaves)이 되어 주는 타입
  * 다섯가지 스칼라 타입
    * Int, Float, String, Boolean, 고유식별자(ID)
  * Object Type : 스키라에 정의한 필드를 그룹으로 묶어 둔 것
    * 응답으로 반환되어야 할 JSON 객체의 형태를 하고 있다.
  * Example
    ```GraphQL
    query trailsAccessdByJazzCat {
      Lift(id:"jazz-cat") {
        capacity # Scalar Type ==> 인원수를 정수 형태로 반환
        trailAccess { # Object Type
          name
          difficulty
        }
      }
    }
    ```
##### Fragment
* Fragment는 셀렉션 세트의 일종이며, 여러번 재사용 할 수 있다.
* ```fragment``` 식별자를 사용하여 생성
* 특정 타입에 대한 셀렉션 세트 이므로, 어떤 타입에 대한 fragment인지 정의에 꼭 써야함
* 점 세개를 찍어서 사용
  ```GraphQL
  ...fragmentName
  ```
* 한차례의 수정으로 여러 쿼리의 셀렉션 세트를 한 번에 바꿀 수 있다는 것이 fragment의 장점

##### Union
* 타입 여러개를 한 번에 리스트에 담아 반환하고 싶다면 Union type 생성
* 두가지 타입을 하나의 집합으로 묶는 것
* inline fragment
  * 이름이 없는 fragment
  * Union type에서 여러 타입의 객체를 반환 할 때, 각각의 객체가 어떤 필드를 반환할 것인지 정할 때 사용
##### Interface
* interface는 필드 하나로 객체 타입을 여러개 반환할 때 사용
* 추상적인 타입이며, 유사한 객체 타입을 만들 때 구현해야하는 필드 리스트를 모아둔 것
* 인터페이스에 정의된 필드는 모두 넣어야 하고, 몇 가지 고유한 필드도 추가로 넣을 수 있다.
##### Mutation
* 데이터를 새로 쓰려면 Mutation 사용
* 데이터를 Mutation하는 방법은 쿼리를 작성하는 방법과 비슷하며, 이름을 붙여야한다.
* 객체 타입이나 스칼라 타입의 반환 값을 가지는 셀렉션 세트가 들어간다.
* 쿼리와 다른 점은 데이터를 뮤테이션하면 백엔드 데이터에 영향을 준다
* 쿼리에 있는 정적(static) 값을 변수로 대체하여 계속해서 바뀌는 동적인(dynamic) 값을 넣기 위해 mutation에 변수를 사용할 수 있다.
* GraphQL은 변수명 앞에 $ 문자가 붙는다
##### Subscription
* 서버에서 푸시 중인 업데이트의 내역을 실시간으로 클라이언트에서 받아야 할 때가 있다.
* 데이터 서브스크립션을 하면 GraphQL API를 사용해 실시간 데이터 변경 내용을 받을 수 있다.
  * ex 페이스북 실시간 좋아요
* Mutation과 query 처럼 Subscription도 루트 타입이 존재
* 클라이언트에서 받을 수 있는 데이터 변경 내용은 subscription 타입아래 필드로 정의되어 API 스키마에 들어간다.
* Subscription이 시작되면 웹소켓으로 리프트 상태 변경 내용을 받는다.
  * Subscription 요청이 서버로 전송되면 받는 쪽에서 데이터의 변경 사항 여부를 듣기 시작할 뿐이다. -> 포착하고 싶다면 일단 다른 곳에서 데이터를 바꿔야 한다.
##### introspection
* 현재 API 스키마의 세부 사항에 관한 쿼리를 작성할 수 있다.
  * GraphQL API (ex. GraphQL Playground Interface)에서 GraphQL 문서를 확인할 수 있다.
* 주어진 API 스키마를 통해 어떤 데이터를 반환받을 수 있는지 조사할 수 있다.
  ```GraphQL
  query {
    ___schema { #를 사용하면 API에서 사용할 수 있는 타입을 확인가능
      tyeps { # 루트타입, 커스텀 타입, 심지어 스칼라타입까지 나온다.
        name
        description
      }
    }
  }
  ```
  ```GraphQL
  query liftDetails {
    __type(name:"Lift") { # 특정 타입에 대한 세부사항만 보고 싶다면 __type 쿼리에 타입명을 인자 작성
      name
      fields {
        name
        description
        type {
          name
        }
      }
    }
  }
  ```
* introspection 쿼리문은 GraphQL 쿼리 언어의 규칙을 따른다.
  * fragment를 사용하여 쿼리문 안의 중복되는 부분을 없앨 수 있다.
##### 추상 구문 쿼리
* 쿼리 문서는 문자열로 이루어져 있음
* GraphQL API로 쿼리를 보낼 때, 문자열은 abstract syntax tree로 파싱 되어 명령 실행 전에 유효성 검사를 거친다.
  * 줄여서 AST는 계층 구조를 지닌 객체로 쿼리를 표현하는데 사용
* AST는 객체이며 GraphQL 쿼리에 관한 부가 정보 필드가 중첩된 구조로 들어간다.
* 첫번째로 거치는 작업은 문자열을 더 작은 여러 개의 조각으로 쪼개어 분석하는 작업
  * 키워드, 인자, 심지어 괄호나 콜론까지 하나하나 독립적인 토큰으로 분해
  * 어휘화(lexing) 또는 어휘 분석(lexical analysis)라고 부른다.
* 쿼리가 어휘화 과정을 거친 뒤 AST로 가공
  * AST로 만들면 수정과 유효성 검사가 더 쉬워진다.
* 쿼리는 처음에 GraphQL '문서'로 시작
* 쿼리에는 최소한 정의(definition)가 하나 이상 들어가며, 여러 개의 정의가 리스트로 들어 있을 수도 있다.
* 정의의 두 타입
  * OperationDefinition
    * mutation, query, subscription 작업 타입만 들어간다.
    * 각 작업 정의에는 OperationType과 SelectionSet가 들어간다.
    * 각 작업 정의에는 중괄호 안에 SelectionSet가 들어간다.
      * 필드가 인자를 받아 쿼리 작업이 진행되는 실제 필드
      * SelectionSet는 중첩이 가능
  * FragmentDefinition
* GraphQL은 AST를 횡단하며 GraphQL 언어와 현재 스키마와 비교해 유효성 검사를 진행
* 오류가 없다면 작업이 실행
### 4. Schema
* 데이터 타입의 집합을 스키마라고 한다.
* 스키마 우선주의(Schema First)
  * 모든 팀원이 애플리케이션 안의 데이터 타입을 같은 선상에서 이해할 수 있다.
* GraphQL은 스키마 정의를 위해 SDL(Schema Definition Language, 스키마 정의 언어)를 지원
* 쿼리 언어처럼 SDL 역시 애플리케이션에서 사용 중인 프로그래밍 언어나 프레임 워크와는 상관없이, 사용법이 항상 동일
* GraphQL 스키마 문서는 애플리케이션에서 사용할 타입을 정의해 둔 텍스트 문서
  * 클라이언트와 서버에서 GraphQL 요청에 대한 유효성 검사를 할 때 사용
#### Type
* Type이 GraphQL의 핵심 단위
* GraphQL에서 타입은 커스텀 객체이며 이를 보고 애플리케이션의 핵심 기능을 알 수 있다.
* Type에는 필드(Field)가 들어간다.
  * 필드는 각 객체의 데이터와 관련이 있다.
* 각각의 필드는 특정 종류의 데이터를 반환
  * 정수나 문자열, 커스텀 객체 타입이나 여러 개의 타입을 리스트로 묶어 반환
* 스키마에는 타입 정의를 모아 둔다.
  * 자바스크립트 파일에 문자열로 작성하거나, 따로 텍스트 파일로 작성
  * 텍스트 파일의 주요 확장자는 .graphql
  ```graphql
  type Photo { #필드 정의
    id: ID!  #Key 값 고유 식별자가 들어감, 문자열 타입, 고유한 값인지 유효성 검사를 함
    name: String! #스칼라 타입 !는 null 값을 허용 하지 않음
    url: String! #스칼라 타입
    description: String #스칼라 타입
  }
  ```
#### Scalar Type
* GraphQL의 내장 스칼라 타입(Int, Float, String, Boolean, ID)외에 직접 만들고 싶은 경우가 발생
* 스칼라 타입은 객체 타입이 아니므로 필드를 가지지 않는다.
* 하지만 커스텀 스칼라 타입의 유효성 검사 방식을 지정할 수 있다.
  ```GraphQL
  scalar DateTime

  type Photo {
    id: ID!
    name: String!
    url: String!
    description: String
    created: DateTime!
  }
  ```
* 커스텀 스칼라 타입인 DateTime
  * 각 사진이 언제 created 되었는지 식별 가능해졌다.

* 커스텀 스칼라 타입을 사용하면 반환 문자열 값이 직렬화와 유효성 검사 과정을 거쳤는지, 공식 날짜 및 시간으로 형식이 맞춰졌는지 검사 할 수 있다.

#### Enumeration Type
* 열거 타입(Enumeration type)은 스칼라 타입에 속하며, 필드에서 반환하는 문자열 값을 세트로 미리 지정해 둘 수 있다.
* 미리 정의해둔 세트에 속하는 값만 필드에서 반환하도록 만들고 싶다면 열거(enum)타입을 사용하면 된다.
  ```graphql
  enum PhotoCategory {
    SELFIE
    PORTRAIT
    ACTION
    LANDSCAPE
    GRAPHIC
  }

  type Photo {
    id :ID!
    name: String!
    url: String!
    description: String
    created: DateTime!
    category: PhotoCategory!
  }
  ```
### 4.2 연결과 리스트
* GraphQL 스키마 필드에서는 GraphQL 타입이 담긴 리스트 반환도 가능
* 리스트는 GraphQL 타입을 대괄호로 감싸서 만든다.
  
|리스트 선언|정의|
|:---|:---|
|[Int]|리스트 안에 담긴 정수 값은 null이 될 수 있다.|
|[Int!]|리스트 안에 담긴 정수 값은 null이 될 수 없다.|
|[Int]!|리스트 안에 정수 값은 null이 될 수 있으나, 리스트 자체는 null이 될 수 없다.|
|[Int!]!| 리스트 안의 정수 값은 null이 될 수 없고, 리스트 자체도 null이 될 수 없다.|

#### 일대일 연결
* 하나의 객체타입이 또 다른 객체 타입과 서로 연결
```graphql
type User {
  githubLogin: ID!
  name: String
  avatar: String
}

type Photo {
  id: ID!
  name: String!
  url: String!
  description: String
  created: DateTime!
  category: PhotoCategory!
  postedBy: User!
}
```
#### 일대다 연결
* GraphQL 서비스는 최대한 방향성이 없도록 유지하는 편이 좋다
  * 방향이 없다면 아무 노드에서 그래프 횡단을 시작할 수 있으므로, 클라이언트 쪽에서 쿼리를 최대한 자유롭게 만들 수 있기 때문이다.
* 흔히 루트 타입에 일대다 관계를 정의
* 사진이나 사용자 정보에 대한 쿼리 요청을 만들려면 Query 루트 타입에 필드를 정의해야 한다
```GraphQL
type Query {
  totalPhotos: Int!
  allPhotos: [Photo!]!
  totalUsers: Int!
  allUsers: [User!]!
}

schema {
  query: Query
}
```
* Query에 타입 정의를 추가하면 API에서 사용할 쿼리를 정의한 것과 같다.
* 또한 Query 타입을 schema에 필드로 추가하면 GraphQL API에서 쿼리를 사용할 수 있다.
* 이제 다음과 같이 사용자와 사진에 대한 쿼리를 보낼 수 있다.
```GraphQL
query {
  totalPhotos
  allPhotos {
    name
    url
  }
}
```

#### 다대다 연결
* 다대다 연결을 만들려면 User와 Photo 타입 양쪽 모두에 리스트 타입 필드를 추가
  ```GraphQL
  type User {
    ...
    inPhotos: [Photo!]!
  }

  type Photo {
    ...
    taggedUsers: [User!]!
  }
  ```
* 하나의 다대다 관계는 두 개의 일대다 관계로 이루어져 있음.
#### 통과 타입
* 다대다 연결을 만들 경우, 관계 자체에 대한 정보를 담고 싶을 때
* through type을 사용
  * 엣지를 커스텀 객체 타입으로 정의

#### 여러 타입을 담는 리스트
##### Union Type
* GraphQL UnionType을 사용하면 여러 타입 가운데 하나를 반환
* 유니온 타입에 타입을 원하는 만큼 결합 할 수 있다.
```graphql
union = StudyGroup | WorkOut | class | Meal | Meeting | FreeTime
```
##### Interface Type
* interface 역시 한 필드 안에 타입을 여러 개 넣을 때 사용
* 객체 타입 용도로 만드는 추상 타입이며, 스키마 코드의 구조를 조직할 때 아주 좋은 방법
* 인터페이스를 통해 특정 필드가 무조건 특정 타입에 포함되도록 만들 수 있으며, 이들 필드는 쿼리에서 사용할 수 있습니다.
* 인터페이스는 타입이 반환하는 데이터 타입에 구애받지 않고 사용할 수 있습니다.
```graphql
scalar DataTime

interface AgendaItem {
  name: String!
  start: DateTime!
  end: DateTime!
}

type StudyGroup implements AgendaItem {
  name: String!
  start: DateTime!
  end: DateTime!
  participants: [User!]!
  topic: String!
}

type Workout implements AgendaItem {
  name: String!
  start: DateTime!
  end: DateTime!
  reps: Int!
}

type Query {
  agenda: [AgendaItem!]!
}
```
* 인터페이스로 타입을 만들려면 인터페이스 안에 정의된 필드가 무조건 들어가야 한다.
  * StudyGroup과 WorkOut 타입은 AgendaItem 인터페이스를 기반으로 만들기 때문에 name, start, end 필드가 들어가야 한다.
  * agenda 쿼리는 AgendaItem 타입 리스트를 리턴
* 타입에는 인터페이스에 정의된 필드 외에 다른 필드도 넣을 수 있다.
* 유니온 타입과 인터페이스 둘다 타입을 여럿 수용하는 필드를 만들 때 사용 
  * 일반적으로 객체에 따라 필드가 완전히 달라져야 한다면 Union type
  * 특정필드가 반드시 들어가야 한다면 Interface type

##### Arguments
* GraphQL 에는 인자를 추가할 수 있다.
* 인자를 사용하면 데이터를 전달할 수 있기 때문에 GraphQL 요청 결과 값이 바뀔 수도 있다.
```graphql
type Query {
  ...
  User(githubLogin: ID!): User!
  Photo(id: ID!): Photo!
}
```
* 데이터 페이징
  * GraphQL 쿼리에 인자를 전달해 반환 데이터의 양을 조절하는 과정
  * 옵션 인자를 추가하여 사용
    * first 인자 : 데이터 페이지 한 장 당 들어가는 레코드 수를 지정
    * start 인자 : 첫번 째 레코드가 시작되는 인덱스
* 정렬
  * 데이터리스트가 반환되는 쿼리를 작성할 때는 리스트의 정렬 방식을 지정
    ```graphql
    enum SortDirection {
      ASCENDING
      DESCENDING
    }

    enum SortablePhotoField {
      name
      description
      category
      created
    }

    Query {
      allPhotos (
        sort: SortDirection = DESCENDING
        sortBy: SortablePhotoField = created
      ): [Photo!]!
    }
    ```
  * allPhotos 쿼리에 sort와 sortBy 인자를 추가
##### Mutation
* 뮤테이션은 반드시 스키마 안에 정의해 두어야 한다.
* 쿼리를 정의할 때처럼 커스텀 타입으로 정의한 다음에 스키마에 추가
  * 스키마 안에서 쿼리와 뮤테이션 작성법은 차이가 없다
  * 애플리케이션 상태를 바꿀 액션이나 이벤트가 있을 때만 뮤테이션을 작성
* 뮤테이션은 애플리케이션의 동사 역할을 해야한다
  * 사용자가 GraphQl 서비스를 가지고 할 수 있는 일을 정의
```graphql
type Mutation {
  postPhoto(
    name: String!
    description: String
    category: PhotoCategory = PORTRAIT
  ): Photo!
}

schema {
  query: Query
  mutation: Mutation
}
```
##### Input Type
* input type을 사용하면 인자 관리를 조금 더 체계적으로 할 수 있다.
* GraphQL 객체 타입과 비슷하나, 인풋 타입은 인자에서만 사용
* 인풋 객체는 JOSN 객체 안에 'input' 키 값으로 묶여 뮤테이션의 쿼리 변수와 함께 전송된다.
* 쿼리 변수의 형식은 JSON
* 인풋 타입을 사용하면 GraphQL 스키마를 깔끔하게 작성하고 유지할 수 있다.
* 인풋 타입은 모든 필드에서 인자로 사용할 수 있다.
* 애플리케이션 데이터 페이징 및 필터링 기능을 개선할 때도 사용 가능
##### Retury Type
##### Subscription Type
