API Studying
===
- [API Studying](#api-studying)
    - [Reference Link](#reference-link)
  - [API (Application Programming Interface)](#api-application-programming-interface)
  - [REST API](#rest-api)
  - [RESTful API](#restful-api)

*****
### Reference Link
* [REST API official](https://restfulapi.net/)
* [REST API non-official](https://gmlwjd9405.github.io/2018/09/21/rest-and-restful.html)
*****

## API (Application Programming Interface)
* API란?
  * **다른 소프트웨어 시스템과 통신하기 위해 따라야 하는 규칙을 정의**
  * 클라이언트에서 요청을 보내면 서버에서 응답하는 방식
* 대표 API 종류
  * SOAP API
    * Simple Object Application Programming
    * 클라이언트와 서버는 XML을 사용하여 메시지 교환
    * 과거에 많이 사용하였으며 유연성이 떨어짐
  * RPC API
    * Remote Procedure Call
    * 클라이언트가 서버에서 함수나 프로시저를 완료하면 서버가 출력을 클라이언트로 다시 전송
  * Websocket API
    * JSON 객체를 사용하여 데이터를 전달하는 웹 API
    * 클라이언트와 서버간의 양방향 통신을 지원
    * 서버가 연결된 클라이언트에 콜백 메시지 전송할 수 있어 REST API 보다 효율적
  * REST API
    * 클라이언트가 서버에 요청을 **데이터**로 전송
    * 서버가 클라이언트의 입력을 사용하여 내부 함수를 시작하고 출력 데이터를 다시 클라이언트에 반환

## REST API
* REST란?
  * Representational State Transfer
  * **an architectural style for distributed hypermedia systems**
  * **HTTP URI(Uniform Resource Identifier)를 통해 자원(Resource)를 명시하고, HTTP Method(POST, GET, PUT, DELETE)를 통해 해당 자원에 대한 CRUD Operation을 적용하는 것**
    * CRUD Operation
      * Create : 생성(POST)
      * Read : 조회(GET)
      * Update : 수정(PUT)
      * Delete : 삭제(Delete)
      * <~~HEAD : header 정보 조회 (HEAD)~~>
  * REST 는 프로토콜이나 표준이 아닌 Architecture Style 중 하나
    * API 개발자는 다양한 방법으로 REST 구현 가능
  * **A Web API(or Web Service) conforming to the REST architectural style is called a REST API (or RESTful API).**
  
## RESTful API



