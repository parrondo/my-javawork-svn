/*     */ package com.dukascopy.dds2.greed.gui.component.export.historicaldata;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.DataField;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.ExportProcessControl;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.ExportProcessControl.State;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.ExportProcessControlListener;
/*     */ import com.toedter.calendar.IDateEditor;
/*     */ import com.toedter.calendar.JDateChooser;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class CellPanel extends JPanel
/*     */   implements ItemListener, PropertyChangeListener
/*     */ {
/*  23 */   private int columnIndex = -1;
/*  24 */   private int rowIndex = -1;
/*  25 */   private ExportProcessControl exportProcessControl = null;
/*     */   private InstrumentSelectionTableModel tableModel;
/*  27 */   private boolean error = false;
/*     */ 
/*     */   public CellPanel(int columnIndex, int rowIndex, ExportProcessControl exportProcessControl, InstrumentSelectionTableModel tableModel)
/*     */   {
/*  35 */     this.columnIndex = columnIndex;
/*  36 */     this.rowIndex = rowIndex;
/*  37 */     this.tableModel = tableModel;
/*  38 */     this.exportProcessControl = exportProcessControl;
/*  39 */     this.exportProcessControl.addExportControlListener(new ExportProcessControlListener()
/*     */     {
/*     */       public void validated(DataField dataField, boolean error, Instrument instrument, int column, String errorText) {
/*  42 */         if ((DataField.INSTRUMENTS_TABLE_CEll == dataField) && (CellPanel.this.columnIndex == column) && (CellPanel.this.rowIndex == CellPanel.this.tableModel.indexOf(instrument)))
/*     */         {
/*  46 */           CellPanel.this.setError(error);
/*     */         }
/*     */       }
/*     */ 
/*     */       public void stateChanged(ExportProcessControl.State state)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void progressChanged(int progressValue, String progressBarText)
/*     */       {
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void setToolTip(String errorText) {
/*  62 */     Component comp = getComponent(0);
/*  63 */     if ((comp != null) && ((comp instanceof JDateChooser))) {
/*  64 */       JDateChooser dateChooser = (JDateChooser)comp;
/*  65 */       IDateEditor dateEditor = dateChooser.getDateEditor();
/*  66 */       if ((dateEditor != null) && (dateEditor.getUiComponent() != null)) {
/*  67 */         dateEditor.getUiComponent().setToolTipText(errorText);
/*     */       }
/*     */     }
/*  70 */     setToolTipText(errorText);
/*     */   }
/*     */ 
/*     */   public void paintComponent(Graphics g)
/*     */   {
/*  75 */     super.paintComponent(g);
/*  76 */     if (this.error) {
/*  77 */       g.setColor(Color.RED);
/*     */ 
/*  79 */       g.fillRect(1, 2, 2, getHeight() - 8);
/*  80 */       g.fillRect(1, 2 + getHeight() - 6, 2, 2);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isError() {
/*  85 */     return this.error;
/*     */   }
/*     */ 
/*     */   public void setError(boolean error) {
/*  89 */     this.error = error;
/*  90 */     repaint();
/*     */   }
/*     */ 
/*     */   public int getColumnIndex() {
/*  94 */     return this.columnIndex;
/*     */   }
/*     */ 
/*     */   public int getRowIndex() {
/*  98 */     return this.rowIndex;
/*     */   }
/*     */ 
/*     */   public void itemStateChanged(ItemEvent e)
/*     */   {
/* 103 */     resetError();
/*     */   }
/*     */ 
/*     */   public void propertyChange(PropertyChangeEvent evt)
/*     */   {
/* 108 */     if (evt.getNewValue() != null)
/* 109 */       resetError();
/*     */   }
/*     */ 
/*     */   private void resetError()
/*     */   {
/* 114 */     setError(false);
/*     */ 
/* 116 */     repaint();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 121 */     if (obj == null)
/* 122 */       return false;
/* 123 */     if (!(obj instanceof CellPanel)) {
/* 124 */       return false;
/*     */     }
/* 126 */     CellPanel cp = (CellPanel)obj;
/*     */ 
/* 128 */     return (this.columnIndex == cp.getColumnIndex()) && (this.rowIndex == cp.getRowIndex());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.export.historicaldata.CellPanel
 * JD-Core Version:    0.6.0
 */