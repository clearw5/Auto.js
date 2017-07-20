"auto";

var 长按间隔 = 1500;

var curPackage = null;
var backPressedCount = 0;
var backPressed = false;

events.observeKey();

events.onKeyDown("back", function(event){
    curPackage = currentPackage();
    backPressed = true;
    backPressedCount++;
    (function(count){
        events.setTimeout(function(){
            if(backPressed && count == backPressedCount){
                backBackBackBack();
            }
        }, 长按间隔);
    })(backPressedCount);
});

events.onKeyUp("back", function(event){
    backPressed = false;
});

events.loop();

function backBackBackBack(){
    while(curPackage == currentPackage()){
        back();
        sleep(200);
    }
}