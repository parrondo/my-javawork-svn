/*     */ package com.dukascopy.charts.dialogs.indicators;
/*     */ 
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.impl.LevelInfo;
/*     */ import com.dukascopy.api.indicators.DoubleRangeDescription;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*     */ import com.dukascopy.charts.ChartProperties;
/*     */ import com.dukascopy.charts.chartbuilder.IDataManagerAndIndicatorsContainer;
/*     */ import com.dukascopy.charts.dialogs.AbsoluteLayout;
/*     */ import com.dukascopy.charts.dialogs.AbsoluteLayoutConstraints;
/*     */ import com.dukascopy.charts.dialogs.indicators.component.spinner.DoubleOptParameterSpinner;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*     */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.FocusListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ 
/*     */ public class IndicatorLevelsController
/*     */ {
/*     */   private final List<LevelInfo> levels;
/*     */   private final JPanel levelsPanel;
/*     */   private final ChangeListener changeListener;
/*     */   private final int levelsPanelWidth;
/*     */   private final IndicatorWrapper indicatorWrapper;
/*     */   private final IDataManagerAndIndicatorsContainer dataManager;
/*  65 */   static final Icon ADD_LEVEL_ICON = new ResizableIcon("edit_table_add.gif");
/*  66 */   static final Icon REMOVE_LEVEL_ICON = new ResizableIcon("edit_table_remove.gif");
/*     */ 
/*  69 */   private double valueMinimum = 0.0D;
/*  70 */   private double valueMaximum = 0.0D;
/*  71 */   private double stepSize = 0.0D;
/*  72 */   private int scale = 0;
/*  73 */   private final int PRICE_PERCENT_RESERVE = 100;
/*     */ 
/*     */   public IndicatorLevelsController(IndicatorWrapper indicatorWrapper, IDataManagerAndIndicatorsContainer dataManager, JPanel levelsPanel, ChangeListener changeListener, int levelsPanelWidth)
/*     */   {
/*  90 */     this.indicatorWrapper = indicatorWrapper;
/*  91 */     this.levels = sortLevels(indicatorWrapper.getLevelInfoList());
/*  92 */     this.levelsPanel = levelsPanel;
/*  93 */     this.levelsPanelWidth = levelsPanelWidth;
/*  94 */     this.changeListener = changeListener;
/*  95 */     this.dataManager = dataManager;
/*  96 */     evaluateValueBounds();
/*  97 */     reset();
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 104 */     set(this.levels);
/*     */   }
/*     */ 
/*     */   public void addLevel()
/*     */   {
/* 111 */     LevelInfo levelInfo = new LevelInfo(getNextLevelValue().doubleValue());
/* 112 */     JPanel levelPanel = createLevelPanel(levelInfo);
/*     */ 
/* 114 */     if (levelPanel == null) {
/* 115 */       return;
/*     */     }
/* 117 */     this.levelsPanel.setVisible(false);
/* 118 */     this.levelsPanel.add(levelPanel);
/* 119 */     this.levelsPanel.validate();
/* 120 */     this.levelsPanel.setVisible(true);
/*     */   }
/*     */ 
/*     */   public List<LevelInfo> getLevels()
/*     */   {
/* 127 */     List levels = new ArrayList();
/*     */ 
/* 129 */     for (Component component : this.levelsPanel.getComponents()) {
/* 130 */       JPanel levelPanel = (JPanel)component;
/* 131 */       String label = ((JTextField)levelPanel.getComponent(0)).getText();
/* 132 */       Double value = (Double)((JSpinner)levelPanel.getComponent(1)).getValue();
/* 133 */       Color color = ((ColorJComboBox)levelPanel.getComponent(2)).getSelectedColor();
/* 134 */       OutputParameterInfo.DrawingStyle drawingStyle = (OutputParameterInfo.DrawingStyle)((JComboBox)levelPanel.getComponent(3)).getSelectedItem();
/* 135 */       Integer width = (Integer)((JSpinner)levelPanel.getComponent(4)).getValue();
/* 136 */       Float alpha = (Float)((JComboBox)levelPanel.getComponent(5)).getSelectedItem();
/* 137 */       levels.add(new LevelInfo(label, value.doubleValue(), drawingStyle, color == null ? Color.BLACK : color, width.intValue(), alpha.floatValue()));
/*     */     }
/* 139 */     return sortLevels(levels);
/*     */   }
/*     */ 
/*     */   public Double getIdenticalLevelValue(List<LevelInfo> validationLevels)
/*     */   {
/* 148 */     if ((validationLevels == null) || (validationLevels.isEmpty())) {
/* 149 */       return null;
/*     */     }
/*     */ 
/* 152 */     Set uniqueValues = new HashSet();
/*     */ 
/* 154 */     for (LevelInfo levelInfo : validationLevels) {
/* 155 */       if (uniqueValues.contains(Double.valueOf(levelInfo.getValue()))) {
/* 156 */         return Double.valueOf(levelInfo.getValue());
/*     */       }
/* 158 */       uniqueValues.add(Double.valueOf(levelInfo.getValue()));
/*     */     }
/* 160 */     return null;
/*     */   }
/*     */ 
/*     */   private synchronized void evaluateValueBounds()
/*     */   {
/* 170 */     if (this.indicatorWrapper.shouldBeShownOnSubWin())
/*     */     {
/* 173 */       this.valueMaximum = normalizeDouble(this.dataManager.getMaxPriceFor(Integer.valueOf(this.indicatorWrapper.getId())));
/* 174 */       this.valueMinimum = normalizeDouble(this.dataManager.getMinPriceFor(Integer.valueOf(this.indicatorWrapper.getId())));
/* 175 */       if ((this.valueMaximum == 0.0D) && (this.valueMinimum == 0.0D)) {
/* 176 */         this.stepSize = 0.0D;
/* 177 */         return;
/*     */       }
/* 179 */       double temp = this.valueMaximum;
/* 180 */       this.valueMaximum = Math.max(this.valueMaximum, this.valueMinimum);
/* 181 */       this.valueMinimum = Math.min(temp, this.valueMinimum);
/*     */ 
/* 183 */       double priceInterval = Math.abs(this.valueMaximum - this.valueMinimum);
/* 184 */       double percent = priceInterval / 100.0D;
/*     */ 
/* 187 */       while ((this.stepSize == 0.0D) && (this.scale <= 10)) {
/* 188 */         double modifier = Math.pow(10.0D, this.scale++);
/* 189 */         this.stepSize = (Math.round(percent * modifier) / modifier);
/*     */       }
/* 191 */       if (this.stepSize == 0.0D)
/* 192 */         this.stepSize = 0.001D;
/* 193 */       else if (this.stepSize > 1.0D) {
/* 194 */         this.stepSize = 1.0D;
/*     */       }
/* 196 */       if (this.scale > 0) {
/* 197 */         this.scale -= 1;
/*     */       }
/*     */ 
/* 200 */       this.valueMaximum = new BigDecimal(this.valueMaximum + 100.0D * percent).setScale(this.scale, 6).doubleValue();
/* 201 */       this.valueMinimum = new BigDecimal(this.valueMinimum - 100.0D * percent).setScale(this.scale, 6).doubleValue();
/*     */ 
/* 203 */       this.stepSize = new BigDecimal(this.stepSize).setScale(this.scale, 6).doubleValue();
/*     */     } else {
/* 205 */       this.valueMinimum = ChartProperties.MIN_LEVEL_VALUE.doubleValue();
/* 206 */       this.valueMaximum = ChartProperties.MAX_LEVEL_VALUE.doubleValue();
/* 207 */       this.stepSize = ChartProperties.LEVEL_STEP_SIZE.doubleValue();
/*     */     }
/*     */   }
/*     */ 
/*     */   private Double getNextLevelValue()
/*     */   {
/* 215 */     List levels = getLevels();
/* 216 */     double nextLevelValue = this.valueMinimum;
/* 217 */     if (ObjectUtils.isNullOrEmpty(levels)) {
/* 218 */       nextLevelValue = StratUtils.round((this.valueMaximum + this.valueMinimum) / 2.0D, this.scale);
/*     */     } else {
/* 220 */       double currentValue = ((LevelInfo)levels.get(levels.size() - 1)).getValue();
/* 221 */       nextLevelValue = currentValue + this.stepSize * ChartProperties.NEXT_LEVEL_INCREMENT.doubleValue();
/*     */     }
/* 223 */     return Double.valueOf(nextLevelValue);
/*     */   }
/*     */ 
/*     */   private void deleteLevel(Container levelPanel)
/*     */   {
/* 231 */     this.levelsPanel.setVisible(false);
/* 232 */     this.levelsPanel.remove(levelPanel);
/* 233 */     this.levelsPanel.doLayout();
/* 234 */     this.levelsPanel.setVisible(true);
/*     */   }
/*     */ 
/*     */   private void set(List<LevelInfo> levels)
/*     */   {
/* 242 */     Collections.sort(levels);
/* 243 */     this.levelsPanel.setVisible(false);
/* 244 */     this.levelsPanel.removeAll();
/*     */ 
/* 246 */     for (LevelInfo levelInfo : levels)
/*     */     {
/* 248 */       JPanel levelPanel = createLevelPanel(levelInfo);
/*     */ 
/* 250 */       if (levelPanel == null) {
/*     */         continue;
/*     */       }
/* 253 */       this.levelsPanel.add(levelPanel);
/*     */     }
/* 255 */     this.levelsPanel.setVisible(true);
/*     */   }
/*     */ 
/*     */   private JPanel createLevelPanel(LevelInfo levelInfo)
/*     */   {
/* 266 */     JPanel levelPanel = new JPanel()
/*     */     {
/*     */     };
/* 272 */     JTextField labelField = new JTextField(levelInfo.getLabel());
/* 273 */     labelField.addFocusListener(new FocusListener()
/*     */     {
/*     */       public void focusLost(FocusEvent e) {
/* 276 */         IndicatorLevelsController.this.changeListener.stateChanged(null);
/*     */       }
/*     */ 
/*     */       public void focusGained(FocusEvent e)
/*     */       {
/*     */       }
/*     */     });
/* 284 */     labelField.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 288 */         IndicatorLevelsController.this.changeListener.stateChanged(null);
/*     */       }
/*     */     });
/* 293 */     JSpinner valueSpinner = createValueSpinner(levelInfo.getValue());
/*     */ 
/* 296 */     ColorJComboBox colorComboBox = new ColorJComboBox(levelInfo)
/*     */     {
/*     */     };
/* 307 */     JComboBox styleComboBox = EditIndicatorHelper.createDrawingStyleEditor(levelInfo.getDrawingStyle(), new ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e) {
/* 310 */         IndicatorLevelsController.this.changeListener.stateChanged(null);
/*     */       }
/*     */     });
/* 316 */     JSpinner widthSpinner = EditIndicatorHelper.createWidthEditor(Integer.valueOf(levelInfo.getLineWidth()), new ChangeListener()
/*     */     {
/*     */       public void stateChanged(ChangeEvent e) {
/* 319 */         IndicatorLevelsController.this.changeListener.stateChanged(null);
/*     */       }
/*     */     });
/* 324 */     JComboBox transparencyComboBox = EditIndicatorHelper.createTransparencyEditor(Float.valueOf(levelInfo.getOpacityAlpha()), new ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e) {
/* 327 */         IndicatorLevelsController.this.changeListener.stateChanged(null);
/*     */       }
/*     */     });
/* 332 */     levelPanel.add(labelField, new AbsoluteLayoutConstraints(0, 1, 87, 20));
/* 333 */     levelPanel.add(valueSpinner, new AbsoluteLayoutConstraints(81, 1, 80, 20));
/* 334 */     levelPanel.add(colorComboBox, new AbsoluteLayoutConstraints(160, 1, 55, 20));
/* 335 */     levelPanel.add(styleComboBox, new AbsoluteLayoutConstraints(210, 1, 65, 20));
/* 336 */     levelPanel.add(widthSpinner, new AbsoluteLayoutConstraints(270, 1, 58, 20));
/* 337 */     levelPanel.add(transparencyComboBox, new AbsoluteLayoutConstraints(324, 1, 105, 20));
/*     */ 
/* 339 */     levelPanel.add(createDeleteButton(), new AbsoluteLayoutConstraints(430, 1, 20, 20));
/*     */ 
/* 341 */     return levelPanel;
/*     */   }
/*     */ 
/*     */   private JSpinner createValueSpinner(double value)
/*     */   {
/* 351 */     value = Math.max(Math.min(value, this.valueMaximum), this.valueMinimum);
/* 352 */     DoubleRangeDescription doubleRangeDescription = new DoubleRangeDescription(value, this.valueMinimum, this.valueMaximum, this.stepSize, this.scale);
/* 353 */     JSpinner valueSpinner = new DoubleOptParameterSpinner(Double.valueOf(value), doubleRangeDescription, new PropertyChangeListener()
/*     */     {
/*     */       public void propertyChange(PropertyChangeEvent evt)
/*     */       {
/* 357 */         IndicatorLevelsController.this.changeListener.stateChanged(null);
/*     */       }
/*     */     });
/* 361 */     return valueSpinner;
/*     */   }
/*     */ 
/*     */   private List<LevelInfo> sortLevels(List<LevelInfo> levelsToSort) {
/* 365 */     Collections.sort(levelsToSort);
/* 366 */     return levelsToSort;
/*     */   }
/*     */ 
/*     */   private JLocalizableButton createDeleteButton() {
/* 370 */     JLocalizableButton button = new JLocalizableButton(REMOVE_LEVEL_ICON);
/* 371 */     button.setBorderPainted(false);
/*     */ 
/* 374 */     button.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 377 */         IndicatorLevelsController.this.deleteLevel(((JComponent)JComponent.class.cast(e.getSource())).getParent());
/* 378 */         IndicatorLevelsController.this.indicatorWrapper.setLevelInfoList(IndicatorLevelsController.this.getLevels());
/* 379 */         IndicatorLevelsController.this.changeListener.stateChanged(null);
/*     */       }
/*     */     });
/* 382 */     button.setVisible(true);
/* 383 */     button.setEnabled(true);
/* 384 */     return button;
/*     */   }
/*     */ 
/*     */   private double normalizeDouble(double value)
/*     */   {
/* 393 */     if ((Double.isInfinite(value)) || (Double.isNaN(value))) {
/* 394 */       value = 0.0D;
/*     */     }
/* 396 */     return value;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.IndicatorLevelsController
 * JD-Core Version:    0.6.0
 */