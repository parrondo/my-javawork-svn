/*     */ package com.dukascopy.dds2.greed.gui.component.tree.nodes;
/*     */ 
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceType;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyStatus;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyType;
/*     */ import java.io.File;
/*     */ 
/*     */ public class StrategyTreeNode extends AbstractServiceTreeNode
/*     */ {
/*     */   private final StrategyNewBean strategyBean;
/*     */   private final StrategyWrapper strategyWrapper;
/*     */ 
/*     */   StrategyTreeNode(StrategyNewBean strategyBean, WorkspaceTreeNode parent)
/*     */   {
/*  18 */     super(strategyBean.getId().intValue(), false, strategyBean.getName());
/*  19 */     if (parent == null) {
/*  20 */       throw new IllegalArgumentException("StrategyTreeNode parent cannot be null!");
/*     */     }
/*     */ 
/*  23 */     this.strategyBean = strategyBean;
/*     */ 
/*  25 */     this.strategyWrapper = new StrategyWrapper();
/*     */ 
/*  27 */     this.strategyWrapper.setBinaryFile(strategyBean.getStrategyBinaryFile());
/*  28 */     this.strategyWrapper.setSourceFile(strategyBean.getStrategySourceFile());
/*  29 */     this.strategyWrapper.setNewUnsaved(false);
/*     */ 
/*  31 */     setParent(parent);
/*     */   }
/*     */ 
/*     */   StrategyTreeNode(StrategyWrapper strategyWrapper, StrategyNewBean strategyBean, WorkspaceTreeNode parent) {
/*  35 */     super(strategyBean.getId().intValue(), false, strategyBean.getName());
/*  36 */     if (parent == null) {
/*  37 */       throw new IllegalArgumentException("StrategyTreeNode parent cannot be null!");
/*     */     }
/*     */ 
/*  40 */     this.strategyWrapper = strategyWrapper;
/*  41 */     this.strategyBean = strategyBean;
/*     */ 
/*  43 */     setParent(parent);
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  48 */     return this.strategyBean.getName();
/*     */   }
/*     */ 
/*     */   public boolean isNewUnsaved() {
/*  52 */     return this.strategyWrapper.isNewUnsaved();
/*     */   }
/*     */ 
/*     */   public boolean isRemote() {
/*  56 */     return this.strategyBean.getType().equals(StrategyType.REMOTE);
/*     */   }
/*     */ 
/*     */   public boolean isRunning() {
/*  60 */     return (this.strategyBean.getStatus().equals(StrategyStatus.RUNNING)) || (this.strategyBean.getStatus().equals(StrategyStatus.STARTING));
/*     */   }
/*     */ 
/*     */   public boolean isRunningRemotely() {
/*  64 */     return (isRunning()) && (this.strategyBean.getType().equals(StrategyType.REMOTE));
/*     */   }
/*     */ 
/*     */   public boolean isInitializing() {
/*  68 */     return this.strategyBean.getStatus().equals(StrategyStatus.STARTING);
/*     */   }
/*     */ 
/*     */   public boolean isEditable() {
/*  72 */     return this.strategyBean.getStrategySourceFile() != null;
/*     */   }
/*     */ 
/*     */   public StrategyNewBean getStrategy() {
/*  76 */     return this.strategyBean;
/*     */   }
/*     */ 
/*     */   public StrategyWrapper getServiceWrapper()
/*     */   {
/*  81 */     return this.strategyWrapper;
/*     */   }
/*     */ 
/*     */   public ServiceSourceType getServiceSourceType()
/*     */   {
/*  86 */     return ServiceSourceType.STRATEGY;
/*     */   }
/*     */ 
/*     */   public void setBinaryFile(File binaryFile) {
/*  90 */     this.strategyBean.setStrategyBinaryFile(binaryFile);
/*  91 */     if ((binaryFile != null) && (this.strategyBean.getStrategySourceFile() == null)) {
/*  92 */       this.strategyBean.setName(binaryFile.getName().substring(0, binaryFile.getName().lastIndexOf('.')));
/*     */     }
/*     */ 
/*  95 */     this.strategyWrapper.setBinaryFile(binaryFile);
/*     */   }
/*     */ 
/*     */   public int getId()
/*     */   {
/* 100 */     if (this.strategyBean != null) {
/* 101 */       return this.strategyBean.getId().intValue();
/*     */     }
/* 103 */     return super.getId();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode
 * JD-Core Version:    0.6.0
 */