while(true){
    var i = dialogs.select("哲学的基本问题是", "社会和自然的关系问题", "思维与存在的关系问题", "政治和经济的关系问题", "实践和理论的关系问题");
    if(i == -1){
        toast("猜一下呗");
        continue;
    }
    if(i == 1){
        toast("答对辣");
        break;
    }else{
        toast("答错辣")
    }
}


