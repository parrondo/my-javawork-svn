package com.dukascopy.charts.tablebuilder;

import com.dukascopy.charts.tablebuilder.component.table.DataTablePresentationAbstractJTable;
import javax.swing.JComponent;

public abstract interface ITablePresentationManager
{
  public abstract JComponent getTablePresentationComponent();

  public abstract DataTablePresentationAbstractJTable<?, ?> getDataPresentationTable();

  public abstract void stop();

  public abstract void start();

  public abstract void clear();

  public abstract Boolean isRunning();

  public abstract void applyQuickFilter(String paramString);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.ITablePresentationManager
 * JD-Core Version:    0.6.0
 */