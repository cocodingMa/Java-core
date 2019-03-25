---
title: Redis原理剖析（四） 跳跃表
updated: 2019/3/22 20:00:00
tags:
- Redis
- 跳跃表
categories:
- Redis
---

![redis_skiplist](http://ww1.sinaimg.cn/large/006QFgWMgy1g1bqw7jsuoj30m80ett9r.jpg)

### 前言

跳跃表（skiplist）是一种有序的数据结构，在链表的结构上进行扩展。普通链表结构查找某个节点的时间复杂度为O(N), 查找跳跃表的复杂度是O(logN)，效率可以代替平衡二叉树（如红黑树）。

### 跳跃表的结构

Redis中的跳跃表是由节点zskiplistNode和存放节点的zskiplist构成。

跳跃表的结构如下图：

![](http://ww1.sinaimg.cn/large/006QFgWMgy1g1bos3vecej30lh077jrv.jpg)

<!-- more --> 

节点zskiplistNode在根目录下server.h/zskiplistNode :
```
typedef struct zskiplistNode {
    // sds对象
    sds ele;
    // 分值
    double score;
    // 后退指针
    struct zskiplistNode *backward;
    //层
    struct zskiplistLevel {
        //前进指针
        struct zskiplistNode *forward;
        // 跨度
        unsigned long span;
    } level[];
} zskiplistNode;

```
1. forward: 前进指针。普通的链表节点只有一个指向下一个节点的指针next，跳跃表的节点中每层都有一个前进指针，指向任意一个尾部节点。可以在时间复杂度0(1)访问该节点指向的对应节点。
2. span: 跨度。记录了两个节点的距离, 跨度越大相距越远，也表示两个节点之间的节点数目。所有指向null的节点跨度都为0。
3. backward: 后退指针。可以从表尾向表头访问节点，每个节点只有一个后退指针，所以每次只能后退至一个节点。当遇到指向null的后退指针的时候表示遍历结束。
4. score: 分值。是一个double类型的浮点数，所有的节点都按照分值从小到大排序。
5. ele: sds对象。用于保存字符串。

跳跃表是由多个跳跃表节点构成，zskiplist结构就是用来保存表节点。

zskiplist定义在根目录下server.h/zskiplist :
```
typedef struct zskiplist {
    //头结点和尾节点
    struct zskiplistNode *header, *tail;
    //节点的数量
    unsigned long length;
    //层数最多的节点的层数
    int level;
} zskiplist;
```
* 表头结点和尾节点的指针可以在O(1)的时间复杂度之内获取头尾节点。
* 不用遍历全部节点就可以获取节点的数量。
* 可以在O(1)复杂度内获取节点层数最多的那个节点的层数

### 主要源码分析

> zslCreate: 创建一个新的跳跃表，时间复杂度0(1)

```
/* Create a new skiplist. */
zskiplist *zslCreate(void) {
    int j;
    zskiplist *zsl;

    zsl = zmalloc(sizeof(*zsl));    //分配内存空间
    zsl->level = 1;     //设置level
    zsl->length = 0;    //设置节点数
    //创建一个层数为32跳跃表头节点
    zsl->header = zslCreateNode(ZSKIPLIST_MAXLEVEL,0,NULL); 
    for (j = 0; j < ZSKIPLIST_MAXLEVEL; j++) {  
        zsl->header->level[j].forward = NULL;   //头节点的前进指针置成null
        zsl->header->level[j].span = 0; //跨度为0
    }   
    zsl->header->backward = NULL;   //头节点的后退指针置成null
    zsl->tail = NULL;   //指向尾节点的指针置为null
    return zsl; 
}
```

> zslFree: 释放一个表头，O(N)
```
void zslFree(zskiplist *zsl) {
    zskiplistNode *node = zsl->header->level[0].forward, *next;

    zfree(zsl->header); //释放头节点
    while(node) {   //释放其他节点
        next = node->level[0].forward;  
        zslFreeNode(node);  //释放节点空间
        node = next;    //获取下一个节点
    }
    zfree(zsl); //释放表头
}

```
> zslInsert: 给定分值和SDS的新节点插入到表中，平均O(logN)，最差O(N)
```
zskiplistNode *zslInsert(zskiplist *zsl, double score, robj *obj) {
    zskiplistNode *update[ZSKIPLIST_MAXLEVEL], *x;
    unsigned int rank[ZSKIPLIST_MAXLEVEL];
    int i, level;

    serverAssert(!isnan(score));
    x = zsl->header;    //获取跳跃表头节点
    for (i = zsl->level-1; i >= 0; i--) {   //遍历头节点的level
        /* store rank that is crossed to reach the insert position */
        rank[i] = i == (zsl->level-1) ? 0 : rank[i+1];  //更新rank[i]为i+1所跨越的节点数，但是最外一层为0

        while (x->level[i].forward &&   //当前层的前进指针不为空
            (x->level[i].forward->score < score ||  //当前的要插入的score大于当前层的score
                (x->level[i].forward->score == score && //当前score等于要插入的score
                compareStringObjects(x->level[i].forward->obj,obj) < 0))) { //当前层的对象与要插入的obj不等
            rank[i] += x->level[i].span;    //该层跨越的节点数和上一层遍历所跨越的节点数
            x = x->level[i].forward;    

        //记录i层的最后一个节点，层数遍历完就要在该节点后要插入节点
        update[i] = x;
    }
    
    level = zslRandomLevel();   //获取随机的层数
    if (level > zsl->level) {   //如果大于当前所有节点最大的层数时
        for (i = zsl->level; i < level; i++) {
            rank[i] = 0;    //将大于等于原来zsl->level层以上的rank[]设置为0
            update[i] = zsl->header;    //将大于等于原来zsl->level层以上update[i]指向头结点
            update[i]->level[i].span = zsl->length; //update[i]指向头结点，将i层的跨度设置为length
        }
        zsl->level = level; //设置最大层数
    }
    x = zslCreateNode(level,score,obj);   
    for (i = 0; i < level; i++) {       //遍历每一层
        x->level[i].forward = update[i]->level[i].forward;  //设置新节点的前进指针

        /* update span covered by update[i] as x is inserted here */
        x->level[i].span = update[i]->level[i].span - (rank[0] - rank[i]);  //更新节点的跨度
        update[i]->level[i].span = (rank[0] - rank[i]) + 1;     //更新节点前一个节点的跨度
    }

    /* increment span for untouched levels */
    for (i = level; i < zsl->level; i++) {  //如果插入节点的level小于原来的zsl->level才会执行
        update[i]->level[i].span++; //因为高度没有达到这些层，所以只需将查找时每层最后一个节点的值的跨度加1
    }

    x->backward = (update[0] == zsl->header) ? NULL : update[0];
    if (x->level[0].forward)    //如果x节点不是最尾部的节点
        x->level[0].forward->backward = x;  //就将x节点后面的节点的后退节点设置成为x地址
    else
        zsl->tail = x;  //否则更新表头的tail指针，指向最尾部的节点x
    zsl->length++;  //跳跃表节点计数器加1
    return x;   //返回x地址
}
```
> zslDeleteNode: 删除节点，平均O(logN)，最差O(N)
```
void zslDeleteNode(zskiplist *zsl, zskiplistNode *x, zskiplistNode **update) {
    int i;
    for (i = 0; i < zsl->level; i++) {
        if (update[i]->level[i].forward == x) {
            update[i]->level[i].span += x->level[i].span - 1;   //前一个节点的跨度-1
            update[i]->level[i].forward = x->level[i].forward;
        } else {
            update[i]->level[i].span -= 1;  //i层的最后一个节点的跨度减1
        }
    }
    //设置后退指针
    if (x->level[0].forward) {      //删除的节点的前进指针存在
        x->level[0].forward->backward = x->backward;    //前进节点的后退指针指向删除节点的后退指针
    } else {
        zsl->tail = x->backward;
    }
    //更新跳跃表最大层数
    while(zsl->level > 1 && zsl->header->level[zsl->level-1].forward == NULL)
        zsl->level--;
    zsl->length--;
}
```
> zslGetRank: 获取节点的排名，平均O(logN)，最差O(N)
```
unsigned long zslGetRank(zskiplist *zsl, double score, sds ele) {
    zskiplistNode *x;
    unsigned long rank = 0;
    int i;

    x = zsl->header;
    for (i = zsl->level-1; i >= 0; i--) {
        while (x->level[i].forward &&
            (x->level[i].forward->score < score ||
                (x->level[i].forward->score == score &&
                sdscmp(x->level[i].forward->ele,ele) <= 0))) {
            rank += x->level[i].span;
            x = x->level[i].forward;
        }

        /* x might be equal to zsl->header, so test if obj is non-NULL */
        if (x->ele && sdscmp(x->ele,ele) == 0) {
            return rank;
        }
    }
    return 0;
}
```

> zslFirstInRange: 给定一个分值范围，返回跳跃表中第一个符合这个范围的节点，平均O(logN)，最差O(N)
```
zskiplistNode *zslFirstInRange(zskiplist *zsl, zrangespec *range) {
    zskiplistNode *x;
    int i;

    /* If everything is out of range, return early. */
    if (!zslIsInRange(zsl,range)) return NULL; //如果不在范围内，则返回NULL，确保至少有一个节点符号range

    //判断下限
    x = zsl->header;
    for (i = zsl->level-1; i >= 0; i--) {   //遍历每一层
        while (x->level[i].forward &&   //如果该层有下一个节点且
            !zslValueGteMin(x->level[i].forward->score,range))  //当前节点的score还小于(小于等于)range的min
                x = x->level[i].forward;    //继续指向下一个节点
    }

    x = x->level[0].forward;//找到目标节点
    serverAssert(x != NULL);//保证能找到

    /* Check if score <= max. */
    //判断上限
    if (!zslValueLteMax(x->score,range)) return NULL; //该节点的值如果比max还要大，就返回NULL
    return x;
}
```

> zslLastInRange: 给定一个分值范围，返回跳跃表中最后一个符合这个范围的节点，平均O(logN)，最差O(N)
```
zskiplistNode *zslLastInRange(zskiplist *zsl, zrangespec *range) {
    zskiplistNode *x;
    int i;

    if (!zslIsInRange(zsl,range)) return NULL;

    x = zsl->header;
    for (i = zsl->level-1; i >= 0; i--) {
        while (x->level[i].forward &&
            zslValueLteMax(x->level[i].forward->score,range))
                x = x->level[i].forward;
    }

    serverAssert(x != NULL);

    if (!zslValueGteMin(x->score,range)) return NULL;
    return x;
}
```
