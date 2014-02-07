var moment = require('moment');
var ES = require('./email-settings');
var EM = {};
module.exports = EM;

EM.server = require("emailjs/email").server.connect({

	host 	    : ES.host,
	user 	    : ES.user,
	password    : ES.password,
	ssl		    : true

});

EM.sendPaymentConfirmation = function(account, callback) {
	EM.server.send({
		from         : ES.sender,
		to           : account.email,
		subject      : 'Magnolia Maintenance Payment received for ' + account.flatnumber,
		text         : 'something went wrong... :(',
		attachment   : EM.composeEmail(account)
	}, callback );
}

EM.composeEmail = function(o) {
	var html = "<html><body>";
		html += "Dear Member, <br><br>";
		html += "We acknowledge receipt of your Maintenance payment of Rs." + o.amount 
		html += " received on " + moment(o.date).format('LL') 
		html += ", towards maintenance contribution of the flat " + o.flatnumber + " at Magnolia.</b><br><br>";
		html += "Cheque payments are subject to realization.<br><br>";
		html += "Thank you<br><br>";
		html += "</body></html>";
	return  [{data:html, alternative:true}];
}