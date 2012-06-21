/**
 * The file AbstractPatternIndicator.java was created on Mar 1, 2010 at 2:37:32 PM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.dukascopy.api.indicators.BooleanOptInputDescription;
import com.dukascopy.api.indicators.DoubleRangeDescription;
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

/*
 * JFOREX-1394
 */
public abstract class AbstractPatternIndicator implements IIndicator, IDrawingIndicator {
	
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    
    private double[][][] inputs = new double[1][][]; // 0 - open, 1 - close, 2 - high, 3 - low, 4 - volume
    
    private Object[][] output = new Object[OUTPUT_COUNT_FOR_ONE_SUBINDICATOR][];
    
    private int barsOnSidesStartCount = 3;
    private int barsOnSidesIncrementCount = 50;
    
    private double patternQuality = 50d;
    
    protected static final double DIGRESSION_RATIO = 200d; // greater value for more precision
//    protected static final double PERCENT_MULTIPLYER = 0.001d;
    protected static final double EQ_DELTA = 0.15;
    protected static final double NORMAL_GAP_DEVIATION = 1.5;
    protected static final double HUNDRED_PERCENT = 100d;
    
    protected static final int MAX_IDX = 0;
    protected static final int MIN_IDX = 1;
    
    protected static final int OUTPUT_COUNT_FOR_ONE_SUBINDICATOR = 3;
    
    public static final String CLOSE_PRICE = "Close";
    public static final String HIGH_LOW_PRICES = "High/Low";
    
    private final String[] pivotPointPriceCaptions = new String[] {HIGH_LOW_PRICES, CLOSE_PRICE};
    private final int[] pivotPointPriceValues = new int[] {0, 1};
    
    private String pivotPointCalculationPrice;
    private boolean wholePatternCalculation = true;
    private double pointsEqualityQuality = HUNDRED_PERCENT;
    private double parallelLineQuality = HUNDRED_PERCENT;
    protected double calculatedPatternQuality = HUNDRED_PERCENT; 
    
	protected static final int BARS_FORWARD_UNTIL_CROSS = 100;

    
    

//	------------------------------------------------------ ABSTRACT METHODS -------------------------------------------------------
    
    
	protected abstract IndicatorInfo createIndicatorInfo();
	public abstract int getMinPatternPivotPointNumber();
	public abstract int getMaxPatternPivotPointNumber();
	public abstract boolean checkPattern(double[] cleanPattern, double[] pivotPointsData, int dirtyPatterStartIndex, int dirtyPatternSize);
	public abstract IndexValue[] constructUppperAsymptote(double[] cleanPattern, double[] pivotPointsData, int dirtyPatternStartIndex, int dirtyPatternSize);
	public abstract IndexValue[] constructBottomAsymptote(double[] cleanPattern, double[] pivotPointsData, int dirtyPatternStartIndex, int dirtyPatternSize);
	public abstract boolean checkEmergingPatternPostTrend(double[] cleanPattern, double[][] sourceData, int dirtyPatterStartIndex, int dirtyPatternSize, IndexValue[] patternPoints, IndexValue[] upperAsymptotePoints, IndexValue[] bottomAsymptotePoints);
	
//	------------------------------------------------------ INDICATOR METHODS -------------------------------------------------------

	
	@Override
	public void onStart(IIndicatorContext context) {
        inputParameterInfos = createInputParameterInfo();
        optInputParameterInfos = createOptInputParameterInfo();
        outputParameterInfos = createOutputParameterInfo();
        
        indicatorInfo = createIndicatorInfo();
        indicatorInfo.setSparseIndicator(true);
    }

	@Override
	public IndicatorResult calculate(int startIndex, int endIndex) {
		IndicatorResult indicatorResult = null;
		
		/** pivotRanks[0][...] - max
		 *  pivotRanks[1][...] - min */
		int[][] pivotRanks = calculatePivotRanks(inputs[0]);
		
		
		for (int i = getBarsOnSidesStartCount(); i < getBarsOnSidesIncrementCount(); i++) {
			
			int offset = i;
			int lookForward = 0;//offset;
			int lookBack = 0;//offset;
			
			if (startIndex < lookBack) {
				startIndex = lookBack;
			}
			if (endIndex + lookForward >= inputs[0][0].length) {
				endIndex = inputs[0][0].length - 1 - lookForward;
			}
			
			if (startIndex > endIndex) {
				continue;
			}
			
			int length = endIndex - startIndex + 1;
			
			int patternIndex 			= 0;
			int firstAsymptoteIndex 	= 1;
			int secondAsymptoteIndex 	= 2;
			
			Object[] pattern 			= output[patternIndex];
			Object[] firstAsymptote 	= output[firstAsymptoteIndex];
			Object[] secondAsymptote 	= output[secondAsymptoteIndex];
			double[] pivotPointsArray 	= new double[length];
			
			IndicatorResult ir = calculatePivotPoints(inputs[0], pivotRanks, pivotPointsArray, pattern, firstAsymptote, secondAsymptote, offset, startIndex, endIndex);
			if (indicatorResult == null) {
				indicatorResult = ir;
			}
			
			checkPatterns(inputs[0], pivotPointsArray, pattern, firstAsymptote, secondAsymptote, offset);
		}
		
		return indicatorResult;
	}
	
	/**
	 * Calculates local max and min ranks for specified data array.
	 * 
	 * @return int[0..1][] 0 - max; 1 - min;
	 */
	private int[][] calculatePivotRanks(double[][] data) {
	    int size = data[0].length;
	    int[][] pivotRanks = new int[2][size];
	    
	    // what to take high/low or close price, depends on user choice
        int highIndex = CLOSE_PRICE.equals(getPivotPointCalculationPrice()) ? 1 : 2;
        int lowIndex = CLOSE_PRICE.equals(getPivotPointCalculationPrice()) ? 1 : 3;
	    
	    for (int i = 0; i < size; i++) {
	        // max rank
	        int rank = 1;
	        while ((i - rank >= 0) && (i + rank < size)) {
	            if ((data[highIndex][i] < data[highIndex][i-rank]) || (data[highIndex][i] < data[highIndex][i+rank])) {
	                break;
	            }
	            rank++;
	        }
	        pivotRanks[0][i] = rank;
	        
	        // min rank
	        rank = 1;
	        while ((i - rank >= 0) && (i + rank < size)) {
	            if ((data[lowIndex][i] > data[lowIndex][i-rank]) || (data[lowIndex][i] > data[lowIndex][i+rank])) {
	                break;
	            }
	            rank++;
	        }
	        pivotRanks[1][i] = rank;
	    }
	    
	    return pivotRanks;
	}
	
	protected IndicatorResult calculatePivotPoints(
			double[][] inputs,
			int[][] pivotRanks,
			double[] pivotPointsArray, 
			Object[] pattern, 
			Object[] firstAsymptote, 
			Object[] secondAsymptote, 
			int barsOnSides, 
			int startIndex, 
			int endIndex
	) {
		
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }
        
        // what to take high/low or close price, depends on user choice
        int highIndex = CLOSE_PRICE.equals(getPivotPointCalculationPrice()) ? 1 : 2;
        int lowIndex = CLOSE_PRICE.equals(getPivotPointCalculationPrice()) ? 1 : 3;
        
        
        Boolean previousMax = null;
        int previousRank = 0;
        int previousIndex = 0;
        
        for (int i = startIndex; i <= endIndex; i++) {
            
            int index = i - startIndex;// + barsOnSides;
            if (index >= pivotPointsArray.length) {
                break;
            }
            pivotPointsArray[index] = Double.NaN;
            
            if (! Boolean.TRUE.equals(previousMax)) {
                if (pivotRanks[MAX_IDX][i] >= barsOnSides) {
                    pivotPointsArray[index] = inputs[highIndex][i];
                    previousMax = Boolean.TRUE;
                    previousRank = pivotRanks[MAX_IDX][i];
                    previousIndex = index;
                } else if (pivotRanks[MIN_IDX][i] > barsOnSides && pivotRanks[MIN_IDX][i] > previousRank) { // found smaller min than previous
                    pivotPointsArray[previousIndex] = Double.NaN;
                    pivotPointsArray[index] = inputs[lowIndex][i];
                    previousRank = pivotRanks[MIN_IDX][i];
                    previousIndex = index;
                }
            }
            if (! Boolean.FALSE.equals(previousMax)) {
                if (pivotRanks[MIN_IDX][i] >= barsOnSides) {
                    pivotPointsArray[index] = inputs[lowIndex][i];
                    previousMax = Boolean.FALSE;
                    previousRank = pivotRanks[MIN_IDX][i];
                    previousIndex = index;
                } else if (pivotRanks[MAX_IDX][i] > barsOnSides && pivotRanks[MAX_IDX][i] > previousRank) { // found greater max than previous
                    pivotPointsArray[previousIndex] = Double.NaN;
                    pivotPointsArray[index] = inputs[highIndex][i];
                    previousRank = pivotRanks[MAX_IDX][i];
                    previousIndex = index;
                }
            }
            
            initPatternIndicatorOutput(pattern, index);
            initPatternIndicatorOutput(firstAsymptote, index);
            initPatternIndicatorOutput(secondAsymptote, index);
        }
        
        
        return new IndicatorResult(startIndex, endIndex - startIndex + 1, endIndex);
	}
	
	private void initPatternIndicatorOutput(Object[] src, int index) {
		if (src == null) {
			return;
		}
       	if (src[index] == null) {
       		src[index] = new PatternIndicatorOutput();
       	}
	}
	
	protected void checkPatterns(
	            double[][] sourceData,
				double[] pivotPointsData, 
				Object[] patternContainer, 
				Object[] firstAsymptoteContainer, 
				Object[] secondAsymptoteContainer,
				int offset
	) {
		
	    for (int points = getMinPatternPivotPointNumber(); points <= getMaxPatternPivotPointNumber(); points++) {
    		double [] cleanPattern = new double[points];
    		
    		for (int i = 0; i < pivotPointsData.length; i++) {
    			if (i >= pivotPointsData.length - cleanPattern.length) {
    				break;
    			}
    			
    			int dirtyPatternSize = preparePatternArray(cleanPattern, pivotPointsData, i);
    
    			if (dirtyPatternSize > 0) {
    				if (checkPattern(cleanPattern, pivotPointsData, i, dirtyPatternSize)) {
    					IndexValue[] pattern = constructPattern(cleanPattern, pivotPointsData, i, dirtyPatternSize);
    					IndexValue[] upperAsymptote = constructUppperAsymptote(cleanPattern, pivotPointsData, i, dirtyPatternSize);
    					IndexValue[] bottomAsymptote = constructBottomAsymptote(cleanPattern, pivotPointsData, i, dirtyPatternSize);
    					
    					PatternObject patternObjectPattern = new PatternObject(pattern);
    					PatternObject patternObjectUpperAsymptote = new PatternObject(upperAsymptote);
    					PatternObject patternObjectBottomAsymptote = new PatternObject(bottomAsymptote);
    					
    					((PatternIndicatorOutput)patternContainer[i]).addPatternObject(patternObjectPattern);
    					((PatternIndicatorOutput)firstAsymptoteContainer[i]).addPatternObject(patternObjectUpperAsymptote);
    					
    					if (secondAsymptoteContainer != null) {
    						((PatternIndicatorOutput)secondAsymptoteContainer[i]).addPatternObject(patternObjectBottomAsymptote);
    					}
    				}
    			}
    		}
	    }
	}

	public IndexValue[] constructPattern(
			double[] cleanPattern, 
			double[] pivotPointsData, 
			int pivotPointsDataStartIndex, 
			int pivotPointsDataDirtyPatternSize
			
	) {
		IndexValue[] indexValues = new IndexValue[cleanPattern.length];
		int[] indicies = getSourceIndices(pivotPointsData, pivotPointsDataStartIndex, pivotPointsDataDirtyPatternSize, cleanPattern);
		
		for (int i = 0; i < cleanPattern.length; i ++) {
			indexValues[i] = new IndexValue(cleanPattern[i], indicies[i]);
		}
		
		return indexValues;
	}
	
	protected IndexValue[] constructDiagonalAsymptoteUntilCrossWithHorizontal(
			double[] cleanPattern,
			double[] dirtyPattern,
			int dirtyPatternStartIndex,
			int dirtyPatternSize,
			int[] diagonalAsymptoteLinePriceIndices,
			int[] horizontalAsymptoteLinePriceIndices
	) {
		double horizontalAsymptoteAverageValue = averageValueForIndicies(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, horizontalAsymptoteLinePriceIndices);
		int crossingIndex = (int)calculateCrossIndex(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, diagonalAsymptoteLinePriceIndices, horizontalAsymptoteLinePriceIndices, horizontalAsymptoteAverageValue);

		int[] sourceIndices = getSourceIndices(dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, cleanPattern);
		
		int pointX1 = diagonalAsymptoteLinePriceIndices[0];
		int pointX2 = diagonalAsymptoteLinePriceIndices[diagonalAsymptoteLinePriceIndices.length - 1];
		
		int x0 = sourceIndices[0];
		int x1 = sourceIndices[pointX1]; 
		int x2 = sourceIndices[pointX2];
		int x3 = crossingIndex;
		
		double y1 = dirtyPattern[x1];
		double y2 = dirtyPattern[x2];
		
		double doubleY0 = y4x(x1, y1, x2, y2, x0);
		double doubleY3 = y4x(x1, y1, x2, y2, x3);
		
		if (isUseful(doubleY0) && isUseful(doubleY3)) {
			
			IndexValue[] indexValue = createIndexValues(x0, doubleY0, x3, doubleY3);
			return indexValue;
		}
		
		return null;
	}
	
	protected IndexValue[] constructHorizontalAsymptoteUntilCross(
			double[] cleanPattern,
			double[] dirtyPattern,
			int dirtyPatternStartIndex,
			int dirtyPatternSize,
			int[] horizontalAsymptoteLinePriceIndices,
			int[] secondCrossingAsymptoteLinePriceIndices
	) {
		double horizontalAsymptoteAverageValue = averageValueForIndicies(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, horizontalAsymptoteLinePriceIndices);
		double crossingIndex = calculateCrossIndex(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, secondCrossingAsymptoteLinePriceIndices, horizontalAsymptoteLinePriceIndices, horizontalAsymptoteAverageValue);
		
		IndexValue[] indexValue = createIndexValues(dirtyPatternStartIndex, horizontalAsymptoteAverageValue, (int) crossingIndex, horizontalAsymptoteAverageValue);
		return indexValue;
	}
	
	private IndexValue[] createIndexValues(int index1, double value1, int index2, double value2) {
		IndexValue[] indexValue = new IndexValue[2];

		IndexValue indexValue0 = new IndexValue(value1, index1);  
		indexValue[0] = indexValue0;
		
		IndexValue indexValue1 = new IndexValue(value2, index2);
		indexValue[1] = indexValue1;
		
		return indexValue;
	}
	
	protected IndexValue[] constructHorizontalAsymptote(
			double[] cleanPattern, 
			double[] source, 
			int sourceStartIndex, 
			int dirtyPatternSize, 
			int[] asymptoteLinePriceIndices
		) {
		
		int lastPatternPointIndex = sourceStartIndex + dirtyPatternSize - 1;
		
		double[] asymptoteLinePriceValues = indicesToValues(cleanPattern, asymptoteLinePriceIndices);
		double avgPoint = average(asymptoteLinePriceValues);
		
		IndexValue[] indexValue = createIndexValues(sourceStartIndex, avgPoint, lastPatternPointIndex, avgPoint);
		return indexValue;
	}
	
	protected IndexValue[] constructDiagonalAsymptoteUntilCross(
			double[] cleanPattern, 
			double[] dirtyPattern, 
			int dirtyPatternStartIndex, 
			int dirtyPatternSize, 
			int[] asymptoteLinePriceIndices,
			int[] secondCrossingAsymptoteLinePriceIndices
	) {
		int[] sourceIndices = getSourceIndices(dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, cleanPattern);
		
		int pointX0 = 0;
		int pointX1 = asymptoteLinePriceIndices[0];
		int pointX2 = asymptoteLinePriceIndices[asymptoteLinePriceIndices.length - 1];
		
		int x0 = sourceIndices[pointX0];
		int x1 = sourceIndices[pointX1]; 
		int x2 = sourceIndices[pointX2];
		int x3 = dirtyPatternStartIndex + dirtyPatternSize - 1;
		if (! isWholePatternCalculation()) {
		    int pointsCount = cleanPattern.length - 1;
		    x3 += dirtyPatternSize / pointsCount;
		}
		
		double y1 = dirtyPattern[x1];
		double y2 = dirtyPattern[x2];
		
		if (secondCrossingAsymptoteLinePriceIndices != null) {
			double crossIndex = calculateCrossIndex(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, asymptoteLinePriceIndices, secondCrossingAsymptoteLinePriceIndices);
			final int EXTRA_CANDLES_COUNT = 5;
			if (isUseful(crossIndex)) {
				x3 = (int) (crossIndex + EXTRA_CANDLES_COUNT);
			}
		}
		
		double doubleY0 = y4x(x1, y1, x2, y2, x0);
		double doubleY3 = y4x(x1, y1, x2, y2, x3);
		
		if (isUseful(doubleY0) && isUseful(doubleY3)) {
			
			IndexValue[] indexValue = createIndexValues(x0, doubleY0, x3, doubleY3);
			return indexValue;
		}
		
		return null;

	}
	
	protected IndexValue[] constructDiagonalAsymptote(
			double[] cleanPattern, 
			double[] source, 
			int sourceStartIndex, 
			int dirtyPatternSize, 
			int[] asymptoteLinePriceIndices
	) {
		return constructDiagonalAsymptoteUntilCross(cleanPattern, source, sourceStartIndex, dirtyPatternSize, asymptoteLinePriceIndices, null);
	}
	
	protected double calculateCrossIndex(
			double x11, 
			double y11, 
			double x12, 
			double y12, 
			double x21, 
			double y21, 
			double x22, 
			double y22
		
	) {
		double a1 = a(x11, y11, x12, y12);
		double b1 = b(x11, y11, a1);
		
		double a2 = a(x21, y21, x22, y22);
		double b2 = b(x21, y21, a2);
		
		double xCross = xCross(a1, b1, a2, b2);
		
		return xCross;
	}

	protected double calculateCrossIndex(
			double[] cleanPattern, 
			double[] dirtyPattern, 
			int dirtyPatternStartIndex, 
			int dirtyPatternSize, 
			int[] firstAsymptoteIndices, 
			int[] secondAsymptoteIndices, 
			double secondAsymptoteAverageValue
	) {
		int[] sourceIndices = getSourceIndices(dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, cleanPattern);
		
		int x11 = sourceIndices[firstAsymptoteIndices[0]]; 
		int x12 = sourceIndices[firstAsymptoteIndices[firstAsymptoteIndices.length - 1]];
		
		int x21 = sourceIndices[secondAsymptoteIndices[0]]; 
		int x22 = sourceIndices[secondAsymptoteIndices[secondAsymptoteIndices.length - 1]];
		
		double y11 = dirtyPattern[x11];
		double y12 = dirtyPattern[x12];
		
		double y21 = isUseful(secondAsymptoteAverageValue) ? secondAsymptoteAverageValue : dirtyPattern[x21];
		double y22 = isUseful(secondAsymptoteAverageValue) ? secondAsymptoteAverageValue : dirtyPattern[x22];
		
		double xCross = calculateCrossIndex(x11, y11, x12, y12, x21, y21, x22, y22);
		
		return xCross;
	}
		
	protected double calculateCrossIndex(
			double[] cleanPattern, 
			double[] source, 
			int sourceStartIndex, 
			int dirtyPatternSize, 
			int[] firstAsymptoteIndices, 
			int[] secondAsymptoteIndices
			
	) {
		return calculateCrossIndex(cleanPattern, source, sourceStartIndex, dirtyPatternSize, firstAsymptoteIndices, secondAsymptoteIndices, Double.NaN);
	}
	
	protected double calculateLastAsymptoteIndex(
			double[] cleanPattern, 
			double[] source, 
			int sourceStartIndex, 
			int dirtyPatternSize, 
			double priceBorderDontCross, 
			int[] asymptoteLinePriceIndices
			
	) {
		int[] sourceIndices = getSourceIndices(source, sourceStartIndex, dirtyPatternSize, cleanPattern);
		
		int pointX1 = asymptoteLinePriceIndices[0];
		int pointX2 = asymptoteLinePriceIndices[asymptoteLinePriceIndices.length - 1];
		
		int x1 = sourceIndices[pointX1]; 
		int x2 = sourceIndices[pointX2];
		
		double y1 = source[x1];
		double y2 = source[x2];
		
		double doubleX3 = x4y(x1, y1, x2, y2, priceBorderDontCross);
		
		return doubleX3;
	}
	
	protected double[] indicesToValues(double[] source, int[] indices) {
		double[] values = new double[indices.length];
		
		for (int i = 0; i < indices.length; i++ ) {
			values[i] = source[indices[i]];
		}
		
		return values;
	}
	
	/*
	 * returns source indices corresponding to clean pattern values
	 */
	private int[] getSourceIndices(
			double[] dirtyPattern, 
			int dirtyStartIndex, 
			int dirtyPatternSize, 
			double[] cleanPattern
			
	) {
		int cleanPatternPointNumber = 0;
		int lastPatternPointIndex = dirtyStartIndex + dirtyPatternSize;
		int sourceAppropriateIndices[] = new int[cleanPattern.length];
		
		for (int i = dirtyStartIndex; i < lastPatternPointIndex; i++) {
			double value = dirtyPattern[i];
			
			if (isUseful(value)) { // we are working only with normal numbers (not NaNs)
					
				sourceAppropriateIndices[cleanPatternPointNumber] = i;
				cleanPatternPointNumber++;
					
				if (cleanPatternPointNumber >= sourceAppropriateIndices.length) { // already filled
					break;
				}
			}
		}
		
		return sourceAppropriateIndices;
	}
	
	
	protected boolean isPointOnAssymptote(
	        int index,
	        double[] cleanPattern, 
            double[] dirtyPattern, 
            int dirtyPatternStartIndex, 
            int dirtyPatternSize, 
            int[] asymptoteIndices
	) {
	    return areParallel(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, asymptoteIndices, new int[]{asymptoteIndices[0], index});
	}
	
	protected boolean areParallel(
			double[] cleanPattern, 
			double[] dirtyPattern, 
			int dirtyPatternStartIndex, 
			int dirtyPatternSize, 
			int[] firstAsymptoteIndices, 
			int[] secondAsymptoteIndices
			
	) {
	    int[] sourceIndices = getSourceIndices(dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, cleanPattern);
	        
	    double priceGrowth1 = getAsymptotePriceGrowth(cleanPattern, sourceIndices, firstAsymptoteIndices);
	    double priceGrowth2 = getAsymptotePriceGrowth(cleanPattern, sourceIndices, secondAsymptoteIndices);
	    
	    double delta = Math.abs(priceGrowth1 - priceGrowth2);
	    double priceDigression = getPatternPriceSpread(cleanPattern) / dirtyPatternSize;
	    double digression = delta / priceDigression * DIGRESSION_RATIO;
	    
	    if (digression < getReversedPatternQuality()) {
	        parallelLineQuality = Math.min(parallelLineQuality, HUNDRED_PERCENT - digression);
	        return true;
	    }
	    return false;
	}
	
	private double getAsymptotePriceGrowth(double[] cleanPattern, int[] sourceIndices, int[] asymptoteIndices) {
	    int idx1 = asymptoteIndices[0];
	    int idx2 = asymptoteIndices[1];
	    int length = Math.abs(sourceIndices[idx1] - sourceIndices[idx2]);
	    double priceDiff = Math.abs(cleanPattern[idx1] - cleanPattern[idx2]);
	    
	    return priceDiff / length;
	}
	
	protected double angleBetweenAsymptotesInDegrees(
			double[] cleanPattern,
			double[] dirtyPattern,
			int dirtyPatternStartIndex,
			int dirtyPatternSize,
			int[] firstAsymptoteIndices,
			int[] secondAsymptoteIndices
	) {
		int[] sourceIndices = getSourceIndices(dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, cleanPattern);
		
		int x11 = sourceIndices[firstAsymptoteIndices[0]]; 
		int x12 = sourceIndices[firstAsymptoteIndices[firstAsymptoteIndices.length - 1]];
		
		int x21 = sourceIndices[secondAsymptoteIndices[0]]; 
		int x22 = sourceIndices[secondAsymptoteIndices[secondAsymptoteIndices.length - 1]];
		
		double y11 = dirtyPattern[x11];
		double y12 = dirtyPattern[x12];
		
		double y21 = dirtyPattern[x21];
		double y22 = dirtyPattern[x22];
		
		double angle = angleBetween2LinesDegrees(
				new Line2D.Double(x11, y11, x12, y12),
				new Line2D.Double(x21, y21, x22, y22)
		);
		
		return angle;
	}
			
	public static double angleBetween2LinesDegrees(Line2D line1, Line2D line2) {
		double angle = angleBetween2LinesRadians(line1, line2);
		double result = angle * 180d / Math.PI;
		return result;
	}
	 
	public static double angleBetween2LinesRadians(Line2D line1, Line2D line2) {
		double angle1 = Math.atan2(line1.getY2() - line1.getY1(), line1.getX2() - line1.getX1());
		double angle2 = Math.atan2(line2.getY2() - line2.getY1(), line2.getX2() - line2.getX1());
		double result = angle1 - angle2;
		return result;
	}
	
	protected boolean willAsymptotesCrossUntilPatternEnd(
			double[] cleanPattern, 
			double[] dirtyPattern, 
			int dirtyPatternStartIndex, 
			int dirtyPatternSize, 
			int[] firstAsymptoteIndices, 
			int[] secondAsymptoteIndices, 
			double secondAsymptoteAverageValue
			
	) {
		double crossIndex = calculateCrossIndex(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, firstAsymptoteIndices, secondAsymptoteIndices, secondAsymptoteAverageValue);
		return willAsymptotesCrossUntilPatternEnd(dirtyPatternStartIndex, dirtyPatternSize, crossIndex);
	}
	
	protected boolean willAsymptotesCrossUntilPatternEnd(
			double[] cleanPattern, 
			double[] dirtyPattern, 
			int dirtyPatternStartIndex, 
			int dirtyPatternSize, 
			int[] firstAsymptoteIndices, 
			int[] secondAsymptoteIndices
			
	) {
		double crossIndex = calculateCrossIndex(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, firstAsymptoteIndices, secondAsymptoteIndices);
		return willAsymptotesCrossUntilPatternEnd(dirtyPatternStartIndex, dirtyPatternSize, crossIndex);
	}

	private boolean willAsymptotesCrossUntilPatternEnd(
			int dirtyPatternStartIndex, 
			int dirtyPatternSize, 
			double crossIndex
			
	) {
		return willAsymptotesCross(dirtyPatternStartIndex, dirtyPatternSize, crossIndex, 0);
	}
	
	private boolean willAsymptotesCross(
			int dirtyPatternStartIndex,
			int dirtyPatternSize,
			double crossIndex,
			int unitsCount
	) {
		int lastPatternPointIndex = dirtyPatternStartIndex + dirtyPatternSize - 1 + unitsCount;
		
		if (isUseful(crossIndex) && crossIndex < lastPatternPointIndex) {
			return true;
		}
		else {
			return false;
		}
	}
	
	protected boolean willAsymptotesCrossBeforeBarsCount(
			double[] cleanPattern, 
			double[] dirtyPattern, 
			int dirtyPatternStartIndex, 
			int dirtyPatternSize, 
			int[] firstAsymptoteIndices, 
			int[] secondAsymptoteIndices,
			double secondAsymptoteAverageValue,
			int unitsCount
	) {
		double crossIndex = calculateCrossIndex(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, firstAsymptoteIndices, secondAsymptoteIndices, secondAsymptoteAverageValue);
		return willAsymptotesCross(dirtyPatternStartIndex, dirtyPatternSize, crossIndex, unitsCount);
	}
	
	protected boolean willAsymptotesCrossForUnitsCount(
			double[] cleanPattern, 
			double[] dirtyPattern, 
			int dirtyPatternStartIndex, 
			int dirtyPatternSize, 
			int[] firstAsymptoteIndices, 
			int[] secondAsymptoteIndices,
			int unitsCount
	) {
		double crossIndex = calculateCrossIndex(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, firstAsymptoteIndices, secondAsymptoteIndices);
		return willAsymptotesCross(dirtyPatternStartIndex, dirtyPatternSize, crossIndex, unitsCount);
	}
	
	protected int preparePatternArray(
			double[] cleanPattern, 
			double[] source, 
			int sourceStartIndex
			
	) {
		for (int destanationIndex = 0,  k = sourceStartIndex; k < source.length; k++) {
			double value = source[k];
			if (isUseful(value)) {
				
				cleanPattern[destanationIndex] = value;
				
				destanationIndex++;
			}
			else if (destanationIndex == 0) {
				return -1;
			}
			
			if (destanationIndex >= cleanPattern.length) {
				return k - sourceStartIndex + 1;
			}
			
		}
		return -1;
	}
	
	@Override
	public IndicatorInfo getIndicatorInfo() {
        return indicatorInfo;
	}

	@Override
    public InputParameterInfo getInputParameterInfo(int index) {
        if (index <= inputParameterInfos.length) {
            return inputParameterInfos[index];
        }
        return null;
    }

	@Override
	public int getLookback() {
//        return getBarsOnSidesStartCount();
	    return 0;
	}

	@Override
	public int getLookforward() {
//		return getBarsOnSidesStartCount();
	    return 0;
	}

	@Override
	public OptInputParameterInfo getOptInputParameterInfo(int index) {
        if (index < optInputParameterInfos.length) {
            return optInputParameterInfos[index];
        }
		return null;
	}

	@Override
	public OutputParameterInfo getOutputParameterInfo(int index) {
		int ind = index % getNumberOfOutputsForOneSubindicator(); 
        if (ind <= outputParameterInfos.length) {
            return outputParameterInfos[ind];
        }
		return null;
	}

	@Override
	public void setInputParameter(int index, Object array) {
        inputs[index] = (double[][]) array;
	}

	@Override
	public void setOptInputParameter(int index, Object value) {
        if (index == 0) {
            setBarsOnSidesStartCount((Integer) value);
        }
        else if (index == 1) {
        	setPatternQuality((Double) value);
        }
        else if (index == 2) {
        	setPivotPointCalculationPrice(pivotPointPriceCaptions[(Integer)value]);
        }
        else if (index == 3) {
        	setWholePatternCalculation(((Boolean)value).booleanValue());
        }
        else {
        	throw new IllegalArgumentException("Unsupported index value " + index);
        }
	}

	@Override
	public void setOutputParameter(int index, Object array) {
		if (array instanceof Object[]) {
			output[index] = (Object[]) array;
		}
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
            Map<Color, List<Point>> handles
            
	) {
		
		if (values instanceof Object[]) {
			Graphics2D g2d = (Graphics2D) g;

			g2d.setColor(color);
			g2d.setStroke(stroke);
			
			Object[] output = (Object[]) values;
			
			for (int i = 0; i < output.length; i ++) {
				if (output[i] instanceof PatternIndicatorOutput) {
					PatternIndicatorOutput patternIndicatorOutput = (PatternIndicatorOutput) output[i];
					if (patternIndicatorOutput.getPatternObjects() != null && !patternIndicatorOutput.getPatternObjects().isEmpty()) {
						for (PatternObject patternObject : patternIndicatorOutput.getPatternObjects()) {
							drawIndexValues(patternObject.getPattern(), g2d, indicatorDrawingSupport, i);
						}
					}
				}
			}
		}
		return null;
	}
	
	protected void drawIndexValues(
			IndexValue[] indexValues, 
			Graphics2D g, 
			IIndicatorDrawingSupport indicatorDrawingSupport,
			int index
	) {
		if (indexValues == null) {
			return;
		}
		
		int prevIndex = -1;
		double prevValue = -1;
		Integer delta = null;
		
		for (int i = 0; i < indexValues.length; i++) {
			IndexValue indexValue = indexValues[i];
			if (delta == null) {
				delta = indexValue.getIndex() - index;
			}
			if (prevIndex != -1) {
				int y1 = (int) indicatorDrawingSupport.getYForValue(indexValue.getValue());
				int y2 = (int) indicatorDrawingSupport.getYForValue(prevValue);
				int x1 = (int) indicatorDrawingSupport.getMiddleOfCandle(indexValue.getIndex() - delta);
				int x2 = (int) indicatorDrawingSupport.getMiddleOfCandle(prevIndex - delta);
				
				if (x1 > 0 || x2 > 0) {
					drawPatternLine(indexValues, i, g, x1, y1, x2, y2);
				}
			}
			prevIndex = indexValue.getIndex();
			prevValue = indexValue.getValue();
		}
	}
	
	protected void drawPatternLine(
			IndexValue[] indexValues,
			int currentIndex,
			Graphics2D g,
			int x1,
			int y1,
			int x2,
			int y2
	) {
		g.drawLine(x1, y1, x2, y2);
	}
	
	
//	------------------------------------------------------ MATH UTILS -------------------------------------------------------
	
	
	/**
	 * 
	 * y = ax + b
	 * 
	 */
	public static double y4x(
			double x1, 
			double y1, 
			double x2, 
			double y2, 
			double x3
			
	) {
		if (x2 - x1 == 0) {
			return Double.NaN;
		}
		
		double a = a(x1, y1, x2, y2);
		double b = b(x1, y1, a);
		
		double y3 = a * x3 + b;
		return y3;
	}
	
	/**
	 * 
	 * x = (y - b) / a
	 * 
	 */
	public static double x4y(
			double x1, 
			double y1, 
			double x2, 
			double y2, 
			double y3
			
	) {
		if (x2 - x1 == 0) {
			return Double.NaN;
		}
		
		double a = a(x1, y1, x2, y2);
		double b = b(x1, y1, a);
		
		if (a == 0) {
			return Double.NaN;
		}
		
		double x3 = (y3 - b) / a; 
		
		return x3;
	}
	
	public static double a(
			double x1, 
			double y1, 
			double x2, 
			double y2
			
	) {
		double a = (y2 - y1) / (x2 - x1) ;
		return a;
	}
	
	public static double b(
			double x1, 
			double y1, 
			double a
			
	) {
		double b = y1 - a * x1;
		return b;
	}
	
	public static double xCross(
			double a1, 
			double b1, 
			double a2, 
			double b2
			
	) {
		if (a1 == a2) {
			return Double.NaN;
		}
		return (b2 - b1) / (a1 - a2);
	}
	
	protected double averageValueForIndicies(
			double[] cleanPattern,
			double[] dirtyPattern,
			int dirtyPatternStartIndex,
			int dirtyPatternSize,
			int[] indicies
	) {
		int[] dirtyPatternIndices = getSourceIndices(dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, cleanPattern);
		
		double values[] = new double[indicies.length];
		for (int i = 0; i < indicies.length; i++) {
			int index = indicies[i];
			values[i] = dirtyPattern[dirtyPatternIndices[index]];
		}
		return average(values);
	}
	
	public static double average(double ... array) {
		double sum = 0;
		for (int i = 0; i < array.length; i++) {
			sum += array[i];
		}
		return sum / array.length;
	}
	
	public static double getPatternPriceSpread(double[] cleanPattern) {
	    double max = cleanPattern[0];
	    double min = cleanPattern[0];
	    for (int i = 1; i < cleanPattern.length; i++) {
	        max = Math.max(max, cleanPattern[i]);
	        min = Math.min(min, cleanPattern[i]);
	    }
	    
	    return max - min;
	}

	protected boolean aproxEqualTriple(
			double v1, 
			double v2, 
			double v3,
			double patternSpread
	) {
		double eps = patternSpread * EQ_DELTA * getReversedPatternQuality() / HUNDRED_PERCENT; // deviation more than 10% of pattern spread means 0% points quality
		
		double maxRealEps = Math.abs(v1 - v2);
        maxRealEps = Math.max(maxRealEps, Math.abs(v2 - v3));
        maxRealEps = Math.max(maxRealEps, Math.abs(v1 - v3));
        
        if (maxRealEps <= eps) {
            
            double currentQuality = HUNDRED_PERCENT - (maxRealEps * HUNDRED_PERCENT / (EQ_DELTA * patternSpread));
            pointsEqualityQuality = Math.min(pointsEqualityQuality, currentQuality);
            
            return true;
        }
        return false;
	}
	
	protected boolean aproxEqual(double v1, double v2, double patternSpread) {
		double eps = patternSpread * EQ_DELTA * getReversedPatternQuality() / HUNDRED_PERCENT;
		
		double realEps = Math.abs(v1 - v2);
        if (realEps <= eps) {
            
            double currentQuality = HUNDRED_PERCENT - (realEps * HUNDRED_PERCENT / (EQ_DELTA * patternSpread));
            pointsEqualityQuality = Math.min(pointsEqualityQuality, currentQuality);
            
            return true;
        }
        return false;
	}
	
	
	
//	------------------------------------------------------ HELPER METHODS -------------------------------------------------------
	
	
	protected double checkGapsQuality(
                	        double[] dirtyPattern, 
                            int dirtyStartIndex, 
                            int dirtyPatternSize, 
                            double[] cleanPattern
    ) {
	    int[] sourceIndices = getSourceIndices(dirtyPattern, dirtyStartIndex, dirtyPatternSize, cleanPattern);
	    double[] gapSizes = new double[sourceIndices.length - 1];
	    for (int i = 1; i < sourceIndices.length; i++) {
	        gapSizes[i-1] = sourceIndices[i] - sourceIndices[i-1];
	    }
	    
        double averageGapSize = average(gapSizes);
        
        double maxReversedQuality = 0;
        
        for (int i = 0; i < gapSizes.length; i++) {  // checks ascending and descending lines separately 
            double eps = Math.abs(averageGapSize - gapSizes[i]);
            double percent = eps / averageGapSize / NORMAL_GAP_DEVIATION;
            maxReversedQuality = Math.max(maxReversedQuality, percent);
        }
        maxReversedQuality *= HUNDRED_PERCENT;
        maxReversedQuality = Math.min(maxReversedQuality, HUNDRED_PERCENT);
        
        return HUNDRED_PERCENT - maxReversedQuality;
    }
	
	protected boolean checkCandlePostTrendIsUnderUpperAsymptote(
	        double[][] sourceData,
            int dirtyPatternStartIndex,
            int dirtyPatternSize,
            IndexValue[] patternPoints,
            IndexValue[] upperAsymptotePoints,
            double sigma
    ) {
	    int highIndex = CLOSE_PRICE.equals(getPivotPointCalculationPrice()) ? 1 : 2;
	    
	    int x1 = upperAsymptotePoints[0].getIndex() + dirtyPatternStartIndex;
	    double y1 = upperAsymptotePoints[0].getValue();
	    
	    int x2 = upperAsymptotePoints[1].getIndex() + dirtyPatternStartIndex;
	    double y2 = upperAsymptotePoints[1].getValue();
	    
	    double asymptoteGrowthStep = (y2 - y1) / (x2 - x1);
	    
	    int patternEndIdx = dirtyPatternStartIndex + dirtyPatternSize;
	    
	    double asymptoteValue = y4x(x1, y1, x2, y2, patternEndIdx);
	    
	    for (int i = patternEndIdx;  i < sourceData[0].length;  i++, asymptoteValue += asymptoteGrowthStep) {
	        if (sourceData[highIndex][i] > asymptoteValue + sigma) {
	            return false;
	        }
	    }
	    
	    return true;
	    
	}
	
	protected boolean checkCandlePostTrendIsOverBottomAsymptote(
	        double[][] sourceData,
            int dirtyPatternStartIndex,
            int dirtyPatternSize,
            IndexValue[] patternPoints,
            IndexValue[] bottomAsymptotePoints,
            double sigma
    ) {
	    int lowIndex = CLOSE_PRICE.equals(getPivotPointCalculationPrice()) ? 1 : 3;
	    
	    int x1 = bottomAsymptotePoints[0].getIndex() + dirtyPatternStartIndex;
        double y1 = bottomAsymptotePoints[0].getValue();
        
        int x2 = bottomAsymptotePoints[1].getIndex() + dirtyPatternStartIndex;
        double y2 = bottomAsymptotePoints[1].getValue();
        
        double asymptoteGrowthStep = (y2 - y1) / (x2 - x1);
        
        int patternEndIdx = dirtyPatternStartIndex + dirtyPatternSize;
        
        double asymptoteValue = y4x(x1, y1, x2, y2, patternEndIdx);
        
        for (int i = patternEndIdx;  i < sourceData[0].length;  i++, asymptoteValue += asymptoteGrowthStep) {
            if (sourceData[lowIndex][i] < asymptoteValue - sigma) {
                return false;
            }
        }
	    
        return true;
    }
	
	
	protected double getAsymptoteCrossIdxQuality(
                	        double[] dirtyPattern,
                            int dirtyPatternStartIndex,
                            int dirtyPatternSize,
                            double[] cleanPattern,
                            int[] firstAsymptoteIndices, 
                            int[] secondAsymptoteIndices,
                            int standardCrossIdxDelta,
                            boolean lessThanStandardCrossIdxAllowed
	) {
	    double crossIdx = calculateCrossIndex(
                            	            cleanPattern,
                            	            dirtyPattern,
                            	            dirtyPatternStartIndex,
                            	            dirtyPatternSize,
                            	            firstAsymptoteIndices,
                            	            secondAsymptoteIndices,
//                            	            average(cleanPattern[secondAsymptoteIndices[0]], cleanPattern[secondAsymptoteIndices[1]])
                            	            Double.NaN
                        	            );
	    int standardCrossIdx = dirtyPatternStartIndex + dirtyPatternSize + standardCrossIdxDelta;
	    if (! lessThanStandardCrossIdxAllowed && crossIdx < standardCrossIdx) {
	        return 0;
	    }
	    return Math.max(0, HUNDRED_PERCENT - (Math.abs(crossIdx - standardCrossIdx) / (dirtyPatternSize + standardCrossIdxDelta) * HUNDRED_PERCENT));
	}
	
	protected boolean checkAsymptoteCrossIdxQuality(
            double[] dirtyPattern,
            int dirtyPatternStartIndex,
            int dirtyPatternSize,
            double[] cleanPattern,
            int[] firstAsymptoteIndices, 
            int[] secondAsymptoteIndices,
            int standardCrossIdxDelta,
            boolean lessThanStandardCrossIdxAllowed
	) {
	    double quality = getAsymptoteCrossIdxQuality(
    	            dirtyPattern, dirtyPatternStartIndex,
    	            dirtyPatternSize,
    	            cleanPattern,
    	            firstAsymptoteIndices,
    	            secondAsymptoteIndices,
    	            standardCrossIdxDelta,
    	            lessThanStandardCrossIdxAllowed
	            );
	    
	    return quality > getPatternQuality();
	}

	protected boolean isUseful(double value) {
		if (Double.isNaN(value)) {
			return false;
		}
		return true;
	}
	
    protected InputParameterInfo[] createInputParameterInfo() {
		return new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
		};
	}

	protected OutputParameterInfo[] createOutputParameterInfo() {
		return new OutputParameterInfo[] {
				createPatterCurveInfo(),
				createFirstAsymptoteInfo(),
				createSecondAsymptoteInfo()
	        };
	}

	protected OptInputParameterInfo[] createOptInputParameterInfo() {
		return new OptInputParameterInfo[] {
				createNumberOfBarsOnSidesStartCountInfo(),
				createPatternInaccuracy(),
				createPriceForPivotPointsCalculationInfo(),
				createWholePatternCalculationInfo()
			};	
	}
	
	protected OutputParameterInfo createSecondAsymptoteInfo() {
		return new OutputParameterInfo("Bottom Asymptote", OutputParameterInfo.Type.OBJECT, OutputParameterInfo.DrawingStyle.LINE) {{
			setDrawnByIndicator(true);
		}};
	}
	
	protected OutputParameterInfo createFirstAsymptoteInfo() {
		return new OutputParameterInfo("Upper Asymptote", OutputParameterInfo.Type.OBJECT, OutputParameterInfo.DrawingStyle.LINE){{
			setDrawnByIndicator(true);
		}};
	}
	
	protected OutputParameterInfo createPatterCurveInfo() {
		return new OutputParameterInfo("Pattern Curve", OutputParameterInfo.Type.OBJECT, OutputParameterInfo.DrawingStyle.LINE){{
			setDrawnByIndicator(true);
		}};
	}
	
	protected OptInputParameterInfo createNumberOfBarsOnSidesStartCountInfo() {
		return new OptInputParameterInfo("Start Count of Bars on Sides", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(getBarsOnSidesStartCount(), 1, 200, 1));
	}
	
	protected OptInputParameterInfo createPriceForPivotPointsCalculationInfo() {
		return new OptInputParameterInfo("Pivot Points Calculation Price", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(0, pivotPointPriceValues, pivotPointPriceCaptions));
	}
	
	protected OptInputParameterInfo createWholePatternCalculationInfo() {
		return new OptInputParameterInfo("Calclate whole Pattern", OptInputParameterInfo.Type.OTHER, new BooleanOptInputDescription(isWholePatternCalculation()));
	}

	
	protected OptInputParameterInfo createPatternInaccuracy() {
		return new OptInputParameterInfo("Quality (%)", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(patternQuality, 0.1d, 100d, 0.1d, 1));
	}
	
//	------------------------------------------------------ INNER PUBLIC CLASSES -------------------------------------------------------
	
	
	public static class PatternIndicatorOutput {
		private List<PatternObject> patternObjects;

		public List<PatternObject> getPatternObjects() {
			return patternObjects;
		}

		public void setPatternObjects(List<PatternObject> patternObjects) {
			this.patternObjects = patternObjects;
		}
		
		public void addPatternObject(PatternObject patternObject) {
			if (getPatternObjects() == null) {
				setPatternObjects(new ArrayList<PatternObject>());
			}
			
			getPatternObjects().add(patternObject);
		}
	}

    public static class IndexValue {
    	private double value;
    	private int index;
    	
    	public IndexValue(double value, int index) {
    		setIndex(index);
    		setValue(value);
    	}
    	
		public double getValue() {
			return value;
		}
		public void setValue(double value) {
			this.value = value;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		@Override
		public String toString() {
			return getIndex() + " " + getValue();
		}
    }
    
	public static class PatternObject {
		private IndexValue[] pattern;
		
		public PatternObject() {
			this(null);
		}
		
		public PatternObject(IndexValue[] pattern) {
			setPattern(pattern);
		}
		
		public IndexValue[] getPattern() {
			return pattern;
		}
		public void setPattern(IndexValue[] pattern) {
			this.pattern = pattern;
		}
		@Override
		public String toString() {
			return Arrays.toString(getPattern());
		}
	}
	
	
//	------------------------------------------------------ GETTERS / SETTERS -------------------------------------------------------
	
	
	protected String getDefaultPatternRecognitionGroup() {
		return "Pattern Recognition";
	}
	
	protected int getNumberOfOutputs() {
		return getNumberOfOutputsForOneSubindicator();
	}
	
	protected int getNumberOfOutputsForOneSubindicator() {
		return OUTPUT_COUNT_FOR_ONE_SUBINDICATOR;
	}

	protected int getBarsOnSidesStartCount() {
		return barsOnSidesStartCount;
	}
	
	protected void setBarsOnSidesStartCount(int barsOnSidesStartCount) {
		this.barsOnSidesStartCount = barsOnSidesStartCount;
	}
	
	protected int getBarsOnSidesIncrementCount() {
		return barsOnSidesIncrementCount;
	}
	
	protected OptInputParameterInfo[] getOptInputParameterInfos() {
		return optInputParameterInfos;
	}
	protected double getReversedPatternQuality() {
		return HUNDRED_PERCENT - getPatternQuality();
	}
	public double getPatternQuality() {
		return patternQuality;
	}
	public void setPatternQuality(double patternQuality) {
		this.patternQuality = patternQuality;
	}
	protected String getPivotPointCalculationPrice() {
		return pivotPointCalculationPrice;
	}
	protected void setPivotPointCalculationPrice(String pivotPointCalculationPrice) {
		this.pivotPointCalculationPrice = pivotPointCalculationPrice;
	}
	public boolean isWholePatternCalculation() {
		return wholePatternCalculation;
	}
	public void setWholePatternCalculation(boolean wholePatternCalculation) {
		this.wholePatternCalculation = wholePatternCalculation;
	}
	protected double getPointsEqualityQuality() {
	    return pointsEqualityQuality;
	}
	protected void resetPointsEqualityQuality() {
	    pointsEqualityQuality = HUNDRED_PERCENT;
	}
	protected double getParallelLineQuality() {
	    return parallelLineQuality;
	}
	protected void resetParallelLineQuality() {
	    parallelLineQuality = HUNDRED_PERCENT;
	}
	public double getCalculatedPatternQuality(){
        return calculatedPatternQuality;
    }
}
