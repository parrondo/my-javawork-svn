/*     */ package com.dukascopy.dds2.greed.gui.component.tree.nodes;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.utils.ChartsLocalizator;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import javax.swing.tree.MutableTreeNode;
/*     */ import javax.swing.tree.TreeNode;
/*     */ 
/*     */ public class ChartTreeNode extends WorkspaceTreeNode
/*     */ {
/*     */   private final int chartPanelId;
/*  19 */   private List<ChartTreeNodeChild> indicators = new ArrayList();
/*     */   public static final String PERIOD_SEPARATOR = ",";
/*     */   private Instrument instrument;
/*     */   private OfferSide offerSide;
/*     */   private JForexPeriod jForexPeriod;
/*     */ 
/*     */   public ChartTreeNode(int chartPanelId, Instrument instrument, OfferSide offerSide, JForexPeriod jForexPeriod, ChartsNode parent)
/*     */   {
/*  34 */     super(true, "");
/*  35 */     setParent(parent);
/*  36 */     this.chartPanelId = chartPanelId;
/*  37 */     this.offerSide = offerSide;
/*  38 */     this.instrument = instrument;
/*  39 */     this.jForexPeriod = jForexPeriod;
/*     */   }
/*     */ 
/*     */   public int getChartPanelId() {
/*  43 */     return this.chartPanelId;
/*     */   }
/*     */ 
/*     */   public String getName() {
/*  47 */     StringBuilder name = new StringBuilder();
/*  48 */     if (this.instrument == null)
/*  49 */       name.append("undefined");
/*     */     else {
/*  51 */       name.append(this.instrument.toString());
/*     */     }
/*  53 */     name.append(",");
/*     */ 
/*  55 */     name.append(ChartsLocalizator.localize(getJForexPeriod()));
/*  56 */     return name.toString();
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument() {
/*  60 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public OfferSide getOfferSide() {
/*  64 */     return this.offerSide;
/*     */   }
/*     */ 
/*     */   public TreeNode getChildAt(int childIndex) {
/*  68 */     return (TreeNode)this.indicators.get(childIndex);
/*     */   }
/*     */ 
/*     */   public int getChildCount() {
/*  72 */     return this.indicators.size();
/*     */   }
/*     */ 
/*     */   public int getIndex(TreeNode node) {
/*  76 */     return this.indicators.indexOf(node);
/*     */   }
/*     */ 
/*     */   public boolean isLeaf() {
/*  80 */     return this.indicators.isEmpty();
/*     */   }
/*     */ 
/*     */   public Enumeration<ChartTreeNodeChild> children() {
/*  84 */     return Collections.enumeration(this.indicators);
/*     */   }
/*     */ 
/*     */   public void insert(MutableTreeNode child, int index) {
/*  88 */     this.indicators.add(index, (ChartTreeNodeChild)child);
/*     */   }
/*     */ 
/*     */   public void remove(int index) {
/*  92 */     this.indicators.remove(index);
/*     */   }
/*     */ 
/*     */   public void setInstrument(Instrument instrument) {
/*  96 */     this.instrument = instrument;
/*     */   }
/*     */ 
/*     */   public JForexPeriod getJForexPeriod() {
/* 100 */     return this.jForexPeriod;
/*     */   }
/*     */ 
/*     */   public void setJForexPeriod(JForexPeriod jForexPeriod) {
/* 104 */     this.jForexPeriod = jForexPeriod;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.nodes.ChartTreeNode
 * JD-Core Version:    0.6.0
 */