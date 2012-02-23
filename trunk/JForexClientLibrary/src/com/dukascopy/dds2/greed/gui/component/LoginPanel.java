/*     */ package com.dukascopy.dds2.greed.gui.component;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.ConnectAction;
/*     */ import com.dukascopy.dds2.greed.actions.DisconnectAction;
/*     */ import com.dukascopy.dds2.greed.gui.CaptchaObtainer;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.MessageList;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.MessagePanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager.Language;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRadioButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.settings.PreferencesStorage;
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Image;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseMotionAdapter;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import javax.swing.AbstractAction;
/*     */ import javax.swing.ActionMap;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.InputMap;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JPasswordField;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.JProgressBar;
/*     */ import javax.swing.JRadioButton;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.KeyStroke;
/*     */ import javax.swing.Timer;
/*     */ import javax.swing.text.DefaultEditorKit.CopyAction;
/*     */ import javax.swing.text.DefaultEditorKit.PasteAction;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class LoginPanel extends JPanel
/*     */ {
/*  93 */   private static final Logger LOGGER = LoggerFactory.getLogger(LoginPanel.class);
/*     */   public static final String ID_JP_LOGIN = "ID_JP_LOGIN";
/*     */   public static final String ID_JFIELD_LOGINFIELD = "ID_JFIELD_LOGINFIELD";
/*     */   public static final String ID_JFIELD_PASSWORDFIELD = "ID_JFIELD_PASSWORDFIELD";
/*     */   public static final String ID_JFIELD_LOGINBUTTON = "ID_JFIELD_LOGINBUTTON";
/*     */   private static final String ACTION_KEY_ENTER = "enter";
/*     */   private static final String ACTION_KEY_ENTER_TO_PWD_FIELD = "enterToPwdField";
/*     */   private static final String ACTION_KEY_SPACE = "space";
/*     */   private static final String ACTION_KEY_EXIT = "exit";
/*     */   private static final int PIN_TIMEOUT = 300;
/*     */   private static final int PIN_HEIGHT = 150;
/* 108 */   private final MessagePanel messagePanel = new MessagePanel(false);
/*     */   private JProgressBar progressBar;
/*     */   private JButton loginButton;
/*     */   private JButton exitButton;
/*     */   private JButton cancelButton;
/*     */   private JTextField accountNameField;
/*     */   private JTextField passwordField;
/* 115 */   private final JTextField securePinField = new JPasswordField();
/*     */   private JCheckBox securePinCheckBox;
/*     */   private JPopupMenu popupMenu;
/* 119 */   private Dimension dim = null;
/*     */ 
/* 121 */   private JPanel versionPanel = new JPanel();
/*     */   private PinPanel pinPanel;
/*     */   private JPanel pinInputPanel;
/*     */   private Timer pinTimer;
/*     */   private final JFrame frame;
/*     */   private JPanel cancelP;
/*     */   private JPanel exitButtonPanel;
/*     */   private JRadioButton classicMode;
/*     */   private JRadioButton jforexMode;
/* 134 */   private final JComboBox languageComboBox = new JComboBox(LocalizationManager.Language.values()) { } ;
/*     */   private Map<String, BufferedImage> captcha;
/*     */ 
/*     */   public LoginPanel(JFrame frame)
/*     */   {
/* 156 */     this.frame = frame;
/* 157 */     setName("ID_JP_LOGIN");
/* 158 */     build();
/* 159 */     this.messagePanel.build();
/*     */ 
/* 161 */     this.messagePanel.getMessageList().setTableHeader(null);
/* 162 */     this.messagePanel.getScroll().setVerticalScrollBarPolicy(20);
/*     */   }
/*     */ 
/*     */   private void build()
/*     */   {
/* 169 */     setLayout(new BoxLayout(this, 1));
/*     */ 
/* 171 */     JPanel topPanel = new JPanel();
/* 172 */     topPanel.setLayout(new BoxLayout(topPanel, 1));
/*     */ 
/* 174 */     JRoundedBorder myBorder = new JRoundedBorder(topPanel, GreedContext.CLIENT_MODE + " " + LocalizationManager.getText("rounded.border.auth"));
/* 175 */     myBorder.setTopBorder(4);
/* 176 */     myBorder.setBottomBorder(4);
/*     */ 
/* 178 */     myBorder.setTopInset(14);
/* 179 */     myBorder.setRightInset(9);
/* 180 */     myBorder.setLeftInset(11);
/* 181 */     myBorder.setBottomInset(12);
/*     */ 
/* 183 */     topPanel.setBorder(myBorder);
/*     */ 
/* 185 */     JLocalizableLabel accountNameLabel = new JLocalizableLabel("label.username");
/* 186 */     this.accountNameField = new JTextField();
/* 187 */     this.accountNameField.setName("ID_JFIELD_LOGINFIELD");
/*     */ 
/* 189 */     JLocalizableLabel passwordLabel = new JLocalizableLabel("label.password");
/* 190 */     this.passwordField = new JPasswordField();
/* 191 */     this.passwordField.setName("ID_JFIELD_PASSWORDFIELD");
/*     */ 
/* 193 */     this.popupMenu = createPopupMenu();
/* 194 */     JPanel namePanel = new JPanel();
/*     */ 
/* 196 */     namePanel.setLayout(new GridLayout(3, 2, 0, 5));
/* 197 */     namePanel.setName("namePanelName");
/* 198 */     namePanel.add(accountNameLabel);
/* 199 */     namePanel.add(this.accountNameField);
/* 200 */     namePanel.add(passwordLabel);
/* 201 */     namePanel.add(this.passwordField);
/*     */ 
/* 203 */     namePanel.add(new JLocalizableLabel("short.menu.item.languages"));
/* 204 */     namePanel.add(this.languageComboBox);
/*     */ 
/* 206 */     this.pinPanel = new PinPanel(null);
/* 207 */     this.pinPanel.setVisible(false);
/*     */ 
/* 209 */     this.securePinCheckBox = new JCheckBox();
/* 210 */     this.securePinCheckBox.setFocusable(false);
/*     */ 
/* 212 */     this.securePinCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
/* 213 */     this.securePinField.setEnabled(false);
/* 214 */     this.securePinCheckBox.setHorizontalAlignment(2);
/* 215 */     JLocalizableLabel whatIsIt = new JLocalizableLabel("label.what.is.it");
/* 216 */     whatIsIt.setForeground(new Color(6586017));
/* 217 */     JPanel whatIsItPanel = new JPanel();
/* 218 */     whatIsItPanel.setLayout(new BoxLayout(whatIsItPanel, 0));
/* 219 */     whatIsItPanel.add(this.securePinCheckBox);
/* 220 */     whatIsItPanel.add(whatIsIt);
/*     */ 
/* 222 */     whatIsIt.addMouseListener(new MouseAdapter()
/*     */     {
/*     */       public void mouseClicked(MouseEvent e) {
/* 225 */         PinAboutFrame.getInstance().setVisible(true);
/*     */       }
/*     */ 
/*     */       public void mouseEntered(MouseEvent e) {
/* 229 */         LoginPanel.this.setCursor(Cursor.getPredefinedCursor(12));
/*     */       }
/*     */ 
/*     */       public void mouseExited(MouseEvent e) {
/* 233 */         LoginPanel.this.setCursor(Cursor.getDefaultCursor());
/*     */       }
/*     */     });
/* 238 */     JPanel pinControlPanel = new JPanel();
/* 239 */     pinControlPanel.setLayout(new GridLayout(1, 2));
/* 240 */     pinControlPanel.add(new JLocalizableLabel("label.use.secure.code"));
/* 241 */     pinControlPanel.add(whatIsItPanel);
/*     */ 
/* 243 */     this.pinInputPanel = new JPanel();
/* 244 */     this.pinInputPanel.setLayout(new GridLayout(1, 2));
/*     */ 
/* 246 */     this.pinInputPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
/* 247 */     this.pinInputPanel.add(new JLocalizableLabel("label.secure.code"));
/* 248 */     this.pinInputPanel.add(this.securePinField);
/* 249 */     this.pinInputPanel.setVisible(false);
/*     */ 
/* 251 */     JPanel progressPanel = new JPanel();
/* 252 */     progressPanel.setLayout(new BorderLayout());
/* 253 */     this.progressBar = new JProgressBar(0, 0);
/* 254 */     this.progressBar.setIndeterminate(false);
/* 255 */     progressPanel.setBorder(BorderFactory.createEmptyBorder(3, 8, 0, 8));
/* 256 */     progressPanel.add(this.progressBar);
/*     */ 
/* 258 */     this.pinTimer = new Timer(15000, new ActionListener() {
/*     */       public void actionPerformed(ActionEvent actionEvent) {
/* 260 */         LoginPanel.this.progressBar.setValue(LoginPanel.this.progressBar.getValue() + 15);
/* 261 */         int timeLeft = 300 - LoginPanel.this.progressBar.getValue();
/* 262 */         if (timeLeft <= 0) {
/* 263 */           LoginPanel.this.securePinCheckBox.doClick();
/*     */         } else {
/* 265 */           int mins = timeLeft / 60;
/* 266 */           int secs = timeLeft % 60;
/* 267 */           String s = new StringBuilder().append(0 != mins ? new StringBuilder().append(mins).append(LocalizationManager.getText("progress.bar.min")).toString() : "").append(0 != secs ? new StringBuilder().append(" ").append(secs).append(LocalizationManager.getText("progress.bar.sec")).toString() : "").append(" ").append(LocalizationManager.getText("progress.bar.left")).toString();
/* 268 */           LoginPanel.this.progressBar.setString(s);
/*     */         }
/*     */       }
/*     */     });
/* 273 */     this.securePinCheckBox.addItemListener(new ItemListener() {
/*     */       public void itemStateChanged(ItemEvent event) {
/* 275 */         if (null == LoginPanel.this.dim) LoginPanel.access$502(LoginPanel.this, LoginPanel.this.getSize());
/* 276 */         JCheckBox sourceCheckBox = (JCheckBox)event.getSource();
/* 277 */         LoginPanel.this.securePinField.setEnabled(sourceCheckBox.isSelected());
/* 278 */         if (sourceCheckBox.isSelected()) {
/* 279 */           LoginPanel.this.setCursor(Cursor.getPredefinedCursor(3));
/*     */           try {
/* 281 */             LoginPanel.this.preparePINPanelAndStartTimer(); } catch (IOException e) { LoginPanel.LOGGER.error(e.getMessage(), e);
/* 284 */             LoginPanel.this.pinFail(sourceCheckBox);
/*     */             return; } finally {
/* 287 */             LoginPanel.this.setCursor(Cursor.getDefaultCursor());
/*     */           }
/*     */         } else {
/* 290 */           LoginPanel.this.cleanUp();
/*     */         }
/* 292 */         Dimension d = new Dimension(LoginPanel.this.dim.width, LoginPanel.this.dim.height + (sourceCheckBox.isSelected() ? 150 : 0));
/* 293 */         LoginPanel.this.setSize(d);
/* 294 */         LoginPanel.this.frame.pack();
/*     */       }
/*     */     });
/* 299 */     this.exitButton = new JLocalizableButton("button.exit");
/* 300 */     this.exitButton.setAction(new AbstractAction("button.exit") {
/*     */       public void actionPerformed(ActionEvent e) {
/* 302 */         LoginPanel.this.cancelRequest();
/*     */       }
/*     */     });
/* 306 */     this.cancelButton = new JLocalizableButton("button.cancel");
/* 307 */     this.cancelButton.setAction(new AbstractAction("button.cancel") {
/*     */       public void actionPerformed(ActionEvent e) {
/* 309 */         LoginPanel.this.cancelLoginRequest();
/*     */       }
/*     */     });
/* 313 */     this.loginButton = new JLocalizableButton("button.login")
/*     */     {
/*     */       public void setEnabled(boolean isEnabled) {
/* 316 */         super.setEnabled(isEnabled);
/* 317 */         LoginPanel.this.doEnable(isEnabled);
/*     */       }
/*     */     };
/* 320 */     this.loginButton.setName("ID_JFIELD_LOGINBUTTON");
/* 321 */     this.loginButton.setAction(new AbstractAction("button.login") {
/*     */       public void actionPerformed(ActionEvent e) {
/* 323 */         LoginPanel.this.loginRequest();
/*     */       }
/*     */     });
/* 327 */     InputMap imLogin = new JTextField().getInputMap(0);
/* 328 */     imLogin.put(KeyStroke.getKeyStroke(10, 0), "enterToPwdField");
/* 329 */     imLogin.put(KeyStroke.getKeyStroke(27, 0), "exit");
/*     */ 
/* 331 */     InputMap imPassword = new JPasswordField().getInputMap(0);
/* 332 */     imPassword.put(KeyStroke.getKeyStroke(10, 0), "enter");
/* 333 */     imPassword.put(KeyStroke.getKeyStroke(27, 0), "exit");
/*     */ 
/* 335 */     InputMap imPin = new JTextField().getInputMap(0);
/* 336 */     imPin.put(KeyStroke.getKeyStroke(10, 0), "enter");
/* 337 */     imPin.put(KeyStroke.getKeyStroke(27, 0), "exit");
/*     */ 
/* 339 */     InputMap imButt = new JButton().getInputMap(0);
/* 340 */     imButt.put(KeyStroke.getKeyStroke(27, 0), "exit");
/* 341 */     imButt.put(KeyStroke.getKeyStroke(10, 0), "enter");
/* 342 */     imButt.put(KeyStroke.getKeyStroke(32, 0), "space");
/*     */ 
/* 344 */     ActionMap am = new JPasswordField().getActionMap();
/* 345 */     am.put("enterToPwdField", new AbstractAction()
/*     */     {
/*     */       public void actionPerformed(ActionEvent evt) {
/* 348 */         LoginPanel.this.accountNameField.transferFocus();
/*     */       }
/*     */     });
/* 352 */     am.put("enter", new AbstractAction()
/*     */     {
/*     */       public void actionPerformed(ActionEvent evt) {
/* 355 */         Object source = evt.getSource();
/* 356 */         if (source == LoginPanel.this.loginButton) {
/* 357 */           LoginPanel.this.loginRequest();
/* 358 */           LoginPanel.this.loginButton.transferFocusBackward();
/* 359 */         } else if (source == LoginPanel.this.exitButton) {
/* 360 */           LoginPanel.this.cancelRequest();
/* 361 */         } else if ((LoginPanel.this.passwordField == source) && (LoginPanel.this.securePinCheckBox.isSelected())) {
/* 362 */           LoginPanel.this.passwordField.transferFocus();
/*     */         } else {
/* 364 */           LoginPanel.this.loginRequest();
/* 365 */           LoginPanel.this.loginButton.transferFocusBackward();
/*     */         }
/*     */       }
/*     */     });
/* 370 */     am.put("exit", new AbstractAction()
/*     */     {
/*     */       public void actionPerformed(ActionEvent evt) {
/* 373 */         LoginPanel.this.cancelRequest();
/*     */       }
/*     */     });
/* 377 */     am.put("space", new AbstractAction()
/*     */     {
/*     */       public void actionPerformed(ActionEvent evt) {
/* 380 */         Object source = evt.getSource();
/* 381 */         if (source == LoginPanel.this.loginButton) {
/* 382 */           LoginPanel.this.loginRequest();
/* 383 */           LoginPanel.this.loginButton.transferFocusBackward();
/* 384 */         } else if (source == LoginPanel.this.exitButton) {
/* 385 */           LoginPanel.this.cancelRequest();
/*     */         }
/*     */       }
/*     */     });
/* 391 */     this.loginButton.setInputMap(0, imButt);
/* 392 */     this.loginButton.setActionMap(am);
/*     */ 
/* 394 */     this.exitButton.setInputMap(0, imButt);
/* 395 */     this.exitButton.setActionMap(am);
/*     */ 
/* 397 */     this.passwordField.setInputMap(0, imPassword);
/* 398 */     this.passwordField.setActionMap(am);
/* 399 */     this.passwordField.add(this.popupMenu);
/* 400 */     this.passwordField.setComponentPopupMenu(this.popupMenu);
/*     */ 
/* 403 */     this.securePinField.setInputMap(0, imPin);
/* 404 */     this.securePinField.setActionMap(am);
/*     */ 
/* 406 */     this.accountNameField.setInputMap(0, imLogin);
/* 407 */     this.accountNameField.setActionMap(am);
/* 408 */     this.accountNameField.add(this.popupMenu);
/* 409 */     this.accountNameField.setComponentPopupMenu(this.popupMenu);
/*     */ 
/* 411 */     this.messagePanel.setBorder(new JLocalizableRoundedBorder(this.messagePanel, "header.messages"));
/*     */ 
/* 414 */     topPanel.add(namePanel);
/*     */ 
/* 417 */     topPanel.add(this.pinPanel);
/*     */ 
/* 420 */     topPanel.add(Box.createVerticalStrut(5));
/* 421 */     topPanel.add(pinControlPanel);
/* 422 */     topPanel.add(Box.createVerticalStrut(5));
/*     */ 
/* 425 */     topPanel.add(this.pinInputPanel);
/*     */ 
/* 428 */     JPanel buttonPanel = new JPanel();
/* 429 */     buttonPanel.setLayout(new GridLayout(1, 2, 2, 0));
/*     */ 
/* 431 */     JPanel loginP = new JPanel();
/* 432 */     loginP.setLayout(new GridLayout(1, 1));
/* 433 */     loginP.add(this.loginButton);
/*     */ 
/* 435 */     this.cancelP = new JPanel();
/* 436 */     this.cancelP.setLayout(new GridLayout(1, 1));
/* 437 */     this.cancelP.add(this.cancelButton);
/* 438 */     this.cancelP.setVisible(false);
/*     */ 
/* 440 */     this.exitButtonPanel = new JPanel();
/* 441 */     this.exitButtonPanel.setLayout(new GridLayout(1, 1));
/* 442 */     this.exitButtonPanel.add(this.exitButton);
/*     */ 
/* 444 */     JPanel exitP = new JPanel();
/* 445 */     exitP.setLayout(new BoxLayout(exitP, 2));
/* 446 */     exitP.add(this.exitButtonPanel);
/* 447 */     exitP.add(this.cancelP);
/*     */ 
/* 449 */     buttonPanel.add(loginP);
/* 450 */     buttonPanel.add(exitP);
/*     */ 
/* 452 */     topPanel.add(Box.createHorizontalStrut(240));
/* 453 */     topPanel.add(buttonPanel);
/*     */ 
/* 455 */     int horizontlStrut = 3;
/*     */ 
/* 457 */     JPanel outerTopPanel = new JPanel();
/* 458 */     outerTopPanel.setLayout(new BoxLayout(outerTopPanel, 1));
/* 459 */     outerTopPanel.add(topPanel);
/* 460 */     outerTopPanel.setBorder(BorderFactory.createEmptyBorder(0, horizontlStrut, 0, horizontlStrut));
/*     */ 
/* 462 */     add(Box.createVerticalStrut(15));
/*     */ 
/* 464 */     add(outerTopPanel);
/*     */ 
/* 466 */     add(progressPanel);
/*     */ 
/* 468 */     JPanel outerMessagesPanel = new JPanel();
/* 469 */     outerMessagesPanel.setLayout(new BoxLayout(outerMessagesPanel, 1));
/* 470 */     outerMessagesPanel.add(this.messagePanel);
/* 471 */     outerMessagesPanel.setBorder(BorderFactory.createEmptyBorder(0, horizontlStrut, 0, horizontlStrut));
/*     */ 
/* 473 */     add(outerMessagesPanel);
/*     */ 
/* 475 */     initAndAddVersionPanel();
/*     */ 
/* 477 */     if (GreedContext.isLive())
/* 478 */       this.securePinCheckBox.doClick();
/*     */   }
/*     */ 
/*     */   private JPopupMenu createPopupMenu()
/*     */   {
/* 486 */     JMenuItem menuItem = null;
/* 487 */     JPopupMenu popupMenu = new JPopupMenu();
/*     */ 
/* 489 */     menuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
/* 490 */     menuItem.setText(LocalizationManager.getText("menuItem.copy"));
/* 491 */     menuItem.setMnemonic(67);
/* 492 */     popupMenu.add(menuItem);
/*     */ 
/* 494 */     menuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
/* 495 */     menuItem.setText(LocalizationManager.getText("menuItem.paste"));
/* 496 */     menuItem.setMnemonic(80);
/* 497 */     popupMenu.add(menuItem);
/*     */ 
/* 499 */     return popupMenu;
/*     */   }
/*     */ 
/*     */   public void doEnable(boolean isEnabled) {
/* 503 */     if (this.classicMode != null) this.classicMode.setEnabled(isEnabled);
/* 504 */     if (this.jforexMode != null) this.jforexMode.setEnabled(isEnabled);
/* 505 */     if (this.exitButtonPanel != null) this.exitButtonPanel.setVisible(isEnabled);
/* 506 */     if (this.cancelP != null) this.cancelP.setVisible(!isEnabled); 
/*     */   }
/*     */ 
/*     */   private void initAndAddVersionPanel()
/*     */   {
/* 510 */     ButtonGroup group = new ButtonGroup();
/* 511 */     this.classicMode = new JLocalizableRadioButton("classic.radio")
/*     */     {
/*     */     };
/* 522 */     this.jforexMode = new JLocalizableRadioButton("jforex.radio")
/*     */     {
/*     */     };
/* 533 */     PreferencesStorage.setJForexMode(isJForexModeOnStart());
/*     */ 
/* 535 */     group.add(this.jforexMode);
/* 536 */     group.add(this.classicMode);
/*     */ 
/* 538 */     JPanel radioBox = new JPanel();
/* 539 */     radioBox.setLayout(new BoxLayout(radioBox, 0));
/*     */ 
/* 541 */     radioBox.add(Box.createHorizontalStrut(10));
/* 542 */     radioBox.add(this.jforexMode);
/* 543 */     radioBox.add(this.classicMode);
/*     */ 
/* 545 */     radioBox.setVisible(!GreedContext.IS_KAKAKU_LABEL);
/*     */ 
/* 547 */     this.versionPanel.setLayout(new BorderLayout());
/*     */ 
/* 549 */     this.versionPanel.add(radioBox, "Before");
/* 550 */     this.versionPanel.add(new JLabel(GreedContext.CLIENT_VERSION + " "), "After");
/* 551 */     this.versionPanel.setVisible(true);
/*     */ 
/* 553 */     add(this.versionPanel);
/* 554 */     add(Box.createVerticalStrut(3));
/*     */   }
/*     */ 
/*     */   private void pinFail(JCheckBox sourceCheckBox) {
/* 558 */     sourceCheckBox.setSelected(false);
/* 559 */     Notification notification = new Notification(null, "PIN service temporarily not available.");
/* 560 */     notification.setPriority("ERROR");
/* 561 */     this.messagePanel.postMessage(notification);
/* 562 */     this.messagePanel.repaint();
/* 563 */     cleanUp();
/*     */   }
/*     */ 
/*     */   private void cleanUp() {
/* 567 */     this.pinPanel.setVisible(false);
/* 568 */     this.pinInputPanel.setVisible(false);
/* 569 */     this.captcha = null;
/* 570 */     this.pinTimer.stop();
/* 571 */     this.progressBar.setValue(0);
/* 572 */     this.progressBar.setString("");
/* 573 */     this.securePinField.setText("");
/*     */   }
/*     */ 
/*     */   private boolean isJForexModeOnStart() {
/* 577 */     String platformMode = System.getProperty("jnlp.platform.mode");
/*     */ 
/* 579 */     return (platformMode != null) && (!"jclient".equals(platformMode));
/*     */   }
/*     */ 
/*     */   private void preparePINPanelAndStartTimer()
/*     */     throws IOException
/*     */   {
/* 586 */     CaptchaObtainer captchaObtainer = null;
/*     */     try {
/* 588 */       captchaObtainer = new CaptchaObtainer();
/* 589 */       captchaObtainer.start();
/* 590 */       this.captcha = ((Map)captchaObtainer.get());
/*     */     } catch (InterruptedException e) {
/* 592 */       LOGGER.error(e.getMessage(), e);
/*     */     } catch (ExecutionException e) {
/* 594 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */ 
/* 597 */     if ((this.captcha == null) || (this.captcha.isEmpty())) {
/* 598 */       if ((captchaObtainer != null) && (captchaObtainer.getError() != null)) {
/* 599 */         LOGGER.error(captchaObtainer.getError().getMessage());
/* 600 */         pinFail(this.securePinCheckBox);
/*     */       }
/* 602 */       return;
/*     */     }
/* 604 */     ImageIcon pinImage = new ImageIcon((Image)this.captcha.values().iterator().next());
/* 605 */     if (-1 == pinImage.getIconWidth()) {
/* 606 */       throw new IOException("bad image");
/*     */     }
/*     */ 
/* 609 */     this.pinPanel.setImage(pinImage);
/*     */ 
/* 611 */     this.pinPanel.setVisible(true);
/* 612 */     this.pinInputPanel.setVisible(true);
/* 613 */     this.progressBar.setIndeterminate(false);
/* 614 */     this.progressBar.setStringPainted(true);
/* 615 */     this.progressBar.setMaximum(300);
/* 616 */     this.progressBar.setString("5" + LocalizationManager.getText("progress.bar.min") + " " + LocalizationManager.getText("progress.bar.left"));
/* 617 */     this.progressBar.setValue(0);
/* 618 */     if (this.pinTimer.isRunning()) {
/* 619 */       this.pinTimer.stop();
/*     */     }
/* 621 */     this.pinTimer.start();
/*     */   }
/*     */ 
/*     */   protected void cancelLoginRequest() {
/* 625 */     LoginTimer.getInstance().stopTimer();
/* 626 */     this.progressBar.setIndeterminate(false);
/* 627 */     this.loginButton.setEnabled(true);
/*     */ 
/* 629 */     GreedContext.publishEvent(new DisconnectAction(this, false));
/*     */ 
/* 631 */     Notification notification = new Notification(null, " Logging process canceled by user ");
/* 632 */     notification.setPriority("INFO");
/* 633 */     this.messagePanel.postMessage(notification);
/* 634 */     this.messagePanel.repaint();
/*     */   }
/*     */ 
/*     */   public void cancelRequest() {
/* 638 */     System.exit(0);
/*     */   }
/*     */ 
/*     */   protected void loginRequest() {
/* 642 */     if (("".equals(this.accountNameField.getText().trim())) || ("".equals(this.passwordField.getText().trim()))) {
/* 643 */       return;
/*     */     }
/*     */ 
/* 646 */     this.loginButton.setEnabled(false);
/*     */ 
/* 648 */     if (this.progressBar.isIndeterminate()) {
/* 649 */       return;
/*     */     }
/*     */ 
/* 652 */     LOGGER.debug("Login request");
/*     */ 
/* 654 */     String pinValue = null;
/* 655 */     String passwordValue = this.passwordField.getText();
/* 656 */     String loginValue = this.accountNameField.getText().trim();
/*     */ 
/* 658 */     GreedContext.setConfig("account_name", loginValue);
/* 659 */     GreedContext.setConfig(" ", passwordValue);
/*     */ 
/* 661 */     if ((this.securePinCheckBox.isSelected()) && 
/* 662 */       (this.captcha != null)) {
/* 663 */       pinValue = this.securePinField.getText().trim();
/*     */ 
/* 665 */       if ((pinValue != null) && (!"".equals(pinValue.trim()))) {
/* 666 */         GreedContext.setPinEntered(true);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 671 */     String instanceId = (String)GreedContext.getConfig("SESSION_ID");
/* 672 */     LOGGER.debug("Instance ID : " + instanceId);
/*     */ 
/* 674 */     loginRequest(pinValue);
/*     */   }
/*     */ 
/*     */   public void loginRequest(String pinValue) {
/* 678 */     this.progressBar.setString("");
/* 679 */     this.progressBar.setIndeterminate(true);
/*     */ 
/* 681 */     ConnectAction connectAction = new ConnectAction(this, this.captcha, pinValue);
/*     */ 
/* 687 */     LoginTimer.getInstance().start();
/* 688 */     GreedContext.publishEvent(connectAction);
/*     */   }
/*     */ 
/*     */   public MessagePanel getMessagePanel() {
/* 692 */     return this.messagePanel;
/*     */   }
/*     */ 
/*     */   public JProgressBar getProgressBar() {
/* 696 */     return this.progressBar;
/*     */   }
/*     */ 
/*     */   public JButton getLoginButton() {
/* 700 */     return this.loginButton;
/*     */   }
/*     */ 
/*     */   public JButton getExitButton() {
/* 704 */     return this.exitButton;
/*     */   }
/*     */ 
/*     */   public JTextField getAccountNameField() {
/* 708 */     return this.accountNameField;
/*     */   }
/*     */ 
/*     */   public JTextField getPasswordField() {
/* 712 */     return this.passwordField;
/*     */   }
/*     */ 
/*     */   public JCheckBox getSecurePinCheckBox() {
/* 716 */     return this.securePinCheckBox;
/*     */   }
/*     */ 
/*     */   public void setSecurePinCheckBox(JCheckBox securePinCheckBox) {
/* 720 */     this.securePinCheckBox = securePinCheckBox;
/*     */   }
/*     */ 
/*     */   private LocalizationManager.Language getSelectedLanguage() {
/* 724 */     for (int i = 0; i < LocalizationManager.Language.values().length; i++) {
/* 725 */       if (LocalizationManager.Language.values()[i].locale.equals(LocalizationManager.getSelectedLocale())) {
/* 726 */         return LocalizationManager.Language.values()[i];
/*     */       }
/*     */     }
/* 729 */     return LocalizationManager.Language.ENGLISH;
/*     */   }
/*     */ 
/*     */   public static class LoginTimer extends Thread
/*     */   {
/* 794 */     private static LoginTimer timer = null;
/* 795 */     private long startTime = 0L;
/* 796 */     private long period = 60000L;
/* 797 */     private boolean isCanceled = false;
/*     */ 
/*     */     public void start()
/*     */     {
/* 802 */       this.startTime = System.currentTimeMillis();
/* 803 */       this.isCanceled = false;
/*     */     }
/*     */ 
/*     */     public void stopTimer() {
/* 807 */       this.startTime = 0L;
/* 808 */       this.isCanceled = true;
/*     */     }
/*     */ 
/*     */     public void run() {
/* 812 */       if ((!isRunning()) && (this.startTime != 0L))
/* 813 */         this.startTime = 0L;
/*     */     }
/*     */ 
/*     */     public static LoginTimer getInstance()
/*     */     {
/* 818 */       if (timer == null) {
/* 819 */         timer = new LoginTimer();
/*     */       }
/* 821 */       return timer;
/*     */     }
/*     */ 
/*     */     public boolean isRunning() {
/* 825 */       return this.startTime + this.period > System.currentTimeMillis();
/*     */     }
/*     */ 
/*     */     public boolean isCanceled()
/*     */     {
/* 831 */       return this.isCanceled;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class PinPanel extends JPanel
/*     */   {
/* 735 */     private final Rectangle recReload = new Rectangle(6, 94, 76, 117);
/* 736 */     private final Rectangle recCancel = new Rectangle(164, 94, 234, 117);
/*     */     private JLabel pictureHolder;
/*     */ 
/*     */     private PinPanel()
/*     */     {
/* 740 */       super();
/*     */ 
/* 742 */       this.pictureHolder = new JLabel();
/* 743 */       add(this.pictureHolder, "Center");
/*     */ 
/* 745 */       addMouseListener(new MouseAdapter(LoginPanel.this)
/*     */       {
/*     */         public void mouseClicked(MouseEvent mouseEvent) {
/* 748 */           Point p = mouseEvent.getPoint();
/* 749 */           if (LoginPanel.PinPanel.this.recCancel.contains(p)) {
/* 750 */             LoginPanel.PinPanel.this.cancel();
/*     */           }
/* 752 */           else if (LoginPanel.PinPanel.this.recReload.contains(p))
/* 753 */             LoginPanel.PinPanel.this.reload();
/*     */         }
/*     */       });
/* 757 */       addMouseMotionListener(new MouseMotionAdapter(LoginPanel.this)
/*     */       {
/*     */         public void mouseMoved(MouseEvent mouseEvent) {
/* 760 */           Point p = mouseEvent.getPoint();
/* 761 */           if ((LoginPanel.PinPanel.this.recCancel.contains(p)) || (LoginPanel.PinPanel.this.recReload.contains(p)))
/* 762 */             LoginPanel.PinPanel.this.setCursor(Cursor.getPredefinedCursor(12));
/*     */           else
/* 764 */             LoginPanel.PinPanel.this.setCursor(Cursor.getDefaultCursor());
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/*     */     private void setImage(ImageIcon image) {
/* 771 */       this.pictureHolder.setIcon(image);
/*     */     }
/*     */ 
/*     */     private void reload() {
/*     */       try {
/* 776 */         setCursor(Cursor.getPredefinedCursor(3));
/* 777 */         this.pictureHolder.setIcon(null);
/* 778 */         LoginPanel.this.preparePINPanelAndStartTimer();
/*     */       } catch (IOException e) {
/* 780 */         LoginPanel.this.securePinCheckBox.doClick();
/* 781 */         LoginPanel.LOGGER.error(e.getMessage(), e);
/*     */       } finally {
/* 783 */         setCursor(Cursor.getDefaultCursor());
/*     */       }
/*     */     }
/*     */ 
/*     */     private void cancel() {
/* 788 */       LoginPanel.this.securePinCheckBox.doClick();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.LoginPanel
 * JD-Core Version:    0.6.0
 */