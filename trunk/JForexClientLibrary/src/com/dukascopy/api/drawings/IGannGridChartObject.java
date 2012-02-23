package com.dukascopy.api.drawings;

public interface IGannGridChartObject extends IChartDependentChartObject {
    
    double getPipsPerBar();
    void setPipsPerBar(double pipsPerBar);
    
    int getCellWidthInBars();
    void setCellWidthInBars(int cellWidthInBars);
}
