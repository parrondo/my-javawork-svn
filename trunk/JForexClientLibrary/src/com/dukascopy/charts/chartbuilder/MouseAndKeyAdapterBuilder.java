package com.dukascopy.charts.chartbuilder;

import com.dukascopy.charts.mouseandkeyadaptors.ChartsMouseAndKeyAdapter;
import com.dukascopy.charts.view.paintingtechnic.InvalidationContent;
import java.awt.event.MouseAdapter;
import javax.swing.JComponent;

public abstract interface MouseAndKeyAdapterBuilder
{
  public abstract ChartsMouseAndKeyAdapter createMouseAndKeyAdapterForMain(InvalidationContent paramInvalidationContent);

  public abstract ChartsMouseAndKeyAdapter createMouseAndKeyAdapterForSub(InvalidationContent paramInvalidationContent, SubIndicatorGroup paramSubIndicatorGroup);

  public abstract MouseAdapter getMouseAndKeyAdapterForDivisionPanel(JComponent paramJComponent1, JComponent paramJComponent2);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.MouseAndKeyAdapterBuilder
 * JD-Core Version:    0.6.0
 */