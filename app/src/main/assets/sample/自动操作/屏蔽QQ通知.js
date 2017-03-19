importClass(android.content.Intent);
importClass(android.net.Uri);
importClass(android.provider.Settings);

function 打开App设置(packageName){
  var intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);  
  var uri = Uri.fromParts("package", packageName, null);  
  intent.setData(uri);  
  context.startActivity(intent); 
}

打开App设置("com.tencent.mobileqq");
while(!currentPackage().equals("com.android.settings"));
while(!click("通知"));
while(!click("全部阻止"));
 