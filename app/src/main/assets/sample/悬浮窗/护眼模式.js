var w = floaty.rawWindow(
    <frame gravity="center" bg="#44ffcc00"/>
);

w.setSize(-1, -1);
w.setTouchable(false);

setTimeout(()=>{
    w.close();
}, 60000);