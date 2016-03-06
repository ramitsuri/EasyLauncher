var mongoose = require ('mongoose');
var models = require('./models');
var keywordExtractor = require('keyword-extractor');
var gps = require('google-play-scraper');

var appData = models.appData;
var apps = [];

// get tags for a list of app packages
var getAppData = function(packageNames, callback){
  packageNames.forEach(function(packageName){
    gps.app({appId:packageName}).then(function(app,packageName){
      var tags = keywordExtractor.extract(app.description,{
                                                                  language:"english",
                                                                  remove_digits: true,
                                                                  return_changed_case:true,
                                                                  remove_duplicates: true
                                                             });

      var appData1 = new appData({
        packageName: app.appId,
        tags: tags
      });
      apps.push(appData1);
      //console.log(appData1);

      //console.log(app.appId);
      //apps.push(app);
      // console.log(tags);
  });
  //console.log(tags);

  });
callback({'apps':apps})
  apps  = [];
}

// get tags for one app package
var getSingleAppData = function(packageName, callback){
  var tags;
    gps.app({appId:packageName}).then(function(app){
      console.log(app.description);
      tags = keywordExtractor.extract(app.description,{
                                                                  language:"english",
                                                                  remove_digits: true,
                                                                  return_changed_case:true,
                                                                  remove_duplicates: true
                                                             });
      console.log(tags);
      callback({'tags':tags})
  });


}

module.exports = {
  getAppData: getAppData,
  getSingleAppData: getSingleAppData
}
