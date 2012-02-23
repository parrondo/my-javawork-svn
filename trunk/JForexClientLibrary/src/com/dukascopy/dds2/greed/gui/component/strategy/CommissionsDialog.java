/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.api.system.Commissions;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.JRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Desktop;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.SortedMap;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRadioButton;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.SpinnerNumberModel;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class CommissionsDialog extends JDialog
/*     */ {
/*  55 */   private static final Logger LOGGER = LoggerFactory.getLogger(CommissionsDialog.class);
/*     */ 
/*  57 */   private static final double[] DEPOSIT_LIMITS = { 0.0D, 5000.0D, 10000.0D, 25000.0D, 50000.0D, 250000.0D, 500000.0D, 1000000.0D, 5000000.0D, 10000000.0D };
/*     */ 
/*  70 */   private static final double[] EQUITY_LIMITS = { 0.0D, 5000.0D, 10000.0D, 25000.0D, 50000.0D, 250000.0D, 500000.0D, 1000000.0D, 5000000.0D, 10000000.0D };
/*     */ 
/*  83 */   private static final double[] TURNOVER_LIMITS = { 5000000.0D, 10000000.0D, 25000000.0D, 50000000.0D, 250000000.0D, 500000000.0D, 1000000000.0D, 2000000000.0D, 4000000000.0D };
/*     */   private JPanel mainPanel;
/*  96 */   private JSpinner[] depositSpinners = new JSpinner[10];
/*  97 */   private JSpinner[] equitySpinners = new JSpinner[10];
/*  98 */   private JSpinner[] turnoverSpinners = new JSpinner[9];
/*     */   private JRadioButton rbnGuaranteeDeposit;
/*     */   private JRadioButton rbnDukascopyDeposit;
/*     */   private JLocalizableButton resetButton;
/*     */   private JLocalizableButton okButton;
/*     */   private JLocalizableButton cancelButton;
/*     */   private Commissions commitedCommissions;
/*     */   private Commissions dukasCommissions;
/*     */   private Commissions guaranteeCommissions;
/*     */ 
/*     */   public CommissionsDialog(Component parent, Commissions commissions)
/*     */   {
/* 112 */     super(SwingUtilities.getWindowAncestor(parent));
/* 113 */     setModal(true);
/* 114 */     setDefaultCloseOperation(2);
/*     */ 
/* 116 */     if (commissions.isCustodianBankOrGuarantee()) {
/* 117 */       this.dukasCommissions = new Commissions(false);
/* 118 */       this.guaranteeCommissions = create(true, commissions);
/*     */     } else {
/* 120 */       this.dukasCommissions = create(false, commissions);
/* 121 */       this.guaranteeCommissions = new Commissions(true);
/*     */     }
/*     */ 
/* 124 */     build(commissions.isCustodianBankOrGuarantee());
/* 125 */     fillSpinners(commissions);
/* 126 */     addSpinnerListeners();
/*     */ 
/* 128 */     setContentPane(this.mainPanel);
/* 129 */     setTitle(LocalizationManager.getText("title.commissions"));
/* 130 */     pack();
/* 131 */     setSize(540, 400);
/* 132 */     setLocationRelativeTo((JFrame)GreedContext.get("clientGui"));
/* 133 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   private Commissions currentCommissions() {
/* 137 */     if (this.rbnGuaranteeDeposit.isSelected()) {
/* 138 */       return this.guaranteeCommissions;
/*     */     }
/* 140 */     return this.dukasCommissions;
/*     */   }
/*     */ 
/*     */   private Commissions create(boolean custodianBankOrGuarantee, Commissions data)
/*     */   {
/* 145 */     Commissions result = new Commissions(custodianBankOrGuarantee);
/* 146 */     result.getDepositLimits().putAll(data.getDepositLimits());
/* 147 */     result.getEquityLimits().putAll(data.getEquityLimits());
/* 148 */     result.getTurnoverLimits().putAll(data.getTurnoverLimits());
/* 149 */     result.setMaxCommission(data.getMaxCommission());
/*     */ 
/* 151 */     double[] last30days = data.getLast30DaysTurnoverAtStart();
/* 152 */     double[] newLast30days = new double[last30days.length];
/* 153 */     System.arraycopy(last30days, 0, newLast30days, 0, last30days.length);
/* 154 */     result.setLast30DaysTurnoverAtStart(newLast30days);
/* 155 */     return result;
/*     */   }
/*     */ 
/*     */   private void build(boolean isCustodianBankOrGuarantee) {
/* 159 */     this.mainPanel = new JPanel(new GridBagLayout());
/*     */ 
/* 162 */     JPanel depositPanel = new JPanel(new GridBagLayout());
/* 163 */     JRoundedBorder border = new JLocalizableRoundedBorder(depositPanel, "border.deposit.commissions");
/* 164 */     depositPanel.setBorder(border);
/*     */ 
/* 166 */     GridBagConstraints gbc = new GridBagConstraints();
/* 167 */     gbc.fill = 1;
/* 168 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, new JLabel("< 5 000"));
/* 169 */     this.depositSpinners[0] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 170 */     GridBagLayoutHelper.add(1, 0, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, this.depositSpinners[0]);
/* 171 */     GridBagLayoutHelper.add(0, 1, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, new JLabel("≥ 5 000"));
/* 172 */     this.depositSpinners[1] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 173 */     GridBagLayoutHelper.add(1, 1, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, this.depositSpinners[1]);
/* 174 */     GridBagLayoutHelper.add(0, 2, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, new JLabel("≥ 10 000"));
/* 175 */     this.depositSpinners[2] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 176 */     GridBagLayoutHelper.add(1, 2, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, this.depositSpinners[2]);
/* 177 */     GridBagLayoutHelper.add(0, 3, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, new JLabel("≥ 25 000"));
/* 178 */     this.depositSpinners[3] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 179 */     GridBagLayoutHelper.add(1, 3, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, this.depositSpinners[3]);
/* 180 */     GridBagLayoutHelper.add(0, 4, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, new JLabel("≥ 50 000"));
/* 181 */     this.depositSpinners[4] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 182 */     GridBagLayoutHelper.add(1, 4, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, this.depositSpinners[4]);
/* 183 */     GridBagLayoutHelper.add(0, 5, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, new JLabel("≥ 250 000"));
/* 184 */     this.depositSpinners[5] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 185 */     GridBagLayoutHelper.add(1, 5, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, this.depositSpinners[5]);
/* 186 */     GridBagLayoutHelper.add(0, 6, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, new JLabel("≥ 500 000"));
/* 187 */     this.depositSpinners[6] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 188 */     GridBagLayoutHelper.add(1, 6, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, this.depositSpinners[6]);
/* 189 */     GridBagLayoutHelper.add(0, 7, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, new JLabel("≥ 1 000 000"));
/* 190 */     this.depositSpinners[7] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 191 */     GridBagLayoutHelper.add(1, 7, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, this.depositSpinners[7]);
/* 192 */     GridBagLayoutHelper.add(0, 8, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, new JLabel("≥ 5 000 000"));
/* 193 */     this.depositSpinners[8] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 194 */     GridBagLayoutHelper.add(1, 8, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, this.depositSpinners[8]);
/* 195 */     GridBagLayoutHelper.add(0, 9, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, new JLabel("≥ 10 000 000"));
/* 196 */     this.depositSpinners[9] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 197 */     GridBagLayoutHelper.add(1, 9, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, depositPanel, this.depositSpinners[9]);
/*     */ 
/* 199 */     JPanel equityPanel = new JPanel(new GridBagLayout());
/* 200 */     border = new JLocalizableRoundedBorder(equityPanel, "border.equity.commissions");
/* 201 */     equityPanel.setBorder(border);
/* 202 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, new JLabel("< 5 000"));
/* 203 */     this.equitySpinners[0] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 204 */     GridBagLayoutHelper.add(1, 0, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, this.equitySpinners[0]);
/* 205 */     GridBagLayoutHelper.add(0, 1, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, new JLabel("≥ 5 000"));
/* 206 */     this.equitySpinners[1] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 207 */     GridBagLayoutHelper.add(1, 1, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, this.equitySpinners[1]);
/* 208 */     GridBagLayoutHelper.add(0, 2, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, new JLabel("≥ 10 000"));
/* 209 */     this.equitySpinners[2] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 210 */     GridBagLayoutHelper.add(1, 2, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, this.equitySpinners[2]);
/* 211 */     GridBagLayoutHelper.add(0, 3, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, new JLabel("≥ 25 000"));
/* 212 */     this.equitySpinners[3] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 213 */     GridBagLayoutHelper.add(1, 3, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, this.equitySpinners[3]);
/* 214 */     GridBagLayoutHelper.add(0, 4, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, new JLabel("≥ 50 000"));
/* 215 */     this.equitySpinners[4] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 216 */     GridBagLayoutHelper.add(1, 4, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, this.equitySpinners[4]);
/* 217 */     GridBagLayoutHelper.add(0, 5, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, new JLabel("≥ 250 000"));
/* 218 */     this.equitySpinners[5] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 219 */     GridBagLayoutHelper.add(1, 5, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, this.equitySpinners[5]);
/* 220 */     GridBagLayoutHelper.add(0, 6, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, new JLabel("≥ 500 000"));
/* 221 */     this.equitySpinners[6] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 222 */     GridBagLayoutHelper.add(1, 6, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, this.equitySpinners[6]);
/* 223 */     GridBagLayoutHelper.add(0, 7, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, new JLabel("≥ 1 000 000"));
/* 224 */     this.equitySpinners[7] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 225 */     GridBagLayoutHelper.add(1, 7, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, this.equitySpinners[7]);
/* 226 */     GridBagLayoutHelper.add(0, 8, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, new JLabel("≥ 5 000 000"));
/* 227 */     this.equitySpinners[8] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 228 */     GridBagLayoutHelper.add(1, 8, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, this.equitySpinners[8]);
/* 229 */     GridBagLayoutHelper.add(0, 9, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, new JLabel("≥ 10 000 000"));
/* 230 */     this.equitySpinners[9] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 231 */     GridBagLayoutHelper.add(1, 9, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, equityPanel, this.equitySpinners[9]);
/*     */ 
/* 233 */     JPanel turnoverPanel = new JPanel(new GridBagLayout());
/* 234 */     border = new JLocalizableRoundedBorder(turnoverPanel, "border.turnover.commissions");
/* 235 */     turnoverPanel.setBorder(border);
/* 236 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, turnoverPanel, new JLabel("≥ 5 million"));
/* 237 */     this.turnoverSpinners[0] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 238 */     GridBagLayoutHelper.add(1, 0, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, turnoverPanel, this.turnoverSpinners[0]);
/* 239 */     GridBagLayoutHelper.add(0, 1, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, turnoverPanel, new JLabel("≥ 10 million"));
/* 240 */     this.turnoverSpinners[1] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 241 */     GridBagLayoutHelper.add(1, 1, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, turnoverPanel, this.turnoverSpinners[1]);
/* 242 */     GridBagLayoutHelper.add(0, 2, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, turnoverPanel, new JLabel("≥ 25 million"));
/* 243 */     this.turnoverSpinners[2] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 244 */     GridBagLayoutHelper.add(1, 2, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, turnoverPanel, this.turnoverSpinners[2]);
/* 245 */     GridBagLayoutHelper.add(0, 3, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, turnoverPanel, new JLabel("≥ 50 million"));
/* 246 */     this.turnoverSpinners[3] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 247 */     GridBagLayoutHelper.add(1, 3, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, turnoverPanel, this.turnoverSpinners[3]);
/* 248 */     GridBagLayoutHelper.add(0, 4, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, turnoverPanel, new JLabel("≥ 250 million"));
/* 249 */     this.turnoverSpinners[4] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 250 */     GridBagLayoutHelper.add(1, 4, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, turnoverPanel, this.turnoverSpinners[4]);
/* 251 */     GridBagLayoutHelper.add(0, 5, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, turnoverPanel, new JLabel("≥ 500 million"));
/* 252 */     this.turnoverSpinners[5] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 253 */     GridBagLayoutHelper.add(1, 5, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, turnoverPanel, this.turnoverSpinners[5]);
/* 254 */     GridBagLayoutHelper.add(0, 6, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, turnoverPanel, new JLabel("≥ 1 billion"));
/* 255 */     this.turnoverSpinners[6] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 256 */     GridBagLayoutHelper.add(1, 6, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, turnoverPanel, this.turnoverSpinners[6]);
/* 257 */     GridBagLayoutHelper.add(0, 7, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, turnoverPanel, new JLabel("≥ 2 billion"));
/* 258 */     this.turnoverSpinners[7] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 259 */     GridBagLayoutHelper.add(1, 7, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, turnoverPanel, this.turnoverSpinners[7]);
/* 260 */     GridBagLayoutHelper.add(0, 8, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, turnoverPanel, new JLabel("≥ 4 billion"));
/* 261 */     this.turnoverSpinners[8] = new JSpinner(new SpinnerNumberModel(1, 1, 2147483647, 1));
/* 262 */     GridBagLayoutHelper.add(1, 8, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, turnoverPanel, this.turnoverSpinners[8]);
/*     */ 
/* 264 */     JPanel buttonsPanel = new JPanel(new FlowLayout(2, 5, 5));
/* 265 */     this.resetButton = new JLocalizableButton("button.reset");
/* 266 */     this.okButton = new JLocalizableButton("button.ok");
/* 267 */     this.cancelButton = new JLocalizableButton("button.cancel");
/* 268 */     buttonsPanel.add(this.resetButton);
/* 269 */     buttonsPanel.add(this.okButton);
/* 270 */     buttonsPanel.add(this.cancelButton);
/*     */ 
/* 272 */     String labelText = "<html><u>" + LocalizationManager.getText("optimizer.label.description.commissions") + "</u></html>";
/* 273 */     JLabel lblDescription = new JLabel(labelText);
/* 274 */     lblDescription.setCursor(Cursor.getPredefinedCursor(12));
/* 275 */     lblDescription.addMouseListener(new MouseAdapter()
/*     */     {
/*     */       public void mouseReleased(MouseEvent e) {
/* 278 */         if (!e.isPopupTrigger())
/*     */           try {
/* 280 */             URI uri = new URI("http://www.dukascopy.com/swiss/english/forex/forex_trading_accounts/commission-policy/");
/*     */ 
/* 282 */             Desktop.getDesktop().browse(uri);
/*     */           } catch (URISyntaxException ex) {
/* 284 */             CommissionsDialog.LOGGER.error("Error opening Dukascopy web page.", ex);
/*     */           } catch (IOException ex) {
/* 286 */             CommissionsDialog.LOGGER.error("Error opening Dukascopy web page.", ex);
/*     */           }
/*     */       }
/*     */     });
/* 292 */     GridBagLayoutHelper.add(0, 0, 1.0D, 0.0D, 1, 1, 5, 5, 5, 0, gbc, this.mainPanel, depositPanel);
/* 293 */     GridBagLayoutHelper.add(1, 0, 1.0D, 0.0D, 1, 1, 5, 5, 5, 0, gbc, this.mainPanel, equityPanel);
/* 294 */     GridBagLayoutHelper.add(2, 0, 1.0D, 0.0D, 1, 1, 5, 5, 5, 0, gbc, this.mainPanel, turnoverPanel);
/*     */ 
/* 296 */     this.rbnDukascopyDeposit = new JRadioButton(LocalizationManager.getTextWithArguments("check.deposit.dukascopy", new Object[] { GuiUtilsAndConstants.LABEL_SHORT_NAME }));
/*     */ 
/* 298 */     this.rbnGuaranteeDeposit = new JRadioButton(LocalizationManager.getText("check.deposit.custodian.or.guarantee"));
/*     */ 
/* 300 */     ButtonGroup group = new ButtonGroup();
/* 301 */     group.add(this.rbnDukascopyDeposit);
/* 302 */     group.add(this.rbnGuaranteeDeposit);
/* 303 */     this.rbnDukascopyDeposit.setSelected(!isCustodianBankOrGuarantee);
/* 304 */     this.rbnGuaranteeDeposit.setSelected(isCustodianBankOrGuarantee);
/* 305 */     this.rbnDukascopyDeposit.addItemListener(new ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e) {
/* 308 */         if (e.getStateChange() == 1)
/* 309 */           CommissionsDialog.this.fillSpinners(CommissionsDialog.this.dukasCommissions);
/*     */       }
/*     */     });
/* 313 */     this.rbnGuaranteeDeposit.addItemListener(new ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e) {
/* 316 */         if (e.getStateChange() == 1)
/* 317 */           CommissionsDialog.this.fillSpinners(CommissionsDialog.this.guaranteeCommissions);
/*     */       }
/*     */     });
/* 321 */     gbc.fill = 0;
/* 322 */     gbc.anchor = 17;
/* 323 */     JPanel pnlDeposit = new JPanel(new BorderLayout());
/* 324 */     pnlDeposit.add(this.rbnDukascopyDeposit, "West");
/* 325 */     pnlDeposit.add(this.rbnGuaranteeDeposit, "Center");
/*     */ 
/* 327 */     JPanel pnlBottom = new JPanel(new BorderLayout());
/* 328 */     pnlBottom.add(lblDescription, "West");
/* 329 */     pnlBottom.add(buttonsPanel, "East");
/*     */ 
/* 331 */     gbc.fill = 2;
/* 332 */     GridBagLayoutHelper.add(0, 1, 0.0D, 0.0D, 3, 1, 5, 5, 5, 0, gbc, this.mainPanel, pnlDeposit);
/* 333 */     GridBagLayoutHelper.add(0, 2, 0.0D, 0.0D, 3, 1, 10, 5, 5, 0, gbc, this.mainPanel, pnlBottom);
/*     */ 
/* 335 */     this.resetButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 338 */         if (CommissionsDialog.this.rbnGuaranteeDeposit.isSelected()) {
/* 339 */           CommissionsDialog.access$302(CommissionsDialog.this, new Commissions(true));
/* 340 */           CommissionsDialog.this.fillSpinners(CommissionsDialog.this.guaranteeCommissions);
/*     */         } else {
/* 342 */           CommissionsDialog.access$102(CommissionsDialog.this, new Commissions(false));
/* 343 */           CommissionsDialog.this.fillSpinners(CommissionsDialog.this.dukasCommissions);
/*     */         }
/*     */       }
/*     */     });
/* 347 */     this.okButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 350 */         CommissionsDialog.this.fillCommissions();
/* 351 */         CommissionsDialog.this.dispose();
/*     */       }
/*     */     });
/* 354 */     this.cancelButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 357 */         CommissionsDialog.this.dispose();
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private void fillSpinners(Commissions commissions) {
/* 363 */     this.depositSpinners[0].setValue(Integer.valueOf((int)((Double)commissions.getDepositLimits().get(Double.valueOf(DEPOSIT_LIMITS[0]))).intValue()));
/* 364 */     for (int i = 1; i < DEPOSIT_LIMITS.length; i++) {
/* 365 */       this.depositSpinners[i].setValue(Integer.valueOf(Math.max(((Double)commissions.getDepositLimits().get(Double.valueOf(DEPOSIT_LIMITS[i]))).intValue(), ((Integer)((SpinnerNumberModel)this.depositSpinners[i].getModel()).getMinimum()).intValue())));
/*     */     }
/*     */ 
/* 373 */     this.equitySpinners[0].setValue(Integer.valueOf((int)((Double)commissions.getEquityLimits().get(Double.valueOf(EQUITY_LIMITS[0]))).intValue()));
/* 374 */     for (int i = 1; i < EQUITY_LIMITS.length; i++) {
/* 375 */       this.equitySpinners[i].setValue(Integer.valueOf(Math.max(((Double)commissions.getEquityLimits().get(Double.valueOf(EQUITY_LIMITS[i]))).intValue(), ((Integer)((SpinnerNumberModel)this.equitySpinners[i].getModel()).getMinimum()).intValue())));
/*     */     }
/*     */ 
/* 383 */     for (int i = 0; i < TURNOVER_LIMITS.length; i++)
/* 384 */       this.turnoverSpinners[i].setValue(Integer.valueOf(Math.max(((Double)commissions.getTurnoverLimits().get(Double.valueOf(TURNOVER_LIMITS[i]))).intValue(), ((Integer)((SpinnerNumberModel)this.turnoverSpinners[i].getModel()).getMinimum()).intValue())));
/*     */   }
/*     */ 
/*     */   private void addSpinnerListeners()
/*     */   {
/* 394 */     for (JSpinner spinner : this.depositSpinners) {
/* 395 */       spinner.addChangeListener(new ChangeListener()
/*     */       {
/*     */         public void stateChanged(ChangeEvent e) {
/* 398 */           JSpinner source = (JSpinner)e.getSource();
/* 399 */           Integer value = (Integer)source.getValue();
/* 400 */           for (int i = 0; i < CommissionsDialog.this.depositSpinners.length; i++)
/* 401 */             if (CommissionsDialog.this.depositSpinners[i] == e.getSource()) {
/* 402 */               CommissionsDialog.this.currentCommissions().setDepositLimit(CommissionsDialog.DEPOSIT_LIMITS[i], value == null ? 0.0D : value.doubleValue());
/* 403 */               break;
/*     */             }
/*     */         }
/*     */       });
/*     */     }
/* 409 */     for (JSpinner spinner : this.equitySpinners) {
/* 410 */       spinner.addChangeListener(new ChangeListener()
/*     */       {
/*     */         public void stateChanged(ChangeEvent e) {
/* 413 */           JSpinner source = (JSpinner)e.getSource();
/* 414 */           Integer value = (Integer)source.getValue();
/* 415 */           for (int i = 0; i < CommissionsDialog.this.equitySpinners.length; i++)
/* 416 */             if (CommissionsDialog.this.equitySpinners[i] == e.getSource()) {
/* 417 */               CommissionsDialog.this.currentCommissions().setEquityLimit(CommissionsDialog.EQUITY_LIMITS[i], value == null ? 0.0D : value.doubleValue());
/* 418 */               break;
/*     */             }
/*     */         }
/*     */       });
/*     */     }
/* 424 */     for (JSpinner spinner : this.turnoverSpinners)
/* 425 */       spinner.addChangeListener(new ChangeListener()
/*     */       {
/*     */         public void stateChanged(ChangeEvent e) {
/* 428 */           JSpinner source = (JSpinner)e.getSource();
/* 429 */           Integer value = (Integer)source.getValue();
/* 430 */           for (int i = 0; i < CommissionsDialog.this.turnoverSpinners.length; i++)
/* 431 */             if (CommissionsDialog.this.turnoverSpinners[i] == e.getSource()) {
/* 432 */               CommissionsDialog.this.currentCommissions().setTurnoverLimit(CommissionsDialog.TURNOVER_LIMITS[i], value == null ? 0.0D : value.doubleValue());
/* 433 */               break;
/*     */             }
/*     */         }
/*     */       });
/*     */   }
/*     */ 
/*     */   private void fillCommissions()
/*     */   {
/* 442 */     Commissions commissions = new Commissions(this.rbnGuaranteeDeposit.isSelected());
/* 443 */     commissions.setMaxCommission(Math.max(((Integer)this.depositSpinners[0].getValue()).intValue(), ((Integer)this.equitySpinners[0].getValue()).intValue()));
/*     */ 
/* 445 */     for (int i = 0; i < DEPOSIT_LIMITS.length; i++) {
/* 446 */       commissions.setDepositLimit(DEPOSIT_LIMITS[i], ((Integer)this.depositSpinners[i].getValue()).intValue());
/*     */     }
/*     */ 
/* 449 */     for (int i = 0; i < EQUITY_LIMITS.length; i++) {
/* 450 */       commissions.setEquityLimit(EQUITY_LIMITS[i], ((Integer)this.equitySpinners[i].getValue()).intValue());
/*     */     }
/*     */ 
/* 453 */     for (int i = 0; i < TURNOVER_LIMITS.length; i++) {
/* 454 */       commissions.setTurnoverLimit(TURNOVER_LIMITS[i], ((Integer)this.turnoverSpinners[i].getValue()).intValue());
/*     */     }
/*     */ 
/* 457 */     this.commitedCommissions = commissions;
/*     */   }
/*     */ 
/*     */   public Commissions getCommissions() {
/* 461 */     return this.commitedCommissions;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.CommissionsDialog
 * JD-Core Version:    0.6.0
 */