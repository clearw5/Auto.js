var sh = new Shell();
sh.setCallback({
    onOutput: function(str){
        print(str);
    }
})
console.show();
do {
    var cmd = console.rawInput();
    sh.exec(cmd);
}while(cmd != "exit");
sh.exit();