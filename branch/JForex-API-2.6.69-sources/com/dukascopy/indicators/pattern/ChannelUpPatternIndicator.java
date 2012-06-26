/**
 * The file ChannelUpPatternIndicator.java was created on Mar 4, 2010 at 9:46:43 AM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import com.dukascopy.api.indicators.IndicatorInfo;

public class ChannelUpPatternIndicator extends AbstractChannelPatternIndicator {
	
	/*
	 * Is needed for not to confuse bottom and upper asymptotes
	 */
	private boolean isTypeOne = false;

	/**                   _        _
	 * Type 1.           |       5  |
	 *    	         3   |      /   |
	 *         1    /    |  \  /    |
	 *         /\  /     |   \/     |
	 *        /  \/      |    4     |
	 *       /    2      |_        _|
	 *      0
	 *   
	 * Type 2.          _            _
	 *                 |      4       |
	 *      OR         |      /\      |
	 *          2      |     /  \     |
	 *          /\     |    /    \    |
	 *  0      /  \    |   /      5   |
	 *   \    /    \   |  /           |
	 *    \  /      3  |              |
	 *     \/          |_            _|
	 *     1                  
	 *                  
	 *
	 *  Lines (0; 2) and (1; 3) has to be parallel
	 *  Point 4 is close to (0; 2) line
	 *  Point 5 is close to (1; 3) line
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
					cleanPattern[1] > cleanPattern[2] &&
					cleanPattern[2] < cleanPattern[3] &&
				
					cleanPattern[0] < cleanPattern[2] &&
				
					cleanPattern[1] < cleanPattern[3] &&
				
					areParallel(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{1, 3}, new int[]{0, 2})
				
		) {
			
			isTypeOne = true;
			checkResult = true;
			
		}
		else if (
					cleanPattern[0] > cleanPattern[1] &&
					cleanPattern[2] > cleanPattern[3] &&
				
					cleanPattern[0] < cleanPattern[2] &&
				
					cleanPattern[1] < cleanPattern[3] &&
				
					areParallel(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{1, 3}, new int[]{0, 2})
		) {
			
			isTypeOne = false;
			checkResult = true;
			
		}
		
		if (!checkResult || pointsCount == MIN_POINTS_COUNT) {
		    if (checkResult) {
		        calculatedPatternQuality = calculatePatternQuality(pointsCount, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, cleanPattern);
		        checkResult = (calculatedPatternQuality >= getPatternQuality());
		    }
		    return checkResult;
		}
		
		// Required part check successful. Perform optional points check.
		
		checkResult = checkFifthPoint(isTypeOne, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize);
		if (! checkResult || pointsCount == 5) {
		    if (checkResult) {
		        calculatedPatternQuality = calculatePatternQuality(pointsCount, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, cleanPattern);
		        checkResult = (calculatedPatternQuality >= getPatternQuality());
		    }
		    return checkResult;
		}
		
		checkResult = checkSixthPoint(isTypeOne, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize);
		if (checkResult) {
		    calculatedPatternQuality = calculatePatternQuality(pointsCount, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, cleanPattern);
		    checkResult = (calculatedPatternQuality >= getPatternQuality());
		}
		return checkResult;
	}
	
	private boolean checkFifthPoint(
            	        boolean isTypeOne,
            	        double[] cleanPattern, 
                        double[] dirtyPattern, 
                        int dirtyPatternStartIndex, 
                        int dirtyPatternSize
    ) {
	    if (
	            isTypeOne &&
	            cleanPattern[2] < cleanPattern[4] &&
	            cleanPattern[3] > cleanPattern[4] &&
	            
	            isPointOnAssymptote(4, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{0, 2})
	    ) {
	        return true;
	    }
	    else if (
	            ! isTypeOne &&
	            cleanPattern[2] < cleanPattern[4] &&
	            
	            isPointOnAssymptote(4, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{0, 2})
	    ) {
	        return true;
	    }
	    
	    return false;
	}
	
	private boolean checkSixthPoint(
            	        boolean isTypeOne,
                        double[] cleanPattern, 
                        double[] dirtyPattern, 
                        int dirtyPatternStartIndex, 
                        int dirtyPatternSize
	) {
	    if (
	            isTypeOne &&
	            cleanPattern[3] < cleanPattern[5] &&
	            
	            isPointOnAssymptote(5, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{1, 3})
	    ) {
	        return true;
	    }
	    else if (
	            ! isTypeOne &&
	            cleanPattern[3] < cleanPattern[5] &&
	            cleanPattern[4] > cleanPattern[5] &&
	            
	            isPointOnAssymptote(5, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{1, 3})
	    ) {
	        return true;
	    }
	    
	    return false;
	}


	@Override
	protected IndicatorInfo createIndicatorInfo() {
		return new IndicatorInfo("CHANNEL_UP", "Channel Up Pattern", getDefaultPatternRecognitionGroup(), true, false, false, 1, getOptInputParameterInfos().length, getNumberOfOutputs());
	}

	@Override
	public IndexValue[] constructBottomAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
		int[] asymptote = null;
		
		if (isTypeOne) {
			asymptote = new int[]{0, 2};
		}
		else {
			asymptote = new int[]{1, 3};
		}
		
		return constructDiagonalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, asymptote);
	}

	@Override
	public IndexValue[] constructUppperAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
		int[] asymptote = null;
		
		if (isTypeOne) {
			asymptote = new int[]{1, 3};
		}
		else {
			asymptote = new int[]{0, 2};
		}
		
		return constructDiagonalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, asymptote);
	}

}