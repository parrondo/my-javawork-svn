/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.table;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import java.awt.Component;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.FocusListener;
/*     */ import java.util.EventObject;
/*     */ import javax.swing.AbstractCellEditor;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import javax.swing.table.TableCellEditor;
/*     */ import javax.swing.text.Document;
/*     */ 
/*     */ final class CommentsTableCellEditor extends AbstractCellEditor
/*     */   implements TableCellEditor
/*     */ {
/* 232 */   private JTextField textfield = new JTextField();
/*     */ 
/*     */   public CommentsTableCellEditor(StrategyNewBean strategy, ClientSettingsStorage settingsStorage)
/*     */   {
/* 236 */     this.textfield.setHorizontalAlignment(2);
/*     */ 
/* 238 */     this.textfield.addFocusListener(new FocusListener()
/*     */     {
/*     */       public void focusLost(FocusEvent e)
/*     */       {
/* 242 */         CommentsTableCellEditor.this.stopCellEditing();
/*     */       }
/*     */ 
/*     */       public void focusGained(FocusEvent e)
/*     */       {
/*     */       }
/*     */     });
/* 251 */     this.textfield.getDocument().addDocumentListener(new DocumentListener(strategy, settingsStorage)
/*     */     {
/*     */       public void removeUpdate(DocumentEvent e)
/*     */       {
/* 255 */         this.val$strategy.setComments(CommentsTableCellEditor.this.textfield.getText());
/* 256 */         this.val$settingsStorage.saveStrategyNewBean(this.val$strategy);
/*     */       }
/*     */ 
/*     */       public void insertUpdate(DocumentEvent e)
/*     */       {
/* 261 */         this.val$strategy.setComments(CommentsTableCellEditor.this.textfield.getText());
/* 262 */         this.val$settingsStorage.saveStrategyNewBean(this.val$strategy);
/*     */       }
/*     */ 
/*     */       public void changedUpdate(DocumentEvent e)
/*     */       {
/* 267 */         this.val$strategy.setComments(CommentsTableCellEditor.this.textfield.getText());
/* 268 */         this.val$settingsStorage.saveStrategyNewBean(this.val$strategy);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public Object getCellEditorValue() {
/* 275 */     return this.textfield.getText();
/*     */   }
/*     */ 
/*     */   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
/*     */   {
/* 280 */     this.textfield.setText((String)value);
/* 281 */     return this.textfield;
/*     */   }
/*     */ 
/*     */   public boolean shouldSelectCell(EventObject anEvent)
/*     */   {
/* 286 */     this.textfield.requestFocus();
/* 287 */     return super.shouldSelectCell(anEvent);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.table.CommentsTableCellEditor
 * JD-Core Version:    0.6.0
 */