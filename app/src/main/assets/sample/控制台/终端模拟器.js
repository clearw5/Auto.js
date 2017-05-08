var sh = new Shell();
sh.setCallback({
    onNewLine: function(str){
        console.log(str);
    }
})
console.show();
console.info("请输入要运行命令：");
do {
    var cmd = console.rawInput();
    sh.exec(cmd);
}while(cmd != "exit");
sh.exit();