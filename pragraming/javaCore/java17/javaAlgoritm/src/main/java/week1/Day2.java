package week1;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Day2 {
    public static void main(String[] args) {
        int[] nums = new int[]{2, 3, 4, 5, 6, 7};
        System.out.println(Arrays.toString(twoSum(nums,7)));
        System.out.println(Arrays.toString(twoSum2(nums,7)));
    }
    public static int[] twoSum(int[] nums, int target) {
        HashSet<Integer> set=new HashSet<>();
        for (int i = 0; i < nums.length; i++) {
            for (int i1 = 0; i1 < nums.length; i1++) {
               if(nums[i]+nums[i1]==target)
                   return new int[]{i1,i};
            }

        }
        return  nums;
    }

    public static int[] twoSum2(int[] nums, int target) {
        var seen = new HashMap<Integer, Integer>(); // value -> index
        for (var i = 0; i < nums.length; i++) {
            var complement = target - nums[i];
            if (seen.containsKey(complement)) {
                return new int[]{seen.get(complement), i};
            }
            seen.put(nums[i], i);
        }
        return new int[]{};
    }
}
