'use strict'

const errors = require('../../borga-error-config')

module.exports = {
    405: { app_error: errors.BAD_REQUEST, message: 'Local database does not allow such method' },
    404: { app_error: errors.NOT_FOUND, message: 'Not found at local database' },
    400: { app_error: errors.UNAUTHENTICATED, message: 'User not found' },
    user_already_exists: { app_error: errors.USER_ALREADY_EXISTS, message: 'User already exists in database' },
    user_not_found: { app_error: errors.UNAUTHENTICATED, message: 'User not found' },
    group_not_found: { app_error: errors.NOT_FOUND, message: 'Group not found' }
}