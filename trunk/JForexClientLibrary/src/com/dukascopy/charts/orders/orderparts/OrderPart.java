package com.dukascopy.charts.orders.orderparts;

public abstract interface OrderPart
{
  public abstract void updatePaintTag(int paramInt);

  public abstract int getPaintTag();

  public abstract boolean hitPoint(int paramInt1, int paramInt2);

  public abstract String getOrderGroupId();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.orders.orderparts.OrderPart
 * JD-Core Version:    0.6.0
 */