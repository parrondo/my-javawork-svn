package chanlun;

import com.dukascopy.api.Filter;
import com.dukascopy.api.IContext;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;

public class FiveMinsMA0510 extends MAInfo {

	public FiveMinsMA0510(IContext context, MAType maType, int fastPeroid,
			int slowPeroid, Instrument instrument, Period period,
			OfferSide side, Filter filter, int numberOfCandlesBefore,
			long time, int numberOfCandlesAfter) throws JFException {
		super(context, maType, fastPeroid, slowPeroid, instrument, period, side,
				filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
		// TODO Auto-generated constructor stub
	}

	
	
}
