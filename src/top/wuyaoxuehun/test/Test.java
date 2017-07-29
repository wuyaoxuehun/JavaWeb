package top.wuyaoxuehun.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class Test {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		testFetchGrades();
	}
	
	public static void testFetchGrades() throws ClientProtocolException, IOException{
		HttpClient httpClient=new DefaultHttpClient();
		DoGet(httpClient, "http://jwxt.swpu.edu.cn/");	
		List<NameValuePair> ln=new ArrayList<NameValuePair>();	
		Properties properties=new Properties();
		properties.load(new FileInputStream(new File("prop.properties")));;
		ln.add(new BasicNameValuePair("zjh",properties.getProperty("studentNum")));
		ln.add(new BasicNameValuePair("mm", properties.getProperty("password")));
		Scanner scanner=new  Scanner(System.in);
		String yzm="";
		do {
			showPng(httpClient);
			yzm=scanner.next();
		} while (yzm.equals("0"));
		scanner.close();
		ln.add(new BasicNameValuePair("v_yzm", yzm));
		String reString=DoPost(httpClient, "http://jwxt.swpu.edu.cn/loginAction.do", ln);
		if(reString.indexOf("frameset")>=0){
			System.out.println("login ok!");
			String mainstr= DoGet(httpClient,"http://jwxt.swpu.edu.cn/bxqcjcxAction.do");
			Document document=Jsoup.parse(mainstr);
			Elements elements=document.select("table:nth-child(4) thead");
			Element element=elements.get(0);
			System.out.println(element);
		}
		else{
			System.out.println("login fail!");
		}
	}
	
	
	
	public static String DoGet(HttpClient httpClient,String url) throws ClientProtocolException, IOException{
		HttpGet httpGet=new HttpGet(url);
		HttpResponse httpResponse=httpClient.execute(httpGet);
		
		return getEntity(httpResponse);
	}
	public static String DoPost(HttpClient httpClient,String url,List<NameValuePair> ln) throws ParseException, ClientProtocolException, IOException{
		UrlEncodedFormEntity uefe=new UrlEncodedFormEntity(ln,"utf-8");
		HttpPost httpPost=new HttpPost(url);
		httpPost.setEntity(uefe);
		return getEntity(httpClient.execute(httpPost));
	}
	
	
	public static String getEntity(HttpResponse hrs) throws ParseException, IOException{
		return EntityUtils.toString(hrs.getEntity());
		
	}
	public static void showPng(HttpClient httpClient) throws ClientProtocolException, IOException{
		HttpGet httpGet1=new HttpGet("http://jwxt.swpu.edu.cn/validateCodeAction.do?random="+Math.random());
		HttpResponse rs=httpClient.execute(httpGet1);
		HttpEntity httpEntity1=rs.getEntity();
		InputStream  fis=httpEntity1.getContent();
		FileOutputStream fw=new FileOutputStream("E:\\test.png");
		byte[] bytes=new byte[1024];
		fis.read(bytes);
		
		fw.write(bytes);
		fis.close();
		fw.close();
		
		Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler file:\\E:\\test.png");
	}
}
