var express = require('express');
var mailer = require('./mailer')
var moment = require('moment')
var bodyParser = require("body-parser");
var fs = require('fs');

var app = express();
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
var wrapped = moment(new Date()); 

function writetofile(req,res,filename){

  var stream = fs.createWriteStream("requests/"+filename);
  stream.once('open', function(fd) {
    console.log("request created, file: requests/"+filename);
    stream.write('{\n');
    stream.write('\"email\" : \"' + req.body.email +'\",\n');
    stream.write('\"app\" : \"'+ req.body.name +'\",\n');
    stream.write('\"image\" : \"' + req.body.image +'\",\n');
    stream.write('\"amount\" : \"' + req.body.nr + '\"\n');
    stream.write('}');
    stream.end();
  });

}

app.post('/sendRequest', function (req, res) {
  res.redirect('http://google.com');
  var filename = "request-"+wrapped+".json";
  writetofile(req,res,filename);
  setTimeout(function() {
     mailer.send(filename);
  },2000);
})


app.set('port', process.env.PORT || 3000);
var server = app.listen(app.get('port'), function () {

  var host = server.address().address
  var port = server.address().port
  console.log("Listening at http://%s:%s", host, port)
})
