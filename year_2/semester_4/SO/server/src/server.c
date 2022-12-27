#include "server.h"
#include "server_util.h"
#include "util.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stddef.h>
#include <stdbool.h>

void alloc_cb(uv_handle_t* handle, size_t suggested_size, uv_buf_t* buf) {
    buf->base = (char*)malloc(suggested_size);
    buf->len = suggested_size;
}

void close_cb(uv_handle_t* handle) {
    puts("> Closing connection");
    handle_destroy(handle);
    // ? more information
}

int main() {
    puts("> Server ON");

    // Event Loop - i/o polling and callbacks scheduling
    uv_loop_t* loop = loop_init();

    // TCP Handler - represents TCP streams and server
    uv_tcp_t* tcp_server1 = tcp_server_init(loop, SRV_DEFAULT_ADDR_STR, 54321, str_to_lower);

    uv_tcp_t* tcp_server2 = tcp_server_init(loop, SRV_DEFAULT_ADDR_STR, 56789, str_to_upper);

    //UDP Handler
    uv_udp_t* udp_server = udp_server_init(loop, SRV_DEFAULT_ADDR_STR, 54345);

    // Run Event Loop (uv_loop_t)
    uv_run(loop, UV_RUN_DEFAULT);

    // Stop Event Loop after handles have closed and their execution halted
    loop_destroy(loop);

    // Free up resources
    tcp_handle_destroy(tcp_server1);
    tcp_handle_destroy(tcp_server2);
    udp_handle_destroy(udp_server);

    puts("> Server OFF");
    return 0;
}