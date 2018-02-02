package com.tasktop.servlet.cors;

class ForbiddenException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	ForbiddenException(String message) {
		super(message);
	}
}
