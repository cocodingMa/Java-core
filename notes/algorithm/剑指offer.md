## 剑指offer

利用Java和Python两种方式实现，数据结构和算法

### 二维数组中的查找

#### 题目描述：

在一个二维数组中（每个一维数组的长度相同），每一行都按照从左到右递增的顺序排序，每一列都按照从上到下递增的顺序排序。请完成一个函数，输入这样的一个二维数组和一个整数，判断数组中是否含有该整数

[二维数组中的查找](https://www.nowcoder.com/practice/abc3fe2ce8e146608e868a70efebf62e?tpId=13&tqId=11154&tPage=1&rp=1&ru=/ta/coding-interviews&qru=/ta/coding-interviews/question-ranking)

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

[替换空格](https://www.nowcoder.com/practice/4060ac7e3e404ad1a894ef3e17650423?tpId=13&tqId=11155&tPage=1&rp=1&ru=/ta/coding-interviews&qru=/ta/coding-interviews/question-ranking)


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

[从尾到头打印链表](https://www.nowcoder.com/practice/d0267f7f55b3412ba93bd35cfa8e8035?tpId=13&tqId=11156&tPage=1&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

#### Java实现

```
```

#### Python实现

```
```