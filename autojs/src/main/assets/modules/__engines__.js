
module.exports = function(__runtime__, scope){
    var rtEngines = __runtime__.engines;

    var engines = {};

    engines.execScript = function(name, script, config){
        config = fillConfig(config);
        return rtEngines.execScript(name, script, config);
    }

    engines.execScriptFile = function(path, config){
        config = fillConfig(config);
        return rtEngines.execScriptFile(path, config);
    }

    engines.execAutoFile = function(path, config){
        config = fillConfig(config);
        return rtEngines.execAutoFile(path, config);
    }

    engines.stopAll = rtEngines.stopAll.bind(rtEngines);
    engines.stopAllAndToast = rtEngines.stopAllAndToast.bind(rtEngines);

    function fillConfig(c){
        var config = new com.stardust.autojs.execution.ExecutionConfig();
        c = c || {};
        if(c.path){
            if(typeof(c.path) == "string"){
                config.path([c.path]);
            }else{
                config.path(c.path);
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