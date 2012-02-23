/*    */ package org.apache.lucene.analysis;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*    */ 
/*    */ @Deprecated
/*    */ public final class ISOLatin1AccentFilter extends TokenFilter
/*    */ {
/* 40 */   private char[] output = new char[256];
/*    */   private int outputPos;
/* 42 */   private final CharTermAttribute termAtt = (CharTermAttribute)addAttribute(CharTermAttribute.class);
/*    */ 
/*    */   public ISOLatin1AccentFilter(TokenStream input)
/*    */   {
/* 37 */     super(input);
/*    */   }
/*    */ 
/*    */   public final boolean incrementToken()
/*    */     throws IOException
/*    */   {
/* 46 */     if (this.input.incrementToken()) {
/* 47 */       char[] buffer = this.termAtt.buffer();
/* 48 */       int length = this.termAtt.length();
/*    */ 
/* 51 */       for (int i = 0; i < length; i++) {
/* 52 */         char c = buffer[i];
/* 53 */         if ((c >= 'À') && (c <= 64262)) {
/* 54 */           removeAccents(buffer, length);
/* 55 */           this.termAtt.copyBuffer(this.output, 0, this.outputPos);
/* 56 */           break;
/*    */         }
/*    */       }
/* 59 */       return true;
/*    */     }
/* 61 */     return false;
/*    */   }
/*    */ 
/*    */   public final void removeAccents(char[] input, int length)
/*    */   {
/* 70 */     int maxSizeNeeded = 2 * length;
/*    */ 
/* 72 */     int size = this.output.length;
/* 73 */     while (size < maxSizeNeeded) {
/* 74 */       size *= 2;
/*    */     }
/* 76 */     if (size != this.output.length) {
/* 77 */       this.output = new char[size];
/*    */     }
/* 79 */     this.outputPos = 0;
/*    */ 
/* 81 */     int pos = 0;
/*    */ 
/* 83 */     for (int i = 0; i < length; pos++) {
/* 84 */       char c = input[pos];
/*    */ 
/* 88 */       if ((c < 'À') || (c > 64262))
/* 89 */         this.output[(this.outputPos++)] = c;
/*    */       else
/* 91 */         switch (c) {
/*    */         case 'À':
/*    */         case 'Á':
/*    */         case 'Â':
/*    */         case 'Ã':
/*    */         case 'Ä':
/*    */         case 'Å':
/* 98 */           this.output[(this.outputPos++)] = 'A';
/* 99 */           break;
/*    */         case 'Æ':
/* 101 */           this.output[(this.outputPos++)] = 'A';
/* 102 */           this.output[(this.outputPos++)] = 'E';
/* 103 */           break;
/*    */         case 'Ç':
/* 105 */           this.output[(this.outputPos++)] = 'C';
/* 106 */           break;
/*    */         case 'È':
/*    */         case 'É':
/*    */         case 'Ê':
/*    */         case 'Ë':
/* 111 */           this.output[(this.outputPos++)] = 'E';
/* 112 */           break;
/*    */         case 'Ì':
/*    */         case 'Í':
/*    */         case 'Î':
/*    */         case 'Ï':
/* 117 */           this.output[(this.outputPos++)] = 'I';
/* 118 */           break;
/*    */         case 'Ĳ':
/* 120 */           this.output[(this.outputPos++)] = 'I';
/* 121 */           this.output[(this.outputPos++)] = 'J';
/* 122 */           break;
/*    */         case 'Ð':
/* 124 */           this.output[(this.outputPos++)] = 'D';
/* 125 */           break;
/*    */         case 'Ñ':
/* 127 */           this.output[(this.outputPos++)] = 'N';
/* 128 */           break;
/*    */         case 'Ò':
/*    */         case 'Ó':
/*    */         case 'Ô':
/*    */         case 'Õ':
/*    */         case 'Ö':
/*    */         case 'Ø':
/* 135 */           this.output[(this.outputPos++)] = 'O';
/* 136 */           break;
/*    */         case 'Œ':
/* 138 */           this.output[(this.outputPos++)] = 'O';
/* 139 */           this.output[(this.outputPos++)] = 'E';
/* 140 */           break;
/*    */         case 'Þ':
/* 142 */           this.output[(this.outputPos++)] = 'T';
/* 143 */           this.output[(this.outputPos++)] = 'H';
/* 144 */           break;
/*    */         case 'Ù':
/*    */         case 'Ú':
/*    */         case 'Û':
/*    */         case 'Ü':
/* 149 */           this.output[(this.outputPos++)] = 'U';
/* 150 */           break;
/*    */         case 'Ý':
/*    */         case 'Ÿ':
/* 153 */           this.output[(this.outputPos++)] = 'Y';
/* 154 */           break;
/*    */         case 'à':
/*    */         case 'á':
/*    */         case 'â':
/*    */         case 'ã':
/*    */         case 'ä':
/*    */         case 'å':
/* 161 */           this.output[(this.outputPos++)] = 'a';
/* 162 */           break;
/*    */         case 'æ':
/* 164 */           this.output[(this.outputPos++)] = 'a';
/* 165 */           this.output[(this.outputPos++)] = 'e';
/* 166 */           break;
/*    */         case 'ç':
/* 168 */           this.output[(this.outputPos++)] = 'c';
/* 169 */           break;
/*    */         case 'è':
/*    */         case 'é':
/*    */         case 'ê':
/*    */         case 'ë':
/* 174 */           this.output[(this.outputPos++)] = 'e';
/* 175 */           break;
/*    */         case 'ì':
/*    */         case 'í':
/*    */         case 'î':
/*    */         case 'ï':
/* 180 */           this.output[(this.outputPos++)] = 'i';
/* 181 */           break;
/*    */         case 'ĳ':
/* 183 */           this.output[(this.outputPos++)] = 'i';
/* 184 */           this.output[(this.outputPos++)] = 'j';
/* 185 */           break;
/*    */         case 'ð':
/* 187 */           this.output[(this.outputPos++)] = 'd';
/* 188 */           break;
/*    */         case 'ñ':
/* 190 */           this.output[(this.outputPos++)] = 'n';
/* 191 */           break;
/*    */         case 'ò':
/*    */         case 'ó':
/*    */         case 'ô':
/*    */         case 'õ':
/*    */         case 'ö':
/*    */         case 'ø':
/* 198 */           this.output[(this.outputPos++)] = 'o';
/* 199 */           break;
/*    */         case 'œ':
/* 201 */           this.output[(this.outputPos++)] = 'o';
/* 202 */           this.output[(this.outputPos++)] = 'e';
/* 203 */           break;
/*    */         case 'ß':
/* 205 */           this.output[(this.outputPos++)] = 's';
/* 206 */           this.output[(this.outputPos++)] = 's';
/* 207 */           break;
/*    */         case 'þ':
/* 209 */           this.output[(this.outputPos++)] = 't';
/* 210 */           this.output[(this.outputPos++)] = 'h';
/* 211 */           break;
/*    */         case 'ù':
/*    */         case 'ú':
/*    */         case 'û':
/*    */         case 'ü':
/* 216 */           this.output[(this.outputPos++)] = 'u';
/* 217 */           break;
/*    */         case 'ý':
/*    */         case 'ÿ':
/* 220 */           this.output[(this.outputPos++)] = 'y';
/* 221 */           break;
/*    */         case 'ﬀ':
/* 223 */           this.output[(this.outputPos++)] = 'f';
/* 224 */           this.output[(this.outputPos++)] = 'f';
/* 225 */           break;
/*    */         case 'ﬁ':
/* 227 */           this.output[(this.outputPos++)] = 'f';
/* 228 */           this.output[(this.outputPos++)] = 'i';
/* 229 */           break;
/*    */         case 'ﬂ':
/* 231 */           this.output[(this.outputPos++)] = 'f';
/* 232 */           this.output[(this.outputPos++)] = 'l';
/* 233 */           break;
/*    */         case 'ﬅ':
/* 246 */           this.output[(this.outputPos++)] = 'f';
/* 247 */           this.output[(this.outputPos++)] = 't';
/* 248 */           break;
/*    */         case 'ﬆ':
/* 250 */           this.output[(this.outputPos++)] = 's';
/* 251 */           this.output[(this.outputPos++)] = 't';
/* 252 */           break;
/*    */         default:
/* 254 */           this.output[(this.outputPos++)] = c;
/*    */         }
/* 83 */       i++;
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.ISOLatin1AccentFilter
 * JD-Core Version:    0.6.0
 */