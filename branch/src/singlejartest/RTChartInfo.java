package singlejartest;

import com.dukascopy.api.*;

public class RTChartInfo {
	private IBar HighBar;
	private IBar LowBar;
	private TrendInfo trendInfo;
	public final int HLBARSHIFT=40; 
	
	public IBar getHighBar(){
		return this.HighBar;
	}
	
	public void  setHighBar(IBar hBar){
		this.HighBar=hBar;
	}

	protected void LookForHLBar(){
		
	}
}
