## LeetCode

纯Python实现！

<!-- TOC -->

- [LeetCode](#leetcode)
    - [Two Sum](#two-sum)
        - [题目描述](#题目描述)
        - [Python代码](#python代码)
    - [Reverse Integer](#reverse-integer)
        - [题目描述](#题目描述-1)
        - [Python代码](#python代码-1)
    - [Palindrome Number](#palindrome-number)
        - [题目描述](#题目描述-2)
        - [Python代码](#python代码-2)
    - [Palindrome Number](#palindrome-number-1)
        - [题目描述](#题目描述-3)
        - [Python代码](#python代码-3)
    - [Valid Parentheses](#valid-parentheses)
        - [题目描述](#题目描述-4)
        - [Python代码](#python代码-4)
    - [Merge Two Sorted Lists](#merge-two-sorted-lists)
        - [题目描述](#题目描述-5)
        - [Python代码](#python代码-5)
    - [Remove Duplicates from Sorted Array](#remove-duplicates-from-sorted-array)
        - [题目描述](#题目描述-6)
        - [Python代码](#python代码-6)
    - [Remove Element](#remove-element)
        - [题目描述](#题目描述-7)
        - [Python代码](#python代码-7)
    - [Implement strStr()](#implement-strstr)
        - [题目描述](#题目描述-8)
        - [Python代码](#python代码-8)
    - [Search Insert Position](#search-insert-position)
        - [题目描述](#题目描述-9)
        - [Python代码](#python代码-9)
    - [Maximum Subarray](#maximum-subarray)
        - [题目描述](#题目描述-10)
        - [Python代码](#python代码-10)
    - [Add Binary](#add-binary)
        - [题目描述](#题目描述-11)
        - [Python代码](#python代码-11)
    - [Climbing Stairs](#climbing-stairs)
        - [题目描述](#题目描述-12)
        - [Python代码](#python代码-12)
    - [Remove Duplicates from Sorted List](#remove-duplicates-from-sorted-list)
        - [题目描述](#题目描述-13)
        - [Python代码](#python代码-13)
    - [Same Tree](#same-tree)
        - [题目描述](#题目描述-14)
        - [Python代码](#python代码-14)
    - [Swap Nodes in Pairs](#swap-nodes-in-pairs)
        - [题目描述](#题目描述-15)
        - [Python代码](#python代码-15)
    - [Search in Rotated Sorted Array](#search-in-rotated-sorted-array)
        - [题目描述](#题目描述-16)
        - [Python代码](#python代码-16)

<!-- /TOC -->

### Two Sum

#### 题目描述

[Two Sum](https://leetcode.com/problems/two-sum/description/)

Given an array of integers, return indices of the two numbers such that they add up to a specific target.

You may assume that each input would have exactly one solution, and you may not use the same element twice.

```
Example:

Given nums = [2, 7, 11, 15], target = 9,
Because nums[0] + nums[1] = 2 + 7 = 9,
return [0, 1].
```

#### Python代码
```
class Solution:
    def twoSum(nums, target):
        if len(nums) <+ 1:
            return false
        buff_dict = {}
        for i in range(len(nums)):
            if nums[i] in buff_dict:
                return [buff_dict[nums[i]], i]
            else:
                buff_dict[target - nums[i]] = i
```

### Reverse Integer

#### 题目描述

[Reverse Integer](https://leetcode.com/problems/reverse-integer/description/)

Given a 32-bit signed integer, reverse digits of an integer.

```
Example 1:

Input: 123
Output: 321
Example 2:

Input: -123
Output: -321
Example 3:

Input: 120
Output: 21
```
#### Python代码

```
class Solution:
    def reverse(self, x):
        # 不考虑溢出的情况
        rev = 0
        while x != 0:
            rev = rev*10 + x%10
            x = x//10
        return int(rev)
```

### Palindrome Number

#### 题目描述

[Palindrome Number](https://leetcode.com/problems/palindrome-number/description/)

Determine whether an integer is a palindrome. An integer is a palindrome when it reads the same backward as forward.

```
Example 1:

Input: 121
Output: true
Example 2:

Input: -121
Output: false
Explanation: From left to right, it reads -121. From right to left, it becomes 121-. Therefore it is not a palindrome.
Example 3:

Input: 10
Output: false
Explanation: Reads 01 from right to left. Therefore it is not a palindrome.
```
#### Python代码
```
class Solution:
    def isPalindrome(self, x):
        if x < 0 or (x != 0 and x%10 == 0):
            return False
        rev = 0
        while x > rev:
            rev = rev*10 + x%10
            x = x//10
        return rev == x or x == rev//10
```

### Palindrome Number

#### 题目描述
[Longest Common Prefix](https://leetcode.com/problems/longest-common-prefix/description/)

Write a function to find the longest common prefix string amongst an array of strings.

If there is no common prefix, return an empty string "".

```
Example 1:

Input: ["flower","flow","flight"]
Output: "fl"
Example 2:

Input: ["dog","racecar","car"]
Output: ""
Explanation: There is no common prefix among the input strings.
```

#### Python代码
```
class Solution:
    def longestCommonPrefix(self, strs):
        if len(strs) == 0:
            return ''
        lens = [len(str) for str in strs]
        min_len = min(lens)
        result = ''
    
        for i in range(1, min_len+1):
            prefix = strs[0][:i]
            for s in strs:             
                if s[:i] != prefix:
                    return result
            result = prefix
        
        return result
```

### Valid Parentheses

#### 题目描述

[Valid Parentheses](https://leetcode.com/problems/valid-parentheses/description/)

Given a string containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid.

An input string is valid if:

Open brackets must be closed by the same type of brackets.
Open brackets must be closed in the correct order.
Note that an empty string is also considered valid.

```
Example 1:

Input: "()"
Output: true
Example 2:

Input: "()[]{}"
Output: true
Example 3:

Input: "(]"
Output: false
Example 4:

Input: "([)]"
Output: false
Example 5:

Input: "{[]}"
Output: true
```
#### Python代码

```
class Solution:
    def isValid(self, s):
        stack = []
        dict = {"]":"[", "}":"{", ")":"("}
        for char in s:
            if char in dict.values():
                stack.append(char)
            elif char in dict.keys():
                if stack == [] or dict[char] != stack.pop():
                    return False
            else:
                return False
        return stack == []
```
### Merge Two Sorted Lists

#### 题目描述

[Merge Two Sorted Lists](https://leetcode.com/problems/merge-two-sorted-lists/)

Merge two sorted linked lists and return it as a new list. The new list should be made by splicing together the nodes of the first two lists.

```
Example:

Input: 1->2->4, 1->3->4
Output: 1->1->2->3->4->4
```

#### Python代码

```
class Solution:
	def mergeTwoList(self, l1, l2):
		if not l1 or not l2:
			return l1 or l2
		if l1.val < l2.val:
			l1.next = self.mergeTwoList(l1.next, l2)
			return l1
		else:
			l2.next = self.mergeTwoList(l1, l2.next)
			return l2
```

### Remove Duplicates from Sorted Array

#### 题目描述

[Remove Duplicates from Sorted Array](https://leetcode.com/problems/remove-duplicates-from-sorted-array/)

Given a sorted array nums, remove the duplicates in-place such that each element appear only once and return the new length.

Do not allocate extra space for another array, you must do this by modifying the input array in-place with O(1) extra memory.
```
Example 1:

Given nums = [1,1,2],

Your function should return length = 2, with the first two elements of nums being 1 and 2 respectively.

It doesn't matter what you leave beyond the returned length.
Example 2:

Given nums = [0,0,1,1,1,2,2,3,3,4],

Your function should return length = 5, with the first five elements of nums being modified to 0, 1, 2, 3, and 4 respectively.

It doesn't matter what values are set beyond the returned length.
```

#### Python代码
```
class Solution:
    def removeDuplicates(self, nums):
        if not nums:
            return 0
        newRes = 0
        for i in range(1, len(nums)):
            if nums[i] != nums[newRes]:
                newRes += 1
                nums[newRes] = nums[i]
        return newRes + 1
```

### Remove Element

#### 题目描述

[Remove Element](https://leetcode.com/problems/remove-element/)

Given an array nums and a value val, remove all instances of that value in-place and return the new length.

Do not allocate extra space for another array, you must do this by modifying the input array in-place with O(1) extra memory.

The order of elements can be changed. It doesn't matter what you leave beyond the new length.

```
Example 1:

Given nums = [3,2,2,3], val = 3,

Your function should return length = 2, with the first two elements of nums being 2.

It doesn't matter what you leave beyond the returned length.
Example 2:

Given nums = [0,1,2,2,3,0,4,2], val = 2,

Your function should return length = 5, with the first five elements of nums containing 0, 1, 3, 0, and 4.

Note that the order of those five elements can be arbitrary.

It doesn't matter what values are set beyond the returned length.
```

####Python代码
```
class Solution:
    def removeElement(self, nums, val):
        """
        :type nums: List[int]
        :type val: int
        :rtype: int
        """
        begin=0
        for i in range(0, len(nums)):
            if nums[i] != val:
                nums[begin] = nums[i]
                begin += 1
        return begin
```

### Implement strStr()

#### 题目描述

[Implement strStr()](https://leetcode.com/problems/implement-strstr/)

Implement strStr().

Return the index of the first occurrence of needle in haystack, or -1 if needle is not part of haystack.
```
Example 1:

Input: haystack = "hello", needle = "ll"
Output: 2
Example 2:

Input: haystack = "aaaaa", needle = "bba"
Output: -1
```

#### Python代码
```
class Solution:
    def strStr(self, haystack, needle):
        """
        :type haystack: str
        :type needle: str
        :rtype: int
        """
        for i in range(len(haystack) - len(needle)+1):
            if haystack[i:i+len(needle)] == needle:
                return i
        return -1
```

### Search Insert Position

#### 题目描述

[Search Insert Position](https://leetcode.com/problems/search-insert-position/)

Given a sorted array and a target value, return the index if the target is found. If not, return the index where it would be if it were inserted in order.

You may assume no duplicates in the array.
```
Example 1:

Input: [1,3,5,6], 5
Output: 2
Example 2:

Input: [1,3,5,6], 2
Output: 1
Example 3:

Input: [1,3,5,6], 7
Output: 4
Example 4:

Input: [1,3,5,6], 0
Output: 0
```

#### Python代码
```
class Solution:
    def searchInsert(self, nums, target):
        """
        :type nums: List[int]
        :type target: int
        :rtype: int
        """
        for i in range(0, len(nums)):
            if nums[i] >= target:
                return i
        return len(nums)
```

### Maximum Subarray

#### 题目描述

[Maximum Subarray](https://leetcode.com/problems/maximum-subarray/)

Given an integer array nums, find the contiguous subarray (containing at least one number) which has the largest sum and return its sum.
```
Example:

Input: [-2,1,-3,4,-1,2,1,-5,4],
Output: 6
Explanation: [4,-1,2,1] has the largest sum = 6.
```

#### Python代码
```
class Solution:
    def maxSubArray(self, nums):
        """
        :type nums: List[int]
        :rtype: int
        """
        sum = 0
        res = 0
        for i in range(0, len(nums)):
            res = res + nums[i]
            if res < 0:
                res = 0
            if res > sum:
                sum = res
        return sum
```

### Add Binary

#### 题目描述

[Add Binary](https://leetcode.com/problems/add-binary/)



Given two binary strings, return their sum (also a binary string).

The input strings are both non-empty and contains only characters 1 or 0.
```
Example 1:

Input: a = "11", b = "1"
Output: "100"
Example 2:

Input: a = "1010", b = "1011"
Output: "10101"
```
#### Python代码
```
class Solution:
        def addBinary(self, a, b):
            if len(a)==0: return b
            if len(b)==0: return a
            if a[-1] == '1' and b[-1] == '1':
                return self.addBinary(self.addBinary(a[0:-1],b[0:-1]),'1')+'0'
            if a[-1] == '0' and b[-1] == '0':
                return self.addBinary(a[0:-1],b[0:-1])+'0'
            else:
                return self.addBinary(a[0:-1],b[0:-1])+'1'
```

### Climbing Stairs

#### 题目描述

[Climbing Stairs](https://leetcode.com/problems/climbing-stairs/submissions/)

You are climbing a stair case. It takes n steps to reach to the top.

Each time you can either climb 1 or 2 steps. In how many distinct ways can you climb to the top?

Note: Given n will be a positive integer.
```
Example 1:

Input: 2
Output: 2


Example 2:

Input: 3
Output: 3
```
#### Python代码
```
class Solution:
    def climbStairs(self, n):
        a = b = 1
        for _ in range(n):
            a, b = b, a + b
        return a
```
>参考

```
Ruby (60 ms)

def climb_stairs(n)
    a = b = 1
    n.times { a, b = b, a+b }
    a
end
C++ (0 ms)

int climbStairs(int n) {
    int a = 1, b = 1;
    while (n--)
        a = (b += a) - a;
    return a;
}
Java (208 ms)

public int climbStairs(int n) {
    int a = 1, b = 1;
    while (n-- > 0)
        a = (b += a) - a;
    return a;
}
C (0 ms)

int climbStairs(int n) {
    int a = 1, b = 1;
    while (n--)
        a = (b += a) - a;
    return a;
}
C# (48 ms)

public int ClimbStairs(int n) {
    int a = 1, b = 1;
    while (n-- > 0)
        a = (b += a) - a;
    return a;
}
Javascript (116 ms)

var climbStairs = function(n) {
    a = b = 1
    while (n--)
        a = (b += a) - a
    return a
};
```

### Remove Duplicates from Sorted List

#### 题目描述

[Remove Duplicates from Sorted List](https://leetcode.com/problems/remove-duplicates-from-sorted-list/)

Given a sorted linked list, delete all duplicates such that each element appear only once.
```
Example 1:

Input: 1->1->2
Output: 1->2
Example 2:

Input: 1->1->2->3->3
Output: 1->2->3
```
#### Python代码
```
class Solution:
    def deleteDuplicates(self, head):
        """
        :type head: ListNode
        :rtype: ListNode
        """
        if not head or not head.next:
            return head
        head.next = self.deleteDuplicates(head.next)
        return (head.val == head.next.val) and head.next or head
```

### Same Tree

#### 题目描述

[Same Tree](https://leetcode.com/problems/same-tree/)

Given two binary trees, write a function to check if they are the same or not.

Two binary trees are considered the same if they are structurally identical and the nodes have the same value.
```
Example 1:

Input:     1         1
          / \       / \
         2   3     2   3

        [1,2,3],   [1,2,3]

Output: true
Example 2:

Input:     1         1
          /           \
         2             2

        [1,2],     [1,null,2]

Output: false
Example 3:

Input:     1         1
          / \       / \
         2   1     1   2

        [1,2,1],   [1,1,2]

Output: false
```
#### Python代码
```
class Solution:
    def isSameTree(self, p, q):
        """
        :type p: TreeNode
        :type q: TreeNode
        :rtype: bool
        """
        if p and q:
            return p.val == q.val and self.isSameTree(p.left, q.left) and self.isSameTree(p.right, q.right)
        return p is q
```

### Swap Nodes in Pairs

#### 题目描述

[Swap Nodes in Pairs](https://leetcode.com/problems/swap-nodes-in-pairs/)

Given a linked list, swap every two adjacent nodes and return its head.

You may not modify the values in the list's nodes, only nodes itself may be changed.

```
Example:

Given 1->2->3->4, you should return the list as 2->1->4->3.
```

#### Python代码

```
class Solution:
    def swapPairs(self, head: ListNode) -> ListNode:
        if not head or not head.next:
            return head
        
        next = head.next
        head.next = self.swapPairs(head.next.next)
        next.next = head
        return next
```

### Search in Rotated Sorted Array

#### 题目描述

[Search in Rotated Sorted Array](https://leetcode.com/problems/search-in-rotated-sorted-array/)

Suppose an array sorted in ascending order is rotated at some pivot unknown to you beforehand.

(i.e., [0,1,2,4,5,6,7] might become [4,5,6,7,0,1,2]).

You are given a target value to search. If found in the array return its index, otherwise return -1.

You may assume no duplicate exists in the array.

Your algorithm's runtime complexity must be in the order of O(log n).

```
Example 1:

Input: nums = [4,5,6,7,0,1,2], target = 0
Output: 4
Example 2:

Input: nums = [4,5,6,7,0,1,2], target = 3
Output: -1
```

#### Python代码
```
class Solution:
    def search(self, nums: List[int], target: int) -> int:
        
        if not nums:
            return -1

        low, high = 0, len(nums) - 1

        while low <= high:
            mid = (low + high) // 2
            if target==nums[mid]:
                return mid

            if nums[low]<=nums[mid]:
                if nums[low] <= target <= nums[mid]:
                    high = mid - 1
                else:
                    low = mid + 1
            else:
                if nums[mid] <= target <= nums[high]:
                    low = mid + 1
                else:
                    high = mid - 1

        return -1
```