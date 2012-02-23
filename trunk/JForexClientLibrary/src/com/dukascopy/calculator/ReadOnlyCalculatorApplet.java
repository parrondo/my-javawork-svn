package com.dukascopy.calculator;

import com.dukascopy.calculator.complex.Complex;
import com.dukascopy.calculator.function.Mean;
import com.dukascopy.calculator.function.PopStDev;
import com.dukascopy.calculator.function.StDev;
import java.awt.Insets;
import javax.swing.JFrame;

public abstract interface ReadOnlyCalculatorApplet
{
  public abstract DisplayPanel displayPanel();

  public abstract int displayHeight(int paramInt);

  public abstract int buttonHeight(int paramInt);

  public abstract int buttonWidth(int paramInt);

  public abstract int strutSize(int paramInt);

  public abstract int displayHeight();

  public abstract int buttonHeight();

  public abstract int buttonWidth();

  public abstract int strutSize();

  public abstract int minSize();

  public abstract float buttonTextSize();

  public abstract float entryTextSize();

  public abstract float displayTextSize();

  public abstract float extraTextSize();

  public abstract boolean getOn();

  public abstract boolean getShift();

  public abstract Parser getParser();

  public abstract OObject getValue();

  public abstract OObject getMemory();

  public abstract AngleType getAngleType();

  public abstract boolean getStat();

  public abstract int getMode();

  public abstract Mean statMean();

  public abstract Complex statSumSquares();

  public abstract StDev statSampleStDev();

  public abstract PopStDev statPopulationStDev();

  public abstract JFrame frame();

  public abstract Base getBase();

  public abstract Notation getNotation();

  public abstract Insets getFrameInsets();

  public abstract int graphHeight();

  public abstract int getSizesSize();

  public abstract int getMinSize();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.calculator.ReadOnlyCalculatorApplet
 * JD-Core Version:    0.6.0
 */