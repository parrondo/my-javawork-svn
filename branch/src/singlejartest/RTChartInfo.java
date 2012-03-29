package singlejartest;

import com.dukascopy.api.*;

public class RTChartInfo {
	private IBar HighBar;
	private IBar LowBar;
	
	public IBar getHighBar(){
		return this.HighBar;
	}
	
	public void  setHighBar(IBar hBar){
		this.HighBar=hBar;
	}

}
