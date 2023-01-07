'use strict'

const errorHandler = require('../borga-error-handler')()

module.exports = (services) => {

    // List all groups
    async function getGroups(req, res) {
        try {
            let token = getUserToken(req)
            const groupArray = await services.getGroups(token)
            res.json(groupArray)
        } catch (e) {
            errorHandler.setHttpError(res, e)
        }
    }

    // Create group providing its name and description
    async function createGroup(req, res) {
        try {
            let token = getUserToken(req)
            const groupRes = await services.createGroup(req.body, token)
            res.status(201)
            res.json(groupRes)
        } catch (e) {
            errorHandler.setHttpError(res, e)
        }
    }

    // Get the details of a group, with its name, description and names of the included games
    async function getGroup(req, res) {
        try {
            let token = getUserToken(req)
            const response = await services.getGroup(req.params.groupId, token)
            res.json(response)
        } catch (e) {
            errorHandler.setHttpError(res, e)
        }
    }

    // Edit group by changing its name and/or description
    async function editGroup(req, res) {
        try {
            let token = getUserToken(req)
            const response = await services.editGroup(req.params.groupId, req.body, token)
            res.json(response)
        } catch (e) {
            errorHandler.setHttpError(res, e)
        }
    }

    // Delete a group
    async function deleteGroup(req, res) {
        try {
            let token = getUserToken(req)
            const response = await services.deleteGroup(req.params.groupId, token)
            res.json(response)
        } catch (e) {
            errorHandler.setHttpError(res, e)
        }
    }

    // Add a game to a group
    async function addGameToGroup(req, res) {
        try {
            let token = getUserToken(req)
            const addedGame = await services.addGameToGroup(req.params.groupId, req.body.gameId, token)
            res.status(201)
            res.json(addedGame)
        } catch (e) {
            errorHandler.setHttpError(res, e)
        }
    }

    // Remove a game from a group
    async function removeGameFromGroup(req, res) {
        try {
            let token = getUserToken(req)
            const removedGame = await services.removeGameFromGroup(req.params.groupId, req.params.gameId, token)
            res.json(removedGame)
        } catch (e) {
            errorHandler.setHttpError(res, e)
        }
    }

    function getUserToken(req) {
        const auth = req.header('Authorization')
        if (auth) {
            const authData = auth.trim();
            if (authData.substr(0, 6).toLowerCase() === 'bearer') {
                return authData.replace(/^bearer\s+/i, '')
            }
        }
        return null
    }

    return {
        createGroup,
        editGroup,
        getGroups,
        deleteGroup,
        getGroup,
        addGameToGroup,
        removeGameFromGroup
    }
}

