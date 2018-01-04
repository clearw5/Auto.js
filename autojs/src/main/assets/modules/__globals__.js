
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

    scope.isShuttingDown = scope.isShopped;

    scope.notStopped = function(){
        return !isStopped();
    }

    scope.isRunning = scope.notStopped;

    scope.exit = __runtime__.exit.bind(__runtime__);


    scope.stop = scope.exit;

    scope.setClip = function(text){
        __runtime__.setClip(text);
    }

    scope.getClip = function(text){
       return __runtime__.getClip();
    }

    scope.currentPackage = function(){
        scope.auto();
        return __runtime__.info.getLatestPackage();
    }

    scope.currentActivity = function(){
        scope.auto();
        return __runtime__.info.getLatestActivity();
    }

    scope.waitForActivity = function(activity, period){
        period = period || 200;
        while(scope.currentActivity() != activity){
            sleep(period);
        }
    }

    scope.waitForPackage = function(packageName, period){
        period = period || 200;
        while(scope.currentPackage() != packageName){
            sleep(period);
        }
    }

    scope.random = function(min, max){
        if(arguments.length == 0){
            return Math.random();
        }
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    scope.setScreenMetrics = __runtime__.setScreenMetrics.bind(__runtime__);

}