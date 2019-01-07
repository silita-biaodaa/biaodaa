import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.silita.biaodaa.utils.MyStringUtils.isNotNull;

/**
 * Created by dh on 2018/4/12.
 */
public class SimpleTest {

    @Test
    public void  test1(){
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

    @Test
    public void asyncTest() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        new Thread(() -> {
            // 模拟执行耗时任务
            System.out.println("task doing...");
            double c = 2;
            try {
//                Thread.sleep(3000);

                for(long i=1;i<100;i++){
                    c*=2;
                }
            } catch (Exception e) {
                // 告诉completableFuture任务发生异常了
                completableFuture.completeExceptionally(e);
            }
            // 告诉completableFuture任务已经完成
            completableFuture.complete("ok"+c);
        }).start();
        // 获取任务结果，如果没有完成会一直阻塞等待
        String result = completableFuture.get();
        System.out.println("计算结果:" + result);
    }


}
