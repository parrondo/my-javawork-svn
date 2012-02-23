/*     */ package com.dukascopy.dds2.greed.gui.component.export.historicaldata;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.api.TickBarSize;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.utils.ChartsLocalizator;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.CompositePeriod;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.CompositePeriod.Type;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.ExportFormat;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.ExportOfferSide;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.ExportProcessControl;
/*     */ import com.dukascopy.dds2.greed.export.historicaldata.PeriodType;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.components.JResizableLabel;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Font;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ import java.text.ParseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.EventObject;
/*     */ import java.util.List;
/*     */ import javax.swing.AbstractCellEditor;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.DefaultListCellRenderer;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JFormattedTextField;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.JSpinner.NumberEditor;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.ListCellRenderer;
/*     */ import javax.swing.border.EmptyBorder;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.plaf.basic.BasicComboBoxUI;
/*     */ import javax.swing.table.TableCellEditor;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.text.DefaultFormatter;
/*     */ 
/*     */ public class MultipurposeTableCellEditor extends AbstractCellEditor
/*     */   implements TableCellEditor, TableCellRenderer, Localizable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  62 */   private static final JLabel STUB = new JLabel("");
/*     */ 
/*  64 */   private String selectDataTypeHint = "";
/*  65 */   private String selectValueHint = "";
/*  66 */   private String selectOfferSideHint = "";
/*     */   private ExportProcessControl exportProcessControl;
/*  70 */   int columnIndex = 0;
/*  71 */   private Object currentValue = null;
/*  72 */   InstrumentSelectionTableModel tableModel = null;
/*  73 */   private final List<Component> editors = new ArrayList();
/*     */ 
/*     */   public MultipurposeTableCellEditor(InstrumentSelectionTableModel tableModel, int columnIndex, ExportProcessControl exportProcessControl) {
/*  76 */     this.tableModel = tableModel;
/*  77 */     this.columnIndex = columnIndex;
/*  78 */     this.exportProcessControl = exportProcessControl;
/*     */ 
/*  80 */     LocalizationManager.addLocalizable(this);
/*     */ 
/*  82 */     build();
/*     */   }
/*     */ 
/*     */   public Object getCellEditorValue()
/*     */   {
/*  87 */     return this.currentValue;
/*     */   }
/*     */ 
/*     */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*     */   {
/*  92 */     return getEditor(row);
/*     */   }
/*     */ 
/*     */   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
/*     */   {
/*  99 */     Component editor = getEditor(row);
/* 100 */     this.currentValue = value;
/* 101 */     return editor;
/*     */   }
/*     */ 
/*     */   public boolean isCellEditable(EventObject evt)
/*     */   {
/* 106 */     return true;
/*     */   }
/*     */ 
/*     */   public void build()
/*     */   {
/* 120 */     List tempEditors = new ArrayList();
/* 121 */     tempEditors.addAll(this.editors);
/*     */ 
/* 123 */     this.editors.clear();
/* 124 */     for (int row = 0; row < this.tableModel.getRowCount(); row++)
/* 125 */       if (this.tableModel.isCellEditable(row, this.columnIndex)) {
/* 126 */         Component newEditor = createEditor(row);
/* 127 */         Component oldEditor = (Component)tempEditors.get(row);
/* 128 */         if (((newEditor instanceof CellPanel)) && ((oldEditor instanceof CellPanel))) {
/* 129 */           CellPanel newCellPanel = (CellPanel)newEditor;
/* 130 */           CellPanel oldCellPanel = (CellPanel)oldEditor;
/* 131 */           newCellPanel.setError(oldCellPanel.isError());
/*     */         }
/*     */ 
/* 134 */         this.editors.add(newEditor);
/*     */       } else {
/* 136 */         this.editors.add(null);
/*     */       }
/*     */   }
/*     */ 
/*     */   private Component getEditor(int row)
/*     */   {
/* 142 */     Component editor = (Component)this.editors.get(row);
/* 143 */     return editor == null ? STUB : editor;
/*     */   }
/*     */ 
/*     */   private JLabel getComboLabel(String text, Color fg, Color bg) {
/* 147 */     JLabel label = new JResizableLabel();
/* 148 */     label.setText(text);
/* 149 */     label.setOpaque(true);
/* 150 */     label.setForeground(fg);
/* 151 */     label.setBackground(bg);
/*     */ 
/* 153 */     EmptyBorder border = new EmptyBorder(new Insets(0, 3, 0, 0));
/* 154 */     label.setBorder(border);
/*     */ 
/* 156 */     return label;
/*     */   }
/*     */ 
/*     */   private Component createEditor(int rowIndex) {
/* 160 */     Object value = this.tableModel.getValueAt(rowIndex, this.columnIndex);
/*     */ 
/* 162 */     if (this.columnIndex == 3) {
/* 163 */       PeriodType periodType = this.tableModel.getPeriodType(rowIndex);
/* 164 */       if (periodType == PeriodType.PF) {
/* 165 */         return getPFEditor(rowIndex, value);
/*     */       }
/*     */     }
/*     */ 
/* 169 */     return getComboBoxEditor(rowIndex, value);
/*     */   }
/*     */ 
/*     */   Component getPFEditor(int rowIndex, Object value) {
/* 173 */     JPanel pfEditorPanel = new JPanel();
/*     */ 
/* 175 */     JSpinner.NumberEditor jsEditor = null;
/* 176 */     DefaultFormatter formatter = null;
/*     */ 
/* 178 */     SpinnerHintNumberModel boxSpinnerModel = new SpinnerHintNumberModel(1, 1, PriceRange.MAXIMAL_PIP_COUNT, 1, "label.caption.box.size.in.pips");
/* 179 */     JSpinnerHint boxSpinner = new JSpinnerHint(boxSpinnerModel, "label.caption.box.size.in.pips");
/* 180 */     boxSpinner.setBorder(BorderFactory.createEmptyBorder());
/* 181 */     jsEditor = (JSpinner.NumberEditor)boxSpinner.getEditor();
/* 182 */     formatter = (DefaultFormatter)jsEditor.getTextField().getFormatter();
/* 183 */     formatter.setAllowsInvalid(false);
/*     */ 
/* 185 */     SpinnerHintNumberModel reveralAmountSpinnerModel = new SpinnerHintNumberModel(1, 1, ReversalAmount.MAXIMAL_REVERSAL_AMOUNT, 1, "label.caption.reversal.amount");
/* 186 */     JSpinnerHint reveralAmountSpinner = new JSpinnerHint(reveralAmountSpinnerModel, "label.caption.reversal.amount");
/* 187 */     reveralAmountSpinner.setBorder(BorderFactory.createEmptyBorder());
/* 188 */     jsEditor = (JSpinner.NumberEditor)reveralAmountSpinner.getEditor();
/* 189 */     formatter = (DefaultFormatter)jsEditor.getTextField().getFormatter();
/* 190 */     formatter.setAllowsInvalid(false);
/*     */ 
/* 192 */     pfEditorPanel.setBackground(Color.WHITE);
/* 193 */     pfEditorPanel.setLayout(new GridBagLayout());
/* 194 */     GridBagConstraints gbc = new GridBagConstraints();
/*     */ 
/* 196 */     gbc.anchor = 21;
/* 197 */     gbc.fill = 3;
/* 198 */     GridBagLayoutHelper.add(0, 0, 0.0D, 1.0D, 1, 1, 0, 0, 0, 0, gbc, pfEditorPanel, boxSpinner);
/*     */ 
/* 200 */     gbc.anchor = 21;
/* 201 */     gbc.fill = 3;
/* 202 */     GridBagLayoutHelper.add(1, 0, 0.0D, 1.0D, 1, 1, 0, 0, 0, 0, gbc, pfEditorPanel, reveralAmountSpinner);
/*     */ 
/* 204 */     gbc.anchor = 21;
/* 205 */     gbc.fill = 2;
/* 206 */     GridBagLayoutHelper.add(2, 0, 1.0D, 0.0D, 1, 1, 0, 0, 0, 0, gbc, pfEditorPanel, new JPanel());
/*     */ 
/* 208 */     if (value != null) {
/* 209 */       JForexPeriod jForexPeriod = checkAndGetPointAndFigure(value);
/* 210 */       boxSpinner.setValue(Integer.valueOf(jForexPeriod.getPriceRange().getPipCount()));
/* 211 */       reveralAmountSpinner.setValue(Integer.valueOf(jForexPeriod.getReversalAmount().getAmount()));
/*     */     } else {
/* 213 */       JForexPeriod jForexPeriod = new JForexPeriod(DataType.POINT_AND_FIGURE);
/* 214 */       jForexPeriod.setPriceRange(PriceRange.valueOf(1));
/* 215 */       jForexPeriod.setReversalAmount(ReversalAmount.valueOf(1));
/* 216 */       this.currentValue = jForexPeriod;
/* 217 */       this.tableModel.setValueAt(jForexPeriod, rowIndex, this.columnIndex);
/*     */     }
/*     */ 
/* 221 */     JSpinnerHint.NumberEditorExtended boxEditor = (JSpinnerHint.NumberEditorExtended)boxSpinner.getEditor();
/* 222 */     JSpinnerHint.NumberEditorExtended reveralAmountEditor = (JSpinnerHint.NumberEditorExtended)reveralAmountSpinner.getEditor();
/*     */ 
/* 224 */     ChangeListener boxChangeListener = new ChangeListener(rowIndex)
/*     */     {
/*     */       public void stateChanged(ChangeEvent e) {
/* 227 */         JSpinner spinner = (JSpinner)e.getSource();
/* 228 */         Integer value = (Integer)spinner.getValue();
/* 229 */         MultipurposeTableCellEditor.this.changeBoxValue(value, this.val$rowIndex);
/*     */       }
/*     */     };
/* 232 */     boxSpinner.addChangeListener(boxChangeListener);
/*     */ 
/* 234 */     ChangeListener reveralAmountChangeListener = new ChangeListener(rowIndex)
/*     */     {
/*     */       public void stateChanged(ChangeEvent e) {
/* 237 */         JSpinner spinner = (JSpinner)e.getSource();
/* 238 */         Integer value = (Integer)spinner.getValue();
/* 239 */         MultipurposeTableCellEditor.this.changeReversalAmountValue(value, this.val$rowIndex);
/*     */       }
/*     */     };
/* 242 */     reveralAmountSpinner.addChangeListener(reveralAmountChangeListener);
/*     */ 
/* 244 */     boxEditor.getTextField().addKeyListener(new Object(boxEditor, boxSpinner, boxChangeListener, rowIndex)
/*     */     {
/*     */       public void keyReleased(KeyEvent e) {
/* 247 */         boolean valueChanged = true;
/*     */ 
/* 249 */         int selStart = this.val$boxEditor.getTextField().getSelectionStart();
/* 250 */         int selEnd = this.val$boxEditor.getTextField().getSelectionEnd();
/* 251 */         boolean isSelection = false;
/* 252 */         if (selStart != selEnd) isSelection = true;
/*     */ 
/* 254 */         int caretPosition = this.val$boxEditor.getTextField().getCaretPosition();
/* 255 */         Integer intValue = null;
/*     */         try
/*     */         {
/* 258 */           this.val$boxSpinner.removeChangeListener(this.val$boxChangeListener);
/* 259 */           this.val$boxEditor.getTextField().commitEdit();
/*     */         } catch (ParseException e1) {
/* 261 */           valueChanged = false;
/*     */         }
/* 263 */         this.val$boxSpinner.addChangeListener(this.val$boxChangeListener);
/* 264 */         Object value = this.val$boxEditor.getTextField().getValue();
/*     */         try
/*     */         {
/* 267 */           intValue = Integer.valueOf(((Integer)value).intValue());
/*     */         } catch (ClassCastException ex) {
/* 269 */           valueChanged = false;
/*     */         }
/*     */         catch (NumberFormatException ex) {
/* 272 */           valueChanged = false;
/*     */         }
/*     */ 
/* 275 */         if (valueChanged) {
/* 276 */           MultipurposeTableCellEditor.this.changeBoxValue(intValue, this.val$rowIndex);
/* 277 */           if (!isSelection)
/* 278 */             this.val$boxEditor.getTextField().setCaretPosition(caretPosition);
/*     */         }
/*     */       }
/*     */ 
/*     */       public void keyTyped(KeyEvent e)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void keyPressed(KeyEvent e)
/*     */       {
/*     */       }
/*     */     });
/* 290 */     reveralAmountEditor.getTextField().addKeyListener(new Object(reveralAmountEditor, reveralAmountSpinner, reveralAmountChangeListener, rowIndex)
/*     */     {
/*     */       public void keyReleased(KeyEvent e) {
/* 293 */         boolean valueChanged = true;
/*     */ 
/* 295 */         int selStart = this.val$reveralAmountEditor.getTextField().getSelectionStart();
/* 296 */         int selEnd = this.val$reveralAmountEditor.getTextField().getSelectionEnd();
/* 297 */         boolean isSelection = false;
/* 298 */         if (selStart != selEnd) isSelection = true;
/*     */ 
/* 300 */         int caretPosition = this.val$reveralAmountEditor.getTextField().getCaretPosition();
/* 301 */         Integer intValue = null;
/*     */         try
/*     */         {
/* 304 */           this.val$reveralAmountSpinner.removeChangeListener(this.val$reveralAmountChangeListener);
/* 305 */           this.val$reveralAmountEditor.getTextField().commitEdit();
/*     */         } catch (ParseException e1) {
/* 307 */           valueChanged = false;
/*     */         }
/* 309 */         this.val$reveralAmountSpinner.addChangeListener(this.val$reveralAmountChangeListener);
/* 310 */         Object value = this.val$reveralAmountEditor.getTextField().getValue();
/*     */         try
/*     */         {
/* 313 */           intValue = Integer.valueOf(((Integer)value).intValue());
/*     */         } catch (ClassCastException ex) {
/* 315 */           valueChanged = false;
/*     */         }
/*     */         catch (NumberFormatException ex) {
/* 318 */           valueChanged = false;
/*     */         }
/*     */ 
/* 321 */         if (valueChanged) {
/* 322 */           MultipurposeTableCellEditor.this.changeReversalAmountValue(intValue, this.val$rowIndex);
/* 323 */           if (!isSelection)
/* 324 */             this.val$reveralAmountEditor.getTextField().setCaretPosition(caretPosition);
/*     */         }
/*     */       }
/*     */ 
/*     */       public void keyTyped(KeyEvent e)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void keyPressed(KeyEvent e)
/*     */       {
/*     */       }
/*     */     });
/* 336 */     return pfEditorPanel;
/*     */   }
/*     */ 
/*     */   private void changeBoxValue(Integer value, int rowIndex) {
/* 340 */     JForexPeriod forexPeriod = this.tableModel.getPointAndFigure(rowIndex);
/* 341 */     if (forexPeriod == null) {
/* 342 */       forexPeriod = new JForexPeriod(DataType.POINT_AND_FIGURE);
/*     */     }
/*     */ 
/* 345 */     forexPeriod.setPriceRange(PriceRange.valueOf(value.intValue()));
/* 346 */     if (forexPeriod.getReversalAmount() == null) {
/* 347 */       forexPeriod.setReversalAmount(ReversalAmount.valueOf(1));
/*     */     }
/*     */ 
/* 350 */     this.currentValue = forexPeriod;
/*     */   }
/*     */ 
/*     */   private void changeReversalAmountValue(Integer value, int rowIndex) {
/* 354 */     JForexPeriod forexPeriod = this.tableModel.getPointAndFigure(rowIndex);
/* 355 */     if (forexPeriod == null) {
/* 356 */       forexPeriod = new JForexPeriod(DataType.POINT_AND_FIGURE);
/*     */     }
/*     */ 
/* 359 */     forexPeriod.setReversalAmount(ReversalAmount.valueOf(value.intValue()));
/* 360 */     if (forexPeriod.getPriceRange() == null) {
/* 361 */       forexPeriod.setPriceRange(PriceRange.valueOf(1));
/*     */     }
/*     */ 
/* 364 */     this.currentValue = forexPeriod;
/*     */   }
/*     */ 
/*     */   private JForexPeriod checkAndGetPointAndFigure(Object value) {
/* 368 */     if ((value instanceof JForexPeriod)) {
/* 369 */       JForexPeriod jForexPeriod = (JForexPeriod)value;
/* 370 */       if (jForexPeriod.getDataType() == DataType.POINT_AND_FIGURE) {
/* 371 */         return jForexPeriod;
/*     */       }
/* 373 */       throw new IllegalArgumentException("Incorrect DataType: " + jForexPeriod.getDataType() + ", must be DataType.POINT_AND_FIGURE");
/*     */     }
/*     */ 
/* 376 */     throw new IllegalArgumentException("Incorrect argument type: " + value.getClass().getName() + ", must be JForexPeriod");
/*     */   }
/*     */ 
/*     */   Component getComboBoxEditor(int rowIndex, Object value)
/*     */   {
/* 382 */     JComboBox combo = new JComboBox();
/* 383 */     Font origFont = combo.getFont();
/* 384 */     Color origForegroundColor = combo.getForeground();
/* 385 */     ListCellRenderer origRenderer = combo.getRenderer();
/*     */ 
/* 387 */     combo.setMaximumRowCount(10);
/* 388 */     combo.setRenderer(new DefaultListCellRenderer(origRenderer, combo, origFont, origForegroundColor)
/*     */     {
/*     */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 391 */         if ((value == null) || ((value.toString().length() == 0) && (index == -1)))
/*     */         {
/* 393 */           String hint = "";
/* 394 */           switch (MultipurposeTableCellEditor.this.columnIndex) {
/*     */           case 2:
/* 396 */             hint = MultipurposeTableCellEditor.this.selectDataTypeHint;
/* 397 */             break;
/*     */           case 3:
/* 399 */             hint = MultipurposeTableCellEditor.this.selectValueHint;
/* 400 */             break;
/*     */           case 4:
/* 402 */             hint = MultipurposeTableCellEditor.this.selectOfferSideHint;
/* 403 */             break;
/*     */           case 7:
/* 405 */             break;
/*     */           case 5:
/*     */           case 6:
/*     */           default:
/* 406 */             throw new IllegalArgumentException("Incorrect column index : " + MultipurposeTableCellEditor.this.columnIndex);
/*     */           }
/*     */ 
/* 409 */           return getHintComponent(list, hint, index, isSelected, cellHasFocus);
/*     */         }
/* 411 */         return returnComponent(list, value, index, isSelected, cellHasFocus);
/*     */       }
/*     */ 
/*     */       private Component getHintComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*     */       {
/* 416 */         JLabel label = (JLabel)this.val$origRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/*     */ 
/* 418 */         if (this.val$combo.getFont().getStyle() != 2) {
/* 419 */           this.val$combo.setFont(label.getFont().deriveFont(2));
/* 420 */           this.val$combo.setForeground(Color.GRAY);
/*     */         }
/*     */ 
/* 423 */         return label;
/*     */       }
/*     */ 
/*     */       private Component returnComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 427 */         if (this.val$combo.getFont() != this.val$origFont) {
/* 428 */           this.val$combo.setFont(this.val$origFont);
/* 429 */           this.val$combo.setForeground(this.val$origForegroundColor);
/*     */         }
/*     */ 
/* 432 */         Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/* 433 */         String text = "";
/* 434 */         if (value != null) {
/* 435 */           if ((value instanceof PeriodType)) {
/* 436 */             text = value.toString();
/*     */           }
/* 438 */           else if ((value instanceof CompositePeriod)) {
/* 439 */             CompositePeriod compositePeriod = (CompositePeriod)value;
/*     */ 
/* 441 */             if (compositePeriod.getType() == CompositePeriod.Type.PERIOD) {
/* 442 */               Period period = compositePeriod.getPeriod();
/* 443 */               text = ChartsLocalizator.getLocalized(period);
/* 444 */             } else if (compositePeriod.getType() == CompositePeriod.Type.TICKBARSIZE) {
/* 445 */               TickBarSize tickBarSize = compositePeriod.getTickBarSize();
/* 446 */               text = ChartsLocalizator.getLocalized(tickBarSize);
/*     */             }
/*     */           }
/* 449 */           else if ((value instanceof ReversalAmount)) {
/* 450 */             text = String.valueOf(((ReversalAmount)value).getAmount());
/*     */           }
/* 452 */           else if ((value instanceof TickBarSize)) {
/* 453 */             text = ChartsLocalizator.getLocalized((TickBarSize)value);
/*     */           }
/* 455 */           else if ((value instanceof PriceRange)) {
/* 456 */             text = ChartsLocalizator.getLocalized((PriceRange)value);
/*     */           }
/* 458 */           else if (value != null) {
/* 459 */             text = value.toString();
/*     */           }
/*     */         }
/* 462 */         return MultipurposeTableCellEditor.this.getComboLabel(text, comp.getForeground(), comp.getBackground());
/*     */       }
/*     */     });
/* 466 */     switch (this.columnIndex) {
/*     */     case 2:
/* 468 */       for (PeriodType periodType : this.tableModel.getPeriodTypes(rowIndex)) {
/* 469 */         combo.addItem(periodType);
/*     */       }
/* 471 */       break;
/*     */     case 3:
/* 473 */       PeriodType periodType = this.tableModel.getPeriodType(rowIndex);
/* 474 */       if (periodType == PeriodType.Range) {
/* 475 */         for (PriceRange priceRange : this.tableModel.getPriceRanges())
/* 476 */           combo.addItem(priceRange);
/*     */       }
/*     */       else {
/* 479 */         for (CompositePeriod compositePeriod : this.tableModel.getPeriodsList(rowIndex)) {
/* 480 */           combo.addItem(compositePeriod);
/*     */         }
/*     */       }
/* 483 */       break;
/*     */     case 4:
/* 485 */       for (ExportOfferSide offerSide : ExportOfferSide.values()) {
/* 486 */         combo.addItem(offerSide);
/*     */       }
/* 488 */       break;
/*     */     case 7:
/* 490 */       for (ExportFormat exportFormat : ExportFormat.values()) {
/* 491 */         combo.addItem(exportFormat);
/*     */       }
/* 493 */       break;
/*     */     case 5:
/*     */     case 6:
/*     */     default:
/* 494 */       throw new IllegalArgumentException("Incorrect column index : " + this.columnIndex);
/*     */     }
/*     */ 
/* 497 */     if (value == null)
/* 498 */       combo.setSelectedIndex(-1);
/*     */     else {
/* 500 */       combo.setSelectedItem(value);
/*     */     }
/*     */ 
/* 503 */     combo.addItemListener(new ItemListener(rowIndex)
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e) {
/* 506 */         if (e.getStateChange() == 1) {
/* 507 */           MultipurposeTableCellEditor.access$602(MultipurposeTableCellEditor.this, e.getItem());
/* 508 */           MultipurposeTableCellEditor.this.fireEditingStopped();
/* 509 */           if ((MultipurposeTableCellEditor.this.columnIndex == 2) || (MultipurposeTableCellEditor.this.columnIndex == 3))
/*     */           {
/* 512 */             MultipurposeTableCellEditor.this.tableModel.fireTableRowsUpdated(this.val$rowIndex, this.val$rowIndex);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/* 518 */     combo.setFocusable(false);
/* 519 */     CellPanel cellPanel = InstrumentSelectionTable.createCellPanel(this.columnIndex, rowIndex, this.tableModel, this.exportProcessControl, combo);
/*     */ 
/* 527 */     combo.setBorder(BorderFactory.createEmptyBorder());
/* 528 */     if (!PlatformSpecific.LINUX) {
/* 529 */       combo.setUI(new BasicComboBoxUI());
/*     */     }
/* 531 */     combo.addItemListener(cellPanel);
/*     */ 
/* 533 */     return cellPanel;
/*     */   }
/*     */ 
/*     */   public void localize()
/*     */   {
/* 538 */     this.selectDataTypeHint = LocalizationManager.getText("hdm.select.data.type");
/* 539 */     this.selectValueHint = LocalizationManager.getText("hdm.select.value");
/* 540 */     this.selectOfferSideHint = LocalizationManager.getText("hdm.select.offer.side");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.export.historicaldata.MultipurposeTableCellEditor
 * JD-Core Version:    0.6.0
 */