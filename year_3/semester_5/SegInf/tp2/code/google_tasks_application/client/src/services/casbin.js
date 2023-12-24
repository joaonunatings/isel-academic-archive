import fetch from 'node-fetch'

const API_ENDPOINT = "http://localhost:3001/"

export async function checkPermission(user, action, object) {
    const reqObj = {
        user: user,
        action: action,
        object: object
    }
    const res = await fetch(API_ENDPOINT + "permission", {
        method: 'POST',
        headers: {
            'Content-type': 'application/json',
        },
        body: JSON.stringify(reqObj)
    })
    const { status } = res
    if (status === 200) return true
    else return false
}

export async function getRoles(user) {
    const reqObj = {
        user: user
    }
    const res = await fetch(API_ENDPOINT + "roles", {
        method: 'POST',
        headers: {
            'Content-type': 'application/json',
        },
        body: JSON.stringify(reqObj)
    })
    return await res.json()
}