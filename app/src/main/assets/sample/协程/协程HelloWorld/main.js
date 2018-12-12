// 注意，要使用协程这个特性，必须使用项目功能，并且在project.json配置好features属性


// delay不同于sleep，不会阻塞当前线程
function delay(millis) {
    var cont = continuation.create();
    setTimeout(()=>{
        cont.resume();
    }, millis);
    cont.await();
}

// 异步IO例子，在另一个线程读取文件，读取完成后返回当前线程继续执行
function read(path) {
    var cont = continuation.create();
    threads.start(function(){
        try {
            cont.resume(files.read(path));
        }catch(err){
            cont.resumeError(err);
        }
    });
    return cont.await();
}

// 使用Promise和协程的例子
function add(a, b) {
    return new Promise(function(resolve, reject) {
        var sum = a + b;
        resolve(sum);
    });
}

toastLog("Hello, Continuation!");

//3秒后发出提示
setTimeout(()=>{
    toastLog("3秒后....");
}, 3000);

// 你可以尝试把delay更换成sleep，看会发生什么！
delay(6000);
toastLog("6秒后...");

try {
    toastLog("读取文件hello.txt: " + read("./hello.txt"));
}catch(err){
    console.error(err);
}

var sum = add(1, 2).await();
toastLog("1 + 2 = " + sum);


