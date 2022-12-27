#include "server_util.h"
#include "server.h"
#include "util.h"

#include <uv.h>
#include <string.h>
#include <stdlib.h>

struct sockaddr_in get_tcp_peername(uv_tcp_t* handle) {
    struct sockaddr_in handle_addr;
    int handle_addr_len = sizeof(struct sockaddr_in);
    uv_tcp_getpeername(handle, (struct sockaddr*)&handle_addr, &handle_addr_len);
    return handle_addr;
}

struct sockaddr_in get_tcp_sockname(uv_tcp_t* handle) {
    struct sockaddr_in handle_addr;
    int handle_addr_len = sizeof(struct sockaddr_in);
    uv_tcp_getsockname(handle, (struct sockaddr*)&handle_addr, &handle_addr_len);
    return handle_addr;
}


tcp_data_t* tcp_data_init(char* addr_str, int port, void (*func)(char* buf)) {
    tcp_data_t* tcp_data = new(tcp_data_t, 1);
    tcp_data->addr_str = addr_str;
    tcp_data->port = port;
    tcp_data->func = func;
    return tcp_data;
}

void tcp_data_destroy(tcp_data_t* tcp_data) {
    free(tcp_data);
}

uv_tcp_t* tcp_handle_init(uv_loop_t* loop, char* addr_str, int port, void (*func)(char* buf)) {
    uv_tcp_t* handle = new(uv_tcp_t, 1);
    uv_tcp_init(loop, handle);
    tcp_data_t* tcp_data = tcp_data_init(addr_str, port, func);
    uv_handle_set_data((uv_handle_t*)handle, tcp_data);    
    return handle;
}

void tcp_handle_destroy(uv_tcp_t* handle) {
    tcp_data_t* tcp_data = (tcp_data_t*)uv_handle_get_data((uv_handle_t*)handle);
    tcp_data_destroy(tcp_data);
    free(handle);
}