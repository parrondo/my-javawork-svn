/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.AbstractListModel;
/*     */ import javax.swing.DefaultListCellRenderer;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.SwingUtilities;
/*     */ 
/*     */ public abstract class AbstractParameterOptimizerDialog<D, E extends Component> extends JDialog
/*     */ {
/*     */   private boolean modalResult;
/*     */   private ParameterListModel<D> mdlValues;
/*     */   protected E editor;
/*     */ 
/*     */   public AbstractParameterOptimizerDialog(Component parent, String titleKey, E valueEditor)
/*     */   {
/*  46 */     super(SwingUtilities.getWindowAncestor(parent));
/*  47 */     setTitle(LocalizationManager.getText(titleKey));
/*  48 */     this.editor = valueEditor;
/*     */ 
/*  51 */     this.mdlValues = new ParameterListModel(null);
/*  52 */     JList lstValues = new JList(this.mdlValues);
/*  53 */     lstValues.setSelectionMode(2);
/*  54 */     lstValues.setCellRenderer(new DefaultListCellRenderer()
/*     */     {
/*     */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*     */       {
/*  58 */         String text = AbstractParameterOptimizerDialog.this.getValueAsString(value);
/*  59 */         return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
/*     */       }
/*     */     });
/*  62 */     JScrollPane scpList = new JScrollPane(lstValues);
/*     */ 
/*  64 */     JLocalizableButton btnRemoveSelected = new JLocalizableButton("optimizer.dialog.list.button.remove");
/*  65 */     btnRemoveSelected.addActionListener(new ActionListener(lstValues)
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  68 */         Object[] selected = this.val$lstValues.getSelectedValues();
/*  69 */         AbstractParameterOptimizerDialog.this.mdlValues.removeElements(selected);
/*     */       }
/*     */     });
/*  72 */     JLocalizableButton btnOk = new JLocalizableButton("button.ok");
/*  73 */     btnOk.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  76 */         AbstractParameterOptimizerDialog.access$202(AbstractParameterOptimizerDialog.this, true);
/*  77 */         AbstractParameterOptimizerDialog.this.dispose();
/*     */       }
/*     */     });
/*  80 */     JLocalizableButton btnCancel = new JLocalizableButton("button.cancel");
/*  81 */     btnCancel.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  84 */         AbstractParameterOptimizerDialog.access$202(AbstractParameterOptimizerDialog.this, false);
/*  85 */         AbstractParameterOptimizerDialog.this.dispose();
/*     */       }
/*     */     });
/*  88 */     JPanel pnlOkCancelButtons = new JPanel(new GridLayout(1, 2, 5, 0));
/*  89 */     pnlOkCancelButtons.add(btnOk);
/*  90 */     pnlOkCancelButtons.add(btnCancel);
/*     */ 
/*  92 */     JLocalizableButton btnAddString = new JLocalizableButton("optimizer.dialog.list.button.add");
/*  93 */     btnAddString.addActionListener(new Object()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  96 */         Object value = AbstractParameterOptimizerDialog.this.getValue(AbstractParameterOptimizerDialog.this.editor);
/*  97 */         if (value != null)
/*  98 */           AbstractParameterOptimizerDialog.this.mdlValues.addElement(value);
/*     */       }
/*     */     });
/* 103 */     JPanel pnlAddString = new JPanel(new BorderLayout(5, 0));
/* 104 */     pnlAddString.add(this.editor, "Center");
/* 105 */     pnlAddString.add(btnAddString, "East");
/*     */ 
/* 107 */     JPanel pnlMain = new JPanel(new GridBagLayout());
/* 108 */     pnlMain.add(pnlAddString, new GridBagConstraints(0, 0, 2, 1, 1.0D, 0.0D, 17, 1, new Insets(10, 10, 0, 10), 0, 0));
/* 109 */     pnlMain.add(scpList, new GridBagConstraints(0, 1, 2, 1, 1.0D, 1.0D, 17, 1, new Insets(10, 10, 0, 10), 0, 0));
/* 110 */     pnlMain.add(btnRemoveSelected, new GridBagConstraints(0, 2, 1, 1, 0.0D, 0.0D, 16, 0, new Insets(10, 10, 10, 10), 0, 0));
/* 111 */     pnlMain.add(pnlOkCancelButtons, new GridBagConstraints(1, 2, 1, 1, 1.0D, 0.0D, 14, 0, new Insets(10, 10, 10, 10), 0, 0));
/*     */ 
/* 113 */     getContentPane().setLayout(new BorderLayout(0, 10));
/* 114 */     getContentPane().add(pnlMain, "Center");
/*     */   }
/*     */ 
/*     */   protected abstract D getValue(E paramE);
/*     */ 
/*     */   abstract String getValueAsString(D paramD);
/*     */ 
/*     */   public Object[] showModal(D[] selected) {
/* 124 */     setModal(true);
/* 125 */     this.mdlValues.setElements(selected);
/* 126 */     pack();
/* 127 */     setLocationRelativeTo(getOwner());
/* 128 */     setVisible(true);
/* 129 */     if (this.modalResult) {
/* 130 */       return this.mdlValues.getElements();
/*     */     }
/* 132 */     return null;
/*     */   }
/*     */ 
/*     */   private static class ParameterListModel<D> extends AbstractListModel
/*     */   {
/*     */     private List<D> elements;
/*     */ 
/*     */     public Object getElementAt(int index)
/*     */     {
/* 142 */       return this.elements.get(index);
/*     */     }
/*     */ 
/*     */     public int getSize()
/*     */     {
/* 147 */       return this.elements == null ? 0 : this.elements.size();
/*     */     }
/*     */ 
/*     */     public void setElements(D[] values) {
/* 151 */       int oldSize = getSize();
/* 152 */       this.elements = new ArrayList();
/* 153 */       if (values != null) {
/* 154 */         for (Object object : values) {
/* 155 */           this.elements.add(object);
/*     */         }
/*     */       }
/* 158 */       int newSize = getSize();
/*     */ 
/* 160 */       if (newSize > oldSize) {
/* 161 */         fireIntervalAdded(this, oldSize, newSize);
/* 162 */         fireContentsChanged(this, 0, oldSize);
/*     */       }
/* 164 */       else if (newSize > oldSize) {
/* 165 */         fireContentsChanged(this, 0, newSize);
/* 166 */         fireIntervalRemoved(this, newSize, oldSize);
/*     */       }
/*     */       else {
/* 169 */         fireContentsChanged(this, 0, newSize);
/*     */       }
/*     */     }
/*     */ 
/*     */     public Object[] getElements() {
/* 174 */       return this.elements.toArray(new Object[this.elements.size()]);
/*     */     }
/*     */ 
/*     */     public void removeElements(Object[] selected) {
/* 178 */       if (selected != null)
/* 179 */         for (Object object : selected) {
/* 180 */           int index = this.elements.indexOf(object);
/* 181 */           if (index >= 0) {
/* 182 */             this.elements.remove(index);
/* 183 */             fireIntervalRemoved(this, index, index + 1);
/*     */           }
/*     */         }
/*     */     }
/*     */ 
/*     */     public boolean addElement(D element)
/*     */     {
/* 190 */       if ((element != null) && (!this.elements.contains(element))) {
/* 191 */         this.elements.add(element);
/* 192 */         fireIntervalAdded(this, getSize() - 1, getSize());
/* 193 */         return true;
/*     */       }
/* 195 */       return false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.AbstractParameterOptimizerDialog
 * JD-Core Version:    0.6.0
 */