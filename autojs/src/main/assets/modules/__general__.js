
module.exports = function(__runtime__, scope){
    scope.toast = function(text){
        __runtime__.toast(text);
    }

    scope.toastLog = function(text){
        __runtime__.toast(text);
        scope.log(text);
    }

    scope.sleep = __runtime__.sleep.bind(__runtime__);

    scope.isStopped = function(){
        return __runtime__.isStopped();
    }

    scope.notStopped = function(){
        return !isStopped();
    }

    scope.exit = function(){
        __runtime__.exit();
    }

    scope.stop = scope.exit;

    scope.setClip = function(text){
        __runtime__.setClip(text);
    }

    scope.getClip = function(text){
       return __runtime__.getClip();
    }

    scope.currentPackage = function(){
        return __runtime__.info.getLatestPackage();
    }

    scope.currentActivity = function(){
        return __runtime__.info.getLatestActivity();
    }

    scope.waitForActivity = function(activity, delay){
        delay = delay || 200;
        while(scope.currentActivity() != activity){
            sleep(delay);
        }
    }

    scope.waitForPackage = function(packageName, delay){
        delay = delay || 200;
        while(scope.currentPackage() != packageName){
            sleep(delay);
        }
    }

    scope.setScreenMetrics = __runtime__.setScreenMetrics.bind(__runtime__);
}