/*
 * Copyright 2016 Carlos Ballesteros Velasco
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.nio.charset;

import com.jtransc.ds.FastStringMap;
import com.jtransc.internal.JTranscGenericCharset;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.SortedMap;

public abstract class Charset implements Comparable<Charset> {
	public static boolean isSupported(String charsetName) {
		try {
			forName(charsetName);
			return true;
		} catch (UnsupportedCharsetException e) {
			return false;
		}
	}

	native public static SortedMap<String, Charset> availableCharsets();

	static private FastStringMap<Charset> charsets = null;

	static private Charset _default;

	public static Charset forName(String charsetName) {
		charsetName = charsetName.toUpperCase();

		if (charsets == null) {
			charsets = new FastStringMap<Charset>();
			charsets.set("UTF-8", new JTranscGenericCharset("UTF-8", new String[]{}) {
				@Override
				public CharsetDecoder newDecoder() {
					return new CharsetDecoder(this, 1f, 4f) {
						int c;
						boolean readC = false;

						@Override
						protected CoderResult implFlush(CharBuffer out) {
							readC = false;
							return super.implFlush(out);
						}

						@Override
						protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
							while (in.hasRemaining() && out.hasRemaining()) {
								if (!readC) {
									c = in.get() & 0xFF;
									readC = true;
								}
								switch (c >> 4) {
									case 0:
									case 1:
									case 2:
									case 3:
									case 4:
									case 5:
									case 6:
									case 7: {
										// 0xxxxxxx
										out.put((char) (c));
										readC = false;
										break;
									}
									case 12:
									case 13: {
										if (in.remaining() < 1) return CoderResult.OVERFLOW;
										// 110x xxxx   10xx xxxx
										out.put((char) (((c & 0x1F) << 6) | (in.get() & 0x3F)));
										readC = false;
										break;
									}
									case 14: {
										if (in.remaining() < 2) return CoderResult.OVERFLOW;
										// 1110 xxxx  10xx xxxx  10xx xxxx
										out.put((char) (((c & 0x0F) << 12) | ((in.get() & 0x3F) << 6) | ((in.get() & 0x3F) << 0)));
										readC = false;
										break;
									}
								}
							}
							readC = false;
							if (in.hasRemaining() && !out.hasRemaining()) {
								return CoderResult.OVERFLOW;
							} else {
								return CoderResult.UNDERFLOW;
							}
						}
					};
				}
			});
			charsets.set("ISO-8859-1", new JTranscGenericCharset("ISO-8859-1", new String[]{}));
			charsets.set("UTF-16", new JTranscGenericCharset("UTF-16", new String[]{}));
			charsets.set("UTF-16LE", new JTranscGenericCharset("UTF-16LE", new String[]{}));
			charsets.set("UTF-16BE", new JTranscGenericCharset("UTF-16BE", new String[]{}));
			charsets.set("US-ASCII", new JTranscGenericCharset("US-ASCII", new String[]{}));
		}

		if (charsets.has(charsetName)) {
			return charsets.get(charsetName);
		} else {
			throw new UnsupportedCharsetException(charsetName);
		}
	}

	public static Charset defaultCharset() {
		if (_default == null) _default = forName("UTF-8");
		return _default;
	}

	private String canonicalName;
	private Set<String> aliases;

	protected Charset(String canonicalName, String[] aliases) {
		this.canonicalName = canonicalName;
		this.aliases = new HashSet<String>();
		for (int n = 0; n < aliases.length; n++) this.aliases.add(aliases[n]);
	}

	public final String name() {
		return canonicalName;
	}

	public final Set<String> aliases() {
		return this.aliases;
	}

	public String displayName() {
		return canonicalName;
	}

	public final boolean isRegistered() {
		return true;
	}

	public String displayName(Locale locale) {
		return canonicalName;
	}

	public abstract boolean contains(Charset cs);

	public abstract CharsetDecoder newDecoder();

	public abstract CharsetEncoder newEncoder();

	public boolean canEncode() {
		return true;
	}

	public final CharBuffer decode(ByteBuffer bb) {
		try {
			byte[] data = new byte[bb.limit()];
			for (int n = 0; n < data.length; n++) data[n] = bb.get(n);
			return CharBuffer.wrap(new String(data, canonicalName));
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}
	}

	public final ByteBuffer encode(CharBuffer cb) {
		try {
			char[] data = new char[cb.limit()];
			for (int n = 0; n < data.length; n++) data[n] = cb.get(n);
			return ByteBuffer.wrap(new String(data).getBytes(canonicalName));
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}
	}

	public final ByteBuffer encode(String str) {
		return encode(CharBuffer.wrap(str));
	}

	public final int hashCode() {
		return displayName().hashCode();
	}

	native public final int compareTo(Charset that);

	native public final boolean equals(Object ob);

	public final String toString() {
		return displayName();
	}
}

