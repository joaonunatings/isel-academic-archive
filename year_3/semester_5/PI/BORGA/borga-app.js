'use strict'

const config = require('./borga-config');

config.board_games_client_id = process.argv[2] || config.board_games_client_id
config.server_address.host = process.argv[3] || config.server_address.host
config.server_address.port = process.argv[4] || config.server_address.port

if (!config.board_games_client_id) {
    throw `No Board Games Atlas API Client ID provided\n${config.app_usage_str}`
}

const app = require('./src/borga-server')(config.es_spec, config.guest, config.board_games_client_id)

app.listen(config.server_address.port, () =>
    console.log(`> Server listening at ${config.server_address.host}:${config.server_address.port}`))