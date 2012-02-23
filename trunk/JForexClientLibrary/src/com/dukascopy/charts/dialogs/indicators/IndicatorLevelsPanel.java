/*     */ package com.dukascopy.charts.dialogs.indicators;
/*     */ 
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*     */ import com.dukascopy.charts.chartbuilder.IDataManagerAndIndicatorsContainer;
/*     */ import com.dukascopy.charts.dialogs.AbsoluteLayout;
/*     */ import com.dukascopy.charts.dialogs.AbsoluteLayoutConstraints;
/*     */ import com.dukascopy.charts.dialogs.indicators.listener.IndicatorChangeListener;
/*     */ import com.dukascopy.charts.dialogs.indicators.listener.IndicatorChangeListener.ParameterType;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ContainerEvent;
/*     */ import java.awt.event.ContainerListener;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ 
/*     */ public class IndicatorLevelsPanel extends JPanel
/*     */ {
/*     */   private static final long serialVersionUID = 3766287604121466866L;
/*     */   private static final int LEVELS_PANEL_WIDTH = 500;
/*     */   private static final int MAX_SCROLL_PANE_HEIGHT = 220;
/*     */   private static final int LEVEL_ROW_HEIGHT = 23;
/*     */   private final IndicatorWrapper indicatorWrapper;
/*     */   private final IndicatorChangeListener indicatorChangeListener;
/*     */   private final JScrollPane levelsScrollPane;
/*     */   private final JPanel levelsPanel;
/*     */   private final JPanel internalPanel;
/*     */   final IndicatorLevelsController indicatorLevelsController;
/*     */ 
/*     */   public IndicatorLevelsPanel(AddEditIndicatorDialog parent, IndicatorWrapper indicatorWrapper, GuiRefresher guiRefresher, IDataManagerAndIndicatorsContainer indicatorsContainer, IndicatorChangeListener indicatorChangeListener)
/*     */   {
/*  58 */     setLayout(new BoxLayout(this, 2));
/*  59 */     setBorder(new JLocalizableRoundedBorder(this, "title.levels.panel"));
/*     */ 
/*  62 */     this.indicatorChangeListener = indicatorChangeListener;
/*  63 */     this.indicatorWrapper = indicatorWrapper;
/*     */ 
/*  65 */     this.levelsScrollPane = new JScrollPane();
/*  66 */     this.levelsPanel = new JPanel();
/*  67 */     this.internalPanel = new JPanel();
/*     */ 
/*  69 */     initLevelsScrollPane();
/*  70 */     initInternalPanel();
/*  71 */     adjustHeight();
/*  72 */     add(this.levelsScrollPane);
/*     */ 
/*  74 */     ChangeListener changeListener = new ChangeListener() {
/*     */       public void stateChanged(ChangeEvent e) {
/*  76 */         IndicatorLevelsPanel.this.indicatorWrapper.setLevelInfoList(IndicatorLevelsPanel.this.indicatorLevelsController.getLevels());
/*  77 */         IndicatorLevelsPanel.this.indicatorChangeListener.indicatorChanged(IndicatorChangeListener.ParameterType.LEVEL);
/*     */       }
/*     */     };
/*  80 */     this.indicatorLevelsController = new IndicatorLevelsController(indicatorWrapper, indicatorsContainer, this.levelsPanel, changeListener, 500);
/*     */   }
/*     */ 
/*     */   public Dimension getPreferredSize()
/*     */   {
/*  91 */     return getDimension();
/*     */   }
/*     */ 
/*     */   public Dimension getMinimumSize()
/*     */   {
/*  96 */     return getPreferredSize();
/*     */   }
/*     */ 
/*     */   private Dimension getDimension() {
/* 100 */     return new Dimension(500, this.levelsScrollPane.getPreferredSize().height + 23);
/*     */   }
/*     */ 
/*     */   private void initInternalPanel()
/*     */   {
/* 105 */     this.internalPanel.setLayout(new BoxLayout(this.internalPanel, 3));
/* 106 */     this.internalPanel.setPreferredSize(new Dimension(500, 200));
/* 107 */     addHeader(this.internalPanel);
/* 108 */     addLevels(this.internalPanel);
/*     */   }
/*     */ 
/*     */   private void initLevelsScrollPane()
/*     */   {
/* 114 */     this.levelsScrollPane.setPreferredSize(new Dimension(500, 220));
/* 115 */     this.levelsScrollPane.setVerticalScrollBarPolicy(20);
/* 116 */     this.levelsScrollPane.setHorizontalScrollBarPolicy(31);
/* 117 */     this.levelsScrollPane.setBorder(BorderFactory.createEmptyBorder());
/* 118 */     this.levelsScrollPane.setViewportView(this.internalPanel);
/*     */   }
/*     */ 
/*     */   private void addHeader(JPanel internalPanel) {
/* 122 */     JPanel levelsHeaderPanel = new JPanel();
/* 123 */     levelsHeaderPanel.setLayout(new AbsoluteLayout());
/* 124 */     levelsHeaderPanel.setBackground(Color.WHITE);
/* 125 */     levelsHeaderPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
/*     */ 
/* 127 */     JLocalizableLabel labelLabel = new JLocalizableLabel("label");
/* 128 */     labelLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
/* 129 */     levelsHeaderPanel.add(labelLabel, new AbsoluteLayoutConstraints(30, 0, 55, 20));
/*     */ 
/* 131 */     JLocalizableLabel levelLabel = new JLocalizableLabel("label.caption.level");
/* 132 */     levelLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
/* 133 */     levelsHeaderPanel.add(levelLabel, new AbsoluteLayoutConstraints(105, 0, 55, 20));
/*     */ 
/* 135 */     JLocalizableLabel colorLabel = new JLocalizableLabel("table.column.color");
/* 136 */     colorLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
/* 137 */     levelsHeaderPanel.add(colorLabel, new AbsoluteLayoutConstraints(175, 0, 40, 20));
/*     */ 
/* 139 */     JLocalizableLabel styleLabel = new JLocalizableLabel("table.column.style");
/* 140 */     styleLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
/* 141 */     levelsHeaderPanel.add(styleLabel, new AbsoluteLayoutConstraints(230, 0, 45, 20));
/*     */ 
/* 143 */     JLocalizableLabel widthLabel = new JLocalizableLabel("table.column.width");
/* 144 */     widthLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
/* 145 */     levelsHeaderPanel.add(widthLabel, new AbsoluteLayoutConstraints(285, 0, 45, 20));
/*     */ 
/* 147 */     JLocalizableLabel transparencyLabel = new JLocalizableLabel("table.column.transparency");
/* 148 */     transparencyLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
/* 149 */     levelsHeaderPanel.add(transparencyLabel, new AbsoluteLayoutConstraints(345, 0, 80, 20));
/*     */ 
/* 151 */     levelsHeaderPanel.add(createAddButton(), new AbsoluteLayoutConstraints(430, 0, 20, 20));
/*     */ 
/* 153 */     levelsHeaderPanel.setMaximumSize(new Dimension(500, 20));
/* 154 */     internalPanel.add(levelsHeaderPanel);
/*     */   }
/*     */ 
/*     */   private JLocalizableButton createAddButton() {
/* 158 */     JLocalizableButton button = new JLocalizableButton(IndicatorLevelsController.ADD_LEVEL_ICON);
/* 159 */     button.setBorderPainted(false);
/*     */ 
/* 161 */     button.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 164 */         IndicatorLevelsPanel.this.indicatorLevelsController.addLevel();
/* 165 */         IndicatorLevelsPanel.this.indicatorWrapper.setLevelInfoList(IndicatorLevelsPanel.this.indicatorLevelsController.getLevels());
/* 166 */         IndicatorLevelsPanel.this.indicatorChangeListener.indicatorChanged(IndicatorChangeListener.ParameterType.LEVEL);
/*     */       }
/*     */     });
/* 169 */     button.setVisible(true);
/* 170 */     button.setEnabled(true);
/* 171 */     return button;
/*     */   }
/*     */ 
/*     */   private void addLevels(JPanel internalPanel)
/*     */   {
/* 176 */     this.levelsPanel.setLayout(new BoxLayout(this.levelsPanel, 1));
/* 177 */     this.levelsPanel.setPreferredSize(new Dimension(500, 20));
/* 178 */     this.levelsPanel.addContainerListener(new ContainerListener() {
/*     */       public void componentAdded(ContainerEvent e) {
/* 180 */         IndicatorLevelsPanel.this.adjustHeight();
/*     */       }
/*     */ 
/*     */       public void componentRemoved(ContainerEvent e) {
/* 184 */         IndicatorLevelsPanel.this.adjustHeight();
/*     */       }
/*     */     });
/* 188 */     internalPanel.add(this.levelsPanel);
/*     */   }
/*     */ 
/*     */   private void adjustHeight() {
/* 192 */     int count = this.levelsPanel.getComponentCount();
/* 193 */     this.levelsPanel.setMaximumSize(new Dimension(500, count * 23 + 20));
/* 194 */     this.internalPanel.setPreferredSize(this.levelsPanel.getMaximumSize());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.IndicatorLevelsPanel
 * JD-Core Version:    0.6.0
 */