/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.StrategiesContentPane;
/*    */ import com.dukascopy.transport.common.msg.strategy.StrategyProcessDescriptor;
/*    */ import java.util.Collection;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ public class RemoteStrategiesListResponseAction extends AbstractRemoteStrategyAction
/*    */ {
/*    */   private static final long serialVersionUID = 2252624820583600528L;
/* 22 */   public static final long REMOTE_STRATEGIES_START_TIMEOUT = TimeUnit.MINUTES.toMillis(3L);
/*    */   private Collection<StrategyProcessDescriptor> descriptors;
/*    */ 
/*    */   public RemoteStrategiesListResponseAction(Object source, Collection<StrategyProcessDescriptor> descriptors)
/*    */   {
/* 29 */     super(source);
/* 30 */     this.descriptors = descriptors;
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/* 39 */     StrategiesContentPane strategiesContentPane = getStrategiesContentPane();
/* 40 */     if (strategiesContentPane != null)
/* 41 */       strategiesContentPane.updateRemotelyRunStrategies(this.descriptors);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.RemoteStrategiesListResponseAction
 * JD-Core Version:    0.6.0
 */