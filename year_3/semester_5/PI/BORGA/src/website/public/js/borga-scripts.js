window.onload = setup

function setup() {
    const logout = document.querySelector('#logout')
    if(logout) {
        logout.addEventListener('click', (event) => handlerLogout(event))
    }

    document.querySelectorAll('.group-item')
        .forEach(item => {
            const deleteButton = item.querySelector('button.delete')
            deleteButton.addEventListener('click', () => processDeleteGroup(item, deleteButton.dataset.groupId, item.dataset.token))
        })

    document.querySelectorAll('.game-item')
        .forEach(item => {
            const deleteButton = item.querySelector('button.delete')
            deleteButton.addEventListener('click', () => processDeleteGame(item, item.dataset.groupId, deleteButton.dataset.gameId, item.dataset.token))
        })
    document.querySelectorAll('div.group-form')
        .forEach(item => {
            const addButton = item.querySelector('button.add-game')
            addButton.addEventListener('click', () => addGame(addButton, item))
    })

    const saveGroup = document.querySelector('div.save-group')
    saveGroup.querySelector('button.save-button').addEventListener('click', () => createGroup(saveGroup))

    const exampleModal = document.getElementById('exampleModal')
    if(exampleModal) {
        const modalButton = exampleModal.querySelector('button.submit-edit')
        exampleModal.addEventListener('show.bs.modal', (event) => modalUpdateSubmitButton(event, exampleModal, modalButton))
        modalButton.addEventListener('click', () => modalSubmitButton(modalButton, exampleModal))
    }

}

async function processDeleteGroup(item, groupId, token) {
    const uri = `/api/groups/${groupId}`

    const options = {
        headers: {
            Authorization: `Bearer ${token}`
        },
        method: 'DELETE'
    }

    const rsp = await fetch(uri, options)
    if(rsp.status === 200) {
        item.remove()
        alertMsg('Group deleted.', 'success')
    } else {
        alertMsg('Error deleting group.')
    }
}

async function processDeleteGame(item, groupId, gameId, token) {
    const uri = `/api/groups/${groupId}/games/${gameId}`

    const options = {
        headers: {
            Authorization: `Bearer ${token}`
        },
        method: 'DELETE'
    }

    const rsp = await fetch(uri, options)
    if(rsp.status === 200) {
        item.remove()
        alertMsg('Game deleted.', 'success')
    } else {
        alertMsg('Error deleting group.')
    }
}

async function createGroup(saveGroup) {
    const groupName = saveGroup.querySelector('input.group-name').value
    const groupDescription = saveGroup.querySelector('input.group-description').value

    const uri = `/groups`

    const rsp = await fetch(uri, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded'},
        body: `name=${encodeURIComponent(groupName)}&description=${encodeURIComponent(groupDescription)}`
    })

    if(rsp.status === 201) {
        location.reload()
        saveGroup.querySelector('input.group-name').value = ""
        saveGroup.querySelector('input.group-description').value = ""
    } else {
        alertMsg('Group not created.')
    }
}

async function addGame(addButton, form) {
    const game = form.querySelector('input').value
    const group = form.querySelector('select').value

    const uri = `/addGameToGroup`

    const rsp = await fetch(uri, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded'},
        body: `groupId=${encodeURIComponent(group)}&gameId=${encodeURIComponent(game)}`
    })

    if(rsp.status === 201) {
        alertMsg('Game added.', 'success')
    } else if (rsp.status === 200) {
        alertMsg('Game already in chosen group.', 'success')
    } else {
        alertMsg('Game not added.')
    }

}

async function modalUpdateSubmitButton(event, exampleModal, modalButton) {
    let editButton = event.relatedTarget

    let groupId = editButton.getAttribute('data-group-id')
    let token = document.querySelector('.group-item').dataset.token

    modalButton.dataset.groupId = groupId
    modalButton.dataset.token = token

    exampleModal.querySelector('.modal-body input').value = editButton.getAttribute('data-group-name')
    exampleModal.querySelector('.modal-body textarea').value = editButton.getAttribute('data-group-description')
}

async function modalSubmitButton(button, modal) {
    let groupName = modal.querySelector('.modal-body input').value
    let groupDescription = modal.querySelector('.modal-body textarea').value

    let groupId = button.dataset.groupId
    let token = button.dataset.token

    const uri = `/api/groups/${groupId}`

    const options = {
        headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": 'application/x-www-form-urlencoded'
        },
        method: 'PUT',
        body: `name=${encodeURIComponent(groupName)}&description=${encodeURIComponent(groupDescription)}`
    }

    const rsp = await fetch(uri, options)
    if(rsp.status === 200) {
        location.reload()
    } else {
        alertMsg('Group unable to edit.')
    }

    modal.querySelector('.modal-body input').value = ""
    modal.querySelector('.modal-body textarea').value = ""
}

async function handlerLogout(event) {
    const loc = window.location.protocol + '//' + window.location.host;
    const req = `${loc}/users/logout`

    const resp = await fetch(req, {
        method: 'POST', 
        headers: { 'Content-Type': 'application/x-www-form-urlencoded'},
        body: ``
    })

    if(resp.status == 200) {
        window.location.href = resp.url
    }
    else{
        alert(resp.message)
    }
}

function alertMsg(message, type) {
    if (!type) type = 'danger'
    document
        .querySelector('.messages')
        .innerHTML = 
            `<div class="alert alert-${type} alert-dismissible fade show" role="alert">
            <strong>${message}</strong>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
          </div>`
}