'use strict'

const errorHandler = require('../borga-error-handler')()

module.exports = (services) => {

    // Get list of games
    async function getGames(req, res) {
        try {
            const games = await services.getGames(req.query)
            res.json(games)
        } catch (e) {
            errorHandler.setHttpError(res, e)
        }
    }

    return {
        getGames
    }
}