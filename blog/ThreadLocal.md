---
title: 深入剖析ThreadLocal
updated: 2019/2/22 20:00:25
tags:
- 多线程
- JDK
categories:
- Java
---

![ThreadLocal](http://ww1.sinaimg.cn/large/006QFgWMgy1g0fc7xdc6cj34n4334x6r.jpg)

### 前言


ThreadLocal为线程提供独立的变量副本，每个线程都可以对自己的副本进行更改，而不影响其他线程的对应副本。


### ThreadLocal是什么

每一个线程都有一个`ThreadLocalMap`, 这个map被ThreadLocal类维护，所以当线程对副本进行读写时，别的线程并不能获取到当前线程的副本值。

<!-- more --> 


```
public class Thread implements Runnable {
    ThreadLocal.ThreadLocalMap threadLocals = null;
}
```


#### ThreadLocalMap

ThreadLocalMap是ThreadLocal的内部类.

```
static class Entry extends WeakReference<ThreadLocal<?>> {
            Object value;

            Entry(ThreadLocal<?> k, Object v) {
                super(k);
                value = v;
            }
        }
```

ThreadLocalMap中，用Entry来保存键值。Entry中key是ThreadLocal对象，value是副本的值。

也就是说对于一个线程来说一个ThreadLocal只能保存一个变量副本，如要保存多个副本需要多个ThreadLocal对象。

#### Thread和ThreadLoacal内部结构

![](http://ww1.sinaimg.cn/large/006QFgWMgy1g0fazf3zuoj30r00k7q37.jpg)

#### 代码展示

```
public class ThreadLocalTest {
    private static ThreadLocal<Integer> number = new ThreadLocal<Integer>() {
        public Integer initialValue() {
            return 0;
        }
    };
    public int getNum() {
        number.set(number.get() + 1);
        return number.get();
    }

    public static void main(String[] args) {
        ThreadLocalTest thread = new ThreadLocalTest();
        ThreadTest t1 = new ThreadTest(thread);
        ThreadTest t2 = new ThreadTest(thread);
        ThreadTest t3 = new ThreadTest(thread);
        t1.start();
        t2.start();
        t3.start();
    }

    private static class ThreadTest extends Thread {
        private ThreadLocalTest number;

        public ThreadTest(ThreadLocalTest number) {
            this.number = number;
        }

        public void run() {
            for (int i = 0; i < 3; i++) {
                System.out.println("name: " + Thread.currentThread().getName() + " number:" + number.getNum());
            }
        }
    }

```
运行结果：

```
name: Thread-0 number:1
name: Thread-1 number:1
name: Thread-2 number:1
name: Thread-0 number:2
name: Thread-0 number:3
name: Thread-2 number:2
name: Thread-1 number:2
name: Thread-1 number:3
name: Thread-2 number:3
```
### 源码分析

ThreadLocal核心方法
* set()：设置当前线程的副本变量
* get()：获取当前线程的副本变量
* remove(): 移除当前线程的副本变量
* initialValue(): 初始化当前副本变量。

#### set

```
public void set(T value) {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
    }

ThreadLocalMap getMap(Thread t) {
        return t.threadLocals;
    }

void createMap(Thread t, T firstValue) {
        t.threadLocals = new ThreadLocalMap(this, firstValue);
    }
```

> 获取当前线程的ThreadLocalMap对象，对象不为空，将ThreadLocal对象和新的value副本放入到map中。

> 若ThreadLocalMap对象为空，则创建新的ThreadLocalMap对象，并将ThreadLocal和value副本放入map中。

#### get

```
public T get() {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null) {
            ThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null) {
                @SuppressWarnings("unchecked")
                T result = (T)e.value;
                return result;
            }
        }
        return setInitialValue();
    }

private T setInitialValue() {
        T value = initialValue();
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
        return value;
    }
```

> 获取当前线程的ThreadLocalMap对象，当Map对象为空时获取Entry节点。

> 从Entry节点获取key为ThreadLocal对象的Value副本值并返回。

> 若Map为空的话，对当前副本初始化并返回。

#### remove()

```
public void remove() {
         ThreadLocalMap m = getMap(Thread.currentThread());
         if (m != null)
             m.remove(this);
     }
```

> 获取当前线程的ThreadLocalMap对象，不为空则移除。

### 注意事项

Entry中key是ThreadLocal对象，此处为弱引用，value是副本的值，此处为强引用。

`get(), set(), remove()方法都会清楚key为null的Entry，否则会造成内存泄漏`

`每次使用完ThreadLocal，都需要调用remove()方法，清除数据，防止内存泄漏。`
