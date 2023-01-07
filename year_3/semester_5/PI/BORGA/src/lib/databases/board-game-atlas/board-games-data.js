'use strict'

const fetch = require('node-fetch')

const utils = require('../../../utils')
utils.board_games = require('./board-games-data-utils')

const errorList = require('./board-games-data-errors')
const errorHandler = require('../../borga-error-handler')(errorList)

const baseUrl = 'https://api.boardgameatlas.com/api'
const searchUrl = `${baseUrl}/search`
const gameUrl = `${baseUrl}/game`
const mechanicsUrl = `${gameUrl}/mechanics`
const categoriesUrl = `${gameUrl}/categories`

const queryUri = (queryUrl, queryString) => `${queryUrl}?${queryString}`

module.exports = (client_id) => {

    init()

    async function getGames(query, gameBuilder = utils.board_games.buildGame) {
        try {
            utils.board_games.addQueryEntry(query, {client_id: client_id})
            const uri = queryUri(searchUrl, utils.queryStringify(query))

            const res = await fetch(uri)
            errorHandler.processResponse(res.status, 200, 'Fail at getting games from board games')

            return utils.board_games.jsonToGames(await res.json(), gameBuilder)
        } catch (err) {
            throw errorHandler.buildError(err.name, err.message)
        }
    }

    async function getGameDetails(id) {
        try {
            const query = utils.board_games.addQueryEntry({}, {ids: id, client_id: client_id})
            const uri = queryUri(searchUrl, utils.queryStringify(query))

            const res = await fetch(uri)
            errorHandler.processResponse(res.status, 200, 'Fail at getting game details from board games')

            const game = utils.board_games.jsonToGames(await res.json(), utils.board_games.buildGameDetails)[0]
            await getMechanicsName(game)
            await getCategoriesName(game)
            return game
        } catch (err) {
            throw errorHandler.buildError(err.name, err.message)
        }
    }

    async function getMechanicsName(game) {
        const query = { client_id: client_id }
        const uri = queryUri(mechanicsUrl, utils.queryStringify(query))

        const res = await fetch(uri)
        errorHandler.processResponse(res.status, 200, 'Fail at getting game mechanics from board games')

        game.mechanics = utils.board_games.IdsToNames((await res.json()).mechanics, game.mechanics)
        return game
    }

    async function getCategoriesName(game) {
        const query = { client_id: client_id }
        const uri = queryUri(categoriesUrl, utils.queryStringify(query))

        const res = await fetch(uri)
        errorHandler.processResponse(res.status, 200, 'Fail at getting game categories from board games')

        game.categories = utils.board_games.IdsToNames((await res.json()).categories, game.categories)
        return game
    }

    async function init() {
        console.log('> Initializing games data module...')
        let res
        try {
            const query = { client_id: client_id }
            res = await fetch(queryUri(baseUrl, utils.queryStringify(query)))
        } catch (err) { throw new Error(err) }
        if (res.error) throw new Error(res.error.message.split('Error: ', 2)[1])
        console.log('> Games data module initialized.')
    }

    return {
        getGames,
        getGameDetails
    }
}