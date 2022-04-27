var mysql = require('mysql');
var fs = require('fs');
var express = require('express');
var app = express();

let access = JSON.parse(fs.readFileSync('access.json'));

var con = mysql.createConnection({
    host: access.host,
    user: access.user,
    password: access.password,
    database: access.database,
    ssl: {
        ca: fs.readFileSync(__dirname + '/certs/ca.pem'),
        key: fs.readFileSync(__dirname + '/certs/client-key.pem'),
        cert: fs.readFileSync(__dirname + '/certs/client-cert.pem')
    }
});

app.listen('9000', '0.0.0.0', () => {
    console.log("server is listening on 9000 port");
})

app.get('/', function (req, res) {
    con.query("SELECT * FROM image", function (err, result) {
        if (err) throw err;
        res.send(result[0]);
    });
});


con.connect(function (err) {
    if (err) throw err;
    console.log("Connected!");
});