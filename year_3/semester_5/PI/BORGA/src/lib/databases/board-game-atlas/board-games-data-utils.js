'use strict'

const utils = require('../../../utils')

module.exports = {
    IdsToNames,
    jsonToGames,
    buildGame,
    buildGameDetails,
    setProperty,
    addQueryEntry
}

function IdsToNames(values, ids) {
    return values.reduce(function (filtered, value) {
        if (ids.some(obj => obj.id === value.id)) {
            filtered.push(value.name);
        }
        return filtered;
    }, [])
}

function jsonToGames(json, gameBuilder) {
    const games = json.games
    return games.map(g => gameBuilder(g))
}

function buildGame(game) {
    return {
        gameId: game.id,
        name: game.name,
        url: game.url,
        image: game.thumb_url,
        description: game.description
    }
}

function buildGameDetails(game) {
    return {
        id: game.id,
        name: game.name,
        description: game.description,
        url: game.url,
        image: game.thumb_url,
        mechanics: game.mechanics,
        categories: game.categories
    }
}

function setProperty(obj, keyName, value) {
    if (!obj.hasOwnProperty(keyName) || !utils.isType(obj[keyName], typeof(value)))
        obj[keyName] = value
}

function addQueryEntry(query, obj) {
    Object.keys(obj).map( (key) => {
        setProperty(query, key, obj[key])
    })
    return query
}