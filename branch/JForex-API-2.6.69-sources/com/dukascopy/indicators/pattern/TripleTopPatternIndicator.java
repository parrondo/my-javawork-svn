/**
 * The file TripleTop.java was created on Mar 2, 2010 at 11:23:32 AM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import com.dukascopy.api.indicators.IndicatorInfo;

public class TripleTopPatternIndicator extends AbstractPatternIndicator {

	/**
	 * 
	 *     1   3   5
	 *     /\  /\  /\
	 *    /  \/  \/  \
	 *   /   2   4    \
	 *  0              6
	 * 
	 */
	@Override
	public boolean checkPattern(
			double[] cleanPattern, 
			double[] dirtyPattern, 
			int dirtyPatternStartIndex, 
			int dirtyPatternSize
			
	) {
	    double patternSpread = getPatternPriceSpread(cleanPattern);
	    
		if (
				cleanPattern[0] < cleanPattern[1] && 
				cleanPattern[1] > cleanPattern[2] &&
				cleanPattern[2] < cleanPattern[3] &&
				cleanPattern[3] > cleanPattern[4] &&
				cleanPattern[4] < cleanPattern[5] &&
				cleanPattern[1] > cleanPattern[4] &&
				
				cleanPattern[3] > cleanPattern[0] &&
				
				cleanPattern[5] > cleanPattern[2] &&
				cleanPattern[5] > cleanPattern[0] &&
				
				cleanPattern[0] < cleanPattern[2] &&
				
				aproxEqualTriple(cleanPattern[1], cleanPattern[3], cleanPattern[5], patternSpread) &&
				aproxEqual(cleanPattern[2], cleanPattern[4], patternSpread) &&
				
				(
						!isWholePatternCalculation() ||
						(
								cleanPattern[5] > cleanPattern[6] &&
								cleanPattern[1] > cleanPattern[6] &&
								cleanPattern[3] > cleanPattern[6] &&
								cleanPattern[6] < cleanPattern[4]
						)
				)
				
		) {
			
			return true;
			
		}
		return false;
	}

	@Override
	protected IndicatorInfo createIndicatorInfo() {
		return new IndicatorInfo("TRIPLE_TOP", "Triple Top Pattern", getDefaultPatternRecognitionGroup(), true, false, false, 1, getOptInputParameterInfos().length, getNumberOfOutputs());
	}

	@Override
	public int getMinPatternPivotPointNumber() {
		return isWholePatternCalculation() ? 7 : 6;
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
		return constructHorizontalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[] {2, 4});
	}

	@Override
	public IndexValue[] constructUppperAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
		return constructHorizontalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[] {1, 3, 5});
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
