package com.exb.server.fileserviceserver.exception;

import java.io.IOException;

public class FileServiceException extends IOException {

	private static final long serialVersionUID = 3689197784645132447L;

	public FileServiceException(final String aMessage) {
		super(aMessage);
	}

	public FileServiceException(final String aMessage, final Throwable aThrowable) {
		super(aMessage, aThrowable);
	}
}
