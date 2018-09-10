## 剑指offer

以前用的Java实现，现在用Python实现,主要掌握Python语言特性。

数据结构和算法

### 二维数组中的查找

#### 题目描述：
在一个二维数组中（每个一维数组的长度相同），每一行都按照从左到右递增的顺序排序，每一列都按照从上到下递增的顺序排序。请完成一个函数，输入这样的一个二维数组和一个整数，判断数组中是否含有该整数

-[二维数组中的查找](https://www.nowcoder.com/practice/abc3fe2ce8e146608e868a70efebf62e?tpId=13&tqId=11154&tPage=1&rp=1&ru=/ta/coding-interviews&qru=/ta/coding-interviews/question-ranking)

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
                //Python不支持i++
            elif target>array[tR][tC]:
                tC += 1
            else:
                return True
        return False
```

