import java.util.Arrays;

public class Day4 {
// آرایه‌ای برگردان که هر عنصرش حاصل‌ضرب همه‌ی عناصر به‌جز خودش باشد.
    public static int[] productExceptSelf(int[] nums) {
int len=nums.length;
int[] result =new int[len];
result[0]=nums[0];
        for (int i = 1; i < nums.length-1; i++) {
            result[i]=nums[i-1]*result[i-1];
        }
       // result[len]=
//        for (int i = 0; i < nums.length-1; i++) {
//            result[i]=nums[i]*result[i+1];
//        }
        return result;
    }

    public static void main(String[] args) {
        int[] nums = new int[]{2, 3, 4, 5, 6, 7};
        System.out.println(Arrays.toString(nums));
        System.out.println(Arrays.toString(productExceptSelf(nums)));
    }
}
