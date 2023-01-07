'use strict'

const utils = require('../../utils')

const errorList = require('../borga-error-config')
const errorHandler = require('../borga-error-handler')(errorList)

module.exports = (data_ext, data_in) => {

    // List all groups
    async function getGroups(token) {
        if (!utils.isValidUuid(token))
            throw errorHandler.buildError('UNAUTHENTICATED', 'Invalid value for token')

        return await data_in.getGroups(token)
    }

    // Create group providing its name and description
    async function createGroup(groupObj, token) {
        if (!utils.isValidUuid(token))
            throw errorHandler.buildError('UNAUTHENTICATED', 'Invalid value for token')

        validateGroup(groupObj)

        return await data_in.createGroup(groupObj, token)
    }

    // Get the details of a group, with its name, description and names of the included games
    async function getGroup(groupId, token) {
        if (!utils.isValidUuid(token))
            throw errorHandler.buildError('UNAUTHENTICATED', 'Invalid value for token')

        if (!utils.isType(groupId, 'string'))
            throw errorHandler.buildError('INVALID_PARAM', 'Group ID is not a string.')

        const group = await data_in.getGroup(groupId, token)
        if (group.games.length > 0) {
            group.games = await data_ext.getGames({
                ids: group.games.join(',')
            })
        }
        return group
    }

    // Edit group by changing its name and description
    async function editGroup(groupId, groupObj, token) {

        if (!utils.isType(groupId, 'string'))
            throw errorHandler.buildError('INVALID_PARAM', 'Group ID is not a string')

        if (!utils.isValidUuid(token))
            throw errorHandler.buildError('UNAUTHENTICATED', 'Invalid value for token')

        if (!groupObj || !groupObj.hasOwnProperty('name') && !groupObj.hasOwnProperty('description'))
            throw errorHandler.buildError('INVALID_PARAM', 'Group has no parameters')

        if (!utils.isType(groupObj.name, 'string') && !utils.isType(groupObj.description, 'string'))
            throw errorHandler.buildError('INVALID_PARAM', 'Group with invalid properties')

        return await data_in.editGroup(groupId, groupObj, token)
    }

    // Delete a group
    async function deleteGroup(groupId, token) {
        if (!utils.isType(groupId, 'string'))
            throw errorHandler.buildError('INVALID_PARAM', 'Group ID is not a string.')

        if (!utils.isValidUuid(token))
            throw errorHandler.buildError('UNAUTHENTICATED', 'Invalid value for token')

        return await data_in.deleteGroup(groupId, token)
    }

    // Add a game to a group
    async function addGameToGroup(groupId, gameId, token) {
        if (!utils.isType(groupId, 'string'))
            throw errorHandler.buildError('INVALID_PARAM', 'Group ID is not a string.')

        if (!utils.isType(gameId, 'string'))
            throw errorHandler.buildError('INVALID_PARAM', 'Game ID is not a string.')

        if (!utils.isValidUuid(token))
            throw errorHandler.buildError('UNAUTHENTICATED', 'Invalid value for token')

        if(await data_in.gameExistsInGroup(groupId, gameId, token))
            throw errorHandler.buildError('INVALID_PARAM', 'Game ID already present on this group.')

        if (!(await data_in.isGroupFromUser(groupId, token)))
            throw errorHandler.buildError('INVALID_PARAM', 'Group ID not present on this user.')

        await data_ext.getGames({
            ids: gameId
        }, buildGameIdOnly)

        return await data_in.addGameToGroup(groupId, gameId, token)
    }

    // Remove a game from a group
    async function removeGameFromGroup(groupId, gameId, token) {
        if (!utils.isValidUuid(token))
            throw errorHandler.buildError('UNAUTHENTICATED', 'Invalid value for token')

        if (!utils.isType(groupId, 'string'))
            throw errorHandler.buildError('INVALID_PARAM', 'Group ID is not a string.')

        if (!utils.isType(gameId, 'string'))
            throw errorHandler.buildError('INVALID_PARAM', 'Game ID is not a string.')

        if(!( await data_in.gameExistsInGroup(groupId, gameId, token)))
            throw errorHandler.buildError('INVALID_PARAM', 'Game ID not present on this group.')

        return await data_in.removeGameFromGroup(groupId, gameId, token)
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

function validateGroup(groupObj) {
    if (!utils.checkAllKeys(groupObj, ['name', 'description']))
        throw errorHandler.buildError('INVALID_PARAM', `Group with invalid properties.`)

    if (!((typeof groupObj.name === 'string') && (typeof groupObj.description === 'string')))
        throw errorHandler.buildError('INVALID_PARAM', `Group's name or description is invalid.`)

    if (!groupObj.name || !groupObj.description)
        throw errorHandler.buildError('INVALID_PARAM', `Group's name or description is missing.`)
}

function buildGameIdOnly(game) {
    return {
        id: game.id
    }
}