/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Container;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.ArrayList;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ 
/*     */ public abstract class AbstractArrayParameterDialog extends JDialog
/*     */ {
/*     */   private ArrayParameterTableModel mdlValues;
/*     */   private Object[] commited;
/*     */ 
/*     */   public AbstractArrayParameterDialog(Window window, Object[] allElements)
/*     */   {
/*  42 */     super(window);
/*  43 */     this.mdlValues = new ArrayParameterTableModel(allElements);
/*  44 */     initUI();
/*     */   }
/*     */ 
/*     */   private void initUI() {
/*  48 */     JTable tblValues = new JTable();
/*  49 */     tblValues.setModel(this.mdlValues);
/*  50 */     JScrollPane scpTable = new JScrollPane(tblValues);
/*     */ 
/*  52 */     JLocalizableButton btnSelectAll = new JLocalizableButton("button.select.all");
/*  53 */     btnSelectAll.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  56 */         AbstractArrayParameterDialog.this.mdlValues.selectAll();
/*     */       }
/*     */     });
/*  59 */     JLocalizableButton btnSelectNone = new JLocalizableButton("button.select.none");
/*  60 */     btnSelectNone.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  63 */         AbstractArrayParameterDialog.this.mdlValues.selectNone();
/*     */       }
/*     */     });
/*  66 */     JPanel pnlSelectButtons = new JPanel(new GridLayout(1, 2, 5, 0));
/*  67 */     pnlSelectButtons.add(btnSelectAll);
/*  68 */     pnlSelectButtons.add(btnSelectNone);
/*     */ 
/*  70 */     JLocalizableButton btnOk = new JLocalizableButton("button.ok");
/*  71 */     btnOk.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  74 */         if (AbstractArrayParameterDialog.this.commit())
/*  75 */           AbstractArrayParameterDialog.this.dispose();
/*     */       }
/*     */     });
/*  79 */     JLocalizableButton btnCancel = new JLocalizableButton("button.cancel");
/*  80 */     btnCancel.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  83 */         AbstractArrayParameterDialog.access$102(AbstractArrayParameterDialog.this, null);
/*  84 */         AbstractArrayParameterDialog.this.dispose();
/*     */       }
/*     */     });
/*  87 */     JPanel pnlOkCancelButtons = new JPanel(new GridLayout(1, 2, 5, 0));
/*  88 */     pnlOkCancelButtons.add(btnOk);
/*  89 */     pnlOkCancelButtons.add(btnCancel);
/*     */ 
/*  91 */     JPanel pnlMain = new JPanel(new GridBagLayout());
/*  92 */     pnlMain.add(scpTable, new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 17, 1, new Insets(10, 10, 0, 10), 0, 0));
/*  93 */     pnlMain.add(pnlSelectButtons, new GridBagConstraints(0, 1, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(5, 10, 0, 10), 0, 0));
/*  94 */     pnlMain.add(pnlOkCancelButtons, new GridBagConstraints(0, 2, 1, 1, 1.0D, 0.0D, 14, 0, new Insets(10, 10, 10, 10), 0, 0));
/*     */ 
/*  96 */     getContentPane().setLayout(new BorderLayout(0, 10));
/*  97 */     getContentPane().add(pnlMain, "Center");
/*     */   }
/*     */ 
/*     */   protected boolean commit() {
/* 101 */     this.commited = this.mdlValues.getSelectedValues();
/* 102 */     return true;
/*     */   }
/*     */ 
/*     */   public Object[] showModal(Object[] selectedValues) {
/* 106 */     this.commited = null;
/* 107 */     setModal(true);
/* 108 */     this.mdlValues.setSelectedValues(selectedValues);
/* 109 */     setVisible(true);
/* 110 */     return this.commited;
/*     */   }
/*     */ 
/*     */   protected abstract Object getValueAsString(Object paramObject);
/*     */ 
/*     */   private class ArrayParameterTableModel extends AbstractTableModel {
/*     */     private Object[] elements;
/*     */     private boolean[] selected;
/*     */ 
/*     */     public ArrayParameterTableModel(Object[] elements) {
/* 123 */       this.elements = elements;
/* 124 */       this.selected = new boolean[elements.length];
/*     */     }
/*     */ 
/*     */     public void setSelectedValues(Object[] values) {
/* 128 */       for (int i = 0; i < this.elements.length; i++) {
/* 129 */         this.selected[i] = false;
/* 130 */         if (values != null) {
/* 131 */           for (Object anObject : values) {
/* 132 */             if (this.elements[i].equals(anObject)) {
/* 133 */               this.selected[i] = true;
/* 134 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 139 */       fireTableDataChanged();
/*     */     }
/*     */ 
/*     */     public Object[] getSelectedValues() {
/* 143 */       ArrayList result = new ArrayList();
/* 144 */       for (int i = 0; i < this.selected.length; i++) {
/* 145 */         if (this.selected[i] != 0) {
/* 146 */           result.add(this.elements[i]);
/*     */         }
/*     */       }
/* 149 */       return result.toArray(new Object[result.size()]);
/*     */     }
/*     */ 
/*     */     public int getColumnCount()
/*     */     {
/* 154 */       return 2;
/*     */     }
/*     */ 
/*     */     public int getRowCount()
/*     */     {
/* 159 */       return this.elements.length;
/*     */     }
/*     */ 
/*     */     public Class<?> getColumnClass(int columnIndex)
/*     */     {
/* 164 */       switch (columnIndex) {
/*     */       case 0:
/* 166 */         return Boolean.class;
/*     */       }
/* 168 */       return String.class;
/*     */     }
/*     */ 
/*     */     public String getColumnName(int column)
/*     */     {
/* 174 */       switch (column) {
/*     */       case 1:
/* 176 */         return LocalizationManager.getText("optimizer.dialog.select.elements.column.value");
/*     */       }
/* 178 */       return "";
/*     */     }
/*     */ 
/*     */     public Object getValueAt(int rowIndex, int columnIndex)
/*     */     {
/* 184 */       switch (columnIndex) {
/*     */       case 0:
/* 186 */         return Boolean.valueOf(this.selected[rowIndex]);
/*     */       }
/* 188 */       return AbstractArrayParameterDialog.this.getValueAsString(this.elements[rowIndex]);
/*     */     }
/*     */ 
/*     */     public boolean isCellEditable(int rowIndex, int columnIndex)
/*     */     {
/* 194 */       switch (columnIndex) {
/*     */       case 0:
/* 196 */         return true;
/*     */       }
/* 198 */       return false;
/*     */     }
/*     */ 
/*     */     public void setValueAt(Object aValue, int rowIndex, int columnIndex)
/*     */     {
/* 204 */       switch (columnIndex) {
/*     */       case 0:
/* 206 */         this.selected[rowIndex] = (aValue == null ? 0 : ((Boolean)aValue).booleanValue());
/*     */       }
/*     */     }
/*     */ 
/*     */     public void selectAll()
/*     */     {
/* 214 */       for (int i = 0; i < this.selected.length; i++) {
/* 215 */         this.selected[i] = true;
/*     */       }
/* 217 */       fireTableDataChanged();
/*     */     }
/*     */ 
/*     */     public void selectNone() {
/* 221 */       for (int i = 0; i < this.selected.length; i++) {
/* 222 */         this.selected[i] = false;
/*     */       }
/* 224 */       fireTableDataChanged();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.AbstractArrayParameterDialog
 * JD-Core Version:    0.6.0
 */