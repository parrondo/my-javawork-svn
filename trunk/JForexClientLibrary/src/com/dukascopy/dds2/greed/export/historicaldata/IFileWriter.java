package com.dukascopy.dds2.greed.export.historicaldata;

import com.dukascopy.api.IBar;
import com.dukascopy.api.ITick;
import com.dukascopy.api.feed.IPointAndFigure;
import com.dukascopy.api.feed.IRangeBar;
import com.dukascopy.api.feed.ITickBar;
import java.io.IOException;

public abstract interface IFileWriter
{
  public abstract void writeHeader()
    throws IOException;

  public abstract void writeTickRateInfo(ITick paramITick)
    throws IOException;

  public abstract void writeCandleRateInfo(IBar paramIBar)
    throws IOException;

  public abstract void writeTickBarInfo(ITickBar paramITickBar)
    throws IOException;

  public abstract void writePriceRangeInfo(IRangeBar paramIRangeBar)
    throws IOException;

  public abstract void writePointAndFigureInfo(IPointAndFigure paramIPointAndFigure)
    throws IOException;

  public abstract void close()
    throws IOException;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.export.historicaldata.IFileWriter
 * JD-Core Version:    0.6.0
 */