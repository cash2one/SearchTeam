package com.dangdang.verifier.filter;
/**   
 * @author liuzhipengjs@dangdang.com  
 * @version 创建时间：2014年10月29日 下午2:45:16  
 * 类说明:无线端手机专享价过滤筛选验证  
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.dom4j.Node;
import org.testng.annotations.Test;

import com.dangdang.data.Product;
import com.dangdang.util.ProdIterator;
import com.dangdang.util.XMLParser;
import com.dangdang.verifier.iVerifier.IFilterVerifier;
import com.dangdang.verifier.iVerifier.M_IFilterVerifier;

public class M_ExclusivePrice extends M_IFilterVerifier {

	@Override
	public boolean doVerify(ProdIterator iterator, Map<String, String> map, boolean hasResult) {
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(!iterator.hasNext()){
			iterator.reSet();
		}
		while(iterator.hasNext()){
			Node node = iterator.next();
			if(!isExclusive(node, hasResult, format, now)){
				return false;
			}
		}
		return true;
	}
	
	
	public boolean isExclusive(Node node,boolean hasResult,SimpleDateFormat format,Date now){

		Product product = new Product();
		product.setProduct_id(XMLParser.product_id(node));
		product.setExclusive_begin_date(XMLParser.product_exclusive_begin_date(node));
		product.setExclusive_end_date(XMLParser.product_exclusive_end_date(node));
		
		product.setExclusive_reduce_price(XMLParser.product_exclusive_reduced_price(node));//is_mphone=1时增加一个差价属性
		
		try {
			//有结果时所有结果有效，无结果时原始url的所有结果都无效
			if(hasResult && !isAvailable(product.getExclusive_begin_date(),product.getExclusive_end_date(),product.getExclusive_reduce_price(),format,now)){
				logger.error(" - [EXCLUSIVE] - "+"No exclusive price is setting on it. Product id="+product.getProduct_id());
				return false;
			}else if(!hasResult && isAvailable(product.getExclusive_begin_date(),product.getExclusive_end_date(),product.getExclusive_reduce_price(),format,now)){
				logger.error(" - [EXCLUSIVE-NORESULT] - "+"无结果时漏筛商品,实际有");
				return false;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			logger.error("- [EXCEPTION] - "+e.getMessage());
			return false;
		}
		return true;
	
	}
	/*
	 * true有效，false无效
	 */
	public boolean isAvailable(String startDate,String endDate,String eReducePrice,SimpleDateFormat format,Date now) throws ParseException{
		if (startDate.equals("")) {
			logger.debug("- [EXCLUSIVE-NORESULT] - "+"Exclusive should be not available!");	
			return false;
			}else {
				// 得到手机专享价的开始结束时间
				Date exclusive_start = format.parse(startDate);
				Date exclusive_end = format.parse(endDate);
				if (eReducePrice.equals("")) {
					return false;
				}
				int exclusive_reduce_price = Integer.parseInt(eReducePrice);
				if (exclusive_reduce_price<0) {
					logger.error("- [EXCLUSIVE] - "+"Exclusive_reduced_prive is negative!");
					return false;
				}
				// 判断设置的手机专享价是否还有效
				return exclusive_reduce_price>0 && !isOutDate(exclusive_start,exclusive_end);
			}
	}
	
	
	/*
	 * true过期，false不过期
	 */
	public boolean isOutDate(Date start,Date end){
		Date now = new Date();
		if (now.before(end) && now.after(start)) {
		logger.debug("- [EXCLUSIVE] - "+"Exclusive is available!");
		return false;
		} else {
			logger.debug(" - [EXCLUSIVE] - "+"start:"+start.toString()+"end:"+end.toString());
			logger.debug(" - [EXCLUSIVE] - "+"Exclusive is out date！");
			return true;
		}
	}
}
