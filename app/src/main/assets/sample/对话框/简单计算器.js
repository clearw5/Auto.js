var num1 = dialogs.input("请输入第一个数字");
var op = dialogs.singleChoice("请选择运算", ["加", "减", "乘", "除", "幂"]);
var num2 = dialogs.input("请输入第二个数字");
var result = 0;
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