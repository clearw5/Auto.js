
module.exports = function (runtime, scope) {
    var rtConsole = runtime.console;
    var console = {};

    console.assert = function (value, message) {
        message = message || "";
        rtConsole.assertTrue(value, message);
    }

    console.rawInput = rtConsole.rawInput.bind(rtConsole);

    console.input = function (data, param) {
        return eval(console.rawInput.call(console, [].slice(arguments)) + "");
    }

    console.log = function () {
        rtConsole.log(util.format.apply(util, arguments));
    }

    console.verbose = function () {
        rtConsole.verbose(util.format.apply(util, arguments));
    }

    console.print = function () {
        rtConsole.print(android.util.Log.DEBUG, util.format.apply(util, arguments));
    }

    console.info = function () {
        rtConsole.info(util.format.apply(util, arguments));
    }

    console.warn = function () {
        rtConsole.warn(util.format.apply(util, arguments));
    }

    console.error = function () {
        rtConsole.error(util.format.apply(util, arguments));
    }

    var timers = {}, ascu = android.os.SystemClock.uptimeMillis;
    console.time = console.time || function (label) {
        label = label || "default";
        timers[label] = ascu();
    }

    console.timeEnd = console.timeEnd || function (label) {
        label = label || "default";
        var result = ascu() - timers[label];
        delete timers[label];
        console.log(label + ": " + result + "ms");
    }

    console.trace = console.trace || function captureStack(message) {
        var k = {};
        Error.captureStackTrace(k, captureStack);
        console.log(util.format.apply(util, arguments) + "\n" + k.stack);
    };

    console.setGlobalLogConfig = function (config) {
        let logConfigurator = new Packages["de.mindpipe.android.logging.log4j"].LogConfigurator();
        if (config.file) {
            logConfigurator.setFileName(files.path(config.file));
            logConfigurator.setUseFileAppender(true);
        }
        logConfigurator.setFilePattern(option(config.filePattern, "%m%n"))
        logConfigurator.setMaxFileSize(option(config.maxFileSize, 512 * 1024));
        logConfigurator.setImmediateFlush(option(config.immediateFlush, true));
        let rootLevel = option(config.rootLevel, "ALL");
        logConfigurator.setRootLevel(org.apache.log4j.Level[rootLevel.toUpperCase()]);
        logConfigurator.setMaxBackupSize(option(config.maxBackupSize, 5));
        logConfigurator.setResetConfiguration(option(config.resetConfiguration, true));
        logConfigurator.configure();
    }

    function option(value, def) {
        return value == undefined ? def : value;
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