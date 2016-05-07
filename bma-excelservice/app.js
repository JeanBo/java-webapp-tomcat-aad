var express = require('express');
var mailer = require('./mailer')
var excelbuilder = require('./excelbuilder')
var app = express();


app.get('/sendExcel/:mailto', function (req, res) {
  excelbuilder.createExcelFile('./excel/output.xlsx');
  setTimeout(function() {
    mailer.sendEmail(req.params.mailto);
  }, 3000);
  res.send('OK');
})

app.get('/test', function (req, res) {
  res.send('TEST OK');
})


app.set('port', process.env.PORT || 3000);
var server = app.listen(app.get('port'), function () {

  var host = server.address().address
  var port = server.address().port
  console.log("BizMilesApp listening at http://%s:%s", host, port)
})
