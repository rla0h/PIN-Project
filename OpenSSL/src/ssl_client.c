/*
 *      cli.c
 *
 *      Copyright 2008 Kim Sung-tae <pchero21@gmail.com>
 *
 *      This program is free software; you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation; either version 2 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program; if not, write to the Free Software
 *      Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 *      MA 02110-1301, USA.
 */

#include <stdio.h>
#include <memory.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>

#include <openssl/crypto.h>
#include <openssl/x509.h>
#include <openssl/pem.h>
#include <openssl/ssl.h>
#include <openssl/err.h>

#define CHK_NULL(x)  \
    if ((x) == NULL) \
        exit(1);
#define CHK_ERR(err, s) \
    if ((err) == -1)    \
    {                   \
        perror(s);      \
        exit(1);        \
    }
#define CHK_SSL(err)                 \
    if ((err) == -1)                 \
    {                                \
        ERR_print_errors_fp(stderr); \
        exit(2);                     \
    }

int main(void)
{
    int err;
    int sd;
    struct sockaddr_in sa;

    /* SSL 관련 정보를 관리할 구조체를 선언한다. */
    SSL_CTX *ctx;
    SSL *ssl;
    X509 *server_cert;
    char *str;
    char buf[4096];
    SSL_METHOD *meth;

    /* 암호화 통신을 위한 초기화 작업을 수행한다. */
    SSL_load_error_strings();
    SSLeay_add_ssl_algorithms();
    meth = TLSv1_2_client_method();
    ctx = SSL_CTX_new(meth);
    CHK_NULL(ctx);

    if (SSL_CTX_set_cipher_list(ctx, "ARIA256-GCM-SHA384") != 1)
    {
        ERR_print_errors_fp(stderr);
        exit(3);
    }
    /* 사용하게 되는 인증서 파일을 설정한다. – opt*/
    if (SSL_CTX_use_certificate_file(ctx, "./rootCA.crt", SSL_FILETYPE_PEM) <= 0)
    { // 인증서를 파일로 부터 로딩할때 사용함.
        ERR_print_errors_fp(stderr);
        exit(3);
    }

    /* 암호화 통신을 위해서 이용하는 개인 키를 설정한다. – opt */
    if (SSL_CTX_use_PrivateKey_file(ctx, "./rootCA.key", SSL_FILETYPE_PEM) <= 0)
    {
        ERR_print_errors_fp(stderr);
        exit(4);
    }

    /* 개인 키가 사용 가능한 것인지 확인한다. – opt */
    if (!SSL_CTX_check_private_key(ctx))
    {
        fprintf(stderr, "Private key does not match the certificate public keyn");
        exit(5);
    }

    // CHK_SSL(err);

    /* Create a socket and connect to server using normal socket calls. */
    sd = socket(AF_INET, SOCK_STREAM, 0);
    CHK_ERR(sd, "socket");

    memset(&sa, "", sizeof(sa));
    sa.sin_family = AF_INET;
    sa.sin_addr.s_addr = inet_addr("127.0.0.1"); // Server IP Address
    sa.sin_port = htons(1111);                   // Server Port Number

    err = connect(sd, (struct sockaddr *)&sa, sizeof(sa));
    CHK_ERR(err, "connect");

    /* Now we have TCP connection. Start SSL negotiation. */
    ssl = SSL_new(ctx); // 세션을 위한 자원을 할당받는다.
    CHK_NULL(ssl);

    SSL_set_fd(ssl, sd);
    err = SSL_connect(ssl); // 기존의 connect() 함수 대신 사용하여 서버로 접속한다.
    CHK_NULL(err);

    /* Following two steps are optional and not required for data exchange to be successful. */

    /* Get the Cipher – opt */
    printf("SSL connection using %s %s\n", SSL_get_cipher(ssl), SSL_get_version(ssl));   
    
    /* 
    *************************
            Option(opt)
    *************************
    */
     
    /* Get server’s certificate (note: beware of dynamic allocation) – opt */
    /* 서버의 인증서를 받는다. */
    // server_cert = SSL_get_peer_certificate(ssl);
    // CHK_NULL(server_cert);
    // printf("Server certificate:\n");

    // /* 인증서의 이름을 출력한다. */
    // str = X509_NAME_oneline(X509_get_subject_name(server_cert), 0, 0);
    // CHK_NULL(str);
    // printf("t subject: %s\n", str);
    // OPENSSL_free(str);

    // /* 인증서의 issuer를 출력한다. */
    // str = X509_NAME_oneline(X509_get_issuer_name(server_cert), 0, 0);
    // CHK_NULL(str);
    // printf("t issuer: %s\n", str);
    // OPENSSL_free(str);

    // /* We could do all sorts of certificate verification stuff here before deallocating the certificate */
    // X509_free(server_cert);

    /* 서버와 데이터를 송수신 한다. */
    err = SSL_write(ssl, "Hello World!", strlen("Hello World!"));
    CHK_SSL(err);

    err = SSL_read(ssl, buf, sizeof(buf) - 1);
    CHK_SSL(err);
    buf[err] = "";
    printf("Got %d chars: %s", err, buf);
    SSL_shutdown(ssl); // SSL로 연결된 접속을 해지한다.
    printf("\n");
    /* 할당된 자원을 반환하고 종료한다. */
    close(sd);
    SSL_free(ssl);
    SSL_CTX_free(ctx);

    return 0;
}