
module.exports = function(__runtime__, scope){
    scope.toast = function(text){
        __runtime__.toast(text);
    }

    scope.toastLog = function(text){
        __runtime__.toast(text);
        scope.log(text);
    }

    scope.sleep = function(millis){
        __runtime__.sleep(millis);
    }

    scope.isStopped = function(){
        return __runtime__.isStopped();
    }

    scope.notStopped = function(){
        return !isStopped();
    }

    scope.stop = function(){
        __runtime__.stop();
    }

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