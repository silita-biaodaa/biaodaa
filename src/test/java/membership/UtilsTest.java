package membership;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.utils.MyDateUtils;
import com.silita.biaodaa.utils.RegexUtils;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static com.silita.biaodaa.utils.EncryptUtils.Base64Decode;
import static com.silita.biaodaa.utils.TokenUtils.parseJsonString;

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

    @Test
    public void parseToken()throws Exception{
        String xToken = "MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMSIsImxvZ2luX25hbWUiOiIxNTU3NjM2MTczNyIsImxvZ2luX3RpbWUiOiIxNTQ3MDI4MjAzMjY0IiwicGVybWlzc2lvbnMiOiJiaWRfZmlsdGVyIiwicGhvbmVfbm8iOiIxNTU3NjM2MTczNyIsInBraWQiOiI4ZTNjZTUzYTJhNjg0NzllYWZiNWM2OWVhOTA1ZjdhNSIsInJvbGVfY29kZSI6Im5vcm1hbCIsInRva2VuVmVyc2lvbiI6IjIwMTkwMTAzIiwidXNlcl9uYW1lIjoiMTU1NzYzNjE3MzcifQ==.BC465A8D53F7563CEC859CB5BC0C4B41";
        String[] sArray = xToken.split(Constant.TOKEN_SPLIT);
        String paramJson = sArray[1];
        String sign = sArray[2];
        paramJson = Base64Decode(paramJson);
        Map<String,String> paramMap = parseJsonString(paramJson);
        System.out.println(paramMap.toString());
    }

    @Test
    public void testDateAdd(){
        Calendar cal = Calendar.getInstance();
        System.out.println(cal.getTime());
        Date newDate = MyDateUtils.addDays(cal.getTime(),30);
        System.out.println(newDate.toString());
    }
}
