import java.util.ArrayList;
import java.util.List;

public class demo {

    public static void main(String[] args) {
        int[] nums = {11, 15,2,7};

        int[] ints = twoSum(nums, 9);
        for (int a: ints){
            System.out.println(a);
        }

    }
    public static int[] twoSum(int[] nums, int target) {
        int[] a = null;
        for(int i=0;i < nums.length; i++){
            for(int j=i+1;j < nums.length; j++){
                if(nums[i]+nums[j] == target){
                    a = new int[]{i,j};
                }
            }
        }
        return a;
    }
}
