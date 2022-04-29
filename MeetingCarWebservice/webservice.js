var https;
try {
  https = require('https');
} catch (err) {
  console.log('https support is disabled!');
}
var mysql = require('mysql');
var fs = require('fs');
var bodyParser = require('body-parser')
var express = require('express');
var app = express();
var jwt = require('jsonwebtoken');

var privateKey  = fs.readFileSync('/etc/letsencrypt/live/m1.flareden.fr/privkey.pem', 'utf8');
var certificate = fs.readFileSync('/etc/letsencrypt/live/m1.flareden.fr/fullchain.pem', 'utf8');

var credentials = {key: privateKey, cert: certificate};

const SECRET = 'mykey'
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }))

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


const authenticateJWT = (req, res, next) => {
    const token = req.headers.authorization;

    if (token) {
        jwt.verify(token, access.secret, (err, user) => {
            if (err) {
                return res.sendStatus(403);
            }

            req.user = user;
            next();
        });
    } else {
        res.sendStatus(401);
    }
};
https
  .createServer(credentials, app)
  .listen(9000, ()=>{
    console.log('server is runing at port 9000')
  });
  
app.all('/',authenticateJWT, function (req, res) {
	console.log("User : " + req.user);
	res.json({oui:true});
});

// --- Methode sans connection préalable ---
app.get('/annonces/page/:page', function(req, res){
	let page = con.escape(req.params.page);
	if(Number.isInteger(page) && page >= 0){
		let limite = 20;
		con.query("SELECT * FROM annonce OFFSET :offset ROWS LIMIT :limit ",{ offset: (page * limite), limit: limite }, function (err, result) {
			if (err) throw err;
			res.json(JSON.stringify(result));
		});
	} else {
		res.json({error:"NaN"});
	}
});

app.get('/annonce/:id', function (req, res) {
	let idVal = con.escape(req.params.id);
	if(Number.isInteger(idVal) && idVal >= 0){
		con.query("SELECT * FROM annonce WHERE id = :id", {id : idVal}, function (err, result) {
			if (err) throw err;
			if(result.length > 0){
				res.json(result[0]);
			} else {
				res.json({error:"unknown"});
			}
		});
	} else {
		res.json({error:"unknown"});
	}
});

app.get('/client/:id',  function (req, res) {
	let idVal = con.escape(req.params.id);
	if(Number.isInteger(idVal) && idVal >= 0){
		con.query("SELECT id, nom, prenom, telephone, photo FROM client WHERE id = :id", {id : idVal}, function (err, result) {
			if (err) throw err;
			if(result.length > 0){
				res.json(result[0]);
			} else {
				res.json({error:"unknown"});
			}
		});
	} else {
		res.json({error:"unknown"});
	}
});

app.get('/connection',function (req, res) {
	let username = con.escape(req.headers.username);
	let password = "0x" + req.headers.password;
	console.log("USERNAME : " + username + " | password : " + password); 
	if(username.length > 0 && password.length > 0){
		con.query("SELECT id, email, nom, prenom, telephone, date_naissance, photo, adresse FROM client WHERE email = " + username + " AND mot_de_passe = " + password + " LIMIT 1", function (err, result) {
			if (err) throw err;
			console.log("FOUNDED : " + result.length);
			if(result.length > 0){
				let utilisateur = result[0];
				console.log("USER : " + result[0]);
				const token = jwt.sign({
					id: utilisateur.id,
					username: utilisateur.email
				}, access.secret, { expiresIn: '3 hours' });
				res.json({user : utilisateur, access_token: token});
			} else {
				res.json({error:"unknown"});
			}
		});
	} else {
		res.json({error:"NoNameOrPassword"});
	}
});

app.get('/inscription', function(req,res){
	let obj = JSON.parse(req.headers.object);
	let email = con.escape(obj.email);
	con.query("SELECT id FROM client WHERE email = " + email + " LIMIT 1", function (err, result) {
		if(err) throw err;
		if(result.length <= 0){
			let password = "0x" + obj.mot_de_passe;
			let nom = con.escape(obj.nom);
			let prenom = con.escape(obj.prenom);
			let date_naissance = con.escape(obj.date_naissance);
			let telephone = con.escape(obj.telephone);
			let adresse = con.escape(obj.adresse);
			
			con.query("INSERT INTO `client`(`id`, `email`, `mot_de_passe`, `nom`, `prenom`, `telephone`, `date_naissance`, `photo`, `adresse`)"
				+ "VALUES (null," + email + "," + password +"," + nom +"," + prenom+"," +telephone+"," + date_naissance+",null," + adresse+")"
				, function (err, result) {
					if(err) throw err;
					res.json({result : "OK"});
			});
		} else {
			res.json({result : "email_already_use"});
		}
	});
});

app.get('/isLogin', function(req, res){
	const token = req.headers.authorization;
	let result = false;
    if (token) {
        jwt.verify(token, access.secret, (err, user) => {
            if (!err) {
                result = true;	
            }
        });
    } 
	res.json({islog : result});
});


con.connect(function (err) {
    if (err) throw err;
    console.log("Connected!");
});

