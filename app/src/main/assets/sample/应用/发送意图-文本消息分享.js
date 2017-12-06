var content = rawInput('请输入要分享的文本');
app.startActivity({
    action: "android.intent.action.SEND",
    type: "text/*",
    extras: {
      "android.intent.extra.TEXT": content
    },
    packageName: "com.tencent.mobileqq",
    className: "com.tencent.mobileqq.activity.JumpActivity"
});