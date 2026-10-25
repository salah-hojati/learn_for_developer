import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Day3 {


    public static void main(String[] args) {
        int [] nums=new int[]{1,3,4,5,6};

        System.out.println(isSett(nums));
    }

    private static boolean isSett(int[] nums) {
        Set numSet = new HashSet<>();
        for (int num : nums) {
            if(!numSet.add(num)){
                return true;
            }
        }
        return false;



    }
}
