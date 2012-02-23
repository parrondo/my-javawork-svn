/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;
/*     */ 
/*     */ import com.dukascopy.api.Configurable;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.params.Variable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.File;
/*     */ import java.lang.reflect.Field;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Arrays;
/*     */ import java.util.Calendar;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.SpinnerNumberModel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class ParametersOptimizationPanel extends JPanel
/*     */   implements Localizable, ParameterOptimizerListener
/*     */ {
/*  54 */   private static final Logger LOGGER = LoggerFactory.getLogger(ParametersOptimizationPanel.class);
/*     */   private IStrategy strategy;
/*     */   private JPanel pnlMain;
/*     */   private JLocalizableButton btnOk;
/*     */   private JLocalizableButton btnCancel;
/*     */   private JLabel lblNumberOfCombinations;
/*     */   private JPanel pnlButtons;
/*     */   private ParameterOptimizationData modalResult;
/*     */   private JSpinner txtDropDown;
/*  65 */   private HashMap<Field, ParameterOptimizer> optimizers = new HashMap();
/*     */   private final JDialog parentDialog;
/*     */ 
/*     */   public ParametersOptimizationPanel(JDialog parentDialog, IStrategy strategy)
/*     */   {
/*  74 */     this.parentDialog = parentDialog;
/*  75 */     this.strategy = strategy;
/*  76 */     initUI();
/*  77 */     LocalizationManager.addLocalizable(this);
/*     */   }
/*     */ 
/*     */   private void initUI()
/*     */   {
/*  82 */     SpinnerNumberModel mdlDropDown = new SpinnerNumberModel(30, 0, 99, 1);
/*  83 */     this.txtDropDown = new JSpinner(mdlDropDown);
/*     */ 
/*  85 */     JLabel lblDropDown = new JLocalizableLabel("optimizer.label.balance.drop.down");
/*  86 */     JPanel pnlDropDown = new JPanel(new GridBagLayout());
/*  87 */     pnlDropDown.add(lblDropDown, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(10, 10, 0, 10), 0, 0));
/*     */ 
/*  90 */     pnlDropDown.add(this.txtDropDown, new GridBagConstraints(1, 0, 1, 1, 1.0D, 0.0D, 17, 0, new Insets(10, 5, 0, 10), 0, 0));
/*     */ 
/*  93 */     JPanel pnlSeparator = new JPanel(new BorderLayout());
/*  94 */     pnlSeparator.setBorder(BorderFactory.createEtchedBorder());
/*  95 */     pnlDropDown.add(pnlSeparator, new GridBagConstraints(0, 1, 3, 1, 0.0D, 0.0D, 10, 2, new Insets(10, 10, 0, 10), 0, 0));
/*     */ 
/* 100 */     this.pnlMain = new JPanel(new GridBagLayout());
/* 101 */     this.pnlMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/* 102 */     JScrollPane scpMain = new JScrollPane(this.pnlMain);
/* 103 */     scpMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*     */ 
/* 106 */     this.btnOk = new JLocalizableButton("button.ok");
/* 107 */     this.btnOk.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 110 */         ParametersOptimizationPanel.this.btnOk_actionPerformed(e);
/*     */       }
/*     */     });
/* 113 */     this.btnCancel = new JLocalizableButton("button.cancel");
/* 114 */     this.btnCancel.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 117 */         ParametersOptimizationPanel.this.btnCancel_actionPerformed(e);
/*     */       }
/*     */     });
/* 120 */     this.lblNumberOfCombinations = new JLabel();
/* 121 */     updateNumberOfCombinations();
/*     */ 
/* 123 */     this.pnlButtons = new JPanel(new GridLayout(1, 2, 5, 0));
/* 124 */     this.pnlButtons.add(this.btnOk);
/* 125 */     this.pnlButtons.add(this.btnCancel);
/* 126 */     JPanel pnlButtonsContainer = new JPanel(new BorderLayout());
/* 127 */     pnlButtonsContainer.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
/* 128 */     pnlButtonsContainer.add(this.pnlButtons, "East");
/* 129 */     pnlButtonsContainer.add(this.lblNumberOfCombinations, "West");
/*     */ 
/* 132 */     setLayout(new BorderLayout());
/* 133 */     add(pnlDropDown, "North");
/* 134 */     add(scpMain, "Center");
/* 135 */     add(pnlButtonsContainer, "South");
/*     */   }
/*     */ 
/*     */   public void localize()
/*     */   {
/* 141 */     updateNumberOfCombinations();
/*     */   }
/*     */ 
/*     */   public void parametersChanged(ParameterOptimizerEvent event)
/*     */   {
/* 146 */     updateNumberOfCombinations();
/*     */   }
/*     */ 
/*     */   private void btnOk_actionPerformed(ActionEvent e) {
/* 150 */     if (commit())
/* 151 */       this.parentDialog.dispose();
/*     */   }
/*     */ 
/*     */   private boolean commit()
/*     */   {
/* 162 */     Object dropDownValue = this.txtDropDown.getValue();
/*     */     double dropDown;
/*     */     double dropDown;
/* 163 */     if ((dropDownValue instanceof Integer))
/* 164 */       dropDown = ((Integer)dropDownValue).doubleValue() / 100.0D;
/*     */     else {
/* 166 */       dropDown = 0.0D;
/*     */     }
/*     */ 
/* 169 */     HashMap params = new HashMap();
/*     */ 
/* 171 */     for (Field field : getStrategyFields()) {
/* 172 */       ParameterOptimizer optimizer = (ParameterOptimizer)this.optimizers.get(field);
/* 173 */       if (optimizer == null)
/*     */         continue;
/*     */       try
/*     */       {
/* 177 */         optimizer.validateParams();
/*     */ 
/* 180 */         Object[] values = optimizer.getParams();
/* 181 */         if ((values != null) && (values.length < 1))
/*     */         {
/*     */           continue;
/*     */         }
/* 185 */         Variable[] variables = new Variable[values.length];
/* 186 */         for (int i = 0; i < variables.length; i++) {
/* 187 */           variables[i] = new Variable(values[i], field.getType());
/*     */         }
/* 189 */         params.put(field.getName(), variables);
/*     */       }
/*     */       catch (CommitErrorException e)
/*     */       {
/* 193 */         Configurable configurable = (Configurable)field.getAnnotation(Configurable.class);
/*     */ 
/* 195 */         String message = MessageFormat.format(LocalizationManager.getText("optimizer.dialog.error.template.invalid.field"), new Object[] { configurable.value(), e.getLocalizedMessage() });
/*     */ 
/* 200 */         String title = LocalizationManager.getText("optimizer.dialog.title.error");
/* 201 */         JOptionPane.showMessageDialog(this, message, title, 1);
/* 202 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 206 */     this.modalResult = new ParameterOptimizationData(params, dropDown);
/* 207 */     return true;
/*     */   }
/*     */ 
/*     */   private void btnCancel_actionPerformed(ActionEvent e) {
/* 211 */     this.modalResult = null;
/* 212 */     this.parentDialog.dispose();
/*     */   }
/*     */ 
/*     */   public void setParameters(ParameterOptimizationData data) {
/* 216 */     List fields = getStrategyFields();
/*     */ 
/* 218 */     this.pnlMain.removeAll();
/* 219 */     this.optimizers.clear();
/*     */     HashMap params;
/*     */     HashMap params;
/* 223 */     if (data == null) {
/* 224 */       this.txtDropDown.setValue(Integer.valueOf(30));
/* 225 */       params = null;
/*     */     } else {
/* 227 */       this.txtDropDown.setValue(Integer.valueOf((int)(data.getDropDown() * 100.0D)));
/* 228 */       params = data.getParameters();
/*     */     }
/*     */ 
/* 231 */     int y = 2;
/* 232 */     for (Field field : fields) {
/* 233 */       y = add(y, field, this.pnlMain, params == null ? null : (Variable[])params.get(field.getName()));
/*     */     }
/*     */ 
/* 237 */     this.pnlMain.add(new JPanel(), new GridBagConstraints(2, y, 1, 1, 1.0D, 1.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
/*     */ 
/* 239 */     updateNumberOfCombinations();
/* 240 */     this.modalResult = null;
/*     */   }
/*     */ 
/*     */   public ParameterOptimizationData getModalResult() {
/* 244 */     return this.modalResult;
/*     */   }
/*     */ 
/*     */   private List<Field> getStrategyFields() {
/* 248 */     List result = new LinkedList();
/*     */ 
/* 250 */     Class strategyClass = this.strategy.getClass();
/* 251 */     Field[] fields = strategyClass.getFields();
/* 252 */     for (Field field : fields) {
/* 253 */       Configurable configurable = (Configurable)field.getAnnotation(Configurable.class);
/* 254 */       if (configurable != null) {
/* 255 */         result.add(field);
/*     */       }
/*     */     }
/*     */ 
/* 259 */     return result;
/*     */   }
/*     */ 
/* 263 */   private int add(int y, Field field, JPanel pnlContainer, Variable[] params) { Configurable configurable = (Configurable)field.getAnnotation(Configurable.class);
/*     */     Object value;
/*     */     try {
/* 267 */       value = field.get(this.strategy);
/*     */     } catch (IllegalArgumentException e) {
/* 269 */       LOGGER.warn(MessageFormat.format("Strategy field {0} is not accessible.", new Object[] { configurable.value() }));
/* 270 */       return y;
/*     */     } catch (IllegalAccessException e) {
/* 272 */       LOGGER.warn(MessageFormat.format("Strategy field {0} is not accessible.", new Object[] { configurable.value() }));
/* 273 */       return y;
/*     */     }
/*     */ 
/* 276 */     ParameterOptimizer optimizer = createOptimizer(field, value, configurable);
/* 277 */     if (optimizer == null) {
/* 278 */       return y;
/*     */     }
/* 280 */     if (params != null) {
/* 281 */       Object[] values = new Object[params.length];
/* 282 */       for (int i = 0; i < values.length; i++)
/* 283 */         values[i] = params[i].getValue();
/*     */       try
/*     */       {
/* 286 */         optimizer.setParams(values);
/*     */       } catch (Exception e) {
/* 288 */         LOGGER.debug(new StringBuilder().append("Error setting values to field ").append(field.getName()).toString(), e);
/*     */       }
/*     */     }
/* 291 */     this.optimizers.put(field, optimizer);
/* 292 */     optimizer.addOptimizerListener(this);
/*     */ 
/* 295 */     String fieldName = field.getName();
/* 296 */     String uiFieldName = configurable.value();
/* 297 */     if ((uiFieldName == null) || (uiFieldName.isEmpty())) {
/* 298 */       uiFieldName = fieldName;
/*     */     }
/*     */ 
/* 301 */     JLabel label = new JLabel(new StringBuilder().append("<html><font family=Verdana>").append(uiFieldName).append("</font>").append(configurable.obligatory() ? "<sup><font family=Verdana color=Red>*</font></sup>" : "").append("</html>").toString(), null, 4);
/* 302 */     label.setToolTipText(!ObjectUtils.isNullOrEmpty(configurable.description()) ? configurable.description() : uiFieldName);
/* 303 */     pnlContainer.add(label, new GridBagConstraints(0, y, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(5, 0, 0, 0), 0, 0));
/*     */ 
/* 305 */     Component c = optimizer.getMainComponent();
/* 306 */     pnlContainer.add(c, new GridBagConstraints(1, y, 1, 1, 0.0D, 0.0D, 10, 2, new Insets(5, 5, 0, 0), 0, 0));
/*     */ 
/* 308 */     JPanel pnlOptimizerComponents = new JPanel();
/* 309 */     optimizer.layoutOptimizerComponents(pnlOptimizerComponents, value);
/* 310 */     pnlContainer.add(pnlOptimizerComponents, new GridBagConstraints(2, y, 1, 1, 1.0D, 0.0D, 10, 2, new Insets(5, 5, 0, 0), 0, 0));
/* 311 */     return y + 1; }
/*     */ 
/*     */   private ParameterOptimizer createOptimizer(Field field, Object value, Configurable configurable)
/*     */   {
/* 315 */     Class fieldType = field.getType();
/* 316 */     boolean mandatory = configurable.obligatory();
/* 317 */     boolean readOnly = configurable.readOnly();
/*     */ 
/* 319 */     if (String.class.equals(fieldType)) {
/* 320 */       return new StringParameterOptimizer((String)value, mandatory, readOnly);
/*     */     }
/* 322 */     if (fieldType.isEnum()) {
/* 323 */       return new ArrayParameterOptimizer(getEnumConstants(fieldType), value, mandatory, readOnly);
/*     */     }
/* 325 */     if (Period.class.equals(fieldType)) {
/* 326 */       return new ArrayParameterOptimizer(Period.valuesForIndicator(), value, mandatory, readOnly);
/*     */     }
/* 328 */     if ((Boolean.TYPE.equals(fieldType)) || (Boolean.class.equals(fieldType))) {
/* 329 */       return new BooleanParameterOptimizer(mandatory, readOnly, (Boolean)value);
/*     */     }
/* 331 */     if (File.class.equals(fieldType)) {
/* 332 */       return new FileParameterOptimizer(configurable.fileType(), mandatory, readOnly, (File)value);
/*     */     }
/* 334 */     if ((Integer.TYPE.equals(fieldType)) || (Integer.class.equals(fieldType)))
/*     */     {
/*     */       int stepSize;
/*     */       int stepSize;
/* 336 */       if (configurable.stepSize() > 0.0D)
/* 337 */         stepSize = (int)configurable.stepSize();
/*     */       else {
/* 339 */         stepSize = 1;
/*     */       }
/* 341 */       return new IntegerParameterOptimizer(((Integer)value).intValue(), 2147483647, stepSize, mandatory, readOnly);
/*     */     }
/* 343 */     if ((Double.TYPE.equals(fieldType)) || (Double.class.equals(fieldType)))
/*     */     {
/*     */       double stepSize;
/*     */       double stepSize;
/* 345 */       if (configurable.stepSize() > 0.0D)
/* 346 */         stepSize = configurable.stepSize();
/*     */       else {
/* 348 */         stepSize = 1.0D;
/*     */       }
/* 350 */       return new DoubleParameterOptimizer(((Double)value).doubleValue(), 1.7976931348623157E+308D, stepSize, mandatory, readOnly);
/*     */     }
/* 352 */     if ((Long.TYPE.equals(fieldType)) || (Long.class.equals(fieldType)))
/*     */     {
/*     */       long stepSize;
/*     */       long stepSize;
/* 354 */       if (configurable.stepSize() > 0.0D)
/* 355 */         stepSize = ()configurable.stepSize();
/*     */       else {
/* 357 */         stepSize = 1L;
/*     */       }
/* 359 */       return new LongParameterOptimizer(((Long)value).longValue(), 9223372036854775807L, stepSize, mandatory, readOnly);
/*     */     }
/* 361 */     if (Calendar.class.isAssignableFrom(fieldType)) {
/* 362 */       return new CalendarParameterOptimizer((Calendar)value, mandatory, readOnly);
/*     */     }
/* 364 */     if (Date.class.isAssignableFrom(fieldType)) {
/* 365 */       return new DateParameterOptimizer((Date)value, mandatory, readOnly);
/*     */     }
/*     */ 
/* 368 */     return null;
/*     */   }
/*     */ 
/*     */   private Object[] getEnumConstants(Class<?> enumType)
/*     */   {
/* 373 */     Object[] constants = enumType.getEnumConstants();
/* 374 */     Arrays.sort(constants, new Comparator()
/*     */     {
/*     */       public int compare(Object o1, Object o2) {
/* 377 */         if ((o1 == null) && (o2 == null))
/* 378 */           return 0;
/* 379 */         if (o1 == null)
/* 380 */           return -1;
/* 381 */         if (o2 == null) {
/* 382 */           return 1;
/*     */         }
/* 384 */         return o1.toString().compareTo(o2.toString());
/*     */       }
/*     */     });
/* 388 */     return constants;
/*     */   }
/*     */ 
/*     */   private void updateNumberOfCombinations()
/*     */   {
/* 395 */     int totalNumber = 1;
/* 396 */     for (ParameterOptimizer optimizer : this.optimizers.values()) {
/* 397 */       Object[] params = optimizer.getParams();
/* 398 */       if ((params != null) && (params.length > 0)) {
/* 399 */         totalNumber *= params.length;
/*     */       }
/*     */     }
/* 402 */     String pattern = LocalizationManager.getText("optimizer.label.template.number.of.combinations");
/* 403 */     this.lblNumberOfCombinations.setText(MessageFormat.format(pattern, new Object[] { Integer.valueOf(totalNumber) }));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.ParametersOptimizationPanel
 * JD-Core Version:    0.6.0
 */