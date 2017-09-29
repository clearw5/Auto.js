function autojs(editor){
    function loadJs(url){
        var script = document.createElement("script");
        // This script has a callback function that will run when the script has
        // finished loading.
        script.src = url;
        script.type = "text/javascript";
        document.getElementsByTagName("head")[0].appendChild(script);
    }
    var server = null;
    var getURL = function(url, c) {
        var xhr = new XMLHttpRequest();
        xhr.open("get", url, true);
        xhr.send();
        xhr.onreadystatechange = function() {
            if (xhr.readyState != 4) return;
            if (xhr.status < 400) return c(null, xhr.responseText);
            var e = new Error(xhr.responseText || "No response");
            e.status = xhr.status;
            c(e);
        };
    }
    connect();
    editor.tern = {};
    editor.tern.showType = function(){
        checkServer();
        server.showType(editor);
    }
    editor.tern.jumpToDef = function(){
        checkServer();
        server.jumpToDef(editor);
    }
    editor.tern.rename = function(){
        checkServer();
        server.rename(editor);
    }
    editor.tern.selectName = function(){
        checkServer();
        server.selectName(editor);
    }
    editor.tern.jumpBack = function(){
        checkServer();
        server.jumpBack(editor);
    }

    function connect(){
        getURL("http://ternjs.net/defs/ecmascript.json", function(err, code) {
            if (err) throw new Error("Request for ecmascript.json: " + err);
            server = new CodeMirror.TernServer({defs: [JSON.parse(code)]});
            editor.tern.server = server;
            editor.on("cursorActivity", function(cm) { server.updateArgHints(cm); });
        });
    }

    function checkServer(){
        if(!server){
            alert("Error: cannot connect to tern server\n联网获取编辑器增强服务失败:(\n");
            connect();
        }
    }
}