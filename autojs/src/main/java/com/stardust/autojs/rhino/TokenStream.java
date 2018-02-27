/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.stardust.autojs.rhino;

import org.mozilla.javascript.Kit;
import org.mozilla.javascript.ObjToIntMap;
import org.mozilla.javascript.Token;

import java.io.IOException;
import java.io.Reader;


public  class TokenStream {
	public static final double NaN = Double
			.longBitsToDouble(0x7ff8000000000000L);

	static double stringToNumber(String s, int start, int radix) {
		char digitMax = '9';
		char lowerCaseBound = 'a';
		char upperCaseBound = 'A';
		int len = s.length();
		if (radix < 10) {
			digitMax = (char) ('0' + radix - 1);
		}
		if (radix > 10) {
			lowerCaseBound = (char) ('a' + radix - 10);
			upperCaseBound = (char) ('A' + radix - 10);
		}
		int end;
		double sum = 0.0;
		for (end = start; end < len; end++) {
			char c = s.charAt(end);
			int newDigit;
			if ('0' <= c && c <= digitMax)
				newDigit = c - '0';
			else if ('a' <= c && c < lowerCaseBound)
				newDigit = c - 'a' + 10;
			else if ('A' <= c && c < upperCaseBound)
				newDigit = c - 'A' + 10;
			else
				break;
			sum = sum * radix + newDigit;
		}
		if (start == end) {
			return NaN;
		}
		if (sum >= 9007199254740992.0) {
			if (radix == 10) {
				/*
				 * If we're accumulating a decimal number and the number is >=
				 * 2^53, then the result from the repeated multiply-add above
				 * may be inaccurate. Call Java to get the correct answer.
				 */
				try {
					return Double.parseDouble(s.substring(start, end));
				} catch (NumberFormatException nfe) {
					return NaN;
				}
			} else if (radix == 2 || radix == 4 || radix == 8 || radix == 16
					|| radix == 32) {
				/*
				 * The number may also be inaccurate for one of these bases.
				 * This happens if the addition in value*radix + digit causes a
				 * round-down to an even least significant mantissa bit when the
				 * first dropped bit is a one. If any of the following digits in
				 * the number (which haven't been added in yet) are nonzero then
				 * the correct action would have been to round up instead of
				 * down. An example of this occurs when reading the number
				 * 0x1000000000000081, which rounds to 0x1000000000000000
				 * instead of 0x1000000000000100.
				 */
				int bitShiftInChar = 1;
				int digit = 0;

				final int SKIP_LEADING_ZEROS = 0;
				final int FIRST_EXACT_53_BITS = 1;
				final int AFTER_BIT_53 = 2;
				final int ZEROS_AFTER_54 = 3;
				final int MIXED_AFTER_54 = 4;

				int state = SKIP_LEADING_ZEROS;
				int exactBitsLimit = 53;
				double factor = 0.0;
				boolean bit53 = false;
				// bit54 is the 54th bit (the first dropped from the mantissa)
				boolean bit54 = false;

				for (;;) {
					if (bitShiftInChar == 1) {
						if (start == end)
							break;
						digit = s.charAt(start++);
						if ('0' <= digit && digit <= '9')
							digit -= '0';
						else if ('a' <= digit && digit <= 'z')
							digit -= 'a' - 10;
						else
							digit -= 'A' - 10;
						bitShiftInChar = radix;
					}
					bitShiftInChar >>= 1;
					boolean bit = (digit & bitShiftInChar) != 0;

					switch (state) {
					case SKIP_LEADING_ZEROS:
						if (bit) {
							--exactBitsLimit;
							sum = 1.0;
							state = FIRST_EXACT_53_BITS;
						}
						break;
					case FIRST_EXACT_53_BITS:
						sum *= 2.0;
						if (bit)
							sum += 1.0;
						--exactBitsLimit;
						if (exactBitsLimit == 0) {
							bit53 = bit;
							state = AFTER_BIT_53;
						}
						break;
					case AFTER_BIT_53:
						bit54 = bit;
						factor = 2.0;
						state = ZEROS_AFTER_54;
						break;
					case ZEROS_AFTER_54:
						if (bit) {
							state = MIXED_AFTER_54;
						}
						// fallthrough
					case MIXED_AFTER_54:
						factor *= 2;
						break;
					}
				}
				switch (state) {
				case SKIP_LEADING_ZEROS:
					sum = 0.0;
					break;
				case FIRST_EXACT_53_BITS:
				case AFTER_BIT_53:
					// do nothing
					break;
				case ZEROS_AFTER_54:
					// x1.1 -> x1 + 1 (round up)
					// x0.1 -> x0 (round down)
					if (bit54 & bit53)
						sum += 1.0;
					sum *= factor;
					break;
				case MIXED_AFTER_54:
					// x.100...1.. -> x + 1 (round up)
					// x.0anything -> x (round down)
					if (bit54)
						sum += 1.0;
					sum *= factor;
					break;
				}
			}
			/* We don't worry about inaccurate numbers for any other base. */
		}
		return sum;
	}

	/*
	 * For chars - because we need something out-of-range to check. (And
	 * checking EOF by exception is annoying.) Note distinction from EOF token
	 * type!
	 */
	private final static int EOF_CHAR = -1;

	private final static char BYTE_ORDER_MARK = '\uFEFF';

	public TokenStream(Reader sourceReader, String sourceString, int lineno) {
		this.lineno = lineno;
		if (sourceReader != null) {
			if (sourceString != null)
				org.mozilla.javascript.Kit.codeBug();
			this.sourceReader = sourceReader;
			this.sourceBuffer = new char[512];
			this.sourceEnd = 0;
		} else {
			if (sourceString == null)
				org.mozilla.javascript.Kit.codeBug();
			this.sourceString = sourceString;
			this.sourceEnd = sourceString.length();
		}
		this.sourceCursor = this.cursor = 0;
	}

	/*
	 * This function uses the cached op, string and number fields in
	 * TokenStream; if getToken has been called since the passed token was
	 * scanned, the op or string printed may be incorrect.
	 */
	String tokenToString(int token) {
		if (Token.printTrees) {
			String name = Token.name(token);

			switch (token) {
			case Token.STRING:
			case Token.REGEXP:
			case Token.NAME:
				return name + " `" + this.string + "'";

			case Token.NUMBER:
				return "NUMBER " + this.number;
			}

			return name;
		}
		return "";
	}

	static boolean isKeyword(String s) {
		return Token.EOF != stringToKeyword(s);
	}

	private static int stringToKeyword(String name) {
		return stringToKeywordForJS(name);
	}

	/**
	 * JavaScript 1.8 and earlier
	 */
	private static int stringToKeywordForJS(String name) {
		// #string_id_map#
		// The following assumes that Token.EOF == 0
		final int Id_break = Token.BREAK, Id_case = Token.CASE, Id_continue = Token.CONTINUE, Id_default = Token.DEFAULT, Id_delete = Token.DELPROP, Id_do = Token.DO, Id_else = Token.ELSE, Id_export = Token.RESERVED, Id_false = Token.FALSE, Id_for = Token.FOR, Id_function = Token.FUNCTION, Id_if = Token.IF, Id_in = Token.IN, Id_let = Token.LET, // reserved
																																																																																							// ES5
																																																																																							// strict
		Id_new = Token.NEW, Id_null = Token.NULL, Id_return = Token.RETURN, Id_switch = Token.SWITCH, Id_this = Token.THIS, Id_true = Token.TRUE, Id_typeof = Token.TYPEOF, Id_var = Token.VAR, Id_void = Token.VOID, Id_while = Token.WHILE, Id_with = Token.WITH, Id_yield = Token.YIELD, // reserved
																																																																							// ES5
																																																																							// strict

		// the following are #ifdef RESERVE_JAVA_KEYWORDS in jsscan.c
		Id_abstract = Token.RESERVED, // ES3 only
		Id_boolean = Token.RESERVED, // ES3 only
		Id_byte = Token.RESERVED, // ES3 only
		Id_catch = Token.CATCH, Id_char = Token.RESERVED, // ES3 only
		Id_class = Token.RESERVED, Id_const = Token.CONST, // reserved
		Id_debugger = Token.DEBUGGER, Id_double = Token.RESERVED, // ES3 only
		Id_enum = Token.RESERVED, Id_extends = Token.RESERVED, Id_final = Token.RESERVED, // ES3
																							// only
		Id_finally = Token.FINALLY, Id_float = Token.RESERVED, // ES3 only
		Id_goto = Token.RESERVED, // ES3 only
		Id_implements = Token.RESERVED, // ES3, ES5 strict
		Id_import = Token.RESERVED, Id_instanceof = Token.INSTANCEOF, Id_int = Token.RESERVED, // ES3
		Id_interface = Token.RESERVED, // ES3, ES5 strict
		Id_long = Token.RESERVED, // ES3 only
		Id_native = Token.RESERVED, // ES3 only
		Id_package = Token.RESERVED, // ES3, ES5 strict
		Id_private = Token.RESERVED, // ES3, ES5 strict
		Id_protected = Token.RESERVED, // ES3, ES5 strict
		Id_public = Token.RESERVED, // ES3, ES5 strict
		Id_short = Token.RESERVED, // ES3 only
		Id_static = Token.RESERVED, // ES3, ES5 strict
		Id_super = Token.RESERVED, Id_synchronized = Token.RESERVED, // ES3 only
		Id_throw = Token.THROW, Id_throws = Token.RESERVED, // ES3 only
		Id_transient = Token.RESERVED, // ES3 only
		Id_try = Token.TRY, Id_volatile = Token.RESERVED; // ES3 only

		int id;
		String s = name;
		// #generated# Last update: 2007-04-18 13:53:30 PDT
		L0: {
			id = 0;
			String X = null;
			int c;
			L: switch (s.length()) {
			case 2:
				c = s.charAt(1);
				if (c == 'f') {
					if (s.charAt(0) == 'i') {
						id = Id_if;
						break L0;
					}
				} else if (c == 'n') {
					if (s.charAt(0) == 'i') {
						id = Id_in;
						break L0;
					}
				} else if (c == 'o') {
					if (s.charAt(0) == 'd') {
						id = Id_do;
						break L0;
					}
				}
				break L;
			case 3:
				switch (s.charAt(0)) {
				case 'f':
					if (s.charAt(2) == 'r' && s.charAt(1) == 'o') {
						id = Id_for;
						break L0;
					}
					break L;
				case 'i':
					if (s.charAt(2) == 't' && s.charAt(1) == 'n') {
						id = Id_int;
						break L0;
					}
					break L;
				case 'l':
					if (s.charAt(2) == 't' && s.charAt(1) == 'e') {
						id = Id_let;
						break L0;
					}
					break L;
				case 'n':
					if (s.charAt(2) == 'w' && s.charAt(1) == 'e') {
						id = Id_new;
						break L0;
					}
					break L;
				case 't':
					if (s.charAt(2) == 'y' && s.charAt(1) == 'r') {
						id = Id_try;
						break L0;
					}
					break L;
				case 'v':
					if (s.charAt(2) == 'r' && s.charAt(1) == 'a') {
						id = Id_var;
						break L0;
					}
					break L;
				}
				break L;
			case 4:
				switch (s.charAt(0)) {
				case 'b':
					X = "byte";
					id = Id_byte;
					break L;
				case 'c':
					c = s.charAt(3);
					if (c == 'e') {
						if (s.charAt(2) == 's' && s.charAt(1) == 'a') {
							id = Id_case;
							break L0;
						}
					} else if (c == 'r') {
						if (s.charAt(2) == 'a' && s.charAt(1) == 'h') {
							id = Id_char;
							break L0;
						}
					}
					break L;
				case 'e':
					c = s.charAt(3);
					if (c == 'e') {
						if (s.charAt(2) == 's' && s.charAt(1) == 'l') {
							id = Id_else;
							break L0;
						}
					} else if (c == 'm') {
						if (s.charAt(2) == 'u' && s.charAt(1) == 'n') {
							id = Id_enum;
							break L0;
						}
					}
					break L;
				case 'g':
					X = "goto";
					id = Id_goto;
					break L;
				case 'l':
					X = "long";
					id = Id_long;
					break L;
				case 'n':
					X = "null";
					id = Id_null;
					break L;
				case 't':
					c = s.charAt(3);
					if (c == 'e') {
						if (s.charAt(2) == 'u' && s.charAt(1) == 'r') {
							id = Id_true;
							break L0;
						}
					} else if (c == 's') {
						if (s.charAt(2) == 'i' && s.charAt(1) == 'h') {
							id = Id_this;
							break L0;
						}
					}
					break L;
				case 'v':
					X = "void";
					id = Id_void;
					break L;
				case 'w':
					X = "with";
					id = Id_with;
					break L;
				}
				break L;
			case 5:
				switch (s.charAt(2)) {
				case 'a':
					X = "class";
					id = Id_class;
					break L;
				case 'e':
					c = s.charAt(0);
					if (c == 'b') {
						X = "break";
						id = Id_break;
					} else if (c == 'y') {
						X = "yield";
						id = Id_yield;
					}
					break L;
				case 'i':
					X = "while";
					id = Id_while;
					break L;
				case 'l':
					X = "false";
					id = Id_false;
					break L;
				case 'n':
					c = s.charAt(0);
					if (c == 'c') {
						X = "const";
						id = Id_const;
					} else if (c == 'f') {
						X = "final";
						id = Id_final;
					}
					break L;
				case 'o':
					c = s.charAt(0);
					if (c == 'f') {
						X = "float";
						id = Id_float;
					} else if (c == 's') {
						X = "short";
						id = Id_short;
					}
					break L;
				case 'p':
					X = "super";
					id = Id_super;
					break L;
				case 'r':
					X = "throw";
					id = Id_throw;
					break L;
				case 't':
					X = "catch";
					id = Id_catch;
					break L;
				}
				break L;
			case 6:
				switch (s.charAt(1)) {
				case 'a':
					X = "native";
					id = Id_native;
					break L;
				case 'e':
					c = s.charAt(0);
					if (c == 'd') {
						X = "delete";
						id = Id_delete;
					} else if (c == 'r') {
						X = "return";
						id = Id_return;
					}
					break L;
				case 'h':
					X = "throws";
					id = Id_throws;
					break L;
				case 'm':
					X = "import";
					id = Id_import;
					break L;
				case 'o':
					X = "double";
					id = Id_double;
					break L;
				case 't':
					X = "static";
					id = Id_static;
					break L;
				case 'u':
					X = "public";
					id = Id_public;
					break L;
				case 'w':
					X = "switch";
					id = Id_switch;
					break L;
				case 'x':
					X = "export";
					id = Id_export;
					break L;
				case 'y':
					X = "typeof";
					id = Id_typeof;
					break L;
				}
				break L;
			case 7:
				switch (s.charAt(1)) {
				case 'a':
					X = "package";
					id = Id_package;
					break L;
				case 'e':
					X = "default";
					id = Id_default;
					break L;
				case 'i':
					X = "finally";
					id = Id_finally;
					break L;
				case 'o':
					X = "boolean";
					id = Id_boolean;
					break L;
				case 'r':
					X = "private";
					id = Id_private;
					break L;
				case 'x':
					X = "extends";
					id = Id_extends;
					break L;
				}
				break L;
			case 8:
				switch (s.charAt(0)) {
				case 'a':
					X = "abstract";
					id = Id_abstract;
					break L;
				case 'c':
					X = "continue";
					id = Id_continue;
					break L;
				case 'd':
					X = "debugger";
					id = Id_debugger;
					break L;
				case 'f':
					X = "function";
					id = Id_function;
					break L;
				case 'v':
					X = "volatile";
					id = Id_volatile;
					break L;
				}
				break L;
			case 9:
				c = s.charAt(0);
				if (c == 'i') {
					X = "interface";
					id = Id_interface;
				} else if (c == 'p') {
					X = "protected";
					id = Id_protected;
				} else if (c == 't') {
					X = "transient";
					id = Id_transient;
				}
				break L;
			case 10:
				c = s.charAt(1);
				if (c == 'm') {
					X = "implements";
					id = Id_implements;
				} else if (c == 'n') {
					X = "instanceof";
					id = Id_instanceof;
				}
				break L;
			case 12:
				X = "synchronized";
				id = Id_synchronized;
				break L;
			}
			if (X != null && X != s && !X.equals(s))
				id = 0;
		}
		// #/generated#
		// #/string_id_map#
		if (id == 0) {
			return Token.EOF;
		}
		return id & 0xff;
	}

	/**
	 * ECMAScript 6.
	 */
	private static int stringToKeywordForES(String name, boolean isStrict) {
		// #string_id_map#
		// The following assumes that Token.EOF == 0
		final int
		// 11.6.2.1 Keywords (ECMAScript2015)
		Id_break = Token.BREAK, Id_case = Token.CASE, Id_catch = Token.CATCH, Id_class = Token.RESERVED, Id_const = Token.CONST, Id_continue = Token.CONTINUE, Id_debugger = Token.DEBUGGER, Id_default = Token.DEFAULT, Id_delete = Token.DELPROP, Id_do = Token.DO, Id_else = Token.ELSE, Id_export = Token.RESERVED, Id_extends = Token.RESERVED, Id_finally = Token.FINALLY, Id_for = Token.FOR, Id_function = Token.FUNCTION, Id_if = Token.IF, Id_import = Token.RESERVED, Id_in = Token.IN, Id_instanceof = Token.INSTANCEOF, Id_new = Token.NEW, Id_return = Token.RETURN, Id_super = Token.RESERVED, Id_switch = Token.SWITCH, Id_this = Token.THIS, Id_throw = Token.THROW, Id_try = Token.TRY, Id_typeof = Token.TYPEOF, Id_var = Token.VAR, Id_void = Token.VOID, Id_while = Token.WHILE, Id_with = Token.WITH, Id_yield = Token.YIELD,

		// 11.6.2.2 Future Reserved Words
		Id_await = Token.RESERVED, Id_enum = Token.RESERVED,

		// 11.6.2.2 NOTE Strict Future Reserved Words
		Id_implements = Token.RESERVED, Id_interface = Token.RESERVED, Id_package = Token.RESERVED, Id_private = Token.RESERVED, Id_protected = Token.RESERVED, Id_public = Token.RESERVED,

		// 11.8 Literals
		Id_false = Token.FALSE, Id_null = Token.NULL, Id_true = Token.TRUE,

		// Non ReservedWord, but Non IdentifierName in strict mode code.
		// 12.1.1 Static Semantics: Early Errors
		Id_let = Token.LET, // TODO : Valid IdentifierName in non-strict mode.
		Id_static = Token.RESERVED;

		int id;
		String s = name;
		// #generated# Last update: 2007-04-18 13:53:30 PDT
		L0: {
			id = 0;
			String X = null;
			int c;
			L: switch (s.length()) {
			case 2:
				c = s.charAt(1);
				if (c == 'f') {
					if (s.charAt(0) == 'i') {
						id = Id_if;
						break L0;
					}
				} else if (c == 'n') {
					if (s.charAt(0) == 'i') {
						id = Id_in;
						break L0;
					}
				} else if (c == 'o') {
					if (s.charAt(0) == 'd') {
						id = Id_do;
						break L0;
					}
				}
				break L;
			case 3:
				switch (s.charAt(0)) {
				case 'f':
					if (s.charAt(2) == 'r' && s.charAt(1) == 'o') {
						id = Id_for;
						break L0;
					}
					break L;
				case 'l':
					if (s.charAt(2) == 't' && s.charAt(1) == 'e') {
						id = Id_let;
						break L0;
					}
					break L;
				case 'n':
					if (s.charAt(2) == 'w' && s.charAt(1) == 'e') {
						id = Id_new;
						break L0;
					}
					break L;
				case 't':
					if (s.charAt(2) == 'y' && s.charAt(1) == 'r') {
						id = Id_try;
						break L0;
					}
					break L;
				case 'v':
					if (s.charAt(2) == 'r' && s.charAt(1) == 'a') {
						id = Id_var;
						break L0;
					}
					break L;
				}
				break L;
			case 4:
				switch (s.charAt(0)) {
				case 'c':
					c = s.charAt(3);
					if (c == 'e') {
						if (s.charAt(2) == 's' && s.charAt(1) == 'a') {
							id = Id_case;
							break L0;
						}
					}
					break L;
				case 'e':
					c = s.charAt(3);
					if (c == 'e') {
						if (s.charAt(2) == 's' && s.charAt(1) == 'l') {
							id = Id_else;
							break L0;
						}
					} else if (c == 'm') {
						if (s.charAt(2) == 'u' && s.charAt(1) == 'n') {
							id = Id_enum;
							break L0;
						}
					}
					break L;
				case 'n':
					X = "null";
					id = Id_null;
					break L;
				case 't':
					c = s.charAt(3);
					if (c == 'e') {
						if (s.charAt(2) == 'u' && s.charAt(1) == 'r') {
							id = Id_true;
							break L0;
						}
					} else if (c == 's') {
						if (s.charAt(2) == 'i' && s.charAt(1) == 'h') {
							id = Id_this;
							break L0;
						}
					}
					break L;
				case 'v':
					X = "void";
					id = Id_void;
					break L;
				case 'w':
					X = "with";
					id = Id_with;
					break L;
				}
				break L;
			case 5:
				switch (s.charAt(2)) {
				case 'a':
					c = s.charAt(0);
					if (c == 'c') {
						X = "class";
						id = Id_class;
					} else if (c == 'a') {
						X = "await";
						id = Id_await;
					}
					break L;
				case 'e':
					c = s.charAt(0);
					if (c == 'b') {
						X = "break";
						id = Id_break;
					} else if (c == 'y') {
						X = "yield";
						id = Id_yield;
					}
					break L;
				case 'i':
					X = "while";
					id = Id_while;
					break L;
				case 'l':
					X = "false";
					id = Id_false;
					break L;
				case 'n':
					X = "const";
					id = Id_const;
					break L;
				case 'p':
					X = "super";
					id = Id_super;
					break L;
				case 'r':
					X = "throw";
					id = Id_throw;
					break L;
				case 't':
					X = "catch";
					id = Id_catch;
					break L;
				}
				break L;
			case 6:
				switch (s.charAt(1)) {
				case 'e':
					c = s.charAt(0);
					if (c == 'd') {
						X = "delete";
						id = Id_delete;
					} else if (c == 'r') {
						X = "return";
						id = Id_return;
					}
					break L;
				case 'm':
					X = "import";
					id = Id_import;
					break L;
				case 't':
					if (isStrict) {
						X = "static";
						id = Id_static;
						break L;
					}
					// fallthru
				case 'u':
					if (isStrict) {
						X = "public";
						id = Id_public;
						break L;
					}
					// fallthru
				case 'w':
					X = "switch";
					id = Id_switch;
					break L;
				case 'x':
					X = "export";
					id = Id_export;
					break L;
				case 'y':
					X = "typeof";
					id = Id_typeof;
					break L;
				}
				break L;
			case 7:
				switch (s.charAt(1)) {
				case 'a':
					if (isStrict) {
						X = "package";
						id = Id_package;
						break L;
					}
					// fallthru
				case 'e':
					X = "default";
					id = Id_default;
					break L;
				case 'i':
					X = "finally";
					id = Id_finally;
					break L;
				case 'r':
					if (isStrict) {
						X = "private";
						id = Id_private;
						break L;
					}
					// fallthru
				case 'x':
					X = "extends";
					id = Id_extends;
					break L;
				}
				break L;
			case 8:
				switch (s.charAt(0)) {
				case 'c':
					X = "continue";
					id = Id_continue;
					break L;
				case 'd':
					X = "debugger";
					id = Id_debugger;
					break L;
				case 'f':
					X = "function";
					id = Id_function;
					break L;
				}
				break L;
			case 9:
				c = s.charAt(0);
				if (c == 'i' && isStrict) {
					X = "interface";
					id = Id_interface;
				} else if (c == 'p' && isStrict) {
					X = "protected";
					id = Id_protected;
				}
				break L;
			case 10:
				c = s.charAt(1);
				if (c == 'm' && isStrict) {
					X = "implements";
					id = Id_implements;
				} else if (c == 'n') {
					X = "instanceof";
					id = Id_instanceof;
				}
				break L;
			}
			if (X != null && X != s && !X.equals(s))
				id = 0;
		}
		// #/generated#
		// #/string_id_map#
		if (id == 0) {
			return Token.EOF;
		}
		return id & 0xff;
	}

	final String getSourceString() {
		return sourceString;
	}

	final int getLineno() {
		return lineno;
	}

	final String getString() {
		return string;
	}

	final char getQuoteChar() {
		return (char) quoteChar;
	}

	final double getNumber() {
		return number;
	}

	final boolean isNumberBinary() {
		return isBinary;
	}

	final boolean isNumberOldOctal() {
		return isOldOctal;
	}

	final boolean isNumberOctal() {
		return isOctal;
	}

	final boolean isNumberHex() {
		return isHex;
	}

	final boolean eof() {
		return hitEOF;
	}

	public final int getToken() throws IOException {
		int c;

		retry: for (;;) {
			// Eat whitespace, possibly sensitive to newlines.
			for (;;) {
				c = getChar();
				if (c == EOF_CHAR) {
					tokenBeg = cursor - 1;
					tokenEnd = cursor;
					return Token.EOF;
				} else if (c == '\n') {
					dirtyLine = false;
					tokenBeg = cursor - 1;
					tokenEnd = cursor;
					return Token.EOL;
				} else if (!isJSSpace(c)) {
					if (c != '-') {
						dirtyLine = true;
					}
					break;
				}
			}

			// Assume the token will be 1 char - fixed up below.
			tokenBeg = cursor - 1;
			tokenEnd = cursor;

			if (c == '@')
				return Token.XMLATTR;

			// identifier/keyword/instanceof?
			// watch out for starting with a <backslash>
			boolean identifierStart;
			boolean isUnicodeEscapeStart = false;
			if (c == '\\') {
				c = getChar();
				if (c == 'u') {
					identifierStart = true;
					isUnicodeEscapeStart = true;
					stringBufferTop = 0;
				} else {
					identifierStart = false;
					ungetChar(c);
					c = '\\';
				}
			} else {
				identifierStart = Character.isJavaIdentifierStart((char) c);
				if (identifierStart) {
					stringBufferTop = 0;
					addToString(c);
				}
			}

			if (identifierStart) {
				boolean containsEscape = isUnicodeEscapeStart;
				for (;;) {
					if (isUnicodeEscapeStart) {
						// strictly speaking we should probably push-back
						// all the bad characters if the <backslash>uXXXX
						// sequence is malformed. But since there isn't a
						// correct context(is there?) for a bad Unicode
						// escape sequence in an identifier, we can report
						// an error here.
						int escapeVal = 0;
						for (int i = 0; i != 4; ++i) {
							c = getChar();
							escapeVal = org.mozilla.javascript.Kit.xDigitToInt(c, escapeVal);
							// Next check takes care about c < 0 and bad escape
							if (escapeVal < 0) {
								break;
							}
						}
						if (escapeVal < 0) {
							return Token.ERROR;
						}
						addToString(escapeVal);
						isUnicodeEscapeStart = false;
					} else {
						c = getChar();
						if (c == '\\') {
							c = getChar();
							if (c == 'u') {
								isUnicodeEscapeStart = true;
								containsEscape = true;
							} else {
								return Token.ERROR;
							}
						} else {
							if (c == EOF_CHAR
									|| c == BYTE_ORDER_MARK
									|| !Character
											.isJavaIdentifierPart((char) c)) {
								break;
							}
							addToString(c);
						}
					}
				}
				ungetChar(c);

				String str = getStringFromBuffer();
				int result = stringToKeyword(str);
				if (result != Token.EOF) {
					return result;
				}
				this.string = (String) allStrings.intern(str);
				return Token.NAME;
			}

			// is it a number?
			if (isDigit(c) || (c == '.' && isDigit(peekChar()))) {
				stringBufferTop = 0;
				int base = 10;
				isHex = isOldOctal = isOctal = isBinary = false;
				boolean es6 = false;

				if (c == '0') {
					c = getChar();
					if (c == 'x' || c == 'X') {
						base = 16;
						isHex = true;
						c = getChar();
					} else if (es6 && (c == 'o' || c == 'O')) {
						base = 8;
						isOctal = true;
						c = getChar();
					} else if (es6 && (c == 'b' || c == 'B')) {
						base = 2;
						isBinary = true;
						c = getChar();
					} else if (isDigit(c)) {
						base = 8;
						isOldOctal = true;
					} else {
						addToString('0');
					}
				}

				boolean isEmpty = true;
				if (base == 16) {
					while (0 <= org.mozilla.javascript.Kit.xDigitToInt(c, 0)) {
						addToString(c);
						c = getChar();
						isEmpty = false;
					}
				} else {
					while ('0' <= c && c <= '9') {
						if (base == 8 && c >= '8') {
							if (isOldOctal) {
								base = 10;
							} else {
								return Token.ERROR;
							}
						} else if (base == 2 && c >= '2') {
							return Token.ERROR;
						}
						addToString(c);
						c = getChar();
						isEmpty = false;
					}
				}
				if (isEmpty && (isBinary || isOctal || isHex)) {
					return Token.ERROR;
				}

				boolean isInteger = true;

				if (base == 10 && (c == '.' || c == 'e' || c == 'E')) {
					isInteger = false;
					if (c == '.') {
						do {
							addToString(c);
							c = getChar();
						} while (isDigit(c));
					}
					if (c == 'e' || c == 'E') {
						addToString(c);
						c = getChar();
						if (c == '+' || c == '-') {
							addToString(c);
							c = getChar();
						}
						if (!isDigit(c)) {
							return Token.ERROR;
						}
						do {
							addToString(c);
							c = getChar();
						} while (isDigit(c));
					}
				}
				ungetChar(c);
				String numString = getStringFromBuffer();
				this.string = numString;

				double dval;
				if (base == 10 && !isInteger) {
					try {
						// Use Java conversion to number from string...
						dval = Double.parseDouble(numString);
					} catch (NumberFormatException ex) {
						return Token.ERROR;
					}
				} else {
					dval = stringToNumber(numString, 0, base);
				}

				this.number = dval;
				return Token.NUMBER;
			}

			// is it a string?
			if (c == '"' || c == '\'') {
				// We attempt to accumulate a string the fast way, by
				// building it directly out of the reader. But if there
				// are any escaped characters in the string, we revert to
				// building it out of a StringBuffer.

				quoteChar = c;
				stringBufferTop = 0;

				c = getChar(false);
				strLoop: while (c != quoteChar) {
					if (c == '\n' || c == EOF_CHAR) {
						ungetChar(c);
						tokenEnd = cursor;
						return Token.ERROR;
					}

					if (c == '\\') {
						// We've hit an escaped character
						int escapeVal;

						c = getChar();
						switch (c) {
						case 'b':
							c = '\b';
							break;
						case 'f':
							c = '\f';
							break;
						case 'n':
							c = '\n';
							break;
						case 'r':
							c = '\r';
							break;
						case 't':
							c = '\t';
							break;

						// \v a late addition to the ECMA spec,
						// it is not in Java, so use 0xb
						case 'v':
							c = 0xb;
							break;

						case 'u':
							// Get 4 hex digits; if the u escape is not
							// followed by 4 hex digits, use 'u' + the
							// literal character sequence that follows.
							int escapeStart = stringBufferTop;
							addToString('u');
							escapeVal = 0;
							for (int i = 0; i != 4; ++i) {
								c = getChar();
								escapeVal = org.mozilla.javascript.Kit.xDigitToInt(c, escapeVal);
								if (escapeVal < 0) {
									continue strLoop;
								}
								addToString(c);
							}
							// prepare for replace of stored 'u' sequence
							// by escape value
							stringBufferTop = escapeStart;
							c = escapeVal;
							break;
						case 'x':
							// Get 2 hex digits, defaulting to 'x'+literal
							// sequence, as above.
							c = getChar();
							escapeVal = org.mozilla.javascript.Kit.xDigitToInt(c, 0);
							if (escapeVal < 0) {
								addToString('x');
								continue strLoop;
							} else {
								int c1 = c;
								c = getChar();
								escapeVal = org.mozilla.javascript.Kit.xDigitToInt(c, escapeVal);
								if (escapeVal < 0) {
									addToString('x');
									addToString(c1);
									continue strLoop;
								} else {
									// got 2 hex digits
									c = escapeVal;
								}
							}
							break;

						case '\n':
							// Remove line terminator after escape to follow
							// SpiderMonkey and C/C++
							c = getChar();
							continue strLoop;

						default:
							if ('0' <= c && c < '8') {
								int val = c - '0';
								c = getChar();
								if ('0' <= c && c < '8') {
									val = 8 * val + c - '0';
									c = getChar();
									if ('0' <= c && c < '8' && val <= 037) {
										// c is 3rd char of octal sequence only
										// if the resulting val <= 0377
										val = 8 * val + c - '0';
										c = getChar();
									}
								}
								ungetChar(c);
								c = val;
							}
						}
					}
					addToString(c);
					c = getChar(false);
				}

				String str = getStringFromBuffer();
				this.string = (String) allStrings.intern(str);
				return Token.STRING;
			}

			switch (c) {
			case ';':
				return Token.SEMI;
			case '[':
				return Token.LB;
			case ']':
				return Token.RB;
			case '{':
				return Token.LC;
			case '}':
				return Token.RC;
			case '(':
				return Token.LP;
			case ')':
				return Token.RP;
			case ',':
				return Token.COMMA;
			case '?':
				return Token.HOOK;
			case ':':
				if (matchChar(':')) {
					return Token.COLONCOLON;
				} else {
					return Token.COLON;
				}
			case '.':
				if (matchChar('.')) {
					return Token.DOTDOT;
				} else if (matchChar('(')) {
					return Token.DOTQUERY;
				} else {
					return Token.DOT;
				}

			case '|':
				if (matchChar('|')) {
					return Token.OR;
				} else if (matchChar('=')) {
					return Token.ASSIGN_BITOR;
				} else {
					return Token.BITOR;
				}

			case '^':
				if (matchChar('=')) {
					return Token.ASSIGN_BITXOR;
				} else {
					return Token.BITXOR;
				}

			case '&':
				if (matchChar('&')) {
					return Token.AND;
				} else if (matchChar('=')) {
					return Token.ASSIGN_BITAND;
				} else {
					return Token.BITAND;
				}

			case '=':
				if (matchChar('=')) {
					if (matchChar('=')) {
						return Token.SHEQ;
					} else {
						return Token.EQ;
					}
				} else if (matchChar('>')) {
					return Token.ARROW;
				} else {
					return Token.ASSIGN;
				}

			case '!':
				if (matchChar('=')) {
					if (matchChar('=')) {
						return Token.SHNE;
					} else {
						return Token.NE;
					}
				} else {
					return Token.NOT;
				}

			case '<':
				/* NB:treat HTML begin-comment as comment-till-eol */
				if (matchChar('!')) {
					if (matchChar('-')) {
						if (matchChar('-')) {
							tokenBeg = cursor - 4;
							skipLine();
							commentType = Token.CommentType.HTML;
							return Token.COMMENT;
						}
						ungetCharIgnoreLineEnd('-');
					}
					ungetCharIgnoreLineEnd('!');
				}
				if (matchChar('<')) {
					if (matchChar('=')) {
						return Token.ASSIGN_LSH;
					} else {
						return Token.LSH;
					}
				} else {
					if (matchChar('=')) {
						return Token.LE;
					} else {
						return Token.LT;
					}
				}

			case '>':
				if (matchChar('>')) {
					if (matchChar('>')) {
						if (matchChar('=')) {
							return Token.ASSIGN_URSH;
						} else {
							return Token.URSH;
						}
					} else {
						if (matchChar('=')) {
							return Token.ASSIGN_RSH;
						} else {
							return Token.RSH;
						}
					}
				} else {
					if (matchChar('=')) {
						return Token.GE;
					} else {
						return Token.GT;
					}
				}

			case '*':
				if (matchChar('=')) {
					return Token.ASSIGN_MUL;
				} else {
					return Token.MUL;
				}

			case '/':
				// is it a // comment?
				if (matchChar('/')) {
					tokenBeg = cursor - 2;
					skipLine();
					commentType = Token.CommentType.LINE;
					return Token.COMMENT;
				}
				// is it a /* or /** comment?
				if (matchChar('*')) {
					boolean lookForSlash = false;
					tokenBeg = cursor - 2;
					if (matchChar('*')) {
						lookForSlash = true;
						commentType = Token.CommentType.JSDOC;
					} else {
						commentType = Token.CommentType.BLOCK_COMMENT;
					}
					for (;;) {
						c = getChar();
						if (c == EOF_CHAR) {
							tokenEnd = cursor - 1;
							return Token.COMMENT;
						} else if (c == '*') {
							lookForSlash = true;
						} else if (c == '/') {
							if (lookForSlash) {
								tokenEnd = cursor;
								return Token.COMMENT;
							}
						} else {
							lookForSlash = false;
							tokenEnd = cursor;
						}
					}
				}

				if (matchChar('=')) {
					return Token.ASSIGN_DIV;
				} else {
					return Token.DIV;
				}

			case '%':
				if (matchChar('=')) {
					return Token.ASSIGN_MOD;
				} else {
					return Token.MOD;
				}

			case '~':
				return Token.BITNOT;

			case '+':
				if (matchChar('=')) {
					return Token.ASSIGN_ADD;
				} else if (matchChar('+')) {
					return Token.INC;
				} else {
					return Token.ADD;
				}

			case '-':
				if (matchChar('=')) {
					c = Token.ASSIGN_SUB;
				} else if (matchChar('-')) {
					if (!dirtyLine) {
						// treat HTML end-comment after possible whitespace
						// after line start as comment-until-eol
						if (matchChar('>')) {
							skipLine();
							commentType = Token.CommentType.HTML;
							return Token.COMMENT;
						}
					}
					c = Token.DEC;
				} else {
					c = Token.SUB;
				}
				dirtyLine = true;
				return c;

			default:
				return Token.ERROR;
			}
		}
	}

	private static boolean isAlpha(int c) {
		// Use 'Z' < 'a'
		if (c <= 'Z') {
			return 'A' <= c;
		} else {
			return 'a' <= c && c <= 'z';
		}
	}

	static boolean isDigit(int c) {
		return '0' <= c && c <= '9';
	}

	/*
	 * As defined in ECMA. jsscan.c uses C isspace() (which allows \v, I think.)
	 * note that code in getChar() implicitly accepts '\r' == as well.
	 */
	static boolean isJSSpace(int c) {
		if (c <= 127) {
			return c == 0x20 || c == 0x9 || c == 0xC || c == 0xB;
		} else {
			return c == 0xA0 || c == BYTE_ORDER_MARK
					|| Character.getType((char) c) == Character.SPACE_SEPARATOR;
		}
	}

	private static boolean isJSFormatChar(int c) {
		return c > 127 && Character.getType((char) c) == Character.FORMAT;
	}

	/**
	 * Parser calls the method when it gets / or /= in literal context.
	 */
	void readRegExp(int startToken) throws IOException {
		int start = tokenBeg;
		stringBufferTop = 0;
		if (startToken == Token.ASSIGN_DIV) {
			// Miss-scanned /=
			addToString('=');
		} else {
			if (startToken != Token.DIV)
				org.mozilla.javascript.Kit.codeBug();
		}

		boolean inCharSet = false; // true if inside a '['..']' pair
		int c;
		while ((c = getChar()) != '/' || inCharSet) {
			if (c == '\n' || c == EOF_CHAR) {
				ungetChar(c);
				tokenEnd = cursor - 1;
				this.string = new String(stringBuffer, 0, stringBufferTop);
				return;
			}
			if (c == '\\') {
				addToString(c);
				c = getChar();
			} else if (c == '[') {
				inCharSet = true;
			} else if (c == ']') {
				inCharSet = false;
			}
			addToString(c);
		}
		int reEnd = stringBufferTop;

		while (true) {
			if (matchChar('g'))
				addToString('g');
			else if (matchChar('i'))
				addToString('i');
			else if (matchChar('m'))
				addToString('m');
			else if (matchChar('y')) // FireFox 3
				addToString('y');
			else
				break;
		}
		tokenEnd = start + stringBufferTop + 2; // include slashes

		if (isAlpha(peekChar())) {
		}

		this.string = new String(stringBuffer, 0, reEnd);
		this.regExpFlags = new String(stringBuffer, reEnd, stringBufferTop
				- reEnd);
	}

	String readAndClearRegExpFlags() {
		String flags = this.regExpFlags;
		this.regExpFlags = null;
		return flags;
	}

	boolean isXMLAttribute() {
		return xmlIsAttribute;
	}

	int getFirstXMLToken() throws IOException {
		xmlOpenTagsCount = 0;
		xmlIsAttribute = false;
		xmlIsTagContent = false;
		if (!canUngetChar())
			return Token.ERROR;
		ungetChar('<');
		return getNextXMLToken();
	}

	int getNextXMLToken() throws IOException {
		tokenBeg = cursor;
		stringBufferTop = 0; // remember the XML

		for (int c = getChar(); c != EOF_CHAR; c = getChar()) {
			if (xmlIsTagContent) {
				switch (c) {
				case '>':
					addToString(c);
					xmlIsTagContent = false;
					xmlIsAttribute = false;
					break;
				case '/':
					addToString(c);
					if (peekChar() == '>') {
						c = getChar();
						addToString(c);
						xmlIsTagContent = false;
						xmlOpenTagsCount--;
					}
					break;
				case '{':
					ungetChar(c);
					this.string = getStringFromBuffer();
					return Token.XML;
				case '\'':
				case '"':
					addToString(c);
					if (!readQuotedString(c))
						return Token.ERROR;
					break;
				case '=':
					addToString(c);
					xmlIsAttribute = true;
					break;
				case ' ':
				case '\t':
				case '\r':
				case '\n':
					addToString(c);
					break;
				default:
					addToString(c);
					xmlIsAttribute = false;
					break;
				}

				if (!xmlIsTagContent && xmlOpenTagsCount == 0) {
					this.string = getStringFromBuffer();
					return Token.XMLEND;
				}
			} else {
				switch (c) {
				case '<':
					addToString(c);
					c = peekChar();
					switch (c) {
					case '!':
						c = getChar(); // Skip !
						addToString(c);
						c = peekChar();
						switch (c) {
						case '-':
							c = getChar(); // Skip -
							addToString(c);
							c = getChar();
							if (c == '-') {
								addToString(c);
								if (!readXmlComment())
									return Token.ERROR;
							} else {
								// throw away the string in progress
								stringBufferTop = 0;
								this.string = null;
								return Token.ERROR;
							}
							break;
						case '[':
							c = getChar(); // Skip [
							addToString(c);
							if (getChar() == 'C' && getChar() == 'D'
									&& getChar() == 'A' && getChar() == 'T'
									&& getChar() == 'A' && getChar() == '[') {
								addToString('C');
								addToString('D');
								addToString('A');
								addToString('T');
								addToString('A');
								addToString('[');
								if (!readCDATA())
									return Token.ERROR;

							} else {
								// throw away the string in progress
								stringBufferTop = 0;
								this.string = null;
								return Token.ERROR;
							}
							break;
						default:
							if (!readEntity())
								return Token.ERROR;
							break;
						}
						break;
					case '?':
						c = getChar(); // Skip ?
						addToString(c);
						if (!readPI())
							return Token.ERROR;
						break;
					case '/':
						// End tag
						c = getChar(); // Skip /
						addToString(c);
						if (xmlOpenTagsCount == 0) {
							// throw away the string in progress
							stringBufferTop = 0;
							this.string = null;
							return Token.ERROR;
						}
						xmlIsTagContent = true;
						xmlOpenTagsCount--;
						break;
					default:
						// Start tag
						xmlIsTagContent = true;
						xmlOpenTagsCount++;
						break;
					}
					break;
				case '{':
					ungetChar(c);
					this.string = getStringFromBuffer();
					return Token.XML;
				default:
					addToString(c);
					break;
				}
			}
		}

		tokenEnd = cursor;
		stringBufferTop = 0; // throw away the string in progress
		this.string = null;
		return Token.ERROR;
	}

	/**
     *
     */
	private boolean readQuotedString(int quote) throws IOException {
		for (int c = getChar(); c != EOF_CHAR; c = getChar()) {
			addToString(c);
			if (c == quote)
				return true;
		}

		stringBufferTop = 0; // throw away the string in progress
		this.string = null;
		return false;
	}

	/**
     *
     */
	private boolean readXmlComment() throws IOException {
		for (int c = getChar(); c != EOF_CHAR;) {
			addToString(c);
			if (c == '-' && peekChar() == '-') {
				c = getChar();
				addToString(c);
				if (peekChar() == '>') {
					c = getChar(); // Skip >
					addToString(c);
					return true;
				} else {
					continue;
				}
			}
			c = getChar();
		}

		stringBufferTop = 0; // throw away the string in progress
		this.string = null;
		return false;
	}

	/**
     *
     */
	private boolean readCDATA() throws IOException {
		for (int c = getChar(); c != EOF_CHAR;) {
			addToString(c);
			if (c == ']' && peekChar() == ']') {
				c = getChar();
				addToString(c);
				if (peekChar() == '>') {
					c = getChar(); // Skip >
					addToString(c);
					return true;
				} else {
					continue;
				}
			}
			c = getChar();
		}

		stringBufferTop = 0; // throw away the string in progress
		this.string = null;
		return false;
	}

	/**
     *
     */
	private boolean readEntity() throws IOException {
		int declTags = 1;
		for (int c = getChar(); c != EOF_CHAR; c = getChar()) {
			addToString(c);
			switch (c) {
			case '<':
				declTags++;
				break;
			case '>':
				declTags--;
				if (declTags == 0)
					return true;
				break;
			}
		}

		stringBufferTop = 0; // throw away the string in progress
		this.string = null;
		return false;
	}

	/**
     *
     */
	private boolean readPI() throws IOException {
		for (int c = getChar(); c != EOF_CHAR; c = getChar()) {
			addToString(c);
			if (c == '?' && peekChar() == '>') {
				c = getChar(); // Skip >
				addToString(c);
				return true;
			}
		}

		stringBufferTop = 0; // throw away the string in progress
		this.string = null;
		return false;
	}

	private String getStringFromBuffer() {
		tokenEnd = cursor;
		return new String(stringBuffer, 0, stringBufferTop);
	}

	private void addToString(int c) {
		int N = stringBufferTop;
		if (N == stringBuffer.length) {
			char[] tmp = new char[stringBuffer.length * 2];
			System.arraycopy(stringBuffer, 0, tmp, 0, N);
			stringBuffer = tmp;
		}
		stringBuffer[N] = (char) c;
		stringBufferTop = N + 1;
	}

	private boolean canUngetChar() {
		return ungetCursor == 0 || ungetBuffer[ungetCursor - 1] != '\n';
	}

	private void ungetChar(int c) {
		// can not unread past across line boundary
		if (ungetCursor != 0 && ungetBuffer[ungetCursor - 1] == '\n')
			org.mozilla.javascript.Kit.codeBug();
		ungetBuffer[ungetCursor++] = c;
		cursor--;
	}

	private boolean matchChar(int test) throws IOException {
		int c = getCharIgnoreLineEnd();
		if (c == test) {
			tokenEnd = cursor;
			return true;
		} else {
			ungetCharIgnoreLineEnd(c);
			return false;
		}
	}

	private int peekChar() throws IOException {
		int c = getChar();
		ungetChar(c);
		return c;
	}

	private int getChar() throws IOException {
		return getChar(true);
	}

	private int getChar(boolean skipFormattingChars) throws IOException {
		if (ungetCursor != 0) {
			cursor++;
			return ungetBuffer[--ungetCursor];
		}

		for (;;) {
			int c;
			if (sourceString != null) {
				if (sourceCursor == sourceEnd) {
					hitEOF = true;
					return EOF_CHAR;
				}
				cursor++;
				c = sourceString.charAt(sourceCursor++);
			} else {
				if (sourceCursor == sourceEnd) {
					if (!fillSourceBuffer()) {
						hitEOF = true;
						return EOF_CHAR;
					}
				}
				cursor++;
				c = sourceBuffer[sourceCursor++];
			}

			if (lineEndChar >= 0) {
				if (lineEndChar == '\r' && c == '\n') {
					lineEndChar = '\n';
					continue;
				}
				lineEndChar = -1;
				lineStart = sourceCursor - 1;
				lineno++;
			}

			if (c <= 127) {
				if (c == '\n' || c == '\r') {
					lineEndChar = c;
					c = '\n';
				}
			} else {
				if (c == BYTE_ORDER_MARK)
					return c; // BOM is considered whitespace
				if (skipFormattingChars && isJSFormatChar(c)) {
					continue;
				}
				if (isJSLineTerminator(c)) {
					lineEndChar = c;
					c = '\n';
				}
			}
			return c;
		}
	}

	private int getCharIgnoreLineEnd() throws IOException {
		if (ungetCursor != 0) {
			cursor++;
			return ungetBuffer[--ungetCursor];
		}

		for (;;) {
			int c;
			if (sourceString != null) {
				if (sourceCursor == sourceEnd) {
					hitEOF = true;
					return EOF_CHAR;
				}
				cursor++;
				c = sourceString.charAt(sourceCursor++);
			} else {
				if (sourceCursor == sourceEnd) {
					if (!fillSourceBuffer()) {
						hitEOF = true;
						return EOF_CHAR;
					}
				}
				cursor++;
				c = sourceBuffer[sourceCursor++];
			}

			if (c <= 127) {
				if (c == '\n' || c == '\r') {
					lineEndChar = c;
					c = '\n';
				}
			} else {
				if (c == BYTE_ORDER_MARK)
					return c; // BOM is considered whitespace
				if (isJSFormatChar(c)) {
					continue;
				}
				if (isJSLineTerminator(c)) {
					lineEndChar = c;
					c = '\n';
				}
			}
			return c;
		}
	}

	private void ungetCharIgnoreLineEnd(int c) {
		ungetBuffer[ungetCursor++] = c;
		cursor--;
	}

	private void skipLine() throws IOException {
		// skip to end of line
		int c;
		while ((c = getChar()) != EOF_CHAR && c != '\n') {
		}
		ungetChar(c);
		tokenEnd = cursor;
	}

	/**
	 * Returns the offset into the current line.
	 */
	final int getOffset() {
		int n = sourceCursor - lineStart;
		if (lineEndChar >= 0) {
			--n;
		}
		return n;
	}

	private final int charAt(int index) {
		if (index < 0) {
			return EOF_CHAR;
		}
		if (sourceString != null) {
			if (index >= sourceEnd) {
				return EOF_CHAR;
			}
			return sourceString.charAt(index);
		} else {
			if (index >= sourceEnd) {
				int oldSourceCursor = sourceCursor;
				try {
					if (!fillSourceBuffer()) {
						return EOF_CHAR;
					}
				} catch (IOException ioe) {
					// ignore it, we're already displaying an error...
					return EOF_CHAR;
				}
				// index recalculuation as fillSourceBuffer can move saved
				// line buffer and change sourceCursor
				index -= (oldSourceCursor - sourceCursor);
			}
			return sourceBuffer[index];
		}
	}

	private final String substring(int beginIndex, int endIndex) {
		if (sourceString != null) {
			return sourceString.substring(beginIndex, endIndex);
		} else {
			int count = endIndex - beginIndex;
			return new String(sourceBuffer, beginIndex, count);
		}
	}

	public static boolean isJSLineTerminator(int c) {
		// Optimization for faster check for eol character:
		// they do not have 0xDFD0 bits set
		if ((c & 0xDFD0) != 0) {
			return false;
		}
		return c == '\n' || c == '\r' || c == 0x2028 || c == 0x2029;
	}

	final String getLine() {
		int lineEnd = sourceCursor;
		if (lineEndChar >= 0) {
			// move cursor before newline sequence
			lineEnd -= 1;
			if (lineEndChar == '\n' && charAt(lineEnd - 1) == '\r') {
				lineEnd -= 1;
			}
		} else {
			// Read until the end of line
			int lineLength = lineEnd - lineStart;
			for (;; ++lineLength) {
				int c = charAt(lineStart + lineLength);
				if (c == EOF_CHAR || isJSLineTerminator(c)) {
					break;
				}
			}
			lineEnd = lineStart + lineLength;
		}
		return substring(lineStart, lineEnd);
	}

	final String getLine(int position, int[] linep) {
		assert position >= 0 && position <= cursor;
		assert linep.length == 2;
		int delta = (cursor + ungetCursor) - position;
		int cur = sourceCursor;
		if (delta > cur) {
			// requested line outside of source buffer
			return null;
		}
		// read back until position
		int end = 0, lines = 0;
		for (; delta > 0; --delta, --cur) {
			assert cur > 0;
			int c = charAt(cur - 1);
			if (isJSLineTerminator(c)) {
				if (c == '\n' && charAt(cur - 2) == '\r') {
					// \r\n sequence
					delta -= 1;
					cur -= 1;
				}
				lines += 1;
				end = cur - 1;
			}
		}
		// read back until line start
		int start = 0, offset = 0;
		for (; cur > 0; --cur, ++offset) {
			int c = charAt(cur - 1);
			if (isJSLineTerminator(c)) {
				start = cur;
				break;
			}
		}
		linep[0] = lineno - lines + (lineEndChar >= 0 ? 1 : 0);
		linep[1] = offset;
		if (lines == 0) {
			return getLine();
		} else {
			return substring(start, end);
		}
	}

	private boolean fillSourceBuffer() throws IOException {
		if (sourceString != null)
			org.mozilla.javascript.Kit.codeBug();
		if (sourceEnd == sourceBuffer.length) {
			if (lineStart != 0 && !isMarkingComment()) {
				System.arraycopy(sourceBuffer, lineStart, sourceBuffer, 0,
						sourceEnd - lineStart);
				sourceEnd -= lineStart;
				sourceCursor -= lineStart;
				lineStart = 0;
			} else {
				char[] tmp = new char[sourceBuffer.length * 2];
				System.arraycopy(sourceBuffer, 0, tmp, 0, sourceEnd);
				sourceBuffer = tmp;
			}
		}
		int n = sourceReader.read(sourceBuffer, sourceEnd, sourceBuffer.length
				- sourceEnd);
		if (n < 0) {
			return false;
		}
		sourceEnd += n;
		return true;
	}

	/**
	 * Return the current position of the scanner cursor.
	 */
	public int getCursor() {
		return cursor;
	}

	/**
	 * Return the absolute source offset of the last scanned token.
	 */
	public int getTokenBeg() {
		return tokenBeg;
	}

	/**
	 * Return the absolute source end-offset of the last scanned token.
	 */
	public int getTokenEnd() {
		return tokenEnd;
	}

	/**
	 * Return tokenEnd - tokenBeg
	 */
	public int getTokenLength() {
		return tokenEnd - tokenBeg;
	}

	/**
	 * Return the type of the last scanned comment.
	 *
	 * @return type of last scanned comment, or 0 if none have been scanned.
	 */
	public Token.CommentType getCommentType() {
		return commentType;
	}

	private boolean isMarkingComment() {
		return commentCursor != -1;
	}

	final String getAndResetCurrentComment() {
		if (sourceString != null) {
			if (isMarkingComment())
				org.mozilla.javascript.Kit.codeBug();
			return sourceString.substring(tokenBeg, tokenEnd);
		} else {
			if (!isMarkingComment())
				Kit.codeBug();
			StringBuilder comment = new StringBuilder(commentPrefix);
			comment.append(sourceBuffer, commentCursor, getTokenLength()
					- commentPrefix.length());
			commentCursor = -1;
			return comment.toString();
		}
	}

	private String convertLastCharToHex(String str) {
		int lastIndex = str.length() - 1;
		StringBuilder buf = new StringBuilder(str.substring(0, lastIndex));
		buf.append("\\u");
		String hexCode = Integer.toHexString(str.charAt(lastIndex));
		for (int i = 0; i < 4 - hexCode.length(); ++i) {
			buf.append('0');
		}
		buf.append(hexCode);
		return buf.toString();
	}

	// stuff other than whitespace since start of line
	private boolean dirtyLine;

	String regExpFlags;

	// Set this to an initial non-null value so that the Parser has
	// something to retrieve even if an error has occurred and no
	// string is found. Fosters one class of error, but saves lots of
	// code.
	private String string = "";
	private double number;
	private boolean isBinary;
	private boolean isOldOctal;
	private boolean isOctal;
	private boolean isHex;

	// delimiter for last string literal scanned
	private int quoteChar;

	private char[] stringBuffer = new char[128];
	private int stringBufferTop;
	private ObjToIntMap allStrings = new ObjToIntMap(50);

	// Room to backtrace from to < on failed match of the last - in <!--
	private final int[] ungetBuffer = new int[3];
	private int ungetCursor;

	private boolean hitEOF = false;

	private int lineStart = 0;
	private int lineEndChar = -1;
	int lineno;

	private String sourceString;
	private Reader sourceReader;
	private char[] sourceBuffer;
	private int sourceEnd;

	// sourceCursor is an index into a small buffer that keeps a
	// sliding window of the source stream.
	int sourceCursor;

	// cursor is a monotonically increasing index into the original
	// source stream, tracking exactly how far scanning has progressed.
	// Its value is the index of the next character to be scanned.
	int cursor;

	// Record start and end positions of last scanned token.
	int tokenBeg;
	int tokenEnd;

	// Type of last comment scanned.
	Token.CommentType commentType;

	// for xml tokenizer
	private boolean xmlIsAttribute;
	private boolean xmlIsTagContent;
	private int xmlOpenTagsCount;

	private String commentPrefix = "";
	private int commentCursor = -1;
}
