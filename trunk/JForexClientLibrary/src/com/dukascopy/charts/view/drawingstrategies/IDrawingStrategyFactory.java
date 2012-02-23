package com.dukascopy.charts.view.drawingstrategies;

import com.dukascopy.charts.chartbuilder.ChartState;
import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
import com.dukascopy.charts.view.displayabledatapart.IDrawingStrategy;
import com.dukascopy.charts.view.displayabledatapart.IOrdersDrawingStrategy;

public abstract interface IDrawingStrategyFactory
{
  public abstract IDrawingStrategy createRawDrawingStrategy();

  public abstract IOrdersDrawingStrategy createOrdersDrawingStrategy();

  public abstract IDrawingStrategy createIndicatorsDrawingStrategy();

  public abstract IDrawingStrategy createMainChartPanelMetaDrawingsDrawingStrategy();

  public abstract IDrawingStrategy createMainAxisYIndicatorValueLabelDrawingStrategy();

  public abstract IDrawingStrategy createMainAxisYPanelMovableLabelDrawingStrategy();

  public abstract IDrawingStrategy createMainAxisYPanelGridDrawingStrategy();

  public abstract IDrawingStrategy createSubIndicatorsDrawingStrategy(SubIndicatorGroup paramSubIndicatorGroup);

  public abstract IDrawingStrategy createSubAxisYPanelGridDrawingStrategy(SubIndicatorGroup paramSubIndicatorGroup);

  public abstract IDrawingStrategy createSubAxisYPanelMovableLabelDrawingStrategy(SubIndicatorGroup paramSubIndicatorGroup);

  public abstract IDrawingStrategy createSubAxisYIndicatorValueLabelDrawingStrategy(SubIndicatorGroup paramSubIndicatorGroup);

  public abstract IDrawingStrategy createCommonAxisXPanelGridDrawingStrategy();

  public abstract IDrawingStrategy createCommonAxisXPanelMovableLabelDrawingStrategy();

  public abstract IDrawingStrategy createMainChartGridDrawingStrategy();

  public abstract IDrawingStrategy createMainChartPeriodSeparatorsDrawingStrategy();

  public abstract IDrawingStrategy createSubChartPeriodSeparatorsDrawingStrategy();

  public abstract IDrawingStrategy createSubIndicatorsInfoDrawingStrategy(SubIndicatorGroup paramSubIndicatorGroup, ChartState paramChartState);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.IDrawingStrategyFactory
 * JD-Core Version:    0.6.0
 */