GraphQL Study
===
- [GraphQL Study](#graphql-study)
  - [Reference Site](#reference-site)
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

***
## Reference Site
* [official Link](https://graphql.org/)
* [Official Link kr](https://graphql-kr.github.io/learn/)
***

## GraphQL Introduction
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

 ## Query & Mutation
 ### Fields
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
### Arguments
* REST와 같은 시스템에서는 요청에 쿼리 파라미터와 URL 세그먼트같은 단일 인자들만 전달 할 수 있지만, GraphQL에서는 모든 필드와 중첩된 객체가 인자를 가질 수 있으므로 여러번의 API fetch를 완벽하게 대체할 수 있다.
* **필드에 인자를 전달하면, 모든 클라이언트에서 개별적으로 처리하는 대신 서버에서 데이터 변환을 한 번만 구현할 수 있다.**

### Alias
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

### Fragments
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

#### Using variables inside fragments
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
### Operation Name
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

### Variables
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
#### Variables definitions
* 변수 정의는 위 쿼리에서 ```($episode: Episode)``` 부분
* 선언된 모든 변수는 스칼라, 열거형, input object type 이어야 한다.
* 변수 정의는 optional 이거나 required
  * 변수 타입 옆에 ! 가 없을 경우 Optional
  * 변수를 전달할 필드에 null이 아닌 인자가 요구된다면 required

#### Default variables
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

### Directives
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

### Mutation
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

#### Multiple fields in mutations
* 뮤테이션은 쿼리와 마찬가지로 여러 필드를 포함할 수 있지만,
* **쿼리 필드는 병렬로 실행되지만 뮤테이션 필드는 하나씩 차례대로 실행된다.**
* 즉, 하나의 요청에서 두개의 incrementCredits 뮤테이션을 보내면 첫번째는 두번째 요청 전에 완료되는 것이 보장

### Inline Fragments
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

#### Meta Fields
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

## Schemas & Types
### Type system
* Schemas가 필요한 이유
  * 서버에서 요청할 수 있는 데이터에 대한 정확한 표현을 갖는것이 좋기에 어떤 필드를 선택할 수 있는지, 어떤 종류의 객체를 반환할 수 있는지, 하위 객체에서 사용할 수 있는 필드는 무엇인지 정확한 표현을 하기 위해
* 모든 GraphQL 서비스는 해당 서비스에서 쿼리 가능한 데이터들을 완벽하게 설명할 수 있는 타입들을 정의하고, 쿼리가 들어오면 해당 스키마에 대해 유효성이 검사된 후 실행된다.

### Object types and fields
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

### Arguments
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

### The Query and Mutation Types
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

### Scalar Types
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
### Enumeration Types
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

### Lists and Non-Null
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

### Interfaces
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

### Union Types
* Interface와 유사하지만 타입 간에 공통 필드를 특정하지 않는다.
  ```GraphQL
  union SearchResult = Human | Droid | Starship
  ```
* SearchResult 타입을 반환 할때마다, Human, Droid, Starship을 얻을 수 있다.
* 유니온 타입의 멤버는 구체저인 객체 타입이어야 한다.
  * 인터페이스나 유니온 타입에서 다른 유니온 타입을 사용할 수 없다.

### Input Types
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

