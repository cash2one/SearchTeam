package com.dangdang;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.dangdang.client.DBAction;
import com.dangdang.data.FuncQuery;
import com.dangdang.data.FuncVP;
import com.dangdang.util.Calculator;
import com.dangdang.util.Utils;
import com.dangdang.verifier.blacklist.NoPictureProductVerifier;
import com.dangdang.verifier.blacklist.TopProductBlackListVerifier;

public class TopAndNoPictureProductScheduler {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(TopAndNoPictureProductScheduler.class);
	@BeforeMethod 
	public void setup() {  		
	}  

	@AfterMethod 
	public void clearup() { 
	}   
	
	//单品置顶黑名单->modules/search_ranking/force_query_pid_direction.txt
	@Test(enabled=true, groups="p2")  
	public void topProductBlackList() { 
			long d = System.currentTimeMillis() ;
			logger.info(Long.toString(d));			
			DBAction dba = new DBAction();
			dba.setFvpCondition("fvp_id=52");
			List<FuncVP> fvps = dba.getFVP();
			
			dba.setFuncCondition("verify_point = 'top_product_blacklist'");
			List<FuncQuery> list = dba.getFuncQuery();
			int pass=0,fail=0;
			String subject = "【搜索后台自动化测试】基础功能回归测试结果";
			StringBuffer content = new StringBuffer();
			content.append("<html><head><meta http-equiv=Content-Type content='text/html; charset=utf-8'></head><body><table border=1 cellspacing=0 cellpadding=0><tr><th>功能模块</th><th>通过query</th><th>失败query</th><th>跳过query</th><th>总计</th></tr>");
			
			String warnSubject = "【搜索后台自动化测试】预警！脚本通过率低";
			StringBuffer warnContent = new StringBuffer();
			warnContent.append("<html><head><meta http-equiv=Content-Type content='text/html; charset=utf-8'></head><body><table border=1 cellspacing=0 cellpadding=0><tr><th>功能模块</th><th>通过query</th><th>失败query</th><th>跳过query</th><th>总计</th><th>跳过率</th><th>实际通过率</th><th>预期通过率</th></tr>");
			
			for(FuncQuery query : list){
				try{
				if(TopProductBlackListVerifier.doVerify(query)){
					logger.info(" - [PASSED] - "+"top product blacklist for:"+query.getFquery());
					pass++;
				}else{
					logger.info(" - [FAILED] - "+"top product blacklist for:"+query.getFquery());
					fail++;
				}
				}catch(Exception e){
					 ByteArrayOutputStream buf = new ByteArrayOutputStream();
					 e.printStackTrace(new PrintWriter(buf, true));
					 String expMessage = buf.toString();
					 logger.error(" - [LOG_EXCEPTION] - "+expMessage);
					logger.error(" - [FAILED] - "+"top product blacklist for:"+query.getFquery());
					e.printStackTrace();
					fail++;
				}
			}
			content.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", 
					fvps.get(0).getFvpname(), pass, fail, 0, pass+fail));
			logger.info("Result  ####    Passed Count:"+pass+" Failed Count:"+fail);
			double pr = Calculator.passrate(pass, pass+fail);
			double fr = 100.00 - pr;	
			logger.info("Result  ####    Passed Rate:"+ pr +"% Failed Rate:"+ fr +"%");
			
			content.append("</table></body></html>");
			Utils.sendMail(subject, content.toString(), "HTML");
			long d2 = System.currentTimeMillis();
			logger.info(Double.toString((d2 - d) / 1000.0));	
			
			// 如果通过率比预期的低，发送邮件
			if(pr < 100){
				warnContent.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", 
						fvps.get(0).getFvpname(), pass, fail, 0, pass+fail, "0.00%", pr + "%", "100.00%"));
				warnContent.append("</table></body></html>");
				Utils.sendWarningMail(warnSubject, warnContent.toString(), "HTML");
			}			
	} 
	
	//无图片商品策略->无图片商品在全部商品列表中的位置除以总商品，如果大于0.5则认为是正确的。
	@Test(enabled=true, groups="p2")  
	public void noPictureProduct() { 
			long d = System.currentTimeMillis() ;
			logger.info(Long.toString(d));			
			DBAction dba = new DBAction();
			dba.setFvpCondition("fvp_id=53");
			List<FuncVP> fvps = dba.getFVP();
			
			dba.setFuncCondition("id < 100");
			//dba.setFuncCondition("id=65");
			List<FuncQuery> list = dba.getFuncQuery();
			int pass=0,fail=0;
			String subject = "【搜索后台自动化测试】基础功能回归测试结果";
			StringBuffer content = new StringBuffer();
			content.append("<html><head><meta http-equiv=Content-Type content='text/html; charset=utf-8'></head><body><table border=1 cellspacing=0 cellpadding=0><tr><th>功能模块</th><th>通过query</th><th>失败query</th><th>跳过query</th><th>总计</th></tr>");
			
			String warnSubject = "【搜索后台自动化测试】预警！脚本通过率低";
			StringBuffer warnContent = new StringBuffer();
			warnContent.append("<html><head><meta http-equiv=Content-Type content='text/html; charset=utf-8'></head><body><table border=1 cellspacing=0 cellpadding=0><tr><th>功能模块</th><th>通过query</th><th>失败query</th><th>跳过query</th><th>总计</th><th>实际通过率</th><th>预期通过率</th></tr>");
			boolean doSendWarnMail = false;
			
			for(FuncQuery query : list){
				try{
				if(NoPictureProductVerifier.doVerify(query)){
					logger.info(" - [PASSED] - "+"no picture product for:"+query.getFquery());
					pass++;
				}else{
					logger.info(" - [FAILED] - "+"no picture product for:"+query.getFquery());
					fail++;
				}
				}catch(Exception e){
					 ByteArrayOutputStream buf = new ByteArrayOutputStream();
					 e.printStackTrace(new PrintWriter(buf, true));
					 String expMessage = buf.toString();
					 logger.error(" - [LOG_EXCEPTION] - "+expMessage);
					logger.error(" - [FAILED] - "+"no picture product for:"+query.getFquery());
					e.printStackTrace();
					fail++;
				}
			}
			content.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", 
					fvps.get(0).getFvpname(), pass, fail, 0, pass+fail+0));
			logger.info("Result  ####    Passed Count:"+pass+" Failed Count:"+fail);
			double pr = Calculator.passrate(pass, pass+fail);
			double fr = 100.00 - pr;	
			logger.info("Result  ####    Passed Rate:"+ pr +"% Failed Rate:"+ fr +"%");
			
			content.append("</table></body></html>");
			Utils.sendMail(subject, content.toString(), "HTML");
			long d2 = System.currentTimeMillis();
			logger.info(Double.toString((d2 - d) / 1000.0));			
			
			double expectedPassrate = fvps.get(0).getMinPassrate();
			// 如果通过率比预期的低，发送邮件
			if(pr < expectedPassrate){
				warnContent.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", 
						fvps.get(0).getFvpname(), pass, fail, 0, pass+fail+0, pr + "%", expectedPassrate + "%"));
				doSendWarnMail = true;
			}		
			if (doSendWarnMail){
				warnContent.append("</table></body></html>");
				Utils.sendWarningMail(warnSubject, warnContent.toString(), "HTML");
			}
	}
}