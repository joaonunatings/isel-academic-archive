#include "server.h"
#include "util.h"
#include "server_util.h"

#include <uv.h>
#include <string.h>
#include <stdlib.h>

#define UDP_CLIENT_PORT 5555

uv_udp_t* udp_server_init(uv_loop_t* loop, char* addr_str, int port) {
    uv_udp_t* handle = udp_handle_init(loop, addr_str, port);
    
    struct sockaddr_in handle_addr = get_ip4_addr(addr_str, port);
    uv_udp_bind(handle, (const struct sockaddr*)&handle_addr, UV_UDP_REUSEADDR);
    uv_udp_recv_start(handle, alloc_cb, udp_recv_cb);
    
    return handle;
}

void udp_recv_cb(uv_udp_t *handle, ssize_t nread, const uv_buf_t *buf, const struct sockaddr *addr, unsigned flags) {
    // ? Different from teacher
    if (nread < 0) {
        error(nread, "> Error reading (UDP)", false);
        uv_close((uv_handle_t*)handle, close_cb);
        return; // ?
    }

    if (nread > 0) {
        buf->base[nread] = 0;

        char* msg = buf->base;

        int strlen = udp_process_response(msg);

        uv_buf_t wbuf = uv_buf_init(msg, strlen);

        struct sockaddr_in send_addr = get_ip4_addr(SRV_DEFAULT_ADDR_STR, UDP_CLIENT_PORT);

        uv_udp_send_t* send_req = new(uv_udp_send_t, 1);
        uv_udp_send(send_req, handle, &wbuf, 1, (const struct sockaddr*)&send_addr, udp_send_cb);
    }
}

int udp_process_response(char* buf) {
    if (*buf != 'Q') {
        char* err_msg = "Bad request";
        strcpy(buf, err_msg);
        return strlen(err_msg);
    }

    char res_buf[UDP_RES_LEN];
    res_buf[0] = 'A';
    strncpy(&res_buf[1], &buf[1], 7);
    int opt = buf[1] - '0' - 1;
    if(opt >= NUM_REQS) {
        char* err_msg = "Request not found";
        strcpy(buf, err_msg);
        return strlen(err_msg);
    }
    le_str_set(&res_buf[8], data[opt], 8);
    strncpy(buf, res_buf, UDP_RES_LEN);
    return UDP_RES_LEN;
}

void udp_send_cb(uv_udp_send_t* req, int status) {
    CHECK(status, "> Error sending udp response", false);
    free(req);
}