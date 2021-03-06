package com.dangdang.verifier.blacklist;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import com.dangdang.client.SearchRequester;
import com.dangdang.client.URLBuilder;
import com.dangdang.data.BlackListQuery;
import com.dangdang.util.XMLParser;

/**
 * @author liuzhipengjs@dangdang.com
 * @version 创建时间：2014年12月20日 上午11:26:05 类说明
 */
public class SearchThroughVerifier {
	
	
//	private static final CloseableHttpClient httpClient;
//	public static final String CHARSET = "GBK";
//	static {
//		//设置超时时间，禁止重定向
//        RequestConfig config = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(15000).setRedirectsEnabled(false).build();
//        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
//    }

	// 日志记录器
	private static Logger logger = Logger.getLogger(ShopThroughVerifier.class);

	public boolean doVerify(BlackListQuery queryInfo) {
		// TODO Auto-generated method stub
		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date start_date = formatDate.parse(queryInfo.getStartDate());
			Date end_date = formatDate.parse(queryInfo.getEndDate());
			Date now = new Date();
		    if(start_date.before(now)&&end_date.after(now)){//搜索直达生效
		    	logger.fatal("SearchThrough filer query ["+queryInfo.getQuery()+"] is on");
		    	return isSearchThrough(queryInfo,true);
		    }else{//搜索直达不生效
		    	logger.fatal("SearchThrough filer query ["+queryInfo.getQuery()+"] is off");
		    	return isSearchThrough(queryInfo,false);
		    }
		} catch (ParseException e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			e.printStackTrace(new PrintStream(baos));  
			String exception = baos.toString();  
			logger.error(" - [LOG_EXCEPTION] - "+exception);
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	private boolean isSearchThrough(BlackListQuery queryInfo, boolean b) {
		// TODO Auto-generated method stub
		Map<String,String> urlMap = this.getUrlMap(queryInfo);
		String urlString = URLBuilder.buildUrl(urlMap);
		String xml = SearchRequester.get(urlString);
		
		try {
			Document doc = XMLParser.read(xml);
			String type = XMLParser.shopinfotype(doc);
			String shopurl = XMLParser.shopinfourl(doc);
			String shopinfopromo = XMLParser.shopinfopromo(doc);
			if (type.equals("NULL")) {
				return !b;
				//op_type存的是附加信息
			}else if (type.equals("2")&&shopurl.equals(queryInfo.getCommons())&&shopinfopromo.equals(queryInfo.getOptype())) {
				return b;
			}else {
				return false;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		try {
//			CloseableHttpResponse response = httpClient.execute(httpGet);
//			int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode != 302) {
////                httpGet.abort();
//            	response.close();
//                logger.fatal("statusCode should be 302,error status code :" + statusCode);
//                return false;
//            }
//            
//            org.apache.http.Header header = response.getFirstHeader("Location");
//          
//            if (header.getValue().equals(queryInfo.getCommons())) {
//            	response.close();
//            	logger.fatal(" - [PASSED] - the link is right");
//        		return true;
//			}else {
//				response.close();
//				logger.fatal(" - [FAILED] - the link is wrong");
//				return false;
//			}
//            
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return false;
	}



	private Map<String,String> getUrlMap(BlackListQuery queryInfo){
		Map<String,String> map = new HashMap<String,String>();
		map.put("st", "full");
		map.put("ps", "60");
		map.put("um", "search_ranking");
		map.put("_new_tpl", "1");//search_ranking必加
		map.put("q", queryInfo.getQuery());
//		try {
//			
//			map.put("q", URLEncoder.encode(queryInfo.getQuery(), "GBK"));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		return map;
	}

}
