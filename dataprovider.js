var Db = require('mongodb').Db;
var Connection = require('mongodb').Connection;
var Server = require('mongodb').Server;
var BSON = require('mongodb').BSON;
var ObjectID = require('mongodb').ObjectID;

ApartmentProvider = function(host, port) {
  this.db= new Db('magnolia', new Server(host, port, {auto_reconnect: true}, {}));
  this.db.open(function(){});
};


ApartmentProvider.prototype.getCollection= function(callback) {
  this.db.collection('apartmentData', function(error, article_collection) {
    if( error ) callback(error);
    else callback(null, article_collection);
  });
};

ApartmentProvider.prototype.findAll = function(callback) {
    this.getCollection(function(error, article_collection) {
      if( error ) callback(error)
      else {
        article_collection.find().toArray(function(error, results) {
          if( error ) callback(error)
          else callback(null, results)
        });
      }
    });
};


ApartmentProvider.prototype.findById = function(id, callback) {
    this.getCollection(function(error, article_collection) {
      if( error ) callback(error)
      else {
        article_collection.findOne({"flatnumber":id}, function(error, result) {
          if( error ) {
			  console.log(error);
		  }
          else {
			  callback(null, result)
		  }
        });
      }
    });
};

// update an employee
ApartmentProvider.prototype.update = function(apartment, callback) {
    this.getCollection(function(error, article_collection) {
      if( error ) callback(error);
      else {
					article_collection.update( {flatnumber:apartment.flatnumber},
											{$set: {
													gasKYC:apartment.gasKYC,
													status:apartment.status,
													propertyTax:apartment.propertyTax,
													owner: { name: apartment.owner.name,
															 address: apartment.owner.address,
															 emails: [apartment.owner.emails[0],apartment.owner.emails[1]],
															 phones: [apartment.owner.phones[0],apartment.owner.phones[1]]
													},
													tenant: {}
												}
											},
                                        function(error, result) {
                                                if(error) {
													console.log(error);
												}
                                                else {
													console.log("Record updated");
													callback(null, result);       
												}
                                        });
					//update tenant data 
					if (apartment.status=="2"){		
								article_collection.update( {flatnumber:apartment.flatnumber},
											{$set: {
													tenant: { name: apartment.tenant.name,
															  address: apartment.tenant.address,
															  policeverification: apartment.tenant.policeverification,
															  agreement : apartment.tenant.agreement,
															  registration : apartment.tenant.registration,
															  emails: [apartment.tenant.emails[0]],
															  phones: [apartment.tenant.phones[0]]
													},
													tenant: {}
												}
											},
                                        function(error, result) {
                                                if(error) {
													console.log(error);
												}
                                                else {
													console.log("Record updated");
													callback(null, result);       
												}
                                        });
					}


      }
    });

};


  this.updateUser = function(user, cb) {

  };


ApartmentProvider.prototype.save = function(articles, callback) {
    this.getCollection(function(error, article_collection) {
      if( error ) callback(error)
      else {
        if( typeof(articles.length)=="undefined")
          articles = [articles];

        for( var i =0;i< articles.length;i++ ) {
          article = articles[i];
          article.created_at = new Date();
          if( article.comments === undefined ) article.comments = [];
          for(var j =0;j< article.comments.length; j++) {
            article.comments[j].created_at = new Date();
          }
        }

        article_collection.insert(articles, function() {
          callback(null, articles);
        });
      }
    });
};

exports.ApartmentProvider = ApartmentProvider;