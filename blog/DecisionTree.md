---
title: 决策树
updated: 2018/10/18 10:00:00
tags:
- 机器学习
- 决策树
categories:
- 机器学习
mathjax: true
---


![decision](http://ww1.sinaimg.cn/large/006QFgWMgy1fwcn9iq207j32bc1jkqmj.jpg)

### ID3算法

该核心就是在决策树各个节点上用信息增益准则来选择特征，递归的构建决策树。

#### 信息增益

信息熵$H(x)$：用来表示随机变量不确定性的度量，熵只依赖$X$的分布，和$X$的取值无关，熵越大随机变量的不确定就越大。特征$x$取值$x_i$的概率为$p_i$,$x_i$的熵计算公式：

$$H(x) = -\sum_{i=1}^{n}p_ilogp_i$$

条件熵$H(Y|X)$：表示在已知随机变量$X$的条件下，随机变量$Y$的不确定性：

$$H(Y|X) = -\sum_{i=1}^{n}p_iH(Y|X=x_i)$$

信息增益：已知随机变量$Y$后，随即变量$X$的不确定性的减少程度

<!-- more --> 
特征`Y`对训练数据集`X`的信息增益`g(X, Y)`,定义为集合`X`的经验熵`H(X)`与特征Y给定条件下X的经验条件熵`H(X|Y)`之差:

$$g(X, Y) = H(X)-H(X|Y)$$

一般来说，信息增益越大，意味着使用属性`Y`来进行划分所得的‘纯度提升’越大，



ID3算法就是选择信息增益最大的特征来对当前节点进行分类。

#### ID3决策树的生成

从根节点开始，对节点计算所有可能的特征的信息增益，选择信息增益最大的特征作为节点的特征，由该特征的不同取值建立子节点，再对子节点递归调用上述方法构建决策树，直到所有特征的信息增益均很小或者没有特征选择为止。相当于用极大似然法进行概率模型的选择。

输入：训练数据集D，特征集A，阈值$\epsilon$;

输出：决策树T

1.
2.
3.
4.
5.
6.

#### ID3缺点

### C4.5算法

#### 信息增益比

#### C4.5决策树的生成

#### C4.5的缺点

### 决策树的剪枝

#### 预剪枝

#### 后剪枝

### CART算法