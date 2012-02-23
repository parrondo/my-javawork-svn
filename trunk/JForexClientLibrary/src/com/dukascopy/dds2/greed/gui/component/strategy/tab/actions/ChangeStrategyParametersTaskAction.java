/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.actions;
/*    */ 
/*    */ import com.dukascopy.api.Configurable;
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.api.impl.StrategyMessages;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.agent.Strategies;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramesTabbedPane;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyType;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParameterLocal;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.StrategyPreset;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTableModel;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.util.CollectionParameterWrapper;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeController;
/*    */ import java.beans.PropertyChangeEvent;
/*    */ import java.io.File;
/*    */ import java.lang.reflect.Field;
/*    */ import java.util.List;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class ChangeStrategyParametersTaskAction extends CommonStrategyAction
/*    */ {
/* 26 */   private static final Logger LOGGER = LoggerFactory.getLogger(ChangeStrategyParametersTaskAction.class);
/*    */ 
/*    */   public ChangeStrategyParametersTaskAction(StrategiesTableModel model, TabsAndFramesTabbedPane tabbedPane, WorkspaceTreeController workspaceTreeController) {
/* 29 */     super(model, tabbedPane, workspaceTreeController);
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param)
/*    */   {
/* 35 */     if ((param instanceof StrategyNewBean)) {
/* 36 */       StrategyNewBean strategyBean = (StrategyNewBean)param;
/* 37 */       strategyBean.setRunningPresetName(strategyBean.getActivePreset().getName());
/*    */ 
/* 39 */       List newParams = strategyBean.getActivePreset().getStrategyParameters();
/*    */ 
/* 41 */       if (strategyBean.getType().equals(StrategyType.REMOTE)) {
/* 42 */         GreedContext.publishEvent(new ChangeRemoteStrategyParamsAppAction(this.strategiesModel, strategyBean));
/*    */       } else {
/* 44 */         IStrategy iStrategy = strategyBean.getStrategy();
/*    */ 
/* 46 */         if (iStrategy != null) {
/* 47 */           for (StrategyParameterLocal newParam : newParams) {
/*    */             try {
/* 49 */               Field field = iStrategy.getClass().getField(newParam.getId());
/* 50 */               if (field != null) {
/* 51 */                 Configurable configurable = (Configurable)field.getAnnotation(Configurable.class);
/* 52 */                 if ((configurable != null) && (!configurable.readOnly())) {
/* 53 */                   Object oldValue = field.get(iStrategy);
/* 54 */                   Object newValue = null;
/* 55 */                   if (field.getType().equals(File.class)) {
/* 56 */                     newValue = new File(String.valueOf(newParam.getValue()));
/* 57 */                     field.set(iStrategy, newValue);
/* 58 */                   } else if ((newParam.getValue() instanceof CollectionParameterWrapper)) {
/* 59 */                     newValue = ((CollectionParameterWrapper)newParam.getValue()).getSelectedValue();
/* 60 */                     field.set(iStrategy, newValue);
/*    */                   } else {
/* 62 */                     newValue = newParam.getValue();
/* 63 */                     field.set(iStrategy, newValue);
/*    */                   }
/*    */ 
/* 66 */                   Strategies.get().fireConfigurationPropertyChange(strategyBean.getRunningProcessId(), new PropertyChangeEvent(iStrategy, field.getName(), oldValue, newValue));
/*    */ 
/* 68 */                   Strategies.get().fireConfigurationPropertyChange(strategyBean.getRunningProcessId(), new PropertyChangeEvent(iStrategy, configurable.value(), oldValue, newValue));
/*    */                 }
/*    */               }
/*    */             }
/*    */             catch (Exception ex) {
/* 73 */               LOGGER.error(ex.getMessage(), ex);
/* 74 */             }continue;
/*    */           }
/*    */ 
/*    */         }
/*    */ 
/* 79 */         StrategyMessages.strategyIsModified(iStrategy);
/*    */       }
/*    */     }
/*    */ 
/* 83 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.actions.ChangeStrategyParametersTaskAction
 * JD-Core Version:    0.6.0
 */