package membership;
import org.junit.Test;
/**
 * Created by dh on 2019/1/9.
 */
public class HttpRequestTest {

	@Test
	public void test(){
		//pre.biaodaa.com  127.0.0.1:8080
		String url="http://127.0.0.1:8080/under/query";
		String body="{}";
		String token="MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMiIsImxvZ2luX25hbWUiOiJsb2dpbl90ZXMyMjIyIiwibG9naW5fdGltZSI6IjE1NDcxODQzNDA4MDgiLCJwZXJtaXNzaW9ucyI6ImJpZF9maWx0ZXIiLCJwaG9uZV9ubyI6IjEzMzE5NTU1ODAyIiwicGtpZCI6IjcyZDJjNTNiYTM1MDQxOWRhOTljNTdkM2Y3Nzc1ZDQ0Iiwicm9sZV9jb2RlIjoibm9ybWFsIiwidG9rZW5WZXJzaW9uIjoiMjAxOTAxMDMiLCJ1c2VyX25hbWUiOiJ1c2VyX25hbWUyMjIifQ==.BE40341FE564D43268B806363D521F48";
		String res = Sender.sendPostBdd(url,token,body,"UTF-8");
		System.out.println(res);
		//MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMSIsImxvZ2luX25hbWUiOiIxNTU3NjM2MTczNyIsImxvZ2luX3RpbWUiOiIxNTQ3MDI4MjAzMjY0IiwicGVybWlzc2lvbnMiOiJiaWRfZmlsdGVyIiwicGhvbmVfbm8iOiIxNTU3NjM2MTczNyIsInBraWQiOiI4ZTNjZTUzYTJhNjg0NzllYWZiNWM2OWVhOTA1ZjdhNSIsInJvbGVfY29kZSI6Im5vcm1hbCIsInRva2VuVmVyc2lvbiI6IjIwMTkwMTAzIiwidXNlcl9uYW1lIjoiMTU1NzYzNjE3MzcifQ==.BC465A8D53F7563CEC859CB5BC0C4B41
	}


	@Test
	public void testQueryFeeStandard(){
		//pre.biaodaa.com  127.0.0.1:8080
		String url="http://pre.biaodaa.com/vip/queryFeeStandard";
		String body="{\"channel\":\"1002\"}";
		String token="MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMiIsImxvZ2luX25hbWUiOiJsb2dpbl90ZXMyMjIyIiwibG9naW5fdGltZSI6IjE1NDcxODQzNDA4MDgiLCJwZXJtaXNzaW9ucyI6ImJpZF9maWx0ZXIiLCJwaG9uZV9ubyI6IjEzMzE5NTU1ODAyIiwicGtpZCI6IjcyZDJjNTNiYTM1MDQxOWRhOTljNTdkM2Y3Nzc1ZDQ0Iiwicm9sZV9jb2RlIjoibm9ybWFsIiwidG9rZW5WZXJzaW9uIjoiMjAxOTAxMDMiLCJ1c2VyX25hbWUiOiJ1c2VyX25hbWUyMjIifQ==.BE40341FE564D43268B806363D521F48";
		String res = Sender.sendPostBdd(url,token,body,"UTF-8");
		System.out.println(res);
		//MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMSIsImxvZ2luX25hbWUiOiIxNTU3NjM2MTczNyIsImxvZ2luX3RpbWUiOiIxNTQ3MDI4MjAzMjY0IiwicGVybWlzc2lvbnMiOiJiaWRfZmlsdGVyIiwicGhvbmVfbm8iOiIxNTU3NjM2MTczNyIsInBraWQiOiI4ZTNjZTUzYTJhNjg0NzllYWZiNWM2OWVhOTA1ZjdhNSIsInJvbGVfY29kZSI6Im5vcm1hbCIsInRva2VuVmVyc2lvbiI6IjIwMTkwMTAzIiwidXNlcl9uYW1lIjoiMTU1NzYzNjE3MzcifQ==.BC465A8D53F7563CEC859CB5BC0C4B41
	}

	@Test
	public void testThirdBinding(){
		String url="http://pre.biaodaa.com/authorize/thirdPartyBinding ";
		String body="{\n" +
				"        \"imageUrl\": \"\",\n" +
				"        \"nikeName\": \"\",\n" +
				"        \"wxOpenId\": \"oT8cu0lPyMcGdIO8XLUyELJF5qpE\",\n" +
				"        \"loginPwd\": \"123456789\",\n" +
				"        \"verifyCode\": \"197607\",\n" +
				"        \"phoneNo\": \"15576361733\",\n" +
				"        \"channel\": \"1001\",\n" +
				"        \"wxUnionId\": \"oL3d6wq_3bc08tl1n25rSbf6NLcg\",\n" +
				"        \"qqOpenId\": \"\",\n" +
				"        \"sex\": \"\",\n" +
				"        \"inviterCode\":\"\"\n" +
				"}";

		String res = Sender.sendPost(url,body,"UTF-8");
		System.out.println(res);
	}
}
