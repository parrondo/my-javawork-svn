/*     */ package com.dukascopy.charts.dialogs.indicators;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.impl.CustIndicatorWrapper;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*     */ import com.dukascopy.charts.chartbuilder.IDataManagerAndIndicatorsContainer;
/*     */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class AddIndicatorDialog extends AddEditIndicatorDialog
/*     */ {
/*  36 */   private static final Logger LOGGER = LoggerFactory.getLogger(AddIndicatorDialog.class);
/*  37 */   private static final Dimension SIZE = new Dimension(860, 500);
/*     */   private IndicatorSelectPanel indicatorSelectPanel;
/*     */   protected JButton customIndicatorButton;
/*     */ 
/*     */   public AddIndicatorDialog(JFrame parentFrame, IDataManagerAndIndicatorsContainer indicatorsContainer, GuiRefresher guiRefresher, Period period, DataType dataType)
/*     */   {
/*  49 */     super(parentFrame, indicatorsContainer, guiRefresher, period, dataType, -1);
/*  50 */     positionWindowAndMakeItVisible();
/*     */   }
/*     */ 
/*     */   public AddIndicatorDialog(int subChartId, JFrame parentFrame, IDataManagerAndIndicatorsContainer indicatorsContainer, GuiRefresher guiRefresher, Period period, DataType dataType)
/*     */   {
/*  61 */     super(parentFrame, indicatorsContainer, guiRefresher, period, dataType, subChartId);
/*  62 */     positionWindowAndMakeItVisible();
/*     */   }
/*     */ 
/*     */   protected void adjustSize() {
/*  66 */     setSize(SIZE);
/*  67 */     setMinimumSize(SIZE);
/*     */   }
/*     */ 
/*     */   protected void build() {
/*  71 */     boolean isTicks = (this.period == Period.TICK) && (this.dataType == DataType.TICKS);
/*  72 */     this.setupPanel = new IndicatorSetupPanel(this, this.guiRefresher, this.indicatorsContainer, isTicks, true);
/*     */ 
/*  79 */     buildSelectionPanel();
/*  80 */     this.addEditButton = new JButton(LocalizationManager.getText("button.add.indicator"))
/*     */     {
/*     */     };
/*  84 */     this.cancelButton = new JButton(LocalizationManager.getText("button.cancel"))
/*     */     {
/*     */     };
/*  88 */     this.customIndicatorButton = new JButton(LocalizationManager.getText("button.custom.indicator"))
/*     */     {
/*     */     };
/*  93 */     createGUI();
/*     */ 
/*  96 */     SwingUtilities.invokeLater(new Runnable()
/*     */     {
/*     */       public void run() {
/*  99 */         AddIndicatorDialog.this.indicatorSelectPanel.getQuickSearchTextField().requestFocus();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   protected void buildSelectionPanel() {
/* 106 */     this.indicatorSelectPanel = new IndicatorSelectPanel(this.subChartId != -1, new Object()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 111 */         Object source = e.getSource();
/* 112 */         if ((source instanceof JCheckBox))
/* 113 */           if ((((JCheckBox)source).isSelected()) && (AddIndicatorDialog.this.indicatorWrapper != null)) {
/* 114 */             AddIndicatorDialog.this.setupPanel.setIndicator(AddIndicatorDialog.this.indicatorWrapper);
/* 115 */             AddIndicatorDialog.this.addNew(AddIndicatorDialog.this.indicatorWrapper);
/*     */           } else {
/* 117 */             AddIndicatorDialog.this.removePrevious();
/*     */           }
/*     */       }
/*     */     }
/*     */     , new IndicatorSelectPanel.IndicatorSelectionListener()
/*     */     {
/*     */       public void selectedIndicators(List<String> indicatorNames)
/*     */       {
/* 126 */         AddIndicatorDialog.this.indicatorsListChanged(indicatorNames);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void createGUI() {
/* 133 */     JPanel contentPane = new JPanel(new GridBagLayout());
/* 134 */     GridBagConstraints gbc = new GridBagConstraints();
/* 135 */     layoutSelectionPanel(contentPane, gbc);
/* 136 */     layoutParametersPanel(contentPane, gbc);
/* 137 */     layoutButtons(contentPane, gbc);
/* 138 */     setContentPane(contentPane);
/*     */   }
/*     */ 
/*     */   private void layoutButtons(JPanel contentPane, GridBagConstraints gbc) {
/* 142 */     JPanel buttonsPanel = new JPanel(new FlowLayout());
/* 143 */     buttonsPanel.add(this.customIndicatorButton);
/* 144 */     buttonsPanel.add(Box.createHorizontalStrut(20));
/* 145 */     buttonsPanel.add(this.addEditButton);
/* 146 */     buttonsPanel.add(Box.createHorizontalStrut(20));
/* 147 */     buttonsPanel.add(this.cancelButton);
/*     */ 
/* 149 */     gbc.fill = 3;
/* 150 */     gbc.anchor = 10;
/* 151 */     gbc.gridx = 0;
/* 152 */     gbc.gridy = 2;
/* 153 */     gbc.weightx = 1.0D;
/* 154 */     gbc.weighty = 0.0D;
/* 155 */     gbc.gridwidth = 2;
/* 156 */     gbc.gridheight = 1;
/* 157 */     gbc.insets.left = 5;
/* 158 */     gbc.insets.top = 5;
/* 159 */     gbc.insets.right = 5;
/* 160 */     gbc.insets.bottom = 5;
/* 161 */     contentPane.add(buttonsPanel, gbc);
/*     */   }
/*     */ 
/*     */   protected void layoutParametersPanel(JPanel contentPane, GridBagConstraints gbc) {
/* 165 */     this.setupPanel.setPreferredSize(new Dimension(100, 150));
/* 166 */     gbc.fill = 1;
/* 167 */     gbc.gridx = 0;
/* 168 */     gbc.gridy = 1;
/* 169 */     gbc.weightx = 1.0D;
/* 170 */     gbc.weighty = 0.0D;
/* 171 */     gbc.insets.left = 0;
/* 172 */     gbc.insets.top = 0;
/* 173 */     gbc.insets.right = 0;
/* 174 */     gbc.insets.bottom = 0;
/* 175 */     contentPane.add(this.setupPanel, gbc);
/*     */   }
/*     */ 
/*     */   protected void layoutSelectionPanel(JPanel contentPane, GridBagConstraints gbc) {
/* 179 */     gbc.fill = 1;
/* 180 */     gbc.gridx = 0;
/* 181 */     gbc.gridy = 0;
/* 182 */     gbc.weightx = 1.0D;
/* 183 */     gbc.weighty = 1.0D;
/* 184 */     gbc.insets.left = 0;
/* 185 */     gbc.insets.top = 0;
/* 186 */     gbc.insets.right = 0;
/* 187 */     gbc.insets.bottom = 0;
/* 188 */     contentPane.add(this.indicatorSelectPanel, gbc);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/* 192 */     if (e.getActionCommand().equals("cancel")) {
/* 193 */       processCancelAction();
/* 194 */       this.indicatorWrapper = null;
/* 195 */       this.indicatorWrappers = null;
/* 196 */       finish();
/* 197 */       this.setupPanel.setIndicator(null);
/* 198 */     } else if (e.getActionCommand().equals("add_edit")) {
/* 199 */       if (finishingAllowed()) {
/* 200 */         if (!this.indicatorSelectPanel.isPreviewEnabled()) {
/* 201 */           addNew(this.indicatorWrappers);
/*     */         }
/* 203 */         finish();
/*     */       }
/* 205 */       this.setupPanel.setIndicator(null);
/* 206 */     } else if (e.getActionCommand().equals("custom_indicator")) {
/* 207 */       processCustomIndicatorAction();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processCustomIndicatorAction() {
/* 212 */     CustIndicatorWrapper custIndWrapper = openCustIndFileChooser();
/* 213 */     if (custIndWrapper == null) {
/* 214 */       return;
/*     */     }
/* 216 */     String name = IndicatorsProvider.getInstance().enableIndicator(custIndWrapper, NotificationUtilsProvider.getNotificationUtils());
/* 217 */     if (name != null) {
/* 218 */       this.indicatorSelectPanel.updateGroups();
/* 219 */       this.indicatorSelectPanel.select(name);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void indicatorsListChanged(List<String> names) {
/* 224 */     if (names == null) {
/* 225 */       return;
/*     */     }
/*     */ 
/* 228 */     if (this.indicatorSelectPanel.isPreviewEnabled()) {
/* 229 */       removePrevious();
/*     */     }
/*     */ 
/* 232 */     List indicatorWrappers = new ArrayList();
/*     */     try {
/* 234 */       for (String name : names)
/* 235 */         indicatorWrappers.add(new IndicatorWrapper(name));
/*     */     }
/*     */     catch (Exception e) {
/* 238 */       LOGGER.error(e.getMessage(), e);
/* 239 */       return;
/*     */     }
/*     */ 
/* 242 */     this.setupPanel.setListOfIndicators(indicatorWrappers);
/*     */ 
/* 244 */     this.indicatorWrapper = ((IndicatorWrapper)indicatorWrappers.get(indicatorWrappers.size() - 1));
/* 245 */     this.setupPanel.setIndicator(this.indicatorWrapper);
/*     */ 
/* 247 */     validate();
/*     */ 
/* 249 */     if (this.indicatorSelectPanel.isPreviewEnabled()) {
/* 250 */       addNew(this.indicatorWrapper);
/*     */     }
/*     */ 
/* 253 */     this.indicatorWrappers = indicatorWrappers;
/*     */   }
/*     */ 
/*     */   private void addNew(List<IndicatorWrapper> indicatorWrappers) {
/* 257 */     if (indicatorWrappers != null)
/* 258 */       for (IndicatorWrapper iw : indicatorWrappers) {
/* 259 */         addNew(iw);
/* 260 */         this.subChartId = -1;
/*     */       }
/*     */   }
/*     */ 
/*     */   private void addNew(IndicatorWrapper indicatorWrapper)
/*     */   {
/* 266 */     if ((this.subChartId == -1) && (indicatorWrapper.shouldBeShownOnSubWin())) {
/* 267 */       this.subChartId = this.guiRefresher.createSubChartView();
/*     */     }
/*     */ 
/* 270 */     if (indicatorWrapper.shouldBeShownOnSubWin())
/*     */       try {
/* 272 */         this.guiRefresher.addSubIndicatorToSubChartView(this.subChartId, indicatorWrapper);
/* 273 */         this.indicatorsContainer.addIndicator(indicatorWrapper, this.subChartId);
/*     */       } catch (Exception e) {
/* 275 */         this.guiRefresher.deleteSubIndicatorFromSubChartView(this.subChartId, indicatorWrapper);
/* 276 */         if (this.guiRefresher.isSubViewEmpty(Integer.valueOf(this.subChartId))) {
/* 277 */           this.guiRefresher.deleteSubChartView(Integer.valueOf(this.subChartId));
/*     */         }
/* 279 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     else
/*     */       try {
/* 283 */         this.indicatorsContainer.addIndicator(indicatorWrapper, -1);
/*     */       } catch (Exception e) {
/* 285 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */   }
/*     */ 
/*     */   private void removePrevious()
/*     */   {
/* 291 */     if (this.indicatorWrapper == null) {
/* 292 */       return;
/*     */     }
/*     */ 
/* 295 */     if (this.indicatorWrapper.shouldBeShownOnSubWin()) {
/* 296 */       this.guiRefresher.deleteSubIndicatorFromSubChartView(this.subChartId, this.indicatorWrapper);
/* 297 */       if (this.guiRefresher.isSubViewEmpty(Integer.valueOf(this.subChartId))) {
/* 298 */         this.guiRefresher.deleteSubChartView(Integer.valueOf(this.subChartId));
/* 299 */         this.subChartId = -1;
/*     */       }
/*     */       try {
/* 302 */         this.indicatorsContainer.deleteIndicator(this.indicatorWrapper);
/*     */       } catch (Exception exc) {
/* 304 */         LOGGER.warn("Indicator already removed: " + this.indicatorWrapper.getNameWithParams());
/*     */       }
/*     */     } else {
/*     */       try {
/* 308 */         this.indicatorsContainer.deleteIndicator(this.indicatorWrapper);
/*     */       } catch (Exception exc) {
/* 310 */         LOGGER.warn("Indicator already removed: " + this.indicatorWrapper.getNameWithParams());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean finishingAllowed() {
/* 316 */     if (this.indicatorWrapper != null) {
/* 317 */       return true;
/*     */     }
/*     */ 
/* 320 */     return this.indicatorSelectPanel.isSelectionValid();
/*     */   }
/*     */ 
/*     */   protected void processCancelAction()
/*     */   {
/* 325 */     if (this.indicatorWrapper != null)
/* 326 */       if (this.indicatorWrapper.shouldBeShownOnSubWin()) {
/* 327 */         this.guiRefresher.deleteSubIndicatorFromSubChartView(this.subChartId, this.indicatorWrapper);
/* 328 */         if (this.guiRefresher.isSubViewEmpty(Integer.valueOf(this.subChartId)))
/* 329 */           this.guiRefresher.deleteSubChartView(Integer.valueOf(this.subChartId));
/*     */         else {
/* 331 */           this.guiRefresher.refreshSubContentBySubViewId(this.subChartId);
/*     */         }
/* 333 */         if (this.indicatorsContainer.getIndicators().contains(this.indicatorWrapper)) {
/*     */           try {
/* 335 */             this.indicatorsContainer.deleteIndicator(this.indicatorWrapper);
/*     */           } catch (Exception exc) {
/* 337 */             LOGGER.warn("Indicator was already deleted: " + this.indicatorWrapper.getNameWithParams());
/*     */           }
/*     */         }
/*     */       }
/* 341 */       else if (this.indicatorsContainer.getIndicators().contains(this.indicatorWrapper)) {
/*     */         try {
/* 343 */           this.indicatorsContainer.deleteIndicator(this.indicatorWrapper);
/*     */         } catch (Exception exc) {
/* 345 */           LOGGER.warn("Indicator was already deleted: " + this.indicatorWrapper.getNameWithParams());
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   protected void setTitle()
/*     */   {
/* 353 */     setTitle(LocalizationManager.getText("title.add.indicator"));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.AddIndicatorDialog
 * JD-Core Version:    0.6.0
 */