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
    - [用两个栈实现队列](#用两个栈实现队列)
        - [题目描述](#题目描述-3)
        - [Java实现](#java实现-4)
        - [Python实现](#python实现-4)
    - [旋转数组的最小数字](#旋转数组的最小数字)
        - [题目描述](#题目描述-4)
        - [Java实现](#java实现-5)
        - [Python实现](#python实现-5)
    - [斐波那契数列](#斐波那契数列)
        - [题目描述](#题目描述-5)
        - [Java实现](#java实现-6)
        - [Python实现](#python实现-6)
    - [跳台阶](#跳台阶)
        - [题目描述](#题目描述-6)
        - [Java实现](#java实现-7)
        - [Python实现](#python实现-7)
    - [变态跳台阶](#变态跳台阶)
        - [题目描述](#题目描述-7)
        - [Java实现](#java实现-8)
        - [Python实现](#python实现-8)
    - [链表中倒数第k个节点](#链表中倒数第k个节点)
        - [题目描述](#题目描述-8)
        - [Java实现](#java实现-9)
        - [Python实现](#python实现-9)

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

### 用两个栈实现队列

#### 题目描述

用两个栈来实现一个队列，完成队列的Push和Pop操作。 队列中的元素为int类型。

#### Java实现
```
public class Solution {
    Stack<Integer> stack1 = new Stack<Integer>();
    Stack<Integer> stack2 = new Stack<Integer>();
     
    public void push(int node) {
        stack1.push(node);
    }
     
    public int pop() {
        if(stack1.empty()&&stack2.empty()){
            throw new RuntimeException("Queue is empty!");
        }
        if(stack2.empty()){
            while(!stack1.empty()){
                stack2.push(stack1.pop());
            }
        }
        return stack2.pop();
    }
}
```

#### Python实现
```
class Solution:
    def __init__(self):
        self.stackA = []
        self.stackB = []
         
    def push(self, node):
        self.stackA.append(node)
         
    def pop(self):
        if self.stackB:
            return self.stackB.pop()
        elif not self.stackA:
            return None
        else:
            while self.stackA:
                self.stackB.append(self.stackA.pop())
            return self.stackB.pop()
```

### 旋转数组的最小数字

#### 题目描述

把一个数组最开始的若干个元素搬到数组的末尾，我们称之为数组的旋转。 输入一个非减排序的数组的一个旋转，输出旋转数组的最小元素。 例如数组{3,4,5,1,2}为{1,2,3,4,5}的一个旋转，该数组的最小值为1。 NOTE：给出的所有元素都大于0，若数组大小为0，请返回0。

#### Java实现
```
public class Solution {
    public int minNumberInRotateArray(int [] array) {
        int low = 0 ; int high = array.length - 1;   
        while(low < high){
            int mid = low + (high - low) / 2;        
            if(array[mid] > array[high]){
                low = mid + 1;
            }else if(array[mid] == array[high]){
                high = high - 1;
            }else{
                high = mid;
            }   
        }
        return array[low];
    }
}
```

#### Python实现
```
class Solution:
    def minNumberInRotateArray(self, rotateArray):
        length = len(rotateArray)
        if length == 0:
           return 0
        elif length == 1:
            return rotateArray[0]
        else:
            left = 0
            right = length - 1
            while left < right:
                mid = (left + right)/2
                if rotateArray[mid] < rotateArray[j]:
                    right = mid
                else:
                    left = mid+1
            return rotateArray[i]
```

### 斐波那契数列

#### 题目描述

大家都知道斐波那契数列，现在要求输入一个整数n，请你输出斐波那契数列的第n项（从0开始，第0项为0）。n<=39 

#### Java实现
```
public class Solution {
    public int Fibonacci(int n) {
        int preNum=1;
        int prePreNum=0;
        int result=0;
        if(n==0)
            return 0;
        if(n==1)
            return 1;
        for(int i=2;i<=n;i++){
            result=preNum+prePreNum;
            prePreNum=preNum;
            preNum=result;
        }
        return result;
 
    }
}
```

#### Python实现
```
class Solution:
    def Fibonacci(self, n):
        res=[0,1,1,2]
        while len(res)<=n:
            res.append(res[-1]+res[-2])
        return res[n]
```

### 跳台阶

#### 题目描述

一只青蛙一次可以跳上1级台阶，也可以跳上2级。求该青蛙跳上一个n级的台阶总共有多少种跳法（先后次序不同算不同的结果）。

#### Java实现
```
int jumpFloor(int number) {
        if(number<2)
            return number;
        int f1=1;
        int f2=0;
        int f=0;
        for(int i=1;i<=number;++i)
            {
            f=f1+f2;
            f2=f1;
            f1=f;
        }
        return s;
```

#### Python实现
```
class Solution:
    def jumpFloor(self, n):
        # write code here
        res=[1,1,2]
        while len(res)<=n:
            res.append(res[-1]+res[-2])
        return res[n]
```

### 变态跳台阶

#### 题目描述

一只青蛙一次可以跳上1级台阶，也可以跳上2级……它也可以跳上n级。求该青蛙跳上一个n级的台阶总共有多少种跳法。

#### Java实现
```
class Solution{
public:
    int jumpFloorII(int number) {
        return 1<<(number-1);        
    }
};
```

#### Python实现
```
class Solution:
    def jumpFloorII(self, number):
        # write code here
        if number<=0:
            return 0
        else:
            return 2**(number-1)
```

### 链表中倒数第k个节点

#### 题目描述

输入一个链表，输出该链表中倒数第k个结点。

** 解题思路：两个指针，先让第一个指针和第二个指针都指向头结点，然后再让第一个指正走(k-1)步，到达第k个节点。然后两个指针同时往后移动，当第一个结点到达末尾的时候，第二个结点所在位置就是倒数第k个节点。**

#### Java实现
```
public class Solution {
    public ListNode FindKthToTail(ListNode head,int k) {
        if(head==null||k<=0){
            return null;
        }
        ListNode pre=head;
        ListNode last=head;      
        for(int i=1;i<k;i++){
            if(pre.next!=null){
                pre=pre.next;
            }else{
                return null;
            }
        }
        while(pre.next!=null){
            pre = pre.next;
            last=last.next;
        }
    return last;
    }
}
```

#### Python实现
```
class Solution:
    def FindKthToTail(self, head, k):
        res=[]
        while head:
            res.append(head)
            head = head.next
        if k > len(res) or k < 1:
            return
        return res[-k]
```