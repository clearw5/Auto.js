
module.exports = function (runtime, global) {
    importClass(android.content.Intent);
    var app = Object.create(runtime.app);
    var context = global.context;

    app.intent = function (i) {
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
            if (i.category instanceof Array) {
                for (var j = 0; i < i.category.length; j++) {
                    intent.addCategory(i.category[j]);
                }
            } else {
                intent.addCategory(i.category);
            }
        }
        if (i.action) {
            if (i.action.indexOf(".") == -1) {
                i.action = "android.intent.action." + i.action;
            }
            intent.setAction(i.action);
        }
        if (i.flags) {
            let flags = 0;
            if (Array.isArray(i.flags)) {
                for (let j = 0; j < i.flags.length; j++) {
                    flags |= parseIntentFlag(i.flags[j]);
                }
            } else {
                flags = parseIntentFlag(i.flags);
            }
            intent.setFlags(flags);
        }
        if (i.type) {
            if (i.data) {
                intent.setDataAndType(app.parseUri(i.data), i.type);
            } else {
                intent.setType(i.type);
            }
        } else if (i.data) {
            intent.setData(android.net.Uri.parse(i.data));
        }
        return intent;
    }

    app.startActivity = function (i) {
        if (typeof (i) == "string") {
            if (runtime.getProperty("class." + i)) {
                context.startActivity(new Intent(context, runtime.getProperty("class." + i))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                return;
            } else {
                throw new Error("class " + i + " not found");
            }
        }
        if(i instanceof android.content.Intent){
            context.startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return;
        }
        if(i && i.root) {
            shell("am start " + app.intentToShell(i), true);
        }else{
            context.startActivity(app.intent(i).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }

    app.sendBroadcast = function (i) {
        if (typeof (i) == "string") {
            if (runtime.getProperty("broadcast." + i)) {
                app.sendLocalBroadcastSync(app.intent({ action: runtime.getProperty("broadcast." + i) }));
            }
        }
        if(i && i.root) {
            shell("am broadcast " + app.intentToShell(i), true);
        }else{
            context.sendBroadcast(app.intent(i));
        }
    }

    app.startService = function(i) {
        if(i && i.root) {
            shell("am startservice " + app.intentToShell(i), true);
        }else{
            context.startService(app.intent(i));
        }
    }

    app.sendEmail = function (options) {
        options = options || {};
        var i = new Intent(Intent.ACTION_SEND);
        if (options.email) {
            i.putExtra(Intent.EXTRA_EMAIL, toArray(options.email));
        }
        if (options.cc) {
            i.putExtra(Intent.EXTRA_CC, toArray(options.cc));
        }
        if (options.bcc) {
            i.putExtra(Intent.EXTRA_BCC, toArray(options.bcc));
        }
        if (options.subject) {
            i.putExtra(Intent.EXTRA_SUBJECT, options.subject);
        }
        if (options.text) {
            i.putExtra(Intent.EXTRA_TEXT, options.text);
        }
        if (options.attachment) {
            i.putExtra(Intent.EXTRA_STREAM, app.parseUri(options.attachment));
        }
        i.setType("message/rfc822");
        context.startActivity(Intent.createChooser(i, "发送邮件").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    function toArray(arg) {
        if (typeof (arg) == 'string') {
            arg = [arg];
        }
        let arr = util.java.array("string", arg.length);
        for (let i = 0; i < arg.length; i++) {
            arr[i] = arg;
        }
        return arr;
    }

    app.parseUri = function (uri) {
        if (uri.startsWith("file://")) {
            return app.getUriForFile(uri);
        }
        return android.net.Uri.parse(uri);
    }

    app.getUriForFile = function (path) {
        if (path.startsWith("file://")) {
            path = path.substring(7);
        }
        let file = new java.io.File(files.path(path));
        if (app.fileProviderAuthority == null) {
            return android.net.Uri.fromFile(file);
        }
        return Packages["androidx"].core.content.FileProvider.getUriForFile(context,
            app.fileProviderAuthority, file);
    };

    app.launch = app.launchPackage;

    app.versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
    app.versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;

    app.autojs = {
        versionCode: org.autojs.autojs.BuildConfig.VERSION_CODE,
        versionName: org.autojs.autojs.BuildConfig.VERSION_NAME
    };

    app.intentToShell = function(i) {
        var cmd = "";
        function quoteStr(str) {
            return "'" + str.replace("'", "\\'") + "'";
        }
        function isInt(value) {
            return Number.isInteger(value) && value <= java.lang.Integer.MAX_VALUE && value >= java.lang.Integer.MIN_VALUE;
        }
        function typeChar(value){
            if(typeof(value) == 'boolean'){
                return 'z';
            }
            if(typeof(value) == 'number'){
                if(Number.isInteger(value)){
                    if(isInt(value)){
                        return 'i';
                    }else{
                        return 'l';
                    }
                }else{
                    return 'f';
                }
            }
            throw new TypeError("unknown type: " + value);
        }
        function addOption(option, param, quote) {
            if(quote == undefined || quote === true){
                param = quoteStr(param);
            }
            cmd += " -" + option + " " + param;
        }
        if (i.className && i.packageName) {
           addOption("n", i.packageName + "/" + i.className);
        }
        if (i.extras) {
            for (var key in i.extras) {
                let value = i.extras[key];
                if(typeof(value) == 'string'){
                    addOption("-es",  quoteStr(key) + ' ' + quoteStr(value), false);
                }else if(Array.isArray(value)){
                    if(value.length == 0){
                        throw new Error('Empty array: ' + key);
                    }
                    var e = value[0];
                    if(typeof(e) == 'string'){
                        cmd += ' --esa ' + quoteStr(key) + ' ';
                        for(let str of value){
                            cmd += quoteStr(str) + ',';
                        }
                        cmd = cmd.substring(0, cmd.length - 1);
                    }else{
                        addOption('-e' + typeChar(e) + 'a', quoteStr(key) + ' ' + value, false);
                    }
                }else {
                    addOption('-e' + typeChar(value), quoteStr(key) + ' ' + value, false);
                }
            }
        }
        if (i.category) {
            if (i.category instanceof Array) {
                for (var j = 0; i < i.category.length; j++) {
                    addOption('c', i.category[j], false);
                }
            } else {
                addOption('c', i.category, false);
            }
        }
        if (i.action) {
            if (i.action.indexOf(".") == -1) {
                i.action = "android.intent.action." + i.action;
            }
            addOption('a', i.action);
        }
        if (i.flags) {
            let flags = 0;
            if (Array.isArray(i.flags)) {
                for (let j = 0; j < i.flags.length; j++) {
                    flags |= parseIntentFlag(i.flags[j]);
                }
            } else {
                flags = parseIntentFlag(i.flags);
            }
            addOption('f', flags, false);
        }
        if (i.type) {
            addOption('t', i.type, false);
        }
        if (i.data) {
            addOption('d', i.data, false);
        }
        return cmd;
    }

    

    global.__asGlobal__(app, ['launchPackage', 'launch', 'launchApp', 'getPackageName', 'getAppName', 'openAppSetting']);

    function parseIntentFlag(flag) {
        if (typeof (flag) == 'string') {
            return android.content.Intent["FLAG_" + flag.toUpperCase()];
        }
        return flag;
    }

    return app;
}

