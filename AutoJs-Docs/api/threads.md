# Threads

> Stability: 1 - Experiment

threads模块提供了多线程支持，可以启动新线程来运行脚本。

脚本主线程会等待所有子线程执行完成后才停止执行，因此如果子线程中有死循环，请在必要的时候调用`exit()`来直接停止脚本或`threads.shutDownAll()`来停止所有子线程。

通过`threads.start()`启动的所有线程会在脚本被强制停止时自动停止。

由于JavaScript自身没有多线程的支持，因此您可能会遇到意料之外的问题。

## threads.start(action)
* `action` {Function} 要在新线程执行的函数
* 返回 [Thread](#threads_thread)

启动一个新线程并执行action。

例如:
```
threads.start(function(){
    //在新线程执行的代码
    while(true){
        log("子线程");
    }
});
while(true){
    log("脚本主线程");
}
```

通过该函数返回的[Thread](#threads_thread)对象可以获取该线程的状态，控制该线程的运行中。例如:
```
var thread = threads.start(function(){
    while(true){
        log("子线程");
    }
});
//停止线程执行
thread.interrupt();
```

更多信息参见[Thread](#threads_thread)。

## threads.shutDownAll()

停止所有通过`threads.start()`启动的子线程。

## threads.currentThread()
* 返回 [Thread](#threads_thread)

返回当前线程。

## threads.disposable()
* 返回 [Disposable](#threads_disposable)

新建一个Disposable对象，用于等待另一个线程的某个一次性结果。更多信息参见[线程通信](#threads_线程通信)以及[Disposable](#threads_disposable)。

## threads.atomic([initialValue])
* `initialValue` {number} 初始整数值，默认为0
* 返回[AtomicLong](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/atomic/AtomicLong.html)

新建一个整数原子变量。更多信息参见[线程安全](#threads_线程安全)以及[AtomicLong](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/atomic/AtomicLong.html)。

## threads.lock()
* 返回[ReentrantLock](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/locks/ReentrantLock.html)

新建一个可重入锁。更多信息参见[线程安全](#threads_线程安全)以及[ReentrantLock](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/locks/ReentrantLock.html)。

# Thread

线程对象，`threads.start()`返回的对象，用于获取和控制线程的状态，与其他线程交互等。

Thread对象提供了和timers模块一样的API，例如`setTimeout()`, `setInterval()`等，用于在该线程执行相应的定时回调，从而使线程之间可以直接交互。例如：

```
var thread = threads.start(function(){
    //在子线程执行的定时器
    setInterval(function(){
        log("子线程:" + threads.currentThread());
    }, 1000);
});

log("当前线程为主线程:" + threads.currentThread());

//等待子线程启动
thread.waitFor();
//在子线程执行的定时器
thread.setTimeout(function(){
    //这段代码会在子线程执行
    log("当前线程为子线程:" + threads.currentThread());
}, 2000);

sleep(30 * 1000);
thread.interrupt();
```

## Thread.interrupt()

中断线程运行。

## Thread.join([timeout])
* `timeout` {number} 等待时间，单位毫秒

等待线程执行完成。如果timeout为0，则会一直等待直至该线程执行完成；否则最多等待timeout毫秒的时间。

例如:
```
var sum = 0;
//启动子线程计算1加到10000
var thread = threads.start(function(){
    for(var i = 0; i < 10000; i++){
        sum += i;
    }
});
//等待该线程完成
thread.join();
toast("sum = " + sum);
```

## isAlive()
* 返回 {boolean}

返回线程是否存活。如果线程仍未开始或已经结束，返回`false`; 如果线程已经开始或者正在运行中，返回`true`。

## waitFor()

等待线程开始执行。调用`threads.start()`以后线程仍然需要一定时间才能开始执行，因此调用此函数会等待线程开始执行；如果线程已经处于执行状态则立即返回。

```
var thread = threads.start(function(){
    //do something
});
thread.waitFor();
thread.setTimeout(function(){
    //do something
}, 1000);
```

## Thread.setTimeout(callback, delay\[, ...args\])

参见[timers.setTimeout()](timers.html#timers_settimeout_callback_delay_args)。

区别在于, 该定时器会在该线程执行。如果当前线程仍未开始执行或已经执行结束，则抛出`IllegalStateException`。

```
log("当前线程(主线程):" + threads.currentThread());

var thread = threads.start(function(){
    //设置一个空的定时来保持线程的运行状态
    setInterval(function(){}, 1000);
});

sleep(1000);
thread.setTimeout(function(){
    log("当前线程(子线程):" + threads.currentThread());
    exit();
}, 1000);
```

## Thread.setInterval(callback, delay\[, ...args\])

参见[timers.setInterval()](timers.html#timers_setinterval_callback_delay_args)。

区别在于, 该定时器会在该线程执行。如果当前线程仍未开始执行或已经执行结束，则抛出`IllegalStateException`。


## Thread.setImmediate(callback[, ...args])

参见[timers.setImmediate()](timers.html#timers_setimmediate_callback_delay_args)。

区别在于, 该定时器会在该线程执行。如果当前线程仍未开始执行或已经执行结束，则抛出`IllegalStateException`。

## Thread.clearInterval(id)

参见[timers.clearInterval()](timers.html#timers_clearinterval_id)。

区别在于, 该定时器会在该线程执行。如果当前线程仍未开始执行或已经执行结束，则抛出`IllegalStateException`。

## Thread.clearTimeout(id)

参见[timers.clearTimeout()](timers.html#timers_cleartimeout_id)。

区别在于, 该定时器会在该线程执行。如果当前线程仍未开始执行或已经执行结束，则抛出`IllegalStateException`。

## Thread.clearImmediate(id)

参见[timers.clearImmediate()](timers.html#timers_clearimmediate_id)。

区别在于, 该定时器会在该线程执行。如果当前线程仍未开始执行或已经执行结束，则抛出`IllegalStateException`。

# 线程安全

线程安全问题是一个相对专业的编程问题，本章节只提供给有需要的用户。

引用维基百科的解释：
> 线程安全是编程中的术语，指某个函数、函数库在多线程环境中被调用时，能够正确地处理多个线程之间的共享变量，使程序功能正确完成。

在Auto.js中，线程间变量在符合JavaScript变量作用域规则的前提下是共享的，例如全局变量在所有线程都能访问，并且保证他们在所有线程的可见性。但是，不保证任何操作的原子性。例如经典的自增"i++"将不是原子性操作。

Rhino和Auto.js提供了一些简单的设施来解决简单的线程安全问题，如锁`threads.lock()`, 函数同步锁`sync()`, 整数原子变量`threads.atomic()`等。

例如，对于多线程共享下的整数的自增操作(自增操作会导致问题，是因为自增操作实际上为`i = i + 1`，也就是先读取i的值, 把他加1, 再赋值给i, 如果两个线程同时进行自增操作，可能出现i的值只增加了1的情况)，应该使用`threads.atomic()`函数来新建一个整数原子变量，或者使用锁`threads.lock()`来保证操作的原子性，或者用`sync()`来增加同步锁。

线程不安全的代码如下：
```
var i = 0;
threads.start(function(){
    while(true){
        log(i++);
    }
});
while(true){
    log(i++);
}
```

此段代码运行后打开日志，可以看到日志中有重复的值出现。

使用`threads.atomic()`的线程安全的代码如下:

```
//atomic返回的对象保证了自增的原子性
var i = threads.atomic();
threads.start(function(){
    while(true){
        log(i.getAndIncrement());
    }
});
while(true){
    log(i.getAndIncrement());
}
```

或者:

```
//锁保证了操作的原子性
var lock = threads.lock();
var i = 0;
threads.start(function(){
    while(true){
        lock.lock();
        log(i++);
        lock.unlock();
    }
});
while(true){
    lock.lock();
    log(i++);
    lock.unlock();
}
```

或者:
```
//sync函数会把里面的函数加上同步锁，使得在同一时刻最多只能有一个线程执行这个函数
var i = 0;
var getAndIncrement = sync(function(){
    return i++;
});
threads.start(function(){
    while(true){
        log(getAndIncrement());
    }
});
while(true){
    log(getAndIncrement());
}
```

另外，数组Array不是线程安全的，如果有这种复杂的需求，请用Android和Java相关API来实现。例如`CopyOnWriteList`, `Vector`等都是代替数组的线程安全的类，用于不同的场景。例如:
```
var nums = new java.util.Vector();
nums.add(123);
nums.add(456);
toast("长度为" + nums.size());
toast("第一个元素为" + nums.get(0));
```
但很明显的是，这些类不像数组那样简便易用，也不能使用诸如`slice()`之类的方便的函数。在未来可能会加入线程安全的数组来解决这个问题。当然您也可以为每个数组的操作加锁来解决线程安全问题：
```
var nums = [];
var numsLock = threads.lock();
threads.start(function(){
    //向数组添加元素123
    numsLock.lock();
    nums.push(123);
    log("线程: %s, 数组: %s", threads.currentThread(), nums);
    numsLock.unlock();
});

threads.start(function(){
    //向数组添加元素456
    numsLock.lock();
    nums.push(456);
    log("线程: %s, 数组: %s", threads.currentThread(), nums);
    numsLock.unlock();
});

//删除数组最后一个元素
numsLock.lock();
nums.pop();
log("线程: %s, 数组: %s", threads.currentThread(), nums);
numsLock.unlock();
```

## sync(func)
* `func` {Function} 函数
* 返回 {Function}

给函数func加上同步锁并作为一个新函数返回。

```
var i = 0;
function add(x){
    i += x;
}

var syncAdd = sync(add);
syncAdd(10);
toast(i);
```

# 线程通信

Auto.js提供了一些简单的设施来支持简单的线程通信。`threads.disposable()`用于一个线程等待另一个线程的(一次性)结果，同时`Lock.newCondition()`提供了Condition对象用于一般的线程通信(await, signal)。另外，`events`模块也可以用于线程通信，通过指定`EventEmiiter`的回调执行的线程来实现。

使用`threads.disposable()`可以简单地等待和获取某个线程的执行结果。例如要等待某个线程计算"1+.....+10000":
```
var sum = threads.disposable();
//启动子线程计算
threads.start(function(){
    var s = 0;
    //从1加到10000
    for(var i = 1; i <= 10000; i++){
        s += i;
    }
    //通知主线程接收结果
    sum.setAndNotify(s);
});
//blockedGet()用于等待结果
toast("sum = " + sum.blockedGet());
```

如果上述代码用`Condition`实现：
```
//新建一个锁
var lock = threads.lock();
//新建一个条件，即"计算完成"
var complete = lock.newCondition();
var sum = 0;
threads.start(function(){
    //从1加到10000
    for(var i = 1; i <= 10000; i++){
        sum += i;
    }
    //通知主线程接收结果
    lock.lock();
    complete.signal();
    lock.unlock();
});
//等待计算完成
lock.lock();
complete.await();
lock.unlock();
//打印结果
toast("sum = " + sum);
```

如果上诉代码用`events`模块实现：
```
//新建一个emitter, 并指定回调执行的线程为当前线程
var sum = events.emitter(threads.currentThread());
threads.start(function(){
    var s = 0;
    //从1加到10000
    for(var i = 1; i <= 10000; i++){
        s += i;
    }
    //发送事件result通知主线程接收结果
    sum.emit('result', s);
});
sum.on('result', function(s){
    toastLog("sum = " + s + ", 当前线程: " + threads.currentThread());
});
```

有关线程的其他问题，例如生产者消费者等问题，请用Java相关方法解决，例如`java.util.concurrent.BlockingQueue`。