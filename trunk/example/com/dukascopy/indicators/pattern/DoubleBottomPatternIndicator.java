/**
 * The file DoubleBottomPatternIndicator.java was created on Mar 2, 2010 at 10:31:41 AM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import com.dukascopy.api.indicators.IndicatorInfo;

public class DoubleBottomPatternIndicator extends AbstractPatternIndicator {
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
		if (
				cleanPattern[0] > cleanPattern[1] && 
				cleanPattern[1] < cleanPattern[2] &&
				cleanPattern[2] > cleanPattern[3] &&
				cleanPattern[2] < cleanPattern[0] &&
				
				aproxEqual(cleanPattern[1], cleanPattern[3], getPatternPriceSpread(cleanPattern)) &&
				(
						!isWholePatternCalculation() ||
						(
								cleanPattern[2] < cleanPattern[4] &&
								cleanPattern[3] < cleanPattern[4]
						)
				)
				
		) {
			return true;
		}
			
		return false;
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
		return constructHorizontalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[] {1, 3});
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
