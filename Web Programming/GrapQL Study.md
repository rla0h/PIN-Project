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