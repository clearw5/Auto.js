importClass(com.stardust.mi666.Cracker);
var cracker = new Cracker();
const path = "/sdcard/666.png";
var count = 1;
sleep(2000);
while(notStopped()){
  Screencap(path);
  sleep(2000);
  toast("你已经被耍了" + count + "次");
  var coords = cracker.crack(path);
  for each(var coord in coords){
    Tap(coord[0], coord[1]);
  }
  sleep(9000);
  关闭();
  sleep(2000);
  console.log(++count);
  再玩一次();
  sleep(10000);
}


function 关闭(){
  Tap(933, 660);
}
  
function 再玩一次(){
  Tap(521, 1615);
}