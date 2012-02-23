/*     */ package com.dukascopy.dds2.greed.agent.compiler;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.agent.protocol.JFXStreamHandler;
/*     */ import com.dukascopy.dds2.greed.util.FilePathManager;
/*     */ import java.io.File;
/*     */ import java.io.FilePermission;
/*     */ import java.io.IOException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.security.AllPermission;
/*     */ import java.security.CodeSource;
/*     */ import java.security.PermissionCollection;
/*     */ import java.security.Permissions;
/*     */ import java.security.SecureClassLoader;
/*     */ import java.security.cert.Certificate;
/*     */ import java.util.Enumeration;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class JFXClassLoader extends SecureClassLoader
/*     */ {
/*  24 */   private static final Logger LOGGER = LoggerFactory.getLogger(JFXClassLoader.class);
/*     */   public static final String PROPERTY_R_ACTION = "read";
/*     */   public static final String PROPERTY_RWD_ACTION = "read,write,delete";
/*  29 */   private JFXPack pack = null;
/*     */   private CodeSource codeSource;
/*     */   private boolean fullAccess;
/*     */ 
/*     */   public JFXClassLoader(ClassLoader parent, JFXPack pack)
/*     */   {
/*  35 */     super(parent);
/*  36 */     this.pack = pack;
/*     */     try {
/*  38 */       this.codeSource = new CodeSource(new URL("http://www.dukascopy.com/JFOREX"), (Certificate[])null);
/*     */     } catch (MalformedURLException e) {
/*  40 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setFullAccess(boolean fullAccess) {
/*  45 */     this.fullAccess = fullAccess;
/*     */   }
/*     */ 
/*     */   public boolean isFullAccess() {
/*  49 */     return this.fullAccess;
/*     */   }
/*     */ 
/*     */   public Class<?> loadClass(String name) throws ClassNotFoundException
/*     */   {
/*  54 */     return super.loadClass(name);
/*     */   }
/*     */ 
/*     */   public Class<?> findClass(String name)
/*     */     throws ClassNotFoundException
/*     */   {
/*  60 */     if ((name != null) && (this.pack.className != null)) {
/*  61 */       for (int i = 0; i < this.pack.className.length; i++) {
/*  62 */         if ((this.pack.className[i] != null) && (name.endsWith(this.pack.className[i]))) {
/*  63 */           byte[] b = this.pack.classCode[i];
/*     */           try {
/*  65 */             return defineClass(name, b, 0, b.length, this.codeSource);
/*     */           } catch (Throwable t) {
/*  67 */             LOGGER.error(t.getMessage(), t);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*  72 */     return super.findClass(name);
/*     */   }
/*     */ 
/*     */   protected URL findResource(String name)
/*     */   {
/*  77 */     if (findResourceAsBytes(name) != null) {
/*     */       try {
/*  79 */         return new URL("jfx", name, -1, "", new JFXStreamHandler(this));
/*     */       } catch (MalformedURLException e) {
/*  81 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*  84 */     return super.findResource(name);
/*     */   }
/*     */ 
/*     */   protected Enumeration<URL> findResources(String name) throws IOException
/*     */   {
/*  89 */     URL url = findResource(name);
/*  90 */     Enumeration enumeration = new Enumeration(url) {
/*  91 */       boolean hasMoreElements = this.val$url != null;
/*     */ 
/*     */       public boolean hasMoreElements()
/*     */       {
/*  95 */         return this.hasMoreElements;
/*     */       }
/*     */ 
/*     */       public URL nextElement() {
/*  99 */         this.hasMoreElements = false;
/* 100 */         return this.val$url;
/*     */       }
/*     */     };
/* 103 */     return enumeration;
/*     */   }
/*     */ 
/*     */   public byte[] findResourceAsBytes(String name) {
/* 107 */     if ((name != null) && (this.pack.resourceName != null)) {
/* 108 */       for (int i = 0; i < this.pack.resourceName.length; i++) {
/* 109 */         if ((this.pack.resourceName[i] != null) && (name.equals(this.pack.resourceName[i]))) {
/* 110 */           byte[] b = this.pack.resourceCode[i];
/*     */           try {
/* 112 */             return b;
/*     */           } catch (Throwable t) {
/* 114 */             LOGGER.error(t.getMessage(), t);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 119 */     return null;
/*     */   }
/*     */ 
/*     */   public Enumeration<URL> getResources(String name)
/*     */     throws IOException
/*     */   {
/* 125 */     return super.getResources(name);
/*     */   }
/*     */ 
/*     */   public String findName(byte[] bbb) {
/* 129 */     String rc = null;
/*     */     try {
/* 131 */       defineClass("", bbb, 0, bbb.length);
/*     */     } catch (NoClassDefFoundError ncdfe) {
/* 133 */       String msg = ncdfe.getMessage();
/* 134 */       Pattern pattern = Pattern.compile("\\(wrong name: (.*?)\\)");
/* 135 */       Matcher matcher = pattern.matcher(msg);
/* 136 */       String goodName = null;
/* 137 */       if (matcher.find()) {
/* 138 */         goodName = matcher.group(1);
/*     */       }
/* 140 */       if (goodName != null)
/* 141 */         rc = goodName.replace('/', '.');
/*     */     }
/*     */     catch (Throwable t) {
/* 144 */       LOGGER.error(t.getMessage(), t);
/*     */     }
/* 146 */     return rc;
/*     */   }
/*     */ 
/*     */   protected PermissionCollection getPermissions(CodeSource codesource)
/*     */   {
/* 152 */     if ((this.codeSource == codesource) && (this.fullAccess)) {
/* 153 */       Permissions permissions = new Permissions();
/* 154 */       permissions.add(new AllPermission());
/* 155 */       return permissions;
/*     */     }
/* 157 */     PermissionCollection permissions = super.getPermissions(codesource);
/* 158 */     if (permissions == null) {
/* 159 */       permissions = new Permissions();
/*     */     }
/* 161 */     permissions.add(new FilePermission(FilePathManager.getInstance().getFilesForStrategiesDir().getPath(), "read"));
/* 162 */     permissions.add(new FilePermission(FilePathManager.getInstance().getFilesForStrategiesDir().getPath() + File.separator + "-", "read,write,delete"));
/* 163 */     return permissions;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.compiler.JFXClassLoader
 * JD-Core Version:    0.6.0
 */