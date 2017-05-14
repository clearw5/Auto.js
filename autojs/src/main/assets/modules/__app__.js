
module.exports = function(__runtime__, scope){
    var app = Object.create(__runtime__.app);
    var context = scope.context;

    app.intent = function(i) {
      var intent = new android.content.Intent();
      if (i.className && i.packageName) {
          intent.setClassName(i.packageName, i.className);
      }
      if (i.extras) {
          for (var key in i.extras) {
              intent.putExtra(key, i.extras[key].toString());
          }
      }
      if (i.category) {
          for (var key in i.category) {
              intent.addCategory(key, i.category[key]);
          }
      }
      if (i.action) {
          intent.setAction(i.action);
      }
      if (i.type) {
          intent.setType(i.type);
      }
      if (i.data) {
          intent.setData(android.net.Uri.parse(i.data));
      }
      return intent;
    }

    app.startActivity = function(i){
        context.startActivity(app.intent(i).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    app.sendBroadcast = function(i){
        context.sendBroadcast(app.intent(i));
    }

    app.launch = app.launchPackage;

    scope.__asGlobal__(app, ['launchPackage', 'launch', 'launchApp', 'getPackageName', 'openAppSetting']);

    return app;
}

