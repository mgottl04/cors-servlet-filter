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

import static java.text.MessageFormat.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

class ConfigurationParameterParser {
	private static final String PATH_DELIMITER_PATTERN = "[,\\s]+";

	public static List<String> parseExclusionPaths(String paths) {
		requireNonNull(paths);
		List<String> values = Arrays.asList(paths.split(PATH_DELIMITER_PATTERN)).stream().map(String::trim)
				.filter(s -> !s.isEmpty()).map(prependSlashIfNotPresent()).collect(toList());
		if (values.isEmpty()) {
			throw new IllegalArgumentException(
					format("When specified, {0} must have at least one value", InitParameterNames.EXCLUSION_PATHS));
		}
		return values;
	}

	private static Function<String, String> prependSlashIfNotPresent() {
		return path -> {
			if (path.startsWith("/")) {
				return path;
			}
			return "/" + path;
		};
	}

	private ConfigurationParameterParser() {
		// prevent instantiation
	}
}
