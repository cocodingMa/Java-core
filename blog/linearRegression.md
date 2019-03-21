---
title: 线性回归
updated: 2018/09/17 10:00:00
tags:
- 机器学习
- 线性回归
categories:
- 机器学习
mathjax: true
---

![liner](http://ww1.sinaimg.cn/large/006QFgWMgy1fwcnafbjc4j33uw2a01ky.jpg)

### 线性回归


线性模型的预测函数：

$$f(x)=w_0+w_1x_1+w_2x_2+\cdots+w_nx_n+b$$

向量形式：

$$f_w(x)=w^Tx+b$$

损失函数(均方误差)：

$$\begin{align}
J(w)&={1\over2m} \sum_{i=1}^{m}{(f_w(x_i)-y(i))^2}\\
&={1\over2m} \sum_{i=1}^{m}{(y_i-w^Tx_i-b)^2}
\end{align}$$

<!-- more --> 

### 损失函数推导过程

训练数据和预测函数的关系可以描述为：

$$y_i=w^Tx_i-b+\epsilon_i$$
其中$y_i$是训练样本真实值，$\epsilon_i$是真实值和预测值的差值，当训练样本足够大时，有中心极限定理可知$\epsilon_i$满足$(\mu, \sigma^2)$高斯分布，令$\mu=0$,$p(\epsilon_i)$的概率分布：

$$\begin{align}
p(\epsilon_i)&={1\over\sqrt {2\pi}\sigma}e^{-{({\epsilon_i^2\over2\sigma^2})}}\\
&={1\over\sqrt {2\pi}\sigma}e^{-{({(y_i-w^Tx_i)^2\over2\sigma^2})}}\\
\end{align}$$

对上式求最大似然函数:

$$\begin{align}
L(w)&=\prod_{i=1}^{m}p(y_i|x_i,w)\\
&=\prod_{i=1}^{m}{1\over\sqrt {2\pi}\sigma}e^{-{({(y_i-w^Tx_i)^2\over2\sigma^2})}}
\end{align}$$

对数似然函数：

$$\begin{align}
l(w)&= logL(w)\\
&=log\prod_{i=1}^{m}{1\over\sqrt {2\pi}\sigma}e^{-{({(y_i-w^Tx_i)^2\over2\sigma^2})}}\\
&=\sum_{i=1}^{m}log{1\over\sqrt {2\pi}\sigma}e^{-{({(y_i-w^Tx_i)^2\over2\sigma^2})}}\\
&=mlog{1\over\sqrt {2\pi}\sigma}-{1\over\sigma^2}{1\over2}\sum_{i=1}^{m}(y_i-w^Tx_i)^2
\end{align}$$


### 最小化损失函数

最小化损失函数一般有两种方法，正规方程和梯度下降。

#### 正规方程求解:

$$
\begin{align}
L(W)&={1\over2}(XW-y)^T(XW-y)\\
& = {1\over2}[W^TX^TXW-W^TX^Ty-y^TXW+y^Ty]\\
& = {1\over2}[W^TX^TXW-2W^TX^Ty+y^Ty]
\end{align}
$$

对$L(W)$求偏导：

$$
\begin{align}
\partial(J(W))\over\partial(W)&={1\over2}(2X^TXW-X^Ty-X^Ty-0)\\
&= X^TXW-X^Ty
\end{align}
$$

令${\partial(J(W))\over \partial(W)}=0$,则有$W =(X^TX)^{-1}X^Ty$

>涉及到的矩阵求导法则：
>$${dAB \over dB}=A^T $$
>$${dX^TAX\over dX} = 2AX$$


#### 梯度下降求解：

$$w_j: = w_j - \alpha{\partial(J(w))\over\partial(w_j)}$$

>$\alpha$是学习率，$: =$是赋值

对每个$W$进行梯度求导：

$$
\begin{align}
\partial(J(W))\over\partial(W_j)&={\partial\over\partial W_j}{1\over2}(f_W(x)-y)^2\\
&= 2{1\over2}(f_W(x)-y){\partial\over\partial W_j}(f_W(x)-y)\\
&=(f_W(x)-y){\partial\over\partial W_j}(\sum_{i=1}^{n}W_ix_i-y)\\
&=f_W(x)-y)x_j
\end{align}
$$

将每个方向上的偏导数带入迭代公式，可计算出值：

$$W_j:=W_j+\alpha\sum_{i=1}^{n}(y_i-f_W(x_i))x_i^j$$

### 正规方程和梯度下降比较

![linear](http://ww1.sinaimg.cn/large/006QFgWMgy1fwcnaty8bij30ga091ab4.jpg)

总结一下，只要特征变量的数目并不大，标准方程是一个很好的计算参数$W$的替代方法。只要特征变量数量小于一万，通常使用标准方程法，而不使用梯度下降法。随着学习算法越来越复杂，例如，分类算法像逻辑回归算法，实际上对于那些算法，并不能使用标准方程法。对于那些更复杂的学习算法，不得不仍然使用梯度下降法。因此，梯度下降法是一个非常有用的算法，可以用在有大量特征变量的线性回归问题。但对于这个特定的线性回归模型，标准方程法是一个比梯度下降法更快的替代算法。


      

        



