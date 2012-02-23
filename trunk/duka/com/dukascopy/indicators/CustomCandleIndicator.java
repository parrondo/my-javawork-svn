package com.dukascopy.indicators;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class CustomCandleIndicator implements IIndicator, IDrawingIndicator{
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private IBar[][] inputs = new IBar[2][];
    private int[][] outputs = new int[1][];  
    private Period[] periods = new Period[7];

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("CUSTOMCANDLE", "Custom candle", "", true, false, false, 2, 1, 1);
        indicatorInfo.setRecalculateAll(true);        
        inputParameterInfos = new InputParameterInfo[]{
    		new InputParameterInfo("childInput", InputParameterInfo.Type.BAR){{
    			setPeriod(Period.ONE_MIN);
    		}},
    		new InputParameterInfo("mainInput", InputParameterInfo.Type.BAR)
		};
        outputParameterInfos = new OutputParameterInfo[]{
    		new OutputParameterInfo("CustomCandle", OutputParameterInfo.Type.INT, OutputParameterInfo.DrawingStyle.LINE) {{	            
	            setDrawnByIndicator(true);
    		}}
		};
        
        int[] periodValues = new int[7];
        String[] periodNames = new String[7];
        periodValues[0] = 0;
        periodNames[0] = "1 Min";
        periods[0] = Period.ONE_MIN;	        
        periodValues[1] = 1;
        periodNames[1] = "5 Mins";
        periods[1] = Period.FIVE_MINS;
        periodValues[2] = 2;
        periodNames[2] = "10 Mins";
        periods[2] = Period.TEN_MINS;
        periodValues[3] = 3;
        periodNames[3] = "15 Mins";
        periods[3] = Period.FIFTEEN_MINS;
        periodValues[4] = 4;
        periodNames[4] = "30 Mins";
        periods[4] = Period.THIRTY_MINS;
        periodValues[5] = 5;
        periodNames[5] = "Hourly";
        periods[5] = Period.ONE_HOUR;
        periodValues[6] = 6;
        periodNames[6] = "4 Hours";
        periods[6] = Period.FOUR_HOURS;
        
        optInputParameterInfos = new OptInputParameterInfo[] {
        		new OptInputParameterInfo("First period", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(0 , periodValues, periodNames))
        };
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
    	Arrays.fill(outputs[0], -1);
        for (int i = 0, j = 0; i < inputs[1].length; i++, j++){
        	int timeIndex = getTimeIndex(inputs[1][i].getTime(), inputs[0]); 
        	outputs[0][j] = timeIndex; 
        }
             
        return new IndicatorResult(startIndex, endIndex - startIndex + 1);
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
        return 0;
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
    	 inputParameterInfos[0].setPeriod(periods[(Integer) value]);   	
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (int[]) array;
    }

    public int getLookforward() {
        return 0;
    }
    
    private int getTimeIndex(long time, IBar[] target) {
    	if (target == null) {
    		return -1;
    	}
 
		int first = 0;
		int upto = target.length;
	 
		while (first < upto) {
		    int mid = (first + upto) / 2;
	 
		    IBar data = target[mid];
	 
		    if (data.getTime() == time) {
		     	return mid;
		    }
	            else if (time < data.getTime()) {
		        upto = mid;
		    } 
		    else if (time > data.getTime()) {
		        first = mid + 1;
		    } 
		}	  
		
    	return -1;
    }
    
    public Point drawOutput(
    		Graphics g, 
    		int outputIdx, 
    		Object values, 
    		Color color, 
    		Stroke stroke, 
    		IIndicatorDrawingSupport indicatorDrawingSupport, 
    		List<Shape> shapes, 
    		Map<Color, List<Point>> handles)
    {
        Color prevColor = g.getColor();
        Stroke prevStroke = ((Graphics2D) g).getStroke();
        
        ((Graphics2D) g).setStroke(stroke);
        
        handles.put(color, new ArrayList<Point>(indicatorDrawingSupport.getNumberOfCandlesOnScreen()));
        handles.put(indicatorDrawingSupport.getDowntrendColor(), new ArrayList<Point>(indicatorDrawingSupport.getNumberOfCandlesOnScreen()));
        
    	int[] outputValues = (int[]) values;
    	int prevIndex = -1;
    	int candleWidth = -1;
    	if (outputValues != null) {
            for (int i = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen(), j = 0; j < indicatorDrawingSupport.getNumberOfCandlesOnScreen(); i++, j++) {            	
            	if (outputValues[i] > 0) {
            		if (prevIndex == -1){
            			prevIndex = i;
            			continue;
            		}
            		
            		int candleMiddle = (int) indicatorDrawingSupport.getMiddleOfCandle(i);
            		int halfOfCandle = (int) indicatorDrawingSupport.getCandleWidthInPixels() / 2;
            		int candleEnd = candleMiddle - halfOfCandle - (int)indicatorDrawingSupport.getSpaceBetweenCandlesInPixels() - 1;
            		int candleStart = (int) indicatorDrawingSupport.getMiddleOfCandle(prevIndex) - halfOfCandle;
            			
            		if (outputValues[prevIndex] > inputs[0].length - 1){
            			continue;
            		}
            		IBar bar = inputs[0][outputValues[prevIndex]];
            		drawCandle(candleStart, candleEnd, bar, g, indicatorDrawingSupport, shapes, handles, color, indicatorDrawingSupport.getDowntrendColor());
            		candleWidth = candleEnd - candleStart;
            		prevIndex = i;
            	}            	            
            }
            //in-progress candle
            if (candleWidth > 0 && prevIndex > 0){
        		int halfOfCandle = (int) indicatorDrawingSupport.getCandleWidthInPixels() / 2;        		
        		int candleStart = (int) indicatorDrawingSupport.getMiddleOfCandle(prevIndex) - halfOfCandle;
        		int candleEnd = candleStart + candleWidth;
        		
        		if (outputValues[prevIndex] < inputs[0].length){        			        		
	            	IBar bar = inputs[0][outputValues[prevIndex]];
	            	drawCandle(candleStart, candleEnd, bar, g, indicatorDrawingSupport, shapes, handles, color, indicatorDrawingSupport.getDowntrendColor());
        		}
            }                        
        }
    	g.setColor(prevColor);
    	((Graphics2D) g).setStroke(prevStroke);
    	
    	return null;
    }
    
    private void drawCandle(
                            int candleStart,
                            int candleEnd,
                            IBar bar,
                            Graphics g,
                            IIndicatorDrawingSupport indicatorDrawingSupport,
                            List<Shape> shapes,
                            Map<Color, List<Point>> handles,
                            Color color,
                            Color color2
    ){
        GeneralPath path = new GeneralPath();
        
		//candle color
        Color candleColor = (bar.getClose() >= bar.getOpen())   ? color
                                                                : color2;
		g.setColor(candleColor);
		
		float yOpen = indicatorDrawingSupport.getYForValue(bar.getOpen());
		float yClose = indicatorDrawingSupport.getYForValue(bar.getClose());
		float yHigh = indicatorDrawingSupport.getYForValue(bar.getHigh());
		float yLow = indicatorDrawingSupport.getYForValue(bar.getLow());
		
		path.moveTo(candleStart, yOpen);
		path.lineTo(candleEnd, yOpen);
		path.lineTo(candleEnd, yClose);
		path.lineTo(candleStart, yClose);
		path.closePath();
	
		//high and low
		float yUpBorder   = (bar.getClose() > bar.getOpen())  ? yClose
				                                              : yOpen;
				
		float yDownBorder = (bar.getClose() > bar.getOpen())  ? yOpen
		                                                      : yClose; 
            				
		int middleOfCandle = (int)(candleEnd + (double)candleStart) / 2;
		
		path.moveTo(middleOfCandle, yUpBorder);
		path.lineTo(middleOfCandle, yHigh);
		
		path.moveTo(middleOfCandle, yDownBorder);
		path.lineTo(middleOfCandle, yLow);
		
		shapes.add(path);
		
		List<Point> points = handles.get(candleColor);
		points.add(new Point(middleOfCandle, (int) yOpen));
		points.add(new Point(middleOfCandle, (int) yClose));
		
		((Graphics2D) g).draw(path);
    }
}
