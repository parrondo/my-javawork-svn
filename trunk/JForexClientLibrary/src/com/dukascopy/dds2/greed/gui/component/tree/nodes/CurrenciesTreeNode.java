/*     */ package com.dukascopy.dds2.greed.gui.component.tree.nodes;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.swing.tree.TreeNode;
/*     */ 
/*     */ public class CurrenciesTreeNode extends WorkspaceTreeNode
/*     */ {
/*  20 */   private final List<CurrencyTreeNode> childs = new ArrayList();
/*     */ 
/*  22 */   private final Comparator<CurrencyTreeNode> comparator = new Comparator() {
/*     */     public int compare(CurrencyTreeNode currencyTreeNode, CurrencyTreeNode currencyTreeNode1) {
/*  24 */       String name1 = currencyTreeNode1.getInstrument().name();
/*  25 */       String name = currencyTreeNode.getInstrument().name();
/*  26 */       return name.compareTo(name1);
/*     */     }
/*  22 */   };
/*     */ 
/*     */   CurrenciesTreeNode(WorkspaceRootNode parent)
/*     */   {
/*  32 */     super(true, "tree.node.currencies");
/*  33 */     if (parent == null) {
/*  34 */       throw new IllegalArgumentException("Parent of CurrenciesNode cannot be null!");
/*     */     }
/*  36 */     setParent(parent);
/*     */   }
/*     */ 
/*     */   public CurrencyTreeNode getChildAt(int childIndex) {
/*  40 */     return (CurrencyTreeNode)this.childs.get(childIndex);
/*     */   }
/*     */ 
/*     */   public int getChildCount() {
/*  44 */     return this.childs.size();
/*     */   }
/*     */ 
/*     */   public int getIndex(TreeNode node) {
/*  48 */     if (!(node instanceof CurrencyTreeNode)) {
/*  49 */       return -1;
/*     */     }
/*  51 */     CurrencyTreeNode currencyTreeNode = (CurrencyTreeNode)node;
/*  52 */     return this.childs.indexOf(currencyTreeNode);
/*     */   }
/*     */ 
/*     */   public boolean isLeaf() {
/*  56 */     return this.childs.isEmpty();
/*     */   }
/*     */ 
/*     */   public Enumeration<CurrencyTreeNode> children() {
/*  60 */     return Collections.enumeration(this.childs);
/*     */   }
/*     */ 
/*     */   public void remove(int index) {
/*  64 */     this.childs.remove(index);
/*  65 */     Collections.sort(this.childs, this.comparator);
/*     */   }
/*     */ 
/*     */   public int addCurrencyNode(CurrencyTreeNode child) {
/*  69 */     this.childs.add(child);
/*  70 */     Collections.sort(this.childs, this.comparator);
/*  71 */     return this.childs.indexOf(child);
/*     */   }
/*     */ 
/*     */   public Set<String> getCurrencyNames() {
/*  75 */     Set instrumentNames = new HashSet();
/*  76 */     for (CurrencyTreeNode child : this.childs) {
/*  77 */       Instrument instrument = child.getInstrument();
/*  78 */       if (instrument != null) {
/*  79 */         String instrumentName = instrument.toString();
/*  80 */         instrumentNames.add(instrumentName);
/*     */       }
/*     */     }
/*  82 */     return instrumentNames;
/*     */   }
/*     */ 
/*     */   public CurrencyTreeNode getCurrencyNodeByInstrumentName(String instrumentName) {
/*  86 */     for (CurrencyTreeNode currencyTreeNode : this.childs) {
/*  87 */       Instrument instrument = currencyTreeNode.getInstrument();
/*  88 */       if (instrument == null) {
/*  89 */         return null;
/*     */       }
/*  91 */       if (instrument.toString().equals(instrumentName)) {
/*  92 */         return currencyTreeNode;
/*     */       }
/*     */     }
/*  95 */     return null;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 100 */     return LocalizationManager.getText(super.getName());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tree.nodes.CurrenciesTreeNode
 * JD-Core Version:    0.6.0
 */