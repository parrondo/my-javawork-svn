/*     */ package com.dukascopy.dds2.greed.gui.component.dialog.components;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.ApplicationClock;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.JTimeUnitComboBox;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableComboBox;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRadioButton;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.util.spinners.CommonJSpinner;
/*     */ import com.toedter.calendar.JDateChooser;
/*     */ import com.toedter.calendar.JSpinnerDateEditor;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.CardLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.SystemColor;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.ButtonModel;
/*     */ import javax.swing.InputVerifier;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JFormattedTextField;
/*     */ import javax.swing.JFormattedTextField.AbstractFormatter;
/*     */ import javax.swing.JFormattedTextField.AbstractFormatterFactory;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.SpinnerModel;
/*     */ import javax.swing.border.EmptyBorder;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class OrderTimeLimitationPanel extends JPanel
/*     */   implements ActionListener
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  73 */   private static final Logger LOGGER = LoggerFactory.getLogger(OrderTimeLimitationPanel.class);
/*     */   private static final int ONE_MINUTE = 60000;
/*     */   private static final String DATE_FORMAT = "dd.MM.yyyy";
/*  78 */   private ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*  79 */   private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
/*  80 */   private final int INSET = 3;
/*     */   private JFrame parent;
/*     */   private JPanel cards;
/*     */   private JPanel radioPanel;
/*     */   private JPanel errorPanel;
/*  86 */   private JPanel pGoodFor = new JPanel();
/*  87 */   private JPanel pGoodTill = new JPanel();
/*     */   private ButtonGroup rbg;
/*     */   private JLocalizableRadioButton rbGTC;
/*     */   private JLocalizableRadioButton rbGF;
/*     */   private JLocalizableRadioButton rbGT;
/*     */   private JLocalizableLabel errorLabel;
/*     */   private CommonJSpinner goodForSpinner;
/*     */   private JLocalizableComboBox comboBox;
/*     */   private JDateChooser dateChooser;
/*     */   private JTimeSpinner timeSpinner;
/*     */ 
/*     */   public OrderTimeLimitationPanel(JFrame parent, long execTimeoutInMillis)
/*     */   {
/*  99 */     build();
/* 100 */     init();
/* 101 */     initRbAndGuessGtGf(execTimeoutInMillis);
/*     */   }
/*     */ 
/*     */   public OrderTimeLimitationPanel(JFrame parent) {
/* 105 */     this.parent = parent;
/* 106 */     build();
/* 107 */     init();
/*     */   }
/*     */ 
/*     */   private void build()
/*     */   {
/* 112 */     setLayout(new BorderLayout());
/*     */ 
/* 114 */     initRadioBtnPanel();
/* 115 */     initGoodForPanel();
/* 116 */     initGoodTillPanel();
/* 117 */     initErrorPanel();
/*     */ 
/* 119 */     this.cards = new JPanel(new CardLayout());
/* 120 */     this.cards.add(new JPanel(), "radio.GTC");
/* 121 */     this.cards.add(this.pGoodFor, "radio.good.for");
/* 122 */     this.cards.add(this.pGoodTill, "radio.good.till");
/* 123 */     this.cards.add(this.errorPanel, "label.error.ok");
/*     */ 
/* 125 */     add(this.radioPanel, "First");
/* 126 */     add(this.cards, "Center");
/*     */ 
/* 128 */     this.cards.setVisible(false);
/*     */   }
/*     */ 
/*     */   private void init() {
/* 132 */     this.sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 133 */     TimeString ts = new TimeString();
/* 134 */     this.goodForSpinner.setValue(this.storage.restoreOrderValidityTime());
/* 135 */     this.timeSpinner.setValue(ts);
/* 136 */     this.dateChooser.setDate(new Date(ts.time));
/*     */   }
/*     */ 
/*     */   private void initRadioBtnPanel() {
/* 140 */     this.radioPanel = new JPanel(new GridLayout(1, 3, 5, 0));
/* 141 */     this.radioPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
/* 142 */     this.rbGTC = new JLocalizableRadioButton("radio.GTC");
/* 143 */     this.rbGF = new JLocalizableRadioButton("radio.good.for");
/* 144 */     this.rbGT = new JLocalizableRadioButton("radio.good.till");
/*     */ 
/* 146 */     this.rbGTC.setActionCommand("radio.GTC");
/* 147 */     this.rbGF.setActionCommand("radio.good.for");
/* 148 */     this.rbGT.setActionCommand("radio.good.till");
/*     */ 
/* 150 */     this.rbGTC.addActionListener(this);
/* 151 */     this.rbGF.addActionListener(this);
/* 152 */     this.rbGT.addActionListener(this);
/*     */ 
/* 154 */     this.rbg = new ButtonGroup();
/* 155 */     this.rbg.add(this.rbGTC);
/* 156 */     this.rbg.add(this.rbGF);
/* 157 */     this.rbg.add(this.rbGT);
/*     */ 
/* 159 */     this.rbGTC.setSelected(true);
/* 160 */     this.radioPanel.add(this.rbGTC);
/* 161 */     this.radioPanel.add(this.rbGF);
/* 162 */     this.radioPanel.add(this.rbGT);
/*     */   }
/*     */ 
/*     */   private void initGoodForPanel() {
/* 166 */     this.pGoodFor.setLayout(new GridLayout(1, 2, 3, 0));
/*     */ 
/* 168 */     this.goodForSpinner = new CommonJSpinner(this.storage.restoreOrderValidityTime().doubleValue(), GuiUtilsAndConstants.ONE.doubleValue(), 1.7976931348623157E+308D, GuiUtilsAndConstants.ONE.doubleValue(), 0, false, false);
/*     */ 
/* 170 */     this.goodForSpinner.setHorizontalAlignment(4);
/* 171 */     this.goodForSpinner.setValue(this.storage.restoreOrderValidityTime());
/*     */ 
/* 173 */     this.comboBox = new JTimeUnitComboBox();
/* 174 */     this.comboBox.setSelectedIndex(this.storage.restoreOrderValidityTimeUnit());
/*     */ 
/* 176 */     this.pGoodFor.add(this.goodForSpinner);
/* 177 */     this.pGoodFor.add(this.comboBox);
/*     */   }
/*     */ 
/*     */   private void initGoodTillPanel() {
/* 181 */     this.pGoodTill.setLayout(new BoxLayout(this.pGoodTill, 0));
/*     */ 
/* 183 */     this.dateChooser = new JDateChooser(new JSpinnerDateEditor());
/* 184 */     BorderLayout layout = (BorderLayout)this.dateChooser.getLayout();
/* 185 */     layout.setHgap(2);
/*     */ 
/* 187 */     this.dateChooser.setDateFormatString("dd.MM.yyyy");
/* 188 */     this.dateChooser.setDate(new Date());
/*     */ 
/* 190 */     this.pGoodTill.add(this.dateChooser);
/* 191 */     this.pGoodTill.add(Box.createHorizontalStrut(3));
/*     */ 
/* 193 */     this.timeSpinner = new JTimeSpinner();
/* 194 */     this.timeSpinner.setValue(this.timeSpinner.adjustTime(60));
/* 195 */     this.timeSpinner.getEditor().setBackground(SystemColor.text);
/* 196 */     this.timeSpinner.getEditor().setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
/* 197 */     this.pGoodTill.add(this.timeSpinner);
/* 198 */     this.pGoodTill.add(Box.createHorizontalStrut(3));
/* 199 */     this.pGoodTill.add(new JLocalizableLabel("label.gmt"));
/*     */   }
/*     */ 
/*     */   private void initErrorPanel() {
/* 203 */     this.errorPanel = new JPanel(new FlowLayout(1));
/* 204 */     this.errorLabel = new JLocalizableLabel("label.error.ok");
/* 205 */     this.errorLabel.setForeground(Color.red);
/* 206 */     this.errorPanel.add(this.errorLabel);
/* 207 */     this.errorPanel.setVisible(false);
/* 208 */     this.errorPanel.addMouseListener(new MouseAdapter() {
/*     */       public void mouseReleased(MouseEvent e) {
/* 210 */         CardLayout cl = (CardLayout)(CardLayout)OrderTimeLimitationPanel.this.cards.getLayout();
/* 211 */         cl.show(OrderTimeLimitationPanel.this.cards, OrderTimeLimitationPanel.this.rbg.getSelection().getActionCommand());
/* 212 */         OrderTimeLimitationPanel.this.errorPanel.setVisible(false);
/*     */       }
/*     */ 
/*     */       public void mouseEntered(MouseEvent e) {
/* 216 */         OrderTimeLimitationPanel.this.setCursor(Cursor.getPredefinedCursor(12));
/*     */       }
/*     */ 
/*     */       public void mouseExited(MouseEvent e) {
/* 220 */         OrderTimeLimitationPanel.this.setCursor(Cursor.getDefaultCursor());
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void initRbAndGuessGtGf(long orderTimeoutMillis)
/*     */   {
/* 231 */     long currentPlatformTime = ((ApplicationClock)GreedContext.get("applicationClock")).getTime();
/*     */ 
/* 233 */     long deltaMillis = Math.abs(orderTimeoutMillis - currentPlatformTime);
/*     */ 
/* 235 */     Calendar cal = Calendar.getInstance();
/* 236 */     cal.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 237 */     cal.setTimeInMillis(orderTimeoutMillis);
/*     */ 
/* 239 */     TimeString ts = new TimeString(cal.getTime());
/* 240 */     this.timeSpinner.setValue(ts);
/* 241 */     this.dateChooser.setDate(cal.getTime());
/*     */ 
/* 243 */     if (deltaMillis < 1576800000000L) {
/* 244 */       this.pGoodFor.setVisible(false);
/* 245 */       this.pGoodTill.setVisible(true);
/*     */ 
/* 247 */       this.rbGT.setSelected(true);
/* 248 */       this.comboBox.setSelectedIndex(this.storage.restoreOrderValidityTimeUnit());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void refresh() {
/* 253 */     TimeString ts = new TimeString();
/* 254 */     ts.setTime(ts.getTime() + 60000L);
/* 255 */     this.timeSpinner.setValue(ts);
/* 256 */     this.dateChooser.setDate(new Date(ts.getTime()));
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/* 262 */     CardLayout cl = (CardLayout)(CardLayout)this.cards.getLayout();
/* 263 */     cl.show(this.cards, e.getActionCommand());
/*     */ 
/* 265 */     if ("radio.GTC".equals(e.getActionCommand())) {
/* 266 */       this.cards.setVisible(false);
/*     */     } else {
/* 268 */       refresh();
/* 269 */       this.cards.setVisible(true);
/*     */     }
/*     */ 
/* 272 */     if (this.parent != null) this.parent.pack();
/*     */   }
/*     */ 
/*     */   public Long getTimeValue()
/*     */   {
/* 281 */     Long ttl = null;
/* 282 */     Calendar calendar = Calendar.getInstance();
/* 283 */     calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */ 
/* 285 */     if (this.rbGF.isSelected())
/*     */       try {
/* 287 */         long currentPlatformTime = ((ApplicationClock)GreedContext.get("applicationClock")).getTime();
/* 288 */         ttl = Long.valueOf(currentPlatformTime + GuiUtilsAndConstants.ONE_THUSAND.multiply((BigDecimal)this.goodForSpinner.getValue()).multiply(((JTimeUnitComboBox)this.comboBox).getSelectedTime()).intValue());
/*     */       }
/*     */       catch (Exception e) {
/* 291 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/* 293 */     else if (this.rbGT.isSelected())
/*     */     {
/*     */       try
/*     */       {
/* 298 */         Date d = this.sdf.parse(this.timeSpinner.getValue().toString());
/* 299 */         calendar.setTime(d);
/* 300 */         int h = calendar.get(11);
/* 301 */         int m = calendar.get(12);
/* 302 */         int s = calendar.get(13);
/* 303 */         calendar.setTimeZone(TimeZone.getDefault());
/* 304 */         calendar.setTime(this.dateChooser.getDate());
/* 305 */         int dom = calendar.get(5);
/* 306 */         int moy = calendar.get(2);
/* 307 */         int y = calendar.get(1);
/* 308 */         calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 309 */         calendar.set(y, moy, dom, h, m, s);
/* 310 */         ttl = Long.valueOf(calendar.getTime().getTime());
/*     */       } catch (ParseException e) {
/* 312 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/* 315 */     return ttl;
/*     */   }
/*     */ 
/*     */   public boolean isTimeValid()
/*     */   {
/* 325 */     if (this.rbGTC.isSelected())
/* 326 */       return true;
/* 327 */     if (this.rbGT.isSelected()) {
/* 328 */       Calendar calendar = Calendar.getInstance();
/* 329 */       calendar.setTime(new Date());
/* 330 */       calendar.set(11, 0);
/* 331 */       calendar.set(12, 0);
/* 332 */       calendar.set(13, 0);
/* 333 */       calendar.set(14, 0);
/* 334 */       Date now = calendar.getTime();
/* 335 */       calendar.setTime(this.dateChooser.getDate());
/* 336 */       calendar.set(11, 0);
/* 337 */       calendar.set(12, 0);
/* 338 */       calendar.set(13, 0);
/* 339 */       calendar.set(14, 0);
/* 340 */       Date then = calendar.getTime();
/* 341 */       if (then.before(now)) {
/* 342 */         this.errorLabel.setText("label.error.select.date");
/* 343 */         CardLayout cl = (CardLayout)(CardLayout)this.cards.getLayout();
/* 344 */         cl.show(this.cards, "label.error.ok");
/* 345 */         return false;
/*     */       }
/*     */ 
/* 348 */       TimeVerifier verifier = this.timeSpinner.getVerifier();
/* 349 */       if (!verifier.verify(this.timeSpinner.getEditor())) {
/* 350 */         this.errorLabel.setText("label.error.valid.time");
/* 351 */         CardLayout cl = (CardLayout)(CardLayout)this.cards.getLayout();
/* 352 */         cl.show(this.cards, "label.error.ok");
/* 353 */         return false;
/*     */       }
/* 355 */     } else if (!this.goodForSpinner.validateEditor()) {
/* 356 */       return false;
/*     */     }
/*     */ 
/* 359 */     return true;
/*     */   }
/*     */ 
/*     */   public void setEnabled(boolean isEnabled)
/*     */   {
/* 513 */     super.setEnabled(isEnabled);
/* 514 */     this.rbGTC.setEnabled(isEnabled);
/* 515 */     this.rbGT.setEnabled(isEnabled);
/* 516 */     this.rbGF.setEnabled(isEnabled);
/*     */   }
/*     */ 
/*     */   private class JTimeSpinner extends JSpinner
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     private JFormattedTextField editor;
/*     */     private OrderTimeLimitationPanel.TimeString timeString;
/* 423 */     private int caretPosition = 0;
/*     */ 
/*     */     public JTimeSpinner()
/*     */     {
/* 427 */       this.timeString = new OrderTimeLimitationPanel.TimeString(OrderTimeLimitationPanel.this);
/*     */     }
/*     */ 
/*     */     protected JComponent createEditor(SpinnerModel model) {
/* 431 */       this.editor = new JFormattedTextField() { private static final long serialVersionUID = 1L;
/*     */ 
/* 434 */         protected void processFocusEvent(FocusEvent e) { if (1005 == e.getID())
/*     */             try {
/* 436 */               OrderTimeLimitationPanel.JTimeSpinner.access$502(OrderTimeLimitationPanel.JTimeSpinner.this, new OrderTimeLimitationPanel.TimeString(OrderTimeLimitationPanel.this, OrderTimeLimitationPanel.this.sdf.parse(OrderTimeLimitationPanel.JTimeSpinner.this.editor.getText())));
/*     */             }
/*     */             catch (ParseException e1) {
/*     */             }
/* 440 */           super.processFocusEvent(e);
/*     */         }
/*     */       };
/* 443 */       this.editor.setMargin(new Insets(0, 5, 0, 5));
/* 444 */       this.editor.setColumns(8);
/* 445 */       this.editor.setValue(new OrderTimeLimitationPanel.TimeString(OrderTimeLimitationPanel.this));
/* 446 */       this.editor.setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory() {
/*     */         public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
/* 448 */           return new OrderTimeLimitationPanel.TimeFormatter(OrderTimeLimitationPanel.this, null);
/*     */         }
/*     */       });
/* 451 */       this.editor.setInputVerifier(new OrderTimeLimitationPanel.TimeVerifier(OrderTimeLimitationPanel.this, null));
/* 452 */       this.editor.setFocusLostBehavior(1);
/* 453 */       return this.editor;
/*     */     }
/*     */ 
/*     */     public JComponent getEditor() {
/* 457 */       return this.editor;
/*     */     }
/*     */ 
/*     */     public Object getValue() {
/* 461 */       return this.timeString;
/*     */     }
/*     */ 
/*     */     public void setValue(Object value) {
/* 465 */       this.timeString = ((OrderTimeLimitationPanel.TimeString)value);
/* 466 */       this.editor.setValue(value);
/* 467 */       this.editor.setCaretPosition(this.caretPosition);
/*     */     }
/*     */ 
/*     */     public Object getPreviousValue() {
/* 471 */       return adjustTime(-1);
/*     */     }
/*     */ 
/*     */     public Object getNextValue() {
/* 475 */       return adjustTime(1);
/*     */     }
/*     */ 
/*     */     OrderTimeLimitationPanel.TimeString adjustTime(int direction) {
/* 479 */       String TIME_SEPARATOR = ":";
/* 480 */       String text = this.editor.getText();
/* 481 */       this.caretPosition = this.editor.getCaretPosition();
/* 482 */       int separatorHours = text.indexOf(":");
/* 483 */       int separatorMinutes = text.indexOf(":", separatorHours + 1);
/*     */       long modifier;
/*     */       long modifier;
/* 485 */       if (this.caretPosition <= separatorHours) {
/* 486 */         modifier = 3600000L;
/*     */       }
/*     */       else
/*     */       {
/*     */         long modifier;
/* 487 */         if (this.caretPosition <= separatorMinutes)
/* 488 */           modifier = 60000L;
/*     */         else {
/* 490 */           modifier = 1000L;
/*     */         }
/*     */       }
/* 493 */       modifier *= direction;
/* 494 */       long oldTime = this.timeString.getTime();
/* 495 */       OrderTimeLimitationPanel.TimeString time = new OrderTimeLimitationPanel.TimeString(OrderTimeLimitationPanel.this);
/* 496 */       time.setTime(oldTime + modifier);
/* 497 */       this.timeString = time;
/* 498 */       return time;
/*     */     }
/*     */ 
/*     */     String getText()
/*     */     {
/* 503 */       return this.editor.getText();
/*     */     }
/*     */ 
/*     */     OrderTimeLimitationPanel.TimeVerifier getVerifier() {
/* 507 */       return (OrderTimeLimitationPanel.TimeVerifier)this.editor.getInputVerifier();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class TimeFormatter extends JFormattedTextField.AbstractFormatter
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     private TimeFormatter()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Object stringToValue(String text)
/*     */       throws ParseException
/*     */     {
/* 411 */       return new OrderTimeLimitationPanel.TimeString(OrderTimeLimitationPanel.this, OrderTimeLimitationPanel.this.sdf.parse(text));
/*     */     }
/*     */ 
/*     */     public String valueToString(Object value) throws ParseException {
/* 415 */       return OrderTimeLimitationPanel.this.sdf.format(Long.valueOf(((OrderTimeLimitationPanel.TimeString)value).getTime()));
/*     */     }
/*     */   }
/*     */ 
/*     */   private class TimeVerifier extends InputVerifier
/*     */   {
/*     */     private TimeVerifier()
/*     */     {
/*     */     }
/*     */ 
/*     */     public boolean verify(JComponent input)
/*     */     {
/* 389 */       if ((input instanceof JFormattedTextField)) {
/* 390 */         String text = ((JFormattedTextField)input).getText();
/* 391 */         JFormattedTextField.AbstractFormatter formatter = ((JFormattedTextField)input).getFormatter();
/*     */         try {
/* 393 */           formatter.stringToValue(text);
/* 394 */           return true;
/*     */         } catch (ParseException e) {
/* 396 */           return false;
/*     */         }
/*     */       }
/* 399 */       return true;
/*     */     }
/*     */ 
/*     */     public boolean shouldYieldFocus(JComponent input) {
/* 403 */       return verify(input);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class TimeString
/*     */   {
/*     */     private long time;
/*     */ 
/*     */     TimeString()
/*     */     {
/* 366 */       this.time = ((ApplicationClock)GreedContext.get("applicationClock")).getTime();
/*     */     }
/*     */ 
/*     */     TimeString(Date date) {
/* 370 */       this.time = date.getTime();
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 374 */       return OrderTimeLimitationPanel.this.sdf.format(new Date(this.time));
/*     */     }
/*     */ 
/*     */     long getTime() {
/* 378 */       return this.time;
/*     */     }
/*     */ 
/*     */     void setTime(long millis) {
/* 382 */       this.time = millis;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.components.OrderTimeLimitationPanel
 * JD-Core Version:    0.6.0
 */