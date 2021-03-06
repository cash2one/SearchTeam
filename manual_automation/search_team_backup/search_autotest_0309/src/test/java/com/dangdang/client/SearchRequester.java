package com.dangdang.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;


/**
 * 这是一个想搜索引擎发送请求的动作类。 拼装一个url后通过get方法想搜索引擎发送请求，接收返回结果。
 * 
 * @author zhangyamin
 * 
 */
public class SearchRequester {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(SearchRequester.class);
	/**
	 * 以post方法向服务发送请求
	 * 
	 * @param url
	 *            服务所在地址
	 * @param params
	 *            键值对形式的访问内容
	 * @return
	 */
	public static String post(String url, Map<String, String> params) {
		return SearchRequest.doPost(url, params);
//		DefaultHttpClient httpclient = new DefaultHttpClient();
//		String body = null;
//
//		HttpPost post = postForm(url, params);
//
//		body = invoke(httpclient, post);
//
//		httpclient.getConnectionManager().shutdown();
//
//		return body;
	}

	/**
	 * 以get方法想服务发送请求
	 * 
	 * @param url
	 *            目标服务地址
	 * @return
	 */
	public static String get(String url) {
//		logger.debug("- [get] - URL:" + url);
		return SearchRequest.doGet(url,null);
//		DefaultHttpClient httpclient = new DefaultHttpClient();
//		String body = null;
//
//		try {
//			// log.info("create httppost:" + url);
//			HttpGet get = new HttpGet(url);
//			body = invoke(httpclient, get);
//			httpclient.getConnectionManager().shutdown();
//		} catch (Exception e) {
//			 ByteArrayOutputStream buf = new ByteArrayOutputStream();
//			 e.printStackTrace(new PrintWriter(buf, true));
//			 String expMessage = buf.toString();
//			logger.error(" - [LOG_EXCEPTION] - "+e.getMessage());
//			logger.error(expMessage);
//			
//			// TODO: handle exception
//		}
////		logger.debug("- [get] - URL:" + url);
////		logger.debug("-[get] - response:" + body);
//		return body;
	}

	private static HttpPost postForm(String url, Map<String, String> params) {

		HttpPost httpost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}

		try {
			// log.info("set utf-8 form entity to httppost");
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			e.printStackTrace(new PrintStream(baos));  
			String exception = baos.toString();  
			logger.error(" - [LOG_EXCEPTION] - "+exception);
			e.printStackTrace();
		}

		return httpost;
	}

	private static String invoke(DefaultHttpClient httpclient,
			HttpUriRequest httpost) {

		HttpResponse response = sendRequest(httpclient, httpost);
		String body = paseResponse(response);

		return body;
	}

	private static String paseResponse(HttpResponse response) {
		// log.info("get response from http server..");
		HttpEntity entity = response.getEntity();

		// log.info("response status: " + response.getStatusLine());
		String charset = EntityUtils.getContentCharSet(entity);
		// log.info(charset);

		String body = null;
		try {
			body = EntityUtils.toString(entity);
			// log.info(body);
		} catch (ParseException | IOException e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			e.printStackTrace(new PrintStream(baos));  
			String exception = baos.toString();  
			logger.error(" - [LOG_EXCEPTION] - "+exception);
			e.printStackTrace();
		}
		return body;
	}

	private static HttpResponse sendRequest(DefaultHttpClient httpclient,
			HttpUriRequest httpost) {
		// log.info("execute post...");
		HttpResponse response = null;

		try {
			response = httpclient.execute(httpost);
		} catch (IOException e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			e.printStackTrace(new PrintStream(baos));  
			String exception = baos.toString();  
			logger.error(" - [LOG_EXCEPTION] - "+exception);
			e.printStackTrace();
		}
		return response;
	}

	public static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[] {};
                    }
 
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }
 
                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }

					@Override
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] arg0,
							String arg1)
							throws java.security.cert.CertificateException {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void checkServerTrusted(
							java.security.cert.X509Certificate[] arg0,
							String arg1)
							throws java.security.cert.CertificateException {
						// TODO Auto-generated method stub
						
					}
                }
        };
     
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			e.printStackTrace(new PrintStream(baos));  
			String exception = baos.toString();  
			logger.error(" - [LOG_EXCEPTION] - "+exception);
            e.printStackTrace();
        }
    }
	
}
