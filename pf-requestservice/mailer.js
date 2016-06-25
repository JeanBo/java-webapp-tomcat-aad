var sendgrid;
var nconf = require('nconf');
var fs = require('fs');

nconf.file({ file: './config/config-prod.json' });
sendgrid = require('sendgrid')(nconf.get('sendgrid_username'),nconf.get('sendgrid_password'));

module.exports = {
  send: function (filenamex) {
    var email = new sendgrid.Email({
      to: 'chrisvugrinec@gmail.com',
      from: 'infraprovision@vmgurus.nl',
      subject: 'infra request',
      text: 'An infra request has been submitted',
      files: [
        {
          path: 'requests/'+filenamex
        }
      ],
    });

    sendgrid.send(email, function(err, json){
      if(err) { return console.error(err); }
      console.log(json);
    });
  }

};

