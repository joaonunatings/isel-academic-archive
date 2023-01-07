'use strict'

const request = require('supertest')
const fetch = require('node-fetch')

const config = require('../../borga-config')
const server = require('../../src/borga-server')

const baseUrl = `http://${config.es_spec.host}:${config.es_spec.port}`

jest.setTimeout(500000)

describe('Integration tests', () => {

    let app

    let groupId, gamesId = []

    beforeAll(async () => {
        app = await server(config.es_spec, config.guest, config.board_games_client_id)
    })

    it('Gets guest user', async () => {
        let res = await fetch(`${baseUrl}/users/_search?q=username:${config.guest.username}`)
        expect(res.status).toBe(200)
        let resJson = await res.json()
        expect(resJson.hits.hits[0]._source.token).toBe(config.guest.token)
    })

    it('Adds group', async () => {
        let res = await request(app)
            .post('/api/groups')
            .set('Authorization', `Bearer ${config.guest.token}`)
            .set('Accept', 'application/json')
            .send( { name: 'groupName', description: 'group description' } )
            .expect(201)
        let body = await res.body
        expect(body).toBeTruthy()
        expect(typeof body).toBe('string')
        groupId = body
    })

    it('Edits a group', async () => {
        let res = await request(app)
            .put(`/api/groups/${groupId}`)
            .set('Authorization', `Bearer ${config.guest.token}`)
            .set('Accept', 'application/json')
            .send( { name: 'newGroupName', description: 'new group description'} )
            .expect(200)

        expect(res.body).toBeTruthy()
        expect(res.body).toBe('updated')
    })

    it('Gets games', async () => {
        let res = await request(app)
            .get('/api/games')
            .set('Accept', 'application/json')
            .expect(200)

        expect(res.body).toBeTruthy()
        res.body.map( (game, index) => {
            gamesId[index] = game.gameId
        })
        expect(gamesId.length).toBe(res.body.length)
    })

    it('Adds games to group', async () => {
        for (let i = 0; i < gamesId.length; i++) {
            const res = await request(app)
                .post(`/api/groups/${groupId}/games`)
                .set('Authorization', `Bearer ${config.guest.token}`)
                .set('Accept', 'application/json')
                .send({ gameId: gamesId[i] })
                .expect(201)
            expect(res.body).toBe('updated')
        }
    })

    it('Gets group details', async () => {
        let res = await request(app)
            .get(`/api/groups/${groupId}`)
            .set('Authorization', `Bearer ${config.guest.token}`)
            .set('Accept', 'application/json')
            .expect(200)

        expect(res.body).toBeTruthy()
        expect(res.body.name).toBe('newGroupName')
        expect(res.body.description).toBe('new group description')
        expect(res.body.games.length).toBe(gamesId.length)
        res.body.games.map( (gameId, index) => {
            expect(gameId.id).toBe(gamesId[index])
        })
    })

    it('Deletes games from group', async () => {
        for (let i = 0; i < gamesId.length; i++) {
            let res = await request(app)
                .delete(`/api/groups/${groupId}/games/${gamesId[i]}`)
                .set('Authorization', `Bearer ${config.guest.token}`)
                .set('Accept', 'application/json')
                .expect(200)
            expect(res.body).toBe('updated')
        }

        // Get group details
        let res = await request(app)
            .get(`/api/groups/${groupId}`)
            .set('Authorization', `Bearer ${config.guest.token}`)
            .set('Accept', 'application/json')
            .expect(200)

        expect(res.body.games.length).toBe(0)
    })

    afterAll(async () => {
        // Deletes group
        await request(app)
            .delete(`/api/groups/${groupId}`)
            .set('Authorization', `Bearer ${config.guest.token}`)
            .set('Accept', 'application/json')
            .expect(200)
    })
})