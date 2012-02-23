/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import com.dukascopy.api.indicators.IDrawingIndicator;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorDrawingSupport;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.Map;

/**
 * Created by: S.Vishnyakov
 * Date: Aug 26, 2009
 * Time: 2:55:39 PM
 */
public class IchimokuIndicator implements IIndicator, IDrawingIndicator {

    public static final int SENKOU_A = 0;
    public static final int SENKOU_B = 1;

    private IIndicator tenkanMaxIndicator;
    private IIndicator tenkanMinIndicator;
    private IIndicator kijunMaxIndicator;
    private IIndicator kijunMinIndicator;
    private IIndicator senkouBMaxIndicator;
    private IIndicator senkouBMinIndicator;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][][] inputs = new double[1][][];
    private double[][] outputs = new double[5][];
    private Object[][] output = new Object[1][];
    private int tenkan = 9;
    private int kijun = 26;
    private int senkou = 52;
    private Color tenkanBrown = new Color(179, 136, 47);

    public void onStart(IIndicatorContext context) {
        tenkanMaxIndicator = context.getIndicatorsProvider().getIndicator("MAX");
        tenkanMinIndicator = context.getIndicatorsProvider().getIndicator("MIN");
        kijunMaxIndicator = context.getIndicatorsProvider().getIndicator("MAX");
        kijunMinIndicator = context.getIndicatorsProvider().getIndicator("MIN");
        senkouBMaxIndicator = context.getIndicatorsProvider().getIndicator("MAX");
        senkouBMinIndicator = context.getIndicatorsProvider().getIndicator("MIN");

        indicatorInfo = new IndicatorInfo("ICHIMOKU", "Ichimoku", "Overlap Studies", true, false, true, 1, 3, 6);
        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };
        optInputParameterInfos = new OptInputParameterInfo[] {
    		new OptInputParameterInfo("Tenkan", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(tenkan, 1, 400, 1)),
    		new OptInputParameterInfo("Kijun", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(kijun, 2, 400, 1)),
    		new OptInputParameterInfo("Senkou", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(senkou, 2, 400, 1))
		};
        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("Tenkan Sen", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {{
    			setColor(tenkanBrown);
			}},
    		new OutputParameterInfo("Ki-jun Sen", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {{
    			setColor(Color.BLUE);
			}},
    		new OutputParameterInfo("Chinkou Span", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {{
    			setShift(-kijun);
    			setColor(Color.MAGENTA);
			}},
			new OutputParameterInfo("Senkou A", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {{
		        setShift(kijun);
		        setColor(Color.RED);
			}},
			new OutputParameterInfo("Senkou B", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) {{
				setShift(kijun);
				setColor(Color.GREEN);
			}},
			new OutputParameterInfo("Cloud", OutputParameterInfo.Type.OBJECT, OutputParameterInfo.DrawingStyle.LINE) {{
				setShift(kijun);
				setDrawnByIndicator(true);
			}}
        };
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        //calculating startIndex taking into account lookback value
        if (startIndex - getLookback() < 0) {
            startIndex -= startIndex - getLookback();
        }

        //calculating endIndex taking into account lookforward value
        if (endIndex + getLookforward() >= inputs[0][0].length) {
            endIndex = inputs[0][0].length - 1 - getLookforward();
        }

        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] tenkanMax = new double[endIndex - startIndex + 2 + getLookback()];
        double[] tenkanMin = new double[endIndex - startIndex + 2 + getLookback()];
        double[] kijunMax = new double[endIndex - startIndex + 2 + getLookback()];
        double[] kijunMin = new double[endIndex - startIndex + 2 + getLookback()];
        double[] senkouBMax = new double[endIndex - startIndex + 2 + getLookback()];
        double[] senkouBMin = new double[endIndex - startIndex + 2 + getLookback()];

        // high value for max
        tenkanMaxIndicator.setInputParameter(0, inputs[0][2]);
        kijunMaxIndicator.setInputParameter(0, inputs[0][2]);
        senkouBMaxIndicator.setInputParameter(0, inputs[0][2]);
        // low value for min
        tenkanMinIndicator.setInputParameter(0, inputs[0][3]);
        kijunMinIndicator.setInputParameter(0, inputs[0][3]);
        senkouBMinIndicator.setInputParameter(0, inputs[0][3]);

        tenkanMaxIndicator.setOutputParameter(0, tenkanMax);
        tenkanMinIndicator.setOutputParameter(0, tenkanMin);
        kijunMaxIndicator.setOutputParameter(0, kijunMax);
        kijunMinIndicator.setOutputParameter(0, kijunMin);
        senkouBMaxIndicator.setOutputParameter(0, senkouBMax);
        senkouBMinIndicator.setOutputParameter(0, senkouBMin);

        IndicatorResult dtenkanMaxResult = tenkanMaxIndicator.calculate(startIndex - 1, endIndex);
        IndicatorResult dtenkanMinResult = tenkanMinIndicator.calculate(startIndex - 1, endIndex);
        IndicatorResult dkijunMaxResult = kijunMaxIndicator.calculate(startIndex - 1, endIndex);
        IndicatorResult dkijunMinResult = kijunMinIndicator.calculate(startIndex - 1, endIndex);
        IndicatorResult dsenkouBMaxResult = senkouBMaxIndicator.calculate(startIndex - 1, endIndex);
        IndicatorResult dsenkouBMinResult = senkouBMinIndicator.calculate(startIndex - 1, endIndex);

        int i, k;
        for (i = 1, k = dtenkanMaxResult.getNumberOfElements(); i < k; i++) {
            outputs[0][i - 1] = (tenkanMax[i] + tenkanMin[i]) / 2;
            outputs[1][i - 1] = (kijunMax[i] + kijunMin[i]) / 2;

            double[] cloud = new double[2];
            cloud[SENKOU_A] = (outputs[1][i - 1] + outputs[0][i - 1]) / 2;
            cloud[SENKOU_B] = (senkouBMax[i] + senkouBMin[i]) / 2;
            output[0][i - 1] = cloud;
            // senkou A
            outputs[3][i - 1] = (outputs[1][i - 1] + outputs[0][i - 1]) / 2;
            outputs[4][i - 1] = (senkouBMax[i] + senkouBMin[i]) / 2;
        }

        int resIndex = 0;
        // cinkou = close with shift by tenkan param
        for (int z = startIndex; z <= endIndex; z++, resIndex++) {
            //Inputs: 0 open, 1 close, 2 high, 3 low, 4 volume
            outputs[2][resIndex] = inputs[0][1][z-1];
        }
        return new IndicatorResult(startIndex, i - 1);
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
        return (Math.max(tenkan * 2, Math.max(kijun * 2, senkou * 2)) + 1);
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
                tenkan = (Integer) value;
                break;
            case 1:
                kijun = (Integer) value;
                break;
            case 2:
                senkou = (Integer) value;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
        outputParameterInfos[2].setShift(-kijun);
        outputParameterInfos[3].setShift(kijun);
        outputParameterInfos[4].setShift(kijun);
        outputParameterInfos[5].setShift(kijun);

        tenkanMaxIndicator.setOptInputParameter(0, tenkan);
        tenkanMinIndicator.setOptInputParameter(0, tenkan);
        kijunMaxIndicator.setOptInputParameter(0, kijun);
        kijunMinIndicator.setOptInputParameter(0, kijun);
        senkouBMaxIndicator.setOptInputParameter(0, senkou);
        senkouBMinIndicator.setOptInputParameter(0, senkou);
    }

    public void setOutputParameter(int index, Object array) {
        if (index < 5) {
            outputs[index] = (double[]) array;
        } else {
            output[0] = (Object[]) array;
        }
    }

    public int getLookforward() {
        return 0;
    }

    public Point drawOutput(Graphics g, int outputIdx, Object values2, Color color, Stroke stroke,
                           IIndicatorDrawingSupport indicatorDrawingSupport, java.util.List<Shape> shapes,
                           Map<Color, java.util.List<Point>> handles) {
        Object[] values = (Object[]) values2;
        if (values2 != null) {
            for (int j = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen() - kijun, k =
                    j + indicatorDrawingSupport.getNumberOfCandlesOnScreen() + kijun; j < k; j++) {
                if (j > 0) {
                    if (values[j] != null) {
                        double[] pointPrev = (double[]) values[j - 1];
                        double[] point = (double[]) values[j];
                        int cloudMiddle = (int) indicatorDrawingSupport.getMiddleOfCandle(j + kijun);
                        int cloudMiddlePrev = (int) indicatorDrawingSupport.getMiddleOfCandle(j - 1 + kijun);
                        if (point[SENKOU_A] > point[SENKOU_B]) {
                            Color color2 = new Color(0, 200, 0, 78);
                            g.setColor(color2);
                        } else {
                            Color color2 = new Color(200, 0, 0, 78);
                            g.setColor(color2);
                        }
                        int[] xPoints = new int[4];
                        int[] yPoints = new int[4];
                        if (pointPrev != null && point != null) {
                            yPoints[0] = (int) indicatorDrawingSupport.getYForValue(pointPrev[SENKOU_A]);
                            yPoints[1] = (int) indicatorDrawingSupport.getYForValue(point[SENKOU_A]);
                            yPoints[2] = (int) indicatorDrawingSupport.getYForValue(point[SENKOU_B]);
                            yPoints[3] = (int) indicatorDrawingSupport.getYForValue(pointPrev[SENKOU_B]);


                            xPoints[0] = cloudMiddlePrev;
                            xPoints[1] = cloudMiddle;
                            xPoints[2] = cloudMiddle;
                            xPoints[3] = cloudMiddlePrev;

                            g.fillPolygon(xPoints, yPoints, 4);
                        }
                    }
                }
            }
        }
        return null;
    }
}