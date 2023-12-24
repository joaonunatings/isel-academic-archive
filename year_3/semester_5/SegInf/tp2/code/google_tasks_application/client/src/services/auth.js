import { getCookie } from '../utils.js'
import * as jose from 'jose'

export const DEFAULT_URI = {
    OPENID_CONFIGURATION: 'https://accounts.google.com/.well-known/openid-configuration',
    AUTHORIZATION_ENDPOINT: 'https://accounts.google.com/o/oauth2/v2/auth',
    TASKS_AUTH_ENDPOINT: 'https://www.googleapis.com/auth/tasks',
    TOKEN_ENDPOINT: 'https://oauth2.googleapis.com/token'
}

export async function getOpenIdConfiguration() {
    const res = await fetch(DEFAULT_URI.OPENID_CONFIGURATION, {method: 'GET'})
    const json = await res.json()
    console.debug(json)
    if (res.status === 200) {
        return json
    }
}

export function buildAuthUri(client_id, baseUrl) {
    const urlParams = new URLSearchParams()
    urlParams.append('response_type', 'code')
    urlParams.append('client_id', client_id)
    urlParams.append('scope', 'openid email profile https://www.googleapis.com/auth/tasks https://www.googleapis.com/auth/tasks.readonly')
    urlParams.append('redirect_uri', 'http://localhost:3000/login-callback')
    urlParams.append('nonce', Math.random().toString(36).substring(2))
    urlParams.append('prompt', 'consent select_account')
    return (baseUrl === undefined) ? DEFAULT_URI.AUTHORIZATION_ENDPOINT : baseUrl + '?' + urlParams.toString()
}

export function setUserState(state) {
    localStorage.clear()
    for (let prop in state) {
        if (prop === 'token') {
            document.cookie = `${prop}=${state[prop]}; expires=${new Date(Date.now() + (state.expires_in * 1000)).toUTCString()}`
        }
        localStorage.setItem(prop, state[prop])
    }
}

export function logoutUser() {
    localStorage.clear()
    document.cookie = 'token=; expires=Thu, 01 Jan 1970 00:00:00 UTC;'
}

export function validateUser(token) {
    const tokenCookie = getCookie('token')
    return tokenCookie !== undefined && tokenCookie === token
}

export function getUserState() {
    return {
        token: getCookie('token'),
        id_token: localStorage.getItem('id_token'),
        access_token: localStorage.getItem('access_token'),
        expires_in: localStorage.getItem('expires_in')
    }
}

export async function getToken(code, client_id, client_secret, redirect_uri) {
    /*let myHeaders = new Headers()
    myHeaders.append('Content-Type', 'application/x-www-form-urlencoded')
    headers.append('Cross-Origin-Resource-Sharing', 'true')
    myHeaders.append('Access-Control-Allow-Origin', '*') // TODO: Change this to local url?*/
    const requestBody = {
        code: code,
        client_id: client_id,
        client_secret: client_secret,
        redirect_uri: redirect_uri,
        grant_type: "authorization_code"
    }

    let formBody = []
    for (let property in requestBody) {
        let encodedKey = encodeURIComponent(property)
        let encodedValue = encodeURIComponent(requestBody[property])
        formBody.push(encodedKey + "=" + encodedValue)
    }
    formBody = formBody.join("&")

    const options = {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: formBody}
    const res = await fetch(DEFAULT_URI.TOKEN_ENDPOINT, options)
    const json = await res.json()
    console.log(json)
    if (res.status === 200) {
        return json
    }
}

export function getJwtObj() {
    const id_token = localStorage.getItem('id_token')
    if (id_token !== null) return jose.decodeJwt(id_token)
}