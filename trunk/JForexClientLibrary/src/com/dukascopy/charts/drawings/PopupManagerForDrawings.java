/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.drawings.IDecoratedChartObject.Decoration;
/*     */ import com.dukascopy.api.drawings.IDecoratedChartObject.Placement;
/*     */ import com.dukascopy.api.drawings.IFiboTimeZonesChartObject;
/*     */ import com.dukascopy.api.drawings.IGannAnglesChartObject;
/*     */ import com.dukascopy.api.drawings.IGannGridChartObject;
/*     */ import com.dukascopy.api.drawings.IOhlcChartObject.CandleInfoParams;
/*     */ import com.dukascopy.api.drawings.IOhlcChartObject.OhlcAlignment;
/*     */ import com.dukascopy.api.drawings.IVerticalLineChartObject;
/*     */ import com.dukascopy.api.drawings.IVerticalRetracementChartObject;
/*     */ import com.dukascopy.api.drawings.IWidgetChartObject;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*     */ import com.dukascopy.charts.chartbuilder.IDataManagerAndIndicatorsContainer;
/*     */ import com.dukascopy.charts.dialogs.drawings.RetracementLevelsDialog;
/*     */ import com.dukascopy.charts.dialogs.indicators.ColorJComboBox;
/*     */ import com.dukascopy.charts.dialogs.indicators.EditIndicatorDialog;
/*     */ import com.dukascopy.charts.indicators.IndicatorsManagerImpl;
/*     */ import com.dukascopy.charts.listeners.ChartsActionListenerRegistry;
/*     */ import com.dukascopy.charts.main.DDSChartsControllerImpl;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.charts.persistence.IChartClient;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IDrawingsManagerContainer;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Point;
/*     */ import java.awt.Stroke;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.swing.ButtonModel;
/*     */ import javax.swing.JCheckBoxMenuItem;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class PopupManagerForDrawings
/*     */ {
/*  82 */   private static final Logger LOGGER = LoggerFactory.getLogger(PopupManagerForDrawings.class);
/*     */   private final ChartState chartState;
/*     */   private final IndicatorsManagerImpl indicatorsManagerImpl;
/*     */   private final IDataManagerAndIndicatorsContainer indicatorsContainer;
/*     */   private final ChartsActionListenerRegistry chartsActionListenerRegistry;
/*     */   private final IDrawingsManagerContainer drawingsManagerContainer;
/*     */   private final GuiRefresher guiRefresher;
/*     */ 
/*     */   public PopupManagerForDrawings(ChartState chartState, IndicatorsManagerImpl indicatorsManagerImpl, IDataManagerAndIndicatorsContainer indicatorsContainer, IDrawingsManagerContainer drawingsManagerContainer, ChartsActionListenerRegistry chartsActionListenerRegistry, GuiRefresher guiRefresher)
/*     */   {
/*  99 */     this.chartState = chartState;
/* 100 */     this.indicatorsManagerImpl = indicatorsManagerImpl;
/* 101 */     this.indicatorsContainer = indicatorsContainer;
/* 102 */     this.chartsActionListenerRegistry = chartsActionListenerRegistry;
/* 103 */     this.drawingsManagerContainer = drawingsManagerContainer;
/* 104 */     this.guiRefresher = guiRefresher;
/*     */   }
/*     */ 
/*     */   public JPopupMenu createPopup(ChartObject editedChartObject, GuiRefresher guiRefresher, Point locationOnScreen) {
/* 108 */     if (editedChartObject == null) {
/* 109 */       LOGGER.warn("Edited chart object is null");
/* 110 */       return null;
/*     */     }
/*     */ 
/* 113 */     if (!editedChartObject.isMenuEnabled()) {
/* 114 */       return null;
/*     */     }
/*     */ 
/* 117 */     JPopupMenu drawingPropertiesMenu = new JPopupMenu();
/*     */ 
/* 119 */     if (isOhlcObject(editedChartObject)) {
/* 120 */       drawingPropertiesMenu.add(createOhlcVisibleValuesMenuItem((OhlcChartObject)editedChartObject, guiRefresher));
/* 121 */       drawingPropertiesMenu.addSeparator();
/* 122 */       drawingPropertiesMenu.add(createOhlcAlignmentMenuItem((OhlcChartObject)editedChartObject, guiRefresher));
/* 123 */       drawingPropertiesMenu.addSeparator();
/*     */     }
/*     */ 
/* 126 */     if (isDrawingWithLabels(editedChartObject)) {
/* 127 */       drawingPropertiesMenu.add(createEditLabelMenuItem(editedChartObject));
/*     */     }
/*     */ 
/* 130 */     if ((editedChartObject.hasPriceValue()) || (editedChartObject.hasTimeValue())) {
/* 131 */       drawingPropertiesMenu.add(createEditCoordinatesMenuItem(editedChartObject));
/*     */     }
/*     */ 
/* 134 */     if (isGannDrawing(editedChartObject)) {
/* 135 */       drawingPropertiesMenu.add(createEditPipsPerBarItem(editedChartObject));
/*     */     }
/*     */ 
/* 138 */     if ((isDrawingWithEditablePrices(editedChartObject)) || (isDrawingWithLabels(editedChartObject))) {
/* 139 */       drawingPropertiesMenu.addSeparator();
/*     */     }
/*     */ 
/* 142 */     if ((editedChartObject instanceof FiboRetracementChartObject)) {
/* 143 */       drawingPropertiesMenu.add(createFixedLevelRightSideMenuItem((FiboRetracementChartObject)editedChartObject, guiRefresher));
/*     */     }
/*     */ 
/* 146 */     if (isDrawingWithLevels(editedChartObject)) {
/* 147 */       boolean allowAllPercents = editedChartObject instanceof AbstractTwoPointExtLevelsChartObject;
/* 148 */       drawingPropertiesMenu.add(createPresetsMenu(editedChartObject, guiRefresher));
/* 149 */       drawingPropertiesMenu.add(createEditLevelsMenuItem(editedChartObject, guiRefresher, locationOnScreen, allowAllPercents));
/* 150 */       drawingPropertiesMenu.addSeparator();
/*     */     }
/*     */ 
/* 153 */     drawingPropertiesMenu.add(createColorsSubMenu(editedChartObject, guiRefresher));
/* 154 */     drawingPropertiesMenu.add(createOpacityMenuItem(editedChartObject, guiRefresher));
/* 155 */     if (!isChartWidget(editedChartObject)) {
/* 156 */       drawingPropertiesMenu.add(createStrokeSubMenu(editedChartObject, guiRefresher));
/* 157 */       drawingPropertiesMenu.add(createLineWeightSubMenu(editedChartObject, guiRefresher));
/*     */     }
/*     */ 
/* 160 */     if ((editedChartObject instanceof DecoratedChartObject)) {
/* 161 */       drawingPropertiesMenu.add(createDecorationsSubMenu(editedChartObject, guiRefresher));
/*     */     }
/*     */ 
/* 164 */     if (isFigureFillable(editedChartObject)) {
/* 165 */       drawingPropertiesMenu.addSeparator();
/* 166 */       drawingPropertiesMenu.add(createFillColorMenuItem((AbstractFillableChartObject)editedChartObject, guiRefresher));
/* 167 */       drawingPropertiesMenu.add(createFillOpacityMenuItem((AbstractFillableChartObject)editedChartObject, guiRefresher));
/* 168 */       drawingPropertiesMenu.add(createNoFillMenuItem((AbstractFillableChartObject)editedChartObject, guiRefresher));
/*     */     }
/*     */ 
/* 171 */     if (!isChartWidget(editedChartObject)) {
/* 172 */       drawingPropertiesMenu.addSeparator();
/* 173 */       drawingPropertiesMenu.add(createCloneMenuItem(editedChartObject));
/*     */     }
/*     */ 
/* 177 */     drawingPropertiesMenu.addSeparator();
/* 178 */     drawingPropertiesMenu.add(createDeleteMenuItem(editedChartObject));
/*     */ 
/* 180 */     return drawingPropertiesMenu;
/*     */   }
/*     */ 
/*     */   boolean isDrawingWithLevels(ChartObject editedChartObject) {
/* 184 */     return editedChartObject instanceof IRetracementLevels;
/*     */   }
/*     */ 
/*     */   boolean isFigureFillable(ChartObject editedChartObject) {
/* 188 */     return editedChartObject instanceof AbstractFillableChartObject;
/*     */   }
/*     */ 
/*     */   boolean isOhlcObject(ChartObject editedChartObject) {
/* 192 */     return editedChartObject instanceof OhlcChartObject;
/*     */   }
/*     */ 
/*     */   boolean isChartWidget(ChartObject editedChartObject) {
/* 196 */     return editedChartObject instanceof AbstractWidgetChartObject;
/*     */   }
/*     */ 
/*     */   boolean isDrawingWithLabels(IChartObject editedChartObject) {
/* 200 */     return editedChartObject.isLabelEnabled();
/*     */   }
/*     */ 
/*     */   boolean isDrawingWithEditablePrices(ChartObject editedChartObject) {
/* 204 */     boolean objWithoutPrice = ((editedChartObject instanceof IVerticalLineChartObject)) || ((editedChartObject instanceof IVerticalRetracementChartObject)) || ((editedChartObject instanceof IWidgetChartObject)) || ((editedChartObject instanceof IFiboTimeZonesChartObject));
/*     */ 
/* 208 */     return !objWithoutPrice;
/*     */   }
/*     */ 
/*     */   boolean isGannDrawing(ChartObject editedChartObject) {
/* 212 */     boolean isGann = ((editedChartObject instanceof IGannAnglesChartObject)) || ((editedChartObject instanceof IGannGridChartObject));
/*     */ 
/* 215 */     return isGann;
/*     */   }
/*     */ 
/*     */   JMenu createDecorationsSubMenu(ChartObject editedChartObject, GuiRefresher component)
/*     */   {
/* 222 */     return new JMenu(LocalizationManager.getText("menu.item.decorations"), editedChartObject, component)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   void addDecoration(IDecoratedChartObject.Decoration decoration, DecoratedChartObject decoratedChartObject, GuiRefresher guiRefresher, JMenu subMenu, ChartsActionListenerRegistry chartsActionListenerRegistry)
/*     */   {
/* 238 */     Map decorations = new EnumMap(IDecoratedChartObject.Placement.class);
/* 239 */     decorations.put(IDecoratedChartObject.Placement.Beginning, decoration);
/* 240 */     decorations.put(IDecoratedChartObject.Placement.End, IDecoratedChartObject.Decoration.None);
/* 241 */     addDecorationItem(decoratedChartObject, guiRefresher, subMenu, chartsActionListenerRegistry, decorations);
/*     */ 
/* 243 */     decorations = new EnumMap(IDecoratedChartObject.Placement.class);
/* 244 */     decorations.put(IDecoratedChartObject.Placement.Beginning, IDecoratedChartObject.Decoration.None);
/* 245 */     decorations.put(IDecoratedChartObject.Placement.End, decoration);
/* 246 */     addDecorationItem(decoratedChartObject, guiRefresher, subMenu, chartsActionListenerRegistry, decorations);
/*     */ 
/* 248 */     decorations = new EnumMap(IDecoratedChartObject.Placement.class);
/* 249 */     decorations.put(IDecoratedChartObject.Placement.Beginning, decoration);
/* 250 */     decorations.put(IDecoratedChartObject.Placement.End, decoration);
/* 251 */     addDecorationItem(decoratedChartObject, guiRefresher, subMenu, chartsActionListenerRegistry, decorations);
/*     */   }
/*     */ 
/*     */   void addDecorationItem(DecoratedChartObject chartObject, GuiRefresher guiRefresher, JMenu subMenu, ChartsActionListenerRegistry chartsActionListenerRegistry, Map<IDecoratedChartObject.Placement, IDecoratedChartObject.Decoration> decorations)
/*     */   {
/* 256 */     subMenu.add(new JMenuItem(decorations, chartObject, chartsActionListenerRegistry, guiRefresher)
/*     */     {
/*     */     });
/*     */   }
/*     */ 
/*     */   JMenu createLineWeightSubMenu(ChartObject editedChartObject, GuiRefresher guiRefresher)
/*     */   {
/* 275 */     return new JMenu(LocalizationManager.getText("menu.item.weight"), editedChartObject, guiRefresher)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   void addLineWeightItem(ChartObject chartObject, GuiRefresher component, JMenu subMenu, ChartsActionListenerRegistry chartsActionListenerRegistry, int lineHeight)
/*     */   {
/* 284 */     JMenuItem menuItem = new JMenuItem();
/*     */ 
/* 286 */     menuItem.setIcon(new DrawingsHelper.LinePatternIcon(DrawingsHelper.DashPattern.Solid, lineHeight));
/*     */ 
/* 288 */     menuItem.addActionListener(new ActionListener(chartObject, lineHeight, chartsActionListenerRegistry, component) {
/*     */       public void actionPerformed(ActionEvent e) {
/* 290 */         if (this.val$chartObject == null) {
/* 291 */           return;
/*     */         }
/*     */ 
/* 294 */         BasicStroke editedObjectStroke = (BasicStroke)this.val$chartObject.getStroke();
/*     */ 
/* 296 */         Stroke stroke = editedObjectStroke == null ? new BasicStroke(this.val$lineHeight, 0, 2, 0.0F, null, 0.0F) : new BasicStroke(this.val$lineHeight, editedObjectStroke.getEndCap(), editedObjectStroke.getLineJoin(), 0.0F, editedObjectStroke.getDashArray(), 0.0F);
/*     */ 
/* 313 */         this.val$chartObject.setStroke(stroke);
/* 314 */         this.val$chartsActionListenerRegistry.drawingChanged(this.val$chartObject);
/*     */ 
/* 316 */         this.val$component.refreshMainContent();
/*     */       }
/*     */     });
/* 320 */     subMenu.add(menuItem);
/*     */   }
/*     */ 
/*     */   JMenu createStrokeSubMenu(ChartObject editedChartObject, GuiRefresher guiRefresher)
/*     */   {
/* 325 */     return new JMenu(LocalizationManager.getText("menu.item.stroke"), editedChartObject, guiRefresher)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   void addDashItem(ChartObject chartObject, GuiRefresher guiRefresher, JMenu subMenu, ChartsActionListenerRegistry chartsActionListenerRegistry, DrawingsHelper.DashPattern dash)
/*     */   {
/* 334 */     subMenu.add(new JMenuItem(dash, chartObject, chartsActionListenerRegistry, guiRefresher)
/*     */     {
/*     */     });
/*     */   }
/*     */ 
/*     */   JMenuItem createEditLevelsMenuItem(ChartObject editedChartObject, GuiRefresher guiRefresher, Point locationOnScreen, boolean allowAllPercents)
/*     */   {
/* 373 */     return new JMenuItem(LocalizationManager.getText("menu.item.levels.edit"), guiRefresher, editedChartObject, allowAllPercents, locationOnScreen)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   JMenu createPresetsMenu(ChartObject editedChartObject, GuiRefresher guiRefresher)
/*     */   {
/* 397 */     IChartClient chartClient = DDSChartsControllerImpl.getInstance().getChartClient();
/* 398 */     Map presets = chartClient.restoreHorizontalRetracementPresets(editedChartObject.getType());
/*     */ 
/* 400 */     return new JMenu(LocalizationManager.getText("menu.item.presets"), presets, editedChartObject, guiRefresher)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   void addPresetItem(ChartObject chartObject, GuiRefresher guiRefresher, JMenu subMenu, ChartsActionListenerRegistry chartsActionListenerRegistry, String presetName, List<Object[]> levels)
/*     */   {
/* 416 */     subMenu.add(new JMenuItem(chartObject, levels, presetName, chartsActionListenerRegistry, guiRefresher)
/*     */     {
/*     */     });
/*     */   }
/*     */ 
/*     */   private Point getSafeLocation(Point desiredLocation, Dimension componentSize)
/*     */   {
/* 446 */     int x = desiredLocation.x;
/* 447 */     int y = desiredLocation.y;
/*     */ 
/* 449 */     if (desiredLocation.x + componentSize.width > Toolkit.getDefaultToolkit().getScreenSize().width) {
/* 450 */       x = Toolkit.getDefaultToolkit().getScreenSize().width - componentSize.width;
/*     */     }
/* 452 */     if (desiredLocation.y + componentSize.height > Toolkit.getDefaultToolkit().getScreenSize().height) {
/* 453 */       y = Toolkit.getDefaultToolkit().getScreenSize().height - componentSize.height;
/*     */     }
/* 455 */     Point p = new Point(x, y);
/* 456 */     return p;
/*     */   }
/*     */ 
/*     */   JMenu createColorsSubMenu(ChartObject editedChartObject, GuiRefresher guiRefresher)
/*     */   {
/* 461 */     return new JMenu(LocalizationManager.getText("menu.item.color"), editedChartObject, guiRefresher)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   void addColorItem(ChartObject chartObject, GuiRefresher guiRefresher, JMenu subMenu, ChartsActionListenerRegistry chartsActionListenerRegistry, Color color)
/*     */   {
/* 470 */     subMenu.add(new JMenuItem(chartObject, color, chartsActionListenerRegistry, guiRefresher)
/*     */     {
/*     */     });
/*     */   }
/*     */ 
/*     */   JMenuItem createDeleteMenuItem(ChartObject editedChartObject)
/*     */   {
/* 492 */     return new JMenuItem(LocalizationManager.getText("menu.item.remove"), editedChartObject)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   JMenuItem createEditLabelMenuItem(ChartObject editedChartObject)
/*     */   {
/* 503 */     return new JMenuItem(LocalizationManager.getText("menu.item.label.edit"), editedChartObject)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   void showEditLabelDialog(ChartObject editedChartObject)
/*     */   {
/* 513 */     if (editedChartObject == null) {
/* 514 */       return;
/*     */     }
/*     */ 
/* 517 */     if (editedChartObject.supportsStyledLabel()) {
/* 518 */       if (!this.drawingsManagerContainer.updateTextChartObject((TextChartObject)editedChartObject))
/* 519 */         this.drawingsManagerContainer.remove(editedChartObject);
/*     */     }
/*     */     else {
/* 522 */       String text = editedChartObject.getText();
/* 523 */       String newText = JOptionPane.showInputDialog(LocalizationManager.getText("menu.item.text.edit"), text);
/* 524 */       editedChartObject.setText(newText);
/*     */     }
/*     */   }
/*     */ 
/*     */   JMenuItem createEditCoordinatesMenuItem(ChartObject editedChartObject) {
/* 529 */     return new JMenuItem(LocalizationManager.getText("menu.item.edit.coordinates"), editedChartObject)
/*     */     {
/*     */       private static final long serialVersionUID = 1L;
/*     */     };
/*     */   }
/*     */ 
/*     */   JMenuItem createEditPipsPerBarItem(ChartObject editedChartObject)
/*     */   {
/* 543 */     return new JMenuItem(LocalizationManager.getText("menu.item.edit.pips.per.bar"), editedChartObject)
/*     */     {
/*     */       private static final long serialVersionUID = 1L;
/*     */     };
/*     */   }
/*     */ 
/*     */   void showEditCoordinatesDialog(ChartObject editedChartObject)
/*     */   {
/* 557 */     if (editedChartObject == null) {
/* 558 */       return;
/*     */     }
/*     */ 
/* 561 */     this.drawingsManagerContainer.updateChartObjectPricesManualy(editedChartObject);
/*     */   }
/*     */ 
/*     */   void showEditPipsPerBarDialog(ChartObject editedChartObject) {
/* 565 */     if (editedChartObject == null) {
/* 566 */       return;
/*     */     }
/*     */ 
/* 569 */     this.drawingsManagerContainer.updatePipsPerBarOption(editedChartObject);
/*     */   }
/*     */ 
/*     */   JMenu createOhlcVisibleValuesMenuItem(OhlcChartObject ohlcChartObject, GuiRefresher guiRefresher)
/*     */   {
/* 575 */     return new JMenu(LocalizationManager.getText("menu.item.ohlc.visible.values"), ohlcChartObject, guiRefresher)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   JMenuItem createFixedLevelRightSideMenuItem(FiboRetracementChartObject chartObject, GuiRefresher guiRefresher)
/*     */   {
/* 656 */     return new JCheckBoxMenuItem(LocalizationManager.getText("menu.item.fixed.level.right.side"), chartObject, guiRefresher)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   JMenu createOhlcAlignmentMenuItem(OhlcChartObject ohlcChartObject, GuiRefresher guiRefresher)
/*     */   {
/* 672 */     return new JMenu(LocalizationManager.getText("menu.item.ohlc.alignment"), ohlcChartObject, guiRefresher)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   JMenu createFillOpacityMenuItem(AbstractFillableChartObject editedChartObject, GuiRefresher guiRefresher)
/*     */   {
/* 691 */     return new JMenu(LocalizationManager.getText("menu.item.fill.opacity"), editedChartObject, guiRefresher)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   JMenuItem createNoFillMenuItem(AbstractFillableChartObject editedChartObject, GuiRefresher guiRefresher)
/*     */   {
/* 719 */     return new JCheckBoxMenuItem(LocalizationManager.getText("menu.item.no.fill"), editedChartObject, guiRefresher)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   JMenu createOpacityMenuItem(ChartObject editedChartObject, GuiRefresher guiRefresher)
/*     */   {
/* 738 */     return new JMenu(LocalizationManager.getText("menu.item.opacity"), editedChartObject, guiRefresher)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   JMenu createFillColorMenuItem(AbstractFillableChartObject editedChartObject, GuiRefresher guiRefresher)
/*     */   {
/* 768 */     return new JMenu(LocalizationManager.getText("menu.item.fill.color"), editedChartObject, guiRefresher)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   void addFillColorItem(AbstractFillableChartObject chartObject, GuiRefresher guiRefresher, JMenu subMenu, ChartsActionListenerRegistry chartsActionListenerRegistry, Color color)
/*     */   {
/* 777 */     subMenu.add(new JMenuItem(chartObject, color, chartsActionListenerRegistry, guiRefresher)
/*     */     {
/*     */     });
/*     */   }
/*     */ 
/*     */   JMenuItem createCloneMenuItem(ChartObject editedChartObject)
/*     */   {
/* 799 */     return new JMenuItem(LocalizationManager.getText("menu.item.clone"), editedChartObject)
/*     */     {
/*     */     };
/*     */   }
/*     */ 
/*     */   public void triggerPopupDialogForIndicators(int subPanelId, IndicatorWrapper indicatorToBeEdited, MouseEvent mouseEvent)
/*     */   {
/* 810 */     JPopupMenu popupMenu = new JPopupMenu();
/*     */ 
/* 812 */     JMenuItem editMenuItem = new JMenuItem(LocalizationManager.getText("menu.item.indicator.edit"));
/* 813 */     editMenuItem.addActionListener(new ActionListener(subPanelId, indicatorToBeEdited) {
/*     */       public void actionPerformed(ActionEvent actionEvent) {
/* 815 */         new EditIndicatorDialog(this.val$subPanelId, this.val$indicatorToBeEdited, null, PopupManagerForDrawings.this.indicatorsContainer, PopupManagerForDrawings.this.guiRefresher, PopupManagerForDrawings.this.chartState.getPeriod(), PopupManagerForDrawings.this.chartState.getDataType()).dispose();
/*     */       }
/*     */     });
/* 828 */     JMenuItem deleteMenuItem = new JMenuItem(LocalizationManager.getText("menu.item.indicator.remove"));
/* 829 */     deleteMenuItem.addActionListener(new ActionListener(indicatorToBeEdited) {
/*     */       public void actionPerformed(ActionEvent actionEvent) {
/* 831 */         PopupManagerForDrawings.this.deleteIndicator(this.val$indicatorToBeEdited);
/* 832 */         PopupManagerForDrawings.this.indicatorsManagerImpl.unseletSelectedIndicator();
/* 833 */         PopupManagerForDrawings.this.indicatorsManagerImpl.dehighlightHighlightedIndicator();
/*     */       }
/*     */     });
/* 837 */     popupMenu.add(editMenuItem);
/* 838 */     popupMenu.add(deleteMenuItem);
/*     */ 
/* 840 */     popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
/*     */   }
/*     */ 
/*     */   private void deleteIndicator(IndicatorWrapper indicatorToBeEdited) {
/*     */     try {
/* 845 */       this.indicatorsContainer.deleteIndicator(indicatorToBeEdited);
/*     */     } catch (Exception exc) {
/* 847 */       LOGGER.error(exc.getMessage(), exc);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.PopupManagerForDrawings
 * JD-Core Version:    0.6.0
 */