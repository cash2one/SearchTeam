package com.dangdang;
/**
 * @author dongxiaobing
 * @func 搜索页增加指定query改写词典是指搜索后台支持人工对具体搜索词进行改写的配置；
	词典配置字段：<原query，改写后的query>；
	应用说明：搜索的召回，排序以及query_pack中的逻辑均使用改写后的query；
	
	query改写词典/d1/search/search_v3/modules/query_pack/NoResultQueryText.txt
	直接人工编写该文件，然后重载query_pack即可

	NoResultQueryText.txt中原始query和替换后的query用====分隔
 */

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class QueryModify {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(QueryModify.class);
	@BeforeMethod 
	public void setup() {  	
	}  

	@AfterMethod 
	public void clearup() { 
	}   
	
	/*
	 * query改写
	 */
	@Test(enabled=true)  
	public void query_modify_test1() { 
		try{
			long d = System.currentTimeMillis() ;
			logger.info(Long.toString(d));
			TestLauncher tl = new TestLauncher();
			//"dxb"只是为了调用一个被重载的方法，无特殊意义
			tl.start("verify_point='query_modify'", "fvp_id=50","dxb");
			//tl.start("id='182255'", "fvp_id=43","dxb");
			long d2 = System.currentTimeMillis();
			logger.info("总耗时："+(d2-d)/1000.0+"秒");
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			e.printStackTrace(new PrintStream(baos));  
			String exception = baos.toString();  
			logger.error(" - [LOG_EXCEPTION] - "+exception);
			e.printStackTrace();
		} finally {	
		}
	}	
}