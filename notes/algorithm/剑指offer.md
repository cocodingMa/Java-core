## 剑指offer

利用Java和Python两种方式实现，数据结构和算法

<!-- TOC -->

- [剑指offer](#剑指offer)
    - [二维数组中的查找](#二维数组中的查找)
        - [题目描述：](#题目描述)
        - [Java实现](#java实现)
        - [Python实现](#python实现)
    - [替换空格](#替换空格)
        - [题目描述](#题目描述)
        - [Java实现](#java实现-1)
        - [Python实现](#python实现-1)
    - [从尾到头打印链表](#从尾到头打印链表)
        - [题目描述](#题目描述-1)
        - [Java实现](#java实现-2)
        - [Python实现](#python实现-2)
    - [重建二叉树](#重建二叉树)
        - [题目描述](#题目描述-2)
        - [Java实现](#java实现-3)
        - [Python实现](#python实现-3)

<!-- /TOC -->

### 二维数组中的查找

#### 题目描述：

在一个二维数组中（每个一维数组的长度相同），每一行都按照从左到右递增的顺序排序，每一列都按照从上到下递增的顺序排序。请完成一个函数，输入这样的一个二维数组和一个整数，判断数组中是否含有该整数

#### Java实现

```
public class Solution{
    public boolean Find(int target, int [][] array){
        int tR = array.length-1;
        int tC1 = array[0].length-1;
        int tC = 0;
        while(tR >=0 && tC <= tC1){
            if(target > array[tR][tC] ){
                tC++;
            }else if(target < array[tR][tC]){
                tR--;
            }else{
                return true;
            }
        }
        return false;
    }
} 
```

#### Python实现

```
class Solution:
    def Find(self, target, array):
        tR = len(array)-1
        tC1 = len(array[0])-1
        tC = 0
        while tC<=tC1 and tR>=0:
            if target<array[tR][tC]:
                tR -= 1
            elif target>array[tR][tC]:
                tC += 1
            else:
                return True
        return False
```

### 替换空格

#### 题目描述

请实现一个函数，将一个字符串中的每个空格替换成“%20”。例如，当字符串为We Are Happy.则经过替换之后的字符串为We%20Are%20Happy。

#### Java实现

```
public class Solution {
    public String replaceSpace(StringBuffer str) {
    	if(str == null){
            return null;
        }
        int blankNum = 0;
        int length = str.length();
        for(int i = 0; i < length; i++){
            if(str.charAt(i) == ' '){
                blankNum++;
            }
        }
        int newLength = length + 2 * blankNum;
        char[] newChars = new char[newLength];
        int index = newLength - 1;
        for( int j = length - 1; j >= 0; j --){
            if(str.charAt(j) == ' '){
                newChars[index--] = '0';
                newChars[index--] = '2';
                newChars[index--] = '%';
            }
            else {
                newChars[index--] = str.charAt(j);
            }
        }
        return new String(newChars);
    }
}
```

#### Python实现

```
class Solution:
    def replaceSpace(self, s):
        num_space = 0
        for i in s:
            if i == ' ':
                num_space += 1

        new_length = len(s) + 2 * num_space
        index_origin = len(s) - 1
        index_new = new_length - 1
        new_string = [None for i in range(new_length)]

        while index_origin >= 0 & (index_new > index_origin):
            if s[index_origin] == ' ':
                new_string[index_new] = '0'
                index_new -= 1
                new_string[index_new] = '2'
                index_new -= 1
                new_string[index_new] = '%'
                index_new -= 1
            else:
                new_string[index_new] = s[index_origin]
                index_new -= 1
            index_origin -= 1
        return ''.join(new_string)
```

### 从尾到头打印链表

#### 题目描述

输入一个链表，按链表值从尾到头的顺序返回一个ArrayList。


#### Java实现

采用很简洁的递归实现，递归的本质是使用堆栈结构。

```
public class Solution {
    ArrayList<Integer> arrayList=new ArrayList<Integer>();
    public ArrayList<Integer> printListFromTailToHead(ListNode listNode) {
        if(listNode!=null){
            this.printListFromTailToHead(listNode.next);
            arrayList.add(listNode.val);
        }
        return arrayList;
    }
}
```

#### Python实现

```
# -*- coding:utf-8 -*-
# class ListNode:
#     def __init__(self, x):
#         self.val = x
#         self.next = None
 
class Solution:
    def printListFromTailToHead(self, listNode):
        # write code here
        if listNode is None:
            return []
        return self.printListFromTailToHead(listNode.next)+[listNode.val]
```

### 重建二叉树

#### 题目描述

输入某二叉树的前序遍历和中序遍历的结果，请重建出该二叉树。假设输入的前序遍历和中序遍历的结果中都不含重复的数字。例如输入前序遍历序列{1,2,4,7,3,5,6,8}和中序遍历序列{4,7,2,1,5,3,8,6}，则重建二叉树并返回。

#### Java实现
```
public class Solution {
    public TreeNode reConstructBinaryTree(int [] pre,int [] in) {
        TreeNode root = reConstructBinaryTree(pre,0,pre.length-1,in,0,in.length-1);
        return root;    
    }
    private TreeNode reConstructBinaryTree(int [] pre,int startPre,int endPre,int [] in,int startIn,int endIn) {
        if(startPre>endPre||startIn>endIn)
            return null;
        TreeNode root=new TreeNode(pre[startPre]);
        for(int i=startIn;i<=endIn;i++)
            if(in[i]==pre[startPre]){
                root.left=reConstructBinaryTree(pre,startPre+1,startPre+i-startIn,in,startIn,i-1);
                root.right=reConstructBinaryTree(pre,i-startIn+startPre+1,endPre,in,i+1,endIn);
            }
                  
        return root;
    }
}
```

#### Python实现
```
class Solution:
    def reConstructBinaryTree(self, pre, tin):
        if not pre or not tin:
            return None
        root = TreeNode(pre.pop(0))
        index = tin.index(root.val)
        root.left = self.reConstructBinaryTree(pre, tin[:index])
        root.right = self.reConstructBinaryTree(pre, tin[index + 1:])
        return root
```