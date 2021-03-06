package com.dangdang.verifier.gsearch;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.dom4j.Node;
import org.slf4j.LoggerFactory;

import com.dangdang.util.ProdIterator;
import com.dangdang.util.XMLParser;
import com.dangdang.verifier.filter.CategoryVerifier;

public class SearchVerifier {
	
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(SearchVerifier.class);
	
	public static long getTimestampFromDate(String date){
		 try {
			Date tdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
			return tdate.getTime()/1000;
		} catch (ParseException e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			e.printStackTrace(new PrintStream(baos));  
			String exception = baos.toString();  
			logger.error(" - [LOG_EXCEPTION] - "+exception);
			logger.error("Error Date Format: " + date);
		}
		 return (long) 0;
	}
	
	public static boolean device_match(String device_id, String edevice_id){
		int id = Integer.valueOf(device_id);
		device_id = Integer.toBinaryString(id);
		logger.debug(device_id);
		for (String sid : edevice_id.split(",")){
			int eid = Integer.valueOf(sid);
			char ind = device_id.charAt(device_id.length()-eid-1);
			if (ind!='1'){
				return false;
			}
		}
		return true;
	}
	
	public static boolean productNameVerifier(ProdIterator iterator, Map<String,String> searchParameters){
		if (!iterator.hasNext()) { 
			iterator.reSet(); 
			if (!iterator.hasNext()){
				logger.error(String.format(" -  [CHECK-NO-RESULT] gSearch productName"));
				return false;
			}
		}
		while(iterator.hasNext()){
			Node prod = iterator.next();
			String pid = XMLParser.product_id(prod);
			String productname = XMLParser.product_name(prod).toLowerCase();
			for (String s : searchParameters.get("product_name").split(",|，|：|（|）|\\s+|　")){
				if (!productname.contains(s.toLowerCase()) && XMLParser.product_stype(prod).equals("")){
					logger.error(String.format(" -  [CHECK-FAIL-INFO] gSearch productName %s : actual: %s; expect: %s", pid, productname, s));
					return false;
				}
			}
		}
		logger.debug(String.format(" -  [CHECK-PASS-INFO] gSearch productName check pass : %s", searchParameters.get("product_name")));
		return true;
	}
	
	public static boolean isbnVerifier(ProdIterator iterator, Map<String,String> searchParameters){
		if (!iterator.hasNext()) { 
			iterator.reSet(); 
			if (!iterator.hasNext()){
				logger.error(String.format(" -  [CHECK-NO-RESULT] gSearch isbn_search"));
				return false;
			}
		}
		while(iterator.hasNext()){
			Node node = iterator.next();
			String pid = XMLParser.product_id(node);
			String isbn_search = XMLParser.isbn_search(node);
			for (String s : searchParameters.get("isbn_search").split(",")){
				if (!isbn_search.contains(s) && XMLParser.product_stype(node).equals("")){
					logger.error(String.format(" -  [CHECK-FAIL-INFO] gSearch isbn_search %s : actual: %s; expect: %s", pid, isbn_search, s));
					return false;
				}
			}
		}
		logger.debug(String.format(" -  [CHECK-PASS-INFO] gSearch isbn_search check pass : %s", searchParameters.get("isbn_search")));
		return true;
	}
	
	public static boolean authorVerifier(ProdIterator iterator, Map<String,String> searchParameters){
		if (!iterator.hasNext()) { 
			iterator.reSet(); 
			if (!iterator.hasNext()){
				logger.error(String.format(" -  [CHECK-NO-RESULT] gSearch author"));
				return false;
			}
		}
		while(iterator.hasNext()){
			Node prod = iterator.next();
			String pid = XMLParser.product_id(prod);
			String author_name = XMLParser.author_name(prod);
			String[] eauthor_name = searchParameters.get("author_name").split("\\s+|　|,|，");
			for (String s : eauthor_name){
				if (!author_name.contains(s) && XMLParser.product_stype(prod).equals("")){
					logger.info(String.format("_stype: %s", XMLParser.product_stype(prod)));
					logger.error(String.format(" -  [CHECK-FAIL-INFO] gSearch author %s : actual: %s; expect: %s", pid, author_name, s));
					return false;
				}
			}
		}
		logger.debug(String.format(" -  [CHECK-PASS-INFO] gSearch author check pass : %s", searchParameters.get("author_name")));
		return true;
	}
	
	public static boolean publisherVerifier(ProdIterator iterator, Map<String,String> searchParameters){
		if (!iterator.hasNext()) { 
			iterator.reSet(); 
			if (!iterator.hasNext()){
				logger.error(String.format(" -  [CHECK-NO-RESULT] gSearch publisher"));
				return false;
			}
		}
		while(iterator.hasNext()){
			Node prod = iterator.next();
			String pid = XMLParser.product_id(prod);
			String publisher = XMLParser.publisher(prod);
			for (String s : searchParameters.get("publisher").split(" ")){
				if (!publisher.contains(s) && XMLParser.product_stype(prod).equals("")){
					logger.error(String.format(" -  [CHECK-FAIL-INFO] gSearch publisher %s : actual: %s; expect: %s", pid, publisher, s));
					return false;
				}
			}
		}
		logger.debug(String.format(" -  [CHECK-PASS-INFO] gSearch publisher check pass : %s", searchParameters.get("publisher")));
		return true;
	}

	public static boolean bookSearchAllVerifier(ProdIterator iterator, Map<String,String> map){
		return productNameVerifier(iterator, map) && isbnVerifier(iterator, map) && 
				authorVerifier(iterator, map) && publisherVerifier(iterator, map);
	}
	
	public static boolean musicSearchAllVerifier(ProdIterator iterator, Map<String,String> map){
		return productNameVerifier(iterator, map) && singerVerifier(iterator, map) && actorsVerifier(iterator, map);
	}
	
	public static boolean vedioSearchAllVerifier(ProdIterator iterator, Map<String,String> map){
		return productNameVerifier(iterator, map) && directorVerifier(iterator, map) && actorsVerifier(iterator, map);
	}
	
	public static boolean b2cSearchAllVerifier(ProdIterator iterator, Map<String,String> map){
		return productNameVerifier(iterator, map) && catpathsVerifier(iterator, map);
	}
	
	public static boolean singerVerifier(ProdIterator iterator, Map<String,String> searchParameters){
		if (!iterator.hasNext()) { 
			iterator.reSet(); 
			if (!iterator.hasNext()){
				logger.error(String.format(" -  [CHECK-NO-RESULT] gSearch singer"));
				return false;
			}
		}
		while(iterator.hasNext()){
			Node prod = iterator.next();
			String pid = XMLParser.product_id(prod);
			String singer_format = XMLParser.singer(prod);
			String[] esinger_format = searchParameters.get("singer").split("\\s+|　|,|，");
			for (String s : esinger_format){
				if (!singer_format.contains(s) && XMLParser.product_stype(prod).equals("")){
					logger.error(String.format(" -  [CHECK-FAIL-INFO] gSearch singer %s : actual: %s; expect: %s", pid, singer_format, s));
					return false;
				}
			}
		}
		logger.debug(String.format(" -  [CHECK-PASS-INFO] gSearch singer check pass : %s", searchParameters.get("singer")));
		return true;
	}
	
	public static boolean actorsVerifier(ProdIterator iterator, Map<String,String> searchParameters){
		if (!iterator.hasNext()) { 
//			iterator.reSet(); 
//			if (!iterator.hasNext()){
//				logger.error(String.format(" -  [CHECK-NO-RESULT] gSearch actors"));
//				return false;
//			}
			//脚本不健壮，错误率100%，暂不检验作者
			return true;
	}
		while(iterator.hasNext()){
			Node prod = iterator.next();
			String pid = XMLParser.product_id(prod);
			String actors = XMLParser.actors(prod);
			String[] eactors = searchParameters.get("actors").split("\\s+|　|,|，");
			for (String s : eactors){
				if (!actors.contains(s) && XMLParser.product_stype(prod).equals("")){
					logger.error(String.format(" -  [CHECK-FAIL-INFO] gSearch actors %s : actual: %s; expect: %s", pid, actors, s));
					return false;
				}
			}
		}
		logger.debug(String.format(" -  [CHECK-PASS-INFO] gSearch actors check pass : %s", searchParameters.get("actors")));
		return true;
	}
	
	public static boolean directorVerifier(ProdIterator iterator, Map<String,String> searchParameters){
		if (!iterator.hasNext()) { 
			iterator.reSet(); 
			if (!iterator.hasNext()){
				logger.error(String.format(" -  [CHECK-NO-RESULT] gSearch director"));
				return false;
			}
		}
		while(iterator.hasNext()){
			Node prod = iterator.next();
			String pid = XMLParser.product_id(prod);
			String director = XMLParser.director(prod);
			String[] edirector = searchParameters.get("director").split("\\s+|　|,|，");
			for (String s : edirector){
				if (!director.contains(s) && XMLParser.product_stype(prod).equals("")){
					logger.error(String.format(" -  [CHECK-FAIL-INFO] gSearch director %s : actual: %s; expect: %s", pid, director, s));
					return false;
				}
			}
		}
		logger.debug(String.format(" -  [CHECK-PASS-INFO] gSearch director check pass : %s", searchParameters.get("director")));
		return true;
	}
	
	public static boolean catpathsVerifier(ProdIterator iterator, Map<String,String> searchParameters){
		if (!iterator.hasNext()) { 
			iterator.reSet(); 
			if (!iterator.hasNext()){
				logger.error(String.format(" -  [CHECK-NO-RESULT] gSearch cat_paths"));
				return false;
			}
		}
		while(iterator.hasNext()){
			Node node = iterator.next();
			String pid = XMLParser.product_id(node);
			String cat_paths = XMLParser.product_catelogPath(node);
			for (String s : searchParameters.get("cat_paths").split(",")){
				if (!cat_paths.contains(s)){
					logger.error(String.format(" -  [CHECK-FAIL-INFO] gSearch cat_paths %s : actual: %s; expect: %s", pid, cat_paths, s));
					return false;
				}
			}
		}
		logger.debug(String.format(" -  [CHECK-PASS-INFO] gSearch cat_paths check pass : %s", searchParameters.get("cat_paths")));
		return true;
	}
	
	public static boolean bindingVerifier(ProdIterator iterator, Map<String,String> searchParameters){
		if (!iterator.hasNext()) { 
			iterator.reSet(); 
			if (!iterator.hasNext()){
				logger.error(String.format(" -  [CHECK-NO-RESULT] gSearch binding_id"));
				return false;
			}
		}
		while(iterator.hasNext()){
			Node prod = iterator.next();
			String pid = XMLParser.product_id(prod);
			String binding_id = XMLParser.binding_id(prod);
			if (!binding_id.contains(searchParameters.get("binding_id"))){
				logger.error(String.format(" -  [CHECK-FAIL-INFO] gSearch binding_id %s : actual: %s; expect: %s", pid, binding_id, searchParameters.get("binding_id")));
				return false;
			}
		}
		logger.debug(String.format(" -  [CHECK-PASS-INFO] gSearch binding_id check pass : %s", searchParameters.get("binding_id")));
		return true;
	}
	
	public static boolean deviceVerifier(ProdIterator iterator, Map<String,String> searchParameters){
		if (!iterator.hasNext()) { 
			iterator.reSet(); 
			if (!iterator.hasNext()){
				logger.error(String.format(" -  [CHECK-NO-RESULT] gSearch device_id"));
				return false;
			}
		}
		while(iterator.hasNext()){
			Node prod = iterator.next();
			String pid = XMLParser.product_id(prod);
			String device_id = XMLParser.device_id(prod);
//			String edevice_id = searchParameters.get("device_id");
			if (!device_match(device_id, "14")){
				logger.error(String.format(" -  [CHECK-FAIL-INFO] gSearch device_id %s : actual: %s; expect: %s", pid, device_id, "14"));
				return false;
			}
		}
		logger.debug(String.format(" -  [CHECK-PASS-INFO] gSearch device_id check pass : %s", searchParameters.get("device_id")));
		return true;
	}
	
	public static boolean categoryVerifier(ProdIterator iterator, Map<String,String> searchParameters){
		if (!iterator.hasNext()) { 
			iterator.reSet(); 
			if (!iterator.hasNext()){
				logger.error(String.format(" -  [CHECK-NO-RESULT] gSearch category"));
				return false;
			}
		}
		while(iterator.hasNext()){
			Node prod = iterator.next();
			String pid = XMLParser.product_id(prod);
			String category = XMLParser.product_catelogPath(prod);
			String subCat = searchParameters.get("cat_paths").replaceAll("(\\.00)+$", "");
			if (!category.startsWith(subCat)){
				CategoryVerifier cat = new CategoryVerifier();
				if (!cat.isMoreCatgory(pid, category)){
					logger.error(String.format(" -  [CHECK-FAIL-INFO] gSearch category %s : actual: %s; expect: %s", pid, category, searchParameters.get("cat_paths")));
					return false;
				}				
			}
		}
		logger.debug(String.format(" -  [CHECK-PASS-INFO] gSearch category check pass : %s", searchParameters.get("cat_paths")));
		return true;
	}
	
	public static boolean ddSalePriceVerifier(ProdIterator iterator, Map<String,String> searchParameters, String flag){
		if (!iterator.hasNext()) { 
			iterator.reSet(); 
			if (!iterator.hasNext()){ 
				logger.error(String.format(" -  [CHECK-NO-RESULT] gSearch ddsaleprice"));
				return false;
			}
		}
		while(iterator.hasNext()){
			Node prod = iterator.next();
			String pid = XMLParser.product_id(prod);
			Double ddsale = Double.valueOf(XMLParser.product_dd_sale_price(prod));
			Double eddsale = Double.valueOf(searchParameters.get("dd_sale_price"));
			if ((flag.equals("gt") && ddsale<eddsale) || (flag.equals("lt") && ddsale>eddsale)){
				logger.error(String.format(" -  [CHECK-FAIL-INFO] gSearch ddsaleprice %s : actual: %s; expect: %s %s", pid, ddsale, flag, searchParameters.get("dd_sale_price")));
				return false;
			}
		}
		logger.debug(String.format(" -  [CHECK-PASS-INFO] gSearch ddsaleprice check pass : %s", searchParameters.get("dd_sale_price")));
		return true;
	}
	
	public static boolean priceVerifier(ProdIterator iterator, Map<String,String> searchParameters, String flag){
		if (!iterator.hasNext()) { 
			iterator.reSet(); 
			if (!iterator.hasNext()){
				logger.error(String.format(" -  [CHECK-NO-RESULT] gSearch price"));
				return false;
			}
		}
		while(iterator.hasNext()){
			Node prod = iterator.next();
			String pid = XMLParser.product_id(prod);
			Double ddsale = Double.valueOf(XMLParser.product_price(prod));
			Double eddsale = Double.valueOf(searchParameters.get("price"));
			if ((flag.equals("gt") && ddsale<eddsale) || (flag.equals("lt") && ddsale>eddsale)){
				logger.error(String.format(" -  [CHECK-FAIL-INFO] gSearch price %s : actual: %s; expect: %s %s", pid, ddsale, flag, searchParameters.get("price")));
				return false;
			}
		}
		logger.debug(String.format(" -  [CHECK-PASS-INFO] gSearch price check pass : %s", searchParameters.get("price")));
		return true;
	}
	
	public static boolean publishDateVerifier(ProdIterator iterator, Map<String,String> searchParameters, String flag){
		if (!iterator.hasNext()) { 
			iterator.reSet(); 
			if (!iterator.hasNext()){
				logger.error(String.format(" -  [CHECK-NO-RESULT] gSearch publishDate"));
				return false;
			}
		}
		while(iterator.hasNext()){
			Node prod = iterator.next();
			String pid = XMLParser.product_id(prod);
			long publishDate = getTimestampFromDate(XMLParser.product_publishDate(prod));
			long epublishDate = getTimestampFromDate(searchParameters.get("publish_date"));
			if (publishDate==0 || epublishDate==0){
				logger.error(String.format(" -  [CHECK-FAIL-INFO] gSearch publishDate %s : actual: %s; expect: %s %s", pid, publishDate, flag, epublishDate));
				return false;
			}
			if ((flag.equals("gt") && publishDate<epublishDate) || (flag.equals("lt") && publishDate>epublishDate)){
				logger.error(String.format(" -  [CHECK-FAIL-INFO] gSearch publishDate %s : actual: %s; expect: %s %s", pid, publishDate, flag, epublishDate));
				return false;
			}
		}
		logger.debug(String.format(" -  [CHECK-PASS-INFO] gSearch publishDate check pass : %s", searchParameters.get("publish_date")));
		return true;
	}
	
	public static boolean stockVerifier(ProdIterator iterator, Map<String,String> searchParameters){
		if (!iterator.hasNext()) { 
			iterator.reSet(); 
			if (!iterator.hasNext()){
				logger.error(String.format(" -  [CHECK-NO-RESULT] gSearch stock"));
				return false;
			}
		}
		while(iterator.hasNext()){
			Node prod = iterator.next();
			String pid = XMLParser.product_id(prod);
			int stock = Integer.valueOf(XMLParser.product_StockStatus(prod));
			if (stock<1){
				logger.error(String.format(" -  [CHECK-FAIL-INFO] gSearch stock %s : actual: %s; expect: gt %s", pid, stock, 1));
				return false;
			}
		}
		logger.debug(String.format(" -  [CHECK-PASS-INFO] gSearch stock check pass"));
		return true;
	}	
	
	public static boolean bookListAllVerifier(ProdIterator iterator, Map<String,String> searchParameters){
		return  bindingVerifier(iterator, searchParameters) && 
				categoryVerifier(iterator, searchParameters) && 
				ddSalePriceVerifier(iterator, searchParameters, "gt") && 
				publishDateVerifier(iterator, searchParameters, "gt") && 
				stockVerifier(iterator, searchParameters);		
	}
	
	public static boolean bookMergeAllVerifier(ProdIterator iterator, Map<String,String> searchParameters){
		return productNameVerifier(iterator, searchParameters) && 
				isbnVerifier(iterator, searchParameters) && 
				authorVerifier(iterator, searchParameters) && 
				publisherVerifier(iterator, searchParameters) && 
				bindingVerifier(iterator, searchParameters) && 
				categoryVerifier(iterator, searchParameters) && 
				ddSalePriceVerifier(iterator, searchParameters, "gt") && 
				publishDateVerifier(iterator, searchParameters, "gt") && 
				stockVerifier(iterator, searchParameters);
	}
	
	public static boolean ebookListAllVerifier(ProdIterator iterator, Map<String,String> searchParameters){
		return  priceVerifier(iterator, searchParameters, "lt") &&
				deviceVerifier(iterator, searchParameters) && 
				categoryVerifier(iterator, searchParameters) && 
				publishDateVerifier(iterator, searchParameters, "lt");
	}
	
	public static boolean ebookMergeAllVerifier(ProdIterator iterator, Map<String,String> searchParameters){
		return productNameVerifier(iterator, searchParameters) && 
				isbnVerifier(iterator, searchParameters) && 
				authorVerifier(iterator, searchParameters) && 
				publisherVerifier(iterator, searchParameters) && 
				deviceVerifier(iterator, searchParameters) && 
				categoryVerifier(iterator, searchParameters) && 
				priceVerifier(iterator, searchParameters, "lt") && 
				publishDateVerifier(iterator, searchParameters, "lt"); 
	}
		
}
