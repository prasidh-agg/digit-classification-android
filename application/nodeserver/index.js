const express = require("express")
var bodyParser = require('body-parser')
const fileUpload = require('express-fileupload');
const fs = require("fs");


var app = express()
app.use(bodyParser.urlencoded({ extended: false }))
app.use(fileUpload());


app.get("/",function(request,response){
response.sendFile(__dirname+"/views/home.html");
})

app.post("/uploadFile",function(request,response){

    console.log(request.files.choosefile);
    let imagefile;
    let uploadpath
    let categoryvalue;

    imagefile = request.files.choosefile;
    categoryvalue = request.body.categoryvalue;
    categoryvalue = categoryvalue.toUpperCase();

    if (!fs.existsSync(__dirname+"/Uploads/"+categoryvalue.toUpperCase())) {
      fs.mkdirSync(__dirname+"/Uploads/"+categoryvalue.toUpperCase());
  }

    console.log("------------------------------------ "+categoryvalue);
    uploadpath = __dirname+"/Uploads/"+categoryvalue+"/"+categoryvalue+"_"+imagefile.name
    //console.log(request.files);

    imagefile.mv(uploadpath, function(err) {
        if (err)
          return response.status(500).send(err);
    
        response.send('File uploaded!');
      });
})

//Add local IP address here instead of '172.20.10.7'
app.listen(80||process.env.IP||'172.20.10.4', function () {
console.log("Started application on ")
});