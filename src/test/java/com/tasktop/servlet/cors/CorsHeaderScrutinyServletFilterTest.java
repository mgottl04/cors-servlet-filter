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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CorsHeaderScrutinyServletFilterTest {

	private static final String HTTP_HEADER_HOST = "Host";

	private static final String HTTP_HEADER_ORIGIN = "Origin";

	private static final String HTTP_HEADER_REFERER = "Referer";

	private static final String HTTP_HEADER_X_FORWARDED_HOST = "X-Forwarded-Host";

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	private final HttpServletResponse response = mock(HttpServletResponse.class);

	private final HttpServletRequest request = mock(HttpServletRequest.class);

	private CorsHeaderScrutinyServletFilter filter = new CorsHeaderScrutinyServletFilter();

	private final FilterChain chain = mock(FilterChain.class);

	@Before
	public void before() {
		doReturn(Collections.emptyEnumeration()).when(request).getHeaders(any());
	}

	@Test
	public void init() throws ServletException {
		filter.init(mock(FilterConfig.class));
	}

	@Test
	public void destroy() {
		filter.destroy();
	}

	@Test
	public void doFilterAcceptsRequestWithoutOriginOrReferer() throws IOException, ServletException {
		filter.doFilter(request, response, chain);
		expectAccepted();
	}

	@Test
	public void doFilterRejectsRequestWithEmptyOrigin() throws IOException, ServletException {
		mockHeader(HTTP_HEADER_ORIGIN, "");
		filter.doFilter(request, response, chain);
		expectForbidden();
	}

	@Test
	public void doFilterRejectsRequestWithOriginWithoutHost() throws IOException, ServletException {
		mockHeader(HTTP_HEADER_ORIGIN, "http://a-host");
		filter.doFilter(request, response, chain);
		expectForbidden();
	}

	@Test
	public void doFilterRejectsRequestWithOriginWithDifferentHost() throws IOException, ServletException {
		mockHeader(HTTP_HEADER_ORIGIN, "http://a-host");
		mockHeader(HTTP_HEADER_HOST, "a-different-host");
		filter.doFilter(request, response, chain);
		expectForbidden();
	}

	@Test
	public void doFilterAcceptsRequestWithOriginWithDifferentHostAndMatchingXForwardedHost()
			throws IOException, ServletException {
		mockHeader(HTTP_HEADER_ORIGIN, "http://a-host");
		mockHeader(HTTP_HEADER_HOST, "a-different-host");
		mockHeader(HTTP_HEADER_X_FORWARDED_HOST, "a-host");
		filter.doFilter(request, response, chain);
		expectAccepted();
	}

	@Test
	public void doFilterRejectsRequestWithOriginWithMatchingHostAndDifferentXForwardedHost()
			throws IOException, ServletException {
		mockHeader(HTTP_HEADER_ORIGIN, "http://a-host");
		mockHeader(HTTP_HEADER_HOST, "a-host");
		mockHeader(HTTP_HEADER_X_FORWARDED_HOST, "a-different-host");
		filter.doFilter(request, response, chain);
		expectForbidden();
	}

	@Test
	public void doFilterRejectsRequestWithOriginWithEmptyHost() throws IOException, ServletException {
		mockHeader(HTTP_HEADER_ORIGIN, "http://");
		mockHeader(HTTP_HEADER_HOST, "");
		filter.doFilter(request, response, chain);
		expectForbidden();
	}

	@Test
	public void doFilterRejectsRequestWithMultipleOriginHeadersWithMatchingHost() throws IOException, ServletException {
		mockHeader(HTTP_HEADER_ORIGIN, "http://a-host", "http://a-different-host");
		mockHeader(HTTP_HEADER_HOST, "a-host");
		filter.doFilter(request, response, chain);
		expectForbidden();
	}

	@Test
	public void doFilterAcceptsRequestWithMatchingOrigin() throws IOException, ServletException {
		mockHeader(HTTP_HEADER_ORIGIN, "http://a-host");
		mockHeader(HTTP_HEADER_HOST, "a-host");
		filter.doFilter(request, response, chain);
		expectAccepted();
	}

	@Test
	public void doFilterAcceptsRequestWithMatchingOriginAndPort() throws IOException, ServletException {
		mockHeader(HTTP_HEADER_ORIGIN, "http://a-host:8080");
		mockHeader(HTTP_HEADER_HOST, "a-host:8080");
		filter.doFilter(request, response, chain);
		expectAccepted();
	}

	@Test
	public void doFilterAcceptsRequestWithMatchingOriginAndDifferentPort() throws IOException, ServletException {
		mockHeader(HTTP_HEADER_ORIGIN, "http://a-host:9999");
		mockHeader(HTTP_HEADER_HOST, "a-host:8080");
		filter.doFilter(request, response, chain);
		expectAccepted();
	}

	@Test
	public void doFilterRejectsRequestWithEmptyReferer() throws IOException, ServletException {
		mockHeader(HTTP_HEADER_REFERER, "");
		filter.doFilter(request, response, chain);
		expectForbidden();
	}

	@Test
	public void doFilterRejectsRequestWithRefererWithoutHost() throws IOException, ServletException {
		mockHeader(HTTP_HEADER_REFERER, "http://a-host/some/path");
		filter.doFilter(request, response, chain);
		expectForbidden();
	}

	@Test
	public void doFilterRejectsRequestWithRefererWithDifferentHost() throws IOException, ServletException {
		mockHeader(HTTP_HEADER_REFERER, "http://a-host/some/path");
		mockHeader(HTTP_HEADER_HOST, "a-different-host");
		filter.doFilter(request, response, chain);
		expectForbidden();
	}

	@Test
	public void doFilterAcceptsRequestWithRefererWithDifferentHostOnExcludedPath()
			throws IOException, ServletException {
		filter = newFilterWithExcludedPath("/a-path");
		mockHeader(HTTP_HEADER_REFERER, "http://a-host/some/path");
		mockHeader(HTTP_HEADER_HOST, "a-different-host");
		mockRequestUri("", "/a-path");
		filter.doFilter(request, response, chain);
		expectAccepted();
	}

	@Test
	public void doFilterRejectsRequestWithRefererWithDifferentHostOnExcludedPath()
			throws IOException, ServletException {
		filter = newFilterWithExcludedPath("/a-path");
		mockHeader(HTTP_HEADER_REFERER, "http://a-host/some/path");
		mockHeader(HTTP_HEADER_HOST, "a-different-host");
		mockRequestUri("", "/a-different-path");
		filter.doFilter(request, response, chain);
		expectForbidden();
	}

	@Test
	public void doFilterRejectsRequestWithRefererWithEmptyHost() throws IOException, ServletException {
		mockHeader(HTTP_HEADER_REFERER, "/a-path");
		mockHeader(HTTP_HEADER_HOST, "");
		filter.doFilter(request, response, chain);
		expectForbidden();
	}

	@Test
	public void doFilterRejectsRequestWithMultipleRefererHeadersWithMatchingHost()
			throws IOException, ServletException {
		mockHeader(HTTP_HEADER_REFERER, "http://a-host", "http://a-host");
		mockHeader(HTTP_HEADER_HOST, "a-host");
		filter.doFilter(request, response, chain);
		expectForbidden();
	}

	@Test
	public void doFilterAcceptsRequestWithMatchingReferer() throws IOException, ServletException {
		mockHeader(HTTP_HEADER_REFERER, "http://a-host/some/path");
		mockHeader(HTTP_HEADER_HOST, "a-host");
		filter.doFilter(request, response, chain);
		expectAccepted();
	}

	@Test
	public void doFilterAcceptsRequestWithMatchingRefererAndOrigin() throws IOException, ServletException {
		mockHeader(HTTP_HEADER_REFERER, "http://a-host/some/path");
		mockHeader(HTTP_HEADER_ORIGIN, "http://a-host");
		mockHeader(HTTP_HEADER_HOST, "a-host");
		filter.doFilter(request, response, chain);
		expectAccepted();
	}

	private void mockRequestUri(String contextPath, String requestPath) {
		doReturn(contextPath).when(request).getContextPath();
		doReturn(requestPath).when(request).getRequestURI();
	}

	private CorsHeaderScrutinyServletFilter newFilterWithExcludedPath(String pathPrefix) throws ServletException {
		CorsHeaderScrutinyServletFilter filter = new CorsHeaderScrutinyServletFilter();
		FilterConfig config = mock(FilterConfig.class);
		doReturn(pathPrefix).when(config).getInitParameter(InitParameterNames.EXCLUSION_PATHS);
		filter.init(config);
		return filter;
	}

	private void expectAccepted() throws IOException, ServletException {
		verify(chain).doFilter(request, response);
		verifyNoMoreInteractions(chain, response);
	}

	private void expectForbidden() throws IOException {
		verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
		verifyNoMoreInteractions(chain);
	}

	private void mockHeader(String headerName, String... values) {
		doAnswer(i -> Collections.enumeration(Arrays.asList(values))).when(request).getHeaders(headerName);
	}
}
