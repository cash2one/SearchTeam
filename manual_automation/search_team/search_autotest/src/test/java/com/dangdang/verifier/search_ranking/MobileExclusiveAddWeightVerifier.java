package com.dangdang.verifier.search_ranking;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import com.dangdang.client.SearchRequester;
import com.dangdang.client.URLBuilder;
import com.dangdang.util.AssertTools;
import com.dangdang.util.DateTimeHandler;
import com.dangdang.util.ProdIterator;
import com.dangdang.util.XMLParser;
/**
 * 说明： 无线端手机专享价商品加权策略，手机专享价商品，且exclusive开始和结束时间在有效期内的商品
 * 在无线端（platform=1）加权
 * 开关参数是platform = 1
 * @author gaoyanjun
 * @version 创建时间：2015/05/11 16:35 
 * 
 */
public class MobileExclusiveAddWeightVerifier {

	public final static org.slf4j.Logger logger = LoggerFactory.getLogger(MobileExclusiveAddWeightVerifier.class);
		
	/**
	 * 无线端手机专享价加权策略
	 * @param old_iterator
	 * @param new_iterator
	 * @param query
	 * @param vps
	 * @return
	 * @throws ParseException 
	 */
	public boolean Verifier(ProdIterator old_iterator,ProdIterator new_iterator,String query, String vps) throws ParseException{	
		boolean fail = false;
		boolean hasResult = false;
		int unmatchedCount = 0;
		
		Map<String, Integer> mapOldResult = new HashMap<String, Integer>();
		Map<String, Integer> mapNewResult = new HashMap<String, Integer>();
		
		// 增加platform=1参数前的结果集，取符合手机专享价时间的pid 和 score 放到mapOldResult中
		while (old_iterator.hasNext()){
			Node old_node = old_iterator.next();
			String old_productId = XMLParser.product_id(old_node);
			Integer beforeScore = Integer.parseInt(XMLParser.product_scope(old_node));	
			String exclusive_begin_time = XMLParser.product_exclusive_begin_date(old_node);
			String exclusive_end_time = XMLParser.product_exclusive_end_date(old_node);	
			if(!exclusive_begin_time.isEmpty() && !exclusive_end_time.isEmpty()){
				Date now = new Date();
				String nowDate = DateTimeHandler.DateToString(now);
				
				if(DateTimeHandler.CompareDate(exclusive_begin_time, nowDate) == 2
						&& DateTimeHandler.CompareDate(nowDate,exclusive_end_time) == 2){
					mapOldResult.put(old_productId, beforeScore);
				}
			}
		}
		
		// 增加platform=1参数后的结果集，取pid 和 score 放到mapNewResult中
		while(new_iterator.hasNext()){
			Node new_node = new_iterator.next();
			String new_product_id = XMLParser.product_id(new_node);
			Integer afterScore = Integer.parseInt(XMLParser.product_scope(new_node));
			mapNewResult.put(new_product_id, afterScore);
		}
		
		// 以mapOldResult为基准，遍历每个pid；
		// 如果mapNewResult中包含该pid，则对比score值是否正确增加
		// 如果不包含该pid，则跳过
		Iterator<String> it = mapOldResult.keySet().iterator();
		while(it.hasNext()){
			hasResult = true;
			Object key = it.next();
			if(mapNewResult.containsKey(key)){
				Integer beforeScore = mapOldResult.get(key);
				Integer afterScore = mapNewResult.get(key);
				
				// 加权，商业因素分加上10分		
				if(AssertTools.BusinessScore(afterScore) != AssertTools.BusinessScore(beforeScore) + 10){					
					logger.error("query -["+ query +"], Score of product [" + key + "] was not set to right value: "
							+ "old score is [" + beforeScore +"], new score is ["+ afterScore +"]");
					fail = true;
					unmatchedCount ++;
				} 
			}
/*			else{
				logger.error("query -["+ query +"], after using new strategy, did not find product [" + key.toString() + "] in new result set!");
				fail = true;
				unmatchedCount ++;
			}*/
		}
		
		if (!hasResult){
			logger.error(" - [LOG_SKIP] - verify function ["+ vps +"], query - [" + query+ "] - NO RESULT" );
			return true;
		}
		
		if (fail){ 
			logger.error(" - [LOG_FAILED] - verify function ["+ vps +"], query - [" + query+ "]"
					+ "- unMatched product count is [" + unmatchedCount +"]");
			return false;
		}
		else {
			logger.info(" - [LOG_PASS] - verify function ["+ vps +"], query - [" + query + "]");
			return true;
		} 
		
		// 原来的实现方式：问题是里层新结果集遍历后，下次会记住位置，前面的品会被忽略，比较不全面；如果每次遍历new_iterator前都reset一遍，会大大降低速度
		/*while(old_iterator.hasNext()){
			hasResult = true;
			Node old_node = old_iterator.next();
			String old_productId = XMLParser.product_id(old_node);
			Integer beforeScore = Integer.parseInt(XMLParser.product_scope(old_node));	
			String exclusive_begin_time = XMLParser.product_exclusive_begin_date(old_node);
			String exclusive_end_time = XMLParser.product_exclusive_end_date(old_node);		
			
			if(!exclusive_begin_time.isEmpty() && !exclusive_end_time.isEmpty()){
				Date now = new Date();
				String nowDate = DateTimeHandler.DateToString(now);
				
				if(DateTimeHandler.CompareDate(exclusive_begin_time, nowDate) == 2
						&& DateTimeHandler.CompareDate(nowDate,exclusive_end_time) == 2){			
					String new_product_id = null;
					Integer afterScore = null;
					
					// 外层依次循环时，里层循环没有从头开始，导致前面走过的一部分pid被忽略掉，所以重新reset
//					if(!new_iterator.reSet())
//						return true;
				
					while(new_iterator.hasNext()){
						Node new_node = new_iterator.next();
						new_product_id = XMLParser.product_id(new_node);
						
						if(new_product_id.equals(old_productId)){
							afterScore = Integer.parseInt(XMLParser.product_scope(new_node));								
							break;
						}else
							continue;					
					}
					
					if(afterScore == null){
						logger.error("query -["+ query +"],  did not find pid [" + old_productId + "] in new result set");
						fail = true;
						unmatchedCount ++;
					}else{			
						// 降权，商业因素分加上10分				
						if(AssertTools.BusinessScore(afterScore) != AssertTools.BusinessScore(beforeScore) + 10){					
							logger.error("query -["+ query +"], Score of product [" + old_productId + "] was not set to right value: "
									+ "old score is [" + beforeScore +"], new score is ["+ afterScore +"]");
							fail = true;
							unmatchedCount ++;
						} 
					}
				}
			}
		}*/
		
		
	}	
}