'use strict'

const express = require('express')

const swaggerUi = require('swagger-ui-express')

const YAML = require('yamljs')
const swaggerSpec = YAML.load('./docs/borga-api-spec.yaml')

module.exports = (services) => {

    const games_route = require('./games.route')(services)
    const groups_route = require('./groups.route')(services)
    const users_route = require('./users.route')(services)

    // CRUD Configuration
    const router = express.Router()

    router.use('/docs', swaggerUi.serve)
    router.get('/docs', swaggerUi.setup(swaggerSpec))

    router.use(express.json())

    // Resource: /games
    router.get('/games', games_route.getGames)

    // Resource: /groups
    router.get('/groups', groups_route.getGroups)
    router.post('/groups/', groups_route.createGroup)

    // Resource: /groups/<groupId>
    router.put('/groups/:groupId', groups_route.editGroup)
    router.delete('/groups/:groupId', groups_route.deleteGroup)
    router.get('/groups/:groupId', groups_route.getGroup)

    // Resource: /groups/:groupId/games
    router.post('/groups/:groupId/games', groups_route.addGameToGroup)

    // Resource: /groups/<groupId>/games/<gameId>
    router.delete('/groups/:groupId/games/:gameId', groups_route.removeGameFromGroup)

    // Resource: /users
    router.post('/users', users_route.createUser)

    return router
}