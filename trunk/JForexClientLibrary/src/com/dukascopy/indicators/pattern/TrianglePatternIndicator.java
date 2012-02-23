/**
 * The file TrianglePatternIndicator.java was created on Mar 3, 2010 at 2:08:22 PM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import com.dukascopy.api.indicators.IndicatorInfo;

public class TrianglePatternIndicator extends AbstractPatternIndicator {
	
    private boolean isTypeOne = false;
    
	/**
	 * Type 1.
	 *   
	 * 0
	 * \            2
	 *  \          /\       4      _     _
	 *   \        /  \      /\    |    6  |
	 *    \      /    \    /  \   |   /   |
	 *     \    /      \  /    \  |  /    |
	 *      \  /        \/     5  |_     _|
	 *       \/         3 
	 *       1
	 *                       
	 *                       
	 *  Type 2.
	 *             
	 *       1       
	 *       /\         3           _    _
	 *      /  \        /\      5  |      |
	 *     /    \      /  \    /   | \    |
	 *    /      \    /    \  /    |  \   |
	 *   /        \  /      \/     |   6  |
	 *  /          \/       4      |_    _|
	 * /           2      
	 * 0            
	 *               
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
					cleanPattern[0] > cleanPattern[2] &&
					cleanPattern[1] < cleanPattern[3] &&
					cleanPattern[2] > cleanPattern[4] &&
					cleanPattern[3] < cleanPattern[4] &&
					
					isPointOnAssymptote(4, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{0, 2}) &&
					
					(
							!isWholePatternCalculation() ||
							(
							        cleanPattern[4] > cleanPattern[5] &&
							        cleanPattern[3] < cleanPattern[5] &&
							        isPointOnAssymptote(5, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{1, 3})
							)
					)
				
		) {
            
            isTypeOne = true;
            checkResult = true;
            
        }
        else if (
					cleanPattern[0] < cleanPattern[2] &&
					cleanPattern[1] > cleanPattern[3] &&
					cleanPattern[2] < cleanPattern[4] &&
					cleanPattern[3] > cleanPattern[4] &&
			
					isPointOnAssymptote(4, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{0, 2}) &&
					
					(
							!isWholePatternCalculation() ||
							(
							        cleanPattern[4] < cleanPattern[5] &&
							        cleanPattern[3] > cleanPattern[5] &&
							        isPointOnAssymptote(5, cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{1, 3})
							)
					)
		) {
            
            isTypeOne = true;
            checkResult = true;
            
        }
		
		
		//////////
//			boolean willCross = willAsymptotesCrossForUnitsCount(
//					cleanPattern,
//					dirtyPattern,
//					dirtyPatternStartIndex,
//					dirtyPatternSize,
//					new int[]{2, 4},
//					new int[]{1, 3},
//					BARS_FORWARD_UNTIL_CROSS
//			);
//			
//			if (!willCross) {
//				return false;
//			}
//
//			/*
//			 * Asymptotes cross point must be later than the last line cross with one of two asymptote
//			 */
//			int asymptotesCrossIndex = (int)calculateCrossIndex(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{2, 4}, new int[]{1, 3});
//			int firstAsymptoteLastLineCrossIndex = (int)calculateCrossIndex(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{2, 4}, new int[]{3, 4});
//			int secondAsymptoteLastLineCrossIndex = (int)calculateCrossIndex(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{1, 3}, new int[]{3, 4});
//			
//			if (
//					asymptotesCrossIndex > firstAsymptoteLastLineCrossIndex &&
//					asymptotesCrossIndex > secondAsymptoteLastLineCrossIndex
//			) {
//				return true;
//			}
//		}
//		return false
		
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
            cleanPattern[4] > cleanPattern[6] &&
            cleanPattern[5] < cleanPattern[6] &&
            
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
     * 2) 5% weight for additional point (7th)
     * 3) 20% points for points uniformity
     * 4) 10% convergence
     */
    private double calculatePatternQuality(
                                int pointsCount,
                                double[] dirtyPattern, 
                                int dirtyStartIndex, 
                                int dirtyPatternSize, 
                                double[] cleanPattern
    ) {
        double additionalPointsQuality = (pointsCount - getMinPatternPivotPointNumber()) * HUNDRED_PERCENT * 0.05;
        double asymptoteQuality = getParallelLineQuality() * 0.65;
        double gapQuality = checkGapsQuality(dirtyPattern, dirtyStartIndex, dirtyPatternSize, cleanPattern) * 0.2;
        
        int[] first  = (isTypeOne) ? new int[] {2, 4} : new int[] {1, 3};
        int[] second = (isTypeOne) ? new int[] {1, 3} : new int[] {2, 4};
        double convergenceQuality = getAsymptoteCrossIdxQuality(dirtyPattern, dirtyStartIndex, dirtyPatternSize, cleanPattern, first, second, 0, false) * 0.1;
        
        return additionalPointsQuality + asymptoteQuality + gapQuality + convergenceQuality;
    }
	
	
	
	
	@Override
	protected IndicatorInfo createIndicatorInfo() {
		return new IndicatorInfo("TRIANGLE", "Triangle Pattern", getDefaultPatternRecognitionGroup(), true, false, false, 1, getOptInputParameterInfos().length, getNumberOfOutputs());
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
		return constructDiagonalAsymptoteUntilCross(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[]{1, 3}, new int[]{2, 4});
	}

	@Override
	public IndexValue[] constructUppperAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
		return constructDiagonalAsymptoteUntilCross(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[]{2, 4}, new int[]{1, 3}	);
	}
	
	@Override
	protected void drawPatternLine(
			IndexValue[] indexValues,
			int currentIndex,
			Graphics2D g,
			int x1,
			int y1,
			int x2,
			int y2
	) {
		if (indexValues.length > 2) {
			if (currentIndex <= 1) {
				Point2D point = continueLine(x1, y1, x2, y2, 15);
				g.drawLine((int)point.getX(), (int)point.getY(), x1, y1);
			}
			else if (isWholePatternCalculation() && currentIndex > indexValues.length - 2) {
				Point2D point = continueLine(x2, y2, x1, y1, 15);
				g.drawLine((int)point.getX(), (int)point.getY(), x2, y2);
			}
			else {
				super.drawPatternLine(indexValues, currentIndex, g, x1, y1, x2, y2);
			}
		}
		else {
			super.drawPatternLine(indexValues, currentIndex, g, x1, y1, x2, y2);
		}
	}

	private Point2D continueLine(int x1, int y1, int x2, int y2, int segmentLength) {
		Line2D line1 = new Line2D.Double(x1, y1, x2, y2);
		Line2D line2 = new Line2D.Double(x2, y2, 2 * x2, y2);

		double angle = angleBetween2LinesRadians(line1, line2);
		
		if (x1 < 0 || x2 < 0) {
			angle = Math.PI + angle;
		}
		
		double dx = Math.cos(angle) * segmentLength;
		double dy = Math.sin(angle) * segmentLength;
		Point2D point = new Point2D.Double(x1 + dx, y1 + dy);
		
		return point;
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
