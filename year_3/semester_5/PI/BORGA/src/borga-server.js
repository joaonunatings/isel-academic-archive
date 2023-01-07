'use strict'

module.exports = (es_spec, guest, board_games_client_id) => {

    console.log('> Initializing server...')

    const express = require('express')
    const app = express()
    const expressSession = require('express-session')
    app.use(expressSession({secret: "BorgaAPI FTW", cookie: { expires : new Date(Date.now() + 3600000*24) }}))

    const data_ext = require('./lib/databases/board-game-atlas/board-games-data')(board_games_client_id)

    const data_in = require('./lib/databases/elastic-search/borga-db')(es_spec, guest)

    const services = require('./lib/services/borga-services')(data_ext, data_in)

    const web_api = require('./lib/routes/borga-web-api')(services)
    const web_site = require('./website/borga-web-site')(services, guest.token)
    
    const users_site = require('./website/borga-users-web-site')(app, services)

    app.set('view engine', 'hbs')
    app.set('views', './src/website/views')
    app.use(express.static('./src/website/public'))
    
    app.use(express.json())
    app.use(express.urlencoded())

    app.use('/api', web_api)
    app.use('/users', users_site)

    app.use('/', web_site)

    app.use((req, resp) => {
        resp.status(404)
        resp.render('error',
            {err: {
                    code: resp.statusCode,
                    name: "NOT_FOUND",
                    info: "Possibly invalid URL"
                }})
    })

    console.log('> Server initialized.')

    return app
}