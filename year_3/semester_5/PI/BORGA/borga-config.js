'use strict'

module.exports = {

    server_address: {
        host: process.env.HOST || 'localhost',
        port: process.env.PORT || 8888,
    },

    board_games_client_id: process.env.ATLAS_CLIENT_ID,

    guest: {
        username: 'guest',
        password: 'guest_pass',
        token: 'e7ced50f-e977-4ffe-ad10-7f7fbbfc8d8b'
    },

    es_spec: {
        url: process.env.BONSAI_URL || 'http://localhost:9200',
        prefix: 'borga'
    },

    app_usage_str: 'Run app using: npm start <client_id> [server_host] [server_port]'
}