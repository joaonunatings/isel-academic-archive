'use strict'

const utils = require('../../utils')

const errorList = require('../borga-error-config')
const errorHandler = require('../borga-error-handler')(errorList)
    
module.exports = (data_ext, config) => {

    // Get list of games
    async function getGames(query) {
        if (utils.emptyObj(query)) {
            Object.assign(query, {
                order_by: 'rank'
            })
        }

        if (!utils.checkAcceptedKeys(query, config.acceptedParams))
            throw errorHandler.buildError('INVALID_PARAM', `Query has invalid parameters`)

        if (!utils.checkAcceptedValues(query.order_by, config.acceptedOrderBys))
            throw errorHandler.buildError('INVALID_PARAM', `Invalid value for order_by`)

        if (!utils.checkNumber(query.limit))
            throw errorHandler.buildError('INVALID_PARAM', `Invalid value for limit`)

        if (!utils.checkNumber(query.skip))
            throw errorHandler.buildError('INVALID_PARAM', `Invalid value for skip`)

        return await data_ext.getGames(query)
    }

    async function getGameDetails(id) {
        return await data_ext.getGameDetails(id)
    }

    return {
        getGames,
        getGameDetails
    }
}