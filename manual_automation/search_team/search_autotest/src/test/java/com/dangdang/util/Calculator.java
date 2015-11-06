package com.dangdang.util;

import java.math.BigDecimal;
import org.slf4j.LoggerFactory;


public class Calculator {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FileUtil.class);

	/*
	 * 计算用例通过率，added by 高彦君  @2015/06/04
	 */
	public static double passrate(int passed, int total){
		double passrate = 0;
		try{
			//e.g. 332/403 = 0.823821339...
			if(total != 0){
				double tmprate = ( passed * 1.0 )/total;
				tmprate = tmprate * 100;
				BigDecimal b = new BigDecimal(tmprate);
				//取两位，四舍五入 = 82.38
				passrate = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); 		
			}
		}catch(ArithmeticException e){			
			logger.error(" - [LOG_EXCEPTION] - "+ e.getMessage());
		}
		// pass rate：  82.38
		return passrate;		
	}
	
	/**
	 * 跳过率
	 * @param skipped	跳过条数
	 * @param total		总条数
	 * @return			跳过率
	 */
	public static double skiprate(int skipped, int total){
		double passrate = 0;
		try{
			//e.g. 332/403 = 0.823821339...
			if(total != 0){
				double tmprate = ( skipped * 1.0 )/total;
				tmprate = tmprate * 100;
				BigDecimal b = new BigDecimal(tmprate);
				//取两位，四舍五入 = 82.38
				passrate = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); 		
			}
		}catch(ArithmeticException e){			
			logger.error(" - [LOG_EXCEPTION] - "+ e.getMessage());
		}
		// skip rate：  82.38
		return passrate;	
	}
}