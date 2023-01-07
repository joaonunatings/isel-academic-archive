'use strict'

const errors = require('./borga-error-config.js')

module.exports = (errorList) => {

    function buildError(name, info) {
        if (name === undefined || errorList === undefined || !errorList.hasOwnProperty(name)) {
            return errors.DEFAULT(info)
        }
        const error = errorList[name]
        return {
            code: error.code || error.app_error.code,
            message: error.message || error.app_error.code,
            status: error.status || error.app_error.status,
            info: error.info || info
        }
    }

    function catchError(res, err, extra) {
        res.status = errorList[err.code].status
        errorList[err.code].action(res, err, extra)
    }

    return {
        buildError,
        processResponse,
        setHttpError,
        catchError
    }
}

function processResponse(result, expectedResultList, onErrorMessage) {
    if (Array.isArray(expectedResultList) && expectedResultList.includes(result))
        return
    else if (expectedResultList === result)
        return
    throw {
        name: result,
        message: onErrorMessage
    }
}

function setHttpError(res, err) {
    res.status(err.status)
    res.json({
        cause: err
    })
}