/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import com.toedter.calendar.JDateChooser;
/*     */ import com.toedter.calendar.JSpinnerDateEditor;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.SwingUtilities;
/*     */ 
/*     */ public class RangeSelectionDialog extends JDialog
/*     */ {
/*     */   private boolean modalResult;
/*     */   private Date dateFrom;
/*     */   private Date dateTo;
/*     */   private JDateChooser dateChooserFrom;
/*     */   private JDateChooser dateChooserTo;
/*     */ 
/*     */   public static RangeSelectionDialog createDialog(Component parent, String titleKey, Long dateFrom, Long dateTo)
/*     */   {
/*  45 */     Window window = SwingUtilities.getWindowAncestor(parent);
/*     */     RangeSelectionDialog dialog;
/*     */     RangeSelectionDialog dialog;
/*  46 */     if (window == null)
/*  47 */       dialog = new RangeSelectionDialog();
/*     */     else {
/*  49 */       dialog = new RangeSelectionDialog(window);
/*     */     }
/*  51 */     dialog.setTitle(LocalizationManager.getText(titleKey));
/*  52 */     dialog.setDateFrom(dateFrom);
/*  53 */     dialog.setDateTo(dateTo);
/*  54 */     dialog.setDefaultCloseOperation(2);
/*  55 */     dialog.pack();
/*  56 */     dialog.setLocationRelativeTo(window);
/*  57 */     return dialog;
/*     */   }
/*     */ 
/*     */   private RangeSelectionDialog()
/*     */   {
/*  69 */     initUI();
/*     */   }
/*     */ 
/*     */   private RangeSelectionDialog(Window owner) {
/*  73 */     super(owner);
/*  74 */     initUI();
/*     */   }
/*     */ 
/*     */   private void initUI()
/*     */   {
/*  79 */     JLocalizableLabel labelFrom = new JLocalizableLabel("label.from");
/*  80 */     this.dateChooserFrom = new JDateChooser(null, null, "yyyy.MM.dd", new JSpinnerDateEditor());
/*     */ 
/*  83 */     JLocalizableLabel labelTo = new JLocalizableLabel("label.to");
/*  84 */     this.dateChooserTo = new JDateChooser(null, null, "yyyy.MM.dd", new JSpinnerDateEditor());
/*     */ 
/*  87 */     JLocalizableButton buttonOk = new JLocalizableButton("button.ok");
/*  88 */     buttonOk.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  91 */         if (RangeSelectionDialog.this.commit()) {
/*  92 */           RangeSelectionDialog.access$102(RangeSelectionDialog.this, true);
/*  93 */           RangeSelectionDialog.this.dispose();
/*     */         }
/*     */       }
/*     */     });
/*  97 */     JLocalizableButton buttonCancel = new JLocalizableButton("button.cancel");
/*  98 */     buttonCancel.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 101 */         RangeSelectionDialog.access$102(RangeSelectionDialog.this, false);
/* 102 */         RangeSelectionDialog.this.dispose();
/*     */       }
/*     */     });
/* 106 */     JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
/* 107 */     buttonPanel.add(buttonOk);
/* 108 */     buttonPanel.add(buttonCancel);
/*     */ 
/* 110 */     JPanel pnlMain = new JPanel(new GridBagLayout());
/* 111 */     pnlMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
/* 112 */     GridBagConstraints gbc = new GridBagConstraints();
/* 113 */     gbc.anchor = 17;
/* 114 */     gbc.fill = 0;
/* 115 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 0, 0, 0, 0, gbc, pnlMain, labelFrom);
/* 116 */     GridBagLayoutHelper.add(2, 0, 0.0D, 0.0D, 1, 1, 10, 0, 0, 0, gbc, pnlMain, labelTo);
/*     */ 
/* 118 */     gbc.fill = 2;
/* 119 */     GridBagLayoutHelper.add(1, 0, 1.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, pnlMain, this.dateChooserFrom);
/* 120 */     GridBagLayoutHelper.add(3, 0, 1.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, pnlMain, this.dateChooserTo);
/*     */ 
/* 122 */     gbc.anchor = 14;
/* 123 */     gbc.fill = 0;
/* 124 */     GridBagLayoutHelper.add(0, 4, 0.0D, 1.0D, 10, 1, 5, 10, 0, 0, gbc, pnlMain, buttonPanel);
/* 125 */     getContentPane().add(pnlMain);
/*     */   }
/*     */ 
/*     */   public void setDateFrom(Long dateTimeFrom) {
/* 129 */     this.dateChooserFrom.setDate(new Date(dateTimeFrom.longValue()));
/*     */   }
/*     */ 
/*     */   public void setDateTo(Long dateTimeTo) {
/* 133 */     if (dateTimeTo == null)
/* 134 */       this.dateChooserTo.setDate(new Date());
/*     */     else
/* 136 */       this.dateChooserTo.setDate(new Date(dateTimeTo.longValue()));
/*     */   }
/*     */ 
/*     */   public boolean showModal()
/*     */   {
/* 141 */     setModal(true);
/* 142 */     this.modalResult = false;
/* 143 */     setMinimumSize(getPreferredSize());
/* 144 */     setVisible(true);
/* 145 */     return this.modalResult;
/*     */   }
/*     */ 
/*     */   private boolean commit() {
/* 149 */     Date from = this.dateChooserFrom.getDate();
/* 150 */     Date to = this.dateChooserTo.getDate();
/* 151 */     if ((from == null) || (to == null) || (from.getTime() == -9223372036854775808L) || (to.getTime() == -9223372036854775808L)) {
/* 152 */       JOptionPane.showMessageDialog(this, LocalizationManager.getText("joption.pane.incorrect.from.to.date"), LocalizationManager.getText("joption.pane.wrong"), 1);
/* 153 */       return false;
/*     */     }
/*     */ 
/* 156 */     Calendar lCal = Calendar.getInstance();
/* 157 */     lCal.setTime(from);
/* 158 */     Calendar gmtCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 159 */     gmtCalendar.set(14, 0);
/* 160 */     gmtCalendar.set(lCal.get(1), lCal.get(2), lCal.get(5), 0, 0, 0);
/* 161 */     from = gmtCalendar.getTime();
/* 162 */     lCal.setTime(to);
/* 163 */     gmtCalendar.set(lCal.get(1), lCal.get(2), lCal.get(5), 0, 0, 0);
/* 164 */     to = gmtCalendar.getTime();
/*     */ 
/* 166 */     if (!from.before(to)) {
/* 167 */       JOptionPane.showMessageDialog(this, LocalizationManager.getText("joption.pane.end.day.should.be.after.start.day"), LocalizationManager.getText("joption.pane.wrong"), 1);
/* 168 */       return false;
/*     */     }
/*     */ 
/* 171 */     this.dateFrom = from;
/* 172 */     this.dateTo = to;
/* 173 */     return true;
/*     */   }
/*     */ 
/*     */   public Date getDateFrom() {
/* 177 */     return this.dateFrom;
/*     */   }
/*     */ 
/*     */   public Date getDateTo() {
/* 181 */     return this.dateTo;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.RangeSelectionDialog
 * JD-Core Version:    0.6.0
 */