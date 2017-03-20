"ui";

importClass(com.afollestad.materialdialogs.MaterialDialog);

new MaterialDialog.Builder(activity)
    .title("简单计算器")
    .input("请输入算式", "1+1", function(dialog, input){
        try{
            eval("var ans = (" + input + ")");
            toast(ans);
        }catch(e){
            toast("式子错误");
        }
    })
    .positiveText("计算")
    .dismissListener(function(dialog){
        activity.finish();
    })
    .show();