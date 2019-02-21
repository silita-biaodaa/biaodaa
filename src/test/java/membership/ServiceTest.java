package membership;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.dao.UserTempBddMapper;
import com.silita.biaodaa.model.SysUser;
import com.silita.biaodaa.service.ConfigTest;
import com.silita.biaodaa.service.VipService;
import com.silita.biaodaa.utils.ShareCodeUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by dh on 2019/1/7.
 */
@Transactional
@TransactionConfiguration(transactionManager="txManager",defaultRollback=true)
@WebAppConfiguration
public class ServiceTest extends ConfigTest {

    @Autowired
    MyRedisTemplate myRedisTemplate;

    @Autowired
    VipService vipService;

    @Autowired
    UserTempBddMapper userTempBddMapper;

    @Test
    public void testShareCode()throws Exception{
        long id =0L;
//        long id = myRedisTemplate.incrementHash("inviteList","map1",null);
//        System.out.println(id);
        for(int i=1;i<100;i++){
            id = myRedisTemplate.incrementHash("inviteList","map211",3L);

            System.out.println(id+"--"+ShareCodeUtils.idToCode(id));
        }
    }

    @Test
    public void testAddUserProfit(){
        String inviterCode= "3AUM6L";
        SysUser u =  userTempBddMapper.queryUserByInviteCode(inviterCode);
        vipService.addUserProfit(Constant.CHANNEL_ANDROID,u.getPkid(),Constant.PROFIT_S_CODE_INVITE,inviterCode);
    }
}
