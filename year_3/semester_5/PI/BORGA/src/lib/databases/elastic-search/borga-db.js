'use strict'

const fetch = require("node-fetch")

const utils = require('../../../utils')
utils.borga_db = require('./borga-db-utils')

const errorList = require('./borga-db-errors')
const errorHandler = require('../../borga-error-handler')(errorList)

const crypto = require('crypto')

module.exports = (es_spec, guest) => {

    const baseUrl = es_spec.url         /* Example: http://127.0.0.1:9200 */

    const usersUrl = `${baseUrl}/users` /* Example: http://127.0.0.1:9200/users */

    const userGroupsUrl = username =>   /* Example: http://127.0.0.1:9200/guest_groups */
        `${baseUrl}/${username}_groups`

    init()

    // List all groups
    async function getGroups(uuid) {
        try {
            const username = await getUsername(uuid)

            const res = await fetch(`${userGroupsUrl(username)}/_search?size=30`)
            const json = await res.json()
            errorHandler.processResponse(res.status, 200, `Fail at getting groups. Error message: ${json.result}`)

            return utils.borga_db.jsonToGroups(json, utils.borga_db.buildGroup)
        } catch(err) {
            throw errorHandler.buildError(err.name, err.message)
        }
    }

    // Create group providing its name and description
    async function createGroup(groupObj, uuid) {
        try {
            const username = await getUsername(uuid)
            groupObj.games = []

            const res = await fetch(`${userGroupsUrl(username)}/_doc?refresh=true`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(groupObj)
            })
            const json = await res.json()
            errorHandler.processResponse(res.status, 201, `Fail at creating group. Error message: ${json.result}`)

            return json._id
        } catch(err) {
            throw errorHandler.buildError(err.name, err.message)
        }
    }

    // Get the details of a group, with its name, description and names of the included games
    async function getGroup(groupId, uuid) {
        try {
            const username = await getUsername(uuid)

            const res = await fetch(`${userGroupsUrl(username)}/_doc/${groupId}`)
            const json = await res.json()
            //errorHandler.processResponse(res.status, 200, `Fail while getting group details. Error message: group not found`)
            if (!json.found) throw { name: 'group_not_found', message: `Group ${groupId} not found` }

            return utils.borga_db.buildGroupDetails(json._source)
        } catch (err) {
            throw errorHandler.buildError(err.name, err.message)
        }
    }

    // Edit group by changing its name and description
    async function editGroup(groupId, groupObj, uuid) {
        try {
            const username = await getUsername(uuid)
            const updateObj = { doc: { name: groupObj.name, description: groupObj.description } }

            const res = await fetch(`${userGroupsUrl(username)}/_update/${groupId}?refresh=true`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(updateObj)
            })
            errorHandler.processResponse(res.status, 200, `Fail at updating group.`)

            return 'Group updated'
        } catch(err) {
            throw errorHandler.buildError(err.name, err.message)
        }
    }

    // Delete a group
    async function deleteGroup(groupId, uuid) {
        try {
            const username = await getUsername(uuid)

            const res = await fetch(`${userGroupsUrl(username)}/_doc/${groupId}`, { method: 'DELETE' })
            errorHandler.processResponse(res.status, 200, `Fail while deleting group. Error message: ${(await res.json()).result}`)

            return 'Group deleted'
        } catch(err) {
            throw errorHandler.buildError(err.name, err.message)
        }
    }

    // Add a game to a group
    async function addGameToGroup(groupId, gameId, uuid) {
        try {
            const username = await getUsername(uuid)
            const scriptObj = {
                script: {
                    source: 'if (!ctx._source.games.contains(params.gameId)) { ctx._source.games.add(params.gameId) }',
                    params: { gameId: gameId }}}

            const res = await fetch(`${userGroupsUrl(username)}/_update/${groupId}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(scriptObj)
            })
            errorHandler.processResponse(res.status, 200, `Fail while adding game to group. Error message: group_not_found`)

            return 'Game added'
        } catch (err) {
            throw errorHandler.buildError(err.name, err.message)
        }
    }

    // Remove a game from a group
    async function removeGameFromGroup(groupId, gameId, uuid) {
        try {
            const username = await getUsername(uuid)
            const scriptObj = {
                script: {
                    source: ' if (ctx._source.games.contains(params.gameId)) { ctx._source.games.remove(ctx._source.games.indexOf(params.gameId)) }',
                    params: {
                        gameId: gameId }}}

            const res = await fetch(`${userGroupsUrl(username)}/_update/${groupId}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(scriptObj)
            })
            errorHandler.processResponse(res.status, 200, `Fail while removing game from group. Error message: group_not_found`)

            return 'Game removed'
        } catch (err) {
            throw errorHandler.buildError(err.name, err.message)
        }
    }

    async function createUser(username, password) {
        try {
            if (await userExists(username)) throw { name: 'user_already_exists', message: `User ${username} already exists` }
            const usernameObj = { username: username, password: password, token: crypto.randomUUID() }

            const res = await fetch(`${usersUrl}/_doc`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(usernameObj)
            })
            errorHandler.processResponse(res.status, 201, `Fail while creating user. Error message: ${(await res.json()).result}`)

            await createIndex(userGroupsUrl(username))
            return usernameObj.token
        } catch (err) {
            throw errorHandler.buildError(err.name, err.message)
        }
    }

    async function gameExistsInGroup(groupId, gameId, uuid) {
        const groupObj = await getGroup(groupId, uuid)

        return (utils.isType(gameId, 'string') && groupObj.games.includes(gameId))
    }

    async function userExists(username) {
        const res = await fetch(`${usersUrl}/_search?q=username:${username}`)
        const json = await res.json()
        errorHandler.processResponse(res.status, 200, `Error while getting user. Error message: ${json.result}`)

        return (json.hits.total.value > 0 && json.hits.hits[0]._source.username === username)
    }

    async function createIndex(index) {
        const res = await fetch(index, {method: 'PUT'})
        errorHandler.processResponse(res.status, [200, 400], `Fail while creating index ${index}. Error message: ${(await res.json()).result}`)
    }

    async function deleteIndex(index) {
        const res = await fetch(index, {method: 'DELETE'})
        errorHandler.processResponse(res.status, [200, 404], `Fail while deleting index ${index}. Error message: ${(await res.json()).result}`)
    }

    async function getUsername(uuid) {
        const res = await fetch(`${usersUrl}/_doc/_search?q=token:${uuid}`)
        const json = await res.json()
        errorHandler.processResponse(res.status, 200, `Fail while getting username`)

        if (json.hits.hits[0]._source.token !== uuid) throw { name: 'user_not_found', message: 'User not found in database' }

        return json.hits.hits[0]._source.username
    }

    async function getUserByUsername(username) {
        const res = await fetch(`${usersUrl}/_search?q=username:${username}`)
        const json = await res.json()
        errorHandler.processResponse(res.status, 200, `Fail while getting user info. Error message: ${json.result}`)

        return utils.borga_db.buildUserInfo(json.hits.hits[0]._source)
    }

    async function isGroupFromUser(groupId, uuid) {
        const username = await getUsername(uuid)

        const res = await fetch(`${userGroupsUrl(username)}/_doc/${groupId}`)
        const json = await res.json()
        return json.found
    }

    async function init() {
        console.log('> Setting up ElasticSearch database...')
        try {
            await fetch(`${baseUrl}`)
        } catch (err) { throw new Error(`ElasticSearch access error, is the database offline?\n${err}`) }
        try {
            await createIndex(usersUrl)
            if (!(await userExists(guest.username))) {
                await fetch(`${usersUrl}/_doc`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(guest)
                })
            }
            await deleteIndex(userGroupsUrl(guest.username))
            await createIndex(userGroupsUrl(guest.username))
        } catch (err) {
            throw new Error(`Error name: ${err.name}\nError message: ${err.message}`)
        } finally {
            console.log('> ElasticSearch setup complete.')
        }
    }

    return {
        createGroup,
        editGroup,
        getGroups,
        deleteGroup,
        getGroup,
        addGameToGroup,
        removeGameFromGroup,
        createUser,
        getUserByUsername,
        gameExistsInGroup,
        isGroupFromUser
    }
}

