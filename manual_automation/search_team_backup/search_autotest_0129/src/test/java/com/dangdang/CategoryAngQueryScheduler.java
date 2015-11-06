package com.dangdang;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.dangdang.client.DBAction;
import com.dangdang.client.URLBuilder;
import com.dangdang.data.FuncCatePath;
import com.dangdang.data.FuncVP;
import com.dangdang.util.ProdIterator;
import com.dangdang.util.Utils;
import com.dangdang.util.XMLParser;

/**
 * @author liuzhipengjs@dangdang.com
 * @version 创建时间：2014年11月19日 下午2:30:44 类说明:分类名搜索词
 */
public class CategoryAngQueryScheduler {

	private static Logger logger = Logger.getLogger(CategoryAngQueryScheduler.class);
	{
		PropertyConfigurator.configure("conf/cateandquery_log4j.properties");
	}
	
	private static int scheme = 2 ;//采用方案2

	@BeforeMethod
	public void setup() {
	}

	@AfterMethod
	public void clearup() {
	}

	@Test(enabled = false, groups="p2")//目前测试环境没有该功能代码
	public void category_and_query() {
		long d = System.currentTimeMillis();
		DBAction dba = new DBAction();
		dba.setFuncCondition("module = 'q_and_c'");
		dba.setFvpCondition("fvp_id = 29");
		List<FuncCatePath> categorys = dba.getFuncCatePath();
		List<FuncVP> fvps = dba.getFVP();
		String subject = "【搜索后台自动化测试】基础功能回归测试结果";
		StringBuffer content = new StringBuffer();
		content.append("<html><head><meta http-equiv=Content-Type content='text/html; charset=utf-8'></head><body><table border=1 cellspacing=0 cellpadding=0><tr><th>功能模块</th><th>通过category</th><th>失败category</th><th>跳过category</th><th>总计</th><th>耗时</th></tr>");
		for (FuncVP fvp : fvps) {
			int passed = 0, failed = 0, skiped = 0;
			for (FuncCatePath category : categorys) {
				int rt = doQuery(category, fvp);
				switch (rt) {
				case 0:
					passed += 1;
					break;
				case -1:
					failed += 1;
					break;
				case -2:
					skiped += 1;
					break;

				default:
					failed += 1;
					break;
				}
			}
			logger.fatal(String.format(" - [LOG_SUMMARY] - vp: %s, passed: %s, failed: %s, skiped: %s", fvp.getFvpname(), passed, failed, skiped));
			long d2 = System.currentTimeMillis();
			int d4 = (int) ((d2 - d) / 60000 + 1);
			logger.fatal("总耗时:" + d4 + "分钟");
			String d3 = String.valueOf(d4) + "分钟";
			content.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", fvp.getFvpname(), passed, failed, skiped,
					passed + failed + skiped, d3));
		}
		content.append("</table></body></html>");
		Utils.sendMail(subject, content.toString(), "HTML");

	}

	private int doQuery(FuncCatePath category, FuncVP fvp) {
		
		logger.fatal(String.format(" - [LOG_SUMMARY] - categoryname: %s, query: %s", category.getFcate_name()+category.getCat_path(), category.getQuery()));
		Map<String, String> infop = URLBuilder.l_getPreSearchInfo(category.getCat_path());  //获取默认的查询内容	
		infop.put("catename", category.getFcate_name());
		infop.put("cat_paths", category.getCat_path());
		infop.put("cat_query", category.getQuery());
		logger.fatal(" - [LOG_SUMMARY] - preSearchInfo: " + infop.toString());
		
		Map<String, String> urlp = URLBuilder.converURLPars(fvp.getFvp(), category.getQuery(), infop);
		ProdIterator s_iterator = new ProdIterator(urlp);
		
		Map<String, String> l_urlp = URLBuilder.l_converURLPars(fvp.getFvp(), category.getCat_path(), infop);
		ProdIterator l_iterator = new ProdIterator(l_urlp);	
		
		
		if (l_iterator.getTotalCount()<1||s_iterator.getTotalCount()<1) {
			logger.fatal(String.format(" - [LOG_SKIP] - no result:categoryname: %s, query: %s", category.getFcate_name()+category.getCat_path(), category.getQuery()));
			return -2;
		}
		
		if(doVerify(l_iterator, s_iterator, fvp, infop)){
			logger.fatal(" - [LOG_SUCCESS] - verify function 【"+fvp.getFvpname()+"】 for category: "+category.getFcate_name()+category.getCat_path()+"query:"+category.getQuery());
			return 0;
		}else{
			logger.error(" - [LOG_FAILED] - verify function 【"+fvp.getFvpname()+"】 for category: "+category.getFcate_name()+category.getCat_path()+"query:"+category.getQuery());
			return -1;
		}
		
	}

	private boolean doVerify(ProdIterator l_iterator, ProdIterator s_iterator, FuncVP fvp, Map<String, String> infop) {
		
		if (scheme == 2) {
			for (int i = 0; i < 1200; i++) {
				if (l_iterator.hasNext()&s_iterator.hasNext()) {
					
					String l_product_id = XMLParser.product_id(l_iterator.next());
					String s_product_id = XMLParser.product_id(s_iterator.next());
					if (!l_product_id.equals(s_product_id)) {
						logger.error(" - [LOG_FAILED] - verify function 【"+fvp.getFvpname()+"】 for l_product_id: "+l_product_id+"s_product_id:"+s_product_id);
						return false;
					}
				}else if (!l_iterator.hasNext()&!s_iterator.hasNext()) {
					break;
				}else {
					logger.error(" - [LOG_FAILED] - verify function 【"+fvp.getFvpname()+"】 : totalCount is not fit");
					break;
				}
			}
		//方案3	
		}else if (scheme == 3) {
		
			
			
			
			
		} 
		
		return true;
	}
	

}