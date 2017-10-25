var editor = CodeMirror.fromTextArea(document.getElementById("code"), {
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
editor.on("keyup", function(cm, e) {
    editor.execCommand("autocomplete");
})
editor.setCursor({line: 0, ch: 0});

window.onload = function(){
    autojs(editor);
}
