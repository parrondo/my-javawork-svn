/*     */ package com.dukascopy.charts.dialogs.customrange;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.chartbuilder.IDataManager;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.utils.ChartsLocalizator;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.gui.component.JRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.toedter.calendar.IDateEditor;
/*     */ import com.toedter.calendar.JDateChooser;
/*     */ import com.toedter.calendar.JSpinnerDateEditor;
/*     */ import java.awt.Component;
/*     */ import java.awt.ComponentOrientation;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseWheelEvent;
/*     */ import java.awt.event.MouseWheelListener;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.swing.DefaultListCellRenderer;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFormattedTextField;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSlider;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.JSpinner.DateEditor;
/*     */ import javax.swing.JSpinner.DefaultEditor;
/*     */ import javax.swing.SpinnerDateModel;
/*     */ import javax.swing.SpinnerModel;
/*     */ import javax.swing.SpinnerNumberModel;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import javax.swing.text.Document;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class CustomRangeDialog extends JDialog
/*     */   implements ChangeListener
/*     */ {
/*  62 */   private static final Logger LOGGER = LoggerFactory.getLogger(CustomRangeDialog.class.getName());
/*     */   private static final int MIN_CANDLES_COUNT = 10;
/*     */   private static final int MAX_CANDLES_COUNT = 2000;
/*  67 */   private static final Image DIALOG_ICON = StratUtils.loadImage("rc/media/custom_range_btn.png");
/*  68 */   private static final Dimension DIALOG_SIZE = new Dimension(600, 300);
/*     */   private final CustomRange customRange;
/*     */   private final ActionListener actionListener;
/*     */   private final PeriodEditorPanel periodEditorPanel;
/*     */   private final TimeEditorPanel timeEditorPanel;
/*     */   private final CandlesCountEditorPanel candlesBeforeEditorPanel;
/*     */   private final CandlesCountEditorPanel candlesAfterEditorPanel;
/*     */   private final List<JForexPeriod> allowedPeriods;
/*     */ 
/*     */   public CustomRangeDialog(Component parent, CustomRange customRange, ActionListener actionListener, IDataManager dataManager, List<JForexPeriod> allowedPeriods)
/*     */   {
/*  86 */     this.customRange = customRange;
/*  87 */     this.actionListener = actionListener;
/*  88 */     this.allowedPeriods = allowedPeriods;
/*  89 */     if (LOGGER.isTraceEnabled()) {
/*  90 */       LOGGER.trace("Creating custom range dialog : " + customRange);
/*     */     }
/*     */ 
/*  93 */     setTitle(LocalizationManager.getText("cr.dialog.title.custom.range"));
/*  94 */     setIconImage(DIALOG_ICON);
/*     */ 
/*  96 */     this.periodEditorPanel = new PeriodEditorPanel(customRange, LocalizationManager.getText("period.panel.title.period"), LocalizationManager.getText("period.panel.choose.time.period"), this, this.allowedPeriods);
/*     */ 
/* 104 */     this.timeEditorPanel = new TimeEditorPanel(customRange, LocalizationManager.getText("time.panel.title.time.reference.point"), LocalizationManager.getText("time.panel.tooltip.define.time.reference.point"), this, dataManager);
/*     */ 
/* 112 */     this.candlesBeforeEditorPanel = new CandlesCountEditorPanel(customRange, LocalizationManager.getText("candles.before.panel.title.before.time.ref.point"), LocalizationManager.getText("candles.before.panel.tooltip.define.time.interval.before.time.ref.point"), CustomRangeDialog.CandlesCountEditorPanel.Type.BEFORE, 2000, this);
/*     */ 
/* 121 */     this.candlesAfterEditorPanel = new CandlesCountEditorPanel(customRange, LocalizationManager.getText("candles.after.panel.title.before.time.ref.point"), LocalizationManager.getText("candles.after.panel.tooltip.define.time.interval.before.time.ref.point"), CustomRangeDialog.CandlesCountEditorPanel.Type.AFTER, 2000, this);
/*     */ 
/* 130 */     this.timeEditorPanel.customRangeChanged();
/* 131 */     this.candlesBeforeEditorPanel.customRangeChanged();
/* 132 */     this.candlesAfterEditorPanel.customRangeChanged();
/*     */ 
/* 134 */     layoutComponents();
/*     */ 
/* 136 */     setSize(DIALOG_SIZE);
/* 137 */     setResizable(false);
/* 138 */     setModal(true);
/* 139 */     setLocationRelativeTo(parent);
/* 140 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   public void stateChanged(ChangeEvent e)
/*     */   {
/* 145 */     Object source = e.getSource();
/*     */ 
/* 147 */     if (source == this.periodEditorPanel) {
/* 148 */       this.timeEditorPanel.customRangeChanged();
/* 149 */       this.candlesBeforeEditorPanel.customRangeChanged();
/* 150 */       this.candlesAfterEditorPanel.customRangeChanged();
/*     */     }
/* 152 */     else if (source == this.candlesBeforeEditorPanel) {
/* 153 */       if (this.customRange.getTotal() > 2000) {
/* 154 */         this.customRange.setAfter(2000 - this.customRange.getBefore());
/* 155 */         this.candlesAfterEditorPanel.customRangeChanged();
/*     */       }
/* 157 */       else if (this.customRange.getTotal() < 10) {
/* 158 */         this.customRange.setBefore(10);
/* 159 */         this.candlesBeforeEditorPanel.customRangeChanged();
/*     */       }
/*     */     }
/* 162 */     else if (source == this.candlesAfterEditorPanel) {
/* 163 */       if (this.customRange.getTotal() > 2000) {
/* 164 */         this.customRange.setBefore(2000 - this.customRange.getAfter());
/* 165 */         this.candlesBeforeEditorPanel.customRangeChanged();
/*     */       }
/* 167 */       else if (this.customRange.getTotal() < 10) {
/* 168 */         this.customRange.setAfter(10);
/* 169 */         this.candlesAfterEditorPanel.customRangeChanged();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void layoutComponents() {
/* 175 */     setLayout(new GridBagLayout());
/* 176 */     setDefaultCloseOperation(2);
/*     */ 
/* 178 */     GridBagConstraints gbc = new GridBagConstraints();
/*     */ 
/* 181 */     gbc.gridy = 0;
/* 182 */     gbc.gridx = 0;
/* 183 */     gbc.weightx = 1.0D;
/* 184 */     gbc.gridwidth = 2;
/* 185 */     gbc.anchor = 10;
/* 186 */     gbc.fill = 2;
/* 187 */     add(this.periodEditorPanel, gbc);
/*     */ 
/* 190 */     gbc.gridy = 1;
/* 191 */     add(this.timeEditorPanel, gbc);
/*     */ 
/* 194 */     gbc.gridy = 2;
/* 195 */     gbc.weightx = 1.0D;
/* 196 */     gbc.fill = 2;
/* 197 */     gbc.gridwidth = 1;
/*     */ 
/* 199 */     gbc.gridx = 0;
/* 200 */     gbc.anchor = 13;
/* 201 */     add(this.candlesBeforeEditorPanel, gbc);
/*     */ 
/* 203 */     gbc.gridx = 1;
/* 204 */     gbc.anchor = 17;
/* 205 */     add(this.candlesAfterEditorPanel, gbc);
/*     */ 
/* 208 */     gbc.gridy = 3;
/* 209 */     gbc.gridwidth = 1;
/* 210 */     gbc.fill = 0;
/* 211 */     gbc.insets.set(10, 10, 0, 10);
/*     */ 
/* 213 */     gbc.gridx = 0;
/* 214 */     gbc.anchor = 13;
/* 215 */     add(new JButton(LocalizationManager.getText("button.display.title"))
/*     */     {
/*     */     }
/*     */     , gbc);
/*     */ 
/* 229 */     gbc.gridx = 1;
/* 230 */     gbc.anchor = 17;
/* 231 */     add(new JButton(LocalizationManager.getText("button.cancel.title"))
/*     */     {
/*     */     }
/*     */     , gbc);
/*     */   }
/*     */ 
/*     */   public static Period extractPeriod(JForexPeriod jForexPeriod)
/*     */   {
/* 753 */     Period p = null;
/* 754 */     if (DataType.TIME_PERIOD_AGGREGATION.equals(jForexPeriod.getDataType())) {
/* 755 */       p = jForexPeriod.getPeriod();
/*     */     }
/*     */     else {
/* 758 */       p = Period.TICK;
/*     */     }
/* 760 */     return p;
/*     */   }
/*     */ 
/*     */   private static final class CandlesCountEditorPanel extends CustomRangeDialog.CustomRangeEditorPanel
/*     */     implements CustomRangeDialog.CustomRangeChangeListener
/*     */   {
/*     */     private final Type type;
/*     */     final JSpinner spinner;
/*     */     final JSlider slider;
/*     */     final JLabel infoLabel;
/*     */ 
/*     */     public CandlesCountEditorPanel(CustomRange customRange, String title, String toolTipText, Type type, int max, ChangeListener changeListener)
/*     */     {
/* 601 */       super(title, toolTipText, changeListener);
/* 602 */       this.type = type;
/*     */ 
/* 604 */       int value = isBefore() ? customRange.getBefore() : customRange.getAfter();
/*     */ 
/* 606 */       this.spinner = new JSpinner(new SpinnerNumberModel(value, 0, max, 1))
/*     */       {
/*     */       };
/* 627 */       this.slider = new JSlider(0, 0, max, value)
/*     */       {
/*     */       };
/* 643 */       this.slider.addChangeListener(new ChangeListener(type)
/*     */       {
/*     */         public void stateChanged(ChangeEvent e) {
/* 646 */           int value = CustomRangeDialog.CandlesCountEditorPanel.this.slider.getValue();
/* 647 */           CustomRangeDialog.CandlesCountEditorPanel.this.spinner.setValue(Integer.valueOf(value));
/* 648 */           CustomRangeDialog.CandlesCountEditorPanel.this.setCandlesCount(value, this.val$type);
/*     */         }
/*     */       });
/* 651 */       this.spinner.addChangeListener(new ChangeListener(type)
/*     */       {
/*     */         public void stateChanged(ChangeEvent e) {
/* 654 */           Integer value = (Integer)CustomRangeDialog.CandlesCountEditorPanel.this.spinner.getValue();
/* 655 */           CustomRangeDialog.CandlesCountEditorPanel.this.slider.setValue(value.intValue());
/* 656 */           CustomRangeDialog.CandlesCountEditorPanel.this.setCandlesCount(value.intValue(), this.val$type);
/*     */         }
/*     */       });
/* 660 */       this.infoLabel = new JLabel();
/*     */ 
/* 662 */       GridBagConstraints gbc = new GridBagConstraints();
/*     */ 
/* 664 */       gbc.gridy = 0;
/* 665 */       gbc.gridx = 0;
/* 666 */       gbc.weightx = 1.0D;
/* 667 */       gbc.gridwidth = 2;
/* 668 */       gbc.anchor = 10;
/* 669 */       add(this.infoLabel, gbc);
/*     */ 
/* 671 */       gbc.gridy = 1;
/* 672 */       gbc.gridwidth = 1;
/* 673 */       gbc.gridx = (isBefore() ? 0 : 1);
/* 674 */       gbc.weighty = 1.0D;
/* 675 */       gbc.fill = 2;
/* 676 */       add(this.slider, gbc);
/*     */ 
/* 678 */       gbc.gridx = (isBefore() ? 1 : 0);
/* 679 */       gbc.weightx = 0.0D;
/* 680 */       gbc.fill = 3;
/* 681 */       add(this.spinner, gbc);
/*     */     }
/*     */ 
/*     */     public void customRangeChanged()
/*     */     {
/* 686 */       int value = isBefore() ? this.customRange.getBefore() : this.customRange.getAfter();
/* 687 */       this.spinner.setValue(Integer.valueOf(value));
/* 688 */       this.slider.setValue(value);
/* 689 */       updateInfo(value);
/*     */     }
/*     */ 
/*     */     private boolean isBefore() {
/* 693 */       return Type.BEFORE == this.type;
/*     */     }
/*     */ 
/*     */     private void updateInfo(int value) {
/* 697 */       this.infoLabel.setText(toString(this.customRange.getPeriod(), value));
/*     */     }
/*     */ 
/*     */     private void setCandlesCount(int value, Type type) {
/* 701 */       if (isBefore())
/* 702 */         this.customRange.setBefore(value);
/*     */       else {
/* 704 */         this.customRange.setAfter(value);
/*     */       }
/*     */ 
/* 707 */       updateInfo(value);
/* 708 */       this.changeListener.stateChanged(new ChangeEvent(this));
/*     */     }
/*     */ 
/*     */     private String toString(JForexPeriod period, int candlesCount) {
/* 712 */       Period p = CustomRangeDialog.extractPeriod(period);
/*     */ 
/* 714 */       if (p.isSmallerThan(Period.WEEKLY)) {
/* 715 */         long interval = candlesCount * (p == Period.TICK ? Period.ONE_SEC : p).getInterval();
/* 716 */         long days = TimeUnit.MILLISECONDS.toDays(interval);
/* 717 */         interval -= TimeUnit.DAYS.toMillis(days);
/* 718 */         long hours = TimeUnit.MILLISECONDS.toHours(interval);
/* 719 */         interval -= TimeUnit.HOURS.toMillis(hours);
/* 720 */         long minutes = TimeUnit.MILLISECONDS.toMinutes(interval);
/* 721 */         interval -= TimeUnit.MINUTES.toMillis(minutes);
/* 722 */         long seconds = TimeUnit.MILLISECONDS.toSeconds(interval);
/*     */ 
/* 724 */         StringBuilder result = new StringBuilder(" ");
/*     */ 
/* 726 */         if (days > 0L) {
/* 727 */           result.append(days).append(new StringBuilder().append(" ").append(LocalizationManager.getText("label.caption.days")).append(" ").toString());
/*     */         }
/* 729 */         if (hours > 0L) {
/* 730 */           result.append(hours).append(new StringBuilder().append(" ").append(LocalizationManager.getText("label.caption.hours")).append(" ").toString());
/*     */         }
/* 732 */         if (minutes > 0L) {
/* 733 */           result.append(minutes).append(new StringBuilder().append(" ").append(LocalizationManager.getText("label.caption.minutes")).append(" ").toString());
/*     */         }
/* 735 */         if (seconds > 0L) {
/* 736 */           result.append(seconds).append(new StringBuilder().append(" ").append(LocalizationManager.getText("label.caption.seconds")).toString());
/*     */         }
/*     */ 
/* 739 */         return result.toString();
/*     */       }
/* 741 */       if (p == Period.WEEKLY) {
/* 742 */         return new StringBuilder().append(candlesCount).append(" ").append(LocalizationManager.getText("label.caption.weeks")).toString();
/*     */       }
/* 744 */       if (p == Period.MONTHLY) {
/* 745 */         return new StringBuilder().append(candlesCount).append(" ").append(" ").append(LocalizationManager.getText("label.caption.months")).toString();
/*     */       }
/*     */ 
/* 748 */       return " ";
/*     */     }
/*     */ 
/*     */     public static enum Type
/*     */     {
/* 584 */       BEFORE, 
/* 585 */       AFTER;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class TimeEditorPanel extends CustomRangeDialog.CustomRangeEditorPanel
/*     */     implements CustomRangeDialog.CustomRangeChangeListener
/*     */   {
/* 344 */     private static final TimeZone GMT0_TIMEZONE = TimeZone.getTimeZone("GMT 0");
/*     */     private static final String DATE_FORMAT = "dd.MM.yyyy";
/*     */     private final IDataManager dataManager;
/*     */     private final JDateChooser dateChooser;
/*     */     private final JSpinner timeSpinner;
/*     */ 
/*     */     public TimeEditorPanel(CustomRange customRange, String title, String toolTipText, ChangeListener changeListener, IDataManager dataManager)
/*     */     {
/* 358 */       super(title, toolTipText, changeListener);
/* 359 */       this.dataManager = dataManager;
/*     */ 
/* 361 */       this.dateChooser = new JDateChooser(new Date(), "dd.MM.yyyy", new JSpinnerDateEditor()
/*     */       {
/*     */       })
/*     */       {
/*     */       };
/* 393 */       Calendar calendar = Calendar.getInstance(GMT0_TIMEZONE);
/* 394 */       calendar.setTimeInMillis(customRange.getTime());
/* 395 */       this.dateChooser.setCalendar(calendar);
/*     */ 
/* 397 */       this.timeSpinner = new JSpinner(new SpinnerDateModel())
/*     */       {
/*     */       };
/* 421 */       GridBagConstraints gbc = new GridBagConstraints();
/*     */ 
/* 423 */       gbc.gridx = 0;
/* 424 */       gbc.gridy = 0;
/* 425 */       gbc.fill = 3;
/* 426 */       gbc.anchor = 10;
/* 427 */       add(this.dateChooser, gbc);
/*     */ 
/* 429 */       gbc.insets.left = 10;
/* 430 */       gbc.gridx = 1;
/* 431 */       add(this.timeSpinner, gbc);
/*     */     }
/*     */ 
/*     */     public void customRangeChanged()
/*     */     {
/* 436 */       Calendar calendar = Calendar.getInstance(GMT0_TIMEZONE);
/* 437 */       calendar.setTimeInMillis(this.customRange.getTime());
/*     */ 
/* 440 */       Period period = CustomRangeDialog.extractPeriod(this.customRange.getPeriod());
/* 441 */       JSpinner.DateEditor editor = getEditor(period);
/*     */ 
/* 443 */       if (editor == null) {
/* 444 */         this.timeSpinner.setEnabled(false);
/*     */       } else {
/* 446 */         editor.getFormat().setTimeZone(GMT0_TIMEZONE);
/* 447 */         editor.getTextField().getDocument().addDocumentListener(new DocumentListener(editor) {
/*     */           public void removeUpdate(DocumentEvent e) {
/* 449 */             updateTime();
/*     */           }
/* 451 */           public void insertUpdate(DocumentEvent e) { updateTime(); } 
/*     */           public void changedUpdate(DocumentEvent e) {
/* 453 */             updateTime();
/*     */           }
/*     */           private void updateTime() {
/*     */             try {
/* 457 */               this.val$editor.commitEdit();
/*     */             } catch (Exception ex) {
/* 459 */               return;
/*     */             }
/*     */ 
/* 462 */             CustomRangeDialog.TimeEditorPanel.this.setTime();
/*     */           }
/*     */         });
/* 466 */         this.timeSpinner.setEditor(editor);
/*     */ 
/* 468 */         this.timeSpinner.setModel(new SpinnerDateModel(new Date(), null, null, getCalendarField(period))
/*     */         {
/*     */           public Object getNextValue()
/*     */           {
/* 481 */             Date nextDate = (Date)super.getNextValue();
/* 482 */             return CustomRangeDialog.TimeEditorPanel.this.getDate(CustomRangeDialog.TimeEditorPanel.this.customRange.getPeriod(), nextDate.getTime());
/*     */           }
/*     */ 
/*     */           public Object getPreviousValue()
/*     */           {
/* 487 */             Date previousDate = (Date)super.getPreviousValue();
/* 488 */             return CustomRangeDialog.TimeEditorPanel.this.getDate(CustomRangeDialog.TimeEditorPanel.this.customRange.getPeriod(), previousDate.getTime());
/*     */           }
/*     */         });
/* 493 */         this.timeSpinner.setValue(calendar.getTime());
/* 494 */         this.timeSpinner.setEnabled(true);
/*     */       }
/*     */ 
/* 498 */       long minimalTime = this.dataManager.getMinimalTime(CustomRangeDialog.extractPeriod(this.customRange.getPeriod()));
/* 499 */       calendar.setTimeInMillis(minimalTime);
/* 500 */       Date minimalDate = calendar.getTime();
/*     */ 
/* 502 */       this.dateChooser.setMinSelectableDate(minimalDate);
/*     */ 
/* 504 */       if (getDate() < minimalTime)
/* 505 */         this.dateChooser.setDate(minimalDate);
/*     */     }
/*     */ 
/*     */     private Date getDate(JForexPeriod period, long time)
/*     */     {
/* 510 */       Calendar calendar = Calendar.getInstance(GMT0_TIMEZONE);
/* 511 */       Period p = CustomRangeDialog.extractPeriod(period);
/*     */ 
/* 513 */       if (!Period.TICK.equals(p)) {
/* 514 */         time = DataCacheUtils.getCandleStartFast(p, time);
/*     */       }
/*     */ 
/* 517 */       calendar.setTimeInMillis(time);
/*     */ 
/* 519 */       return calendar.getTime();
/*     */     }
/*     */ 
/*     */     private long getDate() {
/* 523 */       Calendar calendar = this.dateChooser.getCalendar();
/*     */ 
/* 525 */       calendar.set(11, 0);
/* 526 */       calendar.set(12, 0);
/* 527 */       calendar.set(13, 0);
/* 528 */       calendar.set(14, 0);
/*     */ 
/* 530 */       return calendar.getTimeInMillis();
/*     */     }
/*     */ 
/*     */     private long getTime() {
/* 534 */       Calendar time = Calendar.getInstance(GMT0_TIMEZONE);
/* 535 */       time.setTimeInMillis(((Date)this.timeSpinner.getValue()).getTime());
/*     */ 
/* 537 */       Calendar date = Calendar.getInstance(GMT0_TIMEZONE);
/* 538 */       date.setTimeInMillis(getDate() + TimeZone.getDefault().getOffset(getDate()));
/* 539 */       date.add(11, time.get(11));
/* 540 */       date.add(12, time.get(12));
/* 541 */       date.add(13, time.get(13));
/*     */ 
/* 543 */       return date.getTimeInMillis();
/*     */     }
/*     */ 
/*     */     private void setTime() {
/* 547 */       if ((this.dateChooser != null) && (this.timeSpinner != null)) {
/* 548 */         this.customRange.setTime(getTime());
/* 549 */         this.changeListener.stateChanged(new ChangeEvent(this));
/*     */       }
/*     */     }
/*     */ 
/*     */     private JSpinner.DateEditor getEditor(Period period) {
/* 554 */       if (period.isSmallerThan(Period.ONE_MIN)) {
/* 555 */         return new JSpinner.DateEditor(this.timeSpinner, "HH:mm:ss");
/*     */       }
/* 557 */       if (period.isSmallerThan(Period.ONE_HOUR)) {
/* 558 */         return new JSpinner.DateEditor(this.timeSpinner, "HH:mm:00");
/*     */       }
/* 560 */       if (period.isSmallerThan(Period.DAILY)) {
/* 561 */         return new JSpinner.DateEditor(this.timeSpinner, "HH:00:00");
/*     */       }
/*     */ 
/* 564 */       return null;
/*     */     }
/*     */ 
/*     */     private int getCalendarField(Period period) {
/* 568 */       if (period.isSmallerThan(Period.ONE_MIN)) {
/* 569 */         return 13;
/*     */       }
/* 571 */       if (period.isSmallerThan(Period.ONE_HOUR)) {
/* 572 */         return 12;
/*     */       }
/* 574 */       if (period.isSmallerThan(Period.DAILY)) {
/* 575 */         return 11;
/*     */       }
/*     */ 
/* 578 */       return 5;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class PeriodEditorPanel extends CustomRangeDialog.CustomRangeEditorPanel
/*     */   {
/*     */     private JComboBox cmbPeriods;
/*     */     private final List<JForexPeriod> allowedPeriods;
/*     */ 
/*     */     public PeriodEditorPanel(CustomRange customRange, String title, String toolTipText, ChangeListener changeListener, List<JForexPeriod> allowedPeriods)
/*     */     {
/* 283 */       super(title, toolTipText, changeListener);
/*     */ 
/* 285 */       this.allowedPeriods = allowedPeriods;
/*     */ 
/* 287 */       getCmbPeriods().setSelectedItem(customRange.getPeriod());
/*     */ 
/* 289 */       GridBagConstraints gbc = new GridBagConstraints();
/* 290 */       gbc.gridy = 0;
/* 291 */       gbc.gridx = 0;
/* 292 */       gbc.anchor = 10;
/* 293 */       gbc.fill = 2;
/* 294 */       gbc.weightx = 1.0D;
/* 295 */       add(getCmbPeriods(), gbc);
/*     */     }
/*     */ 
/*     */     protected JComboBox getCmbPeriods() {
/* 299 */       if (this.cmbPeriods == null) {
/* 300 */         this.cmbPeriods = new JComboBox();
/*     */ 
/* 302 */         for (JForexPeriod period : this.allowedPeriods) {
/* 303 */           this.cmbPeriods.addItem(period);
/*     */         }
/*     */ 
/* 306 */         this.cmbPeriods.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 309 */             JForexPeriod period = (JForexPeriod)CustomRangeDialog.PeriodEditorPanel.this.cmbPeriods.getSelectedItem();
/* 310 */             CustomRangeDialog.PeriodEditorPanel.this.customRange.setPeriod(period);
/* 311 */             String translatedPeriod = ChartsLocalizator.localize(period);
/* 312 */             CustomRangeDialog.PeriodEditorPanel.this.border.setHeaderText(CustomRangeDialog.PeriodEditorPanel.this.title + " : " + translatedPeriod);
/*     */ 
/* 314 */             CustomRangeDialog.PeriodEditorPanel.this.changeListener.stateChanged(new ChangeEvent(this));
/*     */ 
/* 316 */             CustomRangeDialog.PeriodEditorPanel.this.repaint();
/*     */           }
/*     */         });
/* 320 */         this.cmbPeriods.setRenderer(new DefaultListCellRenderer() {
/*     */           public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 322 */             Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/*     */ 
/* 324 */             if ((value instanceof JForexPeriod)) {
/* 325 */               JForexPeriod dtpw = (JForexPeriod)value;
/* 326 */               JLabel label = new JLabel();
/* 327 */               label.setText(ChartsLocalizator.localize(dtpw));
/* 328 */               label.setOpaque(true);
/* 329 */               label.setForeground(comp.getForeground());
/* 330 */               label.setBackground(comp.getBackground());
/* 331 */               return label;
/*     */             }
/* 333 */             return comp;
/*     */           }
/*     */         });
/*     */       }
/* 338 */       return this.cmbPeriods;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract class CustomRangeEditorPanel extends JPanel
/*     */   {
/*     */     protected final CustomRange customRange;
/*     */     protected final String title;
/*     */     protected final ChangeListener changeListener;
/*     */     protected final JRoundedBorder border;
/*     */ 
/*     */     protected CustomRangeEditorPanel(CustomRange customRange, String title, String toolTipText, ChangeListener changeListener)
/*     */     {
/* 257 */       this.customRange = customRange;
/* 258 */       this.title = title;
/* 259 */       this.changeListener = changeListener;
/*     */ 
/* 261 */       setLayout(new GridBagLayout());
/*     */ 
/* 263 */       this.border = new JRoundedBorder(this, title);
/* 264 */       setBorder(this.border);
/*     */ 
/* 266 */       setToolTipText(toolTipText);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract interface CustomRangeChangeListener
/*     */   {
/*     */     public abstract void customRangeChanged();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.customrange.CustomRangeDialog
 * JD-Core Version:    0.6.0
 */