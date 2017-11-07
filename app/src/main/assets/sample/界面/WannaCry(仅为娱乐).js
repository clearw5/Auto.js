"ui";

/**
 * By Da Zhang
 * 本脚本仅为娱乐，没有任何破坏性质
 */

ui.statusBarColor("#AA0000");

var Quin = 32552732;

ui.layout(
    <frame background="#AA0000">
	    <vertical align="top" paddingTop="5" margin="10">
		    <text id="oops" color="#FFFFFF" gravity="center" size="20">Oops, your files have been encrypted!</text>
		    <text id="text" bg="#FFFFFF" gravity="left" color="#000000" size="15" marginTop="15" h="425"></text>
		    <button id="payment" text="Payment" margin="20 0 0 0"/>
		    <button id="decrypt" text="Decrypt"/>
	    </vertical>
    </frame>
);
ui.text.text("我的手机出了什么问题？\n您的一些重要文件被我加密保存了。\n" + 
	"照片、图片、文档、压缩包、音频、视频文件、apk文件等，几乎所有类型的文件都被加密了，因此不能正常打开。\n" + 
	"这和一般文件损坏有本质上的区别。您大可在网上找找恢复文件的方法，我敢保证，没有我们的解密服务，就算老天爷来了也不能恢复这些文档。\n\n" + 
	"有没有恢复这些文档的方法?\n当然有可恢复的方法。只能通过我们的解密服务才能恢复。我以人格担保，能够提供安全有效的恢复服务。\n" + 
	"但这是收费的，也不能无限期的推迟。\n请点击 <Decrypt> 按钮，就可以免费恢复一些文档。请您放心，我是绝不会骗你的。\n" + 
	"但想要恢复全部文档，需要付款点费用。\n是否随时都可以固定金额付款，就会恢复的吗，当然不是，推迟付款时间越长，对你不利。\n" + 
	"最好3天之内付款费用，过了三天费用就会翻倍。\n还有，一个礼拜之内未付款，将会永远恢复不了。\n" + 
	"对了，忘了告诉你，对半年以上没钱付款的穷人，会有活动免费恢复，能否轮到你，就要看您的运气怎么样了。");
ui.oops.click(() => toast("Fuck you!"));
ui.oops.longClick(() => {
    var thisjoke="This is a joke : )";
    if(ui.oops.text() != thisjoke){
    	ui.oops.text(thisjoke);
    }else{
    	ui.oops.text("Oops, your files have been encrypted!");
    }
    return true;
});
ui.text.click(() => ui.text.append("。"));
ui.text.longClick(() => {
    ui.text.setText("\n"+ui.text.getText())
    return true;
});
ui.payment.click(() => {
	try{
		app.startActivity({
			action:"android.intent.action.VIEW",
			data:"mqqapi://card/show_pslcard?&uin=" + Quin
		});
		toast("Please payment by QQ");
	}catch(e){
		toast("Payment Error");
	}
});
ui.payment.longClick(() => {
	toast("You are silly b!");
	return true;
});
ui.decrypt.click(() => {
	toast("Decrypt Error");
	activity.finish();
});
ui.decrypt.longClick(() => {
	toast("You can't decrypt!");
	return true;
});
