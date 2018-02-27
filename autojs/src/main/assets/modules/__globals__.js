
module.exports = function(runtime, global){
    global.toast = function(text){
        runtime.toast(text);
    }

    global.toastLog = function(text){
        runtime.toast(text);
        global.log(text);
    }

    global.sleep = runtime.sleep.bind(runtime);

    global.isStopped = function(){
        return runtime.isStopped();
    }

    global.isShuttingDown = global.isShopped;

    global.notStopped = function(){
        return !isStopped();
    }

    global.isRunning = global.notStopped;

    global.exit = runtime.exit.bind(runtime);


    global.stop = global.exit;

    global.setClip = function(text){
        runtime.setClip(text);
    }

    global.getClip = function(text){
       return runtime.getClip();
    }

    global.currentPackage = function(){
        global.auto();
        return runtime.info.getLatestPackage();
    }

    global.currentActivity = function(){
        global.auto();
        return runtime.info.getLatestActivity();
    }

    global.waitForActivity = function(activity, period){
        period = period || 200;
        while(global.currentActivity() != activity){
            sleep(period);
        }
    }

    global.waitForPackage = function(packageName, period){
        period = period || 200;
        while(global.currentPackage() != packageName){
            sleep(period);
        }
    }

    global.random = function(min, max){
        if(arguments.length == 0){
            return Math.random();
        }
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    global.setScreenMetrics = runtime.setScreenMetrics.bind(runtime);

}