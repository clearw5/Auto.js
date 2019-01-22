
module.exports = function(__runtime__, scope){
    var rtEngines = __runtime__.engines;
    var execArgv = null;

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
           config.workingDirectory = c.path;
        }
        config.delay = c.delay || 0;
        config.interval = c.interval || 0;
        config.loopTimes = (c.loopTimes === undefined)? 1 : c.loopTimes;
        if(c.arguments){
            var arguments = c.arguments;
            for(var key in arguments){
                if(arguments.hasOwnProperty(key)){
                    config.setArgument(key, arguments[key]);
                }
            }
        }
        return config;
    }

    var engine = engines.myEngine();
    var execArgv = {};
    var iterator = engine.getTag("execution.config").arguments.entrySet().iterator();
    while(iterator.hasNext()){
        var entry = iterator.next();
        execArgv[entry.getKey()] = entry.getValue();
    }
    engine.execArgv = execArgv;

    return engines;
}