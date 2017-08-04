var script = "toast('Hello, Auto.js');" +
             "sleep(3000);" +
             "toast('略略略');";
var execution = engines.execScript(script);
sleep(1000);
execution.getEngine().forceStop();