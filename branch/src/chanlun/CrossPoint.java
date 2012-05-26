package chanlun;

import com.dukascopy.api.IBar;
import java.util.*;

public class CrossPoint {

	public enum CrossType {
		UpCross, DownCross,
	}

	private double open;
	private double high;
	private double low;
	private double close;
	private long time;
	private CrossType crossType;
	private IBar crossBar;
	
	public CrossPoint(CrossType crossType) {
		super();
		this.crossType = crossType;
	}

	
	
	public IBar getCrossBar() {
		return crossBar;
	}

	public void setCrossBar(IBar crossBar) {
		this.crossBar = crossBar;
	}

	public double getCrossPrice() {
		return CrossPrice;
	}

	public void setCrossPrice(double crossPrice) {
		CrossPrice = crossPrice;
	}

	private double CrossPrice;

	
	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public CrossType getCrossType() {
		return crossType;
	}

	public void setCrossType(CrossType crossType) {
		this.crossType = crossType;
	}

}
