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
        return nums + 1
```
