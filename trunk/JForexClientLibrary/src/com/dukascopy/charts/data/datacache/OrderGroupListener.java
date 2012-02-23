package com.dukascopy.charts.data.datacache;

import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
import com.dukascopy.transport.common.msg.group.OrderMessage;
import com.dukascopy.transport.common.msg.request.MergePositionsMessage;

public abstract interface OrderGroupListener
{
  public abstract void updateOrderGroup(OrderGroupMessage paramOrderGroupMessage);

  public abstract void updateOrder(OrderMessage paramOrderMessage);

  public abstract void groupsMerged(MergePositionsMessage paramMergePositionsMessage);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.OrderGroupListener
 * JD-Core Version:    0.6.0
 */