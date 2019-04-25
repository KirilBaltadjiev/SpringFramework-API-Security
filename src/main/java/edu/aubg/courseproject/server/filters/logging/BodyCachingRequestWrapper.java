package edu.aubg.courseproject.server.filters.logging;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.web.util.WebUtils;

public class BodyCachingRequestWrapper extends HttpServletRequestWrapper {

	private final ByteArrayOutputStream cachedBody = new ByteArrayOutputStream(1024);

	private final ServletInputStream inputStream;

	private final int cacheSize;

	private boolean cachedWholeBody;

	private BufferedReader reader;

	public BodyCachingRequestWrapper(HttpServletRequest request, int cacheSize) throws IOException {

		super(request);
		this.inputStream = new RequestCachingInputStream(request.getInputStream());
		this.cacheSize = cacheSize;
		this.cachedWholeBody = true;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {

		return inputStream;
	}

	@Override
	public String getCharacterEncoding() {

		String enc = super.getCharacterEncoding();
		return (enc != null ? enc : WebUtils.DEFAULT_CHARACTER_ENCODING);
	}

	@Override
	public BufferedReader getReader() throws IOException {

		if (reader == null) {
			reader = new BufferedReader(new InputStreamReader(inputStream, getCharacterEncoding()));
		}
		return reader;
	}

	public String getCachedBody() {

		return new String(cachedBody.toByteArray());
	}

	public boolean isWholeBodyCached() {

		return cachedWholeBody;
	}

	private class RequestCachingInputStream extends ServletInputStream {

		private final ServletInputStream is;

		private RequestCachingInputStream(ServletInputStream is) {

			this.is = is;
		}

		@Override
		public int read() throws IOException {

			int ch = is.read();
			if (ch != -1) {
				if (cachedBody.size() < cacheSize) {
					cachedBody.write(ch);
				} else {
					cachedWholeBody = false;
				}
			}
			return ch;
		}

		@Override
		public int available() throws IOException {

			return is.available();
		}

		@Override
		public boolean isFinished() {

			return is.isFinished();
		}

		@Override
		public boolean isReady() {

			return is.isReady();
		}

		@Override
		public void setReadListener(ReadListener readListener) {

			is.setReadListener(readListener);
		}
	}
}