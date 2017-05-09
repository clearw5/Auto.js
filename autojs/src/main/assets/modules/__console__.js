
module.exports = function(__runtime__, scope){
    var console = new Object(__runtime__.console);

    console.assert = function(value, message){
        message = message || "";
        console.assertTrue(value, message);
    }

    console.input = function(data, param){
        return eval(console.rawInput.call(console, [].slice(arguments)) + "");
    }

    scope.print = console.log.bind(console);

    scope.log = scope.print;

    scope.err = console.error.bind(console);

    scope.openConsole = console.show.bind(console);

    scope.clearConsole = console.clear.bind(console);


    return console;
}