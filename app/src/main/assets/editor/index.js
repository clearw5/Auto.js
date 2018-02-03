var ExcludedIntelliSenseTriggerKeys =
{
    "8": "backspace",
    "9": "tab",
    "13": "enter",
    "16": "shift",
    "17": "ctrl",
    "18": "alt",
    "19": "pause",
    "20": "capslock",
    "27": "escape",
    "32": "space",
    "33": "pageup",
    "34": "pagedown",
    "35": "end",
    "36": "home",
    "37": "left",
    "38": "up",
    "39": "right",
    "40": "down",
    "45": "insert",
    "46": "delete",
    "91": "left window key",
    "92": "right window key",
    "93": "select",
    "107": "add",
    "109": "subtract",
    "110": "decimal point",
    "111": "divide",
    "112": "f1",
    "113": "f2",
    "114": "f3",
    "115": "f4",
    "116": "f5",
    "117": "f6",
    "118": "f7",
    "119": "f8",
    "120": "f9",
    "121": "f10",
    "122": "f11",
    "123": "f12",
    "144": "numlock",
    "145": "scrolllock",
    "186": "semicolon",
    "187": "equalsign",
    "188": "comma",
    "189": "dash",
    "190": "period",
    "191": "slash",
    "192": "graveaccent",
    "220": "backslash",
    "222": "quote"
};
var editor = CodeMirror(document.body, {
    lineNumbers: true,     // 显示行数
    indentUnit: 4,         // 缩进单位为4
    styleActiveLine: true, // 当前行背景高亮
    foldGutter: true,
    gutters:["CodeMirror-linenumbers", "CodeMirror-foldgutter"],
    matchBrackets: true,   // 括号匹配
    mode: 'javascript',
    lineWrapping: false,    // 自动换行
    theme: 'neo'      // 使用neo主题
});
editor.setOption("fullScreen", true);
editor.setOption("hintOptions", {
    completeSingle: false,
    globalScope: autoJsGlobalScope()
});
var id = null;
editor.on("keyup", function(editor, event)
{
    if (!ExcludedIntelliSenseTriggerKeys[(event.keyCode || event.which).toString()] )
    {
        if(id != null){
            clearTimeout(id);
        }
        id = setTimeout(function(){
            editor.showHint({
                completeSingle: false
            })
        }, 100);
    }
});
editor.setCursor({line: 0, ch: 0});

window.onload = function(){
    autojs(editor);
}