/*    */ package org.apache.lucene.messages;
/*    */ 
/*    */ import java.util.Locale;
/*    */ 
/*    */ public class MessageImpl
/*    */   implements Message
/*    */ {
/*    */   private static final long serialVersionUID = -3077643314630884523L;
/*    */   private String key;
/* 32 */   private Object[] arguments = new Object[0];
/*    */ 
/*    */   public MessageImpl(String key) {
/* 35 */     this.key = key;
/*    */   }
/*    */ 
/*    */   public MessageImpl(String key, Object[] args)
/*    */   {
/* 40 */     this(key);
/* 41 */     this.arguments = args;
/*    */   }
/*    */ 
/*    */   public Object[] getArguments() {
/* 45 */     return this.arguments;
/*    */   }
/*    */ 
/*    */   public String getKey() {
/* 49 */     return this.key;
/*    */   }
/*    */ 
/*    */   public String getLocalizedMessage() {
/* 53 */     return getLocalizedMessage(Locale.getDefault());
/*    */   }
/*    */ 
/*    */   public String getLocalizedMessage(Locale locale) {
/* 57 */     return NLS.getLocalizedMessage(getKey(), locale, getArguments());
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 62 */     Object[] args = getArguments();
/* 63 */     StringBuilder sb = new StringBuilder(getKey());
/* 64 */     if (args != null) {
/* 65 */       for (int i = 0; i < args.length; i++) {
/* 66 */         sb.append(i == 0 ? " " : ", ").append(args[i]);
/*    */       }
/*    */     }
/* 69 */     return sb.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.messages.MessageImpl
 * JD-Core Version:    0.6.0
 */