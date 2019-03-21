---
title: Redis原理剖析（二） 链表
updated: 2019/3/15 20:00:00
tags:
- Redis
- 链表
categories:
- Redis
---

![redis_list](http://ww1.sinaimg.cn/large/006QFgWMgy1g13g2d59vaj34is30jkjl.jpg)

### 前言

链表提供了高效的节点重排能力，以及顺序节点的访问方式，增删节点的时间复杂度都是O(1)。Redis中的列表键的底层是阿剑就是链表，发布与订阅，慢查询，监视器等也用到链表，Redis服务器本身还是用链表保存多喝客户端的状态信息，以及用链表来构建客户端输出缓存区。

### 链表的结构

链表节点结构在根目录下adlist.h/listNode :

<!-- more --> 

```
typedef struct listNode {
    struct listNode *prev;  //前驱节点，如果是list的头结点，则prev指向NULL
    struct listNode *next;  //后继节点，如果是list尾部结点，则next指向NULL
    void *value;    //万能指针，能够存放任何信息
} listNode;
```

有链表节点的结构前驱和后继节点组成了双向链表，

表头的实现：
```
typedef struct list {
    listNode *head; //头节点
    listNode *tail; //尾节点
    void *(*dup)(void *ptr);    //复制链表节点的值
    void (*free)(void *ptr);    //释放来链表节点的值
    int (*match)(void *ptr, void *key); //比较两个值是否相等
    unsigned long len;  //链表长度
} list;
```

表头和节点组成的链表结构图：

![](http://ww1.sinaimg.cn/large/006QFgWMgy1g13chdmv27j30it06raab.jpg)

链表的特性：

* 双端：获取某个节点的前置和后置节点的复杂度都是O(1)

* 无环：头节点前置指针和尾节点后置指针都指向NULL,对链表的访问以NULL为终点

* head和tail指针：对于链表的头结点和尾结点操作的复杂度为O(1)。

* len ：获取链表中节点数量的复杂度为O(1)。

* dup、free和match指针：实现多态，链表节点listNode使用万能指针void *保存节点的值，而表头list使用dup、free和match指针来针对链表中存放的不同对象从而实现不同的方法。

### 主要源码分析

> listCreate： 创建一个不包含任何节点的新链表，时间复杂度O(1)
```
list *listCreate(void)
{
    struct list *list;

    if ((list = zmalloc(sizeof(*list))) == NULL)    //表头分配内存
        return NULL;
    // 初始化表头
    list->head = list->tail = NULL;
    list->len = 0;
    list->dup = NULL;
    list->free = NULL;
    list->match = NULL;
    return list;
}
```

> listAddNodeHead： 新节点添加到链表的表头，时间复杂度O(1)
```
list *listAddNodeHead(list *list, void *value)
{
    listNode *node;

    if ((node = zmalloc(sizeof(*node))) == NULL)    //新节点分配内存空间
        return NULL;
    node->value = value;    //设置节点的值
    if (list->len == 0) {   //原链表为空
        list->head = list->tail = node;
        node->prev = node->next = NULL;
    } else {    //原链表不为空
        node->prev = NULL;
        node->next = list->head;
        list->head->prev = node;
        list->head = node;
    }
    list->len++;    //长度加1
    return list;
}
```

> listAddNodeTail： 新节点添加到链表的表尾，时间复杂度O(1)
```
list *listAddNodeTail(list *list, void *value)
{
    listNode *node;

    if ((node = zmalloc(sizeof(*node))) == NULL)
        return NULL;
    node->value = value;
    if (list->len == 0) {
        list->head = list->tail = node;
        node->prev = node->next = NULL;
    } else {
        node->prev = list->tail;
        node->next = NULL;
        list->tail->next = node;
        list->tail = node;
    }
    list->len++;
    return list;
}
```

> listInsertNode： 新节点插到给定节点的前或者后，时间复杂度O(1)
```
list *listInsertNode(list *list, listNode *old_node, void *value, int after) {
    listNode *node;

    if ((node = zmalloc(sizeof(*node))) == NULL)
        return NULL;
    node->value = value;
    if (after) {    //after非零，新节点插入到后面
        node->prev = old_node;
        node->next = old_node->next;
        if (list->tail == old_node) {   //节点是尾节点，更新tail指针
            list->tail = node;
        }
    } else {
        node->next = old_node;
        node->prev = old_node->prev;
        if (list->head == old_node) {
            list->head = node;
        }
    }
    if (node->prev != NULL) {
        node->prev->next = node;
    }
    if (node->next != NULL) {
        node->next->prev = node;
    }
    list->len++;
    return list;
}
```

> listSearchKey： 查找并返回给定值的节点，时间复杂度O(N)，N为链表长度
```
listNode *listSearchKey(list *list, void *key)
{
    listIter iter;
    listNode *node;

    listRewind(list, &iter);    //创建迭代器
    while((node = listNext(&iter)) != NULL) {   //迭代整个链表
        if (list->match) {  //如果设置list结构中的match方法，则用该方法比较
            if (list->match(node->value, key)) {
                return node;
            }
        } else {
            if (key == node->value) {
                return node;
            }
        }
    }
    return NULL;
}
```

> listIndex： 返回链表给定索引的节点，时间复杂度O(N)
```
listNode *listIndex(list *list, long index) {
    listNode *n;

    if (index < 0) {    //如果下标为负数，从链表尾部开始
        index = (-index)-1;
        n = list->tail;
        while(index-- && n) n = n->prev;
    } else {    //如果下标为负数，从链表头部开始
        n = list->head;
        while(index-- && n) n = n->next;
    }
    return n;
}
```

> listDelNode：删除给定节点，时间复杂度O(N)
```
void listDelNode(list *list, listNode *node)
{
    if (node->prev) //更新前驱节点指针
        node->prev->next = node->next;
    else
        list->head = node->next;
    if (node->next) //更新后驱结点指针
        node->next->prev = node->prev;
    else
        list->tail = node->prev;
    if (list->free) list->free(node->value);
    zfree(node);
    list->len--;
}
```

> listRotate： 弹出表尾节点，插到表头，成为新的表头节点，时间复杂度O(1)
```
void listRotate(list *list) {
    listNode *tail = list->tail;

    if (listLength(list) <= 1) return;

    /* Detach current tail */
    list->tail = tail->prev;
    list->tail->next = NULL;
    /* Move it as head */
    list->head->prev = tail;
    tail->prev = NULL;
    tail->next = list->head;
    list->head = tail;
}
```

> listDup： 复制一个给定链表的副本，时间复杂度O(N)
```
list *listDup(list *orig)
{
    list *copy;
    listIter iter;
    listNode *node;

    if ((copy = listCreate()) == NULL)
        return NULL;
    copy->dup = orig->dup;
    copy->free = orig->free;
    copy->match = orig->match;
    listRewind(orig, &iter);
    while((node = listNext(&iter)) != NULL) {
        void *value;

        if (copy->dup) {
            value = copy->dup(node->value);
            if (value == NULL) {
                listRelease(copy);
                return NULL;
            }
        } else
            value = node->value;
        if (listAddNodeTail(copy, value) == NULL) {
            listRelease(copy);
            return NULL;
        }
    }
    return copy;
}
```

> listRelease： 释放给定链表和链表中的节点，时间复杂度O(N)
```
void listRelease(list *list)
{
    listEmpty(list);
    zfree(list);
}
```
