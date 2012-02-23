/*    */ package com.dukascopy.charts.utils.helper;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import java.awt.Component;
/*    */ import javax.swing.Icon;
/*    */ import javax.swing.JOptionPane;
/*    */ 
/*    */ public class LocalizedMessageHelper
/*    */ {
/*    */   private static Object[] yesNoOptions;
/*    */   private static Object[] okOptions;
/*    */ 
/*    */   public static int showConfirmationMessage(Component owner, String message)
/*    */   {
/* 22 */     return showConfirmationMessage(owner, LocalizationManager.getText("confirmation.title"), message, null);
/*    */   }
/*    */   public static int showConfirmationMessage(Component owner, String confirmationTitle, String message) {
/* 25 */     return showConfirmationMessage(owner, confirmationTitle, message, null);
/*    */   }
/*    */   public static int showConfirmationMessage(Component owner, String confirmationTitle, String message, Icon icon) {
/* 28 */     resetTexts();
/*    */ 
/* 30 */     int n = JOptionPane.showOptionDialog(owner, message, confirmationTitle, 0, 3, icon, yesNoOptions, yesNoOptions[0]);
/*    */ 
/* 36 */     return n;
/*    */   }
/*    */ 
/*    */   public static void showInformtionMessage(Component owner, String message)
/*    */   {
/* 41 */     showInformtionMessage(owner, LocalizationManager.getText("information.title"), message, null);
/*    */   }
/*    */   public static void showInformtionMessage(Component owner, String informationTitle, String message, Icon icon) {
/* 44 */     resetTexts();
/*    */ 
/* 46 */     JOptionPane.showOptionDialog(owner, message, informationTitle, 0, 1, icon, okOptions, okOptions[0]);
/*    */   }
/*    */ 
/*    */   public static void showErrorMessage(Component owner, String message)
/*    */   {
/* 55 */     showErrorMessage(owner, LocalizationManager.getText("error.title"), message, null);
/*    */   }
/*    */   public static void showErrorMessage(Component owner, String errorTitle, String message, Icon icon) {
/* 58 */     resetTexts();
/*    */ 
/* 60 */     JOptionPane.showOptionDialog(owner, message, errorTitle, 0, 0, icon, okOptions, okOptions[0]);
/*    */   }
/*    */ 
/*    */   public static String formatMessage(String message, boolean isBold, boolean isItalic, boolean isCenter, boolean isNewLine)
/*    */   {
/* 70 */     StringBuilder result = new StringBuilder(message);
/*    */ 
/* 72 */     if (isBold)
/* 73 */       result.insert(0, "<b>");
/* 74 */     if (isItalic)
/* 75 */       result.insert(0, "<i>");
/* 76 */     if (isCenter)
/* 77 */       result.insert(0, "<body align='center'>");
/* 78 */     if (isNewLine) {
/* 79 */       result.append("<br>");
/*    */     }
/* 81 */     result.insert(0, "<html>");
/* 82 */     return result.toString();
/*    */   }
/*    */ 
/*    */   private static void resetTexts()
/*    */   {
/* 87 */     yesNoOptions = new Object[] { LocalizationManager.getText("yes.option"), LocalizationManager.getText("no.option") };
/* 88 */     okOptions = new Object[] { LocalizationManager.getText("ok.option") };
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.utils.helper.LocalizedMessageHelper
 * JD-Core Version:    0.6.0
 */