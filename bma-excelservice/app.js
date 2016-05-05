var express = require('express');
var nconf = require('nconf');
var sendgrid;
var excelbuilder = require('msexcel-builder');
var app = express();
var fs = require("fs");

app.get('/createExcel', function (req, res) {
  createExcelFile('excel/output.xlsx');
  sendEmail();
  res.send('OK');
})

app.get('/test', function (req, res) {
  res.send('TEST OK');
})

function createExcelFile(filename){

  var workbook = excelbuilder.createWorkbook('./', filename)

  // Create a new worksheet with 10 columns and 12 rows
  var sheet1 = workbook.createSheet('sheet1', 10, 12);

  // Fill some data
  sheet1.set(1, 1, 'I am title');
  for (var i = 2; i < 5; i++)
    sheet1.set(i, 1, 'test'+i);

  // Save it
  workbook.save(function(ok){
    if (!ok)
      workbook.cancel();
    else
      console.log('congratulations, your workbook created');
  });
}

function sendEmail(){
  var email = new sendgrid.Email({
    to: 'chrisvugrinec@gmail.com',
    from: 'bma-mailer@microsoft-azuregurus.nl',
    subject: 'test mail',
    text: 'This is a sample email message.',
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

app.set('port', process.env.PORT || 3000);
var server = app.listen(app.get('port'), function () {

  var host = server.address().address
  var port = server.address().port
  nconf.file({ file: 'config-prod.json' });
  sendgrid = require('sendgrid')(nconf.get('sendgrid_username'),nconf.get('sendgrid_password'));
  console.log("Example app listening at http://%s:%s", host, port)
})
