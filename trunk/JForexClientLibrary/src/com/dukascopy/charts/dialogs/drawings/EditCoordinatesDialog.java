/*     */ package com.dukascopy.charts.dialogs.drawings;
/*     */ 
/*     */ import com.dukascopy.charts.dialogs.AbsoluteLayout;
/*     */ import com.dukascopy.charts.dialogs.AbsoluteLayoutConstraints;
/*     */ import com.dukascopy.charts.drawings.ChartObject;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.util.SpringUtilities;
/*     */ import com.toedter.calendar.IDateEditor;
/*     */ import com.toedter.calendar.JDateChooser;
/*     */ import com.toedter.calendar.JSpinnerDateEditor;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Frame;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseWheelEvent;
/*     */ import java.awt.event.MouseWheelListener;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFormattedTextField;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRootPane;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.JSpinner.DateEditor;
/*     */ import javax.swing.JSpinner.NumberEditor;
/*     */ import javax.swing.SpinnerDateModel;
/*     */ import javax.swing.SpinnerModel;
/*     */ import javax.swing.SpinnerNumberModel;
/*     */ import javax.swing.SpringLayout;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ 
/*     */ public class EditCoordinatesDialog extends JDialog
/*     */   implements ActionListener
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  48 */   private static final TimeZone GMT0_TIMEZONE = TimeZone.getTimeZone("GMT 0");
/*     */   private static final String DATE_FORMAT = "dd.MM.yyyy";
/*     */   private static final String TIME_FORMAT = "HH:mm:ss";
/*     */   private JButton okButton;
/*     */   private JButton cancelButton;
/*  55 */   private List<JSpinner> valueFields = new ArrayList(5);
/*  56 */   private List<JSpinner> timeFields = new ArrayList(5);
/*  57 */   private List<JDateChooser> dateFields = new ArrayList(5);
/*     */   private ChartObject chartObject;
/*     */   private int pipScale;
/*     */   private double pipValue;
/*     */ 
/*     */   public EditCoordinatesDialog(Frame parent, ChartObject chartObject, int pipScale, double pipValue)
/*     */   {
/*  70 */     super(parent, LocalizationManager.getText("menu.item.edit.coordinates"), true);
/*     */ 
/*  72 */     this.chartObject = chartObject;
/*  73 */     this.pipScale = pipScale;
/*  74 */     this.pipValue = pipValue;
/*     */ 
/*  76 */     setLocationRelativeTo(parent);
/*  77 */     setModal(true);
/*     */ 
/*  79 */     init();
/*     */   }
/*     */ 
/*     */   private void init() {
/*  83 */     JPanel fieldsPanel = new JPanel(new SpringLayout());
/*     */ 
/*  85 */     fieldsPanel.add(new JLabel());
/*  86 */     fieldsPanel.add(new JLabel("Date/Time"));
/*  87 */     fieldsPanel.add(new JLabel("Price"));
/*     */ 
/*  89 */     for (int i = 0; i < this.chartObject.getPointsCount(); i++)
/*     */     {
/*  91 */       JSpinner priceSpinner = createValueField(this.chartObject.getPrice(i));
/*  92 */       if (!this.chartObject.hasPriceValue()) {
/*  93 */         priceSpinner.setEnabled(false);
/*     */       }
/*  95 */       this.valueFields.add(priceSpinner);
/*     */ 
/*  97 */       JPanel dateTimeEditorPanel = createDateTimeEditorPanel(i);
/*     */ 
/*  99 */       fieldsPanel.add(new JLabel(String.valueOf(i + 1) + "."));
/* 100 */       fieldsPanel.add(dateTimeEditorPanel);
/* 101 */       fieldsPanel.add(priceSpinner);
/*     */     }
/*     */ 
/* 104 */     SpringUtilities.makeCompactGrid(fieldsPanel, this.chartObject.getPointsCount() + 1, 3, 2, 2, 25, 5);
/*     */ 
/* 106 */     this.okButton = new JButton("OK");
/* 107 */     this.okButton.setActionCommand("OK");
/* 108 */     this.okButton.addActionListener(this);
/*     */ 
/* 110 */     getRootPane().setDefaultButton(this.okButton);
/* 111 */     this.okButton.setFocusCycleRoot(true);
/*     */ 
/* 113 */     this.cancelButton = new JButton("Cancel");
/* 114 */     this.cancelButton.setActionCommand("Cancel");
/* 115 */     this.cancelButton.addActionListener(this);
/*     */ 
/* 117 */     JPanel buttonPanel = new JPanel(new AbsoluteLayout());
/* 118 */     buttonPanel.add(this.okButton, new AbsoluteLayoutConstraints(60, 5, 90, 25));
/* 119 */     buttonPanel.add(this.cancelButton, new AbsoluteLayoutConstraints(180, 5, 90, 25));
/*     */ 
/* 121 */     JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
/* 122 */     mainPanel.add(fieldsPanel, "Center");
/* 123 */     mainPanel.add(buttonPanel, "South");
/* 124 */     mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*     */ 
/* 126 */     setContentPane(mainPanel);
/* 127 */     setSize(350, ((SpringLayout)fieldsPanel.getLayout()).minimumLayoutSize(fieldsPanel).height + 75);
/* 128 */     setResizable(false);
/* 129 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   private JPanel createDateTimeEditorPanel(int index) {
/* 133 */     JPanel panel = new JPanel(new BorderLayout());
/*     */ 
/* 135 */     long defaultTime = this.chartObject.getTime(index);
/*     */ 
/* 137 */     JDateChooser dateChooser = new JDateChooser(null, "dd.MM.yyyy", new JSpinnerDateEditor()
/*     */     {
/*     */       private static final long serialVersionUID = 1L;
/*     */     })
/*     */     {
/*     */       private static final long serialVersionUID = 1L;
/*     */     };
/* 176 */     Calendar calendar = Calendar.getInstance(GMT0_TIMEZONE);
/* 177 */     calendar.setTimeInMillis(defaultTime);
/* 178 */     dateChooser.setCalendar(calendar);
/*     */ 
/* 181 */     SpinnerDateModel timeSpinnerModel = new SpinnerDateModel(calendar.getTime(), null, null, 13) {
/*     */       private static final long serialVersionUID = 1L;
/*     */ 
/*     */       public Object getNextValue() {
/* 186 */         Date value = (Date)getValue();
/* 187 */         Calendar cal = Calendar.getInstance(EditCoordinatesDialog.GMT0_TIMEZONE);
/* 188 */         cal.setTimeInMillis(value.getTime());
/* 189 */         cal.add(13, 1);
/* 190 */         Date next = cal.getTime();
/* 191 */         return next;
/*     */       }
/*     */ 
/*     */       public Object getPreviousValue()
/*     */       {
/* 197 */         Date value = (Date)getValue();
/* 198 */         Calendar cal = Calendar.getInstance(EditCoordinatesDialog.GMT0_TIMEZONE);
/* 199 */         cal.setTimeInMillis(value.getTime());
/* 200 */         cal.add(13, -1);
/* 201 */         Date prev = cal.getTime();
/* 202 */         return prev;
/*     */       }
/*     */     };
/* 207 */     JSpinner timeSpinner = new JSpinner(timeSpinnerModel)
/*     */     {
/*     */       private static final long serialVersionUID = 1L;
/*     */     };
/* 234 */     JSpinner.DateEditor editor = new JSpinner.DateEditor(timeSpinner, "HH:mm:ss");
/* 235 */     editor.getFormat().setTimeZone(GMT0_TIMEZONE);
/* 236 */     editor.getTextField().setValue(calendar.getTime());
/* 237 */     timeSpinner.setEditor(editor);
/*     */ 
/* 239 */     this.dateFields.add(dateChooser);
/* 240 */     this.timeFields.add(timeSpinner);
/*     */ 
/* 242 */     if (!this.chartObject.hasTimeValue()) {
/* 243 */       dateChooser.setEnabled(false);
/* 244 */       timeSpinner.setEnabled(false);
/*     */     }
/*     */ 
/* 247 */     panel.add(dateChooser, "West");
/* 248 */     panel.add(timeSpinner, "Center");
/*     */ 
/* 250 */     return panel;
/*     */   }
/*     */ 
/*     */   private JSpinner createValueField(double defaultValue) {
/* 254 */     JSpinner valueField = new JSpinner(new SpinnerNumberModel(defaultValue, -10000.0D, 10000.0D, this.pipValue * 0.1D));
/* 255 */     valueField.setPreferredSize(new Dimension(100, 20));
/* 256 */     valueField.setMaximumSize(new Dimension(2147483647, 20));
/*     */ 
/* 258 */     JSpinner.NumberEditor editor = new JSpinner.NumberEditor(valueField);
/*     */ 
/* 260 */     DecimalFormat format = editor.getFormat();
/* 261 */     format.setMaximumFractionDigits(this.pipScale + 1);
/* 262 */     format.setMinimumFractionDigits(this.pipScale + 1);
/* 263 */     editor.getTextField().setValue(Double.valueOf(defaultValue));
/*     */ 
/* 265 */     valueField.setEditor(editor);
/*     */ 
/* 268 */     return valueField;
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/* 274 */     if ("OK".equals(e.getActionCommand())) {
/* 275 */       dispose();
/* 276 */       setValues();
/* 277 */     } else if ("Cancel".equals(e.getActionCommand())) {
/* 278 */       dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setValues() {
/* 283 */     for (int index = 0; index < this.chartObject.getPointsCount(); index++) {
/* 284 */       if (this.chartObject.hasPriceValue()) {
/* 285 */         this.chartObject.setPrice(index, ((Double)((JSpinner)this.valueFields.get(index)).getValue()).doubleValue());
/*     */       }
/* 287 */       if (this.chartObject.hasTimeValue()) {
/* 288 */         Calendar resultCal = Calendar.getInstance(GMT0_TIMEZONE);
/*     */ 
/* 290 */         Date date = ((JDateChooser)this.dateFields.get(index)).getDate();
/* 291 */         Calendar cal = Calendar.getInstance(GMT0_TIMEZONE);
/* 292 */         cal.setTimeInMillis(date.getTime());
/* 293 */         resultCal.set(cal.get(1), cal.get(2), cal.get(5));
/*     */ 
/* 295 */         Date time = (Date)((JSpinner)this.timeFields.get(index)).getValue();
/* 296 */         cal.setTimeInMillis(time.getTime());
/* 297 */         resultCal.set(11, cal.get(11));
/* 298 */         resultCal.set(12, cal.get(12));
/* 299 */         resultCal.set(13, cal.get(13));
/*     */ 
/* 301 */         this.chartObject.setTime(index, resultCal.getTimeInMillis());
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.drawings.EditCoordinatesDialog
 * JD-Core Version:    0.6.0
 */