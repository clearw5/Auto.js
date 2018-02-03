
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

editor.setCursor({line: 0, ch: 0});
editor.on('change', function(){
    var c = editor.getCursor();
    if(typeof(__bridge__) != 'undefined'){
        __bridge__.onKeyUp(editor.getLine(c.line), c.ch);
    }else{
        console.log(editor.getLine(c.line), c.ch);
    }
});

window.onload = function(){
    autojs(editor);
}