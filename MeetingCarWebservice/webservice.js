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

app.use(bodyParser.json({limit: '200mb'}));
app.use(bodyParser.urlencoded({ extended: true, limit: '200mb' }))

let access = JSON.parse(fs.readFileSync('access.json'));

var privateKey  = fs.readFileSync(access.https.privateKey, 'utf8');
var certificate = fs.readFileSync(access.https.certificate, 'utf8');
var credentials = {key: privateKey, cert: certificate};

var con = mysql.createConnection({
    host: access.host,
    user: access.user,
    password: access.password,
    database: access.database,
    ssl: {
        ca: fs.readFileSync(__dirname + access.ssl.ca),
        key: fs.readFileSync(__dirname + access.ssl.key),
        cert: fs.readFileSync(__dirname + access.ssl.cert)
    }
});

function formatSQL(data){
	if(data != null){
		return "'" + data + "'";
	}
	return null;
}

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
  .listen(access.port, ()=>{
    console.log('server is runing at port ' + access.port)
  });
  

// --- IMAGES ---
app.post('/sendimage', function(req, res){
	let type = req.body.extension;
	let data = req.body.data;
	if(type.length > 0 && data.length > 0){
		con.query('INSERT INTO image SET ? ', {url : ''} , function(err, result){
			if(err) throw err;
			let idSQL = result.insertId 
			let filename = "stockage/" + idSQL.toString(16) + "." + type;
			con.query('UPDATE image SET url = "' + filename+ '" WHERE id = ' + idSQL , function(err, result){
				if(err) throw err;
				fs.writeFile(__dirname + "/" + filename, data, 'base64', err => {
					if(err) throw err;
				});
			});
			
			res.json({id : idSQL});
		});
	}
});

app.get('/image/:id', function(req,res){
	let id = parseInt(req.params.id);
	if(id >= 0){
		con.query("SELECT url FROM image WHERE id = " + id , function(err, result){
			if(err){
				res.json({error:"SQLError"});
			} else {
				if(result.length > 0){
					res.sendFile(__dirname + "/" + result[0].url);
				} else {
					res.json({error:"NoFile"});
				}
			}
		});
	} else {
		res.json({error:"NotValid"});
	}
});

app.get('/removeimage/:id', authenticateJWT,  function(req,res){
		let id = parseInt(req.params.id);
	let userID = req.user.id;
	if(id >= 0){
		con.query("SELECT url FROM image WHERE id = " + id + " AND id IN (SELECT image FROM image_annonce_link WHERE id_annonce IN (SELECT id FROM annonce WHERE vendeur = " + userID +"))", function(err, result){
			if(err) throw err;
			if(result.length > 0){
				fs.unlinkSync(__dirname + "/" + result[0].url);
				con.query("DELETE FROM image WHERE id = "+ id,  function(err, result){
					if(err) throw err;
				});
				con.query("DELETE FROM image_annonce_link WHERE id_image = " + id, function(err, result){
					if(err) throw err;
				});
				res.json({result : "OK"});
			} else {
				res.json({result : "NONE"});
			}
		});
	} else {
		res.json({error:"NotValid"});
	}
});

// --- MESSAGERIE ---
app.get('/discussion/all/:page', authenticateJWT,  function(req,res){
	let page = parseInt(req.params.page);
	let id = req.user.id;
	if(page >= 0){
		let limite = 30;
		con.query("SELECT discussion.*, Ann.titre AS annonce_title, Dest.nom AS dest_nom, Dest.prenom AS dest_prenom, Dest.photo AS dest_photo, Expe.nom AS exped_nom, Expe.prenom AS exped_prenom, Expe.photo AS exped_photo FROM discussion INNER JOIN annonce AS Ann ON Ann.id = id_annonce INNER JOIN client AS Dest ON Dest.id = id_destinataire LEFT JOIN client AS Expe ON Expe.id = id_expediteur WHERE id_expediteur = " + id + " OR id_destinataire = " + id + " LIMIT ? OFFSET ?" , [limite, page*limite], function(err, result){
			if(err){
				res.json({error:"SQLError"});
			} else {
				res.json({result : result});
			}
		});
	} else {
		res.json({error:"NaN"});
	}
});	 
app.get('/discussion/one/:id/messages/:page', authenticateJWT,  function(req,res){
	let idDiscussion = parseInt(req.params.id);
	let page = parseInt(req.params.page);
	let idUser = req.user.id;
	
	if(idDiscussion >= 0){
		let limite = 30;
			con.query("SELECT message.*, exped.nom AS exped_nom, exped.prenom AS exped_prenom, exped.photo AS exped_image FROM message LEFT JOIN client AS exped ON exped.id = id_expediteur WHERE id_discussion = " + idDiscussion + " AND id_discussion IN (SELECT id FROM discussion WHERE id_expediteur = " + idUser + " OR id_destinataire = " + idUser + ") ORDER BY horodatage ASC LIMIT " + limite + " OFFSET " + (limite * page) , function(err, result){
			if(err){
				res.json({error:"SQLError"});
			} else {
				res.json({result : result});
			}
		});
	} else {
		res.json({error:"NaN"});
	}
});

app.get('/discussion/one/:id', authenticateJWT,  function(req,res){
	let idUser = req.user.id;
	
	let idDiscussion = parseInt(req.params.id);
	
	if(idDiscussion >= 0){
		let limite = 30;
			con.query("SELECT discussion.*, Ann.titre AS annonce_title, Dest.nom AS dest_nom, Dest.prenom AS dest_prenom, Dest.photo AS dest_photo, Expe.nom AS exped_nom, Expe.prenom AS exped_prenom, Expe.photo AS exped_photo FROM discussion INNER JOIN annonce AS Ann ON Ann.id = id_annonce INNER JOIN client AS Dest ON Dest.id = id_destinataire LEFT JOIN client AS Expe ON Expe.id = id_expediteur WHERE id = " + idDiscussion + " AND (id_expediteur = " + idUser + " OR id_destinataire = " + idUser + ")" , function(err, result){
				if(err){
					res.json({error:"SQLError"});
				} else {
					res.json({result : result});
				}
		});
	} else {
		res.json({error:"NaN"});
	}
});


app.post('/discussion/create', authenticateJWT, function(req, res){
	let idUser = req.user.id;
	let data = req.body;
	
	con.query('INSERT INTO discussion SET id_expediteur =  ?, id_destinataire = ?, id_annonce = ?', [idUser, data.id_destinataire, data.id_annonce], function(err, result){
		if(err) throw err;
		res.json({id : result.insertId});
	});		
});

app.post('/discussion/create/mail', authenticateJWT, function(req, res){
	let idUser = req.user.id;
	let data = req.body;
	
	con.query('INSERT INTO discussion SET mail_expediteur = ?, id_destinataire = ?, id_annonce = ?', [data.mail_expediteur, data.id_destinataire, data.id_annonce], function(err, result){
		if(err) throw err;
		res.json({id : result.insertId});
	});
});

app.post('/discussion/one/:id/sendMessage', authenticateJWT, function(req,res){
	let idDiscussion = parseInt(req.params.id);

	let idUser = req.user.id;
	let data = req.body;
	
	if(idDiscussion >= 0){
		con.query('SELECT id FROM discussion WHERE id = ? AND (id_expediteur = ? OR id_destinataire = ?)', [idDiscussion, idUser, idUser], function(err, result){
			if(err) throw err;
			if(result.length > 0){
			let dataVal = [];
				if(data.id_image >=0){
					dataVal = [data.contenu, data.id_image, idUser, idDiscussion, data.horodatage];
				} else {
					dataVal = [data.contenu, null, idUser, idDiscussion, data.horodatage];
				}
				con.query('INSERT INTO message (id, contenu, id_image, id_expediteur, id_discussion, horodatage) VALUES (null, ?, ?, ?, ?, ?)' ,dataVal, function(err, result){
					if(err) throw err;
					res.json({result : result.insertId});
				});
			}
		});
	} else {
		res.json({result : "WrongID"});
	}
	
});

app.get('/discussion/exist/:idAnnonce', authenticateJWT, function(req, res){
	let idAnnonce = req.params.idAnnonce;	
	let idClient = req.user.id;
	con.query("SELECT id FROM discussion WHERE id_expediteur = " + idClient + " AND id_annonce = " + idAnnonce, function(err, result){
		if(err) throw err;
		res.json(result);
	});
});



// --- ANNONCES ---
app.get('/annonce/page/:page', function(req, res){
	let page = parseInt(req.params.page);
	
	if( page >= 0){
		let limite = 20;
		let sql = "SELECT annonce.*, V.nom AS vendeur_nom, V.prenom AS vendeur_prenom, V.photo AS vendeur_photo, V.adresse as vendeur_adresse, A.nom AS acheteur_nom, A.prenom AS acheteur_prenom, A.photo AS acheteur_photo, GROUP_CONCAT(DISTINCT image_annonce_link.image SEPARATOR ',') AS images_id"
		+ " FROM annonce"
		+ " INNER JOIN client as V ON V.id = annonce.vendeur"
		+ " LEFT JOIN client as A ON A.id = annonce.acheteur" 
		+ " LEFT JOIN image_annonce_link ON image_annonce_link.annonce = annonce.id";
		con.query(sql + " GROUP BY annonce.id LIMIT ? OFFSET ?",[ limite , (page * limite)], function (err, result) {
			if (err) throw err;
			res.json({result : result});
		});
	} else {
		res.json({error:"NotValid"});
	}
});

app.get('/annonce/user/:id/:page', function(req, res){
	let idUser = parseInt(req.params.id);
	let page = parseInt(req.params.page);
	if(idUser >= 0){
		let limite = 20;
		let sql = "SELECT annonce.*, V.nom AS vendeur_nom, V.prenom AS vendeur_prenom, V.photo AS vendeur_photo, V.adresse as vendeur_adresse, A.nom AS acheteur_nom, A.prenom AS acheteur_prenom, A.photo AS acheteur_photo, GROUP_CONCAT(DISTINCT image_annonce_link.image SEPARATOR ',') AS images_id"
		+ " FROM annonce"
		+ " INNER JOIN client as V ON V.id = annonce.vendeur"
		+ " LEFT JOIN client as A ON A.id = annonce.acheteur" 
		+ " LEFT JOIN image_annonce_link ON image_annonce_link.annonce = annonce.id";
		con.query( sql + ' WHERE vendeur = ? GROUP BY annonce.id  LIMIT ? OFFSET ?', [idUser, limite, (page*limite)], function (err, result) {
			if (err) throw err;
			
			res.json({result : result});
		});
	}else {
		res.json({error:"NotValid"});
	}
});
app.get('/annonce/purchased/:id/:page', function(req, res){
	let idUser = parseInt(req.params.id);
	let page = parseInt(req.params.page);
	if(idUser >= 0){
		let limite = 20;
		let sql = "SELECT annonce.*, V.nom AS vendeur_nom, V.prenom AS vendeur_prenom, V.photo AS vendeur_photo, V.adresse as vendeur_adresse, A.nom AS acheteur_nom, A.prenom AS acheteur_prenom, A.photo AS acheteur_photo, GROUP_CONCAT(DISTINCT image_annonce_link.image SEPARATOR ',') AS images_id"
		+ " FROM annonce"
		+ " INNER JOIN client as V ON V.id = annonce.vendeur"
		+ " LEFT JOIN client as A ON A.id = annonce.acheteur" 
		+ " LEFT JOIN image_annonce_link ON image_annonce_link.annonce = annonce.id";
		con.query( sql + ' WHERE acheteur = ? GROUP BY annonce.id LIMIT ? OFFSET ? ', [idUser, limite, (page*limite)], function (err, result) {
			if (err) throw err;
			res.json({result : result});
		});
	}else {
		res.json({error:"NotValid"});
	}
});

app.get('/annonce/follow/:id/:page', function(req, res){
	let idUser = parseInt(req.params.id);
	let page = parseInt(req.params.page);
	if(idUser >= 0){
		let limite = 20;
		let sql = "SELECT annonce.*, V.nom AS vendeur_nom, V.prenom AS vendeur_prenom, V.photo AS vendeur_photo, V.adresse as vendeur_adresse, A.nom AS acheteur_nom, A.prenom AS acheteur_prenom, A.photo AS acheteur_photo, GROUP_CONCAT(DISTINCT image_annonce_link.image SEPARATOR ',') AS images_id"
		+ " FROM annonce"
		+ " INNER JOIN client as V ON V.id = annonce.vendeur"
		+ " LEFT JOIN client as A ON A.id = annonce.acheteur" 
		+ " LEFT JOIN image_annonce_link ON image_annonce_link.annonce = annonce.id";
		con.query( sql + ' WHERE annonce.id IN (SELECT id_annonce FROM follow WHERE id_client = ?) GROUP BY annonce.id LIMIT ? OFFSET ? ', [idUser, limite, (page*limite)], function (err, result) {
			if (err) throw err;
			res.json({result : result});
		});
	}else {
		res.json({error:"NotValid"});
	}
});


app.get('/annonce/get/:id', function (req, res) {
	let idVal = parseInt(req.params.id);
	if(idVal >= 0){
		let sql = "SELECT annonce.*, V.nom AS vendeur_nom, V.prenom AS vendeur_prenom, V.photo AS vendeur_photo, V.adresse as vendeur_adresse, A.nom AS acheteur_nom, A.prenom AS acheteur_prenom, A.photo AS acheteur_photo, GROUP_CONCAT(DISTINCT image_annonce_link.image SEPARATOR ',') AS images_id"
		+ " FROM annonce"
		+ " INNER JOIN client as V ON V.id = annonce.vendeur"
		+ " LEFT JOIN client as A ON A.id = annonce.acheteur" 
		+ " LEFT JOIN image_annonce_link ON image_annonce_link.annonce = annonce.id"
		con.query(sql + " WHERE annonce.id = " + idVal +" GROUP BY annonce.id", function (err, result) {
			if (err) throw err;
			if(result.length > 0){
				res.json({result : result[0]});
			} else {
				res.json({error:"unknown"});
			}
		});
	} else {
		res.json({error:"NotValid"});
	}
});

app.post('/annonce/create', authenticateJWT , function (req, res) {
	let user = req.user;
	let data = req.body;
	let imagesID = data.images_ids;
	if(user.id == data.vendeur){
		con.query('INSERT INTO annonce SET titre = ? ,description = ? ,prix = ?,vendeur = ?,acheteur = ?,disponible = ?,location = ?,renforcer = ?'
		, [data.titre, data.description,data.prix,data.vendeur, data.acheteur, data.disponible, data.location, data.renforcer]
		, function (err, result) {
			if(err) throw err;
			let idAnnonce = result.insertId 
			let sql = 'INSERT INTO `image_annonce_link`(`annonce`, `image`) VALUES ';
			for(let i = 0, max = imagesID.length; i < max ; i++){
				sql += '(' + idAnnonce + ',' + imagesID[i] + ')';
				if(i+1 < max){
					sql += ", ";
				}
			}
			con.query(sql, function(req, res){
				if(err) throw err;
				
			});
			res.json({id : idAnnonce});
		});
	} else {
		res.json({error:"wrongVendeur"});
	}
});

app.post('/annonce/update', authenticateJWT , function (req, res) {
	let user = req.user;
	let data = req.body;
	if(user.id == data.vendeur){
		con.query('UPDATE annonce SET titre = ? ,description = ? ,prix = ?,vendeur = ?,acheteur = ?,disponible = ?,location = ?,renforcer = ? WHERE id = ? ' 
		, [data.titre, data.description,data.prix,data.vendeur, data.acheteur, data.disponible, data.location, data.renforcer, data.id]
		, function (err, result) {
			if(err) throw err;
			res.json({result : "OK"});
		});
	} else {
		res.json({error:"wrongVendeur"});
	}
});
app.get('/annonce/visite/:idAnnonce/:horodatage/:idClient', function (req, res) {
	let idAnnonce = parseInt(req.params.idAnnonce);
	let horodatage = req.params.horodatage;
	let idClient = parseInt(req.params.idClient);
	
	if(idAnnonce >= 0){
		if(idClient >= 0){
			con.query('INSERT INTO visite SET id_annonce = ' + idAnnonce +', id_client = '+ idClient + ', horodatage = ' + formatSQL(horodatage) + ', localisation = null' , function(err, result){
				if(err) throw err;
				res.json({id : result.insertId});
			});
		} else {
			con.query('INSERT INTO visite SET id_annonce = ' + idAnnonce +', id_client = null, horodatage = ' + formatSQL(horodatage) + ', localisation = null' , function(err, result){
				if(err) throw err;
				res.json({id : result.insertId});
			});
		}
	} else {
		res.json({error : "WrongID"});
	}
});

app.get('/annonce/getVisites/:idAnnonce', authenticateJWT, function(req, res){
	let idAnnonce = parseInt(req.params.idAnnonce);
	let idUser = req.user.id;

	if(idAnnonce >= 0){
		con.query('SELECT * FROM visite WHERE id_annonce = ' + idAnnonce + ' AND id_annonce IN (SELECT id FROM annonce WHERE vendeur = ' + idUser + ')' , function(err, result){
			if(err) throw err;
			res.json({result : result});
		});
	} else {
		res.json({error : "WrongID"});
	}
});


// --- CLIENTS ---

app.get('/client/:id',  function (req, res) {
	let idVal = parseInt(req.params.id);
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
	if(username.length > 0 && password.length > 0){
		con.query("SELECT * FROM client WHERE email = " + username + " AND mot_de_passe = " + password + " LIMIT 1", function (err, result) {
			if (err) throw err;
			if(result.length > 0){
				let utilisateur = result[0];
				const token = jwt.sign({
					id: utilisateur.id,
					username: utilisateur.email
				}, access.secret, { expiresIn: '1 hours' });
				res.json({user : utilisateur, access_token: token});
			} else {
				res.json({error:"unknown"});
			}
		});
	} else {
		res.json({error:"NoNameOrPassword"});
	}
});

app.post('/inscription/:pro', function(req,res){
	let obj = req.body;
	let email = con.escape(obj.email);
	let pro = parseInt(req.params.pro)
	con.query("SELECT id FROM client WHERE email = " + email + " LIMIT 1", function (err, result) {
		if(err) throw err;
		if(result.length <= 0){
			let password = "0x" + obj.mot_de_passe;
			let nom = con.escape(obj.nom);
			let prenom = con.escape(obj.prenom);
			let date_naissance = con.escape(obj.date_naissance);
			let telephone = con.escape(obj.telephone);
			let adresse = con.escape(obj.adresse);
			let idImage = parseInt(obj.image_id);
			let imageValue = "" + idImage;
			if(idImage < 0){
				imageValue = "null";
			}
			
			con.query("INSERT INTO `client`(`id`, `email`, `mot_de_passe`, `nom`, `prenom`, `telephone`, `date_naissance`, `photo`, `adresse`, `pro`)"
				+ "VALUES (null," + email + "," + password +"," + nom +"," + prenom+"," +telephone+"," + date_naissance+"," + imageValue + "," + adresse+"," + pro + ")"
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

app.post('/update/client', authenticateJWT, function(req, res){
	let id = req.user.id;
	let client = req.body;
	con.query("SELECT photo FROM client WHERE id = " + id, function(err, result){
		if(err) throw err;
		if(result.length > 0) {
			if(client.image_id >= -1){
				if(result[0].photo != client.image_id){
					con.query('SELECT * FROM image AS img WHERE img.id IN (SELECT photo FROM client AS c WHERE c.id = ' + id + ')' , function(err, result){
						if(err) throw err;
						if(result.length > 0){
							fs.unlinkSync(__dirname + "/" + result[0].url);
						} 
					});
				}
			}
			con.query('UPDATE client SET nom = ' +  formatSQL(client.nom) + ', prenom = '+ formatSQL(client.prenom) + ', telephone = ' +  formatSQL(client.telephone) + ', photo = ' +(client.image_id < 0 ? 'null' : client.image_id) + ', adresse = ' +   formatSQL(client.adresse) + ' WHERE id = ' + id, function(err, result){
				if(err) throw err;
				res.end();
			});
		}
	});
});

// --- FOLLOWS ---

app.get('/isFollowing/:idAnnonce', authenticateJWT, function(req, res){
	let idAnnonce = req.params.idAnnonce;	
	let idClient = req.user.id;
	con.query("SELECT COUNT(id_annonce) AS following FROM follow WHERE id_annonce = " + idAnnonce + " AND id_client = " + idClient, function(err, result){
		if(err) throw err;
		res.json(result[0]);
	});
});

app.get('/follow/:idAnnonce', authenticateJWT , function(req, res){
	let idAnnonce = req.params.idAnnonce;	
	let idClient = req.user.id;
	con.query("INSERT INTO `follow`(`id_client`, `id_annonce`) VALUES (" + idClient + "," + idAnnonce + ")", function(err, result){
		if(err) throw err;
		res.end();
	});
});

app.get('/unfollow/:idAnnonce', authenticateJWT , function(req, res){
	let idAnnonce = req.params.idAnnonce;	
	let idClient = req.user.id;
	con.query("DELETE FROM `follow` WHERE id_client = " + idClient+ " AND id_annonce = " + idAnnonce, function(err, result){
		if(err) throw err;
		res.end();
	});
});

// --- BUY ---

app.get('/buy/:idAnnonce', authenticateJWT , function(req, res){
	let idAnnonce = req.params.idAnnonce;	
	let idClient = req.user.id;
	con.query("UPDATE annonce SET acheteur = " + idClient + " WHERE id = " + idAnnonce, function(err, result){
		if(err) throw err;
		res.end();
	});
});

// --- Subscribe ---

app.get('/subscribe', authenticateJWT , function(req, res){
	let idClient = req.user.id;
	con.query("UPDATE client SET abonner = 1 WHERE id = " + idClient, function(err, result){
		if(err) throw err;
		res.end();
	});
});

app.get('/unsubscribe', authenticateJWT , function(req, res){
	let idClient = req.user.id;
	con.query("UPDATE client SET abonner = 0 WHERE id = " + idClient, function(err, result){
		if(err) throw err;
		res.end();
	});
});


con.connect(function (err) {
    if (err) throw err;
    console.log("Connected!");
});

