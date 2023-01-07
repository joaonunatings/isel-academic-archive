'use strict'

const errorHandler = require('../borga-error-handler')()

module.exports = function (services) {

    // Create new user
    async function createUser(req, res) {
        try {
            const user = await services.createUser(req.body.username, req.body.password)
            res.status(201)
            res.json(user)
        } catch (e) {
            errorHandler.setHttpError(res, e)
        }
    }

    return {
        createUser
    }
}
