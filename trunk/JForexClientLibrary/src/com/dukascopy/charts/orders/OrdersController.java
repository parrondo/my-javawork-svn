package com.dukascopy.charts.orders;

import com.dukascopy.api.Instrument;

public abstract interface OrdersController
{
  public abstract void start();

  public abstract void dispose();

  public abstract void changeInstrument(Instrument paramInstrument);

  public abstract void setOrderLineVisible(String paramString, boolean paramBoolean);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.orders.OrdersController
 * JD-Core Version:    0.6.0
 */