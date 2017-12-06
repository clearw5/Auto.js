
module.exports = function(__runtime__, scope){
    var rtConsole = __runtime__.console;
    var console = {};

    console.assert = function(value, message){
        message = message || "";
        rtConsole.assertTrue(value, message);
    }

    console.rawInput = rtConsole.rawInput.bind(rtConsole);

    console.input = function(data, param){
        return eval(console.rawInput.call(console, [].slice(arguments)) + "");
    }

    console.log = function(){
        rtConsole.log(util.format.apply(util, arguments));
    }

    console.verbose = function(){
        rtConsole.verbose(util.format.apply(util, arguments));
    }

    console.print = function(){
        rtConsole.print(android.util.Log.DEBUG, util.format.apply(util, arguments));
    }

    console.info = function(){
        rtConsole.info(util.format.apply(util, arguments));
    }

    console.warn = function(){
        rtConsole.warn(util.format.apply(util, arguments));
    }

    console.error = function(){
        rtConsole.error(util.format.apply(util, arguments));
    }

    console.show = rtConsole.show.bind(rtConsole);
    console.hide = rtConsole.hide.bind(rtConsole);
    console.clear = rtConsole.clear.bind(rtConsole);
    console.setSize = rtConsole.setSize.bind(rtConsole);
    console.setPosition = rtConsole.setPosition.bind(rtConsole);
    console.setTitle = rtConsole.setTitle.bind(rtConsole);

    scope.print = console.print.bind(console);
    scope.log = console.log.bind(console);
    scope.err = console.error.bind(console);
    scope.openConsole = console.show.bind(console);
    scope.clearConsole = console.clear.bind(console);

    return console;
}