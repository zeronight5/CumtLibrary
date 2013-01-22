package org.ahope.cumtlib.Utilities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ScanBook {

	// 检索数目方法
	public static String Scan(String name, String page,boolean flag)
			throws UnsupportedEncodingException {
		String strResult = null;
		//String rname = StringReplace.replace(name, " ", "+").replaceAll("(\\+)\\1+", "$1");
		String Urlbookname = "http://121.248.104.139:8080/opac/openlink.php?location=ALL&title="
				+ URLEncoder.encode(name, "UTF-8")
				+ "&doctype=ALL&lang_code=ALL&match_flag=displaypg=forward&20&showmode=list&orderby=DESC&sort=CATA_DATE&onlylendable=no&page="
				+ page;
		String urlauthor = "http://121.248.104.139:8080/opac/openlink.php?location=ALL&author="
				+ URLEncoder.encode(name, "UTF-8")
				+ "&doctype=ALL&lang_code=ALL&match_flag=displaypg=forward&20&showmode=list&orderby=DESC&sort=CATA_DATE&onlylendable=no&page="
				+ page;
		HttpPost httpRequest = null;
		if(flag)
			httpRequest = new HttpPost(Urlbookname);
		else
			httpRequest = new HttpPost(urlauthor);
		// 取得HTTP response
		HttpResponse httpResponse;
		try {
			httpResponse = new DefaultHttpClient().execute(httpRequest);

			strResult = EntityUtils.toString(httpResponse.getEntity(),HTTP.UTF_8);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strResult;
	}

	// 登陆并检索自己所借书目方法
	public static String mybook(String studentnum, String password) {
		String loginurl = "http://121.248.104.139:8080/reader/redr_verify.php";
		String mbookurl = "http://121.248.104.139:8080/reader/book_lst.php";
		String result = null;
		String bookresult = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpRequest = new HttpPost(loginurl);
		// Post运作传送变数必须用NameValuePair[]阵列储存
		// 传参数 服务端获取的方法为request.getParameter("name")
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("number", studentnum));
		params.add(new BasicNameValuePair("passwd", password));
		params.add(new BasicNameValuePair("select", "cert_no"));
		try {
			// 发出HTTP request
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 取得HTTP response
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			// 取出回应字串
			result = EntityUtils.toString(httpResponse.getEntity());
			Document doc = Jsoup.parse(result);
			Element title = doc.getElementsByTag("title").first();
			if (title.text().length() > 40) {
				// 登陆成功
				httpRequest = new HttpPost(mbookurl);
				httpResponse = httpClient.execute(httpRequest);
				bookresult = EntityUtils.toString(httpResponse.getEntity());
			} else if (title.text().length() == 40) {
				// 登陆失败
				bookresult = "1";
			} else {
				bookresult = "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bookresult;

	}

	// 获得详细内容
	public static String getDetail(String bidnum) {
		String strResult = null;
		String baseurl = "http://121.248.104.139:8080/opac/";
		String url = baseurl + bidnum;
		HttpPost httpRequest = new HttpPost(url);
		// 取得HTTP response
		HttpResponse httpResponse;
		try {
			httpResponse = new DefaultHttpClient().execute(httpRequest);

			strResult = EntityUtils.toString(httpResponse.getEntity(),HTTP.UTF_8);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strResult;
	}
	//续借方法
	public static String reBook(String bar_code,String studentnum, String password)
	{
		String result = null;
		String result2= null;
		String loginurl = "http://121.248.104.139:8080/reader/redr_verify.php";
		String url = "http://121.248.104.139:8080/reader/ajax_renew.php?bar_code="+bar_code;
		String rebookresult = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpRequest = new HttpPost(loginurl);
		// Post运作传送变数必须用NameValuePair[]阵列储存
		// 传参数 服务端获取的方法为request.getParameter("name")
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("number", studentnum));
		params.add(new BasicNameValuePair("passwd", password));
		params.add(new BasicNameValuePair("select", "cert_no"));
		try {
			// 发出HTTP request
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 取得HTTP response
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			// 取出回应字串
			result = EntityUtils.toString(httpResponse.getEntity());
			Document doc = Jsoup.parse(result);
			Element title = doc.getElementsByTag("title").first();
			if (title.text().length() > 40) {
				// 登陆成功
				httpRequest = new HttpPost(url);
				httpResponse = httpClient.execute(httpRequest);
				result2 = EntityUtils.toString(httpResponse.getEntity(),HTTP.UTF_8);
				Document d = Jsoup.parse(result2);
				Element e = d.getElementsByTag("font").first();
				rebookresult = e.text();
			} else if (title.text().length() == 40) {
				// 登陆失败
				rebookresult = "续借失败";
			} else {
				rebookresult = "续借失败";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rebookresult;
	}
	//判断是否联网
    public static boolean isConnectInternet(Context c) {
		ConnectivityManager conManager = (ConnectivityManager) 
				c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isConnected()) {
			return false;
		}
		if (networkInfo.isConnected()) {
			if(networkInfo.getType()==ConnectivityManager.TYPE_WIFI)
				System.out.println("网络类型：WIFI");
			else if(networkInfo.getType()==ConnectivityManager.TYPE_MOBILE)
				System.out.println("网络类型：手机");
			else
				System.out.println("网络类型：未知");
			return true;
		}
		return false;
	}
}
