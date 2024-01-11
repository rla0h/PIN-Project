CC = gcc
CFLAGS = -Wall
LIBS = -lssl -lcrypto

all: server client

server: ssl_server.c
	$(CC) $(CFLAGS) -o server ssl_server.c $(LIBS)

client: ssl_client.c
	$(CC) $(CFLAGS) -o client ssl_client.c $(LIBS)

clean:
	rm -f server client
