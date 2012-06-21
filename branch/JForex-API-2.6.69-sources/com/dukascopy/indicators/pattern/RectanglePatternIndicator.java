/**
 * The file Rectangle.java was created on Mar 2, 2010 at 10:44:14 AM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import com.dukascopy.api.indicators.IndicatorInfo;

public class RectanglePatternIndicator extends AbstractPatternIndicator {
	
    /*
     * Is needed for not to confuse bottom and upper asymptotes
     */
    private boolean isTypeOne = false;
    
	/**
	 * Type 1.
	 * 0              _       _
	 *  \   2   4    |   6     |
	 *   \  /\  /\   |   /\    |
	 *    \/  \/  \  |  /  \   |
	 * 	  1	  3    5 |_     7 _|
	 *              
	 * Type 2.
     *                   _    _
     *      1   3    5  |    7 |
     *      /\  /\  /   | \  / |
     *     /  \/  \/    |  \/  |
     *    /   2   4     |_ 6  _|
     *   0
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
        calculatedPatternQuality = 0;
        
        double patternSpread = getPatternPriceSpread(cleanPattern);
        int pointsCount = cleanPattern.length;
        boolean checkResult = false;
	    
        if (pointsCount == 7) {
            return false; // allowed only 6 and 8 points combinations 
        }
        
		if (
				cleanPattern[0] > cleanPattern[2] &&
				cleanPattern[1] < cleanPattern[2] &&
				cleanPattern[2] > cleanPattern[3] &&
				cleanPattern[3] < cleanPattern[4] &&
				
				cleanPattern[0] - cleanPattern[2] < (cleanPattern[2] - cleanPattern[1]) * 2 &&
				
				aproxEqual(cleanPattern[1], cleanPattern[3], patternSpread) &&
				aproxEqual(cleanPattern[2], cleanPattern[4], patternSpread) &&
				(
						!isWholePatternCalculation() ||
						(
						        pointsCount == 6 &&
								cleanPattern[5] < cleanPattern[3] &&
								cleanPattern[3] - cleanPattern[5] < (cleanPattern[4] - cleanPattern[3]) * 2
						) ||
						(
						        pointsCount == 8 &&
						        aproxEqual(cleanPattern[3], cleanPattern[5], patternSpread)
						)
				)
				
		) {
		    
		    isTypeOne = true;
            checkResult = true;
			
		}
		else if (
		        cleanPattern[0] < cleanPattern[2] &&
                cleanPattern[1] > cleanPattern[2] &&
                cleanPattern[2] < cleanPattern[3] &&
                cleanPattern[3] > cleanPattern[4] &&
                
                cleanPattern[2] - cleanPattern[0] < (cleanPattern[1] - cleanPattern[2]) * 2 &&
                
                aproxEqual(cleanPattern[1], cleanPattern[3], patternSpread) &&
                aproxEqual(cleanPattern[2], cleanPattern[4], patternSpread) &&
                (
                        !isWholePatternCalculation() ||
                        (
                                pointsCount == 6 &&
                                cleanPattern[5] > cleanPattern[3] &&
                                cleanPattern[5] - cleanPattern[3] < (cleanPattern[3] - cleanPattern[4]) * 2
                        ) ||
                        (
                                pointsCount == 8 &&
                                aproxEqual(cleanPattern[3], cleanPattern[5], patternSpread)
                        )
                )
		
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
        
        checkResult = checkSeventhAndEighthPoint(isTypeOne, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, patternSpread);
        if (checkResult) {
            calculatedPatternQuality = calculatePatternQuality(pointsCount, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, cleanPattern);
            checkResult = (calculatedPatternQuality >= getPatternQuality());
        }
        return checkResult;
	}
	
	private boolean checkSeventhAndEighthPoint(
            boolean isTypeOne,
            double[] cleanPattern, 
            double[] dirtyPattern, 
            int dirtyPatternStartIndex, 
            int dirtyPatternSize,
            double patternSpread
    ) {
        if (
            isTypeOne &&
            aproxEqual(cleanPattern[4], cleanPattern[6], patternSpread) &&
            cleanPattern[5] > cleanPattern[7] &&
            cleanPattern[5] - cleanPattern[7] < (cleanPattern[6] - cleanPattern[5]) * 2
        ) {
            return true;
        }
        else if (
            ! isTypeOne &&
            aproxEqual(cleanPattern[4], cleanPattern[6], patternSpread) &&
            cleanPattern[5] < cleanPattern[7] &&
            cleanPattern[7] - cleanPattern[5] < (cleanPattern[5] - cleanPattern[6]) * 2
        ) {
            return true;
        }
        
        return false;
    }
	
	
	/**
     * 1) 70% weight for asymptote quality
     * 2) 10% weight for additional points (7th and 8th)
     * 3) 20% points for points uniformity
     */
    private double calculatePatternQuality(
                                int pointsCount,
                                double[] dirtyPattern, 
                                int dirtyStartIndex, 
                                int dirtyPatternSize, 
                                double[] cleanPattern
    ) {
        double additionalPointsQuality = (pointsCount > 6)  ? HUNDRED_PERCENT * 0.1
                                                            : 0;
        double asymptoteQuality = getPointsEqualityQuality() * 0.7;
        double gapQuality = checkGapsQuality(dirtyPattern, dirtyStartIndex, dirtyPatternSize, cleanPattern) * 0.2;
        
        return additionalPointsQuality + asymptoteQuality + gapQuality;
    }
	

	@Override
	protected IndicatorInfo createIndicatorInfo() {
		return new IndicatorInfo("RECTANGLE", "Rectangle Pattern", getDefaultPatternRecognitionGroup(), true, false, false, 1, getOptInputParameterInfos().length, getNumberOfOutputs());
	}

	@Override
	public int getMinPatternPivotPointNumber() {
		return isWholePatternCalculation() ? 6 : 5;
	}
	
	@Override
    public int getMaxPatternPivotPointNumber() {
        return 8;
    }

	@Override
	public IndexValue[] constructBottomAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
	    int[] asymptote = (isTypeOne)   ? new int[] {1, 3}
	                                    : new int[] {2, 4};
		return constructHorizontalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, asymptote);
	}

	@Override
	public IndexValue[] constructUppperAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
	    int[] asymptote = (isTypeOne)   ? new int[] {2, 4}
                                        : new int[] {1, 3};
		return constructHorizontalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, asymptote);
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
