'use strict'

module.exports = {
    jsonToGroups,
    buildGroup,
    buildGroupDetails,
    buildUserInfo
}

function jsonToGroups(json, groupBuilder) {
    if (json.hits.hits === undefined || json.hits.total.value === 0) return []
    const groups = json.hits.hits
    return groups.map( (group) => groupBuilder(group))
}

function buildGroup(group) {
    return {
        groupId: group._id,
        name: group._source.name,
        description: group._source.description
    }
}

function buildGroupDetails(group) {
    return {
        name: group.name,
        description: group.description,
        games: group.games
    }
}

function buildUserInfo(user) {
    return {
        userId: user.token,
        username: user.username,
        password: user.password
    }
}