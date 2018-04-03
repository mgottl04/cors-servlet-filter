/*******************************************************************************
 * Copyright (c) 2017 Tasktop Technologies.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.tasktop.servlet.cors;

import static com.tasktop.servlet.cors.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

class RequestPathMatcher {
	private static final String PATTERN_OR = "|";

	private final Pattern pathPattern;

	RequestPathMatcher(List<String> paths) {
		requireNonNull(paths);
		checkArgument(!paths.isEmpty());
		this.pathPattern = Pattern.compile(
				"^(" + paths.stream().map(UriDecoder::decode).map(Pattern::quote).collect(joining(PATTERN_OR)) + ").*");
	}

	boolean matches(HttpServletRequest request) {
		return pathPattern.matcher(getRequestUriWithoutContextPath(request)).matches();
	}

	private String getRequestUriWithoutContextPath(HttpServletRequest request) {
		String requestUri = UriDecoder.decode(request.getRequestURI());
		String contextPath = UriDecoder.decode(request.getContextPath());
		checkContextPath(requestUri, contextPath);
		return requestUri.substring(contextPath.length());
	}

	private void checkContextPath(String requestUri, String contextPath) {
		if (!requestUri.startsWith(contextPath)) {
			throw new IllegalStateException(
					MessageFormat.format("Path \"{1}\" must start with context path \"{1}\"", requestUri, contextPath));
		}
	}
}
