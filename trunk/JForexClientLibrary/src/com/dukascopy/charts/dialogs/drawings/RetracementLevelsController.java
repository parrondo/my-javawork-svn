/*     */ package com.dukascopy.charts.dialogs.drawings;
/*     */ 
/*     */ import com.dukascopy.charts.ChartProperties;
/*     */ import com.dukascopy.charts.dialogs.AbsoluteLayout;
/*     */ import com.dukascopy.charts.dialogs.AbsoluteLayoutConstraints;
/*     */ import com.dukascopy.charts.dialogs.indicators.ColorJComboBox;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.SpinnerNumberModel;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ 
/*     */ public class RetracementLevelsController
/*     */ {
/*     */   final List<Object[]> levels;
/*     */   final Color defaultColor;
/*     */   final JPanel levelsPanel;
/*     */   final ChangeListener changeListener;
/*     */   final int levelsPanelWidth;
/*     */   final boolean allowAllPercents;
/*     */ 
/*     */   public RetracementLevelsController(List<Object[]> levels, Color defaultColor, JPanel levelsPanel, ChangeListener changeListener, int levelsPanelWidth, boolean allowAllPercents)
/*     */   {
/*  46 */     this.levels = sortLevels(levels);
/*  47 */     this.defaultColor = defaultColor;
/*  48 */     this.levelsPanel = levelsPanel;
/*  49 */     this.levelsPanelWidth = levelsPanelWidth;
/*  50 */     this.changeListener = changeListener;
/*  51 */     this.allowAllPercents = allowAllPercents;
/*     */ 
/*  53 */     reset();
/*     */   }
/*     */ 
/*     */   public void addLevel() {
/*  57 */     JPanel lastLevelPanel = (JPanel)this.levelsPanel.getComponent(this.levelsPanel.getComponentCount() - 1);
/*  58 */     if (!lastLevelPanel.getComponent(0).isEnabled()) {
/*  59 */       lastLevelPanel.getComponent(0).setEnabled(true);
/*  60 */       lastLevelPanel.getComponent(1).setEnabled(true);
/*  61 */       lastLevelPanel.getComponent(2).setEnabled(true);
/*  62 */       lastLevelPanel.getComponent(3).setEnabled(true);
/*     */ 
/*  64 */       if (this.levelsPanel.getComponentCount() >= 2) {
/*  65 */         JPanel panelBeforeLastPanel = (JPanel)this.levelsPanel.getComponent(this.levelsPanel.getComponentCount() - 2);
/*  66 */         Double previousValue = (Double)((JSpinner)panelBeforeLastPanel.getComponent(1)).getValue();
/*     */ 
/*  68 */         double levelValue = getNextLevelValue(previousValue).doubleValue();
/*     */ 
/*  70 */         if ((levelValue > ChartProperties.MIN_LEVEL_VALUE.doubleValue()) || (levelValue < ChartProperties.MAX_LEVEL_VALUE.doubleValue()))
/*  71 */           ((JSpinner)lastLevelPanel.getComponent(1)).setValue(Double.valueOf(levelValue));
/*     */       }
/*     */     }
/*     */     else {
/*  75 */       Double prevValue = Double.valueOf(getMaximalLevelValue().doubleValue() * 100.0D);
/*  76 */       addNewLevel(prevValue);
/*     */     }
/*     */   }
/*     */ 
/*     */   private Double getMaximalLevelValue() {
/*  81 */     List levels = getLevels();
/*  82 */     if ((levels == null) || (levels.isEmpty())) {
/*  83 */       return Double.valueOf(0.0D);
/*     */     }
/*  85 */     Double max = Double.valueOf(0.0D);
/*  86 */     for (Object[] level : levels) {
/*  87 */       if (max.doubleValue() < ((Double)level[1]).doubleValue()) {
/*  88 */         max = (Double)level[1];
/*     */       }
/*     */     }
/*  91 */     return max;
/*     */   }
/*     */ 
/*     */   private Double getNextLevelValue(Double currentValue) {
/*  95 */     Double nextLevelValue = Double.valueOf(currentValue.doubleValue() + ChartProperties.NEXT_LEVEL_INCREMENT.doubleValue());
/*  96 */     if (this.allowAllPercents) {
/*  97 */       nextLevelValue = Double.valueOf(nextLevelValue.equals(Double.valueOf(100.0D)) ? nextLevelValue.doubleValue() + ChartProperties.NEXT_LEVEL_INCREMENT.doubleValue() : nextLevelValue.doubleValue());
/*     */     }
/*  99 */     if (nextLevelValue.doubleValue() > ChartProperties.MAX_LEVEL_VALUE.doubleValue()) {
/* 100 */       nextLevelValue = ChartProperties.MAX_LEVEL_VALUE;
/*     */     }
/* 102 */     return nextLevelValue;
/*     */   }
/*     */ 
/*     */   public boolean deleteLevels() {
/* 106 */     this.levelsPanel.setVisible(false);
/*     */ 
/* 108 */     List componentsToBeRemoved = new LinkedList();
/* 109 */     int componentsCount = this.levelsPanel.getComponentCount();
/*     */ 
/* 111 */     for (Component levelComponent : this.levelsPanel.getComponents()) {
/* 112 */       JPanel levelPanel = (JPanel)levelComponent;
/* 113 */       if (((JCheckBox)levelPanel.getComponent(3)).isSelected()) {
/* 114 */         componentsToBeRemoved.add(levelComponent);
/*     */       }
/*     */     }
/*     */ 
/* 118 */     for (Component component : componentsToBeRemoved) {
/* 119 */       this.levelsPanel.remove(component);
/*     */     }
/*     */ 
/* 122 */     this.levelsPanel.doLayout();
/* 123 */     this.levelsPanel.setVisible(true);
/*     */ 
/* 125 */     return componentsToBeRemoved.size() == componentsCount - 1;
/*     */   }
/*     */ 
/*     */   public void reset() {
/* 129 */     set(this.levels);
/*     */   }
/*     */ 
/*     */   public void set(List<Object[]> levels) {
/* 133 */     this.levelsPanel.setVisible(false);
/* 134 */     this.levelsPanel.removeAll();
/*     */ 
/* 136 */     ListIterator iterator = levels.listIterator(levels.size());
/* 137 */     while (iterator.hasPrevious()) {
/* 138 */       Object[] level = (Object[])iterator.previous();
/*     */ 
/* 140 */       JPanel levelPanel = createLevelPanel(true, (String)level[0], Double.valueOf(((Double)level[1]).doubleValue() * 100.0D), (Color)level[2]);
/*     */ 
/* 146 */       if (levelPanel == null)
/*     */       {
/*     */         continue;
/*     */       }
/* 150 */       this.levelsPanel.add(levelPanel);
/*     */     }
/* 152 */     this.levelsPanel.setVisible(true);
/*     */   }
/*     */ 
/*     */   private void addNewLevel(Double prevValue) {
/* 156 */     Object[] level = { "", getNextLevelValue(prevValue), null };
/* 157 */     JPanel levelPanel = createLevelPanel(true, (String)level[0], (Double)level[1], (Color)level[2]);
/*     */ 
/* 163 */     if (levelPanel == null) {
/* 164 */       return;
/*     */     }
/*     */ 
/* 167 */     this.levelsPanel.setVisible(false);
/* 168 */     this.levelsPanel.add(levelPanel);
/* 169 */     this.levelsPanel.doLayout();
/* 170 */     this.levelsPanel.setVisible(true);
/*     */   }
/*     */ 
/*     */   JPanel createLevelPanel(boolean isEnabled, String label, Double value, Color color)
/*     */   {
/* 175 */     if ((value.doubleValue() < ChartProperties.MIN_LEVEL_VALUE.doubleValue()) || (value.doubleValue() > ChartProperties.MAX_LEVEL_VALUE.doubleValue())) {
/* 176 */       return null;
/*     */     }
/*     */ 
/* 179 */     JPanel levelPanel = new JPanel()
/*     */     {
/*     */     };
/* 184 */     JTextField labelField = new JTextField(label);
/*     */ 
/* 186 */     JSpinner valueSpinner = new JSpinner(new SpinnerNumberModel(value, ChartProperties.MIN_LEVEL_VALUE, ChartProperties.MAX_LEVEL_VALUE, ChartProperties.LEVEL_STEP_SIZE));
/* 187 */     valueSpinner.addChangeListener(this.changeListener);
/*     */ 
/* 189 */     ColorJComboBox colorComboBox = new ColorJComboBox(color)
/*     */     {
/*     */     };
/* 199 */     JCheckBox checkBox = new JCheckBox();
/*     */ 
/* 201 */     checkBox.addChangeListener(new ChangeListener()
/*     */     {
/*     */       public void stateChanged(ChangeEvent e) {
/* 204 */         RetracementLevelsController.this.changeListener.stateChanged(new ChangeEvent(new Boolean(RetracementLevelsController.this.doesAnySelectionExist())));
/*     */       }
/*     */     });
/* 208 */     labelField.setEnabled(isEnabled);
/* 209 */     valueSpinner.setEnabled(isEnabled);
/* 210 */     colorComboBox.setEnabled(isEnabled);
/* 211 */     checkBox.setEnabled(isEnabled);
/*     */ 
/* 213 */     levelPanel.add(labelField, new AbsoluteLayoutConstraints(10, 1, 70, 20));
/* 214 */     levelPanel.add(valueSpinner, new AbsoluteLayoutConstraints(90, 1, 70, 20));
/* 215 */     levelPanel.add(colorComboBox, new AbsoluteLayoutConstraints(160, 1, 60, 20));
/* 216 */     levelPanel.add(checkBox, new AbsoluteLayoutConstraints(220, 1, 23, 20));
/*     */ 
/* 218 */     if ((!this.allowAllPercents) && ((value.doubleValue() == 0.0D) || (value.doubleValue() == 100.0D))) {
/* 219 */       valueSpinner.setEnabled(false);
/* 220 */       checkBox.setEnabled(false);
/*     */     }
/*     */ 
/* 223 */     return levelPanel;
/*     */   }
/*     */ 
/*     */   public List<Object[]> getLevels() {
/* 227 */     return getLevels(false);
/*     */   }
/*     */ 
/*     */   public List<Object[]> getLevels(boolean useDefaultColor) {
/* 231 */     int levelsCount = this.levelsPanel.getComponentCount();
/*     */ 
/* 233 */     if (levelsCount > 0) {
/* 234 */       JPanel lastLevelPanel = (JPanel)this.levelsPanel.getComponent(levelsCount - 1);
/* 235 */       if (!lastLevelPanel.getComponent(0).isEnabled()) {
/* 236 */         levelsCount--;
/*     */       }
/*     */     }
/*     */ 
/* 240 */     List levels = new ArrayList();
/* 241 */     for (int i = levelsCount - 1; i >= 0; i--) {
/* 242 */       JPanel levelPanel = (JPanel)this.levelsPanel.getComponent(i);
/*     */ 
/* 244 */       String label = ((JTextField)levelPanel.getComponent(0)).getText();
/* 245 */       Double value = Double.valueOf(((Double)((JSpinner)levelPanel.getComponent(1)).getValue()).doubleValue() / 100.0D);
/* 246 */       Color color = ((ColorJComboBox)levelPanel.getComponent(2)).getSelectedColor();
/*     */ 
/* 248 */       if (useDefaultColor) {
/* 249 */         levels.add(new Object[] { label, value, color == null ? this.defaultColor : color });
/*     */       }
/*     */       else
/*     */       {
/* 257 */         levels.add(new Object[] { label, value, color.equals(this.defaultColor) ? null : color });
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 267 */     return sortLevels(levels);
/*     */   }
/*     */ 
/*     */   public List<Object[]> sortLevels(List<Object[]> levelsToSort) {
/* 271 */     Collections.sort(levelsToSort, new Comparator()
/*     */     {
/*     */       public int compare(Object[] o1, Object[] o2) {
/* 274 */         if (((Double)o1[1]).doubleValue() > ((Double)o2[1]).doubleValue()) {
/* 275 */           return -1;
/*     */         }
/*     */ 
/* 278 */         return 1;
/*     */       }
/*     */     });
/* 282 */     return levelsToSort;
/*     */   }
/*     */ 
/*     */   public boolean doesAnySelectionExist() {
/* 286 */     for (Component c : this.levelsPanel.getComponents()) {
/* 287 */       if (((c instanceof Container)) && ((((Container)c).getComponent(3) instanceof JCheckBox)) && 
/* 288 */         (((JCheckBox)((Container)c).getComponent(3)).isSelected())) {
/* 289 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 293 */     return false;
/*     */   }
/*     */ 
/*     */   public Double getIdenticalLevelValue(List<Object[]> validationLevels)
/*     */   {
/* 303 */     if ((validationLevels == null) || (validationLevels.isEmpty())) {
/* 304 */       return null;
/*     */     }
/*     */ 
/* 307 */     for (int i = 0; i < validationLevels.size() - 1; i++) {
/* 308 */       for (int k = i + 1; k < validationLevels.size(); k++) {
/* 309 */         if (((Object[])validationLevels.get(i))[1].equals(((Object[])validationLevels.get(k))[1])) {
/* 310 */           return (Double)((Object[])validationLevels.get(i))[1];
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 315 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.drawings.RetracementLevelsController
 * JD-Core Version:    0.6.0
 */