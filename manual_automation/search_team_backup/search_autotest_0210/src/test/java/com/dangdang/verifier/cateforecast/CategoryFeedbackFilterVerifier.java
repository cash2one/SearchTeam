package com.dangdang.verifier.cateforecast;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import com.dangdang.client.SearchRequester;
import com.dangdang.client.URLBuilder;
import com.dangdang.data.FuncQuery;
import com.dangdang.util.XMLParser;

/**
 * 对分类反馈过滤机制进行验证
 * 
 * @author zhangxiaojing
 * 
 */
public class CategoryFeedbackFilterVerifier {
	//日志记录
	public static  Logger logger = Logger.getLogger(CategoryFeedbackFilterVerifier.class);
	
	public static boolean Verify(FuncQuery query){
		//得到Query词
		String sourceQuery = query.getFquery();
			//构造url的参数map
			Map<String,String> urlMap = new HashMap<String,String>();
			urlMap.put("st", "full");
			urlMap.put("um", "search_ranking");
			urlMap.put("_new_tpl", "1");
			urlMap.put("q",sourceQuery);
//			urlMap.put("ps","60");
			//访问后台，得到返回的xml结果
			String url = URLBuilder.buildUrl(urlMap);
			String xml = SearchRequester.get(url);
			
			try {
				Document doc = XMLParser.read(xml);
				if (Integer.valueOf(XMLParser.totalCount(doc))<600) {
					logger.fatal(" - [LOG_SKIPPED] - 结果总数小于600");
					return true;
				}
				List<Node> pathList = XMLParser.pathInfo(doc);
				if(pathList!=null){
					//验证是否为分类直达(是否包含(result\StatInfo\Path节点)
					if(pathList.size()==0){
						//如果不是分类直达，则获取搜索结果中前三个第一分类反馈类的商品节点
						List<Node> productList = getFirstFeedbackProducts(sourceQuery,doc);
						for(Node product:productList){
							//图书分类，cat_paths 为01开头(result\Body\Product\cat_paths)
							if(XMLParser.product_catelogPath(product).trim().startsWith("01")){
								//不是招商品
								if(!XMLParser.product_shopID(product).trim().endsWith("0")){
									logger.error("the first 3 feedback poduct "+XMLParser.product_id(product)+"'s shop id should be 0! ");
									return false;
								}
							}
							//验证三个商品不是区域有货或缺货，stock_status=0(result\Body\Product\stock_status)
							if(XMLParser.product_StockStatus(product).trim().equals("0")){
								logger.error("the first 3 feedback poduct "+XMLParser.product_id(product)+"'s stock status should be 0! ");
								return false;
							}
						}
					}
				}
			} catch (MalformedURLException | DocumentException e) {
				e.printStackTrace();
				return false;
			}	
	
	    logger.fatal(" - [LOG_SUCCESS] - "+"Test Successed! Query:"+sourceQuery);
		return true;
	}	
	
	private static List<Node> getFirstFeedbackProducts(String query,Document doc){	
		List<Node> list = new ArrayList<Node>();
        //获取第一反馈分类
		String fistFeedbackCate = getFirstFeedbackCate(query);
		if(fistFeedbackCate==null){
			logger.fatal("the fist feedback category if "+query+" is null!");
			return list;
		}
		@SuppressWarnings("unchecked")
		List<Node> productList = XMLParser.getProductNodes(doc);
		for(Node product:productList){
			if(XMLParser.product_catelogPath(product).contains(fistFeedbackCate)){
				list.add(product);
				if(list.size()==3){//获取搜索结果中前三个第一分类反馈类的商品节点
					return list;
				}
			}
		}
		return list;			
	}
	
	private static String getFirstFeedbackCate(String query){
		
		// 拿到query词的第一个分类的权重
				String firstCategory = null;
				// 第一分类的权重
//				int firstPriority = 0;
				
		// 获取第一分类
				Map<String, String> urlMap4Ranking = new HashMap<String, String>();
				urlMap4Ranking.put("st", "full");
				urlMap4Ranking.put("um", "search_ranking");
				urlMap4Ranking.put("_mod_ver", "S5");
				urlMap4Ranking.put("_new_tpl", "1");
				
				urlMap4Ranking.put("q", query);
				urlMap4Ranking.put("ranking_total", "1");
				urlMap4Ranking.put("ranking_info", "1");
				
//				logger.info("####################################################");
				logger.debug(urlMap4Ranking);
				String urlRanking = URLBuilder.buildUrl(urlMap4Ranking);
				String xmlRanking = SearchRequester.get(urlRanking);
				logger.info(urlRanking);
				Document docRanking = null;
				try {
					docRanking = XMLParser.read(xmlRanking);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (DocumentException e) {
					e.printStackTrace();
				}

				@SuppressWarnings("unchecked")
				List<Node> firstPaths = XMLParser.getcatepaths(docRanking);
				if (firstPaths.size() > 0) {
					String firstPathString = firstPaths.get(0).getStringValue();
					String[] arrString = firstPathString.split(":");
					firstCategory = arrString[0];
//					firstPriority = Integer.valueOf(arrString[1]);
					return firstCategory;
				}else {
					logger.info(" - [LOG_EXCEPTION] - " + "该词无预测分类");
					return firstCategory;
				}
	}
	
	
	
}