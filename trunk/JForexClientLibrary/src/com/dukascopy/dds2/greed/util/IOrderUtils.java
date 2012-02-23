package com.dukascopy.dds2.greed.util;

import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.Instrument;
import com.dukascopy.charts.persistence.ChartBean;
import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

public abstract interface IOrderUtils
{
  public abstract boolean cancelOrder(String paramString);

  public abstract void editOrder(String paramString, ActionListener paramActionListener, ChartBean paramChartBean);

  public abstract void editOrder(String paramString, double paramDouble, ActionListener paramActionListener, ChartBean paramChartBean);

  public abstract void addStopLoss(String paramString1, String paramString2);

  public abstract void addTakeProfit(String paramString1, String paramString2);

  public abstract void closeOrder(String paramString);

  public abstract void condCloseOrder(String paramString);

  public abstract void createNewOrder(Window paramWindow, Instrument paramInstrument, IEngine.OrderCommand paramOrderCommand, double paramDouble, Integer paramInteger);

  public abstract void selectGroupIds(List<String> paramList);

  public abstract AccountInfoMessage getAccountInfo();

  public abstract void orderChangePreview(ChartBean paramChartBean, String paramString1, BigDecimal paramBigDecimal, String paramString2, Color paramColor, Stroke paramStroke);

  public abstract void cancelOrderChangePreview(ChartBean paramChartBean, String paramString);

  public abstract void setOrderLinesVisible(ChartBean paramChartBean, String paramString, boolean paramBoolean);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.IOrderUtils
 * JD-Core Version:    0.6.0
 */