/*      */ package org.apache.lucene.analysis;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*      */ import org.apache.lucene.util.ArrayUtil;
/*      */ 
/*      */ public final class ASCIIFoldingFilter extends TokenFilter
/*      */ {
/*   66 */   private char[] output = new char[512];
/*      */   private int outputPos;
/*   68 */   private final CharTermAttribute termAtt = (CharTermAttribute)addAttribute(CharTermAttribute.class);
/*      */ 
/*      */   public ASCIIFoldingFilter(TokenStream input)
/*      */   {
/*   63 */     super(input);
/*      */   }
/*      */ 
/*      */   public boolean incrementToken()
/*      */     throws IOException
/*      */   {
/*   72 */     if (this.input.incrementToken()) {
/*   73 */       char[] buffer = this.termAtt.buffer();
/*   74 */       int length = this.termAtt.length();
/*      */ 
/*   78 */       for (int i = 0; i < length; i++) {
/*   79 */         char c = buffer[i];
/*   80 */         if (c < '')
/*      */           continue;
/*   82 */         foldToASCII(buffer, length);
/*   83 */         this.termAtt.copyBuffer(this.output, 0, this.outputPos);
/*   84 */         break;
/*      */       }
/*      */ 
/*   87 */       return true;
/*      */     }
/*   89 */     return false;
/*      */   }
/*      */ 
/*      */   public void foldToASCII(char[] input, int length)
/*      */   {
/*  102 */     int maxSizeNeeded = 4 * length;
/*  103 */     if (this.output.length < maxSizeNeeded) {
/*  104 */       this.output = new char[ArrayUtil.oversize(maxSizeNeeded, 2)];
/*      */     }
/*      */ 
/*  107 */     this.outputPos = foldToASCII(input, 0, this.output, 0, length);
/*      */   }
/*      */ 
/*      */   public static final int foldToASCII(char[] input, int inputPos, char[] output, int outputPos, int length)
/*      */   {
/*  123 */     int end = inputPos + length;
/*  124 */     for (int pos = inputPos; pos < end; pos++) {
/*  125 */       char c = input[pos];
/*      */ 
/*  128 */       if (c < '')
/*  129 */         output[(outputPos++)] = c;
/*      */       else {
/*  131 */         switch (c) {
/*      */         case 'À':
/*      */         case 'Á':
/*      */         case 'Â':
/*      */         case 'Ã':
/*      */         case 'Ä':
/*      */         case 'Å':
/*      */         case 'Ā':
/*      */         case 'Ă':
/*      */         case 'Ą':
/*      */         case 'Ə':
/*      */         case 'Ǎ':
/*      */         case 'Ǟ':
/*      */         case 'Ǡ':
/*      */         case 'Ǻ':
/*      */         case 'Ȁ':
/*      */         case 'Ȃ':
/*      */         case 'Ȧ':
/*      */         case 'Ⱥ':
/*      */         case 'ᴀ':
/*      */         case 'Ḁ':
/*      */         case 'Ạ':
/*      */         case 'Ả':
/*      */         case 'Ấ':
/*      */         case 'Ầ':
/*      */         case 'Ẩ':
/*      */         case 'Ẫ':
/*      */         case 'Ậ':
/*      */         case 'Ắ':
/*      */         case 'Ằ':
/*      */         case 'Ẳ':
/*      */         case 'Ẵ':
/*      */         case 'Ặ':
/*      */         case 'Ⓐ':
/*      */         case 'Ａ':
/*  166 */           output[(outputPos++)] = 'A';
/*  167 */           break;
/*      */         case 'à':
/*      */         case 'á':
/*      */         case 'â':
/*      */         case 'ã':
/*      */         case 'ä':
/*      */         case 'å':
/*      */         case 'ā':
/*      */         case 'ă':
/*      */         case 'ą':
/*      */         case 'ǎ':
/*      */         case 'ǟ':
/*      */         case 'ǡ':
/*      */         case 'ǻ':
/*      */         case 'ȁ':
/*      */         case 'ȃ':
/*      */         case 'ȧ':
/*      */         case 'ɐ':
/*      */         case 'ə':
/*      */         case 'ɚ':
/*      */         case 'ᶏ':
/*      */         case 'ᶕ':
/*      */         case 'ḁ':
/*      */         case 'ẚ':
/*      */         case 'ạ':
/*      */         case 'ả':
/*      */         case 'ấ':
/*      */         case 'ầ':
/*      */         case 'ẩ':
/*      */         case 'ẫ':
/*      */         case 'ậ':
/*      */         case 'ắ':
/*      */         case 'ằ':
/*      */         case 'ẳ':
/*      */         case 'ẵ':
/*      */         case 'ặ':
/*      */         case 'ₐ':
/*      */         case 'ₔ':
/*      */         case 'ⓐ':
/*      */         case 'ⱥ':
/*      */         case 'Ɐ':
/*      */         case 'ａ':
/*  209 */           output[(outputPos++)] = 'a';
/*  210 */           break;
/*      */         case 'Ꜳ':
/*  212 */           output[(outputPos++)] = 'A';
/*  213 */           output[(outputPos++)] = 'A';
/*  214 */           break;
/*      */         case 'Æ':
/*      */         case 'Ǣ':
/*      */         case 'Ǽ':
/*      */         case 'ᴁ':
/*  219 */           output[(outputPos++)] = 'A';
/*  220 */           output[(outputPos++)] = 'E';
/*  221 */           break;
/*      */         case 'Ꜵ':
/*  223 */           output[(outputPos++)] = 'A';
/*  224 */           output[(outputPos++)] = 'O';
/*  225 */           break;
/*      */         case 'Ꜷ':
/*  227 */           output[(outputPos++)] = 'A';
/*  228 */           output[(outputPos++)] = 'U';
/*  229 */           break;
/*      */         case 'Ꜹ':
/*      */         case 'Ꜻ':
/*  232 */           output[(outputPos++)] = 'A';
/*  233 */           output[(outputPos++)] = 'V';
/*  234 */           break;
/*      */         case 'Ꜽ':
/*  236 */           output[(outputPos++)] = 'A';
/*  237 */           output[(outputPos++)] = 'Y';
/*  238 */           break;
/*      */         case '⒜':
/*  240 */           output[(outputPos++)] = '(';
/*  241 */           output[(outputPos++)] = 'a';
/*  242 */           output[(outputPos++)] = ')';
/*  243 */           break;
/*      */         case 'ꜳ':
/*  245 */           output[(outputPos++)] = 'a';
/*  246 */           output[(outputPos++)] = 'a';
/*  247 */           break;
/*      */         case 'æ':
/*      */         case 'ǣ':
/*      */         case 'ǽ':
/*      */         case 'ᴂ':
/*  252 */           output[(outputPos++)] = 'a';
/*  253 */           output[(outputPos++)] = 'e';
/*  254 */           break;
/*      */         case 'ꜵ':
/*  256 */           output[(outputPos++)] = 'a';
/*  257 */           output[(outputPos++)] = 'o';
/*  258 */           break;
/*      */         case 'ꜷ':
/*  260 */           output[(outputPos++)] = 'a';
/*  261 */           output[(outputPos++)] = 'u';
/*  262 */           break;
/*      */         case 'ꜹ':
/*      */         case 'ꜻ':
/*  265 */           output[(outputPos++)] = 'a';
/*  266 */           output[(outputPos++)] = 'v';
/*  267 */           break;
/*      */         case 'ꜽ':
/*  269 */           output[(outputPos++)] = 'a';
/*  270 */           output[(outputPos++)] = 'y';
/*  271 */           break;
/*      */         case 'Ɓ':
/*      */         case 'Ƃ':
/*      */         case 'Ƀ':
/*      */         case 'ʙ':
/*      */         case 'ᴃ':
/*      */         case 'Ḃ':
/*      */         case 'Ḅ':
/*      */         case 'Ḇ':
/*      */         case 'Ⓑ':
/*      */         case 'Ｂ':
/*  282 */           output[(outputPos++)] = 'B';
/*  283 */           break;
/*      */         case 'ƀ':
/*      */         case 'ƃ':
/*      */         case 'ɓ':
/*      */         case 'ᵬ':
/*      */         case 'ᶀ':
/*      */         case 'ḃ':
/*      */         case 'ḅ':
/*      */         case 'ḇ':
/*      */         case 'ⓑ':
/*      */         case 'ｂ':
/*  294 */           output[(outputPos++)] = 'b';
/*  295 */           break;
/*      */         case '⒝':
/*  297 */           output[(outputPos++)] = '(';
/*  298 */           output[(outputPos++)] = 'b';
/*  299 */           output[(outputPos++)] = ')';
/*  300 */           break;
/*      */         case 'Ç':
/*      */         case 'Ć':
/*      */         case 'Ĉ':
/*      */         case 'Ċ':
/*      */         case 'Č':
/*      */         case 'Ƈ':
/*      */         case 'Ȼ':
/*      */         case 'ʗ':
/*      */         case 'ᴄ':
/*      */         case 'Ḉ':
/*      */         case 'Ⓒ':
/*      */         case 'Ｃ':
/*  313 */           output[(outputPos++)] = 'C';
/*  314 */           break;
/*      */         case 'ç':
/*      */         case 'ć':
/*      */         case 'ĉ':
/*      */         case 'ċ':
/*      */         case 'č':
/*      */         case 'ƈ':
/*      */         case 'ȼ':
/*      */         case 'ɕ':
/*      */         case 'ḉ':
/*      */         case 'ↄ':
/*      */         case 'ⓒ':
/*      */         case 'Ꜿ':
/*      */         case 'ꜿ':
/*      */         case 'ｃ':
/*  329 */           output[(outputPos++)] = 'c';
/*  330 */           break;
/*      */         case '⒞':
/*  332 */           output[(outputPos++)] = '(';
/*  333 */           output[(outputPos++)] = 'c';
/*  334 */           output[(outputPos++)] = ')';
/*  335 */           break;
/*      */         case 'Ð':
/*      */         case 'Ď':
/*      */         case 'Đ':
/*      */         case 'Ɖ':
/*      */         case 'Ɗ':
/*      */         case 'Ƌ':
/*      */         case 'ᴅ':
/*      */         case 'ᴆ':
/*      */         case 'Ḋ':
/*      */         case 'Ḍ':
/*      */         case 'Ḏ':
/*      */         case 'Ḑ':
/*      */         case 'Ḓ':
/*      */         case 'Ⓓ':
/*      */         case 'Ꝺ':
/*      */         case 'Ｄ':
/*  352 */           output[(outputPos++)] = 'D';
/*  353 */           break;
/*      */         case 'ð':
/*      */         case 'ď':
/*      */         case 'đ':
/*      */         case 'ƌ':
/*      */         case 'ȡ':
/*      */         case 'ɖ':
/*      */         case 'ɗ':
/*      */         case 'ᵭ':
/*      */         case 'ᶁ':
/*      */         case 'ᶑ':
/*      */         case 'ḋ':
/*      */         case 'ḍ':
/*      */         case 'ḏ':
/*      */         case 'ḑ':
/*      */         case 'ḓ':
/*      */         case 'ⓓ':
/*      */         case 'ꝺ':
/*      */         case 'ｄ':
/*  372 */           output[(outputPos++)] = 'd';
/*  373 */           break;
/*      */         case 'Ǆ':
/*      */         case 'Ǳ':
/*  376 */           output[(outputPos++)] = 'D';
/*  377 */           output[(outputPos++)] = 'Z';
/*  378 */           break;
/*      */         case 'ǅ':
/*      */         case 'ǲ':
/*  381 */           output[(outputPos++)] = 'D';
/*  382 */           output[(outputPos++)] = 'z';
/*  383 */           break;
/*      */         case '⒟':
/*  385 */           output[(outputPos++)] = '(';
/*  386 */           output[(outputPos++)] = 'd';
/*  387 */           output[(outputPos++)] = ')';
/*  388 */           break;
/*      */         case 'ȸ':
/*  390 */           output[(outputPos++)] = 'd';
/*  391 */           output[(outputPos++)] = 'b';
/*  392 */           break;
/*      */         case 'ǆ':
/*      */         case 'ǳ':
/*      */         case 'ʣ':
/*      */         case 'ʥ':
/*  397 */           output[(outputPos++)] = 'd';
/*  398 */           output[(outputPos++)] = 'z';
/*  399 */           break;
/*      */         case 'È':
/*      */         case 'É':
/*      */         case 'Ê':
/*      */         case 'Ë':
/*      */         case 'Ē':
/*      */         case 'Ĕ':
/*      */         case 'Ė':
/*      */         case 'Ę':
/*      */         case 'Ě':
/*      */         case 'Ǝ':
/*      */         case 'Ɛ':
/*      */         case 'Ȅ':
/*      */         case 'Ȇ':
/*      */         case 'Ȩ':
/*      */         case 'Ɇ':
/*      */         case 'ᴇ':
/*      */         case 'Ḕ':
/*      */         case 'Ḗ':
/*      */         case 'Ḙ':
/*      */         case 'Ḛ':
/*      */         case 'Ḝ':
/*      */         case 'Ẹ':
/*      */         case 'Ẻ':
/*      */         case 'Ẽ':
/*      */         case 'Ế':
/*      */         case 'Ề':
/*      */         case 'Ể':
/*      */         case 'Ễ':
/*      */         case 'Ệ':
/*      */         case 'Ⓔ':
/*      */         case 'ⱻ':
/*      */         case 'Ｅ':
/*  432 */           output[(outputPos++)] = 'E';
/*  433 */           break;
/*      */         case 'è':
/*      */         case 'é':
/*      */         case 'ê':
/*      */         case 'ë':
/*      */         case 'ē':
/*      */         case 'ĕ':
/*      */         case 'ė':
/*      */         case 'ę':
/*      */         case 'ě':
/*      */         case 'ǝ':
/*      */         case 'ȅ':
/*      */         case 'ȇ':
/*      */         case 'ȩ':
/*      */         case 'ɇ':
/*      */         case 'ɘ':
/*      */         case 'ɛ':
/*      */         case 'ɜ':
/*      */         case 'ɝ':
/*      */         case 'ɞ':
/*      */         case 'ʚ':
/*      */         case 'ᴈ':
/*      */         case 'ᶒ':
/*      */         case 'ᶓ':
/*      */         case 'ᶔ':
/*      */         case 'ḕ':
/*      */         case 'ḗ':
/*      */         case 'ḙ':
/*      */         case 'ḛ':
/*      */         case 'ḝ':
/*      */         case 'ẹ':
/*      */         case 'ẻ':
/*      */         case 'ẽ':
/*      */         case 'ế':
/*      */         case 'ề':
/*      */         case 'ể':
/*      */         case 'ễ':
/*      */         case 'ệ':
/*      */         case 'ₑ':
/*      */         case 'ⓔ':
/*      */         case 'ⱸ':
/*      */         case 'ｅ':
/*  475 */           output[(outputPos++)] = 'e';
/*  476 */           break;
/*      */         case '⒠':
/*  478 */           output[(outputPos++)] = '(';
/*  479 */           output[(outputPos++)] = 'e';
/*  480 */           output[(outputPos++)] = ')';
/*  481 */           break;
/*      */         case 'Ƒ':
/*      */         case 'Ḟ':
/*      */         case 'Ⓕ':
/*      */         case 'ꜰ':
/*      */         case 'Ꝼ':
/*      */         case 'ꟻ':
/*      */         case 'Ｆ':
/*  489 */           output[(outputPos++)] = 'F';
/*  490 */           break;
/*      */         case 'ƒ':
/*      */         case 'ᵮ':
/*      */         case 'ᶂ':
/*      */         case 'ḟ':
/*      */         case 'ẛ':
/*      */         case 'ⓕ':
/*      */         case 'ꝼ':
/*      */         case 'ｆ':
/*  499 */           output[(outputPos++)] = 'f';
/*  500 */           break;
/*      */         case '⒡':
/*  502 */           output[(outputPos++)] = '(';
/*  503 */           output[(outputPos++)] = 'f';
/*  504 */           output[(outputPos++)] = ')';
/*  505 */           break;
/*      */         case 'ﬀ':
/*  507 */           output[(outputPos++)] = 'f';
/*  508 */           output[(outputPos++)] = 'f';
/*  509 */           break;
/*      */         case 'ﬃ':
/*  511 */           output[(outputPos++)] = 'f';
/*  512 */           output[(outputPos++)] = 'f';
/*  513 */           output[(outputPos++)] = 'i';
/*  514 */           break;
/*      */         case 'ﬄ':
/*  516 */           output[(outputPos++)] = 'f';
/*  517 */           output[(outputPos++)] = 'f';
/*  518 */           output[(outputPos++)] = 'l';
/*  519 */           break;
/*      */         case 'ﬁ':
/*  521 */           output[(outputPos++)] = 'f';
/*  522 */           output[(outputPos++)] = 'i';
/*  523 */           break;
/*      */         case 'ﬂ':
/*  525 */           output[(outputPos++)] = 'f';
/*  526 */           output[(outputPos++)] = 'l';
/*  527 */           break;
/*      */         case 'Ĝ':
/*      */         case 'Ğ':
/*      */         case 'Ġ':
/*      */         case 'Ģ':
/*      */         case 'Ɠ':
/*      */         case 'Ǥ':
/*      */         case 'ǥ':
/*      */         case 'Ǧ':
/*      */         case 'ǧ':
/*      */         case 'Ǵ':
/*      */         case 'ɢ':
/*      */         case 'ʛ':
/*      */         case 'Ḡ':
/*      */         case 'Ⓖ':
/*      */         case 'Ᵹ':
/*      */         case 'Ꝿ':
/*      */         case 'Ｇ':
/*  545 */           output[(outputPos++)] = 'G';
/*  546 */           break;
/*      */         case 'ĝ':
/*      */         case 'ğ':
/*      */         case 'ġ':
/*      */         case 'ģ':
/*      */         case 'ǵ':
/*      */         case 'ɠ':
/*      */         case 'ɡ':
/*      */         case 'ᵷ':
/*      */         case 'ᵹ':
/*      */         case 'ᶃ':
/*      */         case 'ḡ':
/*      */         case 'ⓖ':
/*      */         case 'ꝿ':
/*      */         case 'ｇ':
/*  561 */           output[(outputPos++)] = 'g';
/*  562 */           break;
/*      */         case '⒢':
/*  564 */           output[(outputPos++)] = '(';
/*  565 */           output[(outputPos++)] = 'g';
/*  566 */           output[(outputPos++)] = ')';
/*  567 */           break;
/*      */         case 'Ĥ':
/*      */         case 'Ħ':
/*      */         case 'Ȟ':
/*      */         case 'ʜ':
/*      */         case 'Ḣ':
/*      */         case 'Ḥ':
/*      */         case 'Ḧ':
/*      */         case 'Ḩ':
/*      */         case 'Ḫ':
/*      */         case 'Ⓗ':
/*      */         case 'Ⱨ':
/*      */         case 'Ⱶ':
/*      */         case 'Ｈ':
/*  581 */           output[(outputPos++)] = 'H';
/*  582 */           break;
/*      */         case 'ĥ':
/*      */         case 'ħ':
/*      */         case 'ȟ':
/*      */         case 'ɥ':
/*      */         case 'ɦ':
/*      */         case 'ʮ':
/*      */         case 'ʯ':
/*      */         case 'ḣ':
/*      */         case 'ḥ':
/*      */         case 'ḧ':
/*      */         case 'ḩ':
/*      */         case 'ḫ':
/*      */         case 'ẖ':
/*      */         case 'ⓗ':
/*      */         case 'ⱨ':
/*      */         case 'ⱶ':
/*      */         case 'ｈ':
/*  600 */           output[(outputPos++)] = 'h';
/*  601 */           break;
/*      */         case 'Ƕ':
/*  603 */           output[(outputPos++)] = 'H';
/*  604 */           output[(outputPos++)] = 'V';
/*  605 */           break;
/*      */         case '⒣':
/*  607 */           output[(outputPos++)] = '(';
/*  608 */           output[(outputPos++)] = 'h';
/*  609 */           output[(outputPos++)] = ')';
/*  610 */           break;
/*      */         case 'ƕ':
/*  612 */           output[(outputPos++)] = 'h';
/*  613 */           output[(outputPos++)] = 'v';
/*  614 */           break;
/*      */         case 'Ì':
/*      */         case 'Í':
/*      */         case 'Î':
/*      */         case 'Ï':
/*      */         case 'Ĩ':
/*      */         case 'Ī':
/*      */         case 'Ĭ':
/*      */         case 'Į':
/*      */         case 'İ':
/*      */         case 'Ɩ':
/*      */         case 'Ɨ':
/*      */         case 'Ǐ':
/*      */         case 'Ȉ':
/*      */         case 'Ȋ':
/*      */         case 'ɪ':
/*      */         case 'ᵻ':
/*      */         case 'Ḭ':
/*      */         case 'Ḯ':
/*      */         case 'Ỉ':
/*      */         case 'Ị':
/*      */         case 'Ⓘ':
/*      */         case 'ꟾ':
/*      */         case 'Ｉ':
/*  638 */           output[(outputPos++)] = 'I';
/*  639 */           break;
/*      */         case 'ì':
/*      */         case 'í':
/*      */         case 'î':
/*      */         case 'ï':
/*      */         case 'ĩ':
/*      */         case 'ī':
/*      */         case 'ĭ':
/*      */         case 'į':
/*      */         case 'ı':
/*      */         case 'ǐ':
/*      */         case 'ȉ':
/*      */         case 'ȋ':
/*      */         case 'ɨ':
/*      */         case 'ᴉ':
/*      */         case 'ᵢ':
/*      */         case 'ᵼ':
/*      */         case 'ᶖ':
/*      */         case 'ḭ':
/*      */         case 'ḯ':
/*      */         case 'ỉ':
/*      */         case 'ị':
/*      */         case 'ⁱ':
/*      */         case 'ⓘ':
/*      */         case 'ｉ':
/*  664 */           output[(outputPos++)] = 'i';
/*  665 */           break;
/*      */         case 'Ĳ':
/*  667 */           output[(outputPos++)] = 'I';
/*  668 */           output[(outputPos++)] = 'J';
/*  669 */           break;
/*      */         case '⒤':
/*  671 */           output[(outputPos++)] = '(';
/*  672 */           output[(outputPos++)] = 'i';
/*  673 */           output[(outputPos++)] = ')';
/*  674 */           break;
/*      */         case 'ĳ':
/*  676 */           output[(outputPos++)] = 'i';
/*  677 */           output[(outputPos++)] = 'j';
/*  678 */           break;
/*      */         case 'Ĵ':
/*      */         case 'Ɉ':
/*      */         case 'ᴊ':
/*      */         case 'Ⓙ':
/*      */         case 'Ｊ':
/*  684 */           output[(outputPos++)] = 'J';
/*  685 */           break;
/*      */         case 'ĵ':
/*      */         case 'ǰ':
/*      */         case 'ȷ':
/*      */         case 'ɉ':
/*      */         case 'ɟ':
/*      */         case 'ʄ':
/*      */         case 'ʝ':
/*      */         case 'ⓙ':
/*      */         case 'ⱼ':
/*      */         case 'ｊ':
/*  696 */           output[(outputPos++)] = 'j';
/*  697 */           break;
/*      */         case '⒥':
/*  699 */           output[(outputPos++)] = '(';
/*  700 */           output[(outputPos++)] = 'j';
/*  701 */           output[(outputPos++)] = ')';
/*  702 */           break;
/*      */         case 'Ķ':
/*      */         case 'Ƙ':
/*      */         case 'Ǩ':
/*      */         case 'ᴋ':
/*      */         case 'Ḱ':
/*      */         case 'Ḳ':
/*      */         case 'Ḵ':
/*      */         case 'Ⓚ':
/*      */         case 'Ⱪ':
/*      */         case 'Ꝁ':
/*      */         case 'Ꝃ':
/*      */         case 'Ꝅ':
/*      */         case 'Ｋ':
/*  716 */           output[(outputPos++)] = 'K';
/*  717 */           break;
/*      */         case 'ķ':
/*      */         case 'ƙ':
/*      */         case 'ǩ':
/*      */         case 'ʞ':
/*      */         case 'ᶄ':
/*      */         case 'ḱ':
/*      */         case 'ḳ':
/*      */         case 'ḵ':
/*      */         case 'ⓚ':
/*      */         case 'ⱪ':
/*      */         case 'ꝁ':
/*      */         case 'ꝃ':
/*      */         case 'ꝅ':
/*      */         case 'ｋ':
/*  732 */           output[(outputPos++)] = 'k';
/*  733 */           break;
/*      */         case '⒦':
/*  735 */           output[(outputPos++)] = '(';
/*  736 */           output[(outputPos++)] = 'k';
/*  737 */           output[(outputPos++)] = ')';
/*  738 */           break;
/*      */         case 'Ĺ':
/*      */         case 'Ļ':
/*      */         case 'Ľ':
/*      */         case 'Ŀ':
/*      */         case 'Ł':
/*      */         case 'Ƚ':
/*      */         case 'ʟ':
/*      */         case 'ᴌ':
/*      */         case 'Ḷ':
/*      */         case 'Ḹ':
/*      */         case 'Ḻ':
/*      */         case 'Ḽ':
/*      */         case 'Ⓛ':
/*      */         case 'Ⱡ':
/*      */         case 'Ɫ':
/*      */         case 'Ꝇ':
/*      */         case 'Ꝉ':
/*      */         case 'Ꞁ':
/*      */         case 'Ｌ':
/*  758 */           output[(outputPos++)] = 'L';
/*  759 */           break;
/*      */         case 'ĺ':
/*      */         case 'ļ':
/*      */         case 'ľ':
/*      */         case 'ŀ':
/*      */         case 'ł':
/*      */         case 'ƚ':
/*      */         case 'ȴ':
/*      */         case 'ɫ':
/*      */         case 'ɬ':
/*      */         case 'ɭ':
/*      */         case 'ᶅ':
/*      */         case 'ḷ':
/*      */         case 'ḹ':
/*      */         case 'ḻ':
/*      */         case 'ḽ':
/*      */         case 'ⓛ':
/*      */         case 'ⱡ':
/*      */         case 'ꝇ':
/*      */         case 'ꝉ':
/*      */         case 'ꞁ':
/*      */         case 'ｌ':
/*  781 */           output[(outputPos++)] = 'l';
/*  782 */           break;
/*      */         case 'Ǉ':
/*  784 */           output[(outputPos++)] = 'L';
/*  785 */           output[(outputPos++)] = 'J';
/*  786 */           break;
/*      */         case 'Ỻ':
/*  788 */           output[(outputPos++)] = 'L';
/*  789 */           output[(outputPos++)] = 'L';
/*  790 */           break;
/*      */         case 'ǈ':
/*  792 */           output[(outputPos++)] = 'L';
/*  793 */           output[(outputPos++)] = 'j';
/*  794 */           break;
/*      */         case '⒧':
/*  796 */           output[(outputPos++)] = '(';
/*  797 */           output[(outputPos++)] = 'l';
/*  798 */           output[(outputPos++)] = ')';
/*  799 */           break;
/*      */         case 'ǉ':
/*  801 */           output[(outputPos++)] = 'l';
/*  802 */           output[(outputPos++)] = 'j';
/*  803 */           break;
/*      */         case 'ỻ':
/*  805 */           output[(outputPos++)] = 'l';
/*  806 */           output[(outputPos++)] = 'l';
/*  807 */           break;
/*      */         case 'ʪ':
/*  809 */           output[(outputPos++)] = 'l';
/*  810 */           output[(outputPos++)] = 's';
/*  811 */           break;
/*      */         case 'ʫ':
/*  813 */           output[(outputPos++)] = 'l';
/*  814 */           output[(outputPos++)] = 'z';
/*  815 */           break;
/*      */         case 'Ɯ':
/*      */         case 'ᴍ':
/*      */         case 'Ḿ':
/*      */         case 'Ṁ':
/*      */         case 'Ṃ':
/*      */         case 'Ⓜ':
/*      */         case 'Ɱ':
/*      */         case 'ꟽ':
/*      */         case 'ꟿ':
/*      */         case 'Ｍ':
/*  826 */           output[(outputPos++)] = 'M';
/*  827 */           break;
/*      */         case 'ɯ':
/*      */         case 'ɰ':
/*      */         case 'ɱ':
/*      */         case 'ᵯ':
/*      */         case 'ᶆ':
/*      */         case 'ḿ':
/*      */         case 'ṁ':
/*      */         case 'ṃ':
/*      */         case 'ⓜ':
/*      */         case 'ｍ':
/*  838 */           output[(outputPos++)] = 'm';
/*  839 */           break;
/*      */         case '⒨':
/*  841 */           output[(outputPos++)] = '(';
/*  842 */           output[(outputPos++)] = 'm';
/*  843 */           output[(outputPos++)] = ')';
/*  844 */           break;
/*      */         case 'Ñ':
/*      */         case 'Ń':
/*      */         case 'Ņ':
/*      */         case 'Ň':
/*      */         case 'Ŋ':
/*      */         case 'Ɲ':
/*      */         case 'Ǹ':
/*      */         case 'Ƞ':
/*      */         case 'ɴ':
/*      */         case 'ᴎ':
/*      */         case 'Ṅ':
/*      */         case 'Ṇ':
/*      */         case 'Ṉ':
/*      */         case 'Ṋ':
/*      */         case 'Ⓝ':
/*      */         case 'Ｎ':
/*  861 */           output[(outputPos++)] = 'N';
/*  862 */           break;
/*      */         case 'ñ':
/*      */         case 'ń':
/*      */         case 'ņ':
/*      */         case 'ň':
/*      */         case 'ŉ':
/*      */         case 'ŋ':
/*      */         case 'ƞ':
/*      */         case 'ǹ':
/*      */         case 'ȵ':
/*      */         case 'ɲ':
/*      */         case 'ɳ':
/*      */         case 'ᵰ':
/*      */         case 'ᶇ':
/*      */         case 'ṅ':
/*      */         case 'ṇ':
/*      */         case 'ṉ':
/*      */         case 'ṋ':
/*      */         case 'ⁿ':
/*      */         case 'ⓝ':
/*      */         case 'ｎ':
/*  883 */           output[(outputPos++)] = 'n';
/*  884 */           break;
/*      */         case 'Ǌ':
/*  886 */           output[(outputPos++)] = 'N';
/*  887 */           output[(outputPos++)] = 'J';
/*  888 */           break;
/*      */         case 'ǋ':
/*  890 */           output[(outputPos++)] = 'N';
/*  891 */           output[(outputPos++)] = 'j';
/*  892 */           break;
/*      */         case '⒩':
/*  894 */           output[(outputPos++)] = '(';
/*  895 */           output[(outputPos++)] = 'n';
/*  896 */           output[(outputPos++)] = ')';
/*  897 */           break;
/*      */         case 'ǌ':
/*  899 */           output[(outputPos++)] = 'n';
/*  900 */           output[(outputPos++)] = 'j';
/*  901 */           break;
/*      */         case 'Ò':
/*      */         case 'Ó':
/*      */         case 'Ô':
/*      */         case 'Õ':
/*      */         case 'Ö':
/*      */         case 'Ø':
/*      */         case 'Ō':
/*      */         case 'Ŏ':
/*      */         case 'Ő':
/*      */         case 'Ɔ':
/*      */         case 'Ɵ':
/*      */         case 'Ơ':
/*      */         case 'Ǒ':
/*      */         case 'Ǫ':
/*      */         case 'Ǭ':
/*      */         case 'Ǿ':
/*      */         case 'Ȍ':
/*      */         case 'Ȏ':
/*      */         case 'Ȫ':
/*      */         case 'Ȭ':
/*      */         case 'Ȯ':
/*      */         case 'Ȱ':
/*      */         case 'ᴏ':
/*      */         case 'ᴐ':
/*      */         case 'Ṍ':
/*      */         case 'Ṏ':
/*      */         case 'Ṑ':
/*      */         case 'Ṓ':
/*      */         case 'Ọ':
/*      */         case 'Ỏ':
/*      */         case 'Ố':
/*      */         case 'Ồ':
/*      */         case 'Ổ':
/*      */         case 'Ỗ':
/*      */         case 'Ộ':
/*      */         case 'Ớ':
/*      */         case 'Ờ':
/*      */         case 'Ở':
/*      */         case 'Ỡ':
/*      */         case 'Ợ':
/*      */         case 'Ⓞ':
/*      */         case 'Ꝋ':
/*      */         case 'Ꝍ':
/*      */         case 'Ｏ':
/*  946 */           output[(outputPos++)] = 'O';
/*  947 */           break;
/*      */         case 'ò':
/*      */         case 'ó':
/*      */         case 'ô':
/*      */         case 'õ':
/*      */         case 'ö':
/*      */         case 'ø':
/*      */         case 'ō':
/*      */         case 'ŏ':
/*      */         case 'ő':
/*      */         case 'ơ':
/*      */         case 'ǒ':
/*      */         case 'ǫ':
/*      */         case 'ǭ':
/*      */         case 'ǿ':
/*      */         case 'ȍ':
/*      */         case 'ȏ':
/*      */         case 'ȫ':
/*      */         case 'ȭ':
/*      */         case 'ȯ':
/*      */         case 'ȱ':
/*      */         case 'ɔ':
/*      */         case 'ɵ':
/*      */         case 'ᴖ':
/*      */         case 'ᴗ':
/*      */         case 'ᶗ':
/*      */         case 'ṍ':
/*      */         case 'ṏ':
/*      */         case 'ṑ':
/*      */         case 'ṓ':
/*      */         case 'ọ':
/*      */         case 'ỏ':
/*      */         case 'ố':
/*      */         case 'ồ':
/*      */         case 'ổ':
/*      */         case 'ỗ':
/*      */         case 'ộ':
/*      */         case 'ớ':
/*      */         case 'ờ':
/*      */         case 'ở':
/*      */         case 'ỡ':
/*      */         case 'ợ':
/*      */         case 'ₒ':
/*      */         case 'ⓞ':
/*      */         case 'ⱺ':
/*      */         case 'ꝋ':
/*      */         case 'ꝍ':
/*      */         case 'ｏ':
/*  995 */           output[(outputPos++)] = 'o';
/*  996 */           break;
/*      */         case 'Œ':
/*      */         case 'ɶ':
/*  999 */           output[(outputPos++)] = 'O';
/* 1000 */           output[(outputPos++)] = 'E';
/* 1001 */           break;
/*      */         case 'Ꝏ':
/* 1003 */           output[(outputPos++)] = 'O';
/* 1004 */           output[(outputPos++)] = 'O';
/* 1005 */           break;
/*      */         case 'Ȣ':
/*      */         case 'ᴕ':
/* 1008 */           output[(outputPos++)] = 'O';
/* 1009 */           output[(outputPos++)] = 'U';
/* 1010 */           break;
/*      */         case '⒪':
/* 1012 */           output[(outputPos++)] = '(';
/* 1013 */           output[(outputPos++)] = 'o';
/* 1014 */           output[(outputPos++)] = ')';
/* 1015 */           break;
/*      */         case 'œ':
/*      */         case 'ᴔ':
/* 1018 */           output[(outputPos++)] = 'o';
/* 1019 */           output[(outputPos++)] = 'e';
/* 1020 */           break;
/*      */         case 'ꝏ':
/* 1022 */           output[(outputPos++)] = 'o';
/* 1023 */           output[(outputPos++)] = 'o';
/* 1024 */           break;
/*      */         case 'ȣ':
/* 1026 */           output[(outputPos++)] = 'o';
/* 1027 */           output[(outputPos++)] = 'u';
/* 1028 */           break;
/*      */         case 'Ƥ':
/*      */         case 'ᴘ':
/*      */         case 'Ṕ':
/*      */         case 'Ṗ':
/*      */         case 'Ⓟ':
/*      */         case 'Ᵽ':
/*      */         case 'Ꝑ':
/*      */         case 'Ꝓ':
/*      */         case 'Ꝕ':
/*      */         case 'Ｐ':
/* 1039 */           output[(outputPos++)] = 'P';
/* 1040 */           break;
/*      */         case 'ƥ':
/*      */         case 'ᵱ':
/*      */         case 'ᵽ':
/*      */         case 'ᶈ':
/*      */         case 'ṕ':
/*      */         case 'ṗ':
/*      */         case 'ⓟ':
/*      */         case 'ꝑ':
/*      */         case 'ꝓ':
/*      */         case 'ꝕ':
/*      */         case 'ꟼ':
/*      */         case 'ｐ':
/* 1053 */           output[(outputPos++)] = 'p';
/* 1054 */           break;
/*      */         case '⒫':
/* 1056 */           output[(outputPos++)] = '(';
/* 1057 */           output[(outputPos++)] = 'p';
/* 1058 */           output[(outputPos++)] = ')';
/* 1059 */           break;
/*      */         case 'Ɋ':
/*      */         case 'Ⓠ':
/*      */         case 'Ꝗ':
/*      */         case 'Ꝙ':
/*      */         case 'Ｑ':
/* 1065 */           output[(outputPos++)] = 'Q';
/* 1066 */           break;
/*      */         case 'ĸ':
/*      */         case 'ɋ':
/*      */         case 'ʠ':
/*      */         case 'ⓠ':
/*      */         case 'ꝗ':
/*      */         case 'ꝙ':
/*      */         case 'ｑ':
/* 1074 */           output[(outputPos++)] = 'q';
/* 1075 */           break;
/*      */         case '⒬':
/* 1077 */           output[(outputPos++)] = '(';
/* 1078 */           output[(outputPos++)] = 'q';
/* 1079 */           output[(outputPos++)] = ')';
/* 1080 */           break;
/*      */         case 'ȹ':
/* 1082 */           output[(outputPos++)] = 'q';
/* 1083 */           output[(outputPos++)] = 'p';
/* 1084 */           break;
/*      */         case 'Ŕ':
/*      */         case 'Ŗ':
/*      */         case 'Ř':
/*      */         case 'Ȑ':
/*      */         case 'Ȓ':
/*      */         case 'Ɍ':
/*      */         case 'ʀ':
/*      */         case 'ʁ':
/*      */         case 'ᴙ':
/*      */         case 'ᴚ':
/*      */         case 'Ṙ':
/*      */         case 'Ṛ':
/*      */         case 'Ṝ':
/*      */         case 'Ṟ':
/*      */         case 'Ⓡ':
/*      */         case 'Ɽ':
/*      */         case 'Ꝛ':
/*      */         case 'Ꞃ':
/*      */         case 'Ｒ':
/* 1104 */           output[(outputPos++)] = 'R';
/* 1105 */           break;
/*      */         case 'ŕ':
/*      */         case 'ŗ':
/*      */         case 'ř':
/*      */         case 'ȑ':
/*      */         case 'ȓ':
/*      */         case 'ɍ':
/*      */         case 'ɼ':
/*      */         case 'ɽ':
/*      */         case 'ɾ':
/*      */         case 'ɿ':
/*      */         case 'ᵣ':
/*      */         case 'ᵲ':
/*      */         case 'ᵳ':
/*      */         case 'ᶉ':
/*      */         case 'ṙ':
/*      */         case 'ṛ':
/*      */         case 'ṝ':
/*      */         case 'ṟ':
/*      */         case 'ⓡ':
/*      */         case 'ꝛ':
/*      */         case 'ꞃ':
/*      */         case 'ｒ':
/* 1128 */           output[(outputPos++)] = 'r';
/* 1129 */           break;
/*      */         case '⒭':
/* 1131 */           output[(outputPos++)] = '(';
/* 1132 */           output[(outputPos++)] = 'r';
/* 1133 */           output[(outputPos++)] = ')';
/* 1134 */           break;
/*      */         case 'Ś':
/*      */         case 'Ŝ':
/*      */         case 'Ş':
/*      */         case 'Š':
/*      */         case 'Ș':
/*      */         case 'Ṡ':
/*      */         case 'Ṣ':
/*      */         case 'Ṥ':
/*      */         case 'Ṧ':
/*      */         case 'Ṩ':
/*      */         case 'Ⓢ':
/*      */         case 'ꜱ':
/*      */         case 'ꞅ':
/*      */         case 'Ｓ':
/* 1149 */           output[(outputPos++)] = 'S';
/* 1150 */           break;
/*      */         case 'ś':
/*      */         case 'ŝ':
/*      */         case 'ş':
/*      */         case 'š':
/*      */         case 'ſ':
/*      */         case 'ș':
/*      */         case 'ȿ':
/*      */         case 'ʂ':
/*      */         case 'ᵴ':
/*      */         case 'ᶊ':
/*      */         case 'ṡ':
/*      */         case 'ṣ':
/*      */         case 'ṥ':
/*      */         case 'ṧ':
/*      */         case 'ṩ':
/*      */         case 'ẜ':
/*      */         case 'ẝ':
/*      */         case 'ⓢ':
/*      */         case 'Ꞅ':
/*      */         case 'ｓ':
/* 1171 */           output[(outputPos++)] = 's';
/* 1172 */           break;
/*      */         case 'ẞ':
/* 1174 */           output[(outputPos++)] = 'S';
/* 1175 */           output[(outputPos++)] = 'S';
/* 1176 */           break;
/*      */         case '⒮':
/* 1178 */           output[(outputPos++)] = '(';
/* 1179 */           output[(outputPos++)] = 's';
/* 1180 */           output[(outputPos++)] = ')';
/* 1181 */           break;
/*      */         case 'ß':
/* 1183 */           output[(outputPos++)] = 's';
/* 1184 */           output[(outputPos++)] = 's';
/* 1185 */           break;
/*      */         case 'ﬆ':
/* 1187 */           output[(outputPos++)] = 's';
/* 1188 */           output[(outputPos++)] = 't';
/* 1189 */           break;
/*      */         case 'Ţ':
/*      */         case 'Ť':
/*      */         case 'Ŧ':
/*      */         case 'Ƭ':
/*      */         case 'Ʈ':
/*      */         case 'Ț':
/*      */         case 'Ⱦ':
/*      */         case 'ᴛ':
/*      */         case 'Ṫ':
/*      */         case 'Ṭ':
/*      */         case 'Ṯ':
/*      */         case 'Ṱ':
/*      */         case 'Ⓣ':
/*      */         case 'Ꞇ':
/*      */         case 'Ｔ':
/* 1205 */           output[(outputPos++)] = 'T';
/* 1206 */           break;
/*      */         case 'ţ':
/*      */         case 'ť':
/*      */         case 'ŧ':
/*      */         case 'ƫ':
/*      */         case 'ƭ':
/*      */         case 'ț':
/*      */         case 'ȶ':
/*      */         case 'ʇ':
/*      */         case 'ʈ':
/*      */         case 'ᵵ':
/*      */         case 'ṫ':
/*      */         case 'ṭ':
/*      */         case 'ṯ':
/*      */         case 'ṱ':
/*      */         case 'ẗ':
/*      */         case 'ⓣ':
/*      */         case 'ⱦ':
/*      */         case 'ｔ':
/* 1225 */           output[(outputPos++)] = 't';
/* 1226 */           break;
/*      */         case 'Þ':
/*      */         case 'Ꝧ':
/* 1229 */           output[(outputPos++)] = 'T';
/* 1230 */           output[(outputPos++)] = 'H';
/* 1231 */           break;
/*      */         case 'Ꜩ':
/* 1233 */           output[(outputPos++)] = 'T';
/* 1234 */           output[(outputPos++)] = 'Z';
/* 1235 */           break;
/*      */         case '⒯':
/* 1237 */           output[(outputPos++)] = '(';
/* 1238 */           output[(outputPos++)] = 't';
/* 1239 */           output[(outputPos++)] = ')';
/* 1240 */           break;
/*      */         case 'ʨ':
/* 1242 */           output[(outputPos++)] = 't';
/* 1243 */           output[(outputPos++)] = 'c';
/* 1244 */           break;
/*      */         case 'þ':
/*      */         case 'ᵺ':
/*      */         case 'ꝧ':
/* 1248 */           output[(outputPos++)] = 't';
/* 1249 */           output[(outputPos++)] = 'h';
/* 1250 */           break;
/*      */         case 'ʦ':
/* 1252 */           output[(outputPos++)] = 't';
/* 1253 */           output[(outputPos++)] = 's';
/* 1254 */           break;
/*      */         case 'ꜩ':
/* 1256 */           output[(outputPos++)] = 't';
/* 1257 */           output[(outputPos++)] = 'z';
/* 1258 */           break;
/*      */         case 'Ù':
/*      */         case 'Ú':
/*      */         case 'Û':
/*      */         case 'Ü':
/*      */         case 'Ũ':
/*      */         case 'Ū':
/*      */         case 'Ŭ':
/*      */         case 'Ů':
/*      */         case 'Ű':
/*      */         case 'Ų':
/*      */         case 'Ư':
/*      */         case 'Ǔ':
/*      */         case 'Ǖ':
/*      */         case 'Ǘ':
/*      */         case 'Ǚ':
/*      */         case 'Ǜ':
/*      */         case 'Ȕ':
/*      */         case 'Ȗ':
/*      */         case 'Ʉ':
/*      */         case 'ᴜ':
/*      */         case 'ᵾ':
/*      */         case 'Ṳ':
/*      */         case 'Ṵ':
/*      */         case 'Ṷ':
/*      */         case 'Ṹ':
/*      */         case 'Ṻ':
/*      */         case 'Ụ':
/*      */         case 'Ủ':
/*      */         case 'Ứ':
/*      */         case 'Ừ':
/*      */         case 'Ử':
/*      */         case 'Ữ':
/*      */         case 'Ự':
/*      */         case 'Ⓤ':
/*      */         case 'Ｕ':
/* 1294 */           output[(outputPos++)] = 'U';
/* 1295 */           break;
/*      */         case 'ù':
/*      */         case 'ú':
/*      */         case 'û':
/*      */         case 'ü':
/*      */         case 'ũ':
/*      */         case 'ū':
/*      */         case 'ŭ':
/*      */         case 'ů':
/*      */         case 'ű':
/*      */         case 'ų':
/*      */         case 'ư':
/*      */         case 'ǔ':
/*      */         case 'ǖ':
/*      */         case 'ǘ':
/*      */         case 'ǚ':
/*      */         case 'ǜ':
/*      */         case 'ȕ':
/*      */         case 'ȗ':
/*      */         case 'ʉ':
/*      */         case 'ᵤ':
/*      */         case 'ᶙ':
/*      */         case 'ṳ':
/*      */         case 'ṵ':
/*      */         case 'ṷ':
/*      */         case 'ṹ':
/*      */         case 'ṻ':
/*      */         case 'ụ':
/*      */         case 'ủ':
/*      */         case 'ứ':
/*      */         case 'ừ':
/*      */         case 'ử':
/*      */         case 'ữ':
/*      */         case 'ự':
/*      */         case 'ⓤ':
/*      */         case 'ｕ':
/* 1331 */           output[(outputPos++)] = 'u';
/* 1332 */           break;
/*      */         case '⒰':
/* 1334 */           output[(outputPos++)] = '(';
/* 1335 */           output[(outputPos++)] = 'u';
/* 1336 */           output[(outputPos++)] = ')';
/* 1337 */           break;
/*      */         case 'ᵫ':
/* 1339 */           output[(outputPos++)] = 'u';
/* 1340 */           output[(outputPos++)] = 'e';
/* 1341 */           break;
/*      */         case 'Ʋ':
/*      */         case 'Ʌ':
/*      */         case 'ᴠ':
/*      */         case 'Ṽ':
/*      */         case 'Ṿ':
/*      */         case 'Ỽ':
/*      */         case 'Ⓥ':
/*      */         case 'Ꝟ':
/*      */         case 'Ꝩ':
/*      */         case 'Ｖ':
/* 1352 */           output[(outputPos++)] = 'V';
/* 1353 */           break;
/*      */         case 'ʋ':
/*      */         case 'ʌ':
/*      */         case 'ᵥ':
/*      */         case 'ᶌ':
/*      */         case 'ṽ':
/*      */         case 'ṿ':
/*      */         case 'ⓥ':
/*      */         case 'ⱱ':
/*      */         case 'ⱴ':
/*      */         case 'ꝟ':
/*      */         case 'ｖ':
/* 1365 */           output[(outputPos++)] = 'v';
/* 1366 */           break;
/*      */         case 'Ꝡ':
/* 1368 */           output[(outputPos++)] = 'V';
/* 1369 */           output[(outputPos++)] = 'Y';
/* 1370 */           break;
/*      */         case '⒱':
/* 1372 */           output[(outputPos++)] = '(';
/* 1373 */           output[(outputPos++)] = 'v';
/* 1374 */           output[(outputPos++)] = ')';
/* 1375 */           break;
/*      */         case 'ꝡ':
/* 1377 */           output[(outputPos++)] = 'v';
/* 1378 */           output[(outputPos++)] = 'y';
/* 1379 */           break;
/*      */         case 'Ŵ':
/*      */         case 'Ƿ':
/*      */         case 'ᴡ':
/*      */         case 'Ẁ':
/*      */         case 'Ẃ':
/*      */         case 'Ẅ':
/*      */         case 'Ẇ':
/*      */         case 'Ẉ':
/*      */         case 'Ⓦ':
/*      */         case 'Ⱳ':
/*      */         case 'Ｗ':
/* 1391 */           output[(outputPos++)] = 'W';
/* 1392 */           break;
/*      */         case 'ŵ':
/*      */         case 'ƿ':
/*      */         case 'ʍ':
/*      */         case 'ẁ':
/*      */         case 'ẃ':
/*      */         case 'ẅ':
/*      */         case 'ẇ':
/*      */         case 'ẉ':
/*      */         case 'ẘ':
/*      */         case 'ⓦ':
/*      */         case 'ⱳ':
/*      */         case 'ｗ':
/* 1405 */           output[(outputPos++)] = 'w';
/* 1406 */           break;
/*      */         case '⒲':
/* 1408 */           output[(outputPos++)] = '(';
/* 1409 */           output[(outputPos++)] = 'w';
/* 1410 */           output[(outputPos++)] = ')';
/* 1411 */           break;
/*      */         case 'Ẋ':
/*      */         case 'Ẍ':
/*      */         case 'Ⓧ':
/*      */         case 'Ｘ':
/* 1416 */           output[(outputPos++)] = 'X';
/* 1417 */           break;
/*      */         case 'ᶍ':
/*      */         case 'ẋ':
/*      */         case 'ẍ':
/*      */         case 'ₓ':
/*      */         case 'ⓧ':
/*      */         case 'ｘ':
/* 1424 */           output[(outputPos++)] = 'x';
/* 1425 */           break;
/*      */         case '⒳':
/* 1427 */           output[(outputPos++)] = '(';
/* 1428 */           output[(outputPos++)] = 'x';
/* 1429 */           output[(outputPos++)] = ')';
/* 1430 */           break;
/*      */         case 'Ý':
/*      */         case 'Ŷ':
/*      */         case 'Ÿ':
/*      */         case 'Ƴ':
/*      */         case 'Ȳ':
/*      */         case 'Ɏ':
/*      */         case 'ʏ':
/*      */         case 'Ẏ':
/*      */         case 'Ỳ':
/*      */         case 'Ỵ':
/*      */         case 'Ỷ':
/*      */         case 'Ỹ':
/*      */         case 'Ỿ':
/*      */         case 'Ⓨ':
/*      */         case 'Ｙ':
/* 1446 */           output[(outputPos++)] = 'Y';
/* 1447 */           break;
/*      */         case 'ý':
/*      */         case 'ÿ':
/*      */         case 'ŷ':
/*      */         case 'ƴ':
/*      */         case 'ȳ':
/*      */         case 'ɏ':
/*      */         case 'ʎ':
/*      */         case 'ẏ':
/*      */         case 'ẙ':
/*      */         case 'ỳ':
/*      */         case 'ỵ':
/*      */         case 'ỷ':
/*      */         case 'ỹ':
/*      */         case 'ỿ':
/*      */         case 'ⓨ':
/*      */         case 'ｙ':
/* 1464 */           output[(outputPos++)] = 'y';
/* 1465 */           break;
/*      */         case '⒴':
/* 1467 */           output[(outputPos++)] = '(';
/* 1468 */           output[(outputPos++)] = 'y';
/* 1469 */           output[(outputPos++)] = ')';
/* 1470 */           break;
/*      */         case 'Ź':
/*      */         case 'Ż':
/*      */         case 'Ž':
/*      */         case 'Ƶ':
/*      */         case 'Ȝ':
/*      */         case 'Ȥ':
/*      */         case 'ᴢ':
/*      */         case 'Ẑ':
/*      */         case 'Ẓ':
/*      */         case 'Ẕ':
/*      */         case 'Ⓩ':
/*      */         case 'Ⱬ':
/*      */         case 'Ꝣ':
/*      */         case 'Ｚ':
/* 1485 */           output[(outputPos++)] = 'Z';
/* 1486 */           break;
/*      */         case 'ź':
/*      */         case 'ż':
/*      */         case 'ž':
/*      */         case 'ƶ':
/*      */         case 'ȝ':
/*      */         case 'ȥ':
/*      */         case 'ɀ':
/*      */         case 'ʐ':
/*      */         case 'ʑ':
/*      */         case 'ᵶ':
/*      */         case 'ᶎ':
/*      */         case 'ẑ':
/*      */         case 'ẓ':
/*      */         case 'ẕ':
/*      */         case 'ⓩ':
/*      */         case 'ⱬ':
/*      */         case 'ꝣ':
/*      */         case 'ｚ':
/* 1505 */           output[(outputPos++)] = 'z';
/* 1506 */           break;
/*      */         case '⒵':
/* 1508 */           output[(outputPos++)] = '(';
/* 1509 */           output[(outputPos++)] = 'z';
/* 1510 */           output[(outputPos++)] = ')';
/* 1511 */           break;
/*      */         case '⁰':
/*      */         case '₀':
/*      */         case '⓪':
/*      */         case '⓿':
/*      */         case '０':
/* 1517 */           output[(outputPos++)] = '0';
/* 1518 */           break;
/*      */         case '¹':
/*      */         case '₁':
/*      */         case '①':
/*      */         case '⓵':
/*      */         case '❶':
/*      */         case '➀':
/*      */         case '➊':
/*      */         case '１':
/* 1527 */           output[(outputPos++)] = '1';
/* 1528 */           break;
/*      */         case '⒈':
/* 1530 */           output[(outputPos++)] = '1';
/* 1531 */           output[(outputPos++)] = '.';
/* 1532 */           break;
/*      */         case '⑴':
/* 1534 */           output[(outputPos++)] = '(';
/* 1535 */           output[(outputPos++)] = '1';
/* 1536 */           output[(outputPos++)] = ')';
/* 1537 */           break;
/*      */         case '²':
/*      */         case '₂':
/*      */         case '②':
/*      */         case '⓶':
/*      */         case '❷':
/*      */         case '➁':
/*      */         case '➋':
/*      */         case '２':
/* 1546 */           output[(outputPos++)] = '2';
/* 1547 */           break;
/*      */         case '⒉':
/* 1549 */           output[(outputPos++)] = '2';
/* 1550 */           output[(outputPos++)] = '.';
/* 1551 */           break;
/*      */         case '⑵':
/* 1553 */           output[(outputPos++)] = '(';
/* 1554 */           output[(outputPos++)] = '2';
/* 1555 */           output[(outputPos++)] = ')';
/* 1556 */           break;
/*      */         case '³':
/*      */         case '₃':
/*      */         case '③':
/*      */         case '⓷':
/*      */         case '❸':
/*      */         case '➂':
/*      */         case '➌':
/*      */         case '３':
/* 1565 */           output[(outputPos++)] = '3';
/* 1566 */           break;
/*      */         case '⒊':
/* 1568 */           output[(outputPos++)] = '3';
/* 1569 */           output[(outputPos++)] = '.';
/* 1570 */           break;
/*      */         case '⑶':
/* 1572 */           output[(outputPos++)] = '(';
/* 1573 */           output[(outputPos++)] = '3';
/* 1574 */           output[(outputPos++)] = ')';
/* 1575 */           break;
/*      */         case '⁴':
/*      */         case '₄':
/*      */         case '④':
/*      */         case '⓸':
/*      */         case '❹':
/*      */         case '➃':
/*      */         case '➍':
/*      */         case '４':
/* 1584 */           output[(outputPos++)] = '4';
/* 1585 */           break;
/*      */         case '⒋':
/* 1587 */           output[(outputPos++)] = '4';
/* 1588 */           output[(outputPos++)] = '.';
/* 1589 */           break;
/*      */         case '⑷':
/* 1591 */           output[(outputPos++)] = '(';
/* 1592 */           output[(outputPos++)] = '4';
/* 1593 */           output[(outputPos++)] = ')';
/* 1594 */           break;
/*      */         case '⁵':
/*      */         case '₅':
/*      */         case '⑤':
/*      */         case '⓹':
/*      */         case '❺':
/*      */         case '➄':
/*      */         case '➎':
/*      */         case '５':
/* 1603 */           output[(outputPos++)] = '5';
/* 1604 */           break;
/*      */         case '⒌':
/* 1606 */           output[(outputPos++)] = '5';
/* 1607 */           output[(outputPos++)] = '.';
/* 1608 */           break;
/*      */         case '⑸':
/* 1610 */           output[(outputPos++)] = '(';
/* 1611 */           output[(outputPos++)] = '5';
/* 1612 */           output[(outputPos++)] = ')';
/* 1613 */           break;
/*      */         case '⁶':
/*      */         case '₆':
/*      */         case '⑥':
/*      */         case '⓺':
/*      */         case '❻':
/*      */         case '➅':
/*      */         case '➏':
/*      */         case '６':
/* 1622 */           output[(outputPos++)] = '6';
/* 1623 */           break;
/*      */         case '⒍':
/* 1625 */           output[(outputPos++)] = '6';
/* 1626 */           output[(outputPos++)] = '.';
/* 1627 */           break;
/*      */         case '⑹':
/* 1629 */           output[(outputPos++)] = '(';
/* 1630 */           output[(outputPos++)] = '6';
/* 1631 */           output[(outputPos++)] = ')';
/* 1632 */           break;
/*      */         case '⁷':
/*      */         case '₇':
/*      */         case '⑦':
/*      */         case '⓻':
/*      */         case '❼':
/*      */         case '➆':
/*      */         case '➐':
/*      */         case '７':
/* 1641 */           output[(outputPos++)] = '7';
/* 1642 */           break;
/*      */         case '⒎':
/* 1644 */           output[(outputPos++)] = '7';
/* 1645 */           output[(outputPos++)] = '.';
/* 1646 */           break;
/*      */         case '⑺':
/* 1648 */           output[(outputPos++)] = '(';
/* 1649 */           output[(outputPos++)] = '7';
/* 1650 */           output[(outputPos++)] = ')';
/* 1651 */           break;
/*      */         case '⁸':
/*      */         case '₈':
/*      */         case '⑧':
/*      */         case '⓼':
/*      */         case '❽':
/*      */         case '➇':
/*      */         case '➑':
/*      */         case '８':
/* 1660 */           output[(outputPos++)] = '8';
/* 1661 */           break;
/*      */         case '⒏':
/* 1663 */           output[(outputPos++)] = '8';
/* 1664 */           output[(outputPos++)] = '.';
/* 1665 */           break;
/*      */         case '⑻':
/* 1667 */           output[(outputPos++)] = '(';
/* 1668 */           output[(outputPos++)] = '8';
/* 1669 */           output[(outputPos++)] = ')';
/* 1670 */           break;
/*      */         case '⁹':
/*      */         case '₉':
/*      */         case '⑨':
/*      */         case '⓽':
/*      */         case '❾':
/*      */         case '➈':
/*      */         case '➒':
/*      */         case '９':
/* 1679 */           output[(outputPos++)] = '9';
/* 1680 */           break;
/*      */         case '⒐':
/* 1682 */           output[(outputPos++)] = '9';
/* 1683 */           output[(outputPos++)] = '.';
/* 1684 */           break;
/*      */         case '⑼':
/* 1686 */           output[(outputPos++)] = '(';
/* 1687 */           output[(outputPos++)] = '9';
/* 1688 */           output[(outputPos++)] = ')';
/* 1689 */           break;
/*      */         case '⑩':
/*      */         case '⓾':
/*      */         case '❿':
/*      */         case '➉':
/*      */         case '➓':
/* 1695 */           output[(outputPos++)] = '1';
/* 1696 */           output[(outputPos++)] = '0';
/* 1697 */           break;
/*      */         case '⒑':
/* 1699 */           output[(outputPos++)] = '1';
/* 1700 */           output[(outputPos++)] = '0';
/* 1701 */           output[(outputPos++)] = '.';
/* 1702 */           break;
/*      */         case '⑽':
/* 1704 */           output[(outputPos++)] = '(';
/* 1705 */           output[(outputPos++)] = '1';
/* 1706 */           output[(outputPos++)] = '0';
/* 1707 */           output[(outputPos++)] = ')';
/* 1708 */           break;
/*      */         case '⑪':
/*      */         case '⓫':
/* 1711 */           output[(outputPos++)] = '1';
/* 1712 */           output[(outputPos++)] = '1';
/* 1713 */           break;
/*      */         case '⒒':
/* 1715 */           output[(outputPos++)] = '1';
/* 1716 */           output[(outputPos++)] = '1';
/* 1717 */           output[(outputPos++)] = '.';
/* 1718 */           break;
/*      */         case '⑾':
/* 1720 */           output[(outputPos++)] = '(';
/* 1721 */           output[(outputPos++)] = '1';
/* 1722 */           output[(outputPos++)] = '1';
/* 1723 */           output[(outputPos++)] = ')';
/* 1724 */           break;
/*      */         case '⑫':
/*      */         case '⓬':
/* 1727 */           output[(outputPos++)] = '1';
/* 1728 */           output[(outputPos++)] = '2';
/* 1729 */           break;
/*      */         case '⒓':
/* 1731 */           output[(outputPos++)] = '1';
/* 1732 */           output[(outputPos++)] = '2';
/* 1733 */           output[(outputPos++)] = '.';
/* 1734 */           break;
/*      */         case '⑿':
/* 1736 */           output[(outputPos++)] = '(';
/* 1737 */           output[(outputPos++)] = '1';
/* 1738 */           output[(outputPos++)] = '2';
/* 1739 */           output[(outputPos++)] = ')';
/* 1740 */           break;
/*      */         case '⑬':
/*      */         case '⓭':
/* 1743 */           output[(outputPos++)] = '1';
/* 1744 */           output[(outputPos++)] = '3';
/* 1745 */           break;
/*      */         case '⒔':
/* 1747 */           output[(outputPos++)] = '1';
/* 1748 */           output[(outputPos++)] = '3';
/* 1749 */           output[(outputPos++)] = '.';
/* 1750 */           break;
/*      */         case '⒀':
/* 1752 */           output[(outputPos++)] = '(';
/* 1753 */           output[(outputPos++)] = '1';
/* 1754 */           output[(outputPos++)] = '3';
/* 1755 */           output[(outputPos++)] = ')';
/* 1756 */           break;
/*      */         case '⑭':
/*      */         case '⓮':
/* 1759 */           output[(outputPos++)] = '1';
/* 1760 */           output[(outputPos++)] = '4';
/* 1761 */           break;
/*      */         case '⒕':
/* 1763 */           output[(outputPos++)] = '1';
/* 1764 */           output[(outputPos++)] = '4';
/* 1765 */           output[(outputPos++)] = '.';
/* 1766 */           break;
/*      */         case '⒁':
/* 1768 */           output[(outputPos++)] = '(';
/* 1769 */           output[(outputPos++)] = '1';
/* 1770 */           output[(outputPos++)] = '4';
/* 1771 */           output[(outputPos++)] = ')';
/* 1772 */           break;
/*      */         case '⑮':
/*      */         case '⓯':
/* 1775 */           output[(outputPos++)] = '1';
/* 1776 */           output[(outputPos++)] = '5';
/* 1777 */           break;
/*      */         case '⒖':
/* 1779 */           output[(outputPos++)] = '1';
/* 1780 */           output[(outputPos++)] = '5';
/* 1781 */           output[(outputPos++)] = '.';
/* 1782 */           break;
/*      */         case '⒂':
/* 1784 */           output[(outputPos++)] = '(';
/* 1785 */           output[(outputPos++)] = '1';
/* 1786 */           output[(outputPos++)] = '5';
/* 1787 */           output[(outputPos++)] = ')';
/* 1788 */           break;
/*      */         case '⑯':
/*      */         case '⓰':
/* 1791 */           output[(outputPos++)] = '1';
/* 1792 */           output[(outputPos++)] = '6';
/* 1793 */           break;
/*      */         case '⒗':
/* 1795 */           output[(outputPos++)] = '1';
/* 1796 */           output[(outputPos++)] = '6';
/* 1797 */           output[(outputPos++)] = '.';
/* 1798 */           break;
/*      */         case '⒃':
/* 1800 */           output[(outputPos++)] = '(';
/* 1801 */           output[(outputPos++)] = '1';
/* 1802 */           output[(outputPos++)] = '6';
/* 1803 */           output[(outputPos++)] = ')';
/* 1804 */           break;
/*      */         case '⑰':
/*      */         case '⓱':
/* 1807 */           output[(outputPos++)] = '1';
/* 1808 */           output[(outputPos++)] = '7';
/* 1809 */           break;
/*      */         case '⒘':
/* 1811 */           output[(outputPos++)] = '1';
/* 1812 */           output[(outputPos++)] = '7';
/* 1813 */           output[(outputPos++)] = '.';
/* 1814 */           break;
/*      */         case '⒄':
/* 1816 */           output[(outputPos++)] = '(';
/* 1817 */           output[(outputPos++)] = '1';
/* 1818 */           output[(outputPos++)] = '7';
/* 1819 */           output[(outputPos++)] = ')';
/* 1820 */           break;
/*      */         case '⑱':
/*      */         case '⓲':
/* 1823 */           output[(outputPos++)] = '1';
/* 1824 */           output[(outputPos++)] = '8';
/* 1825 */           break;
/*      */         case '⒙':
/* 1827 */           output[(outputPos++)] = '1';
/* 1828 */           output[(outputPos++)] = '8';
/* 1829 */           output[(outputPos++)] = '.';
/* 1830 */           break;
/*      */         case '⒅':
/* 1832 */           output[(outputPos++)] = '(';
/* 1833 */           output[(outputPos++)] = '1';
/* 1834 */           output[(outputPos++)] = '8';
/* 1835 */           output[(outputPos++)] = ')';
/* 1836 */           break;
/*      */         case '⑲':
/*      */         case '⓳':
/* 1839 */           output[(outputPos++)] = '1';
/* 1840 */           output[(outputPos++)] = '9';
/* 1841 */           break;
/*      */         case '⒚':
/* 1843 */           output[(outputPos++)] = '1';
/* 1844 */           output[(outputPos++)] = '9';
/* 1845 */           output[(outputPos++)] = '.';
/* 1846 */           break;
/*      */         case '⒆':
/* 1848 */           output[(outputPos++)] = '(';
/* 1849 */           output[(outputPos++)] = '1';
/* 1850 */           output[(outputPos++)] = '9';
/* 1851 */           output[(outputPos++)] = ')';
/* 1852 */           break;
/*      */         case '⑳':
/*      */         case '⓴':
/* 1855 */           output[(outputPos++)] = '2';
/* 1856 */           output[(outputPos++)] = '0';
/* 1857 */           break;
/*      */         case '⒛':
/* 1859 */           output[(outputPos++)] = '2';
/* 1860 */           output[(outputPos++)] = '0';
/* 1861 */           output[(outputPos++)] = '.';
/* 1862 */           break;
/*      */         case '⒇':
/* 1864 */           output[(outputPos++)] = '(';
/* 1865 */           output[(outputPos++)] = '2';
/* 1866 */           output[(outputPos++)] = '0';
/* 1867 */           output[(outputPos++)] = ')';
/* 1868 */           break;
/*      */         case '«':
/*      */         case '»':
/*      */         case '“':
/*      */         case '”':
/*      */         case '„':
/*      */         case '″':
/*      */         case '‶':
/*      */         case '❝':
/*      */         case '❞':
/*      */         case '❮':
/*      */         case '❯':
/*      */         case '＂':
/* 1881 */           output[(outputPos++)] = '"';
/* 1882 */           break;
/*      */         case '‘':
/*      */         case '’':
/*      */         case '‚':
/*      */         case '‛':
/*      */         case '′':
/*      */         case '‵':
/*      */         case '‹':
/*      */         case '›':
/*      */         case '❛':
/*      */         case '❜':
/*      */         case '＇':
/* 1894 */           output[(outputPos++)] = '\'';
/* 1895 */           break;
/*      */         case '‐':
/*      */         case '‑':
/*      */         case '‒':
/*      */         case '–':
/*      */         case '—':
/*      */         case '⁻':
/*      */         case '₋':
/*      */         case '－':
/* 1904 */           output[(outputPos++)] = '-';
/* 1905 */           break;
/*      */         case '⁅':
/*      */         case '❲':
/*      */         case '［':
/* 1909 */           output[(outputPos++)] = '[';
/* 1910 */           break;
/*      */         case '⁆':
/*      */         case '❳':
/*      */         case '］':
/* 1914 */           output[(outputPos++)] = ']';
/* 1915 */           break;
/*      */         case '⁽':
/*      */         case '₍':
/*      */         case '❨':
/*      */         case '❪':
/*      */         case '（':
/* 1921 */           output[(outputPos++)] = '(';
/* 1922 */           break;
/*      */         case '⸨':
/* 1924 */           output[(outputPos++)] = '(';
/* 1925 */           output[(outputPos++)] = '(';
/* 1926 */           break;
/*      */         case '⁾':
/*      */         case '₎':
/*      */         case '❩':
/*      */         case '❫':
/*      */         case '）':
/* 1932 */           output[(outputPos++)] = ')';
/* 1933 */           break;
/*      */         case '⸩':
/* 1935 */           output[(outputPos++)] = ')';
/* 1936 */           output[(outputPos++)] = ')';
/* 1937 */           break;
/*      */         case '❬':
/*      */         case '❰':
/*      */         case '＜':
/* 1941 */           output[(outputPos++)] = '<';
/* 1942 */           break;
/*      */         case '❭':
/*      */         case '❱':
/*      */         case '＞':
/* 1946 */           output[(outputPos++)] = '>';
/* 1947 */           break;
/*      */         case '❴':
/*      */         case '｛':
/* 1950 */           output[(outputPos++)] = '{';
/* 1951 */           break;
/*      */         case '❵':
/*      */         case '｝':
/* 1954 */           output[(outputPos++)] = '}';
/* 1955 */           break;
/*      */         case '⁺':
/*      */         case '₊':
/*      */         case '＋':
/* 1959 */           output[(outputPos++)] = '+';
/* 1960 */           break;
/*      */         case '⁼':
/*      */         case '₌':
/*      */         case '＝':
/* 1964 */           output[(outputPos++)] = '=';
/* 1965 */           break;
/*      */         case '！':
/* 1967 */           output[(outputPos++)] = '!';
/* 1968 */           break;
/*      */         case '‼':
/* 1970 */           output[(outputPos++)] = '!';
/* 1971 */           output[(outputPos++)] = '!';
/* 1972 */           break;
/*      */         case '⁉':
/* 1974 */           output[(outputPos++)] = '!';
/* 1975 */           output[(outputPos++)] = '?';
/* 1976 */           break;
/*      */         case '＃':
/* 1978 */           output[(outputPos++)] = '#';
/* 1979 */           break;
/*      */         case '＄':
/* 1981 */           output[(outputPos++)] = '$';
/* 1982 */           break;
/*      */         case '⁒':
/*      */         case '％':
/* 1985 */           output[(outputPos++)] = '%';
/* 1986 */           break;
/*      */         case '＆':
/* 1988 */           output[(outputPos++)] = '&';
/* 1989 */           break;
/*      */         case '⁎':
/*      */         case '＊':
/* 1992 */           output[(outputPos++)] = '*';
/* 1993 */           break;
/*      */         case '，':
/* 1995 */           output[(outputPos++)] = ',';
/* 1996 */           break;
/*      */         case '．':
/* 1998 */           output[(outputPos++)] = '.';
/* 1999 */           break;
/*      */         case '⁄':
/*      */         case '／':
/* 2002 */           output[(outputPos++)] = '/';
/* 2003 */           break;
/*      */         case '：':
/* 2005 */           output[(outputPos++)] = ':';
/* 2006 */           break;
/*      */         case '⁏':
/*      */         case '；':
/* 2009 */           output[(outputPos++)] = ';';
/* 2010 */           break;
/*      */         case '？':
/* 2012 */           output[(outputPos++)] = '?';
/* 2013 */           break;
/*      */         case '⁇':
/* 2015 */           output[(outputPos++)] = '?';
/* 2016 */           output[(outputPos++)] = '?';
/* 2017 */           break;
/*      */         case '⁈':
/* 2019 */           output[(outputPos++)] = '?';
/* 2020 */           output[(outputPos++)] = '!';
/* 2021 */           break;
/*      */         case '＠':
/* 2023 */           output[(outputPos++)] = '@';
/* 2024 */           break;
/*      */         case '＼':
/* 2026 */           output[(outputPos++)] = '\\';
/* 2027 */           break;
/*      */         case '‸':
/*      */         case '＾':
/* 2030 */           output[(outputPos++)] = '^';
/* 2031 */           break;
/*      */         case '＿':
/* 2033 */           output[(outputPos++)] = '_';
/* 2034 */           break;
/*      */         case '⁓':
/*      */         case '～':
/* 2037 */           output[(outputPos++)] = '~';
/* 2038 */           break;
/*      */         default:
/* 2040 */           output[(outputPos++)] = c;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2045 */     return outputPos;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.ASCIIFoldingFilter
 * JD-Core Version:    0.6.0
 */