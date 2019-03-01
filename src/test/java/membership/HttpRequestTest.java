package membership;
import org.junit.Test;
/**
 * Created by dh on 2019/1/9.
 */
public class HttpRequestTest {


	@Test
	public void testLogin(){
		//pre.biaodaa.com  127.0.0.1:8080
		String url="http://127.0.0.1:8080/authorize/memberLogin";
		String body="{\"loginPwd\":\"cc8d16ccf7f851ae6aba73327230dca5d64d2c21\",\"phoneNo\":\"15111079659\",\"channel\":\"1002\",\"clientVersion\":\"22222\"}";
		String res = Sender.sendPost(url,body,"UTF-8");
		System.out.println(res);
		//MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMSIsImxvZ2luX25hbWUiOiIxNTU3NjM2MTczNyIsImxvZ2luX3RpbWUiOiIxNTQ3MDI4MjAzMjY0IiwicGVybWlzc2lvbnMiOiJiaWRfZmlsdGVyIiwicGhvbmVfbm8iOiIxNTU3NjM2MTczNyIsInBraWQiOiI4ZTNjZTUzYTJhNjg0NzllYWZiNWM2OWVhOTA1ZjdhNSIsInJvbGVfY29kZSI6Im5vcm1hbCIsInRva2VuVmVyc2lvbiI6IjIwMTkwMTAzIiwidXNlcl9uYW1lIjoiMTU1NzYzNjE3MzcifQ==.BC465A8D53F7563CEC859CB5BC0C4B41
	}


	@Test
	public void testrefreshUserInfo(){
		//pre.biaodaa.com  127.0.0.1:8080
		String url="http://127.0.0.1:8080/userCenter/refreshUserInfo";
		String body="";
		String token="MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMyIsImxvZ2luTmFtZSI6ImxpanVuODI2MiIsImxvZ2luVGltZSI6IjE1NTEzNTQzNzMwMDIiLCJwZXJtaXNzaW9ucyI6IiIsInBob25lTm8iOiIxNTIwMDUwODk4NSIsInBraWQiOiIwMDEyMzc5NjI0YjQ0Y2NjOThlZmUzOWEwZWNiZDRjNSIsInJvbGVDb2RlIjoibm9ybWFsIiwidG9rZW5WZXJzaW9uIjoiMjAxOTAxMDMifQ==.D08B68D0D855AD43D2114D35E38D5503";
		String res = Sender.sendPostBdd(url,token,body,"UTF-8");
		System.out.println(res);
		//MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMSIsImxvZ2luX25hbWUiOiIxNTU3NjM2MTczNyIsImxvZ2luX3RpbWUiOiIxNTQ3MDI4MjAzMjY0IiwicGVybWlzc2lvbnMiOiJiaWRfZmlsdGVyIiwicGhvbmVfbm8iOiIxNTU3NjM2MTczNyIsInBraWQiOiI4ZTNjZTUzYTJhNjg0NzllYWZiNWM2OWVhOTA1ZjdhNSIsInJvbGVfY29kZSI6Im5vcm1hbCIsInRva2VuVmVyc2lvbiI6IjIwMTkwMTAzIiwidXNlcl9uYW1lIjoiMTU1NzYzNjE3MzcifQ==.BC465A8D53F7563CEC859CB5BC0C4B41
	}


	@Test
	public void testQueryFeeStandard(){
		//pre.biaodaa.com  127.0.0.1:8080
		String url="http://127.0.0.1:8080/vip/queryMyProfits";
		String body="{\"channel\":\"1002\"}";
		String token="MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMyIsImxvZ2luTmFtZSI6ImxpanVuODI2MiIsImxvZ2luVGltZSI6IjE1NTE0MTY5MjEyOTYiLCJwZXJtaXNzaW9ucyI6ImJpZEZpbHRlcix0ZW5kZXJGaWx0ZXIsdW5kZXJRdWVyeSx1bmRlckxpc3QsY29tRmlsdGVyLGNvbVBlcmZvcm1hbmNlLGNvbUxhdyxjb21QaG9uZSxjb21DYXJkIiwicGhvbmVObyI6IjE1MjAwNTA4OTg1IiwicGtpZCI6IjAwMTIzNzk2MjRiNDRjY2M5OGVmZTM5YTBlY2JkNGM1Iiwicm9sZUNvZGUiOiJ2aXAxIiwidG9rZW5WZXJzaW9uIjoiMjAxOTAxMDMifQ==.5F42B33B691A3058D12360D1323A0089";
		String res = Sender.sendPostBdd(url,token,body,"UTF-8");
		System.out.println(res);
		//MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMSIsImxvZ2luX25hbWUiOiIxNTU3NjM2MTczNyIsImxvZ2luX3RpbWUiOiIxNTQ3MDI4MjAzMjY0IiwicGVybWlzc2lvbnMiOiJiaWRfZmlsdGVyIiwicGhvbmVfbm8iOiIxNTU3NjM2MTczNyIsInBraWQiOiI4ZTNjZTUzYTJhNjg0NzllYWZiNWM2OWVhOTA1ZjdhNSIsInJvbGVfY29kZSI6Im5vcm1hbCIsInRva2VuVmVyc2lvbiI6IjIwMTkwMTAzIiwidXNlcl9uYW1lIjoiMTU1NzYzNjE3MzcifQ==.BC465A8D53F7563CEC859CB5BC0C4B41
	}

	//SABBSM
	@Test
	public void testThirdBinding(){
		String url="http://pre.biaodaa.com/authorize/thirdPartyBinding ";
		String body="{\n" +
				"  \"imageUrl\": \"\",\n" +
				"  \"nikeName\": \"\",\n" +
				"  \"wxOpenId\": \"oT8cu0lPyMcGdIO8XLUyELJF5qpE\",\n" +
				"  \"loginPwd\": \"123456789\",\n" +
				"  \"verifyCode\": \"837458\",\n" +
				"  \"phoneNo\": \"15576361737\",\n" +
				"  \"channel\": \"1001\",\n" +
				"  \"wxUnionId\": \"oL3d6wq_3bc08tl1n25rSbf6NLcg\",\n" +
				"  \"qqOpenId\": \"\",\n" +
				"  \"sex\": \"\",\n" +
				"  \"inviterCode\":\"SABBSM\"\n" +
				"}";

		String res = Sender.sendPost(url,body,"UTF-8");
		System.out.println(res);
	}


	@Test
	public void testQueryMyProfits(){
		//pre.biaodaa.com  127.0.0.1:8080
		String url="http://pre.biaodaa.com/vip/queryMyProfits";
		String body="{\"pageNo\":2,\"pageSize\":8}";
		String token="MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMSIsImxvZ2luTmFtZSI6IndvODE1OTkzNTkzIiwibG9naW5UaW1lIjoiMTU0NzcxNzI0NzI3MSIsInBob25lTm8iOiIxNTExMTA3OTY1OSIsInBraWQiOiIxNTQ2NTk2MDEzIiwidG9rZW5WZXJzaW9uIjoiMjAxOTAxMDMifQ==.A4C4A5AA9C8F2339A67A68E541478984";
		String res = Sender.sendPostBdd(url,token,body,"UTF-8");
		System.out.println(res);
		//MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMSIsImxvZ2luX25hbWUiOiIxNTU3NjM2MTczNyIsImxvZ2luX3RpbWUiOiIxNTQ3MDI4MjAzMjY0IiwicGVybWlzc2lvbnMiOiJiaWRfZmlsdGVyIiwicGhvbmVfbm8iOiIxNTU3NjM2MTczNyIsInBraWQiOiI4ZTNjZTUzYTJhNjg0NzllYWZiNWM2OWVhOTA1ZjdhNSIsInJvbGVfY29kZSI6Im5vcm1hbCIsInRva2VuVmVyc2lvbiI6IjIwMTkwMTAzIiwidXNlcl9uYW1lIjoiMTU1NzYzNjE3MzcifQ==.BC465A8D53F7563CEC859CB5BC0C4B41
	}

	@Test
	public void testOpenMember(){
		//pre.biaodaa.com  127.0.0.1:8080
		String url="http://pre.biaodaa.com/vip/openMemberRights";
		String body="{\n" +
				"  \"channel\": \"1002\",\n" +
				"  \"feeStandard\": {\n" +
				"    \"stdCode\": \"quarter\"\n" +
				"  }\n" +
				"}";
		String token="MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMiIsImxvZ2luTmFtZSI6IjE1Mzg2MTk2NzI2IiwibG9naW5UaW1lIjoiMTU0ODIzMDgxOTg2MSIsInBlcm1pc3Npb25zIjoiYmlkX2ZpbHRlcix0ZW5kZXJfZmlsdGVyIiwicGhvbmVObyI6IjE1Mzg2MTk2NzI2IiwicGtpZCI6IjE1NDY1OTkyMDkiLCJyb2xlQ29kZSI6Im5vcm1hbCIsInRva2VuVmVyc2lvbiI6IjIwMTkwMTAzIn0=.204EC69D4EEC6441AFB4B382BD1ADB20";
		String res = Sender.sendPostBdd(url,token,body,"UTF-8");
		System.out.println(res);
		//MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMSIsImxvZ2luX25hbWUiOiIxNTU3NjM2MTczNyIsImxvZ2luX3RpbWUiOiIxNTQ3MDI4MjAzMjY0IiwicGVybWlzc2lvbnMiOiJiaWRfZmlsdGVyIiwicGhvbmVfbm8iOiIxNTU3NjM2MTczNyIsInBraWQiOiI4ZTNjZTUzYTJhNjg0NzllYWZiNWM2OWVhOTA1ZjdhNSIsInJvbGVfY29kZSI6Im5vcm1hbCIsInRva2VuVmVyc2lvbiI6IjIwMTkwMTAzIiwidXNlcl9uYW1lIjoiMTU1NzYzNjE3MzcifQ==.BC465A8D53F7563CEC859CB5BC0C4B41
	}
}
