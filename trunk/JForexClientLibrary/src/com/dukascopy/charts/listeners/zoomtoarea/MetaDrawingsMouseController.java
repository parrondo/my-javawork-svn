package com.dukascopy.charts.listeners.zoomtoarea;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract interface MetaDrawingsMouseController
{
  public abstract void mousePressed(MouseEvent paramMouseEvent);

  public abstract void mouseReleased(MouseEvent paramMouseEvent);

  public abstract void mouseDragged(MouseEvent paramMouseEvent);

  public abstract void keyPressed(KeyEvent paramKeyEvent);

  public abstract void focusLost(FocusEvent paramFocusEvent);

  public abstract void focusGained(FocusEvent paramFocusEvent);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.listeners.zoomtoarea.MetaDrawingsMouseController
 * JD-Core Version:    0.6.0
 */