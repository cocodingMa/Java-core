---
title: cpu缓存与高性能编程
tags:
- 缓存
- Java
- 性能
categories:
- 缓存
---

![cpucache](http://ww1.sinaimg.cn/large/006QFgWMgy1fwcmylfnf8j33341y5qv5.jpg)

## CPU Cache

### CPU Cache概述


CPU缓存（Cache Memory）位于CPU与内存之间小而快的存储设备，它的容量比内存小但交换速度快。在缓存中的数据是内存中的一小部分，但这一小部分是短时间内CPU即将访问的，当CPU调用大量数据时，就可避开内存直接从缓存中调用，从而加快读取速度。存储器系统是一个具有不同容量，成本和访问时间的存储层次结构。


存储器层次结构中的示意如下图:


![cpucache](http://ww1.sinaimg.cn/large/006QFgWMgy1fwcn0n4vggj30dw0dctbo.jpg)


现在的大多数计算机系统的CPU Cache都具有三级缓存即L1, L2, L3。每一层缓存都来自下一层缓存的一部分，三级缓存技术难度和制造成本是相对递减的，所以其容量也是相对递增的。不同储存技术访问时间差异很大，L1是最接近CPU的一级，他的速度要跟上CPU的速度，往往很小，几十k的大小。当L1中读取不到数据，才会依次从L2，L3或者内存中读取。

<!-- more --> 

### CPU Cache 实例
1. 示例一

```
    public static void main(String[] args) {
        for (int k = 1; k <= 1024; k *= 2) {
            runTime(k);
        }
    }

    public static void runTime(int k) {
        int[] arr = new int[64 * 1024 * 1024];
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < arr.length; i += k) arr[i] *= 3;
        long endTime = System.currentTimeMillis();
        System.out.println("步长为" + k + "时，程序运行时间：" + (endTime - startTime) + "ms");
    }

```
运行结果

```
步长为1时，程序运行时间：58ms
步长为2时，程序运行时间：61ms
步长为4时，程序运行时间：50ms
步长为8时，程序运行时间：83ms
步长为16时，程序运行时间：70ms
步长为32时，程序运行时间：39ms
步长为64时，程序运行时间：20ms
步长为128时，程序运行时间：10ms
步长为256时，程序运行时间：5ms
步长为512时，程序运行时间：3ms
步长为1024时，程序运行时间：1ms

```
当步长为1-16时，运行时间几乎不变，当步长大于16时，每增加一倍，运行时长减倍。背后的原因是今天的CPU不再是按字节访问内存，而是以64字节为单位的块(chunk)拿取，称为一个缓存行(cache line)。当你读一个特定的内存地址，整个缓存行将从主存换入缓存，并且访问同一个缓存行内的其它值的开销是很小的。

由于16个整型数占用64字节（一个缓存行），for循环步长在1到16之间必定接触到相同数目的缓存行：即数组中所有的缓存行。当步长为32，我们只有大约每两个缓存行接触一次，当步长为64，只有每四个接触一次。

理解缓存行对某些类型的程序优化而言可能很重要。比如，数据字节对齐可能决定一次操作接触1个还是2个缓存行。那上面的例子来说，很显然操作不对齐的数据将损失一半性能。


## 缓存性能

### 缓存行

缓存行是缓存的最小单元，一般是2的n次方字节，每个Cache Line包含三个部分，Valid：当前缓存是否有效，Tag：对应的内存地址，Block：缓存数据。

在MESI协议中有四种状态 `M：被修改的(Modified)`, `E：独享的(Exclusive)`, `S：共享的(Shared)`, `I：无效的(Invalid)`

`Modified`：当前CPU cache拥有最新数据（最新的cache line），其他CPU拥有失效数据（cache line的状态是invalid），虽然当前CPU中的数据和主存是不一致的，但是以当前CPU的数据为准；

`Exclusive`：只有当前CPU中有数据，其他CPU中没有改数据，当前CPU的数据和主存中的数据是一致的；

`Shared`：当前CPU和其他CPU中都有共同数据，并且和主存中的数据一致；

`Invalid`：当前CPU中的数据失效，数据应该从主存中获取，其他CPU中可能有数据也可能无数据，当前CPU中的数据和主存被认为是不一致的；

状态转换图：

![cacheLineStatus](http://ww1.sinaimg.cn/large/006QFgWMgy1fwcn42sbfxj30fe0b6af3.jpg)

> `Local Read`:读取本地Cache中的值, `Local Write`:将数据写到本地Cache, `Remote Read`:读取内存中的数据, `Remote Write`: 其它将数据写到主内存。

### 伪共享

伪共享产生的原因就是当处于不同cpu上的多个线程操作位于同一个缓存行上的数据（可能是同一个变量，也有可能是不同变量），就会导致缓存行会在MESI四个状态之间转换，缓存行失效，并强制进行
内存更新，以保持缓存一致性，如下图：

![Falsesharing](http://ww1.sinaimg.cn/large/006QFgWMgy1fwcn1qqfmij30lf0dggm4.jpg)

线程0和1需要在内存中相邻的变量，并位于同一缓存行上。高速缓存线路被加载到CPU 0和CPU 1）的缓存中。即使线程修改了不同的变量，缓存行也会失效，从而迫使内存更新保持缓存一致性，缓存性能下降。

在缓存行的第一次加载时，处理器会将缓存行标记为`E`。只要缓存线路是独享的，后续的负载就可以自由地使用缓存中的现有数据。如果处理器在总线上看到由另一个处理器加载相同的缓存行，那么它将标志着具有`E`访问的缓存行。如果处理器存储了一个标记为`S`的缓存行，那么会被被标记为`M`，所有其他处理器都被发送一个`E`的高速缓存线路消息。如果处理器看到的是相同的缓存行，被另外一个线程标记为`M`，处理器将缓存线路存储回内存，并将其缓存线标记为`S`。访问同一高速缓存线路的另一个处理器会导致缓存丢失。

当缓存行被标记为`I`时，处理器之间需要频繁的协调，需要将高速缓存线路写入内存并随后加载。显著降低应用程序的性能。



## 伪共享处理方案

处理伪共享的两种方式：

    1. 空间换时间,通过增大数组间的元素使不同线程存取的元素位于不同的缓存行。     

    2. 在每个线程中创建全局数组各个元素的本地拷贝，然后结束后再写回全局数组。

### Padding方式

```
class PaddingData
{
    public volatile long a1, a2, a3, a4, a5, a6, a7;

    public volatile long value = 0L;

    public volatile long b1, b2, b3, b4, b5, b6, b7;
}
```
这种方式会使变量value独占一个缓存行，不会出现伪共享的问题


### Java8中Contended注解方式

JDK1.8中新增@sun.misc.Contended注解，是各个变量位于不同的缓存行

```
// 类前加上代表整个类的每个变量都会在单独的cache line中
@sun.misc.Contended
@SuppressWarnings("restriction")
public class ContendedData {
    long a1, long a2, long a3, long a4, long a5, long a6;
}

// 分组，每组位于同一个缓存行
@SuppressWarnings("restriction")
public class ContendedGroupData {
    @sun.misc.Contended("group0")
    long a1;
    @sun.misc.Contended("group0")
    long a2;
    @sun.misc.Contended("group1")
    long a3;
    @sun.misc.Contended("group2")
    long a4;
    @sun.misc.Contended("group2")
    long a5;
}
```

### JDK1.8 涉及到的
```
 src/share/classes/java/util/concurrent/ConcurrentHashMap.java  
    @sun.misc.Contended static final class CounterCell {
        volatile long value;
        CounterCell(long x) { value = x; }
    }  
  
src/share/classes/java/lang/Thread.java  
    /** The current seed for a ThreadLocalRandom */
    @sun.misc.Contended("tlr")
    long threadLocalRandomSeed;

    /** Probe hash value; nonzero if threadLocalRandomSeed initialized */
    @sun.misc.Contended("tlr")
    int threadLocalRandomProbe;

    /** Secondary seed isolated from public ThreadLocalRandom sequence */
    @sun.misc.Contended("tlr")
    int threadLocalRandomSecondarySeed;
```

### 测试代码
```
public class Test implements Runnable {
    public static int NUM_THREADS = 2;
    public final static long ITERATIONS = 64L * 1024L * 1024L;
    private final int arrayIndex;
    private static VolatileLong[] longs;
    private static final CountDownLatch cdl = new CountDownLatch(NUM_THREADS);

    public Test(final int arrayIndex) {
        this.arrayIndex = arrayIndex;
    }

    public static void main(final String[] args) throws Exception {
        System.out.println("starting....");
        longs = new VolatileLong[NUM_THREADS];

        for (int i = 0; i < longs.length; i++) {
            longs[i] = new VolatileLong();
        }
        final long start = System.nanoTime();
        runTest();
        System.out.println("duration = " + (System.nanoTime() - start));
    }

    private static void runTest() throws InterruptedException {
        Thread[] threads = new Thread[NUM_THREADS];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Test(i));
        }
        for (Thread t : threads) {
            t.start();
        }
        cdl.await();
    }

    @Override
    public void run() {
        long i = ITERATIONS + 1;
        while (0 != --i) {
            longs[arrayIndex].value = i;
        }
        cdl.countDown();
    }

    static class VolatileLong {
        public volatile long value = 0L;
    }

    static class VolatileLong1 {
        public volatile long a1, a2, a3, a4, a5, a6, a7;

        public volatile long value = 0L;

        public volatile long b1, b2, b3, b4, b5, b6, b7;
    }

    @sun.misc.Contended
    static class VolatileLong2 {
        public volatile long value = 0L;
    }
}

```

运行结果
```
duration = 2116766934
duration1 = 462678174
duration2 = 447898996
```



## 参考
- [Disruptor并发编程网文档翻译](http://ifeve.com/disruptor/)
- [JAVA8中使用contended避免伪共享](http://budairenqin.iteye.com/blog/2048257)
- [Gallery of Processor Cache Effects](http://igoro.com/archive/gallery-of-processor-cache-effects/)
- 深入理解计算机系统 第六章





