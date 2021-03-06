package com.dangdang.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.dangdang.data.SourceItem;
/*
 * 目前废弃不用
 */
public class FileUtil {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	private File file;
	private File failedfile;
	private File successfile;
	private File untestedfile;
	private File termFixedFile;
	private File fullFixedFile;
	/**
	 * 文件处理工具类；用来打开文件输入/输出流；初始化搜索项文件reader
	 */
	public FileUtil() {
		try {
			Config config = new Config();
			String filePath = config.get_itemFile();
			this.failedfile = new File(config.get_failedFile());
			if (!this.failedfile.exists()) {
				this.failedfile.createNewFile();
			}
			this.successfile = new File(config.get_successFile());
			if (!this.successfile.exists()) {

				this.successfile.createNewFile();
			}

			this.untestedfile = new File(config.get_untestFile());
			if (!this.untestedfile.exists()) {
				this.untestedfile.createNewFile();
			}

			this.termFixedFile = new File(config.get_termFixFile());
			if (!this.termFixedFile.exists()) {
				this.termFixedFile.createNewFile();
			}

			this.fullFixedFile = new File(config.get_fullFixFile());
			if (!this.fullFixedFile.exists()) {
				this.fullFixedFile.createNewFile();
			}
			this.file = new File(filePath);
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			e.printStackTrace(new PrintStream(baos));  
			String exception = baos.toString();  
			logger.error(" - [LOG_EXCEPTION] - "+exception);
		}
	}

	public List<SourceItem> initSourceItem() {
		try {
			List<SourceItem> list = new ArrayList<SourceItem>();
			FileInputStream in = new FileInputStream(file);
			BufferedReader bf = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = bf.readLine()) != null) {
				logger.debug(" - [FileUtil] - 读取内容：" + line);
				String[] terms = line.split("\t");
				SourceItem item = new SourceItem(terms[0], terms[1], terms[2]);
				list.add(item);
			}
			bf.close();
			in.close();
			return list;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			e.printStackTrace(new PrintStream(baos));  
			String exception = baos.toString();  
			logger.error(" - [LOG_EXCEPTION] - "+exception);
			e.printStackTrace();
			return null;
		}
	}

	public BufferedWriter failedOutput() {
		try {
			FileOutputStream out = new FileOutputStream(failedfile, true);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					out));
			return writer;
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			e.printStackTrace(new PrintStream(baos));  
			String exception = baos.toString();  
			logger.error(" - [LOG_EXCEPTION] - "+exception);
			return null;
		}
	}

	public BufferedWriter successOutput() {
		try {
			FileOutputStream out = new FileOutputStream(successfile, true);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					out));
			return writer;
		} catch (Exception e) {
			return null;
		}
	}

	public BufferedWriter untestOutput() {
		try {
			FileOutputStream out = new FileOutputStream(untestedfile, true);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					out));
			return writer;
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			e.printStackTrace(new PrintStream(baos));  
			String exception = baos.toString();  
			logger.error(" - [LOG_EXCEPTION] - "+exception);
			return null;
		}
	}

	public BufferedWriter termFixOnlyOutput() {
		try {
			FileOutputStream out = new FileOutputStream(termFixedFile, true);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					out));
			return writer;
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			e.printStackTrace(new PrintStream(baos));  
			String exception = baos.toString();  
			logger.error(" - [LOG_EXCEPTION] - "+exception);
			return null;
		}
	}

	public BufferedWriter fullfixedOutput() {
		try {
			FileOutputStream out = new FileOutputStream(fullFixedFile, true);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					out));
			return writer;
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			e.printStackTrace(new PrintStream(baos));  
			String exception = baos.toString();  
			logger.error(" - [LOG_EXCEPTION] - "+exception);
			return null;
		}
	}

}
