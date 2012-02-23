/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.system.Overnights;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.JRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import java.awt.Component;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Desktop;
/*     */ import java.awt.Dimension;
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
/*     */ import java.util.Map;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRadioButton;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.SpinnerNumberModel;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class OvernightsDialog extends JDialog
/*     */ {
/*  56 */   private static final Logger LOGGER = LoggerFactory.getLogger(OvernightsDialog.class);
/*     */ 
/*  58 */   private static final Instrument[] instruments = { Instrument.AUDCAD, Instrument.AUDCHF, Instrument.AUDJPY, Instrument.AUDNZD, Instrument.AUDSGD, Instrument.AUDUSD, Instrument.CADCHF, Instrument.CADHKD, Instrument.CADJPY, Instrument.CHFJPY, Instrument.CHFPLN, Instrument.CHFSGD, Instrument.EURAUD, Instrument.EURBRL, Instrument.EURCAD, Instrument.EURCHF, Instrument.EURDKK, Instrument.EURGBP, Instrument.EURHKD, Instrument.EURHUF, Instrument.EURJPY, Instrument.EURMXN, Instrument.EURNOK, Instrument.EURNZD, Instrument.EURPLN, Instrument.EURRUB, Instrument.EURSEK, Instrument.EURSGD, Instrument.EURTRY, Instrument.EURUSD, Instrument.EURZAR, Instrument.GBPAUD, Instrument.GBPCAD, Instrument.GBPCHF, Instrument.GBPJPY, Instrument.GBPNZD, Instrument.GBPUSD, Instrument.HKDJPY, Instrument.HUFJPY, Instrument.MXNJPY, Instrument.NZDCAD, Instrument.NZDCHF, Instrument.NZDJPY, Instrument.NZDSGD, Instrument.NZDUSD, Instrument.SGDJPY, Instrument.USDBRL, Instrument.USDCAD, Instrument.USDCHF, Instrument.USDHKD, Instrument.USDHUF, Instrument.USDJPY, Instrument.USDMXN, Instrument.USDNOK, Instrument.USDRUB, Instrument.USDSEK, Instrument.USDZAR, Instrument.ZARJPY };
/*     */   private JPanel mainPanel;
/*  82 */   private JSpinner[] instrumentLongSpinners = new JSpinner[instruments.length];
/*  83 */   private JSpinner[] instrumentShortSpinners = new JSpinner[instruments.length];
/*     */   private JRadioButton rbnRegular;
/*     */   private JRadioButton rbnInstitutional;
/*     */   private JLocalizableButton resetButton;
/*     */   private JLocalizableButton okButton;
/*     */   private JLocalizableButton cancelButton;
/*     */   private Overnights commitedOvernights;
/*     */   private Overnights regularOvernights;
/*     */   private Overnights institutionalOvernights;
/*     */ 
/*     */   public OvernightsDialog(Component parent, Overnights overnights)
/*     */   {
/*  97 */     super(SwingUtilities.getWindowAncestor(parent));
/*  98 */     setModal(true);
/*  99 */     setDefaultCloseOperation(2);
/* 100 */     this.commitedOvernights = null;
/*     */ 
/* 102 */     if (overnights.isInstitutional()) {
/* 103 */       this.regularOvernights = new Overnights(false);
/* 104 */       this.institutionalOvernights = create(true, overnights);
/*     */     } else {
/* 106 */       this.institutionalOvernights = new Overnights(true);
/* 107 */       this.regularOvernights = create(false, overnights);
/*     */     }
/*     */ 
/* 110 */     createSpinners();
/* 111 */     build(overnights);
/* 112 */     fillSpinners(overnights);
/*     */ 
/* 114 */     setContentPane(this.mainPanel);
/* 115 */     setTitle(LocalizationManager.getText("title.overnights"));
/* 116 */     pack();
/* 117 */     setSize(540, 550);
/* 118 */     setMinimumSize(new Dimension(450, 300));
/* 119 */     setLocationRelativeTo((JFrame)GreedContext.get("clientGui"));
/* 120 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   private void createSpinners() {
/* 124 */     for (int i = 0; i < instruments.length; i++) {
/* 125 */       this.instrumentLongSpinners[i] = createSpinnerForLong();
/* 126 */       this.instrumentShortSpinners[i] = createSpinnerForShort();
/*     */     }
/*     */   }
/*     */ 
/*     */   private JSpinner createSpinnerForLong() {
/* 131 */     JSpinner spinner = new JSpinner(new SpinnerNumberModel(0.0D, -100.0D, 100.0D, 0.01D));
/* 132 */     spinner.addChangeListener(new ChangeListener()
/*     */     {
/*     */       public void stateChanged(ChangeEvent e) {
/* 135 */         OvernightsDialog.this.longValueChanged((JSpinner)e.getSource());
/*     */       }
/*     */     });
/* 138 */     return spinner;
/*     */   }
/*     */ 
/*     */   private JSpinner createSpinnerForShort() {
/* 142 */     JSpinner spinner = new JSpinner(new SpinnerNumberModel(0.0D, -100.0D, 100.0D, 0.01D));
/* 143 */     spinner.addChangeListener(new ChangeListener()
/*     */     {
/*     */       public void stateChanged(ChangeEvent e) {
/* 146 */         OvernightsDialog.this.shortValueChanged((JSpinner)e.getSource());
/*     */       }
/*     */     });
/* 149 */     return spinner;
/*     */   }
/*     */ 
/*     */   private void longValueChanged(JSpinner spinner)
/*     */   {
/* 154 */     for (int i = 0; i < this.instrumentLongSpinners.length; i++)
/* 155 */       if (this.instrumentLongSpinners[i] == spinner) {
/* 156 */         Double longValue = (Double)spinner.getValue();
/* 157 */         Instrument instrument = instruments[i];
/* 158 */         currentOvernights().longValues.put(instrument, Double.valueOf(longValue == null ? 0.0D : longValue.doubleValue()));
/* 159 */         break;
/*     */       }
/*     */   }
/*     */ 
/*     */   private void shortValueChanged(JSpinner spinner)
/*     */   {
/* 166 */     for (int i = 0; i < this.instrumentShortSpinners.length; i++)
/* 167 */       if (this.instrumentShortSpinners[i] == spinner) {
/* 168 */         Double shortValue = (Double)spinner.getValue();
/* 169 */         Instrument instrument = instruments[i];
/* 170 */         currentOvernights().shortValues.put(instrument, Double.valueOf(shortValue == null ? 0.0D : shortValue.doubleValue()));
/* 171 */         break;
/*     */       }
/*     */   }
/*     */ 
/*     */   private void fillSpinners(Overnights overnights)
/*     */   {
/* 177 */     for (int i = 0; i < instruments.length; i++) {
/* 178 */       Double longValue = (Double)overnights.getLongOvernights().get(instruments[i]);
/* 179 */       this.instrumentLongSpinners[i].setValue(Double.valueOf(longValue == null ? 0.0D : longValue.doubleValue()));
/* 180 */       Double shortValue = (Double)overnights.getShortOvernights().get(instruments[i]);
/* 181 */       this.instrumentShortSpinners[i].setValue(Double.valueOf(shortValue == null ? 0.0D : shortValue.doubleValue()));
/*     */     }
/*     */   }
/*     */ 
/*     */   private Overnights create(boolean institutional, Overnights data) {
/* 186 */     Overnights result = new Overnights(institutional);
/* 187 */     result.longValues.putAll(data.longValues);
/* 188 */     result.shortValues.putAll(data.shortValues);
/* 189 */     return result;
/*     */   }
/*     */ 
/*     */   private Overnights currentOvernights() {
/* 193 */     if (this.rbnInstitutional.isSelected()) {
/* 194 */       return this.institutionalOvernights;
/*     */     }
/* 196 */     return this.regularOvernights;
/*     */   }
/*     */ 
/*     */   private void build(Overnights overnights)
/*     */   {
/* 201 */     this.mainPanel = new JPanel(new GridBagLayout());
/*     */ 
/* 204 */     this.rbnRegular = new JRadioButton(LocalizationManager.getText("check.regular.rates"));
/* 205 */     this.rbnRegular.addItemListener(new ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e) {
/* 208 */         if (OvernightsDialog.this.rbnRegular.isSelected())
/* 209 */           OvernightsDialog.this.fillSpinners(OvernightsDialog.access$300(OvernightsDialog.this));
/*     */       }
/*     */     });
/* 213 */     this.rbnInstitutional = new JRadioButton(LocalizationManager.getText("check.institutional"));
/* 214 */     this.rbnInstitutional.addItemListener(new ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e) {
/* 217 */         if (OvernightsDialog.this.rbnInstitutional.isSelected())
/* 218 */           OvernightsDialog.this.fillSpinners(OvernightsDialog.access$300(OvernightsDialog.this));
/*     */       }
/*     */     });
/* 223 */     ButtonGroup group = new ButtonGroup();
/* 224 */     group.add(this.rbnRegular);
/* 225 */     group.add(this.rbnInstitutional);
/* 226 */     this.rbnRegular.setSelected(!overnights.isInstitutional());
/* 227 */     this.rbnInstitutional.setSelected(overnights.isInstitutional());
/*     */ 
/* 230 */     JPanel instrumentsPanel = new JPanel(new GridBagLayout());
/* 231 */     JScrollPane scrollPane = new JScrollPane(instrumentsPanel);
/*     */ 
/* 233 */     JRoundedBorder border = new JLocalizableRoundedBorder(instrumentsPanel, "border.overnight.policy");
/* 234 */     scrollPane.setBorder(border);
/*     */ 
/* 236 */     GridBagConstraints gbc = new GridBagConstraints();
/* 237 */     gbc.fill = 1;
/*     */ 
/* 239 */     for (int i = 0; i < instruments.length; i++) {
/* 240 */       boolean secondHalf = i >= instruments.length / 2;
/* 241 */       GridBagLayoutHelper.add(secondHalf ? 3 : 0, secondHalf ? i - instruments.length / 2 : i, 0.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, instrumentsPanel, new JLabel(instruments[i].toString()));
/* 242 */       GridBagLayoutHelper.add(secondHalf ? 4 : 1, secondHalf ? i - instruments.length / 2 : i, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, instrumentsPanel, this.instrumentLongSpinners[i]);
/* 243 */       GridBagLayoutHelper.add(secondHalf ? 5 : 2, secondHalf ? i - instruments.length / 2 : i, 1.0D, 0.0D, 1, 1, 5, 5, 0, 0, gbc, instrumentsPanel, this.instrumentShortSpinners[i]);
/*     */     }
/*     */ 
/* 246 */     JPanel buttonsPanel = new JPanel(new FlowLayout(2));
/* 247 */     this.resetButton = new JLocalizableButton("button.reset");
/* 248 */     this.okButton = new JLocalizableButton("button.ok");
/* 249 */     this.cancelButton = new JLocalizableButton("button.cancel");
/* 250 */     buttonsPanel.add(this.resetButton);
/* 251 */     buttonsPanel.add(this.okButton);
/* 252 */     buttonsPanel.add(this.cancelButton);
/*     */ 
/* 254 */     String labelText = "<html><u>" + LocalizationManager.getText("optimizer.label.description.overnights") + "</u></html>";
/* 255 */     JLabel lblDescription = new JLabel(labelText);
/* 256 */     lblDescription.setCursor(Cursor.getPredefinedCursor(12));
/* 257 */     lblDescription.addMouseListener(new MouseAdapter()
/*     */     {
/*     */       public void mouseReleased(MouseEvent e) {
/* 260 */         if (!e.isPopupTrigger())
/*     */           try {
/* 262 */             URI uri = new URI("http://www.dukascopy.com/swiss/english/forex/forex_trading_accounts/overnight/");
/*     */ 
/* 264 */             Desktop.getDesktop().browse(uri);
/*     */           } catch (URISyntaxException ex) {
/* 266 */             OvernightsDialog.LOGGER.error("Error opening Dukascopy web page.", ex);
/*     */           } catch (IOException ex) {
/* 268 */             OvernightsDialog.LOGGER.error("Error opening Dukascopy web page.", ex);
/*     */           }
/*     */       }
/*     */     });
/* 274 */     GridBagLayoutHelper.add(0, 0, 1.0D, 1.0D, 3, 1, 5, 5, 5, 0, gbc, this.mainPanel, scrollPane);
/* 275 */     gbc.fill = 0;
/* 276 */     gbc.anchor = 17;
/* 277 */     GridBagLayoutHelper.add(0, 1, 0.0D, 0.0D, 1, 1, 5, 5, 5, 0, gbc, this.mainPanel, this.rbnRegular);
/* 278 */     GridBagLayoutHelper.add(1, 1, 1.0D, 0.0D, 1, 1, 5, 5, 5, 0, gbc, this.mainPanel, this.rbnInstitutional);
/* 279 */     gbc.anchor = 16;
/* 280 */     gbc.fill = 2;
/* 281 */     GridBagLayoutHelper.add(0, 2, 0.0D, 0.0D, 2, 1, 10, 5, 5, 10, gbc, this.mainPanel, lblDescription);
/* 282 */     gbc.anchor = 14;
/* 283 */     GridBagLayoutHelper.add(2, 2, 1.0D, 0.0D, 1, 1, 5, 5, 5, 5, gbc, this.mainPanel, buttonsPanel);
/*     */ 
/* 285 */     this.resetButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 288 */         if (OvernightsDialog.this.rbnInstitutional.isSelected()) {
/* 289 */           OvernightsDialog.access$702(OvernightsDialog.this, new Overnights(true));
/* 290 */           OvernightsDialog.this.fillSpinners(OvernightsDialog.this.institutionalOvernights);
/*     */         } else {
/* 292 */           OvernightsDialog.access$802(OvernightsDialog.this, new Overnights(false));
/* 293 */           OvernightsDialog.this.fillSpinners(OvernightsDialog.this.regularOvernights);
/*     */         }
/*     */       }
/*     */     });
/* 297 */     this.okButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 300 */         OvernightsDialog.this.fillOvernights();
/* 301 */         OvernightsDialog.this.dispose();
/*     */       }
/*     */     });
/* 304 */     this.cancelButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 307 */         OvernightsDialog.this.dispose();
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private void fillOvernights() {
/* 313 */     this.commitedOvernights = create(this.rbnInstitutional.isSelected(), currentOvernights());
/*     */   }
/*     */ 
/*     */   public Overnights getOvernights() {
/* 317 */     return this.commitedOvernights;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.OvernightsDialog
 * JD-Core Version:    0.6.0
 */