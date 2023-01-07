'use strict';

const express = require('express');

const errorList = require('../website/borga-website-errors')
const errorHandler = require('../lib/borga-error-handler')(errorList)

module.exports = (services, guest_token) => {

	async function getHomepage(req, res) {
		res.render('home', {username: getUsername(req)});
	} 

	async function getSearchPage(req, res) {
		try {
			const groups = req.user === undefined ? undefined : await services.getGroups(req.user.userId)
			let games = await services.getGames(req.query ?? {orderBy: 'rank'})
			const newGames = games.map((game) => {return {gameId: game.gameId, name: game.name, image: game.image, description: game.description}})
			res.render('games', {username: getUsername(req), games: newGames, groups: groups})
		} catch(err) {
			errorHandler.catchError(res, err, req.path)
		}
	} 

	async function getGroups(req, res) {
		try {
			const groups = await services.getGroups(getUserId(req))
			res.render('groups', {username: getUsername(req), userId: getUserId(req), groups: groups})
		} catch(err) {
			errorHandler.catchError(res, err, req.path)
		}
	}

	async function createGroup(req, res) {
		try {
			await services.createGroup({name: req.body.name, description: req.body.description}, getUserId(req))
			let groups = await services.getGroups(getUserId(req))
			res.status(201).render('groups', {username: getUsername(req), userId: getUserId(req), groups: groups, sucess: true})
		} catch(err) {
			errorHandler.catchError(res, err, req.path)
		}
	}

	async function getGroup(req, res) {
		try {
			const group = await services.getGroup(req.params.id, getUserId(req))
			res.render('group-details', {username: getUsername(req), userId: getUserId(req), group: group, groupId: req.params.id})
		} catch(err) {
			errorHandler.catchError(res, err, req.path)
		}
	}

	async function addGameToGroup(req, res) {
		try {
			await services.addGameToGroup(req.body.groupId, req.body.gameId, getUserId(req))
			res.status(201).send()
		} catch(err) {
			errorHandler.catchError(res, err, req.path)
		}
	}

	async function getGame(req, res) {
		try {
			let groups = await services.getGroups(getUserId(req))
			const game = await services.getGameDetails(req.params.id)
			res.render('game', {username: getUsername(req), game: game, groups: groups})
		} catch(err) {
			errorHandler.catchError(res, err, req.path)
		}
	}

	function getUsername(req) {
		return req.user === undefined ? undefined : req.user.username
	}

	function getUserId(req) {
		return req.user === undefined ? undefined : req.user.userId
	}

	const router = express.Router();
	
	// Homepage
	router.get('/', getHomepage);
	
	// Search page
	router.get('/search', getSearchPage);

	//Get Groups
	router.get('/groups', getGroups);
	router.post('/groups', createGroup)
	router.get('/groups/:id', getGroup);
	router.post('/addGameToGroup', addGameToGroup)

	router.get('/game/:id', getGame)

	return router;
};