Spring Boot + REST Programming
===
- [Spring Boot + REST Programming](#spring-boot--rest-programming)
    - [Environment](#environment)
- [RESTful API Annotation](#restful-api-annotation)
  - [설정 관련 Annotation](#설정-관련-annotation)
  - [RESTful API의 행위(Verb) Annotation](#restful-api의-행위verb-annotation)
  - [RESTful API의 표현(Representation) Annotation](#restful-api의-표현representation-annotation)
    - [JSON 데이터 형식의 전송 방식](#json-데이터-형식의-전송-방식)
    - [데이터 형식 주요 Annotation](#데이터-형식-주요-annotation)


*****
### Environment
* Ubuntu 20.04
* Intellij IDEA Ultimate
* Java 17
* Spring Framework 
* Spring Boot Starter Web
* [Reference Link](https://adjh54.tistory.com/151) 
*****

# RESTful API Annotation
## 설정 관련 Annotation
* **@SpringBootApplication**
  * 스프링 부트 애플리케이션의 시작점을 나타내는 어노테이션
* **@Controller**
  * HTTP 요청을 처리하는 컨트롤러 클래스를 정의하는 어노테이션
  * Spring MVC 뷰를 반환하는데 사용
* **@RestController**
  * RESTful 웹 서비스를 위한 컨트롤러를 정의하는 어노테이션
  * RESTful 웹 서비스의 JSON, XML 등의 응답을 반환하는 데 사용

## RESTful API의 행위(Verb) Annotation
|   **Annotation**    | **HTTP** | **Method 역할**                                                          | **비고(예시)**                                            |
| :-----------------: | :------- | :----------------------------------------------------------------------- | :-------------------------------------------------------- |
|   **@GetMapping**   | GET      | 클라이언트가 리소스를 조회할 때 사용                                     | @GetMapping(value="/users")                               |
|  **@PostMapping**   | POST     | 클라이언트가 리소스를 생성할 때 사용                                     | @PostMapping(value="/users")                              |
|   **@PutMapping**   | PUT      | 클라이언트가 리소스를 갱신할 때 사용                                     | @PutMapping(value="/users")                               |
|  **@PatchMapping**  | PATCH    | 클라이언트가 리소스 일부를 갱신할 때 사용                                | @PatchMapping(value="/users")                             |
| **@DeleteMapping**  | DELETE   | 클라이언트가 리소스를 삭제할 때 사용                                     | @DeleteMapping(value="/users")                            |
| **@RequestMapping** | ALL      | 요청 메서드(GET, POST, PUT, DELETE 등)와 URL 매핑을 함께 지정할 수 있다. | @RequestMapping(value="/users", method=RequestMethod.GET) |

## RESTful API의 표현(Representation) Annotation
### JSON 데이터 형식의 전송 방식
|         **구분**          |                     **GET 방식**                     |                **POST 방식**                 |
| :-----------------------: | :--------------------------------------------------: | :------------------------------------------: |
|      **전송 데이터**      | URL 끝에 파라미터를 붙여서 서버에 요청을 보내는 방식 | HTTP Body에 담아서 서버에 요청을 보내는 방식 |
| **전송 데이터 크기 제한** |              데이터의 양에 제한이 있음               |           데이터의 양에 제한 없음            |
|    **캐싱 가능 여부**     |                         가능                         |                    불가능                    |
|         **보안**          |               데이터 노출 가능성 있음                |           데이터 노출 가능성 낮음            |
|       **사용 예시**       |               검색어 전송, 페이지 요청               |        로그인, 회원가입, 게시글 작성         |

### 데이터 형식 주요 Annotation
| **Annotation**      | **설명**                                                   | **예시**                                                                                                                 |
| :------------------ | :--------------------------------------------------------- | :----------------------------------------------------------------------------------------------------------------------- |
| **@PathVariable**   | 'URL 경로의 일부'를 매개변수로 전달받는 어노테이션         | @GetMapping("/users/{id}")<br>public ResponseEntity getUserById(@PathVariable Long id) {}                                |
| **@RequestParam**   | 'HTTP 요청 파라미터'를 매개변수로 전달받는 어노테이션      | @GetMapping("/users")<br>public ResponseEntity<list><br>getAllUsers(@RequestParam("age") int age) {} </list>             |
| **@RequestBody**    | 'HTTP 요청의 '본문(body)'을 매개변수로 전달받는 어노테이션 | @PostMapping("/user")<br>public ResponseEntity createUser(@RequestBody User user) {}                                     |
| **@ResponseStatus** | 'HTTP 응답의 상태 코드를 지정하는 어노테이션               | @GetMapping("/users/{id}")<br>@ResponseStatus(HttpStatus.NOT_FOUND)<br>public void getUserById(@PathVariable Long id) {} |
