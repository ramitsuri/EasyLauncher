var express = require('express');
var app = express();
var port = process.env.PORT || 8080;
require('./service/routes.js')(app);
app.listen(port);
console.log('Server running on 8080');
