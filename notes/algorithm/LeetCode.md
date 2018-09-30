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