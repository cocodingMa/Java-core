---
title: 逻辑回归
tags:
- 机器学习
- 逻辑回归
categories:
- 机器学习
mathjax: true
---

![logistic](http://ww1.sinaimg.cn/large/006QFgWMgy1fwcniahrpnj33vc2kwnpf.jpg)

### 逻辑回归

线性回归处理的是回归问题，逻辑回归是根据输入类别，是一种分类的算法，先讨论二分类问题。假设有一个二分类问题，输出为$y\in\{0,1\}$，而线性回归模型产生的预测值为$z=w^Tx+b$是实数值，我们希望有一个理想的阶跃函数来帮我们实现z值到0/1值的转化，单位阶跃函数:

$$y =\begin{cases}0, & \text{z<0}  \\0.5, & \text{z=0}\\1,&\text{z>0}\\\end{cases}$$

常用的逻辑函数是Sigmoid函数，也称对数几率函数，表达式：

$$y={1\over1+e^{-z}}$$

<!-- more --> 

两者的图像：

![logistic](http://ww1.sinaimg.cn/large/006QFgWMgy1fwcnc1wyplj30ry0dnwg9.jpg)

###  参数估计

逻辑回归的概率模型如下：

$$P(Y=0|x) = {1\over1+e^{w^T+b}}$$

$$P(Y=1|x) = {e^{w^T+b}\over1+e^{w^T+b}}$$

设：

$$P(Y=0|x) =\pi(x) $$ $$P(Y=1|x) = 1-\pi(x)$$

则似然函数为:

$$\prod_{i=1}^{n}[\pi(x)]^{y_i}[1-\pi(x_i)]^{1-y_i}$$

对数似然函数：
$$
\begin{align}
L(w) &=\sum_{i=1}^{n}[y_ilog\pi(x_i)+(1-y_i)log(1-\pi(x_i))]\\
& = \sum_{i=1}^{n}[{y_ilog{\pi(x_i)\over1-\pi(x_i)}+log(1-\pi(x_i))}]\\
& = \sum_{i=1}^{n}[y_i(wx_i)-log(1+e^{wx_i})]\\
\end{align}
$$

最大化似然函数等价于最小化$l(w)$:
$$l(w) =\sum_{i=1}^{n}[-y_i(wx_i)+log(1+e^{wx_i})]$$ 

上式是关于$w$的高阶可到凸函数，用梯度下降，牛顿法求解最小化$l(w)$得到$w$的估计值
