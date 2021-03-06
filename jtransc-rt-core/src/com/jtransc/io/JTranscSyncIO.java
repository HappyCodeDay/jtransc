package com.jtransc.io;

import com.jtransc.annotation.JTranscAddFile;
import com.jtransc.annotation.JTranscAddMembers;
import com.jtransc.annotation.JTranscMethodBody;
import com.jtransc.annotation.haxe.HaxeAddMembers;
import com.jtransc.annotation.haxe.HaxeMethodBody;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@JTranscAddFile(target = "js", priority = 0, process = true, prepend = "js/io.js")
public class JTranscSyncIO {
	static public final int BA_EXISTS = 0x01;
	static public final int BA_REGULAR = 0x02;
	static public final int BA_DIRECTORY = 0x04;
	static public final int BA_HIDDEN = 0x08;

	public static final int O_RDONLY = 1;
	public static final int O_RDWR = 2;
	public static final int O_SYNC = 4;
	public static final int O_DSYNC = 8;

	public static final int ACCESS_EXECUTE = 0x01;
	public static final int ACCESS_WRITE = 0x02;
	public static final int ACCESS_READ = 0x04;

	static public Impl impl = new Impl(null) {
		@Override
		public ImplStream open(String path, int mode) throws FileNotFoundException {
			JTranscIOSyncFile file = new JTranscIOSyncFile();
			try {
				file.open(path, mode);
				return file;
			} catch (Throwable e) {
				e.printStackTrace();
				throw new FileNotFoundException(path);
			}
		}

		@Override
		@HaxeMethodBody("return HaxeIO.SyncFS.getLength(p0._str);")
		@JTranscMethodBody(target = "js", value = "return N.lnewFloat(IO.getLength(N.istr(p0)));")
		@JTranscMethodBody(target = "cpp", value = {
			"struct stat stat_buf;",
			"char name[1024] = {0};",
			"::strcpy(name, N::istr3(p0).c_str());",
			"int rc = ::stat(name, &stat_buf);",
			"return (rc == 0) ? stat_buf.st_size : -1;",
		})
		@JTranscMethodBody(target = "d", value = "return std.file.getSize(N.istr2(p0));")
		public long getLength(String file) {
			return 0L;
		}

		@Override
		@HaxeMethodBody("return HaxeIO.SyncFS.delete(p0._str);")
		@JTranscMethodBody(target = "js", value = "return IO.remove(N.istr(p0));")
		@JTranscMethodBody(target = "d", value = "try { std.file.remove(N.istr2(p0)); return true; } catch (Throwable t) { return false; }")
		public native boolean delete(String file);

		@Override
		@HaxeMethodBody("return HaxeIO.SyncFS.getBooleanAttributes(p0._str);")
		@JTranscMethodBody(target = "js", value = "return IO.getBooleanAttributes(N.istr(p0));")
		@JTranscMethodBody(target = "cpp", value = {
			"struct stat stat_buf;",
			"char name[1024] = {0};",
			"::strcpy(name, N::istr3(p0).c_str());",
			"int rc = ::stat(name, &stat_buf);",
			"int res = 0;",
			"if (rc < 0) return 0;",
			"res |= 1;", // exists
			"if (stat_buf.st_mode & _S_IFREG) res |= 2;", // regular
			"if (stat_buf.st_mode & _S_IFDIR) res |= 4;", // directory
			"return res;",
		})
		@JTranscMethodBody(target = "d", value = {
			"int res = 0;",
			"try {",
			"	auto attr = std.file.getAttributes(N.istr2(p0));",
			"	if (std.file.exists(N.istr2(p0))) res |= 1;",
			"	if (std.file.attrIsFile(attr)) res |= 2;",
			"	if (std.file.attrIsDir(attr)) res |= 4;",
			"} catch (std.file.FileException e) {",
			"}",
			"return res;",
		})
		native public int getBooleanAttributes(String file);

		@Override
		@HaxeMethodBody("return HaxeIO.SyncFS.checkAccess(p0._str, p1);")
		@JTranscMethodBody(target = "js", value = "return IO.checkAccess(N.istr(p0), p1);")
		@JTranscMethodBody(target = "cpp", value = {
			"struct stat stat_buf;",
			"char name[1024] = {0};",
			"::strcpy(name, N::istr3(p0).c_str());",
			"int rc = ::stat(name, &stat_buf);",
			"int res = 0;",
			"if (rc < 0) return 0;",
			//"if (stat_buf.st_mode & _S_IXUSR) res |= 1;", // ACCESS_EXECUTE
			//"if (stat_buf.st_mode & _S_IWUSR) res |= 2;", // ACCESS_WRITE
			//"if (stat_buf.st_mode & _S_IRUSR) res |= 4;", // ACCESS_READ
			"return true;", // FAKE!
		})
		@JTranscMethodBody(target = "d", value = {
			//"scope f = new std.stdio.File(N.str(file))",
			"return std.file.exists(N.istr2(p0));",
		})
		public boolean checkAccess(String file, int access) {
			return true;
		}

		@Override
		@HaxeMethodBody("return HaxeIO.SyncFS.createDirectory(p0._str);")
		@JTranscMethodBody(target = "js", value = "return IO.createDirectory(N.istr(p0));")
		@JTranscMethodBody(target = "cpp", value = {
			"char name[1024] = {0};",
			"::strcpy(name, N::istr3(p0).c_str());",
			"return ::mkdir(name, 0777) != 0;"
		})
		@JTranscMethodBody(target = "d", value = {
			"try {",
			"	std.file.mkdir(N.istr(p0));",
			"	return true;",
			"} catch (std.file.FileException fe) {",
			"	return false;",
			"}",
		})
		public native boolean createDirectory(String file);

		@Override
		@HaxeMethodBody("return HaxeIO.SyncFS.rename(p0._str, p1._str);")
		@JTranscMethodBody(target = "js", value = "return IO.rename(N.istr(p0), N.istr(p1));")
		@JTranscMethodBody(target = "cpp", value = {
			"char name1[1024] = {0};",
			"char name2[1024] = {0};",
			"::strcpy(name1, N::istr3(p0).c_str());",
			"::strcpy(name2, N::istr3(p1).c_str());",
			"return ::rename(name1, name2) != 0;"
		})
		@JTranscMethodBody(target = "d", value = {
			"try {",
			"	std.file.rename(N.istr(p0), N.istr(p1));",
			"	return true;",
			"} catch (std.file.FileException fe) {",
			"	return false;",
			"}",
		})
		public native boolean rename(String fileOld, String fileNew);

		@Override
		@HaxeMethodBody("return N.strArray(HaxeIO.SyncFS.list(p0._str));")
		@JTranscMethodBody(target = "js", value = "return N.istrArray(IO.list(N.istr(p0)));")
		//@JTranscMethodBody(target = "cpp", cond = "_WIN32", value = { // Not implemented
		//	"auto str = N::istr3(p0);",
		//	"std::vector<std::string> out;",
		//	"return N::strArray(out);"
		//})
		//@JTranscMethodBody(target = "cpp", value = {
		//	"auto str = N::istr3(p0);",
		//	"std::vector<std::string> out;",
		//	"struct dirent *dir;",
		//	"DIR *d = opendir(str);",
		//	"if (d) {",
		//	"	while ((dir = readdir(d)) != NULL) {",
		//	"		out.push_back(std::string(dir->d_name));",
		//	"	}",
		//	"	closedir(d);",
		//	"}",
		//	"return N::strArray(out);"
		//})
		@JTranscMethodBody(target = "d", value = {
			"try {",
			"	string[] entries;",
			"	foreach (string de; std.file.dirEntries(N.istr2(p0), SpanMode.shallow)) entries ~= de;",
			"	return N.strArray(entries);",
			"} catch (Throwable t) {",
			"	return null;",
			"}",
		})
		public native String[] list(String file);

		@Override
		@HaxeMethodBody(target = "sys", value = "return N.str(Sys.getCwd());")
		@HaxeMethodBody(target = "js", value = "return N.str(untyped __js__('N.isNode() ? process.cwd() : \"/assets\"'));")
		@HaxeMethodBody("return N.str('');")
		@JTranscMethodBody(target = "js", value = "return N.str(IO.getCwd());")
		@JTranscMethodBody(target = "d", value = "return N.str(std.file.getcwd());")
		public String getCwd() {
			return cwd;
		}

		private String cwd = "/";

		@Override
		@HaxeMethodBody(target = "sys", value = "return Sys.setCwd(p0._str);")
		@HaxeMethodBody(target = "js", value = "untyped __js__('process.chdir({0})', p0._str);")
		@HaxeMethodBody("")
		@JTranscMethodBody(target = "d", value = "std.file.chdir(N.istr(p0));")
		public void setCwd(String path) {
			this.cwd = path;
		}

		@Override
		public String normalizePath(String path) {
			return super.normalizePath(path);
		}
	};

	@HaxeAddMembers({
		"private var _stream = new HaxeIO.SyncStream();"
	})
	@JTranscAddMembers(target = "cpp", value = {
		"FILE* file;",
	})
	@JTranscAddMembers(target = "d", value = {
		"std.stdio.File file;",
	})
	static private class JTranscIOSyncFile extends ImplStream {
		int mode;

		public JTranscIOSyncFile() {
			init();
		}

		@JTranscMethodBody(target = "js", value = "this._stream = new IO.Stream();")
		@JTranscMethodBody(target = "cpp", value = {
			//"printf(\"JTranscIOSyncFile<init>\\n\");",
			"this->file = NULL;"
		})
		private void init() {
		}

		public boolean isReadonly() {
			return (mode & O_RDWR) == 0;
		}

		public void open(String name, int mode) throws FileNotFoundException {
			this.mode = mode;
			if (!_open(name, mode)) {
				throw new FileNotFoundException(String.format("Can't open file %s with mode %d", name, mode));
			}
		}

		public void close() throws IOException {
			_close();
		}

		public int read(byte b[], int off, int len) {
			return this._read(b, off, len);
		}

		public int write(byte b[], int off, int len) {
			if (isReadonly()) return -1;
			return this._write(b, off, len);
		}

		public long getPosition() {
			return _getPosition();
		}

		public void setPosition(long pos) {
			_setPosition(pos);
		}

		public long getLength() {
			return _getLength();
		}

		public void setLength(long newLength) {
			if (isReadonly()) return;
			_setLength(newLength);
		}

		@HaxeMethodBody("_stream.syncioOpen(p0._str, p1); return true;")
		@JTranscMethodBody(target = "js", value = "this._stream.open(N.istr(p0), p1); return true;")
		@JTranscMethodBody(target = "cpp", value = {
			//"printf(\"JTranscIOSyncFile.open\\n\");fflush(stdout);",
			//"char temp[1024];",
			"auto readonly = !(p1 & 2);",
			"char name[1024] = {0};",
			"::strcpy(name, N::istr3(p0).c_str());",
			//"printf(\"%s\\n\", mode);",
			//"printf(\"%s\\n\", str.c_str());",
			//"printf(\"PREOPENING\\n\");fflush(stdout);",
			//"sprintf(temp, \"%s\", str.c_str());",
			"this->file = ::fopen(name, readonly ? \"rb\" : \"r+b\");",
			"if (readonly && (this->file == NULL)) return false;",
			"if (this->file == NULL) { this->file = ::fopen(name, \"w+b\"); if (this->file != NULL) fclose(this->file); }",
			//"if (this->file == NULL) throw ",
			"if (this->file != NULL) ::fseek(this->file, 0, SEEK_SET);",
			//"printf(\"OPENED\\n\");fflush(stdout);",
			"return (this->file != NULL);",
		})
		@JTranscMethodBody(target = "d", value = {
			"try {",
			"	scope readonly = !(p1 & 2);",
			"	scope name = N.istr2(p0);",
			"   scope mode = readonly ? \"rb\" : \"r+b\";",
			"	this.file = File(name, mode);",
			//"	writefln(\"Opened: %s in mode %s\", name, mode);",
			"	return true;",
			"} catch (Throwable t) {",
			"	return false;",
			"}",
		})
		native private boolean _open(String name, int mode);

		@HaxeMethodBody("_stream.syncioClose();")
		@JTranscMethodBody(target = "js", value = "this._stream.close();")
		@JTranscMethodBody(target = "cpp", value = "if (this->file != NULL) { ::fclose(this->file); } this->file = NULL;")
		@JTranscMethodBody(target = "d", value = "this.file.close();")
		private native void _close() throws IOException;

		@HaxeMethodBody("return _stream.syncioReadBytes(p0, p1, p2);")
		@JTranscMethodBody(target = "js", value = "return this._stream.read(p0.data, p1, p2);")
		@JTranscMethodBody(target = "cpp", value = "return (this->file != NULL) ? ::fread(GET_OBJECT(JA_B, p0)->getOffsetPtr(p1), 1, p2, this->file) : (-1);")
		@JTranscMethodBody(target = "d", value = "return cast(int)this.file.rawRead(p0.data[p1..p1 + p2]).length;")
		private native int _read(byte b[], int off, int len);

		@HaxeMethodBody("return _stream.syncioWriteBytes(p0, p1, p2);")
		@JTranscMethodBody(target = "js", value = "return this._stream.write(p0.data, p1, p2);")
		@JTranscMethodBody(target = "cpp", value = "return (this->file != NULL) ? ::fwrite(GET_OBJECT(JA_B, p0)->getOffsetPtr(p1), 1, p2, this->file) : (-1);")
		@JTranscMethodBody(target = "d", value = "this.file.rawWrite(p0.data[p1..p1 + p2]); return p2;")
		private native int _write(byte b[], int off, int len);

		@HaxeMethodBody("return _stream.syncioPosition();")
		@JTranscMethodBody(target = "js", value = "return N.lnewFloat(this._stream.getPosition());")
		@JTranscMethodBody(target = "cpp", value = "return (this->file != NULL) ? ::ftell(this->file) : 0;")
		@JTranscMethodBody(target = "d", value = "return this.file.tell;")
		private native long _getPosition();

		@HaxeMethodBody("_stream.syncioSetPosition(p0);")
		@JTranscMethodBody(target = "js", value = "this._stream.setPosition(N.ltoFloat(p0));")
		@JTranscMethodBody(target = "cpp", value = "if (this->file != NULL) ::fseek(this->file, p0, SEEK_SET);")
		@JTranscMethodBody(target = "d", value = "this.file.seek(p0);")
		private native void _setPosition(long pos);

		@HaxeMethodBody("return _stream.syncioLength();")
		@JTranscMethodBody(target = "js", value = "return N.lnewFloat(this._stream.getLength());")
		@JTranscMethodBody(target = "cpp", value = {
			"if (this->file != NULL) { auto prev = ::ftell(this->file); ::fseek(this->file, 0, SEEK_END); auto out = ::ftell(this->file); ::fseek(this->file, prev, SEEK_SET); return out; } return 0L;"
		})
		@JTranscMethodBody(target = "d", value = "return this.file.size;")
		private native long _getLength();

		@HaxeMethodBody("_stream.syncioSetLength(p0);")
		@JTranscMethodBody(target = "js", value = "this._stream.setLength(N.ltoFloat(p0));")
		//@JTranscMethodBody(target = "cpp", value = "if (this->file != NULL) ::ftruncate(fileno(this->file), p0);")
		private void _setLength(long newLength) {
			throw new RuntimeException("Not implemented");
		}
	}

	static public class ByteStream extends ImplStream {
		private int position;
		private byte[] data;

		public ByteStream(byte[] data) {
			this.data = data;
			this.position = 0;
		}

		@Override
		public void setPosition(long offset) {
			this.position = (int) offset;
		}

		@Override
		public long getPosition() {
			return this.position;
		}

		@Override
		public void setLength(long length) {
			throw new RuntimeException("Not implemented ByteStream.setLength");
		}

		@Override
		public long getLength() {
			return this.data.length;
		}

		@Override
		public int read(byte[] data, int offset, int size) {
			int available = (int) (getLength() - getPosition());
			if (available <= 0) return -1;
			int toRead = Math.min(available, size);
			for (int n = 0; n < toRead; n++) {
				data[offset + n] = this.data[this.position + n];
			}
			this.position += toRead;
			return toRead;
		}

		@Override
		public int write(byte[] data, int offset, int size) {
			throw new RuntimeException("Not implemented ByteStream.write");
		}

		@Override
		public void close() {

		}
	}

	static public abstract class Impl {
		protected Impl parent;

		public Impl(Impl parent) {
			this.parent = parent;
		}

		public ImplStream open(String path, int mode) throws FileNotFoundException {
			if (parent != null) {
				return parent.open(path, mode);
			} else {
				throw new RuntimeException("Not implemented JTranscSyncIO.Impl.open()");
			}
		}

		public String normalizePath(String path) {
			if (parent != null) {
				return parent.normalizePath(path);
			} else {
				return path.replace('\\', '/');
			}
		}

		public long getLength(String path) {
			if (parent != null) {
				return parent.getLength(path);
			} else {
				try {
					ImplStream stream = open(path, O_RDONLY);
					try {
						return stream.getLength();
					} finally {
						stream.close();
					}
				} catch (Throwable e) {
					return 0L;
				}
			}
		}

		public long getTotalSpace(String file) {
			if (parent == null) throw new RuntimeException("Not implemented JTranscSyncIO.getTotalSpace");
			return parent.getTotalSpace(file);
		}

		public long getFreeSpace(String file) {
			if (parent == null) throw new RuntimeException("Not implemented JTranscSyncIO.getFreeSpace");
			return parent.getFreeSpace(file);
		}

		public long getUsableSpace(String file) {
			if (parent == null) throw new RuntimeException("Not implemented JTranscSyncIO.getUsableSpace");
			return parent.getUsableSpace(file);
		}

		public boolean setReadOnly(String file) {
			if (parent == null) throw new RuntimeException("Not implemented JTranscSyncIO.setReadOnly");
			return parent.setReadOnly(file);
		}

		public boolean setLastModifiedTime(String file, long time) {
			if (parent == null) throw new RuntimeException("Not implemented JTranscSyncIO.setLastModifiedTime");
			return parent.setLastModifiedTime(file, time);
		}

		public boolean rename(String fileOld, String fileNew) {
			if (parent == null) throw new RuntimeException("Not implemented JTranscSyncIO.rename");
			return parent.rename(fileOld, fileNew);
		}

		public boolean createDirectory(String file) {
			if (parent == null) throw new RuntimeException("Not implemented JTranscSyncIO.createDirectory");
			return parent.createDirectory(file);
		}

		public String[] list(String file) {
			if (parent == null) throw new RuntimeException("Not implemented JTranscSyncIO.list");
			return parent.list(file);
		}

		public boolean delete(String file) {
			if (parent == null) throw new RuntimeException("Not implemented JTranscSyncIO.delete");
			return parent.delete(file);
		}

		public boolean createFileExclusively(String file) {
			if (parent == null) throw new RuntimeException("Not implemented JTranscSyncIO.createFileExclusively");
			return parent.createFileExclusively(file);
		}

		public boolean setPermission(String file, int access, boolean enable, boolean owneronly) {
			if (parent == null) throw new RuntimeException("Not implemented JTranscSyncIO.setPermission");
			return parent.setPermission(file, access, enable, owneronly);
		}

		public long getLastModifiedTime(String file) {
			if (parent == null) throw new RuntimeException("Not implemented JTranscSyncIO.getLastModifiedTime");
			return parent.getLastModifiedTime(file);
		}

		public boolean checkAccess(String file, int access) {
			if (parent == null) throw new RuntimeException("Not implemented JTranscSyncIO.checkAccess");
			return parent.checkAccess(file, access);
		}

		public int getBooleanAttributes(String file) {
			if (parent == null) throw new RuntimeException("Not implemented JTranscSyncIO.getBooleanAttributes");
			return parent.getBooleanAttributes(file);
		}

		public String getCwd() {
			if (parent == null) throw new RuntimeException("Not implemented JTranscSyncIO.getCwd");
			return parent.getCwd();
		}

		public void setCwd(String path) {
			if (parent == null) throw new RuntimeException("Not implemented JTranscSyncIO.setCwd");
			parent.setCwd(path);
		}

		public boolean isAbsolute(String path) {
			if (parent != null) {
				return parent.isAbsolute(path);
			} else {
				return path.startsWith("/") || path.contains(":");
			}
		}
	}

	static public abstract class ImplStream {
		private byte[] temp = new byte[1];

		abstract public void setPosition(long offset);

		abstract public long getPosition();

		abstract public void setLength(long length);

		abstract public long getLength();

		public int read() {
			return (read(temp, 0, 1) == 1) ? temp[0] : -1;
		}

		public void write(int b) {
			temp[0] = (byte) b;
			write(temp, 0, 1);
		}

		abstract public int read(byte[] data, int offset, int size);

		abstract public int write(byte[] data, int offset, int size);

		abstract public void close() throws IOException;

		public byte[] readBytes(int count) {
			byte[] out = new byte[count];
			read(out, 0, count);
			return out;
		}
	}
}