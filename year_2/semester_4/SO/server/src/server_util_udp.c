#include "server_util.h"
#include "server.h"
#include "util.h"

#include <uv.h>
#include <string.h>
#include <stdlib.h>

udp_data_t* udp_data_init(char* addr_str, int port) {
    udp_data_t* udp_data = new(udp_data_t, 1);
    udp_data->addr_str = addr_str;
    udp_data->port = port;
    return udp_data;
}

void udp_data_destroy(udp_data_t* udp_data) {
    free(udp_data);
}


uv_udp_t* udp_handle_init(uv_loop_t* loop, char* addr_str, int port) {
    uv_udp_t* handle = new(uv_udp_t, 1);
    uv_udp_init(loop, handle);
    udp_data_t* udp_data = udp_data_init(addr_str, port);
    uv_handle_set_data((uv_handle_t*)handle, udp_data);
    return handle;
}

void udp_handle_destroy(uv_udp_t* handle) {
    udp_data_t* udp_data = (udp_data_t*)uv_handle_get_data((uv_handle_t*)handle);
    udp_data_destroy(udp_data);
    free(handle);
}