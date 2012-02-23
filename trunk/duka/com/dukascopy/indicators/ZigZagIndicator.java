/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

import java.awt.Color;
import java.util.Arrays;

/**
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class. * 
 * 
 * Created by: S.Vishnyakov
 * Date: May 20, 2009
 * Time: 7:00:10 PM
 */
public class ZigZagIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][][] inputs = new double[1][][];
    private double[][] outputs = new double[1][];

    private int extDepth = 12;
    private int extDeviation = 5;
    private int extBackstep = 3;

    double instrPips = Instrument.EURUSD.getPipValue();

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("ZigZag", "ZigZag", "", true, false, true, 1, 3, 1) {{
        	setRecalculateAll(true);
        	setSparseIndicator(true);
        }};
        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
		};
        optInputParameterInfos = new OptInputParameterInfo[] {
    		new OptInputParameterInfo("ExtDepth", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(extDepth, 1, 200, 1)),
    		new OptInputParameterInfo("ExtDeviation", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(extDeviation, 5, 200, 1)),
    		new OptInputParameterInfo("ExtBackstep", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(extBackstep, 3, 200, 1))
		};
        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("ZigZag", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {{setColor(Color.RED);}}
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

        double[] lowMapBuffer = new double[endIndex + 1];
        double[] highMapBuffer = new double[endIndex + 1];
        double[] zigzagBuffer = new double[endIndex + 1];
        
        int whatlookfor = 0;
        int shift, back, lasthighpos = 0, lastlowpos = 0;
        double val, res;
        double curlow = 0, curhigh = 0, lasthigh = 0, lastlow = 0;
        int i;
        
        Arrays.fill(lowMapBuffer, 0);
        Arrays.fill(highMapBuffer, 0);
        Arrays.fill(zigzagBuffer, 0);               

        for (shift = startIndex; shift <= endIndex; shift++) {
            val = inputs[0][3][shift];
            for (int j = shift - 1; j > (shift - extDepth); j--) {
                val = Math.min(val, inputs[0][3][j]);
            }
            if (val == lastlow) val = 0.0;
            else {
                lastlow = val;
                if ((inputs[0][3][shift] - val) > (extDeviation * instrPips)) {
                    val = 0.0;
                } else {
                    for (back = 1; back <= extBackstep; back++) {
                        res = lowMapBuffer[shift - back];
                        if ((res != 0) && (res > val)) {
                            lowMapBuffer[shift - back] = 0.0;
                        }
                    }
                }
            }
            if (inputs[0][3][shift] == val) {
                lowMapBuffer[shift] = val;
            } else {
                lowMapBuffer[shift] = 0.0;
            }
            //--- high
            val = inputs[0][2][shift];
            for (int j = shift - 1; j > (shift - extDepth); j--) {
                val = Math.max(val, inputs[0][2][j]);
            }
            if (val == lasthigh) {
                val = 0.0;
            } else {
                lasthigh = val;
                if ((val - inputs[0][2][shift]) > (extDeviation * instrPips)) {
                    val = 0.0;
                } else {
                    for (back = 1; back <= extBackstep; back++) {
                        res = highMapBuffer[shift - back];
                        if ((res != 0) && (res < val)) highMapBuffer[shift - back] = 0.0;
                    }
                }
            }
            if (inputs[0][2][shift] == val) {
                highMapBuffer[shift] = val;
            } else {
                highMapBuffer[shift] = 0.0;
            }
        }

        // final cutting
        if (whatlookfor == 0) {
            lastlow = 0;
            lasthigh = 0;
        } else {
            lastlow = curlow;
            lasthigh = curhigh;
        }

        for (shift = startIndex; shift <= endIndex; shift++) {
            switch (whatlookfor) {
                case 0: // look for peak or lawn                  
                        if (highMapBuffer[shift] != 0) {
                            lasthigh = inputs[0][2][shift];
                            lasthighpos = shift;
                            whatlookfor = -1;
                            zigzagBuffer[shift] = lasthigh;
                        }
                        if (lowMapBuffer[shift] != 0) {
                            lastlow = inputs[0][3][shift];
                            lastlowpos = shift;
                            whatlookfor = 1;
                            zigzagBuffer[shift] = lastlow;
                        }
                    break;
                case 1: // look for peak
                    if (lowMapBuffer[shift] != 0.0 && lowMapBuffer[shift] < lastlow && highMapBuffer[shift] == 0.0) {
                        zigzagBuffer[lastlowpos] = 0.0;
                        lastlowpos = shift;
                        lastlow = lowMapBuffer[shift];
                        zigzagBuffer[shift] = lastlow;
                    }
                    if (highMapBuffer[shift] != 0.0 && lowMapBuffer[shift] == 0.0) {
                        lasthigh = highMapBuffer[shift];
                        lasthighpos = shift;
                        zigzagBuffer[shift] = lasthigh;
                        whatlookfor = -1;
                    }
                    break;
                case-1: // look for lawn
                    if (highMapBuffer[shift] != 0.0 && highMapBuffer[shift] > lasthigh && lowMapBuffer[shift] == 0.0) {
                        zigzagBuffer[lasthighpos] = 0.0;
                        lasthighpos = shift;
                        lasthigh = highMapBuffer[shift];
                        zigzagBuffer[shift] = lasthigh;
                    }
                    if (lowMapBuffer[shift] != 0.0 && highMapBuffer[shift] == 0.0) {
                        lastlow = lowMapBuffer[shift];
                        lastlowpos = shift;
                        zigzagBuffer[shift] = lastlow;
                        whatlookfor = 1;
                    }
                    break;
            }
        }

        int iz, j;
        for (iz = 0, j = startIndex; j <= endIndex; iz++, j++) {
            if (zigzagBuffer[j] != 0) {
                outputs[0][iz] = zigzagBuffer[j];
            } else {
                outputs[0][iz] = Double.NaN;
            }
        }
        return new IndicatorResult(startIndex, iz);
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
        return (Math.max(extBackstep, Math.max(extDepth, extDeviation)));
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

    public OptInputParameterInfo getOptInputParameterInfo(int index) {
        if (index <= optInputParameterInfos.length) {
            return optInputParameterInfos[index];
        }
        return null;
    }

    public void setOptInputParameter(int index, Object value) {
        switch (index) {
            case 0:
                extDepth = (Integer) value;
                break;
            case 1:
                extDeviation = (Integer) value;
                break;
            case 2:
                extBackstep = (Integer) value;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookforward() {
        return 0;
    }
}