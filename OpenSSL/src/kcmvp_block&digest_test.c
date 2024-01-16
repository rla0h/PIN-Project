#include <openssl/evp.h>
#include <stdio.h>
#include <string.h>
#include "../include/kcmvp/KDN_LIB.h"

void handleErrors()
{
    printf("openssl error\n");
    exit(1);
}

void digest_message(const unsigned char *message, size_t message_len,
                    unsigned char **digest, unsigned int *digest_len)
{
    EVP_MD_CTX *mdctx;

    if ((mdctx = EVP_MD_CTX_new()) == NULL)
        handleErrors();

    if (1 != EVP_DigestInit_ex(mdctx, EVP_sha256(), NULL))
        handleErrors();

    if (1 != EVP_DigestUpdate(mdctx, message, message_len))
        handleErrors();

    if ((*digest = (unsigned char *)OPENSSL_malloc(EVP_MD_size(EVP_sha256()))) ==
        NULL)
        handleErrors();

    if (1 != EVP_DigestFinal_ex(mdctx, *digest, digest_len))
        handleErrors();

    EVP_MD_CTX_free(mdctx);
}

int main()
{
    const uint8_t *msg = "Hello world";

    unsigned char *digest = NULL;
    int d_len = 0;
    digest_message(msg, strlen(msg), &digest, &d_len);

    for (int i = 0; i < d_len; ++i)
    {
        printf("%02X ", digest[i]);
    }
    printf("\n");

    // KDN_Digest Example
    uint8_t kdigest[128] = {
        0,
    };
    int kd_len = 32;
    const uint8_t *empty = "Hello World";
    KDN_Digest(kdigest, empty, strlen(empty), KDN_SHA_256);
    for (int i = 0; i < kd_len; ++i)
    {
        printf("%02X ", kdigest[i]);
    }
    printf("\n");


    // KDN_Block_Cyper Example

    /* A 256 bit key */
    unsigned char *key = (unsigned char *)"01234567890123456789012345678901";

    /* A 128 bit IV */
    unsigned char *iv = (unsigned char *)"0123456789012345";
    size_t iv_len = 16;
    unsigned char *plaintext =
        (unsigned char *)"The quick brown fox jumps over the lazy dog";
    unsigned char kplaintext[128];
    unsigned char *additional =
        (unsigned char *)"The five boxing wizards jump quickly.";
    unsigned char kciphertext[128];
    unsigned char kdecryptedtest[128];
    unsigned char ktag[16];
    int kdecryptedtext_len, kcipthertext_len, kplaintext_len, plaintext_len;
    
    KDN_BC_Encrypt(kciphertext, &kcipthertext_len, plaintext, strlen((char *)plaintext),
                   key, 32, KDN_BC_Algo_ARIA_GCM, iv, iv_len, additional, strlen((char *)additional), ktag, 16);

    printf("KDN Ciphertext is : \n");
    BIO_dump_fp(stdout, (const char *)kciphertext, kcipthertext_len);
    
    // printf("Tag is:\n");
    // BIO_dump_fp(stdout, (const char *)ktag, 16);

    KDN_BC_Decrypt(kplaintext, &kplaintext_len, kciphertext, strlen((char *) kciphertext),
                   key, 32, KDN_BC_Algo_ARIA_GCM, iv, iv_len, additional, strlen((char *)additional), ktag, 16);
    
    printf("KDN Ciphertext is : \n");
    BIO_dump_fp(stdout, (const char *)kplaintext, kplaintext_len);
    
    return 0;
}