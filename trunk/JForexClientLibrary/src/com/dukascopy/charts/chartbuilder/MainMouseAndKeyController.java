package com.dukascopy.charts.chartbuilder;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public abstract interface MainMouseAndKeyController
{
  public abstract void keyPressed(KeyEvent paramKeyEvent);

  public abstract void mouseClicked(MouseEvent paramMouseEvent);

  public abstract void mouseWheelMoved(MouseWheelEvent paramMouseWheelEvent);

  public abstract void mousePressed(MouseEvent paramMouseEvent);

  public abstract void mouseReleased(MouseEvent paramMouseEvent);

  public abstract void mouseDragged(MouseEvent paramMouseEvent);

  public abstract void focusLost(FocusEvent paramFocusEvent);

  public abstract void focusGained(FocusEvent paramFocusEvent);

  public abstract void mouseMoved(MouseEvent paramMouseEvent);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.MainMouseAndKeyController
 * JD-Core Version:    0.6.0
 */