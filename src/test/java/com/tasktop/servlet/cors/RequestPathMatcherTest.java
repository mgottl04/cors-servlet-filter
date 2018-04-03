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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class RequestPathMatcherTest {
	private static final String ROOT_CONTEXT_PATH = "";
	private RequestPathMatcher matcher = new RequestPathMatcher(Arrays.asList("/first-path", "/second-path"));
	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void rejectsEmptyPaths() {
		thrown.expect(IllegalArgumentException.class);
		new RequestPathMatcher(Collections.emptyList());
	}

	@Test
	public void requires() {
		thrown.expect(NullPointerException.class);
		new RequestPathMatcher(null);
	}

	@Test
	public void matcherMatchesPathsWithPrefix() {
		assertThat(matcher.matches(request(ROOT_CONTEXT_PATH, "/"))).isFalse();
		assertThat(matcher.matches(request(ROOT_CONTEXT_PATH, "/first-path"))).isTrue();
		assertThat(matcher.matches(request("/a-context-path", "/a-context-path/first-path"))).isTrue();
		assertThat(matcher.matches(request("/a-context-path", "/a-context-path/second-path"))).isTrue();

	}

	@Test
	public void matchesAllSpecifiedPaths() {
		assertMatches(request("/second-path"));
		assertMatches(request("/first-path"));
	}

	@Test
	public void matchesAllSpecifiedPathsWithContextPath() {
		assertMatches(request("/a-context-path", "/a-context-path/second-path"));
		assertMatches(request("/a-context-path", "/a-context-path/first-path"));
	}

	@Test
	public void matchesPathExactly() {
		assertMatches(request("/second-path"));
		assertMatches(request("/second-path/"));
	}

	@Test
	public void matchesPathExactlyWithContextPath() {
		assertMatches(request("/a-context-path", "/a-context-path/second-path"));
		assertMatches(request("/a-context-path", "/a-context-path/second-path/"));
	}

	@Test
	public void rejectsBadContextPath() {
		HttpServletRequest request = request("/a-context-path", "/path-without-context");
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Path \"/a-context-path\" must start with context path \"/a-context-path\"");
		matcher.matches(request);
	}

	@Test
	public void matchesUriEncodedPaths() {
		matcher = new RequestPathMatcher(Arrays.asList("/path+with+spaces"));
		assertMatches(request("/path%20with%20spaces"));
	}

	@Test
	public void matchesDoesNotMatchOtherPaths() {
		assertMatchesDoesNotMatch(request(""));
		assertMatchesDoesNotMatch(request("/"));
		assertMatchesDoesNotMatch(request("/different-path"));
	}

	@Test
	public void matchesDoesNotMatchOtherPathsWithContextPath() {
		assertMatchesDoesNotMatch(request("/a-context-path", "/a-context-path"));
		assertMatchesDoesNotMatch(request("/a-context-path", "/a-context-path/"));
		assertMatchesDoesNotMatch(request("/a-context-path", "/a-context-path/different-path"));
	}

	private void assertMatchesDoesNotMatch(HttpServletRequest request) {
		assertThat(matcher.matches(request)).isFalse();
	}

	private void assertMatches(HttpServletRequest request) {
		assertThat(matcher.matches(request)).isTrue();
	}

	private HttpServletRequest request(String path) {
		return request("", path);
	}

	private HttpServletRequest request(String contextPath, String path) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		doReturn(contextPath).when(request).getContextPath();
		doReturn(path).when(request).getRequestURI();
		return request;
	}
}
