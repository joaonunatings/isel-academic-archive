#include "server.h"
#include "server_util.h"
#include "util.h"

#include <uv.h>
#include <stdlib.h>
#include <string.h>

// ? need this
typedef struct {
    uv_write_t req;
    uv_buf_t buf;
} write_req_t;

static void inc_session(int port) {
    if(port == 54321) {
        data[TOTAL_SESSIONS_54321]++;
    } else if (port == 56789) {
        data[TOTAL_SESSIONS_56789]++;
    }
}

static void inc_nchars(int port, size_t nchars) {
    if (port == 54321) {
        data[TOTAL_CHARS_54321] += nchars;
    } else if (port == 56789) {
        data[TOTAL_CHARS_56789] += nchars;
    }
}

uv_tcp_t* tcp_server_init(uv_loop_t* loop, char* addr_str, int port, void (*func)(char* buf)) {
    uv_tcp_t* handle = tcp_handle_init(loop, addr_str, port, func);
    
    struct sockaddr_in handle_addr = get_ip4_addr(addr_str, port);
    uv_tcp_bind(handle, (const struct sockaddr*)&handle_addr, 0);
    uv_listen((uv_stream_t*)handle, SRV_MAX_BACKLOG, connection_cb);
 
    return handle;
}

void tcp_client_init(uv_stream_t* server, uv_loop_t* loop, void (*func)(char* buf)) {
    // Process TCP connecting using server Event Loop
    uv_tcp_t* client = tcp_handle_init(loop, NULL, 0, func);

    tcp_data_t* client_data = (tcp_data_t*)uv_handle_get_data((uv_handle_t*)client);
    tcp_data_t* server_data = (tcp_data_t*)uv_handle_get_data((uv_handle_t*)server);

    // Accepts incoming connection
    int res = uv_accept(server, (uv_stream_t *)client);
    if (res == 0) {
        inc_session(server_data->port);
        struct sockaddr_in client_addr = get_tcp_peername(client);
        char cli_addr_str[INET_ADDRSTRLEN];
        get_ip4_name(client_addr, cli_addr_str);
        client_data->addr_str = (char*)cli_addr_str;
        client_data->port = ntohs(client_addr.sin_port);
        printf("> Connection info: %s:%d\n", cli_addr_str, client_data->port);
        uv_read_start((uv_stream_t*)client, alloc_cb, read_cb);
    } else {
        uv_close((uv_handle_t *)client, close_cb);
    }
}

void write_cb(uv_write_t* req, int status) {
    CHECK(status, "> Error on writing (TCP)", false);
    write_req_t* wreq = (write_req_t*)req;
    free(wreq->buf.base);
    free(wreq);
}

void read_cb(uv_stream_t* stream, ssize_t nread, const uv_buf_t* buf) {
    if (nread > 0) {
        tcp_data_t* client_data = (tcp_data_t*)uv_handle_get_data((uv_handle_t*)stream);

        struct sockaddr_in server_addr = get_tcp_sockname((uv_tcp_t*)stream);
        inc_nchars(htons(server_addr.sin_port), nread);

        // Add string termination
        buf->base[nread] = 0;
        
        if (strncmp(buf->base, "quit", 4) == 0 && buf->base[4] < ' ') {
            uv_close((uv_handle_t *)stream, close_cb);
            return;
        }

        // Create a write request, 
        write_req_t* req = (write_req_t *)malloc(sizeof (write_req_t));
        // copies received buffer to buffer inside write_req_t
        req->buf = uv_buf_init(buf->base, nread);

        // Process response
        client_data->func(buf->base);

        // * add read/write information

        // Write to client
        uv_write((uv_write_t *)req, stream, &(req->buf), 1, write_cb);
        return;
    }

    if (nread < 0) {
        // End of file - no more data to be read
        if (nread == UV_EOF) {
            puts("> End of data");
        } else {
            CHECK(nread, "> Read error (TCP)", false);
        }
        uv_close((uv_handle_t *)stream, close_cb);
    }
}

void connection_cb(uv_stream_t* server, int status) {
    CHECK(status, "> Connection failed (TCP)", false);
    
    puts("> New connection");

    tcp_data_t* server_data = (tcp_data_t*)uv_handle_get_data((uv_handle_t*)server);

    tcp_client_init(server, server->loop, server_data->func);
}