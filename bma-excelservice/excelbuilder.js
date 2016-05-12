var excelbuilder = require('msexcel-builder');
var sql = require('mssql');
var nconf = require('nconf');
nconf.file({ file: './config/config-prod.json' });
var dateFormat = require('dateformat');
var fs = require('fs');


var config = {
    user: nconf.get('azure-ms_username'),
    password: nconf.get('azure-ms_password'),
    server: nconf.get('azure-ms_server'),
    database: nconf.get('azure-ms_database'),
    options: {
        encrypt: true // Use this if you're on Windows Azure
    }
}

module.exports = {
  createExcelFile: function (filename) {

  fs.exists(filename, function(exists) {
    if(exists) {
      fs.unlink(filename);
    }
  }
   
    var workbook = excelbuilder.createWorkbook('./', filename)

    // Create a new worksheet with 10 columns and 12 rows
    var sheet = workbook.createSheet('busines miles registration', 10, 1000);

    sheet.set(1, 1, 'start');
    sheet.set(2, 1, 'stop');
    sheet.set(3, 1, 'KM');
    sheet.set(4, 1, 'start_time');
    sheet.set(5, 1, 'stop_time');

    var rownumber=3;
    sql.connect(config).then(function() {
      console.log("connected to db")
      var request = new sql.Request();
      request.stream = true;
      request.query('select * from ride');

      request.on('row', function(row) {
        rownumber++
        sheet.set(1, rownumber, row.startaddress);
        sheet.set(2, rownumber, row.stopaddress);
        sheet.set(3, rownumber, Number((row.kilometers).toFixed(3)));
        sheet.set(4, rownumber, dateFormat(row.starttime, "yyyy-mm-dd h:MM:ss"));
        sheet.set(5, rownumber, dateFormat(row.stoptime, "yyyy-mm-dd h:MM:ss"));
      });

    }).catch(function(err) {
      console.log("error connecting to db, error: "+err)
    });

    setTimeout(function() {
      // Persist the excel file
      workbook.save(function(ok){
        if (!ok)
          workbook.cancel();
        else
          console.log('excel was created succesfully');
      });
    }, 3000);
  }

};


