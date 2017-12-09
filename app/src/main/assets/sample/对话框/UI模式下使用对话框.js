"ui";

ui.layout(
    <vertical>
        <button id="callback" align="center">回调形式</button>
        <button id="promise" align="center">Promise形式</button>
        <button id="calc" align="center">简单计算器</button>
    </vertical>
);

ui.callback.click(()=>{
    dialogs.confirm("要弹出输入框吗?", "", function(b){
        if(b){
            dialogs.rawInput("输入", "", function(str){
                alert("您输入的是:" + str);
            });
        }else{
            ui.finish();
        }
    });
});

ui.promise.click(()=>{
    dialogs.confirm("要弹出输入框吗")
        .then(function(b){
            if(b){
               return dialogs.rawInput("输入");
            }else{
                ui.finish();
            }
        }).then(function(str){
            alert("您输入的是:" + str);
        });
});


ui.calc.click(()=>{
    let num1, num2, op;
    dialogs.input("请输入第一个数字")
        .then(n => {
            num1 = n;
            return dialogs.singleChoice("请选择运算", ["加", "减", "乘", "除", "幂"]);
        })
        .then(o => {
            op = o;
            return dialogs.input("请输入第二个数字");
         })
        .then(n => {
            num2 = n;
            var result;
            switch(op){
                case 0:
                    result = num1 + num2;
                    break;
                case 1:
                    result = num1 - num2;
                    break;
                case 2:
                    result = num1 * num2;
                    break;
                case 3:
                    result = num1 / num2;
                    break;
                case 4:
                    result = Math.pow(num1, num2);
                    break;
            }
            alert("运算结果", result);
        });
});