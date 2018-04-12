import java.util.Arrays;
import java.util.List;

import static com.silita.biaodaa.utils.MyStringUtils.isNotNull;

/**
 * Created by dh on 2018/4/12.
 */
public class SimpleTest {

    public static void  main(String[] args){
        String ss="||圣诞节";

        String [] res = splitParam(ss);
        System.out.println(ss.length());
        List list = Arrays.asList(res);

        for(String s: res){
            System.out.println("s:"+s);
        }
    }

    public static String[] splitParam(String str){
        if(isNotNull(str)){
            return  str.split("\\|\\|");
        }
        return null;
    }
}
