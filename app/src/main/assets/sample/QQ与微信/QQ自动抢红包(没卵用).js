"auto";
launch("com.tencent.mobileqq");
toast("请打开一个聊天窗口");
toast("出现红包时将会自动拆开并关闭");
while(!isStopped()){
  if(click("点击拆开")){
    //拆开红包后尝试10次关闭红包页面
    for(let i = 0; i < 10; i++){
        sleep(300);
        if(click("关闭"))
          break;
    }
  }
}