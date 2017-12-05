var url = "http://www.autojs.org/assets/uploads/profile/3-profileavatar.png";
var res = http.get(url);
if(res.statusCode != 200){
    toast("请求失败");
}
files.writeBytes("/sdcard/1.png", res.body.bytes());
toast("下载成功");
app.viewFile("/sdcard/1.png");