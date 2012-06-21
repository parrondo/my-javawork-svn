/**
 * The file AbstractChannelPatternIndicator.java was created on Mar 4, 2010 at 10:49:15 AM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;



public abstract class AbstractChannelPatternIndicator extends AbstractPatternIndicator {

    protected static final int MIN_POINTS_COUNT = 4;
    protected static final int MAX_POINTS_COUNT = 6;
    
    
//	@Override
//	protected OptInputParameterInfo[] createOptInputParameterInfo() {
//		return new OptInputParameterInfo[] {
//				createNumberOfBarsOnSidesStartCountInfo(),
//				createBarsOnSidesIncrementCountInfo(),
//				createCenteringInaccuracy(),
//				createParallelityInaccuracy()	
//			};	
//	}
//
//	protected OptInputParameterInfo createParallelityInaccuracy() {
//		return new OptInputParameterInfo("Parallelity Inaccuracy (1%)", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(getEqualityInaccuracy(), 0.1d, 100d, 0.1d, 1));
//	}

    /**
     * 1) 60% weight for asymptote quality
     * 2) 30% weight for additional points (5th and 6th)
     * 3) 10% points for points uniformity
     */
    protected double calculatePatternQuality(
                                int pointsCount,
                                double[] dirtyPattern, 
                                int dirtyStartIndex, 
                                int dirtyPatternSize, 
                                double[] cleanPattern
    ) {
        double pointsGapQuality = (pointsCount - 4) * 15;
        double asymptoteQuality = getParallelLineQuality() * 0.6;
        double gapQuality = checkGapsQuality(dirtyPattern, dirtyStartIndex, dirtyPatternSize, cleanPattern) * 0.1;
        
        return pointsGapQuality + asymptoteQuality + gapQuality;
    }
    
    @Override
    public int getMinPatternPivotPointNumber() {
        return MIN_POINTS_COUNT;
    }
    
    @Override
    public int getMaxPatternPivotPointNumber() {
        return MAX_POINTS_COUNT;
    }
    
    
    @Override
    public boolean checkEmergingPatternPostTrend(
            double[] cleanPattern,
            double[][] sourceData,
            int dirtyPatternStartIndex,
            int dirtyPatternSize,
            IndexValue[] patternPoints,
            IndexValue[] upperAsymptotePoints,
            IndexValue[] bottomAsymptotePoints
    ) {
        double sigma = getPatternPriceSpread(cleanPattern) * 0.05;
        boolean check = checkCandlePostTrendIsUnderUpperAsymptote(sourceData, dirtyPatternStartIndex, dirtyPatternSize, patternPoints, upperAsymptotePoints, sigma);
        check &= checkCandlePostTrendIsOverBottomAsymptote(sourceData, dirtyPatternStartIndex, dirtyPatternSize, patternPoints, bottomAsymptotePoints, sigma);
        return check;
    }
    
}
