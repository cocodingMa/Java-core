---
title: Redis原理剖析（一） 字符串
updated: 2019/3/14 20:00:00
tags:
- Redis
- 字符串
categories:
- Redis
---

![redis_sds](http://ww1.sinaimg.cn/large/006QFgWMgy1g139xcngdoj34mo2lsu0y.jpg)

### 前言

Redis采用了一种独特的字符串对象SDS（simple dynamic string），该对象兼容了C语言字符串类型，用于保存数据库中的字符串的值。

### SDS的结构

redis源码目录下的sds.h关于SDS的定义

<!-- more --> 

```
typedef char *sds;
```

SDS通过字符串的长度有5种不同的数据类型，：sdshdr5，sdshdr8，sdshdr16， sdshdr32， sdshdr64。不同的结构体大小不同，可用于节省内存。

以sdshdr16为例，可用于2^16-1长度的字符串：

```
struct __attribute__ ((__packed__)) sdshdr16 {
    uint16_t len; /* used */
    uint16_t alloc; /* excluding the header and null terminator */
    unsigned char flags; /* 3 lsb of type, 5 unused bits */
    char buf[];
};
```

* len 字符串占buff空间的长度

* alloc 未占用的空间（去除header和terminator的占用空间） 

* flags 结构体的实际类型

* buf[] 数据空间，用于保存数据

`len`时间复杂度O(1)获取整个字符串的长度， `buff[]`基于char[]数组实现

### SDS与C字符串的区别

* 获取字符串长度复杂度O(1)

C字符串不记录自身长度，获取长度的时候需要遍历整个字符串。SDS的头部有len属性记录字符串的长度，获取长度的时间复杂度O(1)。

* 杜绝缓存区溢出

当对SDS进行修改的时候，会先检查空间是否满足，不满足将会扩展空间到所需状态，然后进行修改。不需要手动修改空间大小，不会出现缓存区溢出

* 内存重分配

SDS会进行空间与分配，会有额外的空间剩余。SDS缩短字符串时，不会回收缩短产生的多余字节

* 二进制安全

C语言是通过空字符来判断字符串是否结束，SDS通过len这个属性。


* 总结

![](http://ww1.sinaimg.cn/large/006QFgWMgy1g12f1dqr9jj30gl04rglx.jpg)

### 主要源码分析

> sdsnew: 创建一个给定字符串的SDS，时间复杂度O(N)，N为字符串长度
```
sds sdsnew(const char *init) {
    size_t initlen = (init == NULL) ? 0 : strlen(init);
    return sdsnewlen(init, initlen);
}

sds sdsnewlen(const void *init, size_t initlen) {   //创建一个长度为initlen的SDS, 保存init字符串中的值
    void *sh;
    sds s;
    char type = sdsReqType(initlen);    // 根据长度获取数据类型
    /* Empty strings are usually created in order to append. Use type 8
     * since type 5 is not good at this. */
    if (type == SDS_TYPE_5 && initlen == 0) type = SDS_TYPE_8;  //SDS_TYPE_5 never used
    int hdrlen = sdsHdrSize(type);
    unsigned char *fp; /* flags pointer. */

    sh = s_malloc(hdrlen+initlen+1);
    if (init==SDS_NOINIT)
        init = NULL;
    else if (!init)
        memset(sh, 0, hdrlen+initlen+1);
    if (sh == NULL) return NULL;
    s = (char*)sh+hdrlen;
    fp = ((unsigned char*)s)-1;
    switch(type) {
        case SDS_TYPE_5: {
            *fp = type | (initlen << SDS_TYPE_BITS);
            break;
        }
        case SDS_TYPE_8: {
            SDS_HDR_VAR(8,s);
            sh->len = initlen;  //设置表头的len
            sh->alloc = initlen;    //设置表头的alloc
            *fp = type;
            break;  //结构体的类型
        }
        // 省去SDS_TYPE_16，SDS_TYPE_32，SDS_TYPE_64
    }
    if (initlen && init)
        memcpy(s, init, initlen);   ////将指定的字符串init拷贝到表头的buf中
    s[initlen] = '\0';  //以'\0'结尾
    return s;
}
```

> sdsempty: 创建一个空的SDS, 时间复杂度O(1)
```
sds sdsempty(void) {
    return sdsnewlen("",0);
}
```

> sdsfree: 释放给定的SDS，时间复杂度O(N)，N为释放长度
```
void sdsfree(sds s) {
    if (s == NULL) return;
    s_free((char*)s-sdsHdrSize(s[-1]));
}
```

> sdslen： 返回SDS已使用的空间字节数，时间复杂度O(1)
```
static inline size_t sdslen(const sds s) {
    unsigned char flags = s[-1];
    switch(flags&SDS_TYPE_MASK) {
        case SDS_TYPE_5:
            return SDS_TYPE_5_LEN(flags);
        case SDS_TYPE_8:
            return SDS_HDR(8,s)->len;
        case SDS_TYPE_16:
            return SDS_HDR(16,s)->len;
        case SDS_TYPE_32:
            return SDS_HDR(32,s)->len;
        case SDS_TYPE_64:
            return SDS_HDR(64,s)->len;
    }
    return 0;
}
```

> sdsavail： 返回SDS未使用的空间字节数，时间复杂度O(1)
```
static inline size_t sdsavail(const sds s) {
    unsigned char flags = s[-1];
    switch(flags&SDS_TYPE_MASK) {
        case SDS_TYPE_5: {
            return 0;
        }
        case SDS_TYPE_8: {
            SDS_HDR_VAR(8,s);
            return sh->alloc - sh->len;
        }
        case SDS_TYPE_16: {
            SDS_HDR_VAR(16,s);
            return sh->alloc - sh->len;
        }
        case SDS_TYPE_32: {
            SDS_HDR_VAR(32,s);
            return sh->alloc - sh->len;
        }
        case SDS_TYPE_64: {
            SDS_HDR_VAR(64,s);
            return sh->alloc - sh->len;
        }
    }
    return 0;
}
```

> sdsdup: 创建一个给定的SDS的副本，时间复杂度O(N)
```
sds sdsdup(const sds s) {
    return sdsnewlen(s, sdslen(s));
}
```

> sdsclear: 清空SDS字符串内容，时间复杂度O(1)
```
void sdsclear(sds s) {
    sdssetlen(s, 0);
    s[0] = '\0';
}
```

> sdscat: 将给定C字符串拼接到SDS末尾，时间复杂度O(N)，N拼接字符串的长度
```
sds sdscat(sds s, const char *t) {
    return sdscatlen(s, t, strlen(t));
}
```

> sdscatsds： 将给定的SDS拼接到另一个SDS的末尾， 时间复杂度O(N)
```
sds sdscatsds(sds s, const sds t) {
    return sdscatlen(s, t, sdslen(t));
}
```

> sdscpy： 给定的C字符串复制到SDS，覆盖原有的字符串，时间复杂度O(N)
```
sds sdscpy(sds s, const char *t) {
    return sdscpylen(s, t, strlen(t));
}
```

> sdsgrowzero： 用空字符串将SDS扩展到指定长度，时间复杂度O(N)
```
sds sdsgrowzero(sds s, size_t len) {
    size_t curlen = sdslen(s);

    if (len <= curlen) return s;
    s = sdsMakeRoomFor(s,len-curlen);
    if (s == NULL) return NULL;

    /* Make sure added region doesn't contain garbage */
    memset(s+curlen,0,(len-curlen+1)); /* also set trailing \0 byte */
    sdssetlen(s, len);
    return s;
}
```

> sdsrange： 保留SDS给定区间的数据，不在区间的数据被覆盖或者清楚，时间复杂度O(N)
```
void sdsrange(sds s, ssize_t start, ssize_t end) {
    size_t newlen, len = sdslen(s);

    if (len == 0) return;
    if (start < 0) {
        start = len+start;
        if (start < 0) start = 0;
    }
    if (end < 0) {
        end = len+end;
        if (end < 0) end = 0;
    }
    newlen = (start > end) ? 0 : (end-start)+1;
    if (newlen != 0) {
        if (start >= (ssize_t)len) {
            newlen = 0;
        } else if (end >= (ssize_t)len) {
            end = len-1;
            newlen = (start > end) ? 0 : (end-start)+1;
        }
    } else {
        start = 0;
    }
    if (start && newlen) memmove(s, s+start, newlen);
    s[newlen] = 0;
    sdssetlen(s,newlen);
}
```

> sdstrim: 移除SDS中所有在c字符串出现的字符，时间复杂度O(N^2)
```
sds sdstrim(sds s, const char *cset) {
    char *start, *end, *sp, *ep;
    size_t len;

    sp = start = s;
    ep = end = s+sdslen(s)-1;
    while(sp <= end && strchr(cset, *sp)) sp++;
    while(ep > sp && strchr(cset, *ep)) ep--;
    len = (sp > ep) ? 0 : ((ep-sp)+1);
    if (s != sp) memmove(s, sp, len);
    s[len] = '\0';
    sdssetlen(s,len);
    return s;
}
```

> sdscmp: 对比两个SDS是否相同，时间复杂度O(N)，N为短的SDS的长度
```
int sdscmp(const sds s1, const sds s2) {
    size_t l1, l2, minlen;
    int cmp;

    l1 = sdslen(s1);
    l2 = sdslen(s2);
    minlen = (l1 < l2) ? l1 : l2;
    cmp = memcmp(s1,s2,minlen);
    if (cmp == 0) return l1>l2? 1: (l1<l2? -1: 0);
    return cmp;
}
```