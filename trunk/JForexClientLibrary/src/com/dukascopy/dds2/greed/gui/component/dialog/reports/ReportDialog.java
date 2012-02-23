/*     */ package com.dukascopy.dds2.greed.gui.component.dialog.reports;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.HeaderPanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.util.LoginPassEncoder;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.request.ReportInfo;
/*     */ import com.dukascopy.transport.common.msg.request.ReportParameter;
/*     */ import com.toedter.calendar.JDateChooser;
/*     */ import java.awt.Container;
/*     */ import java.awt.Font;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.text.ParseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class ReportDialog extends JFrame
/*     */   implements ActionListener
/*     */ {
/*  55 */   private static Logger LOGGER = LoggerFactory.getLogger(ReportDialog.class);
/*     */   private final String reportTitle;
/*     */   private List<ReportParameter> reportParams;
/*  59 */   private Map<String, ReportParamDetailsInf> fields = new HashMap();
/*  60 */   private String format = "0";
/*  61 */   private List<JDateChooser> dateChoosers = new ArrayList();
/*     */ 
/*     */   public ReportDialog(ReportInfo report)
/*     */   {
/*  66 */     Toolkit.getDefaultToolkit().setDynamicLayout(true);
/*     */     try
/*     */     {
/*  69 */       setIconImage(GuiUtilsAndConstants.PLATFPORM_ICON.getImage());
/*     */     } catch (Exception e) {
/*  71 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */ 
/*  74 */     setDefaultCloseOperation(2);
/*     */ 
/*  76 */     setTitle(GuiUtilsAndConstants.LABEL_SHORT_NAME);
/*     */ 
/*  78 */     this.reportParams = report.getParams();
/*  79 */     this.reportTitle = report.getName();
/*  80 */     build();
/*     */   }
/*     */ 
/*     */   private void build() {
/*  84 */     JLabel exportLabel = new JLabel("Export type:");
/*  85 */     Font fn = exportLabel.getFont();
/*  86 */     Font f = new Font(fn.getName(), 1, fn.getSize());
/*  87 */     exportLabel.setFont(f);
/*     */ 
/*  90 */     JPanel header = new HeaderPanel(this.reportTitle, true);
/*     */ 
/* 124 */     JPanel content = new JPanel();
/* 125 */     content.setLayout(new GridBagLayout());
/* 126 */     GridBagConstraints c = new GridBagConstraints();
/* 127 */     c.insets = new Insets(0, 10, 5, 10);
/* 128 */     c.weightx = 1.0D; c.weighty = 0.5D;
/* 129 */     c.anchor = 17;
/* 130 */     c.fill = 2;
/* 131 */     int gridy = 0;
/*     */ 
/* 141 */     String COLON = ":";
/* 142 */     for (ReportParameter param : this.reportParams) {
/* 143 */       if (param.getIsForInput()) {
/* 144 */         c.gridx = 0; c.gridy = gridy;
/* 145 */         String name = param.getName();
/* 146 */         if (!name.endsWith(":")) name = name + ":";
/* 147 */         JLabel label = new JLabel(name);
/* 148 */         label.setFont(f);
/* 149 */         content.add(label, c);
/*     */ 
/* 151 */         c.gridx = 1; c.gridy = (gridy++);
/*     */         ReportParamDetailsInf field;
/* 153 */         if ("date".equals(param.getDataType())) {
/* 154 */           ReportParamDetailsInf field = new ReportParameterEditDateField(param, fn);
/* 155 */           this.dateChoosers.add((JDateChooser)field);
/*     */         }
/*     */         else
/*     */         {
/*     */           ReportParamDetailsInf field;
/* 156 */           if ("boolean".equals(param.getDataType())) {
/* 157 */             field = new ReportParameterEditBooleanField(param, fn);
/*     */           }
/*     */           else
/*     */           {
/*     */             ReportParamDetailsInf field;
/* 158 */             if ("integer".equals(param.getDataType()))
/* 159 */               field = new ReportParameterEditNumberField(param, fn);
/*     */             else
/* 161 */               field = new ReportParameterEditTextField(param, fn); 
/*     */           }
/*     */         }
/* 163 */         this.fields.put(param.getKey(), field);
/* 164 */         content.add((JComponent)field, c);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 169 */     JPanel remoteControl = new JPanel();
/* 170 */     GridLayout gl = new GridLayout(1, 2);
/* 171 */     gl.setHgap(5);
/* 172 */     remoteControl.setLayout(gl);
/*     */ 
/* 174 */     JButton bOK = new JButton("OK");
/* 175 */     bOK.setMnemonic(79);
/* 176 */     JButton bCancel = new JButton("Cancel");
/* 177 */     bCancel.setMnemonic(67);
/* 178 */     remoteControl.add(bOK);
/* 179 */     remoteControl.add(bCancel);
/* 180 */     c.gridx = 0; c.gridy = gridy;
/* 181 */     c.anchor = 10;
/* 182 */     c.fill = 0;
/* 183 */     c.ipady = 0; c.gridwidth = 2;
/* 184 */     content.add(remoteControl, c);
/*     */ 
/* 187 */     Container container = getContentPane();
/* 188 */     container.setLayout(new BoxLayout(container, 1));
/*     */ 
/* 190 */     container.add(header);
/* 191 */     container.add(content);
/*     */ 
/* 194 */     bCancel.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 196 */         ReportDialog.this.dispose();
/*     */       }
/*     */     });
/* 199 */     bOK.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 201 */         Properties properties = (Properties)GreedContext.get("properties");
/* 202 */         String baseUrl = properties.getProperty("base.url");
/* 203 */         String reportsUrl = properties.getProperty("reports.url");
/*     */ 
/* 205 */         if ((null == baseUrl) || (null == reportsUrl)) {
/* 206 */           ReportDialog.this.dispose();
/* 207 */           ClientForm formParent = (ClientForm)GreedContext.get("clientGui");
/* 208 */           JOptionPane.showMessageDialog(formParent, LocalizationManager.getText("joption.pane.report.not.available"), GuiUtilsAndConstants.LABEL_SHORT_NAME, 0);
/*     */ 
/* 213 */           return;
/*     */         }
/*     */ 
/* 217 */         StringBuffer sb = new StringBuffer(baseUrl).append(reportsUrl).append("/report.action?").append("userName=").append((String)GreedContext.getConfig("account_name")).append("&password=").append(ReportDialog.this.generateMD5((String)GreedContext.getConfig(" "))).append("&exportType=").append(ReportDialog.this.format);
/*     */ 
/* 227 */         for (ReportParameter param : ReportDialog.this.reportParams) {
/* 228 */           if (param.getIsForInput())
/* 229 */             sb.append("&").append(((ReportParamDetailsInf)ReportDialog.this.fields.get(param.getKey())).getUrlString());
/*     */           else {
/* 231 */             sb.append("&").append(param.getParameterString());
/*     */           }
/*     */         }
/* 234 */         if (ReportDialog.LOGGER.isDebugEnabled()) {
/* 235 */           ReportDialog.LOGGER.debug("Report requested: " + sb.toString());
/*     */         }
/* 237 */         GuiUtilsAndConstants.openURL(sb.toString());
/* 238 */         ReportDialog.this.dispose();
/*     */       }
/*     */     });
/* 242 */     pack();
/* 243 */     setResizable(false);
/* 244 */     setLocationRelativeTo(null);
/* 245 */     setAlwaysOnTop(true);
/* 246 */     addWindowListener(new WindowAdapter() {
/*     */       public void windowClosed(WindowEvent e) {
/* 248 */         for (JDateChooser dateChooser : ReportDialog.this.dateChoosers)
/* 249 */           dateChooser.cleanup();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/* 256 */     this.format = e.getActionCommand();
/*     */   }
/*     */ 
/*     */   private String generateMD5(String string) {
/* 260 */     MessageDigest md5 = null;
/*     */     try {
/* 262 */       md5 = MessageDigest.getInstance("MD5");
/*     */     } catch (NoSuchAlgorithmException nsae) {
/* 264 */       LOGGER.error(nsae.getMessage(), nsae);
/* 265 */       return null;
/*     */     }
/* 267 */     String CHARSET_ISO = "ISO-8859-1";
/* 268 */     char[] encodedChars = new char[0];
/*     */     try {
/* 270 */       byte[] encodedBytes = md5.digest(string.getBytes("ISO-8859-1"));
/* 271 */       encodedChars = new String(encodedBytes, "ISO-8859-1").toCharArray();
/*     */     } catch (UnsupportedEncodingException uee) {
/* 273 */       LOGGER.error(uee.getMessage(), uee);
/* 274 */       return null;
/*     */     }
/* 276 */     return new String(LoginPassEncoder.toHexString(encodedChars));
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 280 */     String reportString = "{\"type\":\"reportInfo\",\"params\":[{\"key\":\"reportName\",\"value\":\"account_statement_NOSCC\",\"type\":\"reportParameter\",\"dataType\":\"integer\",\"isForInput\":true,\"name\":\"ReportName\"},{\"key\":\"CLIENT_ID\",\"value\":\"7752\",\"type\":\"reportParameter\",\"dataType\":\"string\",\"isForInput\":false,\"name\":\"ClientId\"},{\"key\":\"TRADE_DATE1\",\"type\":\"reportParameter\",\"dataType\":\"date\",\"isForInput\":true,\"name\":\"Start Date (DD/MM/YYYY)\"},{\"key\":\"TRADE_DATE2\",\"type\":\"reportParameter\",\"dataType\":\"date\",\"isForInput\":true,\"name\":\"End Date (DD/MM/YYYY)\"},{\"key\":\"TRADE_CONFIRMED\",\"type\":\"reportParameter\",\"dataType\":\"boolean\",\"isForInput\":true,\"name\":\"Confirmed\"}],\"name\":\"Account Statement for No Currency Clients\"}";
/*     */     try
/*     */     {
/* 283 */       ReportDialog dialog = new ReportDialog(new ReportInfo(new ProtocolMessage("{\"type\":\"reportInfo\",\"params\":[{\"key\":\"reportName\",\"value\":\"account_statement_NOSCC\",\"type\":\"reportParameter\",\"dataType\":\"integer\",\"isForInput\":true,\"name\":\"ReportName\"},{\"key\":\"CLIENT_ID\",\"value\":\"7752\",\"type\":\"reportParameter\",\"dataType\":\"string\",\"isForInput\":false,\"name\":\"ClientId\"},{\"key\":\"TRADE_DATE1\",\"type\":\"reportParameter\",\"dataType\":\"date\",\"isForInput\":true,\"name\":\"Start Date (DD/MM/YYYY)\"},{\"key\":\"TRADE_DATE2\",\"type\":\"reportParameter\",\"dataType\":\"date\",\"isForInput\":true,\"name\":\"End Date (DD/MM/YYYY)\"},{\"key\":\"TRADE_CONFIRMED\",\"type\":\"reportParameter\",\"dataType\":\"boolean\",\"isForInput\":true,\"name\":\"Confirmed\"}],\"name\":\"Account Statement for No Currency Clients\"}")));
/* 284 */       dialog.setVisible(true);
/*     */     } catch (ParseException e) {
/* 286 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.reports.ReportDialog
 * JD-Core Version:    0.6.0
 */