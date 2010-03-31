package org.liveSense.utils;

import java.io.InputStream;

public class FileInfo {
		private InputStream stream;
		private String fileName;
		private long size;
		private String mimeType;
		
		public FileInfo(InputStream stream, String fileName, long size,
				String mimeType) {
			super();
			this.stream = stream;
			this.fileName = fileName;
			this.size = size;
			this.mimeType = mimeType;
		}
		public InputStream getStream() {
			return stream;
		}
		public void setStream(InputStream stream) {
			this.stream = stream;
		}
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public long getSize() {
			return size;
		}
		public void setSize(long size) {
			this.size = size;
		}
		public String getMimeType() {
			return mimeType;
		}
		public void setMimeType(String mimeType) {
			this.mimeType = mimeType;
		}
}
