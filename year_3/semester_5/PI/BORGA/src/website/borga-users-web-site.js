'use strict';

const express = require('express');
const res = require('express/lib/response');
const passport = require('passport')

module.exports = (app, services) => {

	async function newUserForm(req, res) {
		req.user === undefined ? res.render('new-user') : res.redirect('/')
	}

	async function newUser(req, res) {
		try {
			if(req.user === undefined) {
				await services.createUser(req.body.username, req.body.password)
				res.status(201).render('login', {messages: {success: "Account created. Try logging in!"}})
			} else {
				res.redirect('/')
			}

		}catch(err) {
			res.status(401).render('new-user', { messages: {error: 'Username introduzido já existe.'}})
		}
	}

	async function loginForm(req, res) {
		req.user === undefined ? res.render('login', {back: req.query.back}) : res.redirect('/')
	}

	async function login(req, res) {
        let username = req.body.username
        let pass = req.body.password

		let back = req.body.back
		if (back === "" || back === undefined)
				back = '/'

        try {
            let user = await services.validateCredentials(username, pass)
            req.login(user, () => res.redirect(back))
        } catch(err) {
            res.status(401).render('login', { username: req.user === undefined ? undefined : req.user.username, messages: {error: 'Credenciais Inválidas.'}})
        }
	}

	async function logout(req, res) {
		if (req.user !== undefined) {
			req.logout()
			res.redirect('/users/login')
		}
	}

    app.use(passport.initialize())
    app.use(passport.session())

    passport.serializeUser((user, done) => done(null, user))
    passport.deserializeUser((user, done) => done(null, user))

	const router = express.Router();
	
	router.get('/register', newUserForm)
	router.post('/register', newUser)
	router.get('/login', loginForm)
	router.post('/login', login)
	router.post('/logout', logout)
	
	return router;
};