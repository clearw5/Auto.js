var url = "www.baidu.com";
var res = http.get(url);
if(res.statusCode == 200){
    toast("请求成功");
    console.show();
    log(res.body.string());
}else{
    toast("请求失败:" + res.statusMessage);
}