package com.dukascopy.dds2.greed.agent.strategy.tester.util;

import com.dukascopy.api.IStrategy;
import com.dukascopy.dds2.greed.agent.strategy.tester.ITesterReport;
import com.dukascopy.dds2.greed.agent.strategy.tester.TesterAccount;
import com.dukascopy.dds2.greed.agent.strategy.tester.TesterConfig;
import com.dukascopy.dds2.greed.agent.strategy.tester.TesterCustodian;
import com.dukascopy.dds2.greed.agent.strategy.tester.TesterHistory;
import com.dukascopy.dds2.greed.agent.strategy.tester.TesterOrdersProvider;

public class StrategyOptimizerSet
{
  public IStrategy strategy;
  public TesterAccount account;
  public ITesterReport testerReport;
  public TesterOrdersProvider testerOrdersProvider;
  public TesterConfig context;
  public TesterCustodian engine;
  public TesterHistory history;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.util.StrategyOptimizerSet
 * JD-Core Version:    0.6.0
 */