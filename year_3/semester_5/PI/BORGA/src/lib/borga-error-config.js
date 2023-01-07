'use strict'

module.exports = {
    DEFAULT: (info) => { return { code: 1000, message: 'Internal Server Error', status: 500, info: info }},
    BAD_REQUEST: { code: 1001, message: 'Bad request', status: 400 },
    NOT_FOUND: { code: 1002, message: 'Not found', status: 404 },
    UNAUTHENTICATED: { code: 1003, message: 'Unauthorized', status: 401 },
    EXT_SVC_FAIL: { code: 1004, message: 'External service failure', status: 502 },
    INVALID_PARAM: { code: 1005, message: 'Invalid parameter', status: 400 },
    USER_ALREADY_EXISTS: { code: 1006, message: 'User already exists', status: 409 }
}