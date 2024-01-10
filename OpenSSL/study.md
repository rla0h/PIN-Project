# OpenSSL 및 보안
- [OpenSSL 및 보안](#openssl-및-보안)
  - [Security Overview](#security-overview)
    - [Computer Security](#computer-security)
    - [보안 3요소](#보안-3요소)
    - [보안에 대한 공격](#보안에-대한-공격)
  - [기초 암호화 기법](#기초-암호화-기법)
    - [평문과 암호문](#평문과-암호문)
    - [대칭키 암호화](#대칭키-암호화)
    - [비대칭키 암호화](#비대칭키-암호화)
    - [블록암호 알고리즘](#블록암호-알고리즘)
    - [블록 암호 모드](#블록-암호-모드)
  - [OpenSSL Library](#openssl-library)
    - [OpenSSL](#openssl)
    - [OpenSSL Library 구조](#openssl-library-구조)
  - [대칭키 암호 알고리즘](#대칭키-암호-알고리즘)
    - [대칭키 암호화](#대칭키-암호화-1)
  - [DES 알고리즘](#des-알고리즘)
    - [DES 알고리즘 개요](#des-알고리즘-개요)
    - [DES 알고리즘 구조](#des-알고리즘-구조)
    - [DES 알고리즘 보안 정도](#des-알고리즘-보안-정도)
    - [DES 알고리즘의 변형](#des-알고리즘의-변형)
    - [DES API 설명](#des-api-설명)
  - [AES 알고리즘](#aes-알고리즘)
    - [AES 알고리즘 개요](#aes-알고리즘-개요)
    - [AES 파라미터](#aes-파라미터)
    - [AES 실행 단계](#aes-실행-단계)
    - [AES API 설명](#aes-api-설명)
  - [ARIA 알고리즘](#aria-알고리즘)
    - [ARIA 알고리즘 개요](#aria-알고리즘-개요)
    - [ARIA 구조](#aria-구조)
    - [ARIA API 설명](#aria-api-설명)
  - [비대칭키 암호 알고리즘](#비대칭키-암호-알고리즘)
    - [비대칭키 암호 알고리즘 개요](#비대칭키-암호-알고리즘-개요)
  - [RSA 알고리즘](#rsa-알고리즘)
    - [RSA 개요](#rsa-개요)
    - [RSA 암호화 진행 단계](#rsa-암호화-진행-단계)
    - [RSA API 설명](#rsa-api-설명)

## Security Overview
### Computer Security
* 개인이나 기관이 사용하는 컴퓨터와 관련된 모든 것을 안전하게 보호하는 것
* 컴퓨터 안에 들어 있는 중요한 정보를 보호하는 행위를 말함
### 보안 3요소
* 기밀성 (Confidentiality)
  * 공동으로 사용되는 통신망을 통해 이동되거나 여러 사람이 이용하는 곳에 저장되어 있는 정보를 제3자가 볼 수 없도록 하는 것
* 무결성 (Integrity)
  * 생성된 정보를 제3자 뿐만 아니라 수신자도 변경할 수 없도록 하는 것
* 가용성 (Availability)
  * 시스템이 적절한 시점에 동작할 수 있도록 하며, 인가된 사용자에게 서비스가 잘 보장되도록 하는 것
### 보안에 대한 공격
* 공격의 종류
  * 도청
    * 불법적으로 정보를 얻는 것을 말함
    * 전화 내용을 엿듣거나 타인의 통신을 무전기로 듣는 행위
    ![Alt text](./img/image.png)
  * 위장
    * 다른 사람으로 가장하여 그 사람의 흉내를 내는 행위
    * 이를 막기 위해 인증이 요구됨
    ![Alt text](./img/image-1.png)
  * 변형
    * 전송되는 정보의 내용을 바꾸는 것을 말함
    * 이를 막기 위해 보안의 원칙 중 무결성이 요구됨
    ![Alt text](./imag/image-2.png)
  * 중단
    * 정보가 합법적인 수신인에게 도착하지 못하도록 하는 행위
    * DoS(Denial of Service) 공격이 이에 해당
    * 보안 원칙 중 가용성이 요구됨
    ![Alt text](./imag/image-3.png)
## 기초 암호화 기법
### 평문과 암호문
* 평문 : 암호화 되기전의 읽을 수 없는 문장
* 암호문 : 암호화를 통해서 읽을 수 없게 된 문장
* 암호화 : 평문을 암호문으로 바꾸는 과정
* 복호화 : 암호문을 평문으로 바꾸는 과정
* 비밀키 : 암호화 및 복호화 과정에서 사용되는 키
![Alt text](./imag/image-4.png)
### 대칭키 암호화
* 암호화 할 때의 키와 복호화 할 때의 키가 같은 경우를 말함
* 암/복호화 할 때 사용되는 키는 보통 비밀키 또는 대칭키라고 함
  * 정보를 주고받는 당사자 두 명을 제외한 타인을 알지 못하도록 비밀을 관리해야 한다는 의미
![Alt text](./imag/image-5.png)
### 비대칭키 암호화
* 암호화 할 때의 키와 복호화 할 때의 키가 서로 다른 경우를 말함
* 암호화 할 때 사용되는 키는 공개키
* 복호화 할 때 사용되는 키는 개인키
  * 공개키는 타인에게 공개되어도 상관없음
  * 개인키는 타인에게 공개 되지 않아야 함
![Alt text](./imag/image-6.png) 
### 블록암호 알고리즘
* 평문 블록 전체를 가지고 같은 크기의 암호문 블록 생성
* 보통 64bit, 또는 128bit 크기로 블록을 나뉘어서 사용
* 알고리즘 종류
  * AES
  * DES
  * ARIA
  * LEA 등
### 블록 암호 모드
* 각 암호화된 블록들간의 암호화 방식(블록단위의 암호화)
  * 블록들 간의 어떤 관계를 가지는가에 따라 여러 블록 암호 모드로 나뉨
* ECB(Electronic Code Book) 모드
  * 가장 단순한 모드
  * 블록간의 암호문이 독립적이며 추가적인 회로나 연산이 수행되지 않음
    * 암호문이 손상 되어도 다른 블록에 영향을 미치지 않음
![Alt text](./imag/image-7.png)
* CBC(Cipher-Block Chaining) 모드
  * ECB의 보안 결함을 위한 모드
    * 동일한 평문 블록에 대해 동일한 암호문 블록이 전송되는 문제를 해결하기 위함
  * 동일한 평문 블록이 반복되어도 상이한 암호 블록을 생성
  * 입력은 평문 블록과 선행 암호 블록의 XOR 연산 결과를 통해, 다음 블록을 얻게됨
    ![Alt text](./imag/image-8.png)
    ![Alt text](./imag/image-9.png)
## OpenSSL Library
### OpenSSL
* 보안 프로토콜인 TLS/SSL를 오픈 된 소스의 형태로 지원하는 라이브러리
  * Commends
    * 명령어를 통해 암호화, 키 생성 등을 수행
  * SSL Library
    * SSL 통신 프로토콜을 생성하고 메시지를 전달 하는 라이브러리
  * Crypto Library
    * 보안에 필요한 여러 알고리즘을 제공하는 라이브러리
### OpenSSL Library 구조
* Continue...

## 대칭키 암호 알고리즘
### 대칭키 암호화
* 대칭키 암호화 조건
  * 암호화에 사용되는 키와 복호화에 사용되는 키가 동일
  * 한 키로부터 다른 키를 쉽게 생성할 수 있는 구조
  * 두개의 조건 중 하나이상 만족하면 됨
  ![Alt text](./imag/image-10.png)
## DES 알고리즘
### DES 알고리즘 개요
* DES (Data Encryption Standard)
  * 대표적인 대칭키 블록 암호 algorithm
  * 현재 web browser 등 여러 program 에서 사용
  * 미 국립 표준국에서 컴퓨터간 통신에서의 데이터 보호를 위해 만들어짐
* DES 는 64bit 크기의 평문을 입력받아 동일한 크기의 암호문을 만듦
* 64bit 크기의 비밀키 입력 받음
* 비밀키 bit가 1번 부터 시작한다면 8의 배수에 해당하는 위치의 bit 들은 parity bit라고 생각하고 무시
  * 실제로 사용되는 비밀키의 크기는 56bit
### DES 알고리즘 구조
* 데이터를 처리하는 부분과 키를 처리하는 부분으로 이루어짐
* 데이터를 처리하는 부분의 구조
  * 초기 순열(Initial Permutation, IP)
    * 입력으로 들어온 64bit를 다른 위치로 옮기는 순열 연산 수행
  * 동일한 구조를 가진 16개의 round
    * 각 round는 round 키와 입력되는 문장을 섞음으로 혼돈과 확산을 높이는 것이 목적
  * 최종 순열
    * 초기 순열의 반대 작업
* 키를 처리하는 부분의 구조
  * 입력으로 받은 키를 각 round에서 사용할 round key로 변환하는 키 생성 부분 16개
### DES 알고리즘 보안 정도
* DES의 키는 56bit 크기이므로 2<sup>56</sup> 개의 키가 존재
* 브룻포스 공격 가정 키를 찾는데 평균 11만년 소요
  * 하나의 키에 대해서 암호화 혹은 복호화에 걸리는 시간 10<sup>-4</sup> 초 가정
  * 브룻포스 공격시 안전하다고 보장 X
    * 보완할 알고리즘 요구가 높아짐
### DES 알고리즘의 변형
* 이중 DES
  * DES 알고리즘 사용 두개의 키를 연속적으로 사용하는 것
  * 즉, 비밀키 K<sub>1</sub> 과 K<sub>2</sub> 사용 두 번 DES 암호화
  * C = E(E(P, K<sub>1</sub>),K<sub>2</sub>)
    ![Alt text](./imag/image-11.png)
* 삼중 DES
  * EDE(Encrypt-Decrypt-Encrypt) 방식으로 2개의 키 사용
  * C = E(D(E(P,K<sub>1</sub>),K<sub>2</sub>),K<sub>1</sub>)
    ![Alt text](./imag/image-12.png)
### DES API 설명
```c
DES_set_key(const_DES_cblock *key, DES_key_schedule *schedule)
```
* DES 암/복호화에 필요한 키 생성 API
* Parameter
  * [in] key: 암호화 키
  * [out] schedule : DES 키 스케줄러
```c
typedef unsigend char DES_cblock[8];
typedef unsigend char const_DES_cblock[8];

typedef struct DES_ks {
    union {
        DES_cblock cblock;
        DES_LONG deslong[2];
    } ks[16];
} DES_key_schedule;
```
```c
DES_encrypt1(DES_LONG *data, DES_key_schedule *ks, int enc);
```
  * DES 암/복호화 API(블록 암호 모드 사용x)
  * enc 파라미터로 암호화 인지 복호화 인지 구분
  * Parameter
    * [inout] data : 암호화인 경우 평문, 복호화인 경우 암호문
      * API에서 따로 출력 파라미터는 제공하지 않고 data 파라미터의 값이 변경됨
    * [in] ks : DES 키 스케줄러
    * [in] enc : 암/복호화 동작 구분
  ```c
  DES_cbc_encrypt(const unsigned char *input, unsigned char *output, long length, DES_key_schedule *schedule, DES_cblock *ivec, int enc);
  ``` 
  * DES 암/복호화 API(CBC 블록 암호 모드 사용)
  * enc 파라미터로 암호화 인지 복호화 인지 구분
  * Parameter
    * [in] input : 암호화인 경우 평문, 복호화인 경우 암호문
    * [out] output : 암호화인 경우 암호문, 복호화인 경우 복호문
    * [in] length : 입력 데이터 크기
    * [in] schedule : DES 키 스케줄러
    * [in] ivec : 초기화 벡터
    * [in] enc : 암/복호화 동작 구분
## AES 알고리즘
### AES 알고리즘 개요
* AES
  * 미국국립표준연구소에 의해 발표되고 유효화된 미국 표준 암호
  * DES를 대신하기 위해 개발된 대칭 블럭 암호방식
  * OpenSSL 이외에 20개 이상의 응용프로그램에서 사용
  * 30개 이상의 라이브러리를 통해서 제공
### AES 파라미터
  * 평문의 블럭사이즈는 128bit
  * 키 길이는 128bit, 192bit, 256bit
  * 키 길이에 따라 AES-128, AES-192, AES-256 으로 나뉨
  ![Alt text](./imag/image-14.png)
### AES 실행 단계
  * 바이트 치환 변환 (Substitue bytes)
    * 블럭의 바이트 대 바이트 치환 변환을 수행
  * 행 이동(Shift row)
    * 행렬의 행 이동을 통한 순열 과정
  * 열 혼합(Mix columns)
    * GF(2<sup>8</sup>)산술식을 사용한 치환 과정
  * 라운드 키 더하기(Add round key)
    * 현재 블럭과 확장된 키의 일부로 단순 비트 단위의 XOR 과정
### AES API 설명
```c
#define AES_MAXNR 14
struct aes_key_st {
    #ifdef AES_LONG
        unsigned long rd_key[4 * (AES_MAXNR + 1)];
    #else
        unsigned int rd_key[4 * (AES_MAXNR + 1)];
    #endif
        int rounds;
};
typedef struct aes_key_st AES_KEY;
```
```c
int AES_set_encrypt_key(const unsigned char *userKey, const int bit, AES_key *key);
```
  * AES 암호화에 필요한 키 생성 API
  * Parameter
    * [in] userkey : 암호화 키
    * [in] bits : 암호화 키 크기
    * [out] key : AES 라운드 키
```c
int AES_set_decrypt_key(const unsigned char *userKey, const int bits, AES_KEY *key);
```
  * AES 복호화에 필요한 키 생성 API
  * Parameter
    * [in] userkey : 암호화키
    * [in] bits : 암호화 키 크기
    * [out] key : AES 라운드 키
```c
void AES_encrypt(const unsigned char *in, unsigned char *out, const AES_KEY *key);
```
  * AES 암호화 API (블록 암호 모드x)
  * Parameter
    * [in] in : 평문
    * [out] out : 암호문
    * [in] key : AES 라운드 키
```c
void AES_decrypt(const unsigned char *in, unsigned char *out, const AES_KEY *key);
```
  * AES 복호화 API (블록 암호 모드x)
  * Parameter
    * [in] in : 암호문
    * [out] out : 평문
    * [in] key : AES 라운드 키
```c
void AES_cbc_encrypt(const unsigned char *in, unsigned char *out, size_t length, const AES_KEY *key, unsigned char *ivec, const int enc);
```
  * AES 암/복호화 API (CBC 블록 암호 모드 사용)
  * enc 파라미터로 암호화 인지 복호화 인지 구분
  * Parameter
    * [in] in : 암호화인 경우 평문, 복호화인 경우 암호문
    * [out] out : 암호화인 경우 암호문, 복호화인 경우 복호문
    * [in] length : 입력 데이터 크기
    * [in] key : AES 라운드 키
    * [in] ivec : 초기화 벡터
    * [in] enc : 암/복호화 동작 구분
## ARIA 알고리즘
### ARIA 알고리즘 개요
* 대한민국의 국가보안기술연구소에서 개발한 블록 암호 체계
* 학계(Academy), 연구소(Research Institue), 정부기관(Agency)이 공동으로 개발한 특징을 함축적으로 표현
* 대한민국의 국가 표준 암호 알고리즘
* 경량 환경 및 hardware 구현을 위해 최적화된 범용 블록 암호 알고리즘
* 암호의 난이도에 따라 128bit, 192bit, 256bit 길이의 암호키 선택
* 키의 길이에 따라서 라운드 함수가 12,14,16번 반복 실행
* 라운드 키는 암호키로부터 키 확장을 통해 생성
* 128bit 데이터 블록에 대해 암호화, 복호화 수행
### ARIA 구조
* ARIA의 암호화, 복호화는 Involution SPN 구조를 가짐
* ISPN(Involution Substitution-Permutation-Networks) 구조
  * ISPN 구조는 짝수 라운드와 홀수 라운드에서 치환계층이 서로 다름
    * 일반적인 SPN 구조의 암호화 알고리즘 순서
      * 평문 -> 치환계층 -> 확산계층 -> 암호문
    * 일반적인 SPN 구조의 복호화 알고리즘 순서
      * 암호문 -> Inverse 확산계층 -> Inverse 치환계층 -> 평문
  * 짝수 라운드의 치환계층의 Inverse가 홀수 라운드의 치환계층이 됨
  * 확산계층의 Matrix의 Inverse가 자기 자신이 됨
### ARIA API 설명
```c
int EncKeySetup(const Byte *mk, Byte *rk, int keyBits)
```
  * 암호화에 사용되는 ARI 라운드 키 생성
  * Parameter   
    * [in] mk : 암호화 키
    * [out] rk : ARIA 라운드 키
    * [in] keyBits : 암호화 키 크기
```c
int DecKeySetup(const Byte *mk, Byte *rk, int keyBits)
```
  * 복호화에 사용되는 ARIA 라운드 키 생성
  * Parameter
    * [in] mk : 암호화 키
    * [out] rk : ARIA 라운드 키
    * [in] keyBits : 암호화 키 크기
```c
void Crypt(const Byte *i, int Nr, const Byte *rk, Byte *o)
```
  * ARIA 암/복호화 API
  * Parameter
    * [in] i : 암호화인 경우 평문, 복호화인 경우 암호문
    * [in] Nr : 라운드 수
    * [in] rk : ARIA 라운드 키
    * [out] o : 암호화인 경우 암호문, 복호화인 경우 복호문
## 비대칭키 암호 알고리즘
### 비대칭키 암호 알고리즘 개요
* 암호화에 사용되는 공개키는 공개적으로 배포
* 복호화에 사용되는 개인키는 노출되지 않도록 함
* 공개키와 개인키는 암호문을 받는 사람이 만듦
* 자신에게 암호문을 보낼 사람에게 공개키 배포
* 노출의 위험성이 매우 적음
![Alt text](./imag/image-15.png)
* 비대칭키 알고리즘은 크게 2가지 문제의 알고리즘으로 나뉨
  * 소인수 분해, 이산대수
  ![Alt text](./imag/image-16.png)
## RSA 알고리즘
### RSA 개요
* 전자서명이 가능한 최초의 알고리즘
  * 인증을 요구하는 전자 상거래 등에 RSA의 광범위한 활용을 가능하게 함
* 안정성은 큰 숫자를 소인수 분해하는 것이 어려운 것에 기반
  * 만일 큰 수의 소인수 분해를 빠르게 할 수 있는 알고리즘이 개발되면 가치 떨어짐
* 모듈러 연산 사용
### RSA 암호화 진행 단계
* 키 생성
  * 공개키와 개인키 생성
  * 공개키는 상대방에게 전달
  * 개인키는 생성자가 보관하며 복호화에 사용
* 암호화
  * 평문에 공개키를 사용하여 암호문 생성
* 복호화
  * 암호문에 개인키를 사용하여 평문 복원
### RSA API 설명
```c
int RSA_public_encrypt(int flen, unsigned char *from, unsigned char *to, RSA *rsa, int padding);
```
  * RSA의 암호화 수행
  * Parameter
    * [in] flen : 입력 데이터 크기
    * [in] from : 평문
    * [out] to : 암호문
    * [in] rsa : rsa 키 쌍(공개키, 개인키 쌍)
      * 암호화를 수행하기 때문에 실제 사용되는 키는 공개키로 사용
    * [in] padding : padding 방식을 선택
      * RSA_PKCS1_PADDING
      * RSA_PKCS1_OAEP_PADDING
      * RSA_SSLV23_PADDING
      * RSA_NO_PADDING
```c
int RSA_private_decrypt(int flen, unsigned char *from, unsigned char *to, RSA *rsa, int padding);
```
  * RSA의 복호화 수행
  * Parameter
    * [in] flen : 입력 데이터 크기
    * [in] from : 암호문
    * [out] to : 복호문
    * [in] rsa : rsa 키 쌍(공개키, 개인키 쌍)
      * 복호화를 수행하기 때문에 실제 사용되는 키는 개인키 사용
    * [in] padding : padding 방식을 선택(암호화 할때 사용했던 방식 사용)
      * RSA_PKCS1_PADDING
      * RSA_PKCS1_OAEP_PADDING
      * RSA_SSLV23_PADDING
      * RSA_NO_PADDING
```c
int RSA_private_encrypt(int flen, unsigned char *from, unsigned char *to, RSA *rsa, int padding);
```
  * RSA의 인증(서명) 수행
  * Parameter
    * [in] flen : 입력 데이터 크기
    * [in] from : 평문
    * [out] to : 암호문(서명값)
    * [in] rsa : RSA 키 쌍(공개키, 개인키 쌍)
    * [in] padding : padding 방식을 선택
      * RSA_PKCS1_PADDING
      * RSA_NO_PADDING
```c
int RSA_public_decrypt(int flen, unsigned char *from, unsigned char *to, RSA *rsa, int padding);
```
  * RSA의 인증(서명)에 대한 검증 수행
  * Parameter
    * [in] flen : 입력 데이터 크기
    * [in] from : 암호문(서명값)
    * [out] to : 복호문
    * [in] rsa : RSA키 쌍(공개키, 개인키 쌍)
    * [in] padding : padding 방식을 선택
      * RSA_PKCS1_PADDING
      * RSA_NO_PADDING
