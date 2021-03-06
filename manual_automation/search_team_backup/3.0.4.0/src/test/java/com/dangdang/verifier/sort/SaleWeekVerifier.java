package com.dangdang.verifier.sort;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import org.dom4j.Node;

import com.dangdang.data.Product;
import com.dangdang.util.ProdIterator;
import com.dangdang.util.XMLParser;
import com.dangdang.verifier.iVerifier.ISortVerifier;

public class SaleWeekVerifier implements ISortVerifier {

	@Override
	public boolean Verifier(ProdIterator iterator, Map<String, String> map) {
		// 上一个商品
				Product pre_Product = null;
				Product pre_Product_reco = null;
				try {
					//暂时不验总商品数量
//					int totalCount = iterator.getTotalCount();
//					int preTotalCount = Integer.valueOf(map.get("totalCount"));
//					if(!NumVerifier(totalCount, preTotalCount)){
//						return false;
//					}
					// 循环所有的商品节点。比较两个相邻的商品是否符合相应规则。（按销售量递减排序）
					while (iterator.hasNext()) {
						logger.debug("/****************************product**************************/");
						Node subNode = iterator.next();
						// 为商品赋值
						Product product = new Product();
						product.setProduct_id(XMLParser.product_id(subNode));
						product.setSale_week_amt(XMLParser.product_sale_week_amt(subNode));
						product.setSale_week(XMLParser.product_sale_week(subNode));
						product.setStype(XMLParser.product_stype(subNode));
						if(product.getStype().equals("")){
							// 判断当前商品是否为第一个
							if (pre_Product == null) {
								pre_Product = product;

							} else {
								// 比较商品的销售量
								if (Double.valueOf(pre_Product.getSale_week()) >= Double
										.valueOf(product.getSale_week())) {
									pre_Product = product;

								} else {
									logger.error(" - [SALEWEEK] - "+"pre_product_id:"
											+ pre_Product.getProduct_id() + ";"
											+ "sale_week:" + pre_Product.getSale_week());
									logger.error(" - [SALEWEEK] - "+"product_id:" + product.getProduct_id()
											+ ";" + "sale_week:" + product.getSale_week());
									return false;

								}
							}
						}else if(product.getStype().equals("reco")){//推荐品排序
							if (pre_Product_reco == null) {
								pre_Product_reco = product;

							} else {
								// 比较商品的销售量
								if (Double.valueOf(pre_Product_reco.getSale_week()) >= Double
										.valueOf(product.getSale_week())) {
									pre_Product_reco = product;

								} else {
									logger.error(" - [SALEWEEK] - "+"pre_product_id:"
											+ pre_Product_reco.getProduct_id() + ";"
											+ "sale_week:" + pre_Product_reco.getSale_week());
									logger.error(" - [SALEWEEK] - "+"product_id:" + product.getProduct_id()
											+ ";" + "sale_week:" + product.getSale_week());
									return false;

								}
							}
						}


						logger.debug("/****************************end**************************/");
					}
				} catch (Exception e) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();  
					e.printStackTrace(new PrintStream(baos));  
					String exception = baos.toString();  
					logger.error(" - [LOG_EXCEPTION] - "+exception);
				}

				return true;
	}

	@Override
	public boolean NumVerifier(int Count, int preCount) {
		//本次结果中商品数量比预查询中的结果数量大
		if(Count!=preCount){
			logger.error(" - [SALEWEEK] - "+"The total count different with the total count of the result before it was sorted by sale week!");
			logger.error(" - [SALEWEEK] - "+"Total Count:"+Count+" Pre Total Count:"+preCount);
			return false;
		}else{
			logger.debug("Correct!");
			return true;
		}
	}

	@Override
	public boolean doVerify(ProdIterator iterator, Map<String, String> map) {
		// TODO Auto-generated method stub
		return false;
	}

}
