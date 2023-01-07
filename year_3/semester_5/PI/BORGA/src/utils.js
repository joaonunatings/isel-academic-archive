'use strict'

const crypto = require('crypto')

module.exports = {

    newCryptoUUID: function (existingTokens) {
        let token = crypto.randomUUID()
        while (existingTokens.hasOwnProperty(token)) {
            token = crypto.randomUUID()
        }
        return token
    },

    // Não foi utilizado reduce porque uma string é imutável logo a cada iteração estava a criar uma,
    // com o map e o join é feito uma única vez
    queryStringify: function (query) {
        const querystring = Object.entries(query).map(([key, value]) => `${key}=${value}`)
        return querystring.join('&')
    },

    emptyObj: function (obj) {
        return Object.keys(obj).length === 0
    },

    isValidUuid: function (uuid) {
        // Regular expression to check if string is a valid UUID
        const regexExp = /^[0-9a-fA-F]{8}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{12}$/gi

        return regexExp.test(uuid)
    },

    checkAllKeys: function (obj, keys) {
        keys.every((key) => {
            if (!(key in obj)) return false
        })
        return true
    },

    checkAcceptedKeys: function (obj, keys) {
        return (Object.keys(obj)
            .every((key) =>
                keys.includes(key)))
    },

    checkAcceptedValues: function (obj, values) {
        return (this.isType(obj, 'undefined') || (values.includes(obj)))
    },

    checkNumber: function (value) {
        if (this.isType(value, 'undefined'))
            return true
        const nr = parseInt(value, 10)
        return !isNaN(nr) && value >= 0
    },

    isType: function (obj, str) {
        return (typeof obj === str)
    },

    makeNumber: function (str) {
        return parseInt(str, 10)
    }
}