const express = require("express");
const casbin = require("./casbin.js")

const PORT = process.env.PORT || 3001;

const app = express();

app.use(express.json())

app.post("/permission", (req, res) => {
    const body = req.body
    casbin.checkPermission(body.user, body.action, body.object).then(r => {
        (r) ? res.status(200).send() : res.status(401).send()
    })
});

app.post("/roles", (req, res) => {
    const body = req.body
    casbin.getRoles(body.user).then(r => {
        res.send(r)
    })
});

app.listen(PORT, () => {
    console.log(`Server listening on ${PORT}`);
});