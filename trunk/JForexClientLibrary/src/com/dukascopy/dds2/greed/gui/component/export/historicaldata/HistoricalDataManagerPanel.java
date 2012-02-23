/*      */ package com.dukascopy.dds2.greed.gui.component.export.historicaldata;
/*      */ 
/*      */ import com.dukascopy.api.DataType;
/*      */ import com.dukascopy.api.Filter;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.PriceRange;
/*      */ import com.dukascopy.api.ReversalAmount;
/*      */ import com.dukascopy.api.TickBarSize;
/*      */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*      */ import com.dukascopy.charts.settings.ChartSettings.DailyFilter;
/*      */ import com.dukascopy.charts.utils.ChartsLocalizator;
/*      */ import com.dukascopy.dds2.greed.export.historicaldata.CancelExportDataActionListener;
/*      */ import com.dukascopy.dds2.greed.export.historicaldata.CompositePeriod;
/*      */ import com.dukascopy.dds2.greed.export.historicaldata.CompositePeriod.Type;
/*      */ import com.dukascopy.dds2.greed.export.historicaldata.DataField;
/*      */ import com.dukascopy.dds2.greed.export.historicaldata.ExportDataParameters;
/*      */ import com.dukascopy.dds2.greed.export.historicaldata.ExportFormat;
/*      */ import com.dukascopy.dds2.greed.export.historicaldata.ExportInstrumentParameter;
/*      */ import com.dukascopy.dds2.greed.export.historicaldata.ExportOfferSide;
/*      */ import com.dukascopy.dds2.greed.export.historicaldata.ExportProcessControl;
/*      */ import com.dukascopy.dds2.greed.export.historicaldata.ExportProcessControl.State;
/*      */ import com.dukascopy.dds2.greed.export.historicaldata.ExportProcessControlListener;
/*      */ import com.dukascopy.dds2.greed.export.historicaldata.HistoricalDataManagerBean;
/*      */ import com.dukascopy.dds2.greed.export.historicaldata.PeriodType;
/*      */ import com.dukascopy.dds2.greed.export.historicaldata.StartExportDataActionListener;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.CardLayoutPanel;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*      */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*      */ import com.toedter.calendar.IDateEditor;
/*      */ import com.toedter.calendar.JCalendar;
/*      */ import com.toedter.calendar.JDateChooser;
/*      */ import com.toedter.calendar.JTextFieldDateEditor;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.FlowLayout;
/*      */ import java.awt.Font;
/*      */ import java.awt.FontMetrics;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.GridBagConstraints;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.awt.Insets;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.ItemEvent;
/*      */ import java.awt.event.ItemListener;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import javax.swing.ComboBoxEditor;
/*      */ import javax.swing.DefaultListCellRenderer;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JFormattedTextField;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JList;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JProgressBar;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JSpinner.NumberEditor;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.event.TableModelEvent;
/*      */ import javax.swing.event.TableModelListener;
/*      */ import javax.swing.text.AttributeSet;
/*      */ import javax.swing.text.BadLocationException;
/*      */ import javax.swing.text.DefaultFormatter;
/*      */ import javax.swing.text.PlainDocument;
/*      */ 
/*      */ public class HistoricalDataManagerPanel extends JPanel
/*      */ {
/*      */   private static final String ID_TESTER_VALUE_COMBOBOX = "ID_TESTER_VALUE_COMBOBOX";
/*      */   private static final String ID_TESTER_POINTANDFIGURE = "ID_TESTER_VALUE_PFPANEL";
/*  103 */   private InstrumentSelectionTable instrumentSelectionTable = null;
/*  104 */   private InstrumentSelectionTableModel instrumentSelectionTableModel = null;
/*      */   private JComboBox dateFormatsCombo;
/*      */   private JComboBox delimiterCombo;
/*      */   private JComboBox offerSideCombo;
/*      */   private JComboBox dataTypeCombo;
/*      */   private JComboBox valueCombo;
/*      */   private JComboBox flatsFilterCombo;
/*      */   private JComboBox sundFilterCombo;
/*      */   private JSpinnerHint boxSpinner;
/*      */   private JSpinnerHint reveralAmountSpinner;
/*      */   private CustomDateChooser dateFrom;
/*      */   private CustomDateChooser dateTo;
/*      */   private JComboBox formatCombo;
/*  122 */   private ExportProcessControl exportProcessControl = null;
/*  123 */   private ExportDataParameters exportDataParameters = new ExportDataParameters();
/*      */   private CardLayoutPanel valuePanel;
/*      */   private static final int PANEL_WIDTH = 916;
/*      */ 
/*      */   public HistoricalDataManagerPanel()
/*      */   {
/*  130 */     this.exportProcessControl = new ExportProcessControl();
/*      */   }
/*      */ 
/*      */   public void build() {
/*  134 */     this.instrumentSelectionTableModel = new InstrumentSelectionTableModel();
/*  135 */     this.instrumentSelectionTable = new InstrumentSelectionTable(this.instrumentSelectionTableModel, this.exportProcessControl);
/*      */ 
/*  137 */     setLayout(new GridBagLayout());
/*  138 */     GridBagConstraints gbc = new GridBagConstraints();
/*      */ 
/*  141 */     gbc.fill = 0;
/*  142 */     gbc.anchor = 21;
/*  143 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 1, 10, 0, 0, gbc, this, getCSVSettingsPanel());
/*      */ 
/*  145 */     JPanel middlePanel = new JPanel();
/*  146 */     middlePanel.setLayout(new GridBagLayout());
/*      */ 
/*  148 */     this.flatsFilterCombo = new CustomComboBox(Filter.values(), 110, 22)
/*      */     {
/*      */       public void translate()
/*      */       {
/*  152 */         if (HistoricalDataManagerPanel.this.flatsFilterCombo != null) {
/*  153 */           HistoricalDataManagerPanel.this.flatsFilterCombo.revalidate();
/*  154 */           HistoricalDataManagerPanel.this.flatsFilterCombo.repaint();
/*      */         }
/*      */       }
/*      */     };
/*  159 */     this.flatsFilterCombo.setRenderer(new DefaultListCellRenderer()
/*      */     {
/*      */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*      */       {
/*  163 */         Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/*      */ 
/*  165 */         if (((value instanceof Filter)) && ((comp instanceof JLabel)))
/*      */         {
/*  167 */           Filter filterValue = (Filter)value;
/*  168 */           String key = "";
/*      */ 
/*  170 */           if (filterValue == Filter.NO_FILTER)
/*  171 */             key = "hdm.all.data";
/*  172 */           else if (filterValue == Filter.WEEKENDS)
/*  173 */             key = "hdm.exclude.weekend.flats";
/*  174 */           else if (filterValue == Filter.ALL_FLATS) {
/*  175 */             key = "hdm.exclude.flats";
/*      */           }
/*      */ 
/*  178 */           ((JLabel)comp).setText(LocalizationManager.getText(key));
/*      */         }
/*  180 */         return comp;
/*      */       }
/*      */     });
/*  185 */     this.sundFilterCombo = new CustomComboBox(ChartSettings.DailyFilter.values(), 110, 22)
/*      */     {
/*      */       public void translate()
/*      */       {
/*  189 */         if (HistoricalDataManagerPanel.this.sundFilterCombo != null) {
/*  190 */           HistoricalDataManagerPanel.this.sundFilterCombo.revalidate();
/*  191 */           HistoricalDataManagerPanel.this.sundFilterCombo.repaint();
/*      */         }
/*      */       }
/*      */     };
/*  196 */     this.sundFilterCombo.setEnabled(false);
/*      */ 
/*  198 */     this.sundFilterCombo.setRenderer(new DefaultListCellRenderer()
/*      */     {
/*      */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*      */       {
/*  202 */         Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/*      */ 
/*  204 */         if (((value instanceof ChartSettings.DailyFilter)) && ((comp instanceof JLabel)))
/*      */         {
/*  206 */           ChartSettings.DailyFilter dailyFilter = (ChartSettings.DailyFilter)value;
/*  207 */           String key = "";
/*      */ 
/*  209 */           if (dailyFilter == ChartSettings.DailyFilter.NONE)
/*  210 */             key = "hdm.all.data";
/*  211 */           else if (dailyFilter == ChartSettings.DailyFilter.SKIP_SUNDAY)
/*  212 */             key = "hdm.exclude.sundays";
/*  213 */           else if (dailyFilter == ChartSettings.DailyFilter.SUNDAY_IN_MONDAY) {
/*  214 */             key = "hdm.merge.sunday.monday";
/*      */           }
/*      */ 
/*  217 */           ((JLabel)comp).setText(LocalizationManager.getText(key));
/*      */         }
/*      */ 
/*  220 */         return comp;
/*      */       }
/*      */     });
/*  224 */     gbc.insets.top = 5;
/*  225 */     gbc.insets.left = 5;
/*  226 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 0, gbc, middlePanel, this.flatsFilterCombo);
/*  227 */     GridBagLayoutHelper.add(1, 0, 0.0D, 0.0D, 0, gbc, middlePanel, this.sundFilterCombo);
/*  228 */     GridBagLayoutHelper.add(2, 0, 0.0D, 0.0D, 2, gbc, middlePanel, getDirectorySelectionPanel());
/*      */ 
/*  230 */     gbc.insets.left = 1;
/*  231 */     gbc.insets.top = 3;
/*  232 */     gbc.anchor = 21;
/*  233 */     JPanel thirdPanel = new JPanel(new GridBagLayout());
/*  234 */     GridBagLayoutHelper.add(0, 0, 1.0D, 0.0D, 2, gbc, thirdPanel, getProgressBarPanel());
/*      */ 
/*  236 */     gbc.anchor = 22;
/*  237 */     GridBagLayoutHelper.add(1, 0, 1.0D, 0.0D, 2, gbc, thirdPanel, getControlButtonsPanel());
/*      */ 
/*  239 */     gbc.anchor = 21;
/*  240 */     gbc.gridwidth = 3;
/*  241 */     gbc.insets.top = 1;
/*  242 */     GridBagLayoutHelper.add(0, 1, 1.0D, 0.0D, 2, gbc, middlePanel, thirdPanel);
/*      */ 
/*  245 */     gbc.gridwidth = 1;
/*  246 */     gbc.fill = 0;
/*  247 */     gbc.anchor = 21;
/*  248 */     GridBagLayoutHelper.add(0, 1, 0.0D, 0.0D, 1, 1, 1, 0, 0, 0, gbc, this, middlePanel);
/*      */ 
/*  251 */     gbc.fill = 3;
/*  252 */     gbc.anchor = 18;
/*  253 */     GridBagLayoutHelper.add(0, 2, 1.0D, 1.0D, 1, 1, 5, 5, 0, 0, gbc, this, getInstrumentTablePane());
/*      */ 
/*  255 */     this.exportProcessControl.setInitialState();
/*      */   }
/*      */ 
/*      */   public void initTradableInstruments() {
/*  259 */     this.instrumentSelectionTableModel.initTradableInstruments();
/*      */   }
/*      */ 
/*      */   private JScrollPane getInstrumentTablePane() {
/*  263 */     JScrollPane jScrollPane = new JScrollPane(this.instrumentSelectionTable);
/*  264 */     jScrollPane.setMinimumSize(new Dimension(916, 0));
/*  265 */     jScrollPane.setPreferredSize(new Dimension(916, 0));
/*  266 */     return jScrollPane;
/*      */   }
/*      */ 
/*      */   private JPanel getDirectorySelectionPanel() {
/*  270 */     DirectorySelectionPanel directorySelectionPanel = new DirectorySelectionPanel(this.exportProcessControl, this.exportDataParameters);
/*      */ 
/*  275 */     return directorySelectionPanel;
/*      */   }
/*      */ 
/*      */   private JPanel getControlButtonsPanel() {
/*  279 */     JPanel panel = new JPanel();
/*  280 */     panel.setLayout(new GridBagLayout());
/*  281 */     GridBagConstraints gbc = new GridBagConstraints();
/*      */ 
/*  283 */     JButton startButton = new JLocalizableButton("button.start");
/*  284 */     JButton cancelButton = new JLocalizableButton("button.cancel");
/*      */ 
/*  286 */     StartExportDataActionListener exportDataActionListener = new StartExportDataActionListener(this.exportProcessControl, this.exportDataParameters, this);
/*      */ 
/*  292 */     CancelExportDataActionListener cancelExportDataActionListener = new CancelExportDataActionListener(this.exportProcessControl);
/*      */ 
/*  296 */     startButton.addActionListener(exportDataActionListener);
/*  297 */     cancelButton.addActionListener(cancelExportDataActionListener);
/*      */ 
/*  299 */     gbc.anchor = 21;
/*  300 */     gbc.fill = 0;
/*  301 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 0, 0, 0, 0, gbc, panel, cancelButton);
/*  302 */     GridBagLayoutHelper.add(1, 0, 0.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, panel, startButton);
/*      */ 
/*  304 */     this.exportProcessControl.addExportControlListener(new ExportProcessControlListener(startButton, cancelButton)
/*      */     {
/*      */       public void stateChanged(ExportProcessControl.State state) {
/*  307 */         switch (HistoricalDataManagerPanel.14.$SwitchMap$com$dukascopy$dds2$greed$export$historicaldata$ExportProcessControl$State[state.ordinal()]) {
/*      */         case 1:
/*  309 */           HistoricalDataManagerPanel.this.setButtonsInitialState(this.val$startButton, this.val$cancelButton);
/*  310 */           HistoricalDataManagerPanel.this.setUIInitialState();
/*  311 */           break;
/*      */         case 2:
/*  313 */           HistoricalDataManagerPanel.this.setButtonsExecutingState(this.val$startButton, this.val$cancelButton);
/*  314 */           HistoricalDataManagerPanel.this.setUIExecutionState();
/*  315 */           break;
/*      */         case 3:
/*  317 */           HistoricalDataManagerPanel.this.setButtonsInitialState(this.val$startButton, this.val$cancelButton);
/*  318 */           HistoricalDataManagerPanel.this.setUIInitialState();
/*  319 */           break;
/*      */         case 4:
/*  321 */           HistoricalDataManagerPanel.this.setButtonsInitialState(this.val$startButton, this.val$cancelButton);
/*  322 */           HistoricalDataManagerPanel.this.setUIInitialState();
/*  323 */           break;
/*      */         default:
/*  324 */           throw new IllegalArgumentException("Incorrect state type: " + state);
/*      */         }
/*      */       }
/*      */ 
/*      */       public void progressChanged(int progressValue, String progressBarText)
/*      */       {
/*      */       }
/*      */ 
/*      */       public void validated(DataField dataField, boolean error, Instrument instrument, int column, String errorText)
/*      */       {
/*      */       }
/*      */     });
/*  336 */     this.instrumentSelectionTableModel.addTableModelListener(new TableModelListener(startButton)
/*      */     {
/*      */       public void tableChanged(TableModelEvent e) {
/*  339 */         HistoricalDataManagerPanel.this.updateStartButton(this.val$startButton);
/*      */       }
/*      */     });
/*  343 */     return panel;
/*      */   }
/*      */ 
/*      */   private void updateStartButton(JButton startButton) {
/*  347 */     if (this.instrumentSelectionTableModel.getSelectedInstrumentsCount() > 0)
/*  348 */       startButton.setEnabled(true);
/*      */     else
/*  350 */       startButton.setEnabled(false);
/*      */   }
/*      */ 
/*      */   private void setButtonsInitialState(JButton startButton, JButton cancelButton)
/*      */   {
/*  355 */     updateStartButton(startButton);
/*  356 */     cancelButton.setEnabled(false);
/*      */   }
/*      */ 
/*      */   private void setButtonsExecutingState(JButton startButton, JButton cancelButton) {
/*  360 */     startButton.setEnabled(false);
/*  361 */     cancelButton.setEnabled(true);
/*      */   }
/*      */ 
/*      */   private void setUIInitialState() {
/*  365 */     this.instrumentSelectionTable.setEnabled(true);
/*      */   }
/*      */ 
/*      */   private void setUIExecutionState() {
/*  369 */     this.instrumentSelectionTable.setEnabled(false);
/*      */   }
/*      */ 
/*      */   private JPanel getProgressBarPanel() {
/*  373 */     JPanel panel = new JPanel(new FlowLayout(0, 4, 0));
/*  374 */     JProgressBar progressBar = new JProgressBar(0, 100)
/*      */     {
/*      */       public Dimension getMaximumSize() {
/*  377 */         Dimension size = super.getMaximumSize();
/*  378 */         size.width = 747;
/*  379 */         return size;
/*      */       }
/*      */ 
/*      */       public Dimension getMinimumSize() {
/*  383 */         Dimension size = super.getMinimumSize();
/*  384 */         size.width = 747;
/*  385 */         return size;
/*      */       }
/*      */ 
/*      */       public Dimension getPreferredSize() {
/*  389 */         Dimension size = super.getPreferredSize();
/*  390 */         size.width = 747;
/*  391 */         return size;
/*      */       }
/*      */     };
/*  394 */     progressBar.setValue(0);
/*  395 */     progressBar.setString("");
/*  396 */     progressBar.setStringPainted(true);
/*  397 */     JLabel progressLabel = new JLabel("  0%");
/*  398 */     panel.add(progressBar);
/*  399 */     panel.add(progressLabel);
/*      */ 
/*  401 */     this.exportProcessControl.addExportControlListener(new ExportProcessControlListener(progressBar, progressLabel)
/*      */     {
/*      */       public void stateChanged(ExportProcessControl.State state) {
/*  404 */         switch (HistoricalDataManagerPanel.14.$SwitchMap$com$dukascopy$dds2$greed$export$historicaldata$ExportProcessControl$State[state.ordinal()]) {
/*      */         case 1:
/*  406 */           break;
/*      */         case 2:
/*  408 */           this.val$progressBar.setString(LocalizationManager.getText("hdm.downloading.started"));
/*  409 */           this.val$progressBar.setValue(0);
/*  410 */           this.val$progressLabel.setText("  0%");
/*  411 */           break;
/*      */         case 3:
/*  413 */           this.val$progressBar.setValue(100);
/*  414 */           this.val$progressBar.setString(LocalizationManager.getText("hdm.downloading.finished"));
/*  415 */           this.val$progressLabel.setText("100%");
/*  416 */           break;
/*      */         case 4:
/*  418 */           this.val$progressBar.setString(LocalizationManager.getText("hdm.downloading.canceled"));
/*  419 */           break;
/*      */         default:
/*  420 */           throw new IllegalArgumentException("Incorrect state type: " + state);
/*      */         }
/*      */       }
/*      */ 
/*      */       public void progressChanged(int progressValue, String progressBarText)
/*      */       {
/*  426 */         this.val$progressBar.setValue(progressValue);
/*  427 */         this.val$progressBar.setString(progressBarText);
/*      */ 
/*  429 */         if (progressValue < 10)
/*  430 */           this.val$progressLabel.setText("  " + progressValue + "%");
/*  431 */         else if (progressValue < 100)
/*  432 */           this.val$progressLabel.setText(" " + progressValue + "%");
/*      */         else
/*  434 */           this.val$progressLabel.setText(progressValue + "%");
/*      */       }
/*      */ 
/*      */       public void validated(DataField dataField, boolean error, Instrument instrument, int column, String errorText)
/*      */       {
/*      */       }
/*      */     });
/*  442 */     return panel;
/*      */   }
/*      */ 
/*      */   private void setComponentSize(Component comp, int width, int height, int additionalWidth) {
/*  446 */     comp.setMinimumSize(new Dimension(width, height));
/*  447 */     comp.setPreferredSize(new Dimension(width, height));
/*  448 */     comp.setMaximumSize(new Dimension(width + additionalWidth, height));
/*      */   }
/*      */ 
/*      */   private JPanel getCSVSettingsPanel() {
/*  452 */     JPanel panel = new JPanel();
/*  453 */     panel.setMinimumSize(new Dimension(916, 40));
/*  454 */     panel.setLayout(new GridBagLayout());
/*      */ 
/*  456 */     GridBagConstraints gbc = new GridBagConstraints();
/*  457 */     gbc.anchor = 21;
/*  458 */     gbc.insets.left = 5;
/*      */ 
/*  460 */     int additionalWidth = 50;
/*      */ 
/*  462 */     String[] dateFormats = { "yyyy.MM.dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "dd.MM.yyyy HH:mm:ss", "dd-MM-yyyy HH:mm:ss", "dd/MM/yyyy HH:mm:ss" };
/*      */ 
/*  471 */     String[] dateFormatsMillisec = { "yyyy.MM.dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss.SSS", "yyyy/MM/dd HH:mm:ss.SSS", "dd.MM.yyyy HH:mm:ss.SSS", "dd-MM-yyyy HH:mm:ss.SSS", "dd/MM/yyyy HH:mm:ss.SSS" };
/*      */ 
/*  480 */     this.dateFormatsCombo = new JComboBox();
/*  481 */     this.dateFormatsCombo.addItem(" YYYY.MM.DD HH:MM:SS");
/*  482 */     this.dateFormatsCombo.addItem(" YYYY-MM-DD HH:MM:SS");
/*  483 */     this.dateFormatsCombo.addItem(" YYYY/MM/DD HH:MM:SS");
/*      */ 
/*  485 */     this.dateFormatsCombo.addItem(" DD.MM.YYYY HH:MM:SS");
/*  486 */     this.dateFormatsCombo.addItem(" DD-MM-YYYY HH:MM:SS");
/*  487 */     this.dateFormatsCombo.addItem(" DD/MM/YYYY HH:MM:SS");
/*      */ 
/*  489 */     int prefferedHeight = 22;
/*      */ 
/*  491 */     Font font = this.dateFormatsCombo.getFont();
/*  492 */     FontMetrics fontMetrics = this.dateFormatsCombo.getFontMetrics(font);
/*  493 */     int width = fontMetrics.stringWidth("YYYY-MM-DD HH:MM");
/*  494 */     this.dateFormatsCombo.setPreferredSize(new Dimension(width + 50, prefferedHeight));
/*      */ 
/*  496 */     this.delimiterCombo = new JComboBox();
/*  497 */     this.delimiterCombo.setEditable(true);
/*  498 */     ((JTextField)this.delimiterCombo.getEditor().getEditorComponent()).setDocument(new LimitDocument(2));
/*      */ 
/*  500 */     this.delimiterCombo.addItem(",");
/*  501 */     this.delimiterCombo.addItem(".");
/*  502 */     this.delimiterCombo.setPreferredSize(new Dimension(55, prefferedHeight));
/*      */ 
/*  504 */     Map periodsByExport = this.instrumentSelectionTableModel.getPeriodsByExport();
/*      */ 
/*  506 */     this.offerSideCombo = new CustomComboBox(ExportOfferSide.values(), ExportOfferSide.BID, 85, prefferedHeight);
/*  507 */     this.dataTypeCombo = new CustomComboBox(PeriodType.values(), PeriodType.Minutes, 115, prefferedHeight);
/*  508 */     this.formatCombo = new CustomComboBox(ExportFormat.values(), ExportFormat.CSV, 55, prefferedHeight);
/*      */ 
/*  510 */     List periodsForMinutes = (List)((Map)periodsByExport.get(ExportFormat.CSV)).get(PeriodType.Minutes);
/*  511 */     this.valueCombo = new CustomComboBox(periodsForMinutes.toArray(), 115, prefferedHeight);
/*      */ 
/*  513 */     JSpinner.NumberEditor jsEditor = null;
/*  514 */     DefaultFormatter formatter = null;
/*      */ 
/*  516 */     SpinnerHintNumberModel boxSpinnerModel = new SpinnerHintNumberModel(1, 1, PriceRange.MAXIMAL_PIP_COUNT, 1, "label.caption.box.size.in.pips");
/*  517 */     this.boxSpinner = new JSpinnerHint(boxSpinnerModel, "label.caption.box.size.in.pips");
/*      */ 
/*  519 */     jsEditor = (JSpinner.NumberEditor)this.boxSpinner.getEditor();
/*  520 */     formatter = (DefaultFormatter)jsEditor.getTextField().getFormatter();
/*  521 */     formatter.setAllowsInvalid(false);
/*      */ 
/*  523 */     SpinnerHintNumberModel reveralAmountSpinnerModel = new SpinnerHintNumberModel(1, 1, ReversalAmount.MAXIMAL_REVERSAL_AMOUNT, 1, "label.caption.reversal.amount");
/*  524 */     this.reveralAmountSpinner = new JSpinnerHint(reveralAmountSpinnerModel, "label.caption.reversal.amount");
/*      */ 
/*  526 */     jsEditor = (JSpinner.NumberEditor)this.reveralAmountSpinner.getEditor();
/*  527 */     formatter = (DefaultFormatter)jsEditor.getTextField().getFormatter();
/*  528 */     formatter.setAllowsInvalid(false);
/*      */ 
/*  530 */     CustomTextFieldDateEditor dateFromEditor = new CustomTextFieldDateEditor();
/*      */ 
/*  532 */     Calendar cal = Calendar.getInstance();
/*  533 */     cal.setTime(new Date(System.currentTimeMillis()));
/*  534 */     cal.set(5, 1);
/*  535 */     cal.set(2, 0);
/*      */ 
/*  537 */     this.dateFrom = new CustomDateChooser(null, cal.getTime(), "yyyy.MM.dd", dateFromEditor);
/*  538 */     dateFromEditor.setDateChooser(this.dateFrom);
/*  539 */     setComponentSize(this.dateFrom, 90, prefferedHeight, 10);
/*      */ 
/*  541 */     CustomTextFieldDateEditor dateToEditor = new CustomTextFieldDateEditor();
/*  542 */     this.dateTo = new CustomDateChooser(null, new Date(System.currentTimeMillis()), "yyyy.MM.dd", dateToEditor);
/*  543 */     dateToEditor.setDateChooser(this.dateTo);
/*  544 */     setComponentSize(this.dateTo, 90, prefferedHeight, 10);
/*      */ 
/*  546 */     JPanel valueComboPanel = new JPanel(new FlowLayout(0, 0, 0));
/*  547 */     valueComboPanel.add(this.valueCombo);
/*      */ 
/*  549 */     JPanel pfPanel = new JPanel(new FlowLayout(0, 3, 0));
/*  550 */     pfPanel.add(this.boxSpinner);
/*  551 */     pfPanel.add(this.reveralAmountSpinner);
/*      */ 
/*  553 */     this.valuePanel = new CardLayoutPanel();
/*  554 */     this.valuePanel.add(valueComboPanel, "ID_TESTER_VALUE_COMBOBOX");
/*  555 */     this.valuePanel.add(pfPanel, "ID_TESTER_VALUE_PFPANEL");
/*  556 */     this.valuePanel.showComponent("ID_TESTER_VALUE_COMBOBOX");
/*      */ 
/*  560 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 2, gbc, panel, new JLocalizableLabel("hdm.date.from"));
/*  561 */     GridBagLayoutHelper.add(1, 0, 0.0D, 0.0D, 2, gbc, panel, new JLocalizableLabel("hdm.date.to"));
/*  562 */     GridBagLayoutHelper.add(2, 0, 0.0D, 0.0D, 2, gbc, panel, new JLocalizableLabel("hdm.export.format"));
/*  563 */     GridBagLayoutHelper.add(3, 0, 0.0D, 0.0D, 2, gbc, panel, new JLocalizableLabel("hdm.date.format"));
/*  564 */     GridBagLayoutHelper.add(4, 0, 0.0D, 0.0D, 2, gbc, panel, new JLocalizableLabel("hdm.delimiter"));
/*  565 */     GridBagLayoutHelper.add(5, 0, 0.0D, 0.0D, 2, gbc, panel, new JLocalizableLabel("hdm.offer.side"));
/*  566 */     GridBagLayoutHelper.add(6, 0, 0.0D, 0.0D, 2, gbc, panel, new JLocalizableLabel("hdm.data.type"));
/*  567 */     GridBagLayoutHelper.add(7, 0, 0.0D, 0.0D, 2, gbc, panel, new JLocalizableLabel("hdm.value"));
/*      */ 
/*  571 */     GridBagLayoutHelper.add(0, 1, 0.0D, 0.0D, 2, gbc, panel, this.dateFrom);
/*  572 */     GridBagLayoutHelper.add(1, 1, 0.0D, 0.0D, 2, gbc, panel, this.dateTo);
/*  573 */     GridBagLayoutHelper.add(2, 1, 0.0D, 0.0D, 2, gbc, panel, this.formatCombo);
/*  574 */     GridBagLayoutHelper.add(3, 1, 0.0D, 0.0D, 2, gbc, panel, this.dateFormatsCombo);
/*  575 */     GridBagLayoutHelper.add(4, 1, 0.0D, 0.0D, 2, gbc, panel, this.delimiterCombo);
/*  576 */     GridBagLayoutHelper.add(5, 1, 0.0D, 0.0D, 2, gbc, panel, this.offerSideCombo);
/*  577 */     GridBagLayoutHelper.add(6, 1, 0.0D, 0.0D, 2, gbc, panel, this.dataTypeCombo);
/*  578 */     GridBagLayoutHelper.add(7, 1, 1.0D, 0.0D, 2, gbc, panel, this.valuePanel);
/*      */ 
/*  581 */     this.valueCombo.setRenderer(new DefaultListCellRenderer()
/*      */     {
/*      */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*      */       {
/*  585 */         Component component = super.getListCellRendererComponent(list, HistoricalDataManagerPanel.this.valueCombo, index, isSelected, cellHasFocus);
/*      */ 
/*  587 */         if ((component instanceof JLabel))
/*      */         {
/*  589 */           JLabel label = (JLabel)component;
/*  590 */           String text = "";
/*      */ 
/*  592 */           if ((value instanceof CompositePeriod)) {
/*  593 */             CompositePeriod compositePeriod = (CompositePeriod)value;
/*      */ 
/*  595 */             if (compositePeriod.getType() == CompositePeriod.Type.PERIOD) {
/*  596 */               Period period = compositePeriod.getPeriod();
/*  597 */               text = ChartsLocalizator.getLocalized(period);
/*  598 */             } else if (compositePeriod.getType() == CompositePeriod.Type.TICKBARSIZE) {
/*  599 */               TickBarSize tickBarSize = compositePeriod.getTickBarSize();
/*  600 */               text = ChartsLocalizator.getLocalized(tickBarSize);
/*      */             }
/*      */           }
/*  603 */           else if ((value instanceof PriceRange)) {
/*  604 */             PriceRange range = (PriceRange)value;
/*  605 */             text = ChartsLocalizator.getLocalized(range);
/*      */           }
/*      */ 
/*  608 */           label.setText(text);
/*      */         }
/*  610 */         return component;
/*      */       }
/*      */     });
/*  615 */     this.valueCombo.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  618 */         if ((HistoricalDataManagerPanel.this.valueCombo.getSelectedItem() instanceof CompositePeriod)) {
/*  619 */           CompositePeriod selectedPeriod = (CompositePeriod)HistoricalDataManagerPanel.this.valueCombo.getSelectedItem();
/*  620 */           if ((selectedPeriod.getPeriod() == Period.DAILY) && (selectedPeriod.getPeriod().getNumOfUnits() == 1))
/*  621 */             HistoricalDataManagerPanel.this.sundFilterCombo.setEnabled(true);
/*      */           else
/*  623 */             HistoricalDataManagerPanel.this.sundFilterCombo.setEnabled(false);
/*      */         }
/*      */       }
/*      */     });
/*  629 */     this.dataTypeCombo.addItemListener(new ItemListener(periodsByExport)
/*      */     {
/*      */       public void itemStateChanged(ItemEvent e)
/*      */       {
/*  633 */         if (e.getStateChange() == 1) {
/*  634 */           PeriodType selectedPeriodType = (PeriodType)HistoricalDataManagerPanel.this.dataTypeCombo.getSelectedItem();
/*      */ 
/*  636 */           if (selectedPeriodType != null)
/*      */           {
/*  638 */             HistoricalDataManagerPanel.this.valueCombo.removeAllItems();
/*      */ 
/*  640 */             if (selectedPeriodType.equals(PeriodType.Tick))
/*      */             {
/*  642 */               HistoricalDataManagerPanel.this.valueCombo.setEnabled(false);
/*  643 */               HistoricalDataManagerPanel.this.valuePanel.showComponent("ID_TESTER_VALUE_COMBOBOX");
/*      */ 
/*  645 */               HistoricalDataManagerPanel.this.offerSideCombo.setSelectedIndex(-1);
/*  646 */               HistoricalDataManagerPanel.this.offerSideCombo.setEnabled(false);
/*      */             }
/*  648 */             else if (selectedPeriodType.equals(PeriodType.Range))
/*      */             {
/*  650 */               List priceRanges = HistoricalDataManagerPanel.this.instrumentSelectionTableModel.getPriceRanges();
/*  651 */               for (PriceRange range : priceRanges) {
/*  652 */                 HistoricalDataManagerPanel.this.valueCombo.addItem(range);
/*      */               }
/*      */ 
/*  655 */               HistoricalDataManagerPanel.this.valueCombo.setEnabled(true);
/*  656 */               HistoricalDataManagerPanel.this.valuePanel.showComponent("ID_TESTER_VALUE_COMBOBOX");
/*      */ 
/*  658 */               HistoricalDataManagerPanel.this.offerSideCombo.setSelectedItem(ExportOfferSide.BID);
/*  659 */               HistoricalDataManagerPanel.this.offerSideCombo.setEnabled(true);
/*      */             }
/*  661 */             else if (selectedPeriodType.equals(PeriodType.PF)) {
/*  662 */               HistoricalDataManagerPanel.this.valuePanel.showComponent("ID_TESTER_VALUE_PFPANEL");
/*      */             }
/*      */             else {
/*  665 */               HistoricalDataManagerPanel.this.offerSideCombo.setSelectedItem(ExportOfferSide.BID);
/*  666 */               HistoricalDataManagerPanel.this.offerSideCombo.setEnabled(true);
/*      */ 
/*  668 */               ExportFormat selectedFormat = (ExportFormat)HistoricalDataManagerPanel.this.formatCombo.getSelectedItem();
/*      */ 
/*  670 */               Map composidePeriodsByType = (Map)this.val$periodsByExport.get(selectedFormat);
/*  671 */               List periods = (List)composidePeriodsByType.get(selectedPeriodType);
/*      */ 
/*  673 */               for (CompositePeriod period : periods) {
/*  674 */                 HistoricalDataManagerPanel.this.valueCombo.addItem(period);
/*      */               }
/*      */ 
/*  677 */               HistoricalDataManagerPanel.this.valueCombo.setEnabled(true);
/*  678 */               HistoricalDataManagerPanel.this.valuePanel.showComponent("ID_TESTER_VALUE_COMBOBOX");
/*      */             }
/*      */ 
/*  681 */             HistoricalDataManagerPanel.this.flatsFilterCombo.setEnabled(!selectedPeriodType.equals(PeriodType.PF));
/*      */ 
/*  683 */             boolean sundFilterComboEnabled = selectedPeriodType.equals(PeriodType.Days);
/*      */ 
/*  685 */             if ((HistoricalDataManagerPanel.this.valueCombo.getSelectedItem() instanceof CompositePeriod)) {
/*  686 */               CompositePeriod selectedPeriod = (CompositePeriod)HistoricalDataManagerPanel.this.valueCombo.getSelectedItem();
/*  687 */               if ((selectedPeriod.getPeriod() == Period.DAILY) && (selectedPeriod.getPeriod().getNumOfUnits() == 1))
/*  688 */                 sundFilterComboEnabled &= true;
/*      */               else {
/*  690 */                 sundFilterComboEnabled &= false;
/*      */               }
/*      */             }
/*      */ 
/*  694 */             HistoricalDataManagerPanel.this.sundFilterCombo.setEnabled(sundFilterComboEnabled);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*  700 */     this.formatCombo.addActionListener(new Object()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  705 */         ExportFormat selectedFormat = (ExportFormat)HistoricalDataManagerPanel.this.formatCombo.getSelectedItem();
/*  706 */         List periodTypes = (List)HistoricalDataManagerPanel.this.instrumentSelectionTableModel.getPeriodsTypesByExport().get(selectedFormat);
/*      */ 
/*  708 */         PeriodType selectedPeriodType = (PeriodType)HistoricalDataManagerPanel.this.dataTypeCombo.getSelectedItem();
/*  709 */         Object selectedValue = HistoricalDataManagerPanel.this.valueCombo.getSelectedItem();
/*      */ 
/*  711 */         HistoricalDataManagerPanel.this.dataTypeCombo.removeAllItems();
/*      */ 
/*  713 */         for (PeriodType periodType : periodTypes) {
/*  714 */           HistoricalDataManagerPanel.this.dataTypeCombo.addItem(periodType);
/*      */         }
/*      */ 
/*  717 */         if (selectedPeriodType.isHSTCompatible()) {
/*  718 */           HistoricalDataManagerPanel.this.dataTypeCombo.setSelectedItem(selectedPeriodType);
/*  719 */           HistoricalDataManagerPanel.this.valueCombo.setSelectedItem(selectedValue);
/*      */         } else {
/*  721 */           HistoricalDataManagerPanel.this.dataTypeCombo.setSelectedItem(PeriodType.Minutes);
/*      */         }
/*      */ 
/*  724 */         HistoricalDataManagerPanel.this.dateFormatsCombo.setEnabled(selectedFormat == ExportFormat.CSV);
/*  725 */         HistoricalDataManagerPanel.this.delimiterCombo.setEnabled(selectedFormat == ExportFormat.CSV);
/*      */       }
/*      */     });
/*  729 */     this.exportProcessControl.addExportControlListener(new Object(dateFormats, dateFormatsMillisec)
/*      */     {
/*      */       public void stateChanged(ExportProcessControl.State state) {
/*  732 */         switch (HistoricalDataManagerPanel.14.$SwitchMap$com$dukascopy$dds2$greed$export$historicaldata$ExportProcessControl$State[state.ordinal()]) {
/*      */         case 1:
/*  734 */           HistoricalDataManagerPanel.this.dateFormatsCombo.setEnabled(true);
/*  735 */           HistoricalDataManagerPanel.this.delimiterCombo.setEnabled(true);
/*      */ 
/*  738 */           if ((HistoricalDataManagerPanel.this.exportDataParameters.getDelimiter() != null) && (HistoricalDataManagerPanel.this.exportDataParameters.getDelimiter().length() > 0)) {
/*  739 */             HistoricalDataManagerPanel.this.delimiterCombo.setSelectedItem(HistoricalDataManagerPanel.this.exportDataParameters.getDelimiter());
/*      */           }
/*      */ 
/*  742 */           if ((HistoricalDataManagerPanel.this.exportDataParameters.getDateFormat() == null) || (HistoricalDataManagerPanel.this.exportDataParameters.getDateFormat().length() <= 0)) break;
/*  743 */           for (int i = 0; i < this.val$dateFormats.length; i++)
/*  744 */             if (this.val$dateFormats[i].equalsIgnoreCase(HistoricalDataManagerPanel.this.exportDataParameters.getDateFormat())) {
/*  745 */               HistoricalDataManagerPanel.this.dateFormatsCombo.setSelectedIndex(i);
/*  746 */               break;
/*      */             }
/*  743 */           break;
/*      */         case 2:
/*  754 */           int selIndex = HistoricalDataManagerPanel.this.dateFormatsCombo.getSelectedIndex();
/*  755 */           String dateFormat = this.val$dateFormats[selIndex];
/*  756 */           String dateFormatMillisec = this.val$dateFormatsMillisec[selIndex];
/*  757 */           HistoricalDataManagerPanel.this.exportDataParameters.setDateFormat(dateFormat.trim());
/*  758 */           HistoricalDataManagerPanel.this.exportDataParameters.setDateFormatMillisec(dateFormatMillisec.trim());
/*      */ 
/*  760 */           String delimiter = (String)HistoricalDataManagerPanel.this.delimiterCombo.getSelectedItem();
/*  761 */           HistoricalDataManagerPanel.this.exportDataParameters.setDelimiter(delimiter);
/*      */ 
/*  764 */           PeriodType periodType = (PeriodType)HistoricalDataManagerPanel.this.dataTypeCombo.getSelectedItem();
/*      */ 
/*  766 */           CompositePeriod period = periodType.equals(PeriodType.Tick) ? new CompositePeriod(CompositePeriod.Type.PERIOD, Period.TICK) : null;
/*  767 */           PriceRange priceRange = null;
/*      */ 
/*  769 */           Object selectedItem = HistoricalDataManagerPanel.this.valueCombo.getSelectedItem();
/*      */ 
/*  771 */           if (selectedItem != null) {
/*  772 */             if ((selectedItem instanceof CompositePeriod)) {
/*  773 */               period = (CompositePeriod)selectedItem;
/*      */ 
/*  775 */               if ((period.getPeriod() != null) && (period.getPeriod().equals(Period.DAILY)) && (period.getPeriod().getNumOfUnits() == 1))
/*      */               {
/*  777 */                 ChartSettings.DailyFilter dailyFilter = (ChartSettings.DailyFilter)HistoricalDataManagerPanel.this.sundFilterCombo.getSelectedItem();
/*      */ 
/*  779 */                 if (dailyFilter == ChartSettings.DailyFilter.SKIP_SUNDAY)
/*  780 */                   period.setPeriod(Period.DAILY_SKIP_SUNDAY);
/*  781 */                 else if (dailyFilter == ChartSettings.DailyFilter.SUNDAY_IN_MONDAY) {
/*  782 */                   period.setPeriod(Period.DAILY_SUNDAY_IN_MONDAY);
/*      */                 }
/*      */               }
/*      */             }
/*  786 */             else if ((selectedItem instanceof PriceRange)) {
/*  787 */               priceRange = (PriceRange)selectedItem;
/*      */             }
/*      */           }
/*      */ 
/*  791 */           JForexPeriod jForexPeriod = null;
/*      */ 
/*  793 */           if (periodType == PeriodType.PF) {
/*  794 */             jForexPeriod = new JForexPeriod(DataType.POINT_AND_FIGURE);
/*  795 */             jForexPeriod.setPriceRange(PriceRange.valueOf(((Integer)HistoricalDataManagerPanel.this.boxSpinner.getValue()).intValue()));
/*  796 */             jForexPeriod.setReversalAmount(ReversalAmount.valueOf(((Integer)HistoricalDataManagerPanel.this.reveralAmountSpinner.getValue()).intValue()));
/*      */           }
/*      */ 
/*  799 */           Long dateFromValue = Long.valueOf(0L);
/*  800 */           if (HistoricalDataManagerPanel.this.dateFrom.getDate() != null) {
/*  801 */             dateFromValue = Long.valueOf(HistoricalDataManagerPanel.this.dateFrom.getDate().getTime());
/*      */           }
/*      */ 
/*  804 */           Long dateToValue = Long.valueOf(0L);
/*  805 */           if (HistoricalDataManagerPanel.this.dateTo.getDate() != null) {
/*  806 */             dateToValue = Long.valueOf(HistoricalDataManagerPanel.this.dateTo.getDate().getTime());
/*      */           }
/*      */ 
/*  809 */           ExportFormat exportFormat = (ExportFormat)HistoricalDataManagerPanel.this.formatCombo.getSelectedItem();
/*      */ 
/*  811 */           Filter filter = (Filter)HistoricalDataManagerPanel.this.flatsFilterCombo.getSelectedItem();
/*      */ 
/*  813 */           OfferSide[] offerSide = { null };
/*  814 */           if (HistoricalDataManagerPanel.this.offerSideCombo.getSelectedItem() != null) {
/*  815 */             offerSide = ((ExportOfferSide)HistoricalDataManagerPanel.this.offerSideCombo.getSelectedItem()).getOfferSides();
/*      */           }
/*      */ 
/*  818 */           List selectedInstrumens = HistoricalDataManagerPanel.this.instrumentSelectionTableModel.getSelectedInstruments();
/*      */ 
/*  820 */           List parameters = new ArrayList();
/*      */ 
/*  822 */           for (Instrument instrument : selectedInstrumens)
/*      */           {
/*  824 */             for (int j = 0; j < offerSide.length; j++) {
/*  825 */               ExportInstrumentParameter parameter = new ExportInstrumentParameter(instrument, periodType, period, priceRange, jForexPeriod, offerSide[j], dateFromValue, dateToValue, exportFormat, filter);
/*      */ 
/*  836 */               parameters.add(parameter);
/*      */             }
/*      */           }
/*      */ 
/*  840 */           HistoricalDataManagerPanel.this.exportDataParameters.setExportInstrumentParameters(parameters);
/*      */ 
/*  842 */           HistoricalDataManagerPanel.this.updateToolbarStatus(false);
/*  843 */           break;
/*      */         case 3:
/*  845 */           HistoricalDataManagerPanel.this.updateToolbarStatus(true);
/*  846 */           break;
/*      */         case 4:
/*  848 */           HistoricalDataManagerPanel.this.updateToolbarStatus(true);
/*  849 */           break;
/*      */         default:
/*  850 */           throw new IllegalArgumentException("Incorrect state type: " + state);
/*      */         }
/*      */       }
/*      */ 
/*      */       public void progressChanged(int progressValue, String progressBarText)
/*      */       {
/*      */       }
/*      */ 
/*      */       public void validated(DataField dataField, boolean error, Instrument instrument, int column, String errorText)
/*      */       {
/*  860 */         if (dataField.equals(DataField.EXPORT_DATE_FROM)) {
/*  861 */           HistoricalDataManagerPanel.this.dateFrom.setError(error);
/*  862 */           HistoricalDataManagerPanel.this.dateFrom.setErrorText(errorText);
/*  863 */         } else if (dataField.equals(DataField.EXPORT_DATE_TO)) {
/*  864 */           HistoricalDataManagerPanel.this.dateTo.setError(error);
/*  865 */           HistoricalDataManagerPanel.this.dateTo.setErrorText(errorText);
/*      */         }
/*  867 */         else if (error) {
/*  868 */           System.out.println("An error happened for dataField " + dataField + " " + errorText);
/*      */         }
/*      */       }
/*      */     });
/*  875 */     return panel;
/*      */   }
/*      */ 
/*      */   private void updateToolbarStatus(boolean enabled) {
/*  879 */     this.dateFormatsCombo.setEnabled((enabled) && (this.formatCombo.getSelectedItem() == ExportFormat.CSV));
/*  880 */     this.delimiterCombo.setEnabled((enabled) && (this.formatCombo.getSelectedItem() == ExportFormat.CSV));
/*  881 */     this.offerSideCombo.setEnabled((this.offerSideCombo.getSelectedIndex() > -1) && (enabled));
/*  882 */     this.dataTypeCombo.setEnabled(enabled);
/*      */ 
/*  884 */     this.valueCombo.setEnabled((this.valueCombo.getSelectedIndex() > -1) && (enabled));
/*  885 */     this.boxSpinner.setEnabled(enabled);
/*  886 */     this.reveralAmountSpinner.setEnabled(enabled);
/*      */ 
/*  888 */     this.dateFrom.setEnabled(enabled);
/*  889 */     this.dateTo.setEnabled(enabled);
/*  890 */     this.formatCombo.setEnabled(enabled);
/*      */ 
/*  892 */     this.flatsFilterCombo.setEnabled(enabled);
/*  893 */     this.sundFilterCombo.setEnabled(false);
/*      */ 
/*  895 */     if ((this.valueCombo.getSelectedItem() instanceof CompositePeriod)) {
/*  896 */       CompositePeriod selectedPeriod = (CompositePeriod)this.valueCombo.getSelectedItem();
/*  897 */       if ((selectedPeriod.getPeriod() == Period.DAILY) && (selectedPeriod.getPeriod().getNumOfUnits() == 1))
/*  898 */         this.sundFilterCombo.setEnabled(enabled);
/*      */     }
/*      */   }
/*      */ 
/*      */   public HistoricalDataManagerBean getSettings()
/*      */   {
/*  904 */     HistoricalDataManagerBean historicalDataManagerBean = new HistoricalDataManagerBean();
/*      */ 
/*  906 */     historicalDataManagerBean.setOutputDirectory(this.exportDataParameters.getOutputDirectory());
/*  907 */     historicalDataManagerBean.setDateFormat(this.exportDataParameters.getDateFormat());
/*  908 */     historicalDataManagerBean.setDelimiter(this.exportDataParameters.getDelimiter());
/*      */ 
/*  910 */     return historicalDataManagerBean;
/*      */   }
/*      */ 
/*      */   public void restoreSettings(HistoricalDataManagerBean historicalDataManagerBean) {
/*  914 */     if (historicalDataManagerBean != null) {
/*  915 */       this.exportDataParameters.setOutputDirectory(historicalDataManagerBean.getOutputDirectory());
/*  916 */       this.exportDataParameters.setDateFormat(historicalDataManagerBean.getDateFormat());
/*  917 */       this.exportDataParameters.setDelimiter(historicalDataManagerBean.getDelimiter());
/*  918 */       this.exportProcessControl.setInitialState();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void panelClosed()
/*      */   {
/*      */   }
/*      */ 
/*      */   class CustomTextFieldDateEditor extends JTextFieldDateEditor
/*      */   {
/*      */     private HistoricalDataManagerPanel.CustomDateChooser dateChooser;
/*      */ 
/*      */     public CustomTextFieldDateEditor()
/*      */     {
/* 1005 */       super("##/##/##", '_');
/*      */     }
/*      */ 
/*      */     public void setDateChooser(HistoricalDataManagerPanel.CustomDateChooser dateChooser) {
/* 1009 */       this.dateChooser = dateChooser;
/*      */     }
/*      */ 
/*      */     protected void paintComponent(Graphics g)
/*      */     {
/* 1014 */       super.paintComponent(g);
/*      */ 
/* 1016 */       if ((this.dateChooser != null) && (this.dateChooser.isError())) {
/* 1017 */         g.setColor(Color.RED);
/* 1018 */         g.fillRect(3, 4, 2, getHeight() - 12);
/* 1019 */         g.fillRect(3, 2 + getHeight() - 8, 2, 2);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   class CustomDateChooser extends JDateChooser
/*      */   {
/*  971 */     private boolean error = false;
/*  972 */     private String errorText = "";
/*      */ 
/*      */     public CustomDateChooser(JCalendar jcal, Date date, String dateFormatString, IDateEditor dateEditor) {
/*  975 */       super(date, dateFormatString, dateEditor);
/*      */ 
/*  977 */       if ((dateEditor instanceof JTextFieldDateEditor))
/*  978 */         ((JTextFieldDateEditor)dateEditor).setMargin(new Insets(0, 4, 0, 3));
/*      */     }
/*      */ 
/*      */     public void setError(boolean error) {
/*  982 */       this.error = error;
/*      */ 
/*  984 */       if ((error) && (!this.errorText.isEmpty()))
/*  985 */         ((JTextFieldDateEditor)this.dateEditor).setToolTipText(this.errorText);
/*      */       else
/*  987 */         ((JTextFieldDateEditor)this.dateEditor).setToolTipText(getDateFormatString());
/*      */     }
/*      */ 
/*      */     public boolean isError()
/*      */     {
/*  992 */       return this.error;
/*      */     }
/*      */ 
/*      */     public void setErrorText(String errorText) {
/*  996 */       this.errorText = errorText;
/*      */     }
/*      */   }
/*      */ 
/*      */   class CustomComboBox extends JComboBox
/*      */     implements Localizable
/*      */   {
/*      */     public CustomComboBox(Object[] items, int width, int height)
/*      */     {
/*  943 */       this(items, null, width, height);
/*      */     }
/*      */ 
/*      */     public CustomComboBox(Object[] items, Object selectedItem, int width, int height) {
/*  947 */       super();
/*      */ 
/*  949 */       LocalizationManager.addLocalizable(this);
/*      */ 
/*  951 */       HistoricalDataManagerPanel.this.setComponentSize(this, width, height, 25);
/*      */ 
/*  953 */       if (selectedItem != null)
/*  954 */         setSelectedItem(selectedItem);
/*      */     }
/*      */ 
/*      */     public void localize()
/*      */     {
/*  960 */       setFont(LocalizationManager.getDefaultFont(getFont().getSize()));
/*  961 */       translate();
/*      */     }
/*      */ 
/*      */     protected void translate()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   class LimitDocument extends PlainDocument
/*      */   {
/*      */     int limit;
/*      */ 
/*      */     public LimitDocument(int limit)
/*      */     {
/*  929 */       this.limit = limit;
/*      */     }
/*      */ 
/*      */     public void insertString(int offset, String s, AttributeSet a) throws BadLocationException {
/*  933 */       if (offset + s.length() < this.limit)
/*  934 */         super.insertString(offset, s, a);
/*      */       else
/*  936 */         Toolkit.getDefaultToolkit().beep();
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.export.historicaldata.HistoricalDataManagerPanel
 * JD-Core Version:    0.6.0
 */