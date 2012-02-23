/*     */ package com.dukascopy.dds2.greed.gui.component.chart;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.impl.ServiceWrapper;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ class TabPanelFrameContainer
/*     */ {
/*  12 */   private final Map<Integer, DockedUndockedFrame> frames = new HashMap();
/*     */ 
/*     */   public void addFrame(DockedUndockedFrame frame)
/*     */   {
/*  16 */     this.frames.put(Integer.valueOf(frame.getPanelId()), frame);
/*     */   }
/*     */ 
/*     */   public void removeFrameByPanelId(int panelId) {
/*  20 */     this.frames.remove(Integer.valueOf(panelId));
/*     */   }
/*     */ 
/*     */   public UndockedJFrame getUndockedChartPanelById(int panelId) {
/*  24 */     DockedUndockedFrame dockedUndockedFrame = (DockedUndockedFrame)this.frames.get(Integer.valueOf(panelId));
/*  25 */     if ((dockedUndockedFrame instanceof UndockedJFrame)) {
/*  26 */       return (UndockedJFrame)dockedUndockedFrame;
/*     */     }
/*  28 */     return null;
/*     */   }
/*     */ 
/*     */   public DockedUndockedFrame getFrameByPanelId(int panelId)
/*     */   {
/*  34 */     return (DockedUndockedFrame)this.frames.get(Integer.valueOf(panelId));
/*     */   }
/*     */ 
/*     */   public void hideOtherHeadlessInternalFrames(HeadlessJInternalFrame maximizedFrame) {
/*  38 */     for (DockedUndockedFrame frame : this.frames.values())
/*  39 */       if ((frame instanceof HeadlessJInternalFrame)) {
/*  40 */         HeadlessJInternalFrame internalFrame = (HeadlessJInternalFrame)frame;
/*  41 */         if (internalFrame != maximizedFrame) {
/*  42 */           internalFrame.setSelected(false);
/*  43 */           internalFrame.setVisible(false);
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   public List<HeadlessJInternalFrame> getJInternalFrames()
/*     */   {
/*  50 */     List internalFrames = new ArrayList(this.frames.size());
/*  51 */     for (DockedUndockedFrame frame : getFrames()) {
/*  52 */       if ((frame instanceof HeadlessJInternalFrame)) {
/*  53 */         internalFrames.add((HeadlessJInternalFrame)frame);
/*     */       }
/*     */     }
/*  56 */     return internalFrames;
/*     */   }
/*     */ 
/*     */   public Collection<DockedUndockedFrame> getFrames() {
/*  60 */     return new ArrayList(this.frames.values());
/*     */   }
/*     */ 
/*     */   public List<DockedUndockedFrame> getFramesList() {
/*  64 */     List dockedUndockedFrames = new ArrayList();
/*  65 */     if (dockedUndockedFrames != null) {
/*  66 */       for (DockedUndockedFrame frame : this.frames.values()) {
/*  67 */         dockedUndockedFrames.add(frame);
/*     */       }
/*     */     }
/*  70 */     return dockedUndockedFrames;
/*     */   }
/*     */ 
/*     */   public ServiceSourceEditorPanel getEditorPanel(ServiceWrapper service) {
/*  74 */     for (DockedUndockedFrame frame : this.frames.values()) {
/*  75 */       JPanel frameComponent = frame.getContent();
/*  76 */       if ((frameComponent instanceof ServiceSourceEditorPanel)) {
/*  77 */         ServiceSourceEditorPanel editorPanel = (ServiceSourceEditorPanel)frameComponent;
/*  78 */         if ((editorPanel.getSourceFile() != null) && (editorPanel.getSourceFile().equals(service.getSourceFile()))) {
/*  79 */           return editorPanel;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  84 */     return null;
/*     */   }
/*     */ 
/*     */   public int getFirstChartPanelIdFor(Instrument instrument) {
/*  88 */     int chartPanelId = -1;
/*  89 */     for (DockedUndockedFrame frame : this.frames.values()) {
/*  90 */       JPanel frameComponent = frame.getContent();
/*  91 */       if ((frameComponent instanceof ChartPanel)) {
/*  92 */         ChartPanel chartPanel = (ChartPanel)frameComponent;
/*  93 */         if (chartPanel.getInstrument() == instrument) {
/*  94 */           chartPanelId = chartPanel.getPanelId();
/*  95 */           break;
/*     */         }
/*     */       }
/*     */     }
/*  99 */     return chartPanelId;
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 103 */     this.frames.clear();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.TabPanelFrameContainer
 * JD-Core Version:    0.6.0
 */