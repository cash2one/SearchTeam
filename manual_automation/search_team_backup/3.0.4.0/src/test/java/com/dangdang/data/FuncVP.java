package com.dangdang.data;

public class FuncVP {

	private int fvp_id;
	private String fvp;
	private String fvpname;
	private double min_passrate;
	
	public int getFvp_id() {
		return fvp_id;
	}
	public void setFvp_id(int fvp_id) {
		this.fvp_id = fvp_id;
	}
	
	public String getFvp() {
		return fvp;
	}
	public void setFvp(String fvp) {
		this.fvp = fvp;
	}
	
	/**
	 * @return the fvpname
	 */
	public String getFvpname() {
		return fvpname;
	}
	/**
	 * @param fvpname the fvpname to set
	 */
	public void setFvpname(String fvpname) {
		this.fvpname = fvpname;
	}
	
	/**
	 * @param return minPassrate
	 */
	public double getMinPassrate() {
		return min_passrate;
	}
	
	/**
	 * @param set min_passrate
	 */
	public void setMinPassrate(double min_passrate) {
		this.min_passrate = min_passrate;
	}
	
	
}