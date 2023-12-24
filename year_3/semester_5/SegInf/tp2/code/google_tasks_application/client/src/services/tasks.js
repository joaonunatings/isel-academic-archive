const DEFAULT_URI = {
    TASKS_ENDPOINT: 'https://tasks.googleapis.com',
    AUTHORIZATION_ENDPOINT: 'https://accounts.google.com/o/oauth2/v2/auth',
    TOKEN_ENDPOINT: 'https://oauth2.googleapis.com/token'
}

export async function getTasksLists(access_token) {
    const res = await fetch (DEFAULT_URI.TASKS_ENDPOINT + "/tasks/v1/users/@me/lists",
        {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + access_token,
                'Content-Type': 'application/json',
            }
        })

    const json = await res.json()
    console.log(json)
    return json
}

export async function getTasks(access_token, list_id) {
    const res = await fetch (DEFAULT_URI.TASKS_ENDPOINT + "/tasks/v1/lists/" + list_id + "/tasks",
        {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + access_token,
                'Content-Type': 'application/json',
            }
        })

    const json = await res.json()
    console.log(json)
    return json
}

export async function deleteTask(access_token, list_id, task_id) {
    const res = await fetch (DEFAULT_URI.TASKS_ENDPOINT + "/tasks/v1/lists/" + list_id + "/tasks/" + task_id,
        {
            method: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + access_token,
            }
        })
}

export async function createTask(access_token, list_id, task) {
    const taskObj = {
        kind: "tasks#task",
        title: task.title,
        notes: task.description
    }
    const res = await fetch(DEFAULT_URI.TASKS_ENDPOINT + "/tasks/v1/lists/" + list_id + "/tasks",
        {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + access_token,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(taskObj)
        })
    const json = await res.json()
    console.log(json)
}