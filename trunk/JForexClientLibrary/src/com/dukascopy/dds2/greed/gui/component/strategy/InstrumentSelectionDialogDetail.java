/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.ResizableGridLayoutPanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class InstrumentSelectionDialogDetail extends JDialog
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  41 */   private final int DIALOG_WIDTH = 360;
/*  42 */   private final int DIALOG_HEIGHT = 310;
/*     */ 
/*  44 */   private boolean canceled = true;
/*     */   private ResizableGridLayoutPanel resizablePanel;
/*     */   private List<Component> instrumentsCheckBoxes;
/*  49 */   private List<Instrument> tradableInstruments = new ArrayList();
/*     */ 
/*  51 */   private TesterParameters testerParameters = null;
/*     */ 
/*     */   public InstrumentSelectionDialogDetail(TesterParameters testerParameters) {
/*  54 */     super((JFrame)GreedContext.get("clientGui"), true);
/*  55 */     setDefaultCloseOperation(2);
/*     */ 
/*  57 */     if (testerParameters == null) {
/*  58 */       throw new IllegalArgumentException("testerParameters is empty");
/*     */     }
/*     */ 
/*  61 */     this.testerParameters = testerParameters;
/*     */ 
/*  63 */     buildUI();
/*     */ 
/*  65 */     setTitle(LocalizationManager.getText("tester.instruments"));
/*  66 */     setSize(360, 520);
/*  67 */     setMinimumSize(new Dimension(360, 310));
/*     */ 
/*  69 */     setLocationRelativeTo((JFrame)GreedContext.get("clientGui"));
/*     */ 
/*  71 */     setResizable(false);
/*  72 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   private void buildUI()
/*     */   {
/*  77 */     this.resizablePanel = new ResizableGridLayoutPanel(new Insets(10, 10, 5, 10), 0, 0);
/*     */ 
/*  79 */     JLocalizableRoundedBorder roundedBorder = new JLocalizableRoundedBorder(this.resizablePanel, "tester.instruments");
/*  80 */     this.resizablePanel.setBorder(roundedBorder);
/*     */ 
/*  82 */     this.instrumentsCheckBoxes = new ArrayList();
/*     */ 
/*  84 */     initTradableInstruments();
/*     */ 
/*  86 */     for (Instrument instrument : this.tradableInstruments)
/*     */     {
/*  88 */       InstrumentCheckBox checkBox = new InstrumentCheckBox(instrument);
/*  89 */       if (this.testerParameters.getInstruments().contains(instrument)) {
/*  90 */         checkBox.setSelected(true);
/*     */       }
/*  92 */       this.instrumentsCheckBoxes.add(checkBox);
/*     */     }
/*     */ 
/*  95 */     this.resizablePanel.setItems(this.instrumentsCheckBoxes);
/*     */ 
/*  97 */     GridBagConstraints gbc = new GridBagConstraints();
/*  98 */     JPanel mainPanel = getMainPanel();
/*     */ 
/* 101 */     gbc.fill = 2;
/* 102 */     gbc.anchor = 18;
/* 103 */     GridBagLayoutHelper.add(0, 0, 1.0D, 1.0D, 1, 1, 5, 5, 5, 5, gbc, mainPanel, this.resizablePanel);
/*     */ 
/* 106 */     gbc.fill = 1;
/* 107 */     gbc.anchor = 17;
/* 108 */     GridBagLayoutHelper.add(0, 1, 1.0D, 0.0D, 1, 1, 0, 5, 0, 0, gbc, mainPanel, getSelectionControlPanel());
/*     */ 
/* 111 */     gbc.fill = 1;
/* 112 */     gbc.anchor = 17;
/* 113 */     GridBagLayoutHelper.add(0, 3, 1.0D, 0.0D, 1, 1, 0, 5, 0, 0, gbc, mainPanel, getOkCancelPanel());
/*     */ 
/* 115 */     setContentPane(mainPanel);
/*     */   }
/*     */ 
/*     */   private void initTradableInstruments() {
/* 119 */     IFeedDataProvider feedDataProvider = FeedDataProvider.getDefaultInstance();
/* 120 */     List supportedInstruments = feedDataProvider.getInstrumentsSupportedByFileCacheGenerator();
/* 121 */     this.tradableInstruments.addAll(supportedInstruments);
/*     */ 
/* 123 */     Collections.sort(this.tradableInstruments, new Comparator()
/*     */     {
/*     */       public int compare(Instrument o1, Instrument o2) {
/* 126 */         return o1.toString().compareTo(o2.toString());
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private JPanel getMainPanel() {
/* 132 */     JPanel mainPanel = new JPanel(new GridBagLayout());
/* 133 */     return mainPanel;
/*     */   }
/*     */ 
/*     */   private JPanel getSelectionControlPanel() {
/* 137 */     JPanel selectionControlPanel = new JPanel(new FlowLayout(3));
/* 138 */     JButton selectAllButton = new JLocalizableButton("button.select.all");
/* 139 */     JButton selectNoneButton = new JLocalizableButton("button.select.none");
/*     */ 
/* 141 */     selectAllButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 144 */         for (Component checkBox : InstrumentSelectionDialogDetail.this.instrumentsCheckBoxes)
/* 145 */           ((InstrumentSelectionDialogDetail.InstrumentCheckBox)checkBox).setSelected(true);
/*     */       }
/*     */     });
/* 150 */     selectNoneButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 153 */         for (Component checkBox : InstrumentSelectionDialogDetail.this.instrumentsCheckBoxes)
/* 154 */           ((InstrumentSelectionDialogDetail.InstrumentCheckBox)checkBox).setSelected(false);
/*     */       }
/*     */     });
/* 159 */     selectionControlPanel.add(selectAllButton);
/* 160 */     selectionControlPanel.add(selectNoneButton);
/*     */ 
/* 162 */     return selectionControlPanel;
/*     */   }
/*     */ 
/*     */   private JPanel getOkCancelPanel() {
/* 166 */     JPanel okCancelButtonsPanel = new JPanel(new FlowLayout(4));
/*     */ 
/* 168 */     JButton okButton = new JLocalizableButton("button.ok");
/* 169 */     JButton cancelButton = new JLocalizableButton("button.cancel");
/*     */ 
/* 171 */     okButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/* 175 */         Set selectedInstruments = new HashSet();
/*     */ 
/* 177 */         for (Component component : InstrumentSelectionDialogDetail.this.instrumentsCheckBoxes) {
/* 178 */           InstrumentSelectionDialogDetail.InstrumentCheckBox checkBox = (InstrumentSelectionDialogDetail.InstrumentCheckBox)component;
/* 179 */           if (checkBox.isSelected()) {
/* 180 */             selectedInstruments.add(checkBox.getInstrument());
/*     */           }
/*     */         }
/*     */ 
/* 184 */         InstrumentSelectionDialogDetail.this.testerParameters.setInstruments(selectedInstruments);
/*     */ 
/* 186 */         InstrumentSelectionDialogDetail.access$202(InstrumentSelectionDialogDetail.this, false);
/* 187 */         InstrumentSelectionDialogDetail.this.dispose();
/*     */       }
/*     */     });
/* 191 */     cancelButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 194 */         InstrumentSelectionDialogDetail.access$202(InstrumentSelectionDialogDetail.this, true);
/* 195 */         InstrumentSelectionDialogDetail.this.dispose();
/*     */       }
/*     */     });
/* 199 */     okCancelButtonsPanel.add(okButton);
/* 200 */     okCancelButtonsPanel.add(cancelButton);
/*     */ 
/* 202 */     return okCancelButtonsPanel;
/*     */   }
/*     */ 
/*     */   public boolean isCanceled() {
/* 206 */     return this.canceled;
/*     */   }
/*     */ 
/*     */   public TesterParameters getTesterParameters() {
/* 210 */     return this.testerParameters;
/*     */   }
/*     */   class InstrumentCheckBox extends JPanel {
/*     */     private static final long serialVersionUID = 1L;
/*     */     private Instrument instrument;
/*     */     private JCheckBox checkbox;
/*     */ 
/*     */     InstrumentCheckBox(Instrument instrument) {
/* 221 */       if (instrument == null) {
/* 222 */         throw new NullPointerException("Instrument cannot be null");
/*     */       }
/*     */ 
/* 225 */       this.instrument = instrument;
/*     */ 
/* 227 */       setLayout(new BoxLayout(this, 0));
/*     */ 
/* 229 */       setPreferredSize(new Dimension(100, 24));
/* 230 */       setMinimumSize(new Dimension(100, 24));
/* 231 */       setSize(new Dimension(100, 24));
/*     */ 
/* 233 */       this.checkbox = new JCheckBox(instrument.toString());
/* 234 */       this.checkbox.setOpaque(false);
/* 235 */       add(this.checkbox);
/*     */     }
/*     */ 
/*     */     public Instrument getInstrument() {
/* 239 */       return this.instrument;
/*     */     }
/*     */ 
/*     */     public boolean isSelected() {
/* 243 */       return this.checkbox.isSelected();
/*     */     }
/*     */ 
/*     */     public void setSelected(boolean selected) {
/* 247 */       this.checkbox.setSelected(selected);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.InstrumentSelectionDialogDetail
 * JD-Core Version:    0.6.0
 */