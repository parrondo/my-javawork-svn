/*     */ package com.dukascopy.charts.dialogs.drawings;
/*     */ 
/*     */ import com.dukascopy.charts.dialogs.indicators.ColorJComboBox;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.awt.event.KeyAdapter;
/*     */ import java.awt.event.KeyEvent;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRootPane;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTextPane;
/*     */ import javax.swing.JToggleButton;
/*     */ import javax.swing.event.CaretEvent;
/*     */ import javax.swing.event.CaretListener;
/*     */ import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
/*     */ import javax.swing.text.AttributeSet;
/*     */ import javax.swing.text.DefaultStyledDocument;
/*     */ import javax.swing.text.Element;
/*     */ import javax.swing.text.SimpleAttributeSet;
/*     */ import javax.swing.text.StyleConstants;
/*     */ import javax.swing.text.StyleContext;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class TextLabelEditDialog extends JDialog
/*     */   implements ActionListener
/*     */ {
/*  54 */   private static final Logger LOGGER = LoggerFactory.getLogger(TextLabelEditDialog.class);
/*     */   private JTextPane textField;
/*     */   private DefaultStyledDocument document;
/*     */   private JButton okButton;
/*     */   private JButton cancelButton;
/*     */   JToggleButton boldButton;
/*     */   JToggleButton italicButton;
/*     */   JToggleButton underlineButton;
/*     */   ColorJComboBox fontColorCombo;
/*     */   JComboBox fontCombo;
/*     */   JComboBox fontSizeCombo;
/*     */   private static final byte BOLD = 0;
/*     */   private static final byte ITALIC = 1;
/*     */   private static final byte UNDERLINE = 2;
/*  75 */   StyledStringTransformer value = null;
/*     */ 
/*     */   public TextLabelEditDialog(Frame parent, StyledStringTransformer styledString)
/*     */   {
/*  92 */     super(parent, " Define Text", true);
/*  93 */     this.value = styledString;
/*     */ 
/*  95 */     setLocationRelativeTo(parent);
/*     */ 
/*  97 */     JPanel root = new HorizontallyBorderedPanel(new BorderLayout());
/*  98 */     JPanel toolBarPanel = new JPanel(new GridLayout(2, 1));
/*     */ 
/* 100 */     JPanel toolBar1Row = new JPanel(new GridBagLayout());
/* 101 */     JPanel toolBar2Row = new JPanel(new GridBagLayout());
/* 102 */     toolBarPanel.add(toolBar1Row);
/* 103 */     toolBarPanel.add(toolBar2Row);
/* 104 */     JPanel buttonPanel = new JPanel(new GridBagLayout());
/*     */ 
/* 107 */     this.boldButton = new JToggleButton(new ResizableIcon("dialog_drawing_text_edit_bold.png"));
/* 108 */     this.italicButton = new JToggleButton(new ResizableIcon("dialog_drawing_text_edit_italic.png"));
/* 109 */     this.underlineButton = new JToggleButton(new ResizableIcon("dialog_drawing_text_edit_underline.png"));
/*     */ 
/* 111 */     this.boldButton.setMargin(new Insets(0, 0, 0, 0));
/* 112 */     this.italicButton.setMargin(new Insets(0, 0, 0, 0));
/* 113 */     this.underlineButton.setMargin(new Insets(0, 0, 0, 0));
/*     */ 
/* 115 */     this.boldButton.setActionCommand("Bold");
/* 116 */     this.italicButton.setActionCommand("Italic");
/* 117 */     this.underlineButton.setActionCommand("Under");
/*     */ 
/* 119 */     this.boldButton.addActionListener(this);
/* 120 */     this.italicButton.addActionListener(this);
/* 121 */     this.underlineButton.addActionListener(this);
/*     */ 
/* 123 */     this.boldButton.setFocusable(false);
/* 124 */     this.italicButton.setFocusable(false);
/* 125 */     this.underlineButton.setFocusable(false);
/*     */ 
/* 127 */     GridBagConstraints styleBarConstraints = new GridBagConstraints();
/* 128 */     styleBarConstraints.insets = new Insets(4, 2, 2, 2);
/* 129 */     styleBarConstraints.anchor = 17;
/* 130 */     styleBarConstraints.ipadx = 2;
/* 131 */     styleBarConstraints.ipady = 2;
/*     */ 
/* 133 */     styleBarConstraints.gridy = 0;
/* 134 */     styleBarConstraints.gridx = 0;
/*     */ 
/* 136 */     String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
/*     */ 
/* 138 */     this.fontCombo = new JComboBox(fontNames);
/* 139 */     this.fontCombo.setSelectedItem(styledString.getFontFamilyName());
/* 140 */     this.fontCombo.addItemListener(new ItemListener() {
/*     */       public void itemStateChanged(ItemEvent e) {
/* 142 */         if (e.getStateChange() == 1) {
/* 143 */           String newFontFamilyName = (String)TextLabelEditDialog.this.fontCombo.getSelectedItem();
/* 144 */           TextLabelEditDialog.this.changeFontFamily(newFontFamilyName);
/*     */         }
/*     */       }
/*     */     });
/* 148 */     toolBar1Row.add(this.fontCombo, styleBarConstraints);
/*     */ 
/* 150 */     styleBarConstraints.gridx = 1;
/* 151 */     this.fontSizeCombo = new JComboBox(new Integer[] { Integer.valueOf(8), Integer.valueOf(9), Integer.valueOf(10), Integer.valueOf(11), Integer.valueOf(12), Integer.valueOf(14), Integer.valueOf(16), Integer.valueOf(18), Integer.valueOf(20), Integer.valueOf(24) });
/* 152 */     this.fontSizeCombo.setSelectedItem(Integer.valueOf(styledString.getFontSize()));
/* 153 */     this.fontSizeCombo.addItemListener(new ItemListener() {
/*     */       public void itemStateChanged(ItemEvent e) {
/* 155 */         if (e.getStateChange() == 1)
/* 156 */           TextLabelEditDialog.this.changeFontSize(((Integer)TextLabelEditDialog.this.fontSizeCombo.getSelectedItem()).intValue());
/*     */       }
/*     */     });
/* 160 */     toolBar1Row.add(this.fontSizeCombo, styleBarConstraints);
/*     */ 
/* 162 */     styleBarConstraints.insets = new Insets(2, 2, 4, 2);
/* 163 */     styleBarConstraints.gridy = 1;
/* 164 */     styleBarConstraints.gridx = 0;
/* 165 */     this.fontColorCombo = new ColorJComboBox();
/* 166 */     this.fontColorCombo.setSelectedColor(styledString.getFontColor());
/* 167 */     this.fontColorCombo.addItemListener(new ItemListener() {
/*     */       public void itemStateChanged(ItemEvent e) {
/* 169 */         if (e.getStateChange() == 1) {
/* 170 */           Color newColor = TextLabelEditDialog.this.fontColorCombo.getSelectedColor();
/* 171 */           TextLabelEditDialog.this.fontColorChange(newColor);
/*     */         }
/*     */       }
/*     */     });
/* 175 */     toolBar2Row.add(this.fontColorCombo, styleBarConstraints);
/*     */ 
/* 177 */     styleBarConstraints.gridx = 1;
/* 178 */     toolBar2Row.add(this.boldButton, styleBarConstraints);
/* 179 */     styleBarConstraints.gridx = 2;
/* 180 */     toolBar2Row.add(this.italicButton, styleBarConstraints);
/* 181 */     styleBarConstraints.gridx = 3;
/* 182 */     toolBar2Row.add(this.underlineButton, styleBarConstraints);
/*     */ 
/* 185 */     StyleContext context = new StyleContext();
/* 186 */     this.document = new DefaultStyledDocument(context) {
/*     */       protected void insertUpdate(AbstractDocument.DefaultDocumentEvent chng, AttributeSet attr) {
/* 188 */         SimpleAttributeSet newAs = new SimpleAttributeSet(attr);
/* 189 */         StyleConstants.setBold(newAs, TextLabelEditDialog.this.boldButton.isSelected());
/* 190 */         StyleConstants.setItalic(newAs, TextLabelEditDialog.this.italicButton.isSelected());
/* 191 */         StyleConstants.setUnderline(newAs, TextLabelEditDialog.this.underlineButton.isSelected());
/* 192 */         StyleConstants.setForeground(newAs, TextLabelEditDialog.this.fontColorCombo.getSelectedColor());
/* 193 */         StyleConstants.setFontFamily(newAs, String.valueOf(TextLabelEditDialog.this.fontCombo.getSelectedItem()));
/* 194 */         StyleConstants.setFontSize(newAs, ((Integer)TextLabelEditDialog.this.fontSizeCombo.getSelectedItem()).intValue());
/* 195 */         super.insertUpdate(chng, newAs);
/*     */       }
/*     */     };
/* 198 */     this.document.setDocumentFilter(new TabFilter());
/*     */ 
/* 211 */     styledString.transformToStyledDocument(this.document);
/* 212 */     this.textField = new NonWrappingTextPane(this.document);
/*     */ 
/* 214 */     this.textField.addCaretListener(new CaretListener()
/*     */     {
/*     */       public void caretUpdate(CaretEvent event) {
/* 217 */         JTextPane parent = (JTextPane)event.getSource();
/* 218 */         String content = parent.getText();
/*     */ 
/* 220 */         if ((content == null) || (content.length() == 0)) {
/* 221 */           return;
/*     */         }
/*     */ 
/* 224 */         int dot = event.getDot();
/* 225 */         if (dot != event.getMark())
/*     */         {
/* 227 */           boolean selectedTextBold = TextLabelEditDialog.this.selectedTextStyle(0);
/* 228 */           boolean selectedTextItalic = TextLabelEditDialog.this.selectedTextStyle(1);
/* 229 */           boolean selectedTextUnder = TextLabelEditDialog.this.selectedTextStyle(2);
/*     */ 
/* 231 */           TextLabelEditDialog.this.boldButton.setSelected(selectedTextBold);
/* 232 */           TextLabelEditDialog.this.italicButton.setSelected(selectedTextItalic);
/* 233 */           TextLabelEditDialog.this.underlineButton.setSelected(selectedTextUnder);
/*     */         }
/*     */         else {
/* 236 */           int styleCharacterPos = dot == 0 ? 0 : dot - 1;
/* 237 */           AttributeSet as = TextLabelEditDialog.this.document.getCharacterElement(styleCharacterPos).getAttributes();
/* 238 */           TextLabelEditDialog.this.boldButton.setSelected(StyleConstants.isBold(as));
/* 239 */           TextLabelEditDialog.this.italicButton.setSelected(StyleConstants.isItalic(as));
/* 240 */           TextLabelEditDialog.this.underlineButton.setSelected(StyleConstants.isUnderline(as));
/*     */         }
/*     */       }
/*     */     });
/* 246 */     this.textField.setEditable(true);
/* 247 */     this.textField.addKeyListener(new KeyAdapter() {
/*     */       public void keyPressed(KeyEvent e) {
/* 249 */         if (9 == e.getKeyCode())
/* 250 */           TextLabelEditDialog.this.cancelButton.grabFocus();
/*     */       }
/*     */     });
/* 254 */     JScrollPane textScrollPane = new JScrollPane(this.textField);
/*     */ 
/* 256 */     this.okButton = new JButton("OK");
/* 257 */     this.okButton.setActionCommand("OK");
/* 258 */     this.okButton.addActionListener(this);
/* 259 */     this.okButton.setPreferredSize(new Dimension(70, 20));
/*     */ 
/* 261 */     getRootPane().setDefaultButton(this.okButton);
/* 262 */     this.okButton.setFocusCycleRoot(true);
/*     */ 
/* 264 */     this.cancelButton = new JButton("Cancel");
/* 265 */     this.cancelButton.setActionCommand("Cancel");
/* 266 */     this.cancelButton.addActionListener(this);
/* 267 */     this.cancelButton.setPreferredSize(new Dimension(70, 20));
/*     */ 
/* 269 */     GridBagConstraints buttonsC = new GridBagConstraints();
/* 270 */     buttonsC.insets = new Insets(4, 2, 4, 2);
/* 271 */     buttonsC.ipadx = 2;
/* 272 */     buttonsC.ipady = 2;
/* 273 */     buttonsC.gridx = 0;
/* 274 */     buttonsC.gridy = 0;
/*     */ 
/* 276 */     buttonPanel.add(this.okButton, buttonsC);
/* 277 */     buttonsC.gridx = 1;
/* 278 */     buttonPanel.add(this.cancelButton, buttonsC);
/*     */ 
/* 280 */     root.add(toolBarPanel, "North");
/* 281 */     root.add(textScrollPane, "Center");
/* 282 */     root.add(buttonPanel, "South");
/*     */ 
/* 284 */     setModal(true);
/* 285 */     setContentPane(root);
/* 286 */     pack();
/*     */ 
/* 288 */     setSize(300, 250);
/* 289 */     setResizable(false);
/* 290 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   public StyledStringTransformer getValue() {
/* 294 */     if ((this.value != null) && (this.value.getSource() != null) && (this.value.getSource().replace(" ", "").replace("\n", "").replace("\r", "").length() != 0))
/*     */     {
/* 296 */       return this.value;
/*     */     }
/* 298 */     return null;
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/* 303 */     if ("OK".equals(e.getActionCommand())) {
/* 304 */       dispose();
/* 305 */       this.value = new StyledStringTransformer(this.textField.getText(), this.document);
/* 306 */     } else if ("Cancel".equals(e.getActionCommand())) {
/* 307 */       dispose();
/*     */     }
/* 309 */     else if ("Bold".equals(e.getActionCommand())) {
/* 310 */       styleButtonPressed(this.boldButton.isSelected(), 0);
/* 311 */     } else if ("Italic".equals(e.getActionCommand())) {
/* 312 */       styleButtonPressed(this.italicButton.isSelected(), 1);
/* 313 */     } else if ("Under".equals(e.getActionCommand())) {
/* 314 */       styleButtonPressed(this.underlineButton.isSelected(), 2);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fontColorChange(Color newColor) {
/* 319 */     String text = this.textField.getText();
/* 320 */     if ((text == null) || (text.length() == 0)) {
/* 321 */       return;
/*     */     }
/*     */ 
/* 324 */     for (int i = 0; i < this.document.getLength(); i++) {
/* 325 */       AttributeSet as = this.document.getCharacterElement(i).getAttributes();
/* 326 */       SimpleAttributeSet mutableAs = new SimpleAttributeSet(as);
/* 327 */       StyleConstants.setForeground(mutableAs, newColor);
/* 328 */       this.document.setCharacterAttributes(i, 1, mutableAs, true);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void changeFontSize(int newFontSize) {
/* 333 */     String text = this.textField.getText();
/* 334 */     if ((text == null) || (text.length() == 0)) {
/* 335 */       return;
/*     */     }
/*     */ 
/* 338 */     for (int i = 0; i < this.document.getLength(); i++) {
/* 339 */       AttributeSet as = this.document.getCharacterElement(i).getAttributes();
/* 340 */       SimpleAttributeSet mutableAs = new SimpleAttributeSet(as);
/* 341 */       StyleConstants.setFontSize(mutableAs, newFontSize);
/* 342 */       this.document.setCharacterAttributes(i, 1, mutableAs, true);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void changeFontFamily(String newFontFamily) {
/* 347 */     String text = this.textField.getText();
/* 348 */     if ((text == null) || (text.length() == 0)) {
/* 349 */       return;
/*     */     }
/*     */ 
/* 352 */     for (int i = 0; i < this.document.getLength(); i++) {
/* 353 */       AttributeSet as = this.document.getCharacterElement(i).getAttributes();
/* 354 */       SimpleAttributeSet mutableAs = new SimpleAttributeSet(as);
/* 355 */       StyleConstants.setFontFamily(mutableAs, newFontFamily);
/* 356 */       this.document.setCharacterAttributes(i, 1, mutableAs, true);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void styleButtonPressed(boolean isSelected, byte type) {
/* 361 */     String selectedText = this.textField.getSelectedText();
/* 362 */     if ((selectedText == null) || (selectedText.length() == 0)) {
/* 363 */       return;
/*     */     }
/*     */ 
/* 366 */     int offset = this.textField.getSelectionStart();
/* 367 */     int selectionEnd = offset + selectedText.length();
/*     */     try
/*     */     {
/* 370 */       for (int i = offset; i < selectionEnd; i++) {
/* 371 */         AttributeSet as = this.document.getCharacterElement(i).getAttributes();
/* 372 */         SimpleAttributeSet attributes = new SimpleAttributeSet(as);
/* 373 */         switch (type) {
/*     */         case 0:
/* 375 */           StyleConstants.setBold(attributes, isSelected);
/* 376 */           break;
/*     */         case 1:
/* 378 */           StyleConstants.setItalic(attributes, isSelected);
/* 379 */           break;
/*     */         case 2:
/* 381 */           StyleConstants.setUnderline(attributes, isSelected);
/*     */         }
/*     */ 
/* 384 */         this.document.setCharacterAttributes(i, 1, attributes, true);
/*     */       }
/*     */     } catch (Exception ex) {
/* 387 */       LOGGER.error(ex.getMessage(), ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean selectedTextStyle(byte type)
/*     */   {
/* 393 */     boolean result = true;
/* 394 */     int selectionStart = this.textField.getSelectionStart();
/* 395 */     int selectionEnd = this.textField.getSelectionEnd();
/* 396 */     for (int i = selectionStart; i < selectionEnd; i++) {
/* 397 */       AttributeSet attributeSet = this.document.getCharacterElement(i).getAttributes();
/* 398 */       switch (type) {
/*     */       case 0:
/* 400 */         if (StyleConstants.isBold(attributeSet)) continue;
/* 401 */         return false;
/*     */       case 1:
/* 405 */         if (StyleConstants.isItalic(attributeSet)) continue;
/* 406 */         return false;
/*     */       case 2:
/* 410 */         if (StyleConstants.isUnderline(attributeSet)) continue;
/* 411 */         return false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 416 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.drawings.TextLabelEditDialog
 * JD-Core Version:    0.6.0
 */