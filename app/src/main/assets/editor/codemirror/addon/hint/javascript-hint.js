// CodeMirror, copyright (c) by Marijn Haverbeke and others
// Distributed under an MIT license: http://codemirror.net/LICENSE

(function(mod) {
  if (typeof exports == "object" && typeof module == "object") // CommonJS
    mod(require("../../lib/codemirror"));
  else if (typeof define == "function" && define.amd) // AMD
    define(["../../lib/codemirror"], mod);
  else // Plain browser env
    mod(CodeMirror);
})(function(CodeMirror) {
  var Pos = CodeMirror.Pos;

  function forEach(arr, f) {
    for (var i = 0, e = arr.length; i < e; ++i) f(arr[i]);
  }

  function arrayContains(arr, item) {
    if (!Array.prototype.indexOf) {
      var i = arr.length;
      while (i--) {
        if (arr[i] === item) {
          return true;
        }
      }
      return false;
    }
    return arr.indexOf(item) != -1;
  }

  function scriptHint(editor, keywords, getToken, options) {
    // Find the token at the cursor
    var cur = editor.getCursor(), token = getToken(editor, cur);
    if (/\b(?:string|comment)\b/.test(token.type)) return;
    token.state = CodeMirror.innerMode(editor.getMode(), token.state).state;

    // If it's not a 'word-style' token, ignore the token.
    if (!/^[\w$_]*$/.test(token.string)) {
      token = {start: cur.ch, end: cur.ch, string: "", state: token.state,
               type: token.string == "." ? "property" : null};
    } else if (token.end > cur.ch) {
      token.end = cur.ch;
      token.string = token.string.slice(0, cur.ch - token.start);
    }

    var tprop = token;
    // If it is a property, find out what it is a property of.
    while (tprop.type == "property") {
      tprop = getToken(editor, Pos(cur.line, tprop.start));
      if (tprop.string != ".") return;
      tprop = getToken(editor, Pos(cur.line, tprop.start));
      if (!context) var context = [];
      context.push(tprop);
    }
    return {list: getCompletions(token, context, keywords, options),
            from: Pos(cur.line, token.start),
            to: Pos(cur.line, token.end)};
  }

  function javascriptHint(editor, options) {
    return scriptHint(editor, javascriptKeywords,
                      function (e, cur) {return e.getTokenAt(cur);},
                      options);
  };
  CodeMirror.registerHelper("hint", "javascript", javascriptHint);
  var __global__ = {};
  regsiterAutoJsObjects();

  function getCoffeeScriptToken(editor, cur) {
  // This getToken, it is for coffeescript, imitates the behavior of
  // getTokenAt method in javascript.js, that is, returning "property"
  // type and treat "." as indepenent token.
    var token = editor.getTokenAt(cur);
    if (cur.ch == token.start + 1 && token.string.charAt(0) == '.') {
      token.end = token.start;
      token.string = '.';
      token.type = "property";
    }
    else if (/^\.[\w$_]*$/.test(token.string)) {
      token.type = "property";
      token.start++;
      token.string = token.string.replace(/\./, '');
    }
    return token;
  }

  function coffeescriptHint(editor, options) {
    return scriptHint(editor, coffeescriptKeywords, getCoffeeScriptToken, options);
  }
  CodeMirror.registerHelper("hint", "coffeescript", coffeescriptHint);

  var stringProps = ("charAt charCodeAt indexOf lastIndexOf substring substr slice trim trimLeft trimRight " +
                     "toUpperCase toLowerCase split concat match replace search").split(" ");
  var arrayProps = ("length concat join splice push pop shift unshift slice reverse sort indexOf " +
                    "lastIndexOf every some filter forEach map reduce reduceRight ").split(" ");
  var funcProps = "prototype apply call bind".split(" ");
  var javascriptKeywords = ("break case catch continue debugger default delete do else false finally for function " +
                  "if in instanceof new null return switch throw true try typeof var void while with").split(" ");
  var coffeescriptKeywords = ("and break catch class continue delete do else extends false finally for " +
                  "if in instanceof isnt new no not null of off on or return switch then throw true try typeof until void while with yes").split(" ");

  function forAllProps(obj, callback) {
    if (!Object.getOwnPropertyNames || !Object.getPrototypeOf) {
      for (var name in obj) callback(name)
    } else {
      for (var o = obj; o; o = Object.getPrototypeOf(o))
        Object.getOwnPropertyNames(o).forEach(callback)
    }
  }

  function getCompletions(token, context, keywords, options) {
    var found = [], start = token.string, global = options && options.globalScope || window;
    function maybeAdd(str) {
      if (str.lastIndexOf(start, 0) == 0 && !arrayContains(found, str)) found.push(str);
    }
    function gatherCompletions(obj) {
      console.log("gatherCompletions: ", obj);
      if (typeof obj == "string") forEach(stringProps, maybeAdd);
      else if (obj instanceof Array) forEach(arrayProps, maybeAdd);
      else if (obj instanceof Function) forEach(funcProps, maybeAdd);
      forAllProps(obj, maybeAdd)
    }

    if (context && context.length) {
      // If this is a property, see if it belongs to some object we can
      // find in the current environment.
      var obj = context.pop(), base;
      if (obj.type && obj.type.indexOf("variable") === 0) {
        if (options && options.additionalContext)
          base = options.additionalContext[obj.string];
        if (!options || options.useGlobalScope !== false)
          base = base || global[obj.string];
      } else if (obj.type == "string") {
        base = "";
      } else if (obj.type == "atom") {
        base = 1;
      } else if (obj.type == "function") {
        if (global.jQuery != null && (obj.string == '$' || obj.string == 'jQuery') &&
            (typeof global.jQuery == 'function'))
          base = global.jQuery();
        else if (global._ != null && (obj.string == '_') && (typeof global._ == 'function'))
          base = global._();
      }
      while (base != null && context.length)
        base = base[context.pop().string];
      if (base != null) gatherCompletions(base);
    } else {
      // If not, just look in the global object and any local scope
      // (reading into JS mode internals to get at the local and global variables)
      for (var v = token.state.localVars; v; v = v.next) maybeAdd(v.name);
      for (var v = token.state.globalVars; v; v = v.next) maybeAdd(v.name);
      if (!options || options.useGlobalScope !== false)
        gatherCompletions(__global__);
      forEach(keywords, maybeAdd);
    }
    return found;
  }

  function regsiterAutoJsObjects(){
    var objs = {
      "global": {
        "modules": [
          "app",
          "automator",
          "console",
          "dialogs",
          "events",
          "images",
          "files",
          "timers",
          "ui"
        ],
        "app": [
          "launchPackage",
          "launch",
          "launchApp",
          "getPackageName",
          "openAppSetting"
        ],
        "automator": [
          "click",
          "longClick",
          "press",
          "swipe",
          "gesture",
          "gestures",
          "gestureAsync",
          "gesturesAsync",
          "scrollDown",
          "scrollUp",
          "input",
          "setText"
        ],
        "console": [
          "print",
          "log",
          "err",
          "openConsole",
          "clearConsole"
        ],
        "dialogs": [
          "rawInput",
          "input",
          "alert",
          "confirm",
          "prompt"
        ],
        "images": [
          "requestScreenCapture",
          "captureScreen",
          "findColor",
          "findColorInRegion",
          "findColorEquals"
        ],
        "files": [
          "open"
        ],
        "selector": [
          "id",
          "idContains",
          "idStartsWith",
          "idEndsWith",
          "idMatches",
          "text",
          "textContains",
          "textStartsWith",
          "textEndsWith",
          "textMatches",
          "desc",
          "descContains",
          "descStartsWith",
          "descEndsWith",
          "descMatches",
          "className",
          "classNameContains",
          "classNameStartsWith",
          "classNameEndsWith",
          "classNameMatches",
          "packageName",
          "packageNameContains",
          "packageNameStartsWith",
          "packageNameEndsWith",
          "packageNameMatches",
          "bounds",
          "boundsInside",
          "boundsContains",
          "drawingOrder",
          "checkable",
          "checked",
          "focusable",
          "focused",
          "visibleToUser",
          "accessibilityFocused",
          "selected",
          "clickable",
          "longClickable",
          "enabled",
          "password",
          "scrollable",
          "editable",
          "contentInvalid",
          "contextClickable",
          "multiLine",
          "dismissable",
          "checkable",
          "checked",
          "focusable",
          "focused",
          "visibleToUser",
          "accessibilityFocused",
          "selected",
          "clickable",
          "longClickable",
          "enabled",
          "password",
          "scrollable",
          "editable",
          "contentInvalid",
          "contextClickable",
          "multiLine",
          "dismissable"
        ],
        "shell": [
          "SetScreenMetrics",
          "Tap",
          "Swipe",
          "Screencap",
          "KeyCode",
          "Home",
          "Back",
          "Power",
          "Up",
          "Down",
          "Left",
          "Right",
          "OK",
          "VolumeUp",
          "VolumeDown",
          "Menu",
          "Camera",
          "Text"
        ],
        "timers": [
          "loop",
          "setTimeout",
          "clearTimeout",
          "setInterval",
          "clearInterval",
          "setImmediate",
          "clearImmediate"
        ],
        "web": [
          "newInjectableWebClient",
          "newInjectableWebView"
        ],
        "general": [
          "toast",
          "toastLog",
          "sleep",
          "isStopped",
          "notStopped",
          "exit",
          "setClip",
          "getClip",
          "currentPackage",
          "currentActivity",
          "waitForActivity",
          "waitForPackage",
          "setScreenMetrics"
        ],
        "variables": [
          "context",
          "activity"
        ]
      },
      "app": [
        "uninstall",
        "viewFile",
        "editFile",
        "openUrl",
        "launchPackage",
        "launch",
        "launchApp",
        "getPackageName",
        "openAppSetting"
      ],
      "automator": [
        "click",
        "longClick",
        "press",
        "swipe",
        "gesture",
        "gestures",
        "gestureAsync",
        "gesturesAsync",
        "scrollDown",
        "scrollUp",
        "input",
        "setText"
      ],
      "console": [
        "show",
        "hide",
        "clear",
        "verbose",
        "print",
        "info",
        "log",
        "warn",
        "error",
        "assert"
      ],
      "dialogs": [
        "select",
        "singleChoice",
        "multiChoice",
        "rawInput",
        "input",
        "alert",
        "confirm",
        "prompt"
      ],
      "images": [
        "saveImage",
        "pixel",
        "read",
        "requestScreenCapture",
        "captureScreen",
        "findColor",
        "findColorInRegion",
        "findColorEquals"
      ],
      "colors": [
        "red",
        "green",
        "blue",
        "alpha",
        "toString",
        "rgb",
        "argb"
      ],
      "events": [
        "emitter",
        "observeKey",
        "observeTouch",
        "observeNotification",
        "onKeyDown",
        "onKeyUp",
        "onceKeyDown",
        "onceKeyUp",
        "onToast",
        "onNotification",
        "removeAllKeyDownListeners",
        "removeAllKeyUpListeners",
        "onTouch",
        "removeAllTouchListeners",
        "getTouchEventTimeout",
        "setTouchEventTimeout",
        "on",
        "once",
        "emit",
        "getListeners",
        "addListener",
        "eventNames",
        "listenerCount",
        "listeners",
        "prependListener",
        "prependOnceListener",
        "removeAllListeners",
        "removeAllListeners",
        "removeListener",
        "setMaxListeners",
        "getMaxListeners",
        "defaultMaxListeners"
      ],
      "files": [
        "open",
        "isFile",
        "isDir",
        "isEmptyDir",
        "join",
        "create",
        "createIfNotExists",
        "exists",
        "ensureDir",
        "read",
        "write",
        "copy",
        "rename",
        "renameWithoutExtension",
        "getName",
        "getExtension",
        "remove",
        "removeDir",
        "getSdcardPath",
        "listDir"
      ],
      "timers": [
        "loop",
        "setTimeout",
        "clearTimeout",
        "setInterval",
        "clearInterval",
        "setImmediate",
        "clearImmediate"
      ]
    };
    for(var key in objs){
      if(!objs.hasOwnProperty(key)){
          continue;
      }
      if(key == "global"){
            for(var k in objs[key]){
                if(!objs[key].hasOwnProperty(k)){
                    continue;
                }
                __global__[k] = "";
            }
            continue;
      }
      var props = objs[key];
      if(!__global__[key]){
        __global__[key] = {};
      }
      for(var i = 0; i < props.length; i++){
        if(!__global__[key][props[i]])
          __global__[key][props[i]] = '';
      }
    }
  }
});
