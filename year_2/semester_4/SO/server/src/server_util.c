#include "server_util.h"
#include "util.h"

#include <uv.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

loop_data_t* loop_data_init() {
    loop_data_t* loop_data = new(loop_data_t, 1);
    return loop_data;
}

void loop_data_destroy(loop_data_t* loop_data) {
    free(loop_data);
}


void error(int err, const char * msg, bool exit_cond) {
    fprintf(stderr, "%s: %s\n", msg, uv_strerror(err));
    if (exit_cond)
        exit(1);
}

void get_ip4_name(struct sockaddr_in handle_addr, char* handle_addr_str) {
    uv_ip4_name(&handle_addr, handle_addr_str, INET_ADDRSTRLEN);
}

struct sockaddr_in get_ip4_addr(char* addr_str, int port) {
    struct sockaddr_in handle_addr;
    uv_ip4_addr(addr_str, port, &handle_addr);
    return handle_addr;
}

void handle_destroy(uv_handle_t* handle) {
    switch(handle->type) {
        case UV_TCP:
            tcp_handle_destroy((uv_tcp_t*)handle);
            break;
        case UV_UDP:
            udp_handle_destroy((uv_udp_t*)handle);
            break;
        default:
            break;
    }
}


uv_loop_t* loop_init() {
    uv_loop_t* loop = new(uv_loop_t, 1);
    uv_loop_init(loop);
    loop_data_t* loop_data = loop_data_init();
    uv_loop_set_data(loop, loop_data);
    return loop;
}

void loop_destroy(uv_loop_t* loop) {
    loop_data_t* loop_data = (loop_data_t*)uv_loop_get_data(loop);
    loop_data_destroy(loop_data);
    uv_loop_close(loop);
    free(loop);
}