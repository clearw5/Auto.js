module.exports = function (runtime, scope) {
    function plugins(){
    }

    plugins.load = function(packageName){
        var plugin = runtime.plugins.load(packageName);
        var index = require(plugin.mainScriptPath);
        return index(plugin.unwrap());
    }

    return plugins;
}