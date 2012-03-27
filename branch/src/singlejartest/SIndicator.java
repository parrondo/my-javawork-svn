package singlejartest;

import com.dukascopy.api.indicators.*;

public class SIndicator implements IIndicator {
	private IndicatorInfo indicatorInfo;
	private InputParameterInfo[] inputParameterInfos;
	private OptInputParameterInfo[] optInputParameterInfos;
	private OutputParameterInfo[] outputParameterInfos;
	// Indicator input used in calculations
	private double[][] inputs = new double[1][];
	// Default value of optional parameter
	private int timePeriod = 4;
	// Array of indicator output values
	private double[][] outputs = new double[1][];

	public void onStart(IIndicatorContext context) {
		// Indicator name is [EXAMPIND], indicator group is [My indicators],
		// indicator is displayed in a subwindow and doesn't have an unstable
		// period,
		// it has one input, one optional parameter and one output.
		indicatorInfo = new IndicatorInfo("EXAMPIND", "Sums previous values",
				"My indicators", false, false, false, 1, 1, 1);
		// Input of type double
		inputParameterInfos = new InputParameterInfo[] { new InputParameterInfo(
				"Input data", InputParameterInfo.Type.DOUBLE) };
		// Type: integer, default value: 4, minimum value: 2, maximum value:
		// 100, incremental step: 1.
		optInputParameterInfos = new OptInputParameterInfo[] { new OptInputParameterInfo(
				"Time period", OptInputParameterInfo.Type.OTHER,
				new IntegerRangeDescription(4, 2, 100, 1)) };
		// Output of type double, output is displayed as a line.
		outputParameterInfos = new OutputParameterInfo[] { new OutputParameterInfo(
				"out", OutputParameterInfo.Type.DOUBLE,
				OutputParameterInfo.DrawingStyle.LINE) };
	}

	public IndicatorResult calculate(int startIndex, int endIndex) {
		// calculating startIndex taking into an account the lookback value
		if (startIndex - getLookback() < 0) {
			startIndex -= startIndex - getLookback();
		}
		int i, j;
		for (i = startIndex, j = 0; i <= endIndex; i++, j++) {
			double value = 0;
			// sum values
			for (int k = timePeriod; k > 0; k--) {
				value += inputs[0][i - k];
			}
			outputs[0][j] = value;
		}
		return new IndicatorResult(startIndex, j);
	}

	public IndicatorInfo getIndicatorInfo() {
		return indicatorInfo;
	}

	public InputParameterInfo getInputParameterInfo(int index) {
		if (index <= inputParameterInfos.length) {
			return inputParameterInfos[index];
		}
		return null;
	}

	public int getLookback() {
		// calculate indicator lookBack
		return timePeriod;
	}

	public int getLookforward() {
		return 0;
	}

	public OptInputParameterInfo getOptInputParameterInfo(int index) {
		if (index <= optInputParameterInfos.length) {
			return optInputParameterInfos[index];
		}
		return null;
	}

	public OutputParameterInfo getOutputParameterInfo(int index) {
		if (index <= outputParameterInfos.length) {
			return outputParameterInfos[index];
		}
		return null;
	}

	public void setInputParameter(int index, Object array) {
		inputs[index] = (double[]) array;
	}

	public void setOptInputParameter(int index, Object value) {
		timePeriod = (Integer) value;
	}

	public void setOutputParameter(int index, Object array) {
		outputs[index] = (double[]) array;
	}
}
