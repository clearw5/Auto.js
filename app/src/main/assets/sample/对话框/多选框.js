var i = dialogs.multiChoice("下列作品出自李贽的是", ["《焚书》", "《西湖寻梦》", "《高太史全集》", "《续焚烧书》", "《藏书》"]);
toast("选择了: " + i);
if(i.length == 2 && i.toString() == [0, 4].toString()){
    toast("答对辣");
}else{
    toast("答错辣");
}