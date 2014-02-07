var express = require('express');
var ApartmentProvider = require('./dataprovider').ApartmentProvider;
var PaymentProvider = require('./paymentsDataProvider').PaymentProvider;
var email = require('./modules/email-dispatcher');


var app = express();

app.use(express.static(__dirname + '/public'));

app.configure('development', function(){
  app.use(express.errorHandler({ dumpExceptions: true, showStack: true })); 
  app.use(express.bodyParser());
});

app.configure('production', function(){
  app.use(express.errorHandler()); 
  app.use(express.bodyParser());
});

var apartmentProvider = new ApartmentProvider('localhost', 27017);
// Routes

app.get('/api/apartments', function(req, res){
    apartmentProvider.findAll( function(error,docs){
        res.send(docs);
    })
});

app.get('/api/apartments/:id', function(req, res) {
    apartmentProvider.findById(req.params.id, function(error, article) {
        res.send(article);
    });
});

app.post('/api/apartments', function(req, res) {
    var apartment = req.body;
	apartmentProvider.update(apartment, function(error, user) {
      if (error) {
        res.send(error, 404);
      } else {
        res.send('');
      }
    });    

});


var paymentProvider = new PaymentProvider('localhost', 27017);

app.get('/api/payments', function(req, res){
    paymentProvider.findAll( function(error,docs){
        res.send(docs);
    })
});

app.get('/api/payments/:id', function(req, res) {
    paymentProvider.findById(req.params.id, function(error, article) {
        res.send(article);
    });
});


app.delete('/api/payments/:id', function(req, res) {
    paymentProvider.delete(req.params.id, function(error, article) {
        res.send('ok', 200);
    });
});

app.post('/api/payments', function(req, res) {
    var payment = req.body;
	paymentProvider.add(payment, function(error, user) {
      if (error) {
        res.send(error, 404);
      } else {
	  email.sendPaymentConfirmation(payment, function(e, m){
			if (!e) {
				//	res.send('ok', 200);
			}	else{
					res.send('email-server-error', 400);
					for (k in e) console.log('error : ', k, e[k]);
			}
	   });
       res.send('');
      }
    });  
});

app.listen(3000);
console.log("Express server listening on port 3000");