package com.dangdang.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.slf4j.LoggerFactory;

import com.dangdang.client.SearchRequester;
import com.dangdang.client.URLBuilder;

public class ProdIterator implements Iterator<Node> {
	
	//日志记录器
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ProdIterator.class);
	//商品节点列表
	private List<Node> productList = new ArrayList<Node>();
	//url组合map
	private Map<String,String> map = new HashMap<String,String>();
	//页面数
	private int pageCount = 1;     
	//页面容量
	private static int pageSize = 60;
	//当前页数
	private int pageIndex = 1;
	//指向当前商品列表上的当前商品
	private int point= -1;
	public int getPoint() {
		return point;
	}
	
	
//	private List<Node> final_productList;//最终全部结果
//	private int final_totalCount = 0;//最终实际结果个数

	//返回的最大商品数量
	private int maxIndex = 0;
	//总数
	private int totalCount = 0;
	//总数
	private String term = "";
	

	public int getTotalCount() {
		return totalCount;
	}

	public int getpageIndex() {
		return pageIndex;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}


	public int getMaxIndex() {
		return maxIndex;
	}


	public void setMaxIndex(int maxIndex) {
		this.maxIndex = maxIndex;
	}

	public static int getPageSize() {
		return pageSize;
	}

	public static void setPageSize(int PageSize) {
		pageSize = PageSize;
	}
	
	public ProdIterator(Map<String,String> map){
		this(map, 1199);//默认从0开始1200个
	}
	/*
	 * 	通过Map参数拼URL获取XML数据，再获取productList
	 */
	public ProdIterator(Map<String,String> map,int maxIndex){
		this.map = map;
		this.maxIndex = maxIndex;
		this.map.put("pg", String.valueOf(pageIndex));
		this.map.put("ps", String.valueOf(pageSize));
		String url = URLBuilder.buildURL(this.map);
		String xml = SearchRequester.get(url);
		logger.debug(url);
		try {
			Document doc = XMLParser.read(xml);
			productList = XMLParser.getProductNodes(doc);
			pageCount = Integer.valueOf(XMLParser.PageCount(doc));
			totalCount = Integer.valueOf(XMLParser.totalCount(doc));
			setTerm(XMLParser.term(doc));
			if (totalCount<maxIndex) {
				this.maxIndex = totalCount;
			}
			//logger.debug(" - [Iterator] - init the product List!");
		} catch (MalformedURLException | DocumentException e) {
			 ByteArrayOutputStream buf = new ByteArrayOutputStream();
			 e.printStackTrace(new PrintWriter(buf, true));
			 String expMessage = buf.toString();
			 logger.error(" - [LOG_EXCEPTION] - "+expMessage);
//			 logger.error(expMessage);
//			e.printStackTrace();
		}
	}
	
	/*
	 * 	通过Map参数拼URL获取XML数据，再获取productList
	 */
	public ProdIterator(Map<String,String> map,int maxIndex, String baseurl){
		this.map = map;
		this.maxIndex = maxIndex;
		this.map.put("pg", String.valueOf(pageIndex));
		this.map.put("ps", String.valueOf(pageSize));
		String url = URLBuilder.buildURL(this.map,baseurl);
		String xml = SearchRequester.get(url);
		logger.debug(url);
		try {
			Document doc = XMLParser.read(xml);
			productList = XMLParser.getProductNodes(doc);
			pageCount = Integer.valueOf(XMLParser.PageCount(doc));
			totalCount = Integer.valueOf(XMLParser.totalCount(doc));
			setTerm(XMLParser.term(doc));
			if (totalCount<maxIndex) {
				this.maxIndex = totalCount;
			}
			//logger.debug(" - [Iterator] - init the product List!");
		} catch (MalformedURLException | DocumentException e) {
			 ByteArrayOutputStream buf = new ByteArrayOutputStream();
			 e.printStackTrace(new PrintWriter(buf, true));
			 String expMessage = buf.toString();
			 logger.error(" - [LOG_EXCEPTION] - "+expMessage);
//			 logger.error(expMessage);
//			e.printStackTrace();
		}
	}
	
	public boolean reSet(){
		this.pageIndex=1;
		this.point =-1;
		this.map.put("pg", String.valueOf(pageIndex));
		this.map.put("ps", String.valueOf(pageSize));
		String url = URLBuilder.buildURL(this.map);
		String xml = SearchRequester.get(url);
		try {
			Document doc = XMLParser.read(xml);
			productList = XMLParser.getProductNodes(doc);
			pageCount = Integer.valueOf(XMLParser.PageCount(doc));
			totalCount = Integer.valueOf(XMLParser.totalCount(doc));
			//logger.debug(" - [Iterator] - reSet the product List!");
			return true;
		} catch (MalformedURLException | DocumentException e) {
			// TODO Auto-generated catch block
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			e.printStackTrace(new PrintStream(baos));  
			String exception = baos.toString();  
			logger.error(" - [LOG_EXCEPTION] - "+exception);
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 返回结果中是否还有下一个结果
	 */
	public boolean hasNext(){
		int productIndex = (pageIndex-1)*pageSize+point+1;
			if(pageIndex >= pageCount){
				if(point >= productList.size()-1| productIndex>=maxIndex){
//					//logger.debug(" - [Interator] - Dont has next!");
					return false;
				}else{
//					//logger.debug(" - [Interator] - has next!");
					return true;
				}
					
			}else{
				if(productIndex>=maxIndex){
					return false;
				}
//				//logger.debug(" - [Interator] - has next!");
				return true;
			}
	}
	
	public void remove(){
		
	}

	/**
	 * 得到下一个商品节点
	 */
	@Override
	public Node next() {
		if(hasNext()){
			if(point ==productList.size()-1){
				pageIndex++;
				map.put("pg", String.valueOf(pageIndex));
				map.put("ps", String.valueOf(pageSize));
				String url = URLBuilder.buildURL(map);
				String xml = SearchRequester.get(url);
				try {
					productList = XMLParser.getProductNodes(XMLParser.read(xml));
				} catch (MalformedURLException | DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				productList = URLBuilder.getXML(verifyPoint, keyWord, media);
				point=0;
				return productList.get(point);
			}else{
				point++;
				return productList.get(point);
			}
			
		}else{
			return null;
		}
	
	}

	/**
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}

	/**
	 * @param term the term to set
	 */
	public void setTerm(String term) {
		this.term = term;
	}

}