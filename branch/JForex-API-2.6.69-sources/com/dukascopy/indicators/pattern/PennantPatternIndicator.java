/**
 * The file PennantPatternIndicator.java was created on Mar 3, 2010 at 2:57:14 PM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import com.dukascopy.api.indicators.IndicatorInfo;

public class PennantPatternIndicator extends AbstractPatternIndicator {

	
	/**
	 *
	 *                        5
	 *                       /
	 *         1            /
	 *         /\     3    /
	 *        /  \    /\  /
	 *       /    \  /  \/
	 *      /      \/   4
	 *     /       2
	 *    /
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
		if (
				
				cleanPattern[0] < cleanPattern[1] &&
				cleanPattern[1] > cleanPattern[2] &&
				cleanPattern[2] < cleanPattern[3] &&
				cleanPattern[3] > cleanPattern[4] &&
				
				cleanPattern[0] < cleanPattern[2] &&
				cleanPattern[0] < cleanPattern[4] &&
				cleanPattern[2] < cleanPattern[4] &&
				
				cleanPattern[3] < cleanPattern[1] &&

				willAsymptotesCrossUntilPatternEnd(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{2, 4}, new int[]{1, 3}) &&
				
				(
						!isWholePatternCalculation() ||
						(
								cleanPattern[5] > cleanPattern[3] &&
								cleanPattern[5] > cleanPattern[1] &&
								cleanPattern[4] < cleanPattern[5]
						)
				)
				
		) {
			
			return true;
			
		}

		return false;
		
	}

	@Override
	protected IndicatorInfo createIndicatorInfo() {
		return new IndicatorInfo("PENNANT", "Pennant Pattern", getDefaultPatternRecognitionGroup(), true, false, false, 1, getOptInputParameterInfos().length, getNumberOfOutputs());
	}

	@Override
	public int getMinPatternPivotPointNumber() {
		return isWholePatternCalculation() ? 6 : 5;
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
		return constructDiagonalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[]{2, 4});
	}

	@Override
	public IndexValue[] constructUppperAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
		return constructDiagonalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[]{1, 3});
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
