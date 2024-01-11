/*
 *      serv.c
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
#include <unistd.h>
#include <stdlib.h>
#include <memory.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>

/*
 * openssl 관련 헤더 파일을 include 한다.
 */
#include <openssl/rsa.h> /* SSLeay stuff */
#include <openssl/crypto.h>
#include <openssl/x509.h>
#include <openssl/pem.h>
#include <openssl/ssl.h>
#include <openssl/err.h>

/* define HOME to be dir for key and cert files…. */
#define HOME "./"

/* Make these what you want for cert & key files */
#define CERTF HOME "server.crt"
#define KEYF HOME "server.key"

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
    int listen_sd;
    int sd;
    struct sockaddr_in sa_serv;
    struct sockaddr_in sa_cli;
    size_t client_len;

    /* SSL Context 및 관련 구조체를 선언한다. */
    SSL_CTX *ctx;
    SSL *ssl;
    X509 *client_cert;
    char *str;
    char buf[4096];
    SSL_METHOD *meth;

    /* SSL 관련 초기화 작업을 수행한다. */
    SSL_load_error_strings();
    SSLeay_add_ssl_algorithms();
    meth = TLSv1_2_server_method(); // 서버 메소드.

    ctx = SSL_CTX_new(meth); // 지정된 초기 값을 이용하여 SSL Context를 생성한다.

    if (!ctx)
    {
        ERR_print_errors_fp(stderr);
        exit(2);
    }

    if (SSL_CTX_set_cipher_list(ctx, "ARIA256-GCM-SHA384") != 1)
    {
        ERR_print_errors_fp(stderr);
        exit(3);
    }

    /* 사용하게 되는 인증서 파일을 설정한다. */
    if (SSL_CTX_use_certificate_file(ctx, CERTF, SSL_FILETYPE_PEM) <= 0)
    { // 인증서를 파일로 부터 로딩할때 사용함.
        ERR_print_errors_fp(stderr);
        exit(3);
    }

    /* 암호화 통신을 위해서 이용하는 개인 키를 설정한다. */
    if (SSL_CTX_use_PrivateKey_file(ctx, KEYF, SSL_FILETYPE_PEM) <= 0)
    {
        ERR_print_errors_fp(stderr);
        exit(4);
    }

    /* 개인 키가 사용 가능한 것인지 확인한다. */
    if (!SSL_CTX_check_private_key(ctx))
    {
        fprintf(stderr, "Private key does not match the certificate public key\n");
        exit(5);
    }

    /* Prepare TCP socket for receiving connections */
    listen_sd = socket(AF_INET, SOCK_STREAM, 0);
    CHK_ERR(listen_sd, "socket");

    memset(&sa_serv, "", sizeof(sa_serv));
    sa_serv.sin_family = AF_INET;
    sa_serv.sin_addr.s_addr = INADDR_ANY;
    sa_serv.sin_port = htons(1111); /* Server Port number */

    err = bind(listen_sd, (struct sockaddr *)&sa_serv, sizeof(sa_serv));
    CHK_ERR(err, "bind");

    /* Receive a TCP connection. */
    err = listen(listen_sd, 5);
    CHK_ERR(err, "listen");

    client_len = sizeof(sa_cli);
    sd = accept(listen_sd, (struct sockaddr *)&sa_cli, &client_len);
    CHK_ERR(sd, "accept");
    close(listen_sd);

    printf("Connection from %1x, port %x\n", sa_cli.sin_addr.s_addr, sa_cli.sin_port);

    /* TCP connection is ready. Do server side SSL. */
    ssl = SSL_new(ctx); // 설정된 Context를 이용하여 SSL 세션의 초기화 작업을 수행한다.
    CHK_NULL(ssl);
    SSL_set_fd(ssl, sd);
    err = SSL_accept(ssl); // SSL 세션을 통해 클라이언트의 접속을 대기한다.
    CHK_SSL(err);
    
    /* 
    *************************
            Option(opt)
    *************************
    */
    // /* Get the cipher – opt */
    // printf("SSL connection using %s\n", SSL_get_cipher(ssl));

    // /* 클라이언트의 인증서를 받음 – opt */
    // client_cert = SSL_get_peer_certificate(ssl);
    // if(client_cert != NULL) {
    //     printf("Client certificate:\n");

    //     str = X509_NAME_oneline(X509_get_subject_name(client_cert), 0, 0);
    //     CHK_NULL(str);
    //     printf("t subject: %s\n", str);
    //     OPENSSL_free(str);

    //     str = X509_NAME_oneline(X509_get_issuer_name(client_cert), 0, 0);
    //     CHK_NULL(str);
    //     printf("t issuer: %s\n", str);
    //     OPENSSL_free(str);

    //     /* We could do all sorts of certificate verification stuff here before deallocating the certificate. */
    //     X509_free(client_cert);
    // } else {
    //     printf("Client does not have certificate\n");
    // }

    /* SSL 세션을 통해서 클라이언트와 데이터를 송수신한다. */
    err = SSL_read(ssl, buf, sizeof(buf) - 1);
    CHK_SSL(err);
    buf[err] = "";
    printf("Got %d chars: %s", err, buf);

    err = SSL_write(ssl, "I hear you", strlen("I hear you."));
    CHK_SSL(err);
    printf("\n");
    /* 설정한 자원을 반환하고 종료한다. */
    close(sd);
    SSL_free(ssl);
    SSL_CTX_free(ctx);

    return (0);
}