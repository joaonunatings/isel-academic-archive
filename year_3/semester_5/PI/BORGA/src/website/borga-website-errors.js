'use strict'

const errors = require('../lib/borga-error-config')

module.exports = {
    1000: { app_error: errors.DEFAULT, status: 500, action: (res, err) => {
        res.render('error', { err: err})} },
    1002: { app_error: errors.NOT_FOUND, status: 404, action: (res, err) => {
        res.render('error', { err: err})} },
    1004: { app_error: errors.EXT_SVC_FAIL, status: 502, action: (res, err) => {
        res.render('error', {err: err})} },
    1005: { app_error: errors.INVALID_PARAM, status: 400, action: (res, err) => {
        res.render('error', { err: err})} },
    1003: { app_error: errors.UNAUTHENTICATED, status: 401, action: (res, err, extra) => {
        res.redirect(`/users/login?back=${extra}`)} }
}