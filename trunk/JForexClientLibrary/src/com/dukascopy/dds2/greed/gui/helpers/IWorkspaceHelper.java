package com.dukascopy.dds2.greed.gui.helpers;

import com.dukascopy.api.IStrategy;
import com.dukascopy.api.IStrategyListener;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
import java.io.File;
import java.util.Map;
import java.util.Set;
import javax.swing.event.TreeSelectionEvent;

public abstract interface IWorkspaceHelper
{
  public abstract void selectTabForSelectedNode(WorkspaceJTree paramWorkspaceJTree);

  public abstract int calculatePreviousNodeIndxToBeFocused(int paramInt);

  public abstract int calculateNextNodeIndexToBeFocused(int paramInt);

  public abstract void loadDataIntoWorkspace(WorkspaceJTree paramWorkspaceJTree);

  public abstract void checkDependantCurrenciesAndAddThemIfNecessary(WorkspaceJTree paramWorkspaceJTree, String paramString);

  public abstract void addDependantCurrenciesAndSubscribe(Instrument paramInstrument);

  public abstract void addDependantCurrenciesAndSubscribe(Set<Instrument> paramSet);

  public abstract Set<Instrument> getSubscribedInstruments();

  public abstract Set<Instrument> getUnsubscribedInstruments();

  public abstract void findAndsubscribeToCurrenciesForProfitLossCalculation();

  public abstract void refreshDealPanel(Instrument paramInstrument);

  public abstract boolean isInstrumentSubscribed(Instrument paramInstrument);

  public abstract Instrument[] getAvailableInstrumentsAsArray();

  public abstract void subscribeToInstruments(Set<String> paramSet);

  public abstract Instrument getSelectedInstrument(TreeSelectionEvent paramTreeSelectionEvent);

  public abstract long startStrategy(File paramFile, IStrategyListener paramIStrategyListener, Map<String, Object> paramMap, boolean paramBoolean)
    throws JFException;

  public abstract long startStrategy(IStrategy paramIStrategy, IStrategyListener paramIStrategyListener, boolean paramBoolean)
    throws JFException;

  public abstract void dispose();

  public abstract void populateWorkspace();

  public abstract void showChart(Instrument paramInstrument);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper
 * JD-Core Version:    0.6.0
 */