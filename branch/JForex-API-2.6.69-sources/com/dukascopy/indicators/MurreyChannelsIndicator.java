package com.dukascopy.indicators;

import com.dukascopy.api.Filter;
import com.dukascopy.api.IBar;
import com.dukascopy.api.Period;
import com.dukascopy.api.indicators.IDrawingIndicator;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorDrawingSupport;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerListDescription;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.List;
import java.util.Map;

/**
 * Created by: S.Vishnyakov
 * Date: Feb 23, 2010
 * Time: 10:44:05 AM
 */
public class MurreyChannelsIndicator implements IIndicator, IDrawingIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private IBar[][] inputs = new IBar[1][];
    private double[][] outputs = new double[13][];
    private int p = 90;
    private int stepBack = 0;
    InputParameterInfo dailyInput;
    private GeneralPath generalPath = new GeneralPath();

    public void onStart(IIndicatorContext context) {
        indicatorInfo =
                new IndicatorInfo("MURRCH", "Murrey Channels", "Momentum Indicators", true, false, false, 1, 3, 13);
        indicatorInfo.setSparseIndicator(true);
        dailyInput = new InputParameterInfo("Input data", InputParameterInfo.Type.BAR);
        dailyInput.setPeriod(Period.DAILY_SUNDAY_IN_MONDAY);
        dailyInput.setFilter(Filter.WEEKENDS);
        inputParameterInfos = new InputParameterInfo[]{dailyInput};
        int[] periodValues = new int[10];
        String[] periodNames = new String[10];
        periodValues[0] = 0;
        periodNames[0] = Period.ONE_MIN.name();
        periodValues[1] = 1;
        periodNames[1] = Period.FIVE_MINS.name();
        periodValues[2] = 2;
        periodNames[2] = Period.TEN_MINS.name();
        periodValues[3] = 3;
        periodNames[3] = Period.FIFTEEN_MINS.name();
        periodValues[4] = 4;
        periodNames[4] = Period.THIRTY_MINS.name();
        periodValues[5] = 5;
        periodNames[5] = Period.ONE_HOUR.name();
        periodValues[6] = 6;
        periodNames[6] = Period.FOUR_HOURS.name();
        periodValues[7] = 7;
        periodNames[7] = Period.DAILY.name();
        periodValues[8] = 8;
        periodNames[8] = Period.WEEKLY.name();
        periodValues[9] = 9;
        periodNames[9] = Period.MONTHLY.name();

        optInputParameterInfos =
                new OptInputParameterInfo[]{new OptInputParameterInfo("N Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(p, 2, 400, 1)), new OptInputParameterInfo("Candle Period", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(7, periodValues, periodNames)), new OptInputParameterInfo("Step Back", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(stepBack, 0, 100, 1))};
        outputParameterInfos =
                new OutputParameterInfo[]{new OutputParameterInfo("[-2/8P]", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE) {
                    {
                        setColor(Color.BLACK);
                        setDrawnByIndicator(true);
                    }
                }, new OutputParameterInfo("[-1/8P]", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE) {
                    {
                        setColor(Color.BLACK);
                        setDrawnByIndicator(true);
                    }
                }, new OutputParameterInfo("[0/8P]", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE) {
                    {
                        setColor(Color.CYAN);
                        setDrawnByIndicator(true);
                    }
                }, new OutputParameterInfo("[1/8P]", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE) {
                    {
                        setColor(Color.YELLOW);
                        setDrawnByIndicator(true);
                    }
                }, new OutputParameterInfo("[2/8P]", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE) {
                    {
                        setColor(Color.RED);
                        setDrawnByIndicator(true);
                    }
                }, new OutputParameterInfo("[3/8P]", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE) {
                    {
                        setColor(Color.GREEN);
                        setDrawnByIndicator(true);
                    }
                }, new OutputParameterInfo("[4/8P]", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE) {
                    {
                        setColor(Color.BLUE);
                        setDrawnByIndicator(true);
                    }
                }, new OutputParameterInfo("[5/8P]", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE) {
                    {
                        setColor(Color.GREEN);
                        setDrawnByIndicator(true);
                    }
                }, new OutputParameterInfo("[6/8P]", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE) {
                    {
                        setColor(Color.RED);
                        setDrawnByIndicator(true);
                    }
                }, new OutputParameterInfo("[7/8P]", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE) {
                    {
                        setColor(Color.YELLOW);
                        setDrawnByIndicator(true);
                    }
                }, new OutputParameterInfo("[8/8P]", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE) {
                    {
                        setColor(Color.CYAN);
                        setDrawnByIndicator(true);
                    }
                }, new OutputParameterInfo("[+1/8P]", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE) {
                    {
                        setColor(Color.BLACK);
                        setDrawnByIndicator(true);
                    }
                }, new OutputParameterInfo("[+2/8P]", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE) {
                    {
                        setColor(Color.BLACK);
                        setDrawnByIndicator(true);
                    }
                }};
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        //calculating startIndex taking into account lookback value
        if (startIndex - getLookback() < 0) {
            startIndex -= startIndex - getLookback();
        }

        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }


        double fractal = 0;
        double v2 = 0;
        double v1 = 0;

        int i, j;

        double step = stepBack;
        if (stepBack >= p) {
            step = p - 2;
        }
        for (j = 0; j < (inputs[0].length - 1 - step); j++) {
            //Inputs: 0 open, 1 close, 2 high, 3 low, 4 volume
            if ((inputs[0].length) > 1) {
                IBar bar = inputs[0][j];
                if (j == 0) {
                    v1 = bar.getLow();
                    v2 = bar.getHigh();
                } else if (v1 > bar.getLow()) {
                    v1 = bar.getLow();
                } else if (v2 < bar.getHigh()) {
                    v2 = bar.getHigh();
                }
            }
        }

        //determine fractal.....
        if (v2 <= 250000 && v2 > 25000) fractal = 100000;
        else if (v2 <= 25000 && v2 > 2500) fractal = 10000;
        else if (v2 <= 2500 && v2 > 250) fractal = 1000;
        else if (v2 <= 250 && v2 > 25) fractal = 100;
        else if (v2 <= 25 && v2 > 12.5) fractal = 12.5;
        else if (v2 <= 12.5 && v2 > 6.25) fractal = 12.5;
        else if (v2 <= 6.25 && v2 > 3.125) fractal = 6.25;
        else if (v2 <= 3.125 && v2 > 1.5625) fractal = 3.125;
        else if (v2 <= 1.5625 && v2 > 0.390625) fractal = 1.5625;
        else if (v2 <= 0.390625 && v2 > 0) fractal = 0.1953125;

        double range = v2 - v1;
        double sum = Math.floor(Math.log(fractal / range) / Math.log(2));
        double octave = fractal * Math.pow(0.5, sum);
        double mn = Math.floor(v1 / octave) * octave;
        double mx;
        if ((mn + octave) > v2) {
            mx = mn + octave;
        } else {
            mx = mn + 2 * octave;
        }

        //System.out.println(" mx " + mx + " " + mn);
        double x1 = 0,
                x2 = 0,
                x3 = 0,
                x4 = 0,
                x5 = 0,
                x6 = 0,
                y1 = 0,
                y2 = 0,
                y3 = 0,
                y4 = 0,
                y5 = 0,
                y6 = 0,
                finalH = 0,
                finalL = 0;

// calculating xx
//x2
        if ((v1 >= (3 * (mx - mn) / 16 + mn)) && (v2 <= (9 * (mx - mn) / 16 + mn))) x2 = mn + (mx - mn) / 2;
        else x2 = 0;
//x1
        if ((v1 >= (mn - (mx - mn) / 8)) && (v2 <= (5 * (mx - mn) / 8 + mn)) && (x2 == 0)) x1 = mn + (mx - mn) / 2;
        else x1 = 0;

//x4
        if ((v1 >= (mn + 7 * (mx - mn) / 16)) && (v2 <= (13 * (mx - mn) / 16 + mn))) x4 = mn + 3 * (mx - mn) / 4;
        else x4 = 0;

//x5
        if ((v1 >= (mn + 3 * (mx - mn) / 8)) && (v2 <= (9 * (mx - mn) / 8 + mn)) && (x4 == 0)) x5 = mx;
        else x5 = 0;

//x3
        if ((v1 >= (mn + (mx - mn) / 8)) && (v2 <= (7 * (mx - mn) / 8 + mn)) && (x1 == 0) && (x2 == 0) && (x4 == 0) && (x5 == 0))
            x3 = mn + 3 * (mx - mn) / 4;
        else x3 = 0;

//x6
        if ((x1 + x2 + x3 + x4 + x5) == 0) x6 = mx;
        else x6 = 0;

        finalH = x1 + x2 + x3 + x4 + x5 + x6;
// calculating yy
//y1
        if (x1 > 0) y1 = mn;
        else y1 = 0;

//y2
        if (x2 > 0) y2 = mn + (mx - mn) / 4;
        else y2 = 0;

//y3
        if (x3 > 0) y3 = mn + (mx - mn) / 4;
        else y3 = 0;

//y4
        if (x4 > 0) y4 = mn + (mx - mn) / 2;
        else y4 = 0;

//y5
        if (x5 > 0) y5 = mn + (mx - mn) / 2;
        else y5 = 0;

//y6
        if ((finalH > 0) && ((y1 + y2 + y3 + y4 + y5) == 0)) y6 = mn;
        else y6 = 0;

        finalL = y1 + y2 + y3 + y4 + y5 + y6;

        double dmml = (finalH - finalL) / 8;

        for (i = startIndex, j = 0; i <= endIndex; i++, j++) {
            outputs[0][j] = (finalL - dmml * 2);
            outputs[1][j] = outputs[0][j] + dmml;
            outputs[2][j] = outputs[1][j] + dmml;
            outputs[3][j] = outputs[2][j] + dmml;
            outputs[4][j] = outputs[3][j] + dmml;
            outputs[5][j] = outputs[4][j] + dmml;
            outputs[6][j] = outputs[5][j] + dmml;
            outputs[7][j] = outputs[6][j] + dmml;
            outputs[8][j] = outputs[7][j] + dmml;
            outputs[9][j] = outputs[8][j] + dmml;
            outputs[10][j] = outputs[9][j] + dmml;
            outputs[11][j] = outputs[10][j] + dmml;
            outputs[12][j] = outputs[11][j] + dmml;
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
        return (p);
    }

    public OutputParameterInfo getOutputParameterInfo(int index) {
        if (index <= outputParameterInfos.length) {
            return outputParameterInfos[index];
        }
        return null;
    }

    public void setInputParameter(int index, Object array) {
        inputs[index] = (IBar[]) array;
    }

    public OptInputParameterInfo getOptInputParameterInfo(int index) {
        if (index <= optInputParameterInfos.length) {
            return optInputParameterInfos[index];
        }
        return null;
    }

    public void setOptInputParameter(int index, Object value) {
//        System.out.println(" index  " + index);
        switch (index) {
            case 0:
                p = (Integer) value;
                break;
            case 1:
                int period = (Integer) value;
                switch (period) {
                    case 0:
                        dailyInput.setPeriod(Period.ONE_MIN);
                        break;
                    case 1:
                        dailyInput.setPeriod(Period.FIVE_MINS);
                        break;
                    case 2:
                        dailyInput.setPeriod(Period.TEN_MINS);
                        break;
                    case 3:
                        dailyInput.setPeriod(Period.FIFTEEN_MINS);
                        break;
                    case 4:
                        dailyInput.setPeriod(Period.THIRTY_MINS);
                        break;
                    case 5:
                        dailyInput.setPeriod(Period.ONE_HOUR);
                        break;
                    case 6:
                        dailyInput.setPeriod(Period.FOUR_HOURS);
                        break;
                    case 7:
                        dailyInput.setPeriod(Period.DAILY_SUNDAY_IN_MONDAY);
                        break;
                    case 8:
                        dailyInput.setPeriod(Period.WEEKLY);
                        break;
                    case 9:
                        dailyInput.setPeriod(Period.MONTHLY);
                        break;
                    default:
                        dailyInput.setPeriod(Period.DAILY_SUNDAY_IN_MONDAY);
                }
            case 2:
                stepBack = (Integer) value;
                break;
            default:
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookforward() {
        return 0;
    }
    
    @Override
    public Point drawOutput(
    		Graphics g, 
    		int outputIdx, 
    		Object values,
            Color color, 
            Stroke stroke,
            IIndicatorDrawingSupport indicatorDrawingSupport,
            List<Shape> shapes, 
            Map<Color, List<Point>> handles) {
            	    
    	if (values != null) {    	
    		generalPath.reset();
    		Graphics2D g2 = (Graphics2D) g;
            g2.setColor(color);
            g2.setStroke(stroke);             
            g2.setFont(new Font("Dialog Input", Font.PLAIN, 9));
            FontMetrics fontMetrics = g2.getFontMetrics();
            double[] output = (double[]) values;    	                                                    
                		
            int x1 = (int)indicatorDrawingSupport.getMiddleOfCandle(indicatorDrawingSupport.getIndexOfFirstCandleOnScreen()) - 
            	(int) (indicatorDrawingSupport.getCandleWidthInPixels() / 2);
            int y = (int)indicatorDrawingSupport.getYForValue(output[output.length - 1]);
            int x2 = indicatorDrawingSupport.getChartWidth();

            double roundedPrice = 
            	Math.round(output[output.length - 1] * 
            	Math.pow(10, indicatorDrawingSupport.getInstrument().getPipScale() + 1)) / 
            	Math.pow(10, indicatorDrawingSupport.getInstrument().getPipScale() + 1);                                                                                          

            String label = outputParameterInfos[outputIdx].getName() + ": " + Double.toString(roundedPrice);
            int width = fontMetrics.stringWidth(label);
            int height = fontMetrics.getHeight();                                                                                            
            Rectangle rect = new Rectangle(x2 - width - 20 - 2, y - fontMetrics.getHeight() / 2, width, height);
            
            generalPath.moveTo(x1, y);
            generalPath.lineTo(rect.x, y);           
            generalPath.moveTo(rect.x + rect.width, y);
            generalPath.lineTo(x2, y);
            g2.drawString(label, rect.x, rect.y + rect.height - 3);
            g2.draw(generalPath);            
            shapes.add((Shape)generalPath.clone());
        }                
        return null;
    }
}
