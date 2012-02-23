/*     */ package com.dukascopy.dds2.greed.agent.strategy.ide.impl;
/*     */ 
/*     */ import java.awt.Container;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Point;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.FocusAdapter;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.KeyAdapter;
/*     */ import java.awt.event.KeyEvent;
/*     */ import javax.swing.AbstractAction;
/*     */ import javax.swing.Action;
/*     */ import javax.swing.ActionMap;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.InputMap;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRadioButton;
/*     */ import javax.swing.JRootPane;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.JViewport;
/*     */ import javax.swing.KeyStroke;
/*     */ import javax.swing.border.Border;
/*     */ import javax.swing.border.EmptyBorder;
/*     */ import javax.swing.border.TitledBorder;
/*     */ import javax.swing.text.BadLocationException;
/*     */ import javax.swing.text.Document;
/*     */ import javax.swing.text.JTextComponent;
/*     */ import javax.swing.text.Position;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class FindReplaceDialog extends JDialog
/*     */   implements ActionListener
/*     */ {
/*  17 */   private static final Logger LOGGER = LoggerFactory.getLogger(FindReplaceDialog.class);
/*     */   private static final int TEXT_FIELD_SIZE = 20;
/*     */   private static final int COMPONENT_GAP = 10;
/*  21 */   private static final Border EMPTY_BORDER = new EmptyBorder(5, 5, 5, 5);
/*     */   private static FindReplaceDialog sharedFindReplace;
/*     */   private JLabel findLabel;
/*     */   private JLabel replaceLabel;
/*     */   private JTextField findData;
/*     */   private JTextField replaceData;
/*     */   private JCheckBox matchCase;
/*     */   private JCheckBox matchWord;
/*     */   private JRadioButton searchUp;
/*     */   private JRadioButton searchDown;
/*     */   private JButton findNextButton;
/*     */   private JButton closeButton;
/*     */   private JButton replaceButton;
/*     */   private JButton replaceAllButton;
/*     */   private JPanel findPanel;
/*     */   private JPanel replacePanel;
/*     */   private JPanel optionPanel;
/*     */   private JPanel commandPanel;
/*     */   private JTextComponent textComponent;
/*     */   private Position searchStartPosition;
/*     */   private boolean searchWrap;
/*     */ 
/*     */   public FindReplaceDialog(Frame owner)
/*     */   {
/*  54 */     super(owner);
/*  55 */     setDefaultCloseOperation(1);
/*  56 */     setResizable(false);
/*  57 */     setAlwaysOnTop(true);
/*     */ 
/*  59 */     createFindPanel();
/*  60 */     createReplacePanel();
/*  61 */     createOptionsPanel();
/*  62 */     createCommandPanel();
/*     */ 
/*  64 */     layoutAllThePanels();
/*     */ 
/*  67 */     getRootPane().setDefaultButton(this.findNextButton);
/*     */ 
/*  69 */     FocusAdapter resetDefaultButton = new FocusAdapter() {
/*     */       public void focusLost(FocusEvent e) {
/*  71 */         FindReplaceDialog.this.getRootPane().setDefaultButton(FindReplaceDialog.this.findNextButton);
/*     */       }
/*     */     };
/*  76 */     this.findData.addKeyListener(new KeyAdapter()
/*     */     {
/*     */       public void keyReleased(KeyEvent e) {
/*  79 */         if (e.getKeyCode() != 10) {
/*  80 */           boolean state = FindReplaceDialog.this.findData.getDocument().getLength() > 0;
/*     */ 
/*  82 */           FindReplaceDialog.this.findNextButton.setEnabled(state);
/*  83 */           FindReplaceDialog.this.replaceButton.setEnabled(state);
/*  84 */           FindReplaceDialog.this.replaceAllButton.setEnabled(state);
/*     */ 
/*  86 */           FindReplaceDialog.this.resetSearchVariables();
/*     */         }
/*     */       }
/*     */     });
/*  92 */     KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(27, 0, false);
/*  93 */     Action escapeAction = new AbstractAction() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  95 */         FindReplaceDialog.this.processClose();
/*     */       }
/*     */     };
/* 100 */     getRootPane().getInputMap(2).put(escapeKeyStroke, "ESCAPE");
/* 101 */     getRootPane().getActionMap().put("ESCAPE", escapeAction);
/*     */   }
/*     */ 
/*     */   void layoutAllThePanels()
/*     */   {
/* 106 */     JPanel panel = new JPanel();
/* 107 */     panel.setLayout(new BoxLayout(panel, 1));
/* 108 */     panel.setBorder(EMPTY_BORDER);
/* 109 */     panel.add(this.findPanel);
/* 110 */     panel.add(this.replacePanel);
/* 111 */     panel.add(this.optionPanel);
/* 112 */     panel.add(this.commandPanel);
/* 113 */     setContentPane(panel);
/*     */   }
/*     */ 
/*     */   void createCommandPanel() {
/* 117 */     this.commandPanel = new JPanel();
/* 118 */     this.findNextButton = createButton(this.commandPanel, "Find Next", 'F');
/* 119 */     this.replaceButton = createButton(this.commandPanel, "Replace", 'R');
/* 120 */     this.replaceAllButton = createButton(this.commandPanel, "Replace All", 'a');
/* 121 */     this.closeButton = createButton(this.commandPanel, "Close", ' ');
/* 122 */     this.closeButton.setEnabled(true);
/*     */   }
/*     */ 
/*     */   void createOptionsPanel() {
/* 126 */     JPanel matchPanel = new JPanel();
/* 127 */     matchPanel.setLayout(new GridLayout(2, 1));
/* 128 */     this.matchCase = new JCheckBox("Match case");
/* 129 */     this.matchCase.setMnemonic('C');
/* 130 */     this.matchWord = new JCheckBox("Match word");
/* 131 */     this.matchWord.setMnemonic('W');
/* 132 */     matchPanel.add(this.matchCase);
/* 133 */     matchPanel.add(this.matchWord);
/*     */ 
/* 135 */     JPanel searchPanel = new JPanel();
/* 136 */     searchPanel.setLayout(new GridLayout(2, 1));
/* 137 */     this.searchDown = new JRadioButton("Search Down");
/* 138 */     this.searchDown.setMnemonic('D');
/* 139 */     this.searchDown.setSelected(true);
/* 140 */     this.searchUp = new JRadioButton("Search Up");
/* 141 */     this.searchUp.setMnemonic('U');
/* 142 */     searchPanel.add(this.searchDown);
/* 143 */     searchPanel.add(this.searchUp);
/*     */ 
/* 145 */     ButtonGroup searchGroup = new ButtonGroup();
/* 146 */     searchGroup.add(this.searchDown);
/* 147 */     searchGroup.add(this.searchUp);
/*     */ 
/* 149 */     this.optionPanel = new JPanel();
/* 150 */     this.optionPanel.setLayout(new GridLayout(1, 2));
/* 151 */     this.optionPanel.setBorder(new TitledBorder("Options"));
/* 152 */     this.optionPanel.add(matchPanel);
/* 153 */     this.optionPanel.add(searchPanel);
/*     */   }
/*     */ 
/*     */   void createReplacePanel() {
/* 157 */     this.replaceData = new JTextField(20);
/* 158 */     this.replaceData.setMaximumSize(this.findData.getPreferredSize());
/*     */ 
/* 160 */     this.replaceLabel = new JLabel("Replace with:");
/* 161 */     this.replaceLabel.setDisplayedMnemonic('P');
/* 162 */     this.replaceLabel.setLabelFor(this.replaceData);
/*     */ 
/* 164 */     this.replacePanel = new JPanel();
/* 165 */     this.replacePanel.setBorder(EMPTY_BORDER);
/* 166 */     this.replacePanel.setLayout(new BoxLayout(this.replacePanel, 0));
/* 167 */     this.replacePanel.add(this.replaceLabel);
/* 168 */     this.replacePanel.add(Box.createHorizontalGlue());
/* 169 */     this.replacePanel.add(Box.createHorizontalStrut(10));
/* 170 */     this.replacePanel.add(this.replaceData);
/*     */   }
/*     */ 
/*     */   void createFindPanel() {
/* 174 */     this.findData = new JTextField(20);
/* 175 */     this.findData.setMaximumSize(this.findData.getPreferredSize());
/*     */ 
/* 177 */     this.findLabel = new JLabel("Find what:");
/* 178 */     this.findLabel.setDisplayedMnemonic('N');
/* 179 */     this.findLabel.setLabelFor(this.findData);
/*     */ 
/* 181 */     this.findPanel = new JPanel();
/* 182 */     this.findPanel.setBorder(EMPTY_BORDER);
/* 183 */     this.findPanel.setLayout(new BoxLayout(this.findPanel, 0));
/* 184 */     this.findPanel.add(this.findLabel);
/* 185 */     this.findPanel.add(Box.createHorizontalGlue());
/* 186 */     this.findPanel.add(Box.createHorizontalStrut(10));
/* 187 */     this.findPanel.add(this.findData);
/*     */   }
/*     */ 
/*     */   public static FindReplaceDialog getSharedInstance(Frame owner) {
/* 191 */     if (sharedFindReplace == null) {
/* 192 */       sharedFindReplace = new FindReplaceDialog(owner);
/* 193 */       sharedFindReplace.setLocationRelativeTo(owner);
/*     */     }
/* 195 */     return sharedFindReplace;
/*     */   }
/*     */ 
/*     */   public void showFind(JTextComponent textComponent) {
/* 199 */     setTitle("Find");
/* 200 */     setTextComponent(textComponent);
/* 201 */     showReplaceComponents(false);
/* 202 */     pack();
/* 203 */     setLocationRelativeTo(getOwner());
/* 204 */     if (textComponent.getSelectedText() != null) {
/* 205 */       this.findData.setText(textComponent.getSelectedText());
/*     */     }
/* 207 */     this.findData.selectAll();
/* 208 */     setVisible(true);
/* 209 */     this.findData.requestFocus();
/* 210 */     resetSearchVariables();
/*     */   }
/*     */ 
/*     */   public void doFindNext(JTextComponent textComponent) {
/* 214 */     if (this.findData.getText().length() == 0) {
/* 215 */       showFind(textComponent);
/*     */     } else {
/* 217 */       setTextComponent(textComponent);
/* 218 */       processFindNext();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void showReplace(JTextComponent textComponent) {
/* 223 */     setTitle("Find and Replace");
/* 224 */     setTextComponent(textComponent);
/* 225 */     showReplaceComponents(true);
/* 226 */     pack();
/* 227 */     setLocationRelativeTo(getOwner());
/* 228 */     if (textComponent.getSelectedText() != null) {
/* 229 */       this.findData.setText(textComponent.getSelectedText());
/*     */     }
/* 231 */     this.findData.selectAll();
/* 232 */     setVisible(true);
/* 233 */     this.findData.requestFocus();
/* 234 */     resetSearchVariables();
/*     */   }
/*     */ 
/*     */   private void showReplaceComponents(boolean value) {
/* 238 */     this.replacePanel.setVisible(value);
/* 239 */     this.replaceButton.setVisible(value);
/* 240 */     this.replaceAllButton.setVisible(value);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/* 244 */     Object o = e.getSource();
/*     */ 
/* 246 */     if (o == this.findNextButton) {
/* 247 */       processFindNext();
/*     */     }
/* 249 */     if (o == this.replaceButton) {
/* 250 */       processReplace();
/*     */     }
/* 252 */     if (o == this.replaceAllButton) {
/* 253 */       processReplaceAll();
/*     */     }
/* 255 */     if (o == this.closeButton)
/* 256 */       processClose();
/*     */   }
/*     */ 
/*     */   private JButton createButton(JPanel panel, String label, char mnemonic)
/*     */   {
/* 261 */     JButton button = new JButton(label);
/* 262 */     button.setMnemonic(mnemonic);
/* 263 */     button.setEnabled(false);
/* 264 */     button.addActionListener(this);
/* 265 */     panel.add(button);
/*     */ 
/* 267 */     return button;
/*     */   }
/*     */ 
/*     */   private boolean processFindNext()
/*     */   {
/* 273 */     String needle = this.findData.getText();
/* 274 */     String haystack = this.textComponent.getText();
/* 275 */     int offset = this.textComponent.getSelectionStart();
/* 276 */     String selectedText = this.textComponent.getSelectedText();
/* 277 */     this.textComponent.setSelectionEnd(offset);
/*     */ 
/* 281 */     if (!this.matchCase.isSelected()) {
/* 282 */       haystack = haystack.toLowerCase();
/* 283 */       needle = needle.toLowerCase();
/*     */     }
/*     */ 
/* 289 */     if (needle.equalsIgnoreCase(selectedText)) {
/* 290 */       if (searchDown())
/* 291 */         offset++;
/*     */       else {
/* 293 */         offset--;
/*     */       }
/*     */     }
/*     */ 
/* 297 */     int result = searchFor(needle, haystack, offset);
/*     */ 
/* 299 */     if (result == -1) {
/* 300 */       JOptionPane.showMessageDialog(this, "Finished searching the document.", "Find", 1);
/* 301 */       resetSearchVariables();
/* 302 */       return false;
/*     */     }
/* 304 */     this.textComponent.setSelectionStart(result);
/* 305 */     this.textComponent.setSelectionEnd(result + needle.length());
/*     */ 
/* 307 */     return true;
/*     */   }
/*     */ 
/*     */   private boolean searchDown()
/*     */   {
/* 312 */     return this.searchDown.isSelected();
/*     */   }
/*     */ 
/*     */   private int searchFor(String needle, String haystack, int offset)
/*     */   {
/*     */     int result;
/*     */     int wrapSearchOffset;
/*     */     int result;
/* 319 */     if (searchDown()) {
/* 320 */       int wrapSearchOffset = 0;
/* 321 */       result = haystack.indexOf(needle, offset);
/*     */     } else {
/* 323 */       wrapSearchOffset = haystack.length();
/* 324 */       result = haystack.lastIndexOf(needle, offset);
/*     */     }
/*     */ 
/* 330 */     if (result == -1) {
/* 331 */       if (this.searchWrap) {
/* 332 */         return result;
/*     */       }
/* 334 */       this.searchWrap = true;
/* 335 */       return searchFor(needle, haystack, wrapSearchOffset);
/*     */     }
/*     */     int wrapResult;
/*     */     int wrapResult;
/* 342 */     if (searchDown())
/* 343 */       wrapResult = result - this.searchStartPosition.getOffset();
/*     */     else {
/* 345 */       wrapResult = this.searchStartPosition.getOffset() - result - 1;
/*     */     }
/* 347 */     if ((this.searchWrap) && (wrapResult >= 0)) {
/* 348 */       return -1;
/*     */     }
/*     */ 
/* 353 */     if ((this.matchWord.isSelected()) && (!isWord(haystack, result, needle.length()))) {
/* 354 */       if (searchDown()) {
/* 355 */         return searchFor(needle, haystack, result + 1);
/*     */       }
/* 357 */       return searchFor(needle, haystack, result - 1);
/*     */     }
/*     */ 
/* 361 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean isWord(String haystack, int offset, int length) {
/* 365 */     int leftSide = offset - 1;
/* 366 */     int rightSide = offset + length;
/*     */ 
/* 369 */     return (isDelimiter(haystack, leftSide)) && (isDelimiter(haystack, rightSide));
/*     */   }
/*     */ 
/*     */   private boolean isDelimiter(String haystack, int offset)
/*     */   {
/* 375 */     if ((offset < 0) || (offset > haystack.length())) {
/* 376 */       return true;
/*     */     }
/* 378 */     return !Character.isLetterOrDigit(haystack.charAt(offset));
/*     */   }
/*     */ 
/*     */   private boolean processReplace() {
/* 382 */     String needle = this.findData.getText();
/* 383 */     String replaceText = this.replaceData.getText();
/* 384 */     String selectedText = this.textComponent.getSelectedText();
/*     */ 
/* 386 */     if ((this.matchCase.isSelected()) && (needle.equals(selectedText))) {
/* 387 */       this.textComponent.replaceSelection(replaceText);
/*     */     }
/* 389 */     if ((!this.matchCase.isSelected()) && (needle.equalsIgnoreCase(selectedText))) {
/* 390 */       this.textComponent.replaceSelection(replaceText);
/*     */     }
/* 392 */     return processFindNext();
/*     */   }
/*     */ 
/*     */   private void processReplaceAll() {
/* 396 */     JViewport viewport = null;
/* 397 */     Point point = null;
/*     */ 
/* 399 */     resetSearchVariables();
/*     */ 
/* 401 */     Container c = this.textComponent.getParent();
/*     */ 
/* 403 */     if ((c instanceof JViewport)) {
/* 404 */       viewport = (JViewport)c;
/* 405 */       point = viewport.getViewPosition();
/*     */     }
/*     */ 
/* 408 */     while (processReplace());
/* 410 */     if ((c instanceof JViewport))
/* 411 */       viewport.setViewPosition(point);
/*     */   }
/*     */ 
/*     */   private void processClose()
/*     */   {
/* 417 */     setVisible(false);
/*     */   }
/*     */ 
/*     */   private void setTextComponent(JTextComponent textComponent) {
/* 421 */     if (this.textComponent != textComponent) {
/* 422 */       this.textComponent = textComponent;
/* 423 */       resetSearchVariables();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void resetSearchVariables() {
/*     */     try {
/* 429 */       this.searchWrap = false;
/* 430 */       this.searchStartPosition = this.textComponent.getDocument().createPosition(this.textComponent.getSelectionStart());
/*     */     } catch (BadLocationException e) {
/* 432 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.impl.FindReplaceDialog
 * JD-Core Version:    0.6.0
 */