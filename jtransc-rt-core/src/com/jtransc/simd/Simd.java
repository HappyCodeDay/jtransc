package com.jtransc.simd;

import com.jtransc.annotation.JTranscAddFile;

@JTranscAddFile(target = "js", priority = -2000, prepend = "js/SimdPolyfill.js")
final public class Simd {
	static public void ref() {
	}
}
