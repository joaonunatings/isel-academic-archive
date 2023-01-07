'use strict'

const errors = require('../../borga-error-config')

module.exports = {
    400: { app_error: errors.BAD_REQUEST, message:  'Bad request from board games data' },
    403: { app_error: errors.BAD_REQUEST, message: 'Forbidden access to board games data resource' },
    404: { app_error: errors.NOT_FOUND, message: 'Not found at board games data' },
    429: { app_error: errors.EXT_SVC_FAIL, message: 'Too many requests from board games data' },
}