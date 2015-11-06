package com.dangdang.verifier.filter;

import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Node;

import com.dangdang.util.ProdIterator;
import com.dangdang.util.XMLParser;
import com.dangdang.verifier.iVerifier.IFilterVerifier;

public class BrandVerifier extends IFilterVerifier {

	private static Logger logger = Logger.getLogger(BrandVerifier.class);

	@Override
	public boolean doVerify(ProdIterator iterator, Map<String, String> map, boolean hasResult) {
		String eBrand = map.get("brand");
		if(!iterator.hasNext()){
			iterator.reSet();
		}
		while(iterator.hasNext()){
			Node prod = iterator.next();
			if(!isBrand(prod, hasResult, map, eBrand)){
				return false;
			}
		}
		logger.debug(String.format(" -  [CHECK-PASS-INFO] check pass for 【brand】 filte : %s", eBrand));
		return true;
	}
	
	public boolean isBrand(Node prod,boolean hasResult,Map<String,String> map,String eBrand){
		logger.debug("/****************************product**************************/");
		String pid = XMLParser.product_id(prod);
		String brandId = XMLParser.product_brand(prod);
		logger.debug("/****************************end**************************/");
		if (hasResult && !brandId.equals(eBrand)){
			logger.error(String.format(" -  [CHECK-FAIL-INFO] %s : actual: %s; expect: %s", pid, brandId, eBrand));
			return false;
		}else if (!hasResult && brandId.equals(eBrand)){
			logger.error(String.format(" -  [CHECK-NO-RESULT] %s : actual: %s; expect: %s", pid, brandId, eBrand));
			return false;
		}
		return true;
	}

}