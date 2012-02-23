/*     */ package org.eclipse.jdt.internal.compiler.env;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ 
/*     */ public class AccessRuleSet
/*     */ {
/*     */   private AccessRule[] accessRules;
/*     */   public byte classpathEntryType;
/*     */   public String classpathEntryName;
/*     */ 
/*     */   public AccessRuleSet(AccessRule[] accessRules, byte classpathEntryType, String classpathEntryName)
/*     */   {
/*  34 */     this.accessRules = accessRules;
/*  35 */     this.classpathEntryType = classpathEntryType;
/*  36 */     this.classpathEntryName = classpathEntryName;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object object)
/*     */   {
/*  43 */     if (this == object)
/*  44 */       return true;
/*  45 */     if (!(object instanceof AccessRuleSet))
/*  46 */       return false;
/*  47 */     AccessRuleSet otherRuleSet = (AccessRuleSet)object;
/*  48 */     if ((this.classpathEntryType != otherRuleSet.classpathEntryType) || 
/*  49 */       ((this.classpathEntryName == null) && (otherRuleSet.classpathEntryName != null)) || 
/*  50 */       (!this.classpathEntryName.equals(otherRuleSet.classpathEntryName))) {
/*  51 */       return false;
/*     */     }
/*  53 */     int rulesLength = this.accessRules.length;
/*  54 */     if (rulesLength != otherRuleSet.accessRules.length) return false;
/*  55 */     for (int i = 0; i < rulesLength; i++)
/*  56 */       if (!this.accessRules[i].equals(otherRuleSet.accessRules[i]))
/*  57 */         return false;
/*  58 */     return true;
/*     */   }
/*     */ 
/*     */   public AccessRule[] getAccessRules() {
/*  62 */     return this.accessRules;
/*     */   }
/*     */ 
/*     */   public AccessRestriction getViolatedRestriction(char[] targetTypeFilePath)
/*     */   {
/*  73 */     int i = 0; for (int length = this.accessRules.length; i < length; i++) {
/*  74 */       AccessRule accessRule = this.accessRules[i];
/*  75 */       if (!CharOperation.pathMatch(accessRule.pattern, targetTypeFilePath, 
/*  76 */         true, '/')) continue;
/*  77 */       switch (accessRule.getProblemId()) {
/*     */       case 16777496:
/*     */       case 16777523:
/*  80 */         return new AccessRestriction(accessRule, this.classpathEntryType, this.classpathEntryName);
/*     */       }
/*  82 */       return null;
/*     */     }
/*     */ 
/*  86 */     return null;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  91 */     int result = 1;
/*  92 */     result = 31 * result + hashCode(this.accessRules);
/*  93 */     result = 31 * result + (this.classpathEntryName == null ? 0 : this.classpathEntryName.hashCode());
/*  94 */     result = 31 * result + this.classpathEntryType;
/*  95 */     return result;
/*     */   }
/*     */ 
/*     */   private int hashCode(AccessRule[] rules)
/*     */   {
/* 100 */     if (rules == null)
/* 101 */       return 0;
/* 102 */     int result = 1;
/* 103 */     int i = 0; for (int length = rules.length; i < length; i++) {
/* 104 */       result = 31 * result + (rules[i] == null ? 0 : rules[i].hashCode());
/*     */     }
/* 106 */     return result;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 110 */     return toString(true);
/*     */   }
/*     */ 
/*     */   public String toString(boolean wrap) {
/* 114 */     StringBuffer buffer = new StringBuffer(200);
/* 115 */     buffer.append("AccessRuleSet {");
/* 116 */     if (wrap)
/* 117 */       buffer.append('\n');
/* 118 */     int i = 0; for (int length = this.accessRules.length; i < length; i++) {
/* 119 */       if (wrap)
/* 120 */         buffer.append('\t');
/* 121 */       AccessRule accessRule = this.accessRules[i];
/* 122 */       buffer.append(accessRule);
/* 123 */       if (wrap)
/* 124 */         buffer.append('\n');
/* 125 */       else if (i < length - 1)
/* 126 */         buffer.append(", ");
/*     */     }
/* 128 */     buffer.append("} [classpath entry: ");
/* 129 */     buffer.append(this.classpathEntryName);
/* 130 */     buffer.append("]");
/* 131 */     return buffer.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.env.AccessRuleSet
 * JD-Core Version:    0.6.0
 */