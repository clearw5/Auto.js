var i = 0;

setInterval(function(){
    i++;
    toast(i * 4 + "秒");
    if(i == 5){
        exit();
    }
}, 4000);

