"auto";

launchApp("微信");
while(!click("通讯录"));

toast("在好友列表中点谁删谁。被删除的好友将在日志中记录。");
while(true){
  if(desc("更多").exists()){
      var weChatId = textContains("微信号").find();
      if(weChatId && weChatId.size()){
         console.log(weChatId.get(0).text());
      }
      while(!desc("更多").click());
      while(!click("删除"));
      while(!click("删除"));
      sleep(200);
  }
}