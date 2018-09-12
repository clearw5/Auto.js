
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
        var engine = Object.create(rtEngines.myEngine());
        if(!execArgv){
            execArgv = {};
            var iter = engine.getTag("execution.config").getArguments().entrySet().iterator();
            while(iter.hasNext()){
                var entry = iter.next();
                execArgv[entry.getKey()] = entry.getValue();
            }
        }
        engine.execArgv = execArgv;
        return engine;
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

    return engines;
}