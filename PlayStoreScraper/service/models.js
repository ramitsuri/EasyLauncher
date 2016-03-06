var mongoose = require('mongoose');
var schema = mongoose.Schema;
var appDataSchema = schema({
  packageName: String,
  tags: []
});

mongoose.connect('mongodb://localhost:27017/playStore')
var appData = mongoose.model('apps', appDataSchema);
exports.appData = appData;
