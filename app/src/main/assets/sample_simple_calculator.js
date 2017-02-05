"ui";

importClass("com.afollestad.materialdialogs.MaterialDialog.Builder");
importClass("com.afollestad.materialdialogs.MaterialDialog.InputCallback");
importClass("android.content.DialogInterface.OnDismissListener");

new Builder(activity)
    .title("简单计算器")
    .input("请输入算式", "1+1", new InputCallback(function(dialog, input){
        eval("var ans = (" + input + ")");
        toast(ans);
    }))
    .positiveText("计算")
    .dismissListener(new OnDismissListener(function(dialog){
        activity.finish();
    }))
    .show();