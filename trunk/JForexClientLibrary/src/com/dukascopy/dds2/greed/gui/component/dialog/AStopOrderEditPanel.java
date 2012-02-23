package com.dukascopy.dds2.greed.gui.component.dialog;

import com.dukascopy.dds2.greed.model.AccountInfoListener;
import com.dukascopy.dds2.greed.model.MarketStateWrapperListener;
import com.dukascopy.dds2.greed.util.QuickieOrderSupport;
import javax.swing.JPanel;

public abstract class AStopOrderEditPanel extends JPanel
  implements QuickieOrderSupport, MarketStateWrapperListener, AccountInfoListener
{
  abstract void build();

  abstract void submitButtonPressed();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.AStopOrderEditPanel
 * JD-Core Version:    0.6.0
 */