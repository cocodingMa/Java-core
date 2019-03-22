---
title: Redis原理剖析（三） 哈希
updated: 2019/3/21 20:00:00
tags:
- Redis
- 哈希
categories:
- Redis
---

![redis_hash](http://ww1.sinaimg.cn/large/006QFgWMgy1g13g2d59vaj34is30jkjl.jpg)

### 前言

Redis hash 是一个string类型的field和value的映射表，hash特别适合用于存储对象。Redis中hash也称作字典（Dict）

### 哈希的结构

使用哈希表作为底层实现，一个哈希表中可以有多个哈希节点，每个哈希节点保存了一个键值对。

哈希表结构在根目录下dict.h/dictht :
```
typedef struct dictht {
    dictEntry **table;  //存放一个数组的地址，数组存放着哈希表节点dictEntry的地址。
    unsigned long size; //哈希表数组的大小，初始化大小为4(2的n次方，原理和hashmap一样)
    unsigned long sizemask; //用于将哈希值映射到table的位置索引。值是size-1
    unsigned long used; //哈希表中的节点数。
} dictht;

```
<!-- more --> 

哈希表节点 dict.h/dictEntry：
```
typedef struct dictEntry {
    void *key;  //键
    union { //值
        void *val;
        uint64_t u64;
        int64_t s64;
        double d;
    } v;
    struct dictEntry *next; //下一个节点
} dictEntry;
```

哈希结构dict.h/dict:
```
typedef struct dict {
    dictType *type;
    void *privdata;
    dictht ht[2];
    long rehashidx; /* rehashing not in progress if rehashidx == -1 */
    unsigned long iterators; /* number of iterators currently running */
} dict;
```
* type：指向dictType的指针，保存了一组函数如下。
* privdata：私有数据，保存着dictType结构中函数的参数
* rehashidx：记录了rehash的进度，没有进度的时候值是-1。
* iterators: 正在迭代的迭代器数量。
* ht：通常只用到ht[0],只有在rehash的时候才用到ht[1]。

```
typedef struct dictType { 
    unsigned int (*hashFunction)(const void *key);  //计算哈希值
    void *(*keyDup)(void *privdata, const void *key);   //复制key
    void *(*valDup)(void *privdata, const void *obj);   //复制value 
    int (*keyCompare)(void *privdata, const void *key1, const void *key2);  //对比key 
    void (*keyDestructor)(void *privdata, void *key);   //销毁key
    void (*valDestructor)(void *privdata, void *obj);   //销毁val 
} dictType;

```

整个hash结构
![](http://ww1.sinaimg.cn/large/006QFgWMgy1g1aftl3cjej30lw0a00t5.jpg)

### rehash

和Java中的集合一样，Redis中的hash也需要随着hash表中键值对数量的多少进行扩容和收缩。Redis对于哈希表的扩容和收缩时通过rehash进行的，步骤如下：

1. 为字典中ht[1]哈希表分配内存空间，空间的大小取决于ht[0]包含的键值对的数量（ht[0].used的属性的值）
   * 扩展：ht[1]的大小为第一个大于等于ht[0].used * 2的 2^n
   * 收缩：ht[1]的大小为第一个大于等于ht[0].used的 2^n
2. 将ht[0]的键值对rehash到ht[1]上：重新计算hash值和索引值。
3. 当ht[0]所有的键值对rehash完后，释放ht[0],将ht[1]设置为ht[0]，并新创建一个空白的哈希表ht[1]。

那什么时候进行扩容和收缩呢？

* 服务器没有执行BGSAVE或者BGREWRITEAOF命令时，哈希表的负载因子大于等于1
* 服务器执行BGSAVE或者BGREWRITEAOF命令时，哈希表的负载因子大于等于5

负载因子的计算公式：
```
load_factor = ht[0].used / ht[0].size
```
> 当负载因子小于0.1的时候，会自动对哈希表进行收缩操作

当哈希表中的键值对容量很大时，进行rehash时会有庞大的计算量，服务器的性能受到很大的影响。服务器不是一次性的全部rehash到新的哈希表，而是采用渐进式rehash。

渐进式rehash的步骤：
1. 为ht[1]分配空间，
2. 字典结构dict中的一个成员rehashidx，当rehashidx为-1时表示不进行rehash，当rehashidx值为0时，表示rehash开始。
3. 在rehash期间，每次对字典的添加、删除、查找、或更新操作时，除了执行指定操作外，会顺带将ht[0]在rehashidx索引上的所有键值对rehash到ht[1],完成时rehashidx+1。
4. 当rehash时完成，将rehashidx置为-1，表示完成rehash全部完成。

> rehash过程中，字典的添加、删除、查找、更新操作都会在ht[0],ht[1]上同时进行。查找的时候先会在ht[0]上查找，没有的话查找ht[1]。新增的键值对会被添加到ht[1]上面，保证ht[0]包含的键值对数量只减不增。

### 源码分析

> 扩容操作_dictExpandIfNeeded(dict *d):
```
static int _dictExpandIfNeeded(dict *d)
{
    if (dictIsRehashing(d)) return DICT_OK; //正在进行rehash

    if (d->ht[0].size == 0) return dictExpand(d, DICT_HT_INITIAL_SIZE);

    if (d->ht[0].used >= d->ht[0].size &&
        (dict_can_resize ||
         d->ht[0].used/d->ht[0].size > dict_force_resize_ratio))
    {
        return dictExpand(d, d->ht[0].used*2); //键值对数量的2倍扩容
    }
    return DICT_OK;
}
```

> 收缩操作dictResize(dict *d):
```
int dictResize(dict *d)
{
    int minimal;

    if (!dict_can_resize || dictIsRehashing(d)) return DICT_ERR;
    minimal = d->ht[0].used;
    if (minimal < DICT_HT_INITIAL_SIZE)
        minimal = DICT_HT_INITIAL_SIZE;
    return dictExpand(d, minimal); //键值对数量大小收缩
}

```

> 扩容和收缩的操作都调用了dictExpand(dict *d, unsigned long size)

```int dictExpand(dict *d, unsigned long size)
{
    if (dictIsRehashing(d) || d->ht[0].used > size) //键值对的数量大于扩容的size
        return DICT_ERR;

    dictht n; //新建哈希表
    unsigned long realsize = _dictNextPower(size);

    if (realsize == d->ht[0].size) return DICT_ERR; //rehash前后的哈希表大小相等

    //为新的哈希表分配内存，初始化所有指针为null
    n.size = realsize;
    n.sizemask = realsize-1;
    n.table = zcalloc(realsize*sizeof(dictEntry*));
    n.used = 0;

    if (d->ht[0].table == NULL) {//如果ht[0]为空，将新的哈希表n设置为ht[0]
        d->ht[0] = n;
        return DICT_OK;
    }

    d->ht[1] = n;   //将新的哈希表n设置为ht[0]
    d->rehashidx = 0;   //设置rehash标志位为0，开始渐进式rehash（incremental rehashing）
    return DICT_OK;
}
```
> 渐进式rehash：
```
static void _dictRehashStep(dict *d) {
    if (d->iterators == 0) dictRehash(d,1);
}

int dictRehash(dict *d, int n) {
    int empty_visits = n*10; 
    if (!dictIsRehashing(d)) return 0;  //是否正在进行rehash

    while(n-- && d->ht[0].used != 0) {
        dictEntry *de, *nextde;

        assert(d->ht[0].size > (unsigned long)d->rehashidx);

    
        //找到哈希表中不为空的数组，rehashidx移动到ht[0]节点table[d->rehashidx]

        while(d->ht[0].table[d->rehashidx] == NULL) {
            d->rehashidx++;
            if (--empty_visits == 0) return 1;
        }

        de = d->ht[0].table[d->rehashidx];  //获取ht[0]下标为rehashidx的节点
        
        while(de) {
            uint64_t h;

            nextde = de->next;
            
            h = dictHashKey(d, de->key) & d->ht[1].sizemask;    //获得哈希值按位与新的哈希表的size哈希表中的数组index

            //将该节点插入到数组index下标为h的位置
            de->next = d->ht[1].table[h];
            d->ht[1].table[h] = de;
            d->ht[0].used--;    //ht[0].used减一
            d->ht[1].used++;    //当ht[1].used加一
            de = nextde;
        }
        d->ht[0].table[d->rehashidx] = NULL;    //该节点rehash完成后ht[0]上的该节点置为null
        d->rehashidx++; //rehash+1
    }

    if (d->ht[0].used == 0) {   //当ht[0]的键值对全部rehash完成
        zfree(d->ht[0].table);  //释放旧的ht[0]
        d->ht[0] = d->ht[1];    //将ht[1]设置成ht[0]
        _dictReset(&d->ht[1]);  //新建一个ht[1]
        d->rehashidx = -1;      //哈希表的rehashidx设置成-1
        return 0;   //rehash已经完成
    }


    return 1;   //还有节点需要rehash
}
```


