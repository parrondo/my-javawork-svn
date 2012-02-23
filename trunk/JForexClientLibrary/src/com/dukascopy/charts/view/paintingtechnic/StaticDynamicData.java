package com.dukascopy.charts.view.paintingtechnic;

import com.dukascopy.charts.view.staticdynamicdata.IDisplayableDataPart;
import com.dukascopy.charts.view.staticdynamicdata.IDisplayableDataPart.TYPE;
import java.awt.Graphics;
import javax.swing.JComponent;

public abstract interface StaticDynamicData
{
  public abstract void drawDynamicData(Graphics paramGraphics, JComponent paramJComponent);

  public abstract void drawStaticData(Graphics paramGraphics, JComponent paramJComponent);

  public abstract void addDisplayableDataPart(IDisplayableDataPart.TYPE paramTYPE, IDisplayableDataPart paramIDisplayableDataPart);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.paintingtechnic.StaticDynamicData
 * JD-Core Version:    0.6.0
 */