package org.ahope.cumtlib.Utilities;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class Feedback {
	public static String feedback(String contents) throws Exception{
    	String result = null;
    	String Url = "http://202.119.199.113:8292/news_for_android_server/FeedbackServlet?username="
		+ URLEncoder
				.encode(URLEncoder.encode("Õº Èπ›”√ªß", "UTF-8"), "UTF-8")
		+"&contents="
		+ URLEncoder.encode(URLEncoder.encode(contents, "UTF-8"),
						"UTF-8")
		+"&category="
		+ URLEncoder.encode(URLEncoder.encode("2", "UTF-8"),
						"UTF-8");
		HttpPost httpRequest = new HttpPost(Url);
		HttpResponse httpResponse;
		try {
			httpResponse = new DefaultHttpClient()
								.execute(httpRequest);
			
			result = EntityUtils.toString(httpResponse.getEntity());
			System.out.println(result);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
    }
}
