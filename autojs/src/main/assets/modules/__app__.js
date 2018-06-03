
module.exports = function(runtime, global){
    importClass(android.content.Intent);
    var app = Object.create(runtime.app);
    var context = global.context;

    app.intent = function(i) {
      var intent = new android.content.Intent();
      if (i.className && i.packageName) {
          intent.setClassName(i.packageName, i.className);
      }
      if (i.extras) {
          for (var key in i.extras) {
              intent.putExtra(key, i.extras[key]);
          }
      }
      if (i.category) {
          if(i.category instanceof Array){
              for(var j = 0; i < i.category.length; j++){
                  intent.addCategory(i.category[j]);
              }
          }else{
              intent.addCategory(i.category);
          }
      }
      if (i.action) {
          if(i.action.indexOf(".") == -1){
              i.action = "android.intent.action." + i.action;
          }
          intent.setAction(i.action);
      }
      if (i.type) {
          if(i.data){
              intent.setDataAndType(android.net.Uri.parse(i.data), i.type);
          }else{
              intent.setType(i.type);
          }
      }else if (i.data) {
          intent.setData(android.net.Uri.parse(i.data));
      }
      return intent;
    }

    app.startActivity = function(i){
        if(typeof(i) == "string"){
            if(runtime.getProperty("class." + i)){
                context.startActivity(new Intent(context, runtime.getProperty("class." + i))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                return;
            }else{
                throw new Error("class " + i + " not found");
            }
        }
        context.startActivity(app.intent(i).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    app.sendBroadcast = function(i){
        context.sendBroadcast(app.intent(i));
    }

    app.sendEmail = function(options){
        options = options || {};
        var i = new Intent(Intent.ACTION_SENDTO);
        if(options.email){
            i.putExtra(Intent.EXTRA_EMAIL, toArray(options.email));
        }
        if(options.cc){
            i.putExtra(Intent.EXTRA_CC, toArray(options.cc));
        }
        if(options.bcc){
            i.putExtra(Intent.EXTRA_BCC, toArray(options.bcc));
        }
        if(options.subject){
            i.putExtra(Intent.EXTRA_SUBJECT, options.subject);
        }
        if(options.text){
            i.putExtra(Intent.EXTRA_TEXT, options.text);
        }
        if(options.attachment){
            i.putExtra(Intent.EXTRA_STREAM, android.content.Uri.parse("file://" + options.attachment));
        }
        context.startActivity(Intent.createChooser(i, "发送邮件").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    function toArray(arg){
        if(typeof(arg) == 'string'){
            return [arg];
        }
        return arg;
    }

    app.launch = app.launchPackage;

    app.versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
    app.versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;

    app.autojs = {
        versionCode: org.autojs.autojs.BuildConfig.VERSION_CODE,
        versionName: org.autojs.autojs.BuildConfig.VERSION_NAME
    };

    global.__asGlobal__(app, ['launchPackage', 'launch', 'launchApp', 'getPackageName', 'getAppName', 'openAppSetting']);

    return app;
}

