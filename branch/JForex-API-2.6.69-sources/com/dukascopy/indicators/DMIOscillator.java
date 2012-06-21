package com.dukascopy.indicators;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

import java.awt.Color;

/**
 * Created by: S.Vishnyakov
 */
public class DMIOscillator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][][] inputs = new double[1][][];
    private int period = 12;
    private double[][] outputs = new double[3][];
    private IIndicator adx;
    private IIndicator plusDI;
    private IIndicator minusDI;

    public void onStart(IIndicatorContext context) {
        adx = context.getIndicatorsProvider().getIndicator("ADX");
        adx.setOptInputParameter(0, period);
        plusDI = context.getIndicatorsProvider().getIndicator("PLUS_DI");
        plusDI.setOptInputParameter(0, period);
        minusDI = context.getIndicatorsProvider().getIndicator("MINUS_DI");
        minusDI.setOptInputParameter(0, period);

        indicatorInfo = new IndicatorInfo("DMI", "Average Directional Movement Index", "Momentum Indicators", false, false, true, 1, 1, 3);
        inputParameterInfos = new InputParameterInfo[]{
    		new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };
        optInputParameterInfos = new OptInputParameterInfo[]{new OptInputParameterInfo("Signal period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(14, 3, 100, 1))};
        outputParameterInfos = new OutputParameterInfo[]{
                new OutputParameterInfo("ADX", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {{
                    setColor(new Color(32, 178, 170));
                }},
                new OutputParameterInfo("+DI", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {{
                    setColor(new Color(154, 205, 50));
                }},
                new OutputParameterInfo("-DI", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {{
                    setColor(new Color(245, 222, 179));
                }}
        };
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        //calculating startIndex taking into account lookback value
        if (startIndex - getLookback() < 0) {
            startIndex -= startIndex - getLookback();
        }

        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }
        adx.setOptInputParameter(0, period);
        plusDI.setOptInputParameter(0, period);
        minusDI.setOptInputParameter(0, period);

        double[] adxA = new double[endIndex + 1 - adx.getLookback()];
        double[] plusDIA = new double[endIndex + 1 - plusDI.getLookback()];
        double[] minusDIA = new double[endIndex + 1 - minusDI.getLookback()];
        adx.setInputParameter(0, inputs[0]);
        adx.setOutputParameter(0, adxA);
        IndicatorResult dADXResult = adx.calculate(startIndex, endIndex);
        plusDI.setInputParameter(0, inputs[0]);
        plusDI.setOutputParameter(0, plusDIA);
        IndicatorResult dPlusDIResult = plusDI.calculate(startIndex, endIndex);
        minusDI.setInputParameter(0, inputs[0]);
        minusDI.setOutputParameter(0, minusDIA);
        IndicatorResult dMinusDiResult = minusDI.calculate(startIndex, endIndex);

        int i, k;
        for (i = 0, k = dADXResult.getNumberOfElements(); i < k; i++) {
            outputs[0][i] = adxA[i];
            outputs[1][i] = plusDIA[i];
            outputs[2][i] = minusDIA[i];
        }
        return new IndicatorResult(startIndex, i);
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
        return adx.getLookback();
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
        inputs[index] = (double[][]) array;
    }

    public void setOptInputParameter(int index, Object value) {
        if (index == 0) {
            period = (Integer) value;
            adx.setOptInputParameter(0, period);
            minusDI.setOptInputParameter(0, period);
            plusDI.setOptInputParameter(0, period);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }
}



