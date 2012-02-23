/*     */ package com.dukascopy.charts.dialogs.indicators;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.impl.CustIndicatorWrapper;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.api.indicators.IndicatorInfo;
/*     */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*     */ import com.dukascopy.charts.chartbuilder.IDataManagerAndIndicatorsContainer;
/*     */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterIndicatorWrapper;
/*     */ import com.dukascopy.dds2.greed.util.FilePathManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.File;
/*     */ import java.util.List;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.filechooser.FileFilter;
/*     */ 
/*     */ abstract class AddEditIndicatorDialog extends JDialog
/*     */   implements ActionListener
/*     */ {
/*     */   protected static final String CANCEL_ACTION = "cancel";
/*     */   protected static final String ADD_EDIT_ACTION = "add_edit";
/*     */   protected static final String CUSTOM_INDICATOR_ACTION = "custom_indicator";
/*     */   protected JButton addEditButton;
/*     */   protected JButton cancelButton;
/*     */   protected IndicatorSetupPanel setupPanel;
/*     */   protected IndicatorLevelsPanel levelsPanel;
/*     */   protected IndicatorGeneralParamsPanel generalParamsPanel;
/*     */   protected IndicatorWrapper indicatorWrapper;
/*     */   protected List<IndicatorWrapper> indicatorWrappers;
/*     */   protected IndicatorWrapper copyOfOriginalIndicator;
/*     */   protected IDataManagerAndIndicatorsContainer indicatorsContainer;
/*     */   protected GuiRefresher guiRefresher;
/*     */   protected Period period;
/*     */   protected DataType dataType;
/*  49 */   int subChartId = -1;
/*     */ 
/*     */   protected AddEditIndicatorDialog(JFrame parentFrame, IDataManagerAndIndicatorsContainer indicatorsContainer, GuiRefresher guiRefresher, Period period, DataType dataType, int subChartId)
/*     */   {
/*  60 */     this(parentFrame, indicatorsContainer, null, guiRefresher, period, dataType, subChartId);
/*     */   }
/*     */ 
/*     */   protected AddEditIndicatorDialog(JFrame parentFrame, IDataManagerAndIndicatorsContainer indicatorsContainer, IndicatorWrapper indicatorWrapper, GuiRefresher guiRefresher, Period period, DataType dataType, int subChartId)
/*     */   {
/*  73 */     super(parentFrame, true);
/*  74 */     this.indicatorsContainer = indicatorsContainer;
/*  75 */     this.indicatorWrapper = indicatorWrapper;
/*  76 */     this.guiRefresher = guiRefresher;
/*  77 */     this.period = period;
/*  78 */     this.dataType = dataType;
/*  79 */     this.subChartId = subChartId;
/*     */ 
/*  81 */     if ((this.indicatorWrapper != null) && (!(indicatorWrapper instanceof TesterIndicatorWrapper)) && ((indicatorWrapper.getIndicator() == null) || (IndicatorsProvider.getInstance().getIndicatorHolder(indicatorWrapper.getIndicator().getIndicatorInfo().getName()) == null)))
/*     */     {
/*  83 */       JOptionPane.showMessageDialog(parentFrame, "Indicator is no longer available", "Indicator not available", 1);
/*  84 */       return;
/*     */     }
/*     */ 
/*  87 */     setTitle();
/*  88 */     setDefaultCloseOperation(0);
/*  89 */     addWindowListener(new WindowAdapter() {
/*     */       public void windowClosing(WindowEvent e) {
/*  91 */         AddEditIndicatorDialog.this.actionPerformed(new ActionEvent(this, 0, "cancel"));
/*     */       }
/*     */     });
/*  94 */     build();
/*  95 */     setResizable(false);
/*     */   }
/*     */ 
/*     */   protected void positionWindowAndMakeItVisible() {
/*  99 */     adjustSize();
/* 100 */     setLocationRelativeTo(getOwner());
/* 101 */     setVisible(true); } 
/*     */   protected abstract void setTitle();
/*     */ 
/*     */   protected abstract void adjustSize();
/*     */ 
/*     */   protected abstract void build();
/*     */ 
/* 111 */   public CustIndicatorWrapper openCustIndFileChooser() { CustIndicatorWrapper rc = null;
/*     */ 
/* 113 */     File lastOpenDirectory = FilePathManager.getInstance().getIndicatorsFolder();
/*     */ 
/* 115 */     JFileChooser fileChooser = new JFileChooser(lastOpenDirectory);
/* 116 */     fileChooser.setFileSelectionMode(0);
/*     */ 
/* 118 */     fileChooser.addChoosableFileFilter(new FileFilter() {
/*     */       public boolean accept(File dir) {
/* 120 */         return (dir.getName().endsWith(".jfx")) || (dir.isDirectory());
/*     */       }
/*     */ 
/*     */       public String getDescription() {
/* 124 */         return "Dukascopy indicator";
/*     */       }
/*     */     });
/* 127 */     int result = fileChooser.showOpenDialog(this);
/* 128 */     if (0 == result) {
/* 129 */       File tmp = fileChooser.getSelectedFile();
/*     */ 
/* 131 */       if ((tmp != null) && (tmp.exists())) {
/* 132 */         rc = new CustIndicatorWrapper();
/* 133 */         if (tmp.getName().endsWith(".jfx")) {
/* 134 */           rc.setBinaryFile(tmp);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 139 */     return rc; }
/*     */ 
/*     */   void finish()
/*     */   {
/* 143 */     dispose();
/*     */   }
/*     */ 
/*     */   public List<IndicatorWrapper> getIndicators() {
/* 147 */     return this.indicatorWrappers;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.AddEditIndicatorDialog
 * JD-Core Version:    0.6.0
 */