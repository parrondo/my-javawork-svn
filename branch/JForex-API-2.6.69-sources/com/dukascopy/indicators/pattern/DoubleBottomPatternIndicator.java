/**
 * The file DoubleBottomPatternIndicator.java was created on Mar 2, 2010 at 10:31:41 AM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import com.dukascopy.api.indicators.IndicatorInfo;

public class DoubleBottomPatternIndicator extends AbstractPatternIndicator {
    
    private static final double MIN_WING_BREAKOUT = 0.1;
    private static final double MAX_WING_BREAKOUT = 0.5;
    
    private double wingBreakoutQuality;
    
	/**
	 *
	 *  0		   4
	 *   \   2    /
	 *	  \  /\  /
	 * 	   \/  \/
	 *	   1	3
	 * 
	 */
	@Override
	public boolean checkPattern(
			double[] cleanPattern, 
			double[] dirtyPattern, 
			int dirtyPatternStartIndex, 
			int dirtyPatternSize
			
	) {
	    
	    resetPointsEqualityQuality();
	    wingBreakoutQuality = HUNDRED_PERCENT;
        calculatedPatternQuality = 0;
        
        double patternPriceSpread = getPatternPriceSpread(cleanPattern);
	    
		if (
				cleanPattern[0] > cleanPattern[2] && 
				cleanPattern[1] < cleanPattern[2] &&
				cleanPattern[2] > cleanPattern[3] &&
				
				aproxEqual(cleanPattern[1], cleanPattern[3], patternPriceSpread) &&
				
				checkWingQuality(0, cleanPattern) &&
				
				(
    				!isWholePatternCalculation() ||
    				(
    						cleanPattern[2] < cleanPattern[4] &&
    						
    						checkWingQuality(4, cleanPattern)
    				)
				)
				
		) {
		    calculatedPatternQuality = calculatePatternQuality(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize);
			return (calculatedPatternQuality >= getPatternQuality());
		}
			
		return false;
	}
	
	/**
	 * 1) 40% wings quality
	 * 2) 30% points for points uniformity
	 * 3) 30% top points are placed on horizontal line
	 */
	private double calculatePatternQuality(
	                        double[] cleanPattern,
                	        double[] dirtyPattern, 
                            int dirtyStartIndex,
                            int dirtyPatternSize
    ) {
	    double priceSpread = getPatternPriceSpread(cleanPattern);
	    
	    double wingQuality = wingBreakoutQuality * 0.40;
	    double gapQuality = checkGapsQuality(dirtyPattern, dirtyStartIndex, dirtyPatternSize, cleanPattern) * 0.3;
	    double topPointsQuality = (! isWholePatternCalculation())  ? 10d // default average quality for emerging patterns
	                                                               : (HUNDRED_PERCENT - (Math.abs(cleanPattern[0] - cleanPattern[4]) / priceSpread * HUNDRED_PERCENT)) * 0.3;
	    
	    return wingQuality + gapQuality + topPointsQuality; 
	}
	
	protected boolean checkWingQuality(int wingPointIdx, double[] cleanPattern) {
	    
	    double midBottomPoint = (cleanPattern[1] + cleanPattern[3]) / 2;
	    double midDif = cleanPattern[2] - midBottomPoint;
	    
	    if (midDif > (cleanPattern[2] - Math.max(cleanPattern[1], cleanPattern[3])) * 2) {
	         return false;
	    }
	    
	    double wingDif = cleanPattern[wingPointIdx] - midBottomPoint;
	    double wingBreakoutCoeficient = midDif / wingDif;
	    
	    if (wingBreakoutCoeficient < MIN_WING_BREAKOUT || wingBreakoutCoeficient > MAX_WING_BREAKOUT) {
	        return false;
	    } else {
	        double idealBreakoutCoef = (MAX_WING_BREAKOUT + MIN_WING_BREAKOUT) / 2;
	        double coefDispersion = MAX_WING_BREAKOUT - idealBreakoutCoef;
	        
	        wingBreakoutQuality = Math.min(wingBreakoutQuality, Math.abs(wingBreakoutCoeficient - idealBreakoutCoef) / coefDispersion * HUNDRED_PERCENT);
	        return true;
	    }
	}

	@Override
	protected IndicatorInfo createIndicatorInfo() {
		return new IndicatorInfo("DOUBLE_BOTTOM", "Double Bottom Pattern", getDefaultPatternRecognitionGroup(), true, false, false, 1, getOptInputParameterInfos().length, getNumberOfOutputs());
	}

	@Override
	public int getMinPatternPivotPointNumber() {
		return isWholePatternCalculation() ? 5 : 4;
	}
	
	@Override
    public int getMaxPatternPivotPointNumber() {
        return getMinPatternPivotPointNumber();
    }

	@Override
	public IndexValue[] constructBottomAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
		return null;
	}

	@Override
	public IndexValue[] constructUppperAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
		return constructHorizontalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[] {2});
	}
	
	@Override
    public boolean checkEmergingPatternPostTrend(
            double[] cleanPattern,
            double[][] sourceData,
            int dirtyPatterStartIndex,
            int dirtyPatternSize,
            IndexValue[] patternPoints,
            IndexValue[] upperAsymptotePoints,
            IndexValue[] bottomAsymptotePoints
    ) {
        return true;
    }

}
