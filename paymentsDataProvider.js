var Db = require('mongodb').Db;
var Connection = require('mongodb').Connection;
var Server = require('mongodb').Server;
var BSON = require('mongodb').BSON;
var ObjectID = require('mongodb').ObjectID;

PaymentProvider = function(host, port) {
  this.db= new Db('magnolia', new Server(host, port, {auto_reconnect: true}, {}));
  this.db.open(function(){});
};


PaymentProvider.prototype.getCollection= function(callback) {
  this.db.collection('paymentData', function(error, payment_collection) {
    if( error ) callback(error);
    else callback(null, payment_collection);
  });
};

PaymentProvider.prototype.findAll = function(callback) {
    this.getCollection(function(error, payment_collection) {
      if( error ) callback(error)
      else {
        payment_collection.find().toArray(function(error, results) {
          if( error ) callback(error)
          else callback(null, results)
        });
      }
    });
};


PaymentProvider.prototype.add = function(payment, callback) {
    this.getCollection(function(error, payment_collection) {
      if( error ) callback(error)
      else {
        payment_collection.insert(payment, function() {
          callback(null, payment);        });

      }
    });
};

PaymentProvider.prototype.delete = function(id, callback) {
    this.getCollection(function(error, payment_collection) {
      if( error ) callback(error)
      else {
        payment_collection.remove({"_id": payment_collection.db.bson_serializer.ObjectID.createFromHexString(id)}, function(error, result) {
          if( error ) {
			  console.log(error);
		  }
          else {
			 callback(null,result)
		  }
        });
      }
    });
};


exports.PaymentProvider = PaymentProvider;