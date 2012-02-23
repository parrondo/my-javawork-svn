/**
 * The file RisingWedgePatternIndicator.java was created on Mar 3, 2010 at 5:15:13 PM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import com.dukascopy.api.indicators.IndicatorInfo;

public class RisingWedgePatternIndicator extends AbstractPatternIndicator {
	
    private boolean isTypeOne = false;
    
	/**
	 * Type 1.                   _      _
	 *                          |     6  |
	 *                    4     |    /   |
	 *             2      /\    |   /    |
	 *    0        /\    /  \   |  /     |
	 *     \      /  \  /    5  |_      _|
	 *      \    /    \/
	 *       \  /      3        
	 *        \/                 
	 *        1                  
	 *                             _     _
	 * Type 2.                 5  |       |
	 *                 3      /   |  \    |
	 *        1        /\    /    |   \   |
  	 *        /\      /  \  /     |    6  |
	 *       /  \    /    \/      |_     _|
	 *      /    \  /     4
	 *     /      \/
	 *    /       2  
	 *   /          
	 *  0
	 */
	@Override
	public boolean checkPattern(
			double[] cleanPattern, 
			double[] dirtyPattern, 
			int dirtyPatternStartIndex, 
			int dirtyPatternSize
			
	) {
	    resetParallelLineQuality();
        calculatedPatternQuality = 0;
        
        int pointsCount = cleanPattern.length;
        boolean checkResult = false;
        
        if (
//                cleanPattern[0] < cleanPattern[2] &&
                cleanPattern[1] < cleanPattern[3] &&
                cleanPattern[2] < cleanPattern[4] &&
                cleanPattern[2] > cleanPattern[3] &&
                
                isPointOnAssymptote(4, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{0, 2}) &&
                
                (
                        ! isWholePatternCalculation() ||
                        (
                                cleanPattern[3] < cleanPattern[5] &&
                                cleanPattern[2] > cleanPattern[5] &&
                                isPointOnAssymptote(5, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{1, 3})
                        )
                ) &&
                
                checkAsymptoteCrossIdxQuality(dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, cleanPattern, new int[]{2, 4}, new int[]{1, 3}, dirtyPatternSize*2, true)
        ) {
            
            isTypeOne = true;
            checkResult = true;
            
        }
        else if (
//                cleanPattern[0] < cleanPattern[2] &&
                cleanPattern[1] < cleanPattern[3] &&
                cleanPattern[2] < cleanPattern[4] &&
                cleanPattern[1] > cleanPattern[4] &&
                
                isPointOnAssymptote(4, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{0, 2}) &&
                
                (
                        ! isWholePatternCalculation() ||
                        (
                                cleanPattern[3] < cleanPattern[5] &&
                                isPointOnAssymptote(5, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{1, 3})
                        )
                ) &&
                
                
                checkAsymptoteCrossIdxQuality(dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, cleanPattern, new int[]{1, 3}, new int[]{2, 4}, dirtyPatternSize*2, true)
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
        
        checkResult = checkSeventhPoint(isTypeOne, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize);
        if (checkResult) {
            calculatedPatternQuality = calculatePatternQuality(pointsCount, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, cleanPattern);
            checkResult = (calculatedPatternQuality >= getPatternQuality());
        }
		
		return checkResult;
	}
	
	private boolean checkSeventhPoint(boolean isTypeOne,
            double[] cleanPattern, 
            double[] dirtyPattern, 
            int dirtyPatternStartIndex, 
            int dirtyPatternSize
    ) {
	    if (
            isTypeOne &&
            cleanPattern[4] < cleanPattern[6] &&
            
            isPointOnAssymptote(6, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{2, 4})
        ) {
            return true;
        }
        else if (
            ! isTypeOne &&
            cleanPattern[4] < cleanPattern[6] &&
            cleanPattern[5] > cleanPattern[6] &&
            
            isPointOnAssymptote(6, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{2, 4})
        ) {
            return true;
        }
        
        return false;
	}
	
	/**
     * 1) 65% weight for asymptote quality
     * 2) 15% weight for additional point (7th)
     * 3) 20% points for points uniformity
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
        double asymptoteQuality = getParallelLineQuality() * 0.65;
        double gapQuality = checkGapsQuality(dirtyPattern, dirtyStartIndex, dirtyPatternSize, cleanPattern) * 0.2;
        
        int[] first  = (isTypeOne) ? new int[] {2, 4} : new int[] {1, 3};
        int[] second = (isTypeOne) ? new int[] {1, 3} : new int[] {2, 4};
        double convergenceQuality = getAsymptoteCrossIdxQuality(dirtyPattern, dirtyStartIndex, dirtyPatternSize, cleanPattern, first, second, dirtyPatternSize*2, true) * 0.05;
        
        return additionalPointsQuality + asymptoteQuality + gapQuality + convergenceQuality;
    }
    
	
	

	@Override
	protected IndicatorInfo createIndicatorInfo() {
		return new IndicatorInfo("RISING_WEDGE", "Rising Wedge Pattern", getDefaultPatternRecognitionGroup(), true, false, false, 1, getOptInputParameterInfos().length, getNumberOfOutputs());
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
	    int[] asymptote = (isTypeOne) ? new int[] {1, 3} : new int[] {2, 4};
		return constructDiagonalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, asymptote);
	}

	@Override
	public IndexValue[] constructUppperAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
	    int[] asymptote = (isTypeOne) ? new int[] {2, 4} : new int[] {1, 3};
		return constructDiagonalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, asymptote);
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
