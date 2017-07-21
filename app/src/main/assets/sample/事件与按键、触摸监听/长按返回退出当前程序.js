"auto";

var 长按间隔 = 1500;

var curPackage = null;
var timeoutId = null;

events.observeKey();

events.onKeyDown("back", function(event){
    curPackage = currentPackage();
    timeoutId = setTimeout(function(){
       backBackBackBack();
    }, 长按间隔);
});

events.onKeyUp("back", function(event){
    clearTimeout(timeoutId);
});

loop();

function backBackBackBack(){
    while(curPackage == currentPackage()){
        back();
        sleep(200);
    }
}