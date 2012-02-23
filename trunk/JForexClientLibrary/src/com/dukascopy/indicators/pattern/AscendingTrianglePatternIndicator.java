/**
 * The file AscendingTrianglePatternIndicator.java was created on Mar 2, 2010 at 2:17:48 PM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import com.dukascopy.api.indicators.IndicatorInfo;

public class AscendingTrianglePatternIndicator extends AbstractPatternIndicator {

    private boolean isTypeOne = false;
    
	/**
	 * Type 1.                   
	 *                           _    _
	 *       1       3      5   |      |
	 *       /\      /\    /    | \    |
	 *      /  \    /  \  /     |  \   |
	 *     /    \  /    \/      |   6  |
	 *    /      \/      4      |_    _|
	 *   /        2
	 *  /
	 * 0
	 * 
	 * (1; 3; 5) - on horizontal line
	 * (2; 4; 6) - on ascending asymptote
	 * 
	 * Type 2.
	 *                        _     _
	 * 0                     |       |
	 *  \       2     4      |    6  |
	 *   \      /\    /\     |   /   |
	 *    \    /  \  /  \    |  /    |
	 *     \  /    \/    5   |_     _|
	 *      \/      3
	 *       1
	 * 
	 * (2; 4; 6) - on horizontal line
	 * (1; 3; 5) - on ascending asymptote
	 */
	@Override
	public boolean checkPattern(
			double[] cleanPattern, 
			double[] dirtyPattern, 
			int dirtyPatternStartIndex, 
			int dirtyPatternSize
			
	) {
	    resetParallelLineQuality();
	    resetPointsEqualityQuality();
        calculatedPatternQuality = 0;
	    
        double patternSpread = getPatternPriceSpread(cleanPattern);
	    int pointsCount = cleanPattern.length;
	    boolean checkResult = false;
	    
	    if (
    	            cleanPattern[0] < cleanPattern[2] &&
    	            cleanPattern[2] < cleanPattern[4] &&
    	            cleanPattern[1] > cleanPattern[4] &&
    	            
    	            aproxEqual(cleanPattern[1], cleanPattern[3], patternSpread) &&
                    (! isWholePatternCalculation() || aproxEqual(cleanPattern[3], cleanPattern[5], patternSpread)) &&
                    
                    checkAsymptoteCrossIdxQuality(dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, cleanPattern, new int[]{2, 4}, new int[]{1, 3}, 0, false)
	    ) {
	        
	        isTypeOne = true;
	        checkResult = true;
	        
	    }
	    else if (
	                cleanPattern[1] < cleanPattern[3] &&
	                cleanPattern[0] > cleanPattern[3] &&
	                (
	                        ! isWholePatternCalculation() ||
	                        (
	                                cleanPattern[3] < cleanPattern[5] &&
	                                cleanPattern[0] > cleanPattern[5] &&
	                                isPointOnAssymptote(5, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{1, 3})
	                        )
	                ) &&
	                
	                aproxEqual(cleanPattern[2], cleanPattern[4], patternSpread) &&
	                
	                checkAsymptoteCrossIdxQuality(dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, cleanPattern, new int[]{1, 3}, new int[]{2, 4}, 0, false)
	    ) {
	        
	        isTypeOne = false;
	        checkResult = true;
	        
	    }
	    
	    if (!checkResult || pointsCount <= getMinPatternPivotPointNumber()) {
            if (checkResult) {
                calculatedPatternQuality = calculatePatternQuality(pointsCount, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, cleanPattern);
                checkResult = (calculatedPatternQuality >= getPatternQuality());
            }
            return checkResult;
        }
	    
	    // Required part check successful. Perform optional points check.
	    
	    checkResult = checkSeventhPoint(isTypeOne, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, patternSpread);
        if (checkResult) {
            calculatedPatternQuality = calculatePatternQuality(pointsCount, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, cleanPattern);
            checkResult = (calculatedPatternQuality >= getPatternQuality());
        }
        return checkResult;
	}
	
	private boolean checkSeventhPoint(
            boolean isTypeOne,
            double[] cleanPattern, 
            double[] dirtyPattern, 
            int dirtyPatternStartIndex, 
            int dirtyPatternSize,
            double patternSpread
    ) {
        if (
            isTypeOne &&
            cleanPattern[4] < cleanPattern[6] &&
            cleanPattern[5] > cleanPattern[6] &&
            
            isPointOnAssymptote(6, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{2, 4})
        ) {
            return true;
        }
        else if (
            ! isTypeOne &&
            cleanPattern[5] < cleanPattern[6] &&
            
            aproxEqual(cleanPattern[4], cleanPattern[6], patternSpread)
        ) {
            return true;
        }
        
        return false;
    }
	
	
	/**
     * 1) 55% weight for asymptote quality
     * 2) 15% weight for additional point (7th)
     * 3) 25% points for points uniformity
     * 4) 5%  convergence
     */
    private double calculatePatternQuality(
                                int pointsCount,
                                double[] dirtyPattern, 
                                int dirtyStartIndex, 
                                int dirtyPatternSize, 
                                double[] cleanPattern
    ) {
        double additionalPointsQuality = (pointsCount - getMinPatternPivotPointNumber()) * HUNDRED_PERCENT * 0.15;
        double asymptoteQuality = (getParallelLineQuality() + getPointsEqualityQuality()) / 2 * 0.55;
        double gapQuality = checkGapsQuality(dirtyPattern, dirtyStartIndex, dirtyPatternSize, cleanPattern) * 0.25;
        
        int[] first  = (isTypeOne) ? new int[] {2, 4} : new int[] {1, 3};
        int[] second = (isTypeOne) ? new int[] {1, 3} : new int[] {2, 4};
        double convergenceQuality = getAsymptoteCrossIdxQuality(dirtyPattern, dirtyStartIndex, dirtyPatternSize, cleanPattern, first, second, 0, false) * 0.05;
        
        return additionalPointsQuality + asymptoteQuality + gapQuality + convergenceQuality;
    }
	
	

	@Override
	protected IndicatorInfo createIndicatorInfo() {
		return new IndicatorInfo("ASC_TRIANGLE", "Ascending Triangle Pattern", getDefaultPatternRecognitionGroup(), true, false, false, 1, getOptInputParameterInfos().length, getNumberOfOutputs());
	}

	@Override
	public int getMinPatternPivotPointNumber() {
		return isWholePatternCalculation() ? 6 : 5;
	}
	
	@Override
	public int getMaxPatternPivotPointNumber() {
	    return 7;
	}

	@Override
	public IndexValue[] constructBottomAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
	    int[] up   = (isTypeOne) ? new int[] {2, 4} : new int[] {1, 3};
	    int[] down = (isTypeOne) ? new int[] {1, 3} : new int[] {2, 4};
		return constructDiagonalAsymptoteUntilCrossWithHorizontal(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, up, down);
	}

	@Override
	public IndexValue[] constructUppperAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
	) {
	    int[] up   = (isTypeOne) ? new int[] {1, 3} : new int[] {2, 4};
        int[] down = (isTypeOne) ? new int[] {2, 4} : new int[] {1, 3};
		return constructHorizontalAsymptoteUntilCross(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, up, down);
	}
	
	
	@Override
    public boolean checkEmergingPatternPostTrend(
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
