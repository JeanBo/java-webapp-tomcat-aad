var sendgrid;
var nconf = require('nconf');
nconf.file({ file: './config/config-prod.json' });
sendgrid = require('sendgrid')(nconf.get('sendgrid_username'),nconf.get('sendgrid_password'));

module.exports = {
  sendEmail: function (recipient) {
    console.log('sending out email to '+recipient);
    var email = new sendgrid.Email({
      to: recipient,
      from: 'bma-mailer@microsoft-azuregurus.nl',
      subject: 'Biz Miles Registration overview',
      text: 'Here is the overview you requested...',
      files: [
        {
          path: './excel/output.xlsx'
        }
      ],
    });

    sendgrid.send(email, function(err, json){
      if(err) { return console.error(err); }
      console.log(json);
    });
  }

};


