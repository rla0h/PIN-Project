OpenSSL Build & Compile 방법
===
* Openssl Version : 1.1.1 (study date : 24/01/10)


# READ Install.md file
To install OpenSSL, you will need:
* A make implementation
    ```bash
    $ sudo apt-get install build-essentail
    ```
* Perl 5 with core modules (NOTES.PERL을 읽어서 Perl 설치)
  * ubuntu는 기본적으로 perl 설치가 되어있음
  * perl module 설치
    ```bash
    $ apt-cache search Text::Template
    $ sudo apt-get install libtext-template-perl
    $ cpan -i Text::Template # require ROOT 설치될 모듈에 대한 모든 테스트를 실행 모두 OK 뜨면 성공
    ``` 
* ANSI C compiler
* a development environment in the form of development libraries and C header files
* a supported operating system

# Quick Start to install
```bash
sudo apt-get remove openssl (기존 openssl를 삭제)
cd $OPENSSL_DIRECTORY
./config
make
make test
make install
```

# Install, BUILD, COMPILE OpenSSL another Path
```bash
mkdir MY_BUILD_PATH
cd MY_BUILD_PATH
mkdir ssl
/PATH/TO/OPENSSL/SOURCE/config --prefix=MY_BUILD_PATH --openssldir=MY_BUILD_PATH/ssl

# cd /PATH/TO/OPENSSL/SOURCE/ => X
make
(already make --> make clean)
make test
make install

export LD_LIBRARY_PATH=MY_BUILD_PATH${LD_LIBRARY_PATH:+:$LD_LIBRARY_PATH}
```

# OpenSSL TCP/IP Example
* [OpenSSL CODE Analysis REF LINK](https://tribal1012.tistory.com/213)
* [OpenSSL TCP/IP in C CODE REF](http://pchero21.com/?p=603)

## Client
* [Code](./src/ssl_client.c)
## Server
* [Code](./src/ssl_server.c)
### 기존 예제 코드에서 변경한 점
* Change SSL/TLS version to TLS_v1.2
* Change Algorithm to ARIA
  * [Algorithm REF](https://www.openssl.org/docs/man1.1.1/man1/ciphers.html)
* Continue...
