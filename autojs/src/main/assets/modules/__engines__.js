
module.exports = function(__runtime__, scope){
    var rtEngines = __runtime__.engines;

    var engines = {};

    engines.execScript = function(name, script, config){
        return rtEngines.execScript(name, script, fillConfig(config));
    }

    engines.execScriptFile = function(path, config){
        return rtEngines.execScriptFile(path, fillConfig(config));
    }

    engines.execAutoFile = function(path, config){
        return rtEngines.execAutoFile(path, fillConfig(config));
    }

    engines.myEngine = function(){
        return rtEngines.myEngine();
    }

    engines.all = function(){
        return rtEngines.all();
    }

    engines.stopAll = rtEngines.stopAll.bind(rtEngines);
    engines.stopAllAndToast = rtEngines.stopAllAndToast.bind(rtEngines);

    function fillConfig(c){
        var config = new com.stardust.autojs.execution.ExecutionConfig();
        c = c || {};
        c.path = c.path || files.cwd();
        if(c.path){
            if(Array.isArray(c.path)){
                config.requirePath(c.path);
                config.executePath(c.path[0]);
            }else{
                config.requirePath([c.path]);
                config.executePath(c.path);
            }
        }
        c.delay = c.delay || 0;
        c.interval = c.interval || 0;
        c.loopTimes = c.loopTimes || 1;
        config.loop(c.delay, c.loopTimes, c.interval);
        return config;
    }

    return engines;
}