"ui";

showLoginUI();
ui.statusBarColor("#000000")

//显示登录界面
function showLoginUI(){
    ui.layout(
      <frame>
        <vertical h="auto" align="center" margin="0 50">
          <linear>
             <text w="56" gravity="center" color="#111111" size="16">用户名</text>
             <input id="name" w="*" h="40"/>
          </linear>
          <linear>
             <text w="56" gravity="center" color="#111111" size="16">密码</text>
             <input id="password" w="*" h="40" password="true"/>
          </linear>
          <linear gravity="center">
             <button id="login" text="登录"/>
             <button id="register" text="注册"/>
          </linear>
        </vertical>
      </frame>
    );

    ui.login.on("click", () => {
       toast("您输入的用户名为" + ui.name.text() + " 密码为" + ui.password.text());
    });
    ui.register.on("click", () => showRegisterUI());
}

//显示注册界面
function showRegisterUI(){
    ui.layout(
      <frame>
        <vertical h="auto" align="center" margin="0 50">
          <linear>
             <text w="56" gravity="center" color="#111111" size="16">用户名</text>
             <input w="*" h="40"/>
          </linear>
          <linear>
             <text w="56" gravity="center" color="#111111" size="16">密码</text>
             <input w="*" h="40" password="true"/>
          </linear>
          <linear>
             <text w="56" gravity="center" color="#111111" size="16">邮箱</text>
             <input w="*" h="40" inputType="textEmailAddress"/>
          </linear>
          <linear gravity="center">
             <button>确定</button>
             <button id="cancel">取消</button>
          </linear>
        </vertical>
      </frame>
    );
    ui.cancel.on("click", () => showLoginUI());
}