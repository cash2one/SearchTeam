package com.dangdang;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;




import com.dangdang.client.DBAction;
import com.dangdang.data.FuncQuery;
import com.dangdang.util.Calculator;
import com.dangdang.util.Config;
import com.dangdang.verifier.cateforecast.CategoryForecastVerifier;

public class CateForecastScheduler {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(CateForecastScheduler.class);
	private static Config config = new Config();
	private static int pass=0;
	private static int fail=0;
	private static int skip=0;
	private boolean isSearchBG=Boolean.valueOf(config.get_isSearchBG());
	
	@BeforeClass
	public void setup() {  		
	}  

	@AfterClass
	public void clearup() {
		logger.info("Result  ####    Passed Count:"+pass+" Failed Count:"+fail+" Skip Count:"+skip);
		double pr = Calculator.passrate(pass, pass+fail+skip);
		double fr = 100.00 - pr;		
		logger.info("Result  ####    Passed Rate:"+ pr+"% Failed Rate:"+ fr+"%");
	}   
	
	//分类预测归一化
	@Test(enabled=false)  
	public void CateForecastVerifer_1() { 
		try{
		
			DBAction action = new DBAction();
			action.setFuncCondition("id MOD 4 =0 ");
			List<FuncQuery> list = action.getFuncQuery();
			
			for(FuncQuery query:list){
				if(query.equals("")){
					skip++;
					break;
				}
				if(CategoryForecastVerifier.Verify(query, isSearchBG)){
					pass++;
				}else{
					fail++;
				}
			}
			
			
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			e.printStackTrace(new PrintStream(baos));  
			String exception = baos.toString();  
			logger.error(" - [LOG_EXCEPTION] - "+exception);
			fail++;
			e.printStackTrace();
		} finally {	
		}
	} 
	@Test(enabled=false)  
	public void CateForecastVerifer_2() { 
		try{
		
			DBAction action = new DBAction();
			action.setFuncCondition("id MOD 4 = 1 ");
			List<FuncQuery> list = action.getFuncQuery();
			
			for(FuncQuery query:list){
				if(query.equals("")){
					skip++;
					break;
				}
				if(CategoryForecastVerifier.Verify(query, isSearchBG)){
					pass++;
				}else{
					fail++;
				}
			}
			
			
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			e.printStackTrace(new PrintStream(baos));  
			String exception = baos.toString();  
			logger.error(" - [LOG_EXCEPTION] - "+exception);
			fail++;
			e.printStackTrace();
		} finally {	
		}
	} 

	@Test(enabled=false)  
	public void CateForecastVerifer_3() { 
		try{
		
			DBAction action = new DBAction();
			action.setFuncCondition("id MOD 4 = 2 ");
			List<FuncQuery> list = action.getFuncQuery();
			
			for(FuncQuery query:list){
				if(query.equals("")){
					skip++;
					break;
				}
				if(CategoryForecastVerifier.Verify(query, isSearchBG)){
					pass++;
				}else{
					fail++;
				}
			}
			
			
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			e.printStackTrace(new PrintStream(baos));  
			String exception = baos.toString();  
			logger.error(" - [LOG_EXCEPTION] - "+exception);
			fail++;
			e.printStackTrace();
		} finally {	
		}
	} 
	
	@Test(enabled=false)  
	public void CateForecastVerifer_4() { 
		try{
		
			DBAction action = new DBAction();
			action.setFuncCondition("id MOD 4 = 3 ");
			List<FuncQuery> list = action.getFuncQuery();
			
			for(FuncQuery query:list){
				if(query.equals("")){
					skip++;
					break;
				}
				if(CategoryForecastVerifier.Verify(query, isSearchBG)){
					pass++;
				}else{
					fail++;
				}
			}
			
			
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			e.printStackTrace(new PrintStream(baos));  
			String exception = baos.toString();  
			logger.error(" - [LOG_EXCEPTION] - "+exception);
			fail++;
			e.printStackTrace();
		} finally {	
		}
	} 
}
