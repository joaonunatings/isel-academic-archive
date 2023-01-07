'use strict'

const utils = require('../../utils')

const errorList = require('../borga-error-config')
const errorHandler = require('../borga-error-handler')(errorList)

module.exports = (data_in) => {

    // Create new user
    async function createUser(username, password) {
        if (!utils.isType(password, 'string'))
            throw errorHandler.buildError('INVALID_PARAM', 'Invalid value for password')
        if (!utils.isType(username, 'string'))
            throw errorHandler.buildError('INVALID_PARAM', 'Username is not a string')

        return data_in.createUser(username, password)
    }

    // Validate user credentials
    async function validateCredentials(username, password) {
        if(!username || !password)
            throw errorHandler.buildError('INVALID_PARAM', 'Invalid values for password or username')

        const user = await data_in.getUserByUsername(username)
        if(user.password === password) {
            return {
                userId: user.userId,
                username: user.username
            }
        }
        throw errorHandler.buildError('UNAUTHENTICATED', 'Invalid username or password')
    }

    return {
        createUser,
        validateCredentials
    }
}