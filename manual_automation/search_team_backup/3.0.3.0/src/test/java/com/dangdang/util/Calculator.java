package com.dangdang.util;

import java.math.BigDecimal;
import org.slf4j.LoggerFactory;


public class Calculator {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FileUtil.class);

	/*
	 * 计算用例通过率，added by 高彦君  @2015/06/04
	 */
	public static double passrate(int passed, int total){
		double passrate = 1;
		try{
			//e.g. 332/403 = 0.823821339...
			double tmprate = ( passed * 1.0 )/total;		
			BigDecimal b = new BigDecimal(tmprate);
			//取四位，四舍五入 = 0.8238
			passrate = b.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue(); 
			
		}catch(ArithmeticException e){			
			logger.error(" - [LOG_EXCEPTION] - "+ e.getMessage());
		}
		// pass rate： 0.8238 * 100 = 82.38
		return passrate * 100;		
	}
}