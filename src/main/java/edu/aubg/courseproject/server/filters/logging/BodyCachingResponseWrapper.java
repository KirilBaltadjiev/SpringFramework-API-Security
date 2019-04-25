package edu.aubg.courseproject.server.filters.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.web.util.WebUtils;

public class BodyCachingResponseWrapper extends HttpServletResponseWrapper {

	private final ByteArrayOutputStream cachedBody = new ByteArrayOutputStream(1024);

	private final ServletOutputStream outputStream;

	private final int cacheSize;

	private boolean cachedWholeBody;

	private PrintWriter writer;

	public BodyCachingResponseWrapper(HttpServletResponse response, int cacheSize) throws IOException {

		super(response);
		this.outputStream = new ResponseCachingOutputStream(response.getOutputStream());
		this.cacheSize = cacheSize;
		this.cachedWholeBody = true;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {

		return outputStream;
	}

	@Override
	public String getCharacterEncoding() {

		String enc = super.getCharacterEncoding();
		return (enc != null ? enc : WebUtils.DEFAULT_CHARACTER_ENCODING);
	}

	@Override
	public PrintWriter getWriter() throws IOException {

		if (writer == null) {
			writer = new PrintWriter(new OutputStreamWriter(outputStream, getCharacterEncoding()));
		}
		return writer;
	}

	public String getCachedBody() {

		return new String(cachedBody.toByteArray());
	}

	public boolean isWholeBodyCached() {

		return cachedWholeBody;
	}

	private class ResponseCachingOutputStream extends ServletOutputStream {

		private final ServletOutputStream os;

		private ResponseCachingOutputStream(ServletOutputStream os) {

			this.os = os;
		}

		@Override
		public void write(int i) throws IOException {

			if (i != -1) {
				if (cachedBody.size() < cacheSize) {
					cachedBody.write(i);
				} else {
					cachedWholeBody = false;
				}
			}
			os.write(i);
		}

		@Override
		public boolean isReady() {

			return os.isReady();
		}

		@Override
		public void setWriteListener(WriteListener writeListener) {

			os.setWriteListener(writeListener);
		}
	}
}
