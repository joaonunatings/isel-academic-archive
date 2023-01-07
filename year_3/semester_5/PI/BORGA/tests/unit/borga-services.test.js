'use strict';

const userTokens = {
    '36b8f84d-df4e-4d49-b662-bcde71a8764f': 'adminUser',
    '36b8f84d-df4e-4d49-b662-bcde71b8764e': 'dummyUser'
}

const userData = {
    'adminUser': {
        groups: {
            '1': {
                groupId: '1',
                name: 'Strategy',
                description: 'Strategy description',
                games: ['56onxUCrXc', 'KVDWzMWit8', 'TAAifFP590']
            },
            '2': {
                groupId: '2',
                name: 'Racing',
                description: 'Racing description',
                games: ['bSFxESny3q', 'unMxq0jVdk', 'kKaq4geQYI']
            },
            '3': {
                groupId: '3',
                name: 'Action',
                description: 'Action description',
                games: []
            }
        }
    },
    'dummyUser': {
        groups: {}
    }
}

const nextGroupID = 4

const services_builder = require('../../src/lib/services/borga-services')
const local_data_mem = require('../../src/lib/databases/borga-data-mem')(userTokens, userData, nextGroupID);
const services = services_builder(null, local_data_mem);

const test_user = 'alex';

describe('Group tests without given DATA.', () => {

    test('Get Groups with wrong authentication.', async () => {

        try {
            await services.getGroups("BAD TOKEN");
        } catch (err) {
            expect(err.name).toEqual('UNAUTHENTICATED');
            return;
        }
        throw new Error(
            "Shouldn't return from getGroups when query is empty."
        );

    })

    test('Create user and get empty Groups.', async () => {

        try {
            const token = await services.createUser(test_user)
            const groups = await services.getGroups(token);
            expect(groups).toHaveLength(0)
            return
        } catch (err) {
            throw new Error(
                "Shouldn't return from getGroups with empty Array."
            );
        }
    })

    test('Create user and group then get group with empty games.', async () => {

        try {

            const token = await services.createUser("newUser")
            const groupObj = {
                name: 'newDummyGroupName',
                description: 'Group description'
            }

            const groupID = await services.createGroup(groupObj, token)
            const groupDetails = await services.getGroup(groupID, token)

            expect(groupDetails.games).toEqual([])
            return
        } catch (err) {
            console.log(err)
            throw new Error(
                "Should return games with empty Array"
            );
        }
    })

    
    test('Repeat creation of user', async () => {
        try{
            try {
                await services.createUser("repeatedUser")
                await services.createUser("repeatedUser")
            
            } catch (err) {
                console.log(err)
                expect(err.name).toEqual('USER_ALREADY_EXISTS')
                return
            }
        } catch(err2) {
            throw new Error(
                "Should return games with empty Array."
            );
        }
        
    })

    test('Create user, group, edit group name only.', async () => {
        let token
        let groupId

        try{
            token = await services.createUser('newUser5')
        } catch(err) {
            throw new Error(
                "Error creating user."
            );
        }

        try{
            groupId = await services.createGroup({name: 'group5', description: 'Create group, edit name only.'}, token)
        } catch(err) {
            throw new Error(
                "Error creating group."
            );
        }

        try{
            const groupDetails1 = await services.getGroup(groupId, token)
            expect(groupDetails1.name).toEqual('group5')
        } catch(err) {
            throw new Error(
                "Error comparing initial group name."
            );
        }
        try{
            await services.editGroup(groupId, {name: 'new name'}, token)
        } catch(err) {
            throw new Error(
                "Error editing group name only."
            );
        }

        try{
            const groupDetails2 = await services.getGroup(groupId, token)
            expect(groupDetails2.name).toEqual('new name')
        } catch(err) {
            throw new Error(
                "Error comparing initial group name."
            );
        }
        
    })

    test('Delete Group ID.', async () => {

        try {
            await services.deleteGroup(1, '36b8f84d-df4e-4d49-b662-bcde71a8764f')
            await services.editGroup(1, {
                name: 'nome',
                description: 'descricao'
            }, '36b8f84d-df4e-4d49-b662-bcde71a8764f')
            throw new Error("Should not get here")
        } catch (err) {
            expect(err.name).toEqual('NOT_FOUND')
            return;
        }
    })
});