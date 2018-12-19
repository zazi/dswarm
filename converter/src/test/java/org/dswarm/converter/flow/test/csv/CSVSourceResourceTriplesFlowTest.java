/**
 * Copyright (C) 2013 – 2017 SLUB Dresden & Avantgarde Labs GmbH (<code@dswarm.org>)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dswarm.converter.flow.test.csv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.base.Charsets;
import com.google.inject.ProvisionException;
import org.culturegraph.mf.stream.source.ResourceOpener;
import org.culturegraph.mf.types.Triple;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import rx.Observable;

import org.dswarm.converter.DMPConverterException;
import org.dswarm.converter.GuicedTest;
import org.dswarm.converter.flow.CSVResourceFlowFactory;
import org.dswarm.converter.flow.CSVSourceResourceTriplesFlow;
import org.dswarm.persistence.model.resource.Configuration;
import org.dswarm.persistence.model.resource.utils.ConfigurationStatics;
import org.dswarm.persistence.service.UUIDService;

public class CSVSourceResourceTriplesFlowTest extends GuicedTest {

	private static void testFlow(
			final CSVSourceResourceTriplesFlow flow,
			final String fileName,
			final int rowNumbers,
			final int columnNumbers,
			final Matcher<String> predicateMatcher) throws DMPConverterException {
		final ResourceOpener opener = new ResourceOpener();

		final List<String> subjects = new ArrayList<String>();
		for (int i = 1; i <= rowNumbers; i++) {
			for (int j = 0; j < columnNumbers; j++) {
				subjects.add(String.valueOf(i));
			}
		}
		final Iterator<String> subjectsIterator = subjects.iterator();

		final Collection<Triple> triples = flow.apply(fileName, opener).flatMap(Observable::<Triple>from).toList().toBlocking().first();

		for (final Triple triple : triples) {
			MatcherAssert.assertThat(triple.getSubject(), CoreMatchers.equalTo(subjectsIterator.next()));
			MatcherAssert.assertThat(triple.getPredicate(), predicateMatcher);
			MatcherAssert.assertThat(triple.getObjectType(), CoreMatchers.equalTo(Triple.ObjectType.STRING));
		}

		Assert.assertFalse(subjectsIterator.hasNext());
	}

	@Test
	public void testEndToEnd() throws Exception {

		final CSVSourceResourceTriplesFlow flow = injector
				.getInstance(CSVResourceFlowFactory.class)
				.fromConfigurationParameters(Charsets.UTF_8.name(), '\\', '"', ';', "\n");

		@SuppressWarnings("unchecked")
		final Matcher<String> predicateMatcher = CoreMatchers.anyOf(CoreMatchers.equalTo("id"), CoreMatchers.equalTo("name"),
				CoreMatchers.equalTo("description"), CoreMatchers.equalTo("isbn"), CoreMatchers.equalTo("year"));

		testFlow(flow, "test_csv.csv", 19, 5, predicateMatcher);
	}

	@Test
	public void testEndToEnd2() throws Exception {

		final CSVSourceResourceTriplesFlow flow = injector
				.getInstance(CSVResourceFlowFactory.class)
				.fromConfigurationParameters(Charsets.UTF_8.name(), '\\', '"', ',', "\n");

		@SuppressWarnings("unchecked")
		final Matcher<String> predicateMatcher = CoreMatchers.anyOf(CoreMatchers.equalTo("spalte1"), CoreMatchers.equalTo("spalte2"));

		testFlow(flow, "space_ending2.csv", 2, 2, predicateMatcher);
	}

	@Test
	public void testEndToEnd3() throws Exception {

		final CSVSourceResourceTriplesFlow flow = injector
				.getInstance(CSVResourceFlowFactory.class)
				.fromConfigurationParameters(Charsets.UTF_8.name(), '\\', '"', ',', "\n");

		@SuppressWarnings("unchecked")
		final Matcher<String> predicateMatcher = CoreMatchers.anyOf(CoreMatchers.equalTo("spalte1"), CoreMatchers.equalTo("spalte2"));

		testFlow(flow, "space_ending.csv", 2, 2, predicateMatcher);
	}

	@Test
	public void testEndToEnd4() throws Exception {

		/*
	    "quote_character": "\"",
	    "first_row_is_headings": false,
	    // "column_names": "columnN",
	    "row_delimiter": "\\r\\n",
	    "ignore_lines": "1"
		 */

		final String uuid = UUIDService.getUUID(Configuration.class.getSimpleName());

		final Configuration configuration = new Configuration(uuid);

		configuration.addParameter(ConfigurationStatics.COLUMN_DELIMITER, new TextNode(","));
		configuration.addParameter(ConfigurationStatics.ENCODING, new TextNode("UTF-8"));
		configuration.addParameter(ConfigurationStatics.ESCAPE_CHARACTER, new TextNode("\\"));
		configuration.addParameter(ConfigurationStatics.ROW_DELIMITER, new TextNode("\r\n"));
		// remove following line, of quote character should be null
		configuration.addParameter(ConfigurationStatics.QUOTE_CHARACTER, new TextNode("\""));
		configuration.addParameter(ConfigurationStatics.FIRST_ROW_IS_HEADINGS, BooleanNode.FALSE);
		configuration.addParameter(ConfigurationStatics.IGNORE_LINES, new IntNode(1));

		final CSVSourceResourceTriplesFlow flow = injector
				.getInstance(CSVResourceFlowFactory.class)
				.fromConfiguration(configuration);

		@SuppressWarnings("unchecked")
		final Matcher<String> predicateMatcher = CoreMatchers.anyOf(
				CoreMatchers.equalTo("column1"),
				CoreMatchers.equalTo("column2"),
				CoreMatchers.equalTo("column3"),
				CoreMatchers.equalTo("column4"),
				CoreMatchers.equalTo("column5"),
				CoreMatchers.equalTo("column6"),
				CoreMatchers.equalTo("column7"),
				CoreMatchers.equalTo("column8"),
				CoreMatchers.equalTo("column9"),
				CoreMatchers.equalTo("column10"),
				CoreMatchers.equalTo("column11"),
				CoreMatchers.equalTo("column12"),
				CoreMatchers.equalTo("column13"),
				CoreMatchers.equalTo("column14"),
				CoreMatchers.equalTo("column15"),
				CoreMatchers.equalTo("column16"),
				CoreMatchers.equalTo("column17"),
				CoreMatchers.equalTo("column18"),
				CoreMatchers.equalTo("column19"));

		testFlow(flow, "excerpt_w_linebreak_in_value.csv", 3, 19, predicateMatcher);
	}

	@Test
	public void testFromConfiguration() throws Exception {

		final String uuid = UUIDService.getUUID(Configuration.class.getSimpleName());

		final Configuration configuration = new Configuration(uuid);

		configuration.addParameter(ConfigurationStatics.COLUMN_DELIMITER, new TextNode(";"));
		configuration.addParameter(ConfigurationStatics.ENCODING, new TextNode("UTF-8"));
		configuration.addParameter(ConfigurationStatics.ESCAPE_CHARACTER, new TextNode("\\"));
		configuration.addParameter(ConfigurationStatics.QUOTE_CHARACTER, new TextNode("\""));
		configuration.addParameter(ConfigurationStatics.ROW_DELIMITER, new TextNode("\n"));

		final CSVSourceResourceTriplesFlow flow = injector
				.getInstance(CSVResourceFlowFactory.class)
				.fromConfiguration(configuration);

		@SuppressWarnings("unchecked")
		final Matcher<String> predicateMatcher = CoreMatchers.anyOf(CoreMatchers.equalTo("id"), CoreMatchers.equalTo("name"),
				CoreMatchers.equalTo("description"), CoreMatchers.equalTo("isbn"), CoreMatchers.equalTo("year"));

		testFlow(flow, "test_csv.csv", 19, 5, predicateMatcher);
	}

	@Test(expected = ProvisionException.class)
	public void testNullConfiguration() throws Exception {
		final Configuration configuration = null;
		@SuppressWarnings("UnusedDeclaration")
		final CSVSourceResourceTriplesFlow flow = injector
				.getInstance(CSVResourceFlowFactory.class)
				.fromConfiguration(configuration);
	}

	@Test(expected = ProvisionException.class)
	public void testNullConfigurationParameter() throws Exception {

		final String uuid = UUIDService.getUUID(Configuration.class.getSimpleName());

		final Configuration configuration = new Configuration(uuid);
		@SuppressWarnings("UnusedDeclaration")

		final CSVSourceResourceTriplesFlow flow = injector
				.getInstance(CSVResourceFlowFactory.class)
				.fromConfiguration(configuration);
	}

	@Test
	public void testDefaultValues() throws Exception {

		final String uuid = UUIDService.getUUID(Configuration.class.getSimpleName());

		final Configuration configuration = new Configuration(uuid);

		configuration.addParameter(ConfigurationStatics.COLUMN_DELIMITER, new TextNode(";"));

		final CSVSourceResourceTriplesFlow flow = injector
				.getInstance(CSVResourceFlowFactory.class)
				.fromConfiguration(configuration);

		@SuppressWarnings("unchecked")
		final Matcher<String> predicateMatcher = CoreMatchers.anyOf(CoreMatchers.equalTo("id"), CoreMatchers.equalTo("name"),
				CoreMatchers.equalTo("description"), CoreMatchers.equalTo("isbn"), CoreMatchers.equalTo("year"));

		testFlow(flow, "test_csv.csv", 19, 5, predicateMatcher);
	}

}
