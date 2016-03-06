var request = require('request');
var requests = require('./requests');
var bodyParser = require('body-parser');

module.exports = function(app){
app.use(bodyParser.json());

app.post('/apps/details', function(request, response){
  console.log(request.body);
    requests.getAppData(request.body.ids, function(tags){
      response.json(tags);
    });
    //console.log(request.body.ids[0]);
});

app.post('/app/details', function(request, response){
    requests.getSingleAppData(request.body.id, function(tags){
      response.json(tags);
    });
    //console.log(request.body.ids[0]);
});

app.post('/hello', function(request,response){
  response.json('{hello:hi}');
});

}
