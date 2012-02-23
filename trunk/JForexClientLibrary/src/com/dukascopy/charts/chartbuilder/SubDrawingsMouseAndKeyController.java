package com.dukascopy.charts.chartbuilder;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public abstract interface SubDrawingsMouseAndKeyController
{
  public abstract void mouseExited(MouseEvent paramMouseEvent);

  public abstract void mouseEntered(MouseEvent paramMouseEvent);

  public abstract byte mouseClicked(MouseEvent paramMouseEvent);

  public abstract void mousePressed(MouseEvent paramMouseEvent);

  public abstract void mouseReleased(MouseEvent paramMouseEvent);

  public abstract byte mouseMoved(MouseEvent paramMouseEvent);

  public abstract void mouseDragged(MouseEvent paramMouseEvent);

  public abstract void mouseWheelMoved(MouseWheelEvent paramMouseWheelEvent);

  public abstract void keyPressed(KeyEvent paramKeyEvent);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.SubDrawingsMouseAndKeyController
 * JD-Core Version:    0.6.0
 */