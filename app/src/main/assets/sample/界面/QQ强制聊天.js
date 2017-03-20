"ui";

importClass(android.widget.EditText);
importClass(android.widget.Button);
importClass(android.widget.LinearLayout);
importClass(android.content.Intent);
importClass(android.net.Uri);

var qq = new EditText(activity);
qq.setHint("请输入QQ号");
var btnOk = new Button(activity);
btnOk.setText("确定");
var container = new LinearLayout(activity);
container.addView(qq);
container.addView(btnOk);
activity.setContentView(container);

btnOk.setOnClickListener(function(view){
    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=" + qq.getText()))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    activity.finish();
});