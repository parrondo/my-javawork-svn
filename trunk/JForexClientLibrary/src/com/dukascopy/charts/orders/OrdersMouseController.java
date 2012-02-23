package com.dukascopy.charts.orders;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public abstract interface OrdersMouseController
{
  public abstract void keyPressed(KeyEvent paramKeyEvent);

  public abstract void mouseReleased(MouseEvent paramMouseEvent);

  public abstract byte mouseClicked(MouseEvent paramMouseEvent);

  public abstract void mousePressed(MouseEvent paramMouseEvent);

  public abstract byte mouseMoved(MouseEvent paramMouseEvent);

  public abstract void mouseDragged(MouseEvent paramMouseEvent);

  public abstract void mouseWheelMoved(MouseWheelEvent paramMouseWheelEvent);

  public abstract void focusLost(FocusEvent paramFocusEvent);

  public abstract void focusGained(FocusEvent paramFocusEvent);

  public abstract void unselectSeletedOrders();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.orders.OrdersMouseController
 * JD-Core Version:    0.6.0
 */