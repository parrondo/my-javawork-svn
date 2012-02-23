/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.util.AbstractCurrencyConverter;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.Currency;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.event.TableModelEvent;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ 
/*     */ public class InstrumentSelectionDialog extends JDialog
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private JTable instrumentsTable;
/*     */   private InstrumentsTableModel instrumentsTableModel;
/*     */   private boolean[] selectedInstruments;
/*     */   private JButton selectAllButton;
/*     */   private JButton selectNoneButton;
/*     */   private JButton okButton;
/*     */   private JButton cancelButton;
/*  45 */   private boolean canceled = true;
/*     */   private Instrument accountCurrencyConversionInstrument;
/*  47 */   private boolean[] instrumentAvailability = new boolean[Instrument.values().length];
/*     */ 
/*     */   public InstrumentSelectionDialog(Set<Instrument> selectedInstruments, Instrument accountCurrencyConversionInstrument) {
/*  50 */     super((JFrame)GreedContext.get("clientGui"), true);
/*     */ 
/*  52 */     setDefaultCloseOperation(2);
/*     */ 
/*  54 */     IFeedDataProvider feedDataProvider = FeedDataProvider.getDefaultInstance();
/*  55 */     for (Instrument instrument : Instrument.values()) {
/*  56 */       if (feedDataProvider.getTimeOfFirstCandle(instrument, Period.TICK) != 9223372036854775807L) {
/*  57 */         this.instrumentAvailability[instrument.ordinal()] = true;
/*     */       }
/*     */     }
/*     */ 
/*  61 */     this.selectedInstruments = new boolean[Instrument.values().length];
/*  62 */     for (Instrument instrument : selectedInstruments) {
/*  63 */       if (isInstrumentTradable(instrument)) {
/*  64 */         this.selectedInstruments[instrument.ordinal()] = true;
/*     */       }
/*     */     }
/*  67 */     this.accountCurrencyConversionInstrument = accountCurrencyConversionInstrument;
/*  68 */     if (accountCurrencyConversionInstrument != null) {
/*  69 */       this.selectedInstruments[accountCurrencyConversionInstrument.ordinal()] = true;
/*     */     }
/*     */ 
/*  73 */     this.instrumentsTableModel = new InstrumentsTableModel(null);
/*  74 */     this.instrumentsTable = new JTable(this.instrumentsTableModel);
/*  75 */     this.instrumentsTable.getColumnModel().getColumn(0).setWidth(30);
/*     */ 
/*  77 */     JPanel mainPanel = new JPanel(new GridBagLayout());
/*  78 */     GridBagConstraints gbc = new GridBagConstraints();
/*  79 */     gbc.fill = 1;
/*  80 */     gbc.anchor = 18;
/*  81 */     GridBagLayoutHelper.add(0, 0, 1.0D, 1.0D, 1, 1, 5, 5, 5, 0, gbc, mainPanel, new JScrollPane(this.instrumentsTable));
/*  82 */     JPanel selectButtonsPanel = new JPanel(new FlowLayout(3));
/*  83 */     this.selectAllButton = new JLocalizableButton("button.select.all");
/*  84 */     this.selectNoneButton = new JLocalizableButton("button.select.none");
/*  85 */     selectButtonsPanel.add(this.selectAllButton);
/*  86 */     selectButtonsPanel.add(this.selectNoneButton);
/*  87 */     GridBagLayoutHelper.add(0, 1, 1.0D, 0.0D, 1, 1, 5, 5, 5, 0, gbc, mainPanel, selectButtonsPanel);
/*     */ 
/*  89 */     JPanel okCancelButtonsPanel = new JPanel(new FlowLayout(4));
/*  90 */     this.okButton = new JLocalizableButton("button.ok");
/*  91 */     this.cancelButton = new JLocalizableButton("button.cancel");
/*  92 */     okCancelButtonsPanel.add(this.okButton);
/*  93 */     okCancelButtonsPanel.add(this.cancelButton);
/*  94 */     GridBagLayoutHelper.add(0, 2, 1.0D, 0.0D, 1, 1, 5, 5, 5, 0, gbc, mainPanel, okCancelButtonsPanel);
/*     */ 
/*  96 */     this.okButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  98 */         InstrumentSelectionDialog.access$102(InstrumentSelectionDialog.this, false);
/*  99 */         InstrumentSelectionDialog.this.dispose();
/*     */       }
/*     */     });
/* 103 */     this.cancelButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 105 */         InstrumentSelectionDialog.access$102(InstrumentSelectionDialog.this, true);
/* 106 */         InstrumentSelectionDialog.this.dispose();
/*     */       }
/*     */     });
/* 109 */     this.selectAllButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 111 */         for (int i = 0; i < InstrumentSelectionDialog.this.selectedInstruments.length; i++) {
/* 112 */           if (InstrumentSelectionDialog.this.isInstrumentTradable(Instrument.values()[i])) {
/* 113 */             InstrumentSelectionDialog.this.selectedInstruments[i] = 1;
/*     */           }
/*     */         }
/* 116 */         InstrumentSelectionDialog.this.instrumentsTableModel.fireTableChanged(new TableModelEvent(InstrumentSelectionDialog.this.instrumentsTableModel, 0, Instrument.values().length - 1, 0));
/*     */       }
/*     */     });
/* 120 */     this.selectNoneButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 122 */         for (int i = 0; i < InstrumentSelectionDialog.this.selectedInstruments.length; i++) {
/* 123 */           InstrumentSelectionDialog.this.selectedInstruments[i] = 0;
/*     */         }
/* 125 */         InstrumentSelectionDialog.this.instrumentsTableModel.fireTableChanged(new TableModelEvent(InstrumentSelectionDialog.this.instrumentsTableModel, 0, Instrument.values().length - 1, 0));
/*     */       }
/*     */     });
/* 129 */     setContentPane(mainPanel);
/* 130 */     setTitle(LocalizationManager.getText("title.instrument.selector"));
/* 131 */     pack();
/* 132 */     setSize(400, 300);
/* 133 */     setLocationRelativeTo((JFrame)GreedContext.get("clientGui"));
/* 134 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   public Set<Instrument> getSelectedInstruments() {
/* 138 */     Set instruments = new LinkedHashSet();
/* 139 */     for (int i = 0; i < this.selectedInstruments.length; i++) {
/* 140 */       if (this.selectedInstruments[i] != 0) {
/* 141 */         instruments.add(Instrument.values()[i]);
/*     */       }
/*     */     }
/* 144 */     return instruments;
/*     */   }
/*     */ 
/*     */   public boolean isCanceled() {
/* 148 */     return this.canceled;
/*     */   }
/*     */ 
/*     */   private boolean isInstrumentTradable(Instrument instrument) {
/* 152 */     return this.instrumentAvailability[instrument.ordinal()];
/*     */   }
/*     */   private class InstrumentsTableModel extends AbstractTableModel {
/*     */     private InstrumentsTableModel() {
/*     */     }
/*     */     public int getColumnCount() {
/* 158 */       return 2;
/*     */     }
/*     */ 
/*     */     public int getRowCount() {
/* 162 */       return Instrument.values().length;
/*     */     }
/*     */ 
/*     */     public Object getValueAt(int rowIndex, int columnIndex) {
/* 166 */       if (columnIndex == 0) {
/* 167 */         return Boolean.valueOf(InstrumentSelectionDialog.this.selectedInstruments[rowIndex]);
/*     */       }
/* 169 */       return Instrument.values()[rowIndex];
/*     */     }
/*     */ 
/*     */     public Class<?> getColumnClass(int columnIndex)
/*     */     {
/* 175 */       if (columnIndex == 0) {
/* 176 */         return Boolean.class;
/*     */       }
/* 178 */       return Object.class;
/*     */     }
/*     */ 
/*     */     public String getColumnName(int column)
/*     */     {
/* 184 */       if (column == 0) {
/* 185 */         return "";
/*     */       }
/* 187 */       return LocalizationManager.getText("label.instrument");
/*     */     }
/*     */ 
/*     */     public boolean isCellEditable(int rowIndex, int columnIndex)
/*     */     {
/* 193 */       return (columnIndex == 0) && (InstrumentSelectionDialog.this.isInstrumentTradable(Instrument.values()[rowIndex])) && ((InstrumentSelectionDialog.this.accountCurrencyConversionInstrument == null) || (rowIndex != InstrumentSelectionDialog.this.accountCurrencyConversionInstrument.ordinal()));
/*     */     }
/*     */ 
/*     */     public void setValueAt(Object value, int rowIndex, int columnIndex)
/*     */     {
/* 198 */       Boolean selected = (Boolean)value;
/* 199 */       if (!selected.booleanValue())
/*     */       {
/* 202 */         Map conversionPairs = AbstractCurrencyConverter.getConversionPairs();
/* 203 */         for (Map.Entry entry : conversionPairs.entrySet()) {
/* 204 */           if (entry.getValue() == Instrument.values()[rowIndex]) {
/* 205 */             Currency currencyToDeselect = (Currency)entry.getKey();
/* 206 */             for (Instrument instrument : Instrument.values()) {
/* 207 */               if ((!instrument.getSecondaryCurrency().equals(currencyToDeselect)) || 
/* 208 */                 (InstrumentSelectionDialog.this.selectedInstruments[instrument.ordinal()] == 0)) continue;
/* 209 */               InstrumentSelectionDialog.this.selectedInstruments[instrument.ordinal()] = 0;
/* 210 */               fireTableCellUpdated(instrument.ordinal(), columnIndex);
/*     */             }
/*     */ 
/* 214 */             break;
/*     */           }
/*     */         }
/*     */       }
/* 218 */       InstrumentSelectionDialog.this.selectedInstruments[rowIndex] = selected.booleanValue();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.InstrumentSelectionDialog
 * JD-Core Version:    0.6.0
 */