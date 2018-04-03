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

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ConfigurationParameterParserTest {
	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void parseExclusionPathsRejectsEmptyValue() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("When specified, exclusion-paths must have at least one value");
		ConfigurationParameterParser.parseExclusionPaths("   \t\n\r\n");
	}

	@Test
	public void parseExclusionPathsWithMultipleValuesSeparatedByComma() {
		List<String> value = ConfigurationParameterParser.parseExclusionPaths("one,two");
		assertThat(value).containsExactly("/one", "/two");
	}

	@Test
	public void parseExclusionPathsWithMultipleValuesSeparatedByWhitespace() {
		List<String> value = ConfigurationParameterParser.parseExclusionPaths("  one\n\ttwo   ");
		assertThat(value).containsExactly("/one", "/two");
	}

	@Test
	public void parseExclusionPathsWithSingleValue() {
		List<String> value = ConfigurationParameterParser.parseExclusionPaths("  one\n\t   ");
		assertThat(value).containsExactly("/one");
	}

	@Test
	public void parseExclusionPathsPrependsSlashOnlyIfNeeded() {
		List<String> value = ConfigurationParameterParser.parseExclusionPaths("/one,two");
		assertThat(value).containsExactly("/one", "/two");
	}

	@Test
	public void parseExclusionPathsIgnoresEmptyValues() {
		List<String> value = ConfigurationParameterParser.parseExclusionPaths("/one,,");
		assertThat(value).containsExactly("/one");
	}
}
