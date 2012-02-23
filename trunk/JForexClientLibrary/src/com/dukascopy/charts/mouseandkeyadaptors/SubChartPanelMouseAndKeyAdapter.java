/*     */ package com.dukascopy.charts.mouseandkeyadaptors;
/*     */ 
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*     */ import com.dukascopy.charts.chartbuilder.IDataManagerAndIndicatorsContainer;
/*     */ import com.dukascopy.charts.chartbuilder.IMainOperationManager;
/*     */ import com.dukascopy.charts.chartbuilder.MainMouseAndKeyController;
/*     */ import com.dukascopy.charts.chartbuilder.SubDrawingsMouseAndKeyController;
/*     */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*     */ import com.dukascopy.charts.dialogs.indicators.AddIndicatorDialog;
/*     */ import com.dukascopy.charts.dialogs.indicators.EditIndicatorDialog;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseWheelEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPopupMenu;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ class SubChartPanelMouseAndKeyAdapter extends ChartPanelMouseAndKeyAdapter
/*     */ {
/*  37 */   private static final Logger LOGGER = LoggerFactory.getLogger(SubChartPanelMouseAndKeyAdapter.class);
/*     */   final SubIndicatorGroup subIndicatorGroup;
/*     */   final SubDrawingsMouseAndKeyController subDrawingsMouseAndKeyController;
/*     */   final MainMouseAndKeyController mainMouseAndKeyController;
/*     */   final IMainOperationManager mainOperationManager;
/*     */   final IDataManagerAndIndicatorsContainer indicatorsContainer;
/*  46 */   boolean isNewDragging = false;
/*     */   int draggedFrom;
/*     */ 
/*     */   public SubChartPanelMouseAndKeyAdapter(SubIndicatorGroup subIndicatorGroup, IMainOperationManager mainOperationManager, MainMouseAndKeyController mainMouseAndKeyController, SubDrawingsMouseAndKeyController subDrawingsMouseAndKeyController, GuiRefresher guiRefresher, IDataManagerAndIndicatorsContainer indicatorsContainer, ChartState chartState)
/*     */   {
/*  58 */     super(guiRefresher, chartState);
/*  59 */     this.mouseOverPane = false;
/*  60 */     this.mainMouseAndKeyController = mainMouseAndKeyController;
/*  61 */     this.subDrawingsMouseAndKeyController = subDrawingsMouseAndKeyController;
/*  62 */     this.subIndicatorGroup = subIndicatorGroup;
/*  63 */     this.mainOperationManager = mainOperationManager;
/*  64 */     this.indicatorsContainer = indicatorsContainer;
/*     */   }
/*     */ 
/*     */   protected int getWindowId()
/*     */   {
/*  69 */     return this.subIndicatorGroup.getSubWindowId();
/*     */   }
/*     */ 
/*     */   public void mouseEntered(MouseEvent e)
/*     */   {
/*  74 */     super.mouseEntered(e);
/*  75 */     this.subDrawingsMouseAndKeyController.mouseEntered(e);
/*     */   }
/*     */ 
/*     */   public void mouseExited(MouseEvent e)
/*     */   {
/*  80 */     super.mouseExited(e);
/*  81 */     this.subDrawingsMouseAndKeyController.mouseExited(e);
/*     */   }
/*     */ 
/*     */   public void mouseClicked(MouseEvent e)
/*     */   {
/*  87 */     e.getComponent().requestFocus();
/*  88 */     if (mouseClickedOnClosingArea(e)) {
/*  89 */       List indicators = this.subIndicatorGroup.getSubIndicators();
/*  90 */       if (indicators.size() == 1)
/*  91 */         deleteSubIndicator((IndicatorWrapper)indicators.get(0));
/*     */       else {
/*  93 */         new JPopupMenu(indicators, e) {  }
/*  93 */         .show(e.getComponent(), e.getX(), e.getY());
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*  99 */       byte cursorValue = this.subDrawingsMouseAndKeyController.mouseClicked(e);
/* 100 */       if (cursorValue == 0)
/* 101 */         e.getComponent().setCursor(Cursor.getPredefinedCursor(12));
/* 102 */       else if (cursorValue == 1)
/* 103 */         e.getComponent().setCursor(Cursor.getPredefinedCursor(13));
/*     */       else {
/* 105 */         e.getComponent().setCursor(Cursor.getPredefinedCursor(0));
/*     */       }
/* 107 */       if (e.isConsumed())
/* 108 */         e.getComponent().getParent().repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseMoved(MouseEvent e)
/*     */   {
/* 115 */     super.mouseMoved(e);
/* 116 */     byte isSomeDrawingHighlighted = this.subDrawingsMouseAndKeyController.mouseMoved(e);
/* 117 */     if (isSomeDrawingHighlighted == 0)
/* 118 */       e.getComponent().setCursor(Cursor.getPredefinedCursor(12));
/* 119 */     else if (isSomeDrawingHighlighted == 1)
/* 120 */       e.getComponent().setCursor(Cursor.getPredefinedCursor(13));
/*     */     else {
/* 122 */       e.getComponent().setCursor(Cursor.getPredefinedCursor(0));
/*     */     }
/* 124 */     if (!e.isConsumed())
/* 125 */       e.getComponent().getParent().getParent().getParent().repaint();
/*     */   }
/*     */ 
/*     */   public void mouseDragged(MouseEvent e)
/*     */   {
/* 131 */     if (!this.mouseOverPane) {
/* 132 */       return;
/*     */     }
/* 134 */     super.mouseDragged(e);
/*     */ 
/* 136 */     this.subDrawingsMouseAndKeyController.mouseDragged(e);
/*     */ 
/* 138 */     if (!e.isConsumed()) {
/* 139 */       this.mainOperationManager.moveTimeFrame(this.draggedFrom, e.getX(), 0, 0, this.isNewDragging);
/* 140 */       this.isNewDragging = false;
/* 141 */       this.draggedFrom = e.getX();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e)
/*     */   {
/* 147 */     e.getComponent().requestFocus();
/*     */ 
/* 149 */     this.subDrawingsMouseAndKeyController.mousePressed(e);
/*     */ 
/* 151 */     if (!e.isConsumed()) {
/* 152 */       this.isNewDragging = true;
/*     */ 
/* 154 */       if (e.isPopupTrigger()) {
/* 155 */         showPopupMenues(e, getFrame(e.getComponent()));
/* 156 */         e.consume();
/*     */       }
/*     */     }
/* 159 */     this.draggedFrom = e.getX();
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent e)
/*     */   {
/* 164 */     e.getComponent().requestFocus();
/*     */ 
/* 166 */     this.subDrawingsMouseAndKeyController.mouseReleased(e);
/*     */ 
/* 168 */     if (!e.isConsumed()) {
/* 169 */       this.isNewDragging = true;
/*     */ 
/* 171 */       if (e.isPopupTrigger()) {
/* 172 */         showPopupMenues(e, getFrame(e.getComponent()));
/* 173 */         e.consume();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseWheelMoved(MouseWheelEvent e)
/*     */   {
/* 180 */     this.subDrawingsMouseAndKeyController.mouseWheelMoved(e);
/* 181 */     this.mainMouseAndKeyController.mouseWheelMoved(e);
/*     */   }
/*     */ 
/*     */   public void keyPressed(KeyEvent e)
/*     */   {
/* 186 */     this.subDrawingsMouseAndKeyController.keyPressed(e);
/* 187 */     this.mainMouseAndKeyController.keyPressed(e);
/*     */   }
/*     */ 
/*     */   void showPopupMenues(MouseEvent mouseEvent, JFrame frame)
/*     */   {
/* 193 */     List indicatorWrappers = this.subIndicatorGroup.getSubIndicators();
/*     */ 
/* 195 */     JPopupMenu indicatorsMenu = new JPopupMenu();
/* 196 */     indicatorsMenu.add(createAddIndicatorMenu(frame));
/* 197 */     indicatorsMenu.add(createEditIndicatorMenu(frame, indicatorWrappers));
/* 198 */     indicatorsMenu.add(createDeleteIndicatorMenu(indicatorWrappers));
/*     */ 
/* 200 */     if (indicatorWrappers.size() > 1) {
/* 201 */       indicatorsMenu.add(createDeleteAllIndicatorsMenuItem(mouseEvent.getComponent()));
/*     */     }
/*     */ 
/* 204 */     indicatorsMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
/*     */   }
/*     */ 
/*     */   JMenuItem createAddIndicatorMenu(JFrame frame) {
/* 208 */     JMenuItem addIndicatorMenuItem = new JMenuItem(LocalizationManager.getText("item.add.indicator"));
/* 209 */     addIndicatorMenuItem.addActionListener(new ActionListener(frame) {
/*     */       public void actionPerformed(ActionEvent e) {
/* 211 */         new AddIndicatorDialog(SubChartPanelMouseAndKeyAdapter.this.subIndicatorGroup.getSubWindowId(), this.val$frame, SubChartPanelMouseAndKeyAdapter.this.indicatorsContainer, SubChartPanelMouseAndKeyAdapter.this.guiRefresher, SubChartPanelMouseAndKeyAdapter.this.chartState.getPeriod(), SubChartPanelMouseAndKeyAdapter.this.chartState.getDataType()).dispose();
/*     */       }
/*     */     });
/* 220 */     return addIndicatorMenuItem;
/*     */   }
/*     */ 
/*     */   Component createEditIndicatorMenu(JFrame frame, List<IndicatorWrapper> indicatorWrappers)
/*     */   {
/* 225 */     if (indicatorWrappers.size() == 1) {
/* 226 */       return new JMenuItem(LocalizationManager.getText("item.edit.indicator"), indicatorWrappers, frame)
/*     */       {
/*     */       };
/*     */     }
/*     */ 
/* 234 */     return new JMenu(LocalizationManager.getText("item.edit.indicator"), indicatorWrappers, frame)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   JMenuItem createDeleteAllIndicatorsMenuItem(Component component)
/*     */   {
/* 252 */     return new JMenuItem(LocalizationManager.getText("item.remove.all"), component)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   Component createDeleteIndicatorMenu(List<IndicatorWrapper> indicatorWrappers)
/*     */   {
/* 272 */     if (indicatorWrappers.size() == 1) {
/* 273 */       return new JMenuItem(LocalizationManager.getText("item.remove.indicator"), indicatorWrappers)
/*     */       {
/*     */       };
/*     */     }
/*     */ 
/* 281 */     return new JMenu(LocalizationManager.getText("item.remove.indicator"), indicatorWrappers)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   void deleteSubIndicator(IndicatorWrapper indicatorWrapper)
/*     */   {
/* 296 */     Integer subViewId = this.guiRefresher.getSubChartViewIdFor(indicatorWrapper.getId());
/* 297 */     if (subViewId == null) {
/* 298 */       return;
/*     */     }
/* 300 */     this.guiRefresher.deleteSubIndicatorFromSubChartView(subViewId.intValue(), indicatorWrapper);
/* 301 */     List indicatorWrappers = this.subIndicatorGroup.getSubIndicators();
/* 302 */     if (indicatorWrappers.isEmpty())
/* 303 */       this.guiRefresher.deleteSubChartView(subViewId);
/*     */     try
/*     */     {
/* 306 */       this.indicatorsContainer.deleteIndicator(indicatorWrapper);
/*     */     } catch (Exception e) {
/* 308 */       LOGGER.error(e.getMessage(), e);
/*     */     } finally {
/* 310 */       this.guiRefresher.refreshSubContentBySubViewId(subViewId.intValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   void editIndicator(IndicatorWrapper indicatorWrapper, JFrame jFrame) {
/* 315 */     new EditIndicatorDialog(this.subIndicatorGroup.getSubWindowId(), indicatorWrapper, jFrame, this.indicatorsContainer, this.guiRefresher, this.chartState.getPeriod(), this.chartState.getDataType()).dispose();
/*     */   }
/*     */ 
/*     */   JFrame getFrame(Component curComponent)
/*     */   {
/* 327 */     while (!(curComponent instanceof JFrame)) {
/* 328 */       curComponent = curComponent.getParent();
/*     */     }
/* 330 */     return (JFrame)curComponent;
/*     */   }
/*     */ 
/*     */   boolean mouseClickedOnClosingArea(MouseEvent e) {
/* 334 */     boolean isInXRange = (e.getX() > 5) && (e.getX() < 15);
/* 335 */     boolean isInYRange = (e.getY() > 5) && (e.getY() < 15);
/* 336 */     return (isInXRange) && (isInYRange);
/*     */   }
/*     */ 
/*     */   void deleteSubChartViewWithAllIndicators() {
/* 340 */     List indicatorWrappers = this.subIndicatorGroup.getSubIndicators();
/* 341 */     List indicatorsToBeDeleted = new ArrayList(indicatorWrappers.size());
/* 342 */     for (IndicatorWrapper indicatorWrapper : indicatorWrappers) {
/* 343 */       tryToDeleteIndicator(indicatorWrapper, indicatorsToBeDeleted);
/*     */     }
/* 345 */     for (IndicatorWrapper indicatorWrapper : indicatorsToBeDeleted) {
/* 346 */       this.guiRefresher.deleteSubIndicatorFromSubChartView(this.subIndicatorGroup.getSubWindowId(), indicatorWrapper);
/*     */     }
/* 348 */     this.subIndicatorGroup.subIndicatorsDeleted(indicatorsToBeDeleted);
/* 349 */     if (this.subIndicatorGroup.getSubIndicators().isEmpty())
/* 350 */       this.guiRefresher.deleteSubChartView(Integer.valueOf(this.subIndicatorGroup.getSubWindowId()));
/*     */   }
/*     */ 
/*     */   void tryToDeleteIndicator(IndicatorWrapper indicatorWrapper, List<IndicatorWrapper> indicatorsToBeDeleted)
/*     */   {
/*     */     try {
/* 356 */       this.indicatorsContainer.deleteIndicator(indicatorWrapper);
/* 357 */       indicatorsToBeDeleted.add(indicatorWrapper);
/*     */     } catch (Exception exc) {
/* 359 */       LOGGER.error(exc.getMessage(), exc);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mouseandkeyadaptors.SubChartPanelMouseAndKeyAdapter
 * JD-Core Version:    0.6.0
 */