/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

public interface IGannAnglesChartObject extends IChartDependentChartObject {
	
	double getPipsPerBar();
	void setPipsPerBar(double pipsPerBar);
}
