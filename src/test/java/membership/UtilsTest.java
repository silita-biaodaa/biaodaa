package membership;

import com.silita.biaodaa.utils.RegexUtils;
import org.junit.Test;

/**
 * Created by dh on 2019/1/8.
 */
public class UtilsTest {

    @Test
    public void testRegex(){
        String s = "2019-1-8";
        String regex = "^0{0,1}(13[0-9]|15[7-9]|153|156|18[7-9]|1[0-9][0-9])[0-9]{8}$";
        regex= "^[A-Za-z\\d]+([-_.][A-Za-z\\d]+)*@([A-Za-z\\d]+[-.])+[A-Za-z\\d]{1,6}$";
        regex= "^[12][0-9]{3}-[0-1][0-9]-[0-3][0-9]$";
        System.out.println(RegexUtils.matchExists(s,regex));
    }
}
