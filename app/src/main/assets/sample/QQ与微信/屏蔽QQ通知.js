"auto";
importClass(android.content.Intent);
importClass(android.net.Uri);
importClass(android.provider.Settings);


openAppSetting("com.tencent.mobileqq");
while(!currentPackage().equals("com.android.settings"));
while(!click("通知"));
while(!click("全部阻止"));
 