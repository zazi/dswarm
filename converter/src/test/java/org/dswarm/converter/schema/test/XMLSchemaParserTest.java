/**
 * Copyright (C) 2013 – 2016 SLUB Dresden & Avantgarde Labs GmbH (<code@dswarm.org>)
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
package org.dswarm.converter.schema.test;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.inject.Provider;
import org.junit.Assert;
import org.junit.Test;

import org.dswarm.common.types.Tuple;
import org.dswarm.converter.GuicedTest;
import org.dswarm.converter.schema.AbstractJSONSchemaParser;
import org.dswarm.converter.schema.XMLSchemaParser;
import org.dswarm.persistence.DMPPersistenceException;
import org.dswarm.persistence.model.internal.helper.AttributePathHelper;
import org.dswarm.persistence.model.internal.helper.AttributePathHelperHelper;
import org.dswarm.persistence.model.schema.AttributePath;
import org.dswarm.persistence.model.schema.ContentSchema;
import org.dswarm.persistence.model.schema.Schema;
import org.dswarm.persistence.model.schema.utils.SchemaUtils;
import org.dswarm.persistence.service.UUIDService;
import org.dswarm.persistence.service.schema.AttributePathService;
import org.dswarm.persistence.service.schema.AttributeService;
import org.dswarm.persistence.service.schema.SchemaAttributePathInstanceService;
import org.dswarm.persistence.service.schema.SchemaService;

/**
 * @author tgaengler
 */
public class XMLSchemaParserTest extends AbstractJSONSchemaParserTest {

	private static final AbstractJSONSchemaParser XML_SCHEMA_PARSER = GuicedTest.injector.getInstance(XMLSchemaParser.class);

	@Test
	public void testAttributePathsParsingForMabxml() throws IOException {

		testAttributePathsParsing("mabxml-1.xsd", "datensatz", "mabxml-1.attribute_paths.txt", false, XML_SCHEMA_PARSER);
	}

	@Test
	public void testAttributePathsParsingForSpringerJournals() throws IOException {

		testAttributePathsParsing("springer_journals.xsd", "Publisher", "springer_journals.attribute_paths.txt", false, XML_SCHEMA_PARSER);
	}

	@Test
	public void testAttributePathsParsingForMarcXml() throws IOException {

		testAttributePathsParsing("MARC21slim.xsd", "record", "marcxml_schema_attribute_paths.txt", false, XML_SCHEMA_PARSER);
	}

	@Test
	public void testAttributePathsParsingForPNX() throws IOException {

		testAttributePathsParsing("pnx.xsd", "record", "pnx_schema_-_attribute_paths.txt", false, XML_SCHEMA_PARSER);
	}

	@Test
	public void testAttributePathsParsingForDCElements() throws IOException {

		testAttributePathsParsing("dc.xsd", null, "dc_elements_schema_-_attribute_paths.txt", false, XML_SCHEMA_PARSER);
	}

	@Test
	public void testAttributePathsParsingForOAIDCElements() throws IOException {

		testAttributePathsParsing("oai_dc.xsd", "dc", "oai_dc_elements_schema_-_attribute_paths.txt", true, XML_SCHEMA_PARSER);
	}

	@Test
	public void testAttributePathsParsingForDCTerms() throws IOException {

		testAttributePathsParsing("dcterms.xsd", null, "dc_terms_schema_-_attribute_paths.txt", false, XML_SCHEMA_PARSER);
	}

	@Test
	public void testAttributePathsParsingForOAIPMH() throws IOException {

		testAttributePathsParsing("OAI-PMH.xsd", "record", "oai-pmh_schema_-_attribute_paths.txt", false, XML_SCHEMA_PARSER);
	}

	@Test
	public void testAttributePathsParsingForSRU11() throws IOException {

		testAttributePathsParsing("srw-types.1_1.xsd", "record", "sru_1_1_schema_-_attribute_paths.txt", false, XML_SCHEMA_PARSER);
	}

	@Test
	public void testAttributePathsParsingForPICAPlusXml() throws IOException {

		testAttributePathsParsing("ppxml-1.0.xsd", "record", "pica_schema_-_attribute_paths.txt", false, XML_SCHEMA_PARSER);
	}

	@Test
	public void testAttributePathsParsingForPICAPlusXmlGlobal() throws IOException {

		final Set<String> excludeAttributePathStubs = new HashSet<>();

		excludeAttributePathStubs.add("http://www.oclcpica.org/xmlns/ppxml-1.0#owner");

		testAttributePathsParsing("ppxml-1.0.xsd", "record", "pica_global_schema_-_attribute_paths.txt", false, Optional.of(excludeAttributePathStubs), XML_SCHEMA_PARSER);
	}

	/**
	 * note: http://europeana.eu/terms is not the original EDM namespace URI; http://www.europeana.eu/schemas/edm/ is the original namespace URI
	 *
	 * @throws IOException
	 */
	@Test
	public void testAttributePathsParsingForEDM() throws IOException {

		testAttributePathsParsing("dd-1168/EDM-COMMON-MAIN.xsd", null, "dd-1168/edm_schema_-_attribute_paths.txt", false, XML_SCHEMA_PARSER);
	}

	@Test
	public void testAttributePathsParsingForOAIPMHPlusOAIDCElements() throws IOException {

		final Map<String, AttributePathHelper> rootAttributePaths = parseAttributePaths("OAI-PMH.xsd", "record", false, XML_SCHEMA_PARSER);

		final String rootAttributePathIdentifier = "http://www.openarchives.org/OAI/2.0/metadata";
		final AttributePathHelper rootAttributePath = rootAttributePaths.get(rootAttributePathIdentifier);

		final Map<String, AttributePathHelper> childAttributePaths = parseAttributePaths("oai_dc.xsd", "dc", true, XML_SCHEMA_PARSER);

		final Map<String, AttributePathHelper> newAttributePaths = composeAttributePaths(rootAttributePaths, rootAttributePath, childAttributePaths);

		compareAttributePaths("oai-pmh_plus_oai_dc_elements_schema_-_attribute_paths.txt", newAttributePaths);
	}

	@Test
	public void testAttributePathsParsingForOAIPMHPlusOAIDCElementsAndEDM() throws IOException {

		final Map<String, AttributePathHelper> rootAttributePaths = parseAttributePaths("OAI-PMH.xsd", "record", false, XML_SCHEMA_PARSER);

		final Map<String, AttributePathHelper> newAttributePaths = buildOAIPMHPlusDCElementsAndEDMAttributePaths(rootAttributePaths);

		compareAttributePaths("oai-pmh_plus_oai_dc_elements_and_edm_schema_-_attribute_paths.txt", newAttributePaths);
	}

	@Test
	public void testAttributePathsParsingForSRU11PlusPICAPlusXML() throws IOException {

		final Map<String, AttributePathHelper> rootAttributePaths = parseAttributePaths("srw-types.1_1.xsd", "record", false, XML_SCHEMA_PARSER);

		final String rootAttributePathIdentifier = "http://www.loc.gov/zing/srw/recordData";
		final AttributePathHelper rootAttributePath = rootAttributePaths.get(rootAttributePathIdentifier);

		final Set<String> excludeAttributePathStubs = new HashSet<>();

		excludeAttributePathStubs.add("http://www.oclcpica.org/xmlns/ppxml-1.0#record\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#owner");

		final Map<String, AttributePathHelper> childAttributePaths = parseAttributePaths("ppxml-1.0.xsd", "record", true, Optional.of(excludeAttributePathStubs), XML_SCHEMA_PARSER);

		final Map<String, AttributePathHelper> newAttributePaths = composeAttributePaths(rootAttributePaths, rootAttributePath, childAttributePaths);

		compareAttributePaths("sru_1_1_plus_picaplus_xml_schema_-_attribute_paths.txt", newAttributePaths);
	}

	private static Map<String, AttributePathHelper> buildOAIPMHPlusDCElementsAndEDMAttributePaths(final Map<String, AttributePathHelper> rootAttributePaths) {
		final String rootAttributePathIdentifier = "http://www.openarchives.org/OAI/2.0/metadata";
		final AttributePathHelper rootAttributePath = rootAttributePaths.get(rootAttributePathIdentifier);

		final Map<String, AttributePathHelper> childAttributePaths = parseAttributePaths("oai_dc.xsd", "dc", true, XML_SCHEMA_PARSER);

		final String childAttributePathIdentifier = "http://www.openarchives.org/OAI/2.0/oai_dc/dc";
		final AttributePathHelper childAttributePath = childAttributePaths.get(childAttributePathIdentifier);

		final Map<String, AttributePathHelper> childAttributePaths3 = new LinkedHashMap<>();
		childAttributePaths3.put(childAttributePathIdentifier, childAttributePath);

		// insert OAI-PMH DC attribute before EDM, because it should be the root attribute path for EDM
		final Map<String, AttributePathHelper> rootAttributePaths3 = composeAttributePaths(rootAttributePaths, rootAttributePath,
				childAttributePaths3);

		final String rootAttributePathIdentifier2 = "http://www.openarchives.org/OAI/2.0/metadata\u001Ehttp://www.openarchives.org/OAI/2.0/oai_dc/dc";
		final AttributePathHelper rootAttributePath2 = rootAttributePaths3.get(rootAttributePathIdentifier2);

		final Map<String, AttributePathHelper> childAttributePaths2 = parseAttributePaths("dd-1168/EDM-COMMON-MAIN.xsd", null, false, XML_SCHEMA_PARSER);

		// insert EDM schema before, to guarantee the order DCE before EDM
		final Map<String, AttributePathHelper> newAttributePaths2 = composeAttributePaths(rootAttributePaths3, rootAttributePath2,
				childAttributePaths2);

		return composeAttributePaths(newAttributePaths2, rootAttributePath, childAttributePaths);
	}

	@Test
	public void testAttributePathsParsingForOAIPMHPlusDCTerms() throws IOException {

		final Map<String, AttributePathHelper> rootAttributePaths = parseAttributePaths("OAI-PMH.xsd", "record", false, XML_SCHEMA_PARSER);

		final String rootAttributePathIdentifier = "http://www.openarchives.org/OAI/2.0/metadata";
		final AttributePathHelper rootAttributePath = rootAttributePaths.get(rootAttributePathIdentifier);

		final Map<String, AttributePathHelper> childAttributePaths = parseAttributePaths("dcterms.xsd", null, false, XML_SCHEMA_PARSER);

		final Map<String, AttributePathHelper> newAttributePaths = composeAttributePaths(rootAttributePaths, rootAttributePath, childAttributePaths);

		compareAttributePaths("oai-pmh_plus_dc_terms_schema_-_attribute_paths.txt", newAttributePaths);
	}

	@Test
	public void testAttributePathsParsingForOAIPMHPlusMARCXML() throws IOException {

		final Map<String, AttributePathHelper> rootAttributePaths = parseAttributePaths("OAI-PMH.xsd", "record", false, XML_SCHEMA_PARSER);

		final String rootAttributePathIdentifier = "http://www.openarchives.org/OAI/2.0/metadata";
		final AttributePathHelper rootAttributePath = rootAttributePaths.get(rootAttributePathIdentifier);

		// collection - to include "record" attribute into the attribute paths
		final Map<String, AttributePathHelper> childAttributePaths = parseAttributePaths("MARC21slim.xsd", "collection", false, XML_SCHEMA_PARSER);

		final Map<String, AttributePathHelper> newAttributePaths = composeAttributePaths(rootAttributePaths, rootAttributePath, childAttributePaths);

		compareAttributePaths("oai-pmh_plus_marcxml_schema_-_attribute_paths.txt", newAttributePaths);
	}

	public static Schema parseOAIPMHPlusDCElementsSchema(final Optional<Map<String, String>> optionalAttributePathsSAPIUUIDs,
	                                                     final Optional<String> optionalContentSchemaIdentifier) throws DMPPersistenceException {

		final Schema schema = parseOAIPMHPlusXSchema("oai_dc.xsd", "DC Elements", SchemaUtils.OAI_PMH_DC_ELEMENTS_SCHEMA_UUID, "dc", true, optionalAttributePathsSAPIUUIDs);

		final Map<String, AttributePath> aps = SchemaUtils.generateAttributePathMap(schema);

		final String uuid = getOrCreateContentSchemaIdentifier(optionalContentSchemaIdentifier);

		final ContentSchema contentSchema = new ContentSchema(uuid);
		contentSchema.setName("OAI-PMH + DC Elements content schema");

		final AttributePath id = aps
				.get("http://www.openarchives.org/OAI/2.0/header\u001Ehttp://www.openarchives.org/OAI/2.0/identifier\u001Ehttp://www.w3.org/1999/02/22-rdf-syntax-ns#value");

		return fillContentSchemaAndUpdateSchema(contentSchema, id, null, schema);
	}

	@Test
	public void parseOAIPMHPlusDCElementsSchema() throws DMPPersistenceException {

		parseOAIPMHPlusDCElementsSchema(Optional.empty(), Optional.empty());
	}

	public static Schema parseOAIPMHPlusDCElementsAndEDMSchema(final Optional<Map<String, String>> optionalAttributePathsSAPIUUIDs,
	                                                           final Optional<String> optionalContentSchemaIdentifier) throws DMPPersistenceException {

		final String childSchemaName = "DC Elements + EDM";
		final Tuple<Schema, Map<String, AttributePathHelper>> result = parseSchemaSeparately("OAI-PMH.xsd", "record",
				SchemaUtils.OAI_PMH_DC_ELEMENTS_AND_EDM_SCHEMA_UUID, "OAI-PMH + " + childSchemaName + " schema");

		final Map<String, AttributePathHelper> rootAttributePaths = result.v2();

		final Map<String, AttributePathHelper> newAttributePaths = buildOAIPMHPlusDCElementsAndEDMAttributePaths(rootAttributePaths);

		final Schema schema = result.v1();
		final Schema updatedSchema = addChildSchemataAttributePathsToSchema(newAttributePaths, schema, optionalAttributePathsSAPIUUIDs);

		final Map<String, AttributePath> aps = SchemaUtils.generateAttributePathMap(updatedSchema);

		final String uuid = getOrCreateContentSchemaIdentifier(optionalContentSchemaIdentifier);

		final ContentSchema contentSchema = new ContentSchema(uuid);
		contentSchema.setName("OAI-PMH + DC Elements + EDM content schema");

		final AttributePath id = aps
				.get("http://www.openarchives.org/OAI/2.0/header\u001Ehttp://www.openarchives.org/OAI/2.0/identifier\u001Ehttp://www.w3.org/1999/02/22-rdf-syntax-ns#value");

		return fillContentSchemaAndUpdateSchema(contentSchema, id, null, updatedSchema);
	}

	@Test
	public void parseOAIPMHPlusDCElementsAndEDMSchema() throws DMPPersistenceException {

		parseOAIPMHPlusDCElementsAndEDMSchema(Optional.empty(), Optional.empty());
	}

	public static Schema parseOAIPMHPlusDCTermsSchema(final Optional<Map<String, String>> optionalAttributePathsSAPIUUIDs,
	                                                  final Optional<String> optionalContentSchemaIdentifier) throws DMPPersistenceException {

		final Schema schema = parseOAIPMHPlusXSchema("dcterms.xsd", "DC Terms", SchemaUtils.OAI_PMH_DC_TERMS_SCHEMA_UUID, null, false, optionalAttributePathsSAPIUUIDs);

		final Map<String, AttributePath> aps = SchemaUtils.generateAttributePathMap(schema);

		final String uuid = getOrCreateContentSchemaIdentifier(optionalContentSchemaIdentifier);

		final ContentSchema contentSchema = new ContentSchema(uuid);
		contentSchema.setName("OAI-PMH + DC Terms content schema");

		final AttributePath id = aps
				.get("http://www.openarchives.org/OAI/2.0/header\u001Ehttp://www.openarchives.org/OAI/2.0/identifier\u001Ehttp://www.w3.org/1999/02/22-rdf-syntax-ns#value");

		return fillContentSchemaAndUpdateSchema(contentSchema, id, null, schema);
	}

	@Test
	public void parseOAIPMHPlusDCTermsSchema() throws DMPPersistenceException {

		parseOAIPMHPlusDCTermsSchema(Optional.empty(), Optional.empty());
	}

	public static Schema parseOAIPMHPlusMARCXMLSchema(final Optional<Map<String, String>> optionalAttributePathsSAPIUUIDs,
	                                                  final Optional<String> optionalContentSchemaIdentifier) throws DMPPersistenceException {

		// collection - to include "record" attribute into the attribute paths
		final Schema schema = parseOAIPMHPlusXSchema("MARC21slim.xsd", "MARCXML", SchemaUtils.OAI_PMH_MARCXML_SCHEMA_UUID, "collection", false, optionalAttributePathsSAPIUUIDs);

		final Map<String, AttributePath> aps = SchemaUtils.generateAttributePathMap(schema);

		final String uuid = getOrCreateContentSchemaIdentifier(optionalContentSchemaIdentifier);

		final ContentSchema contentSchema = new ContentSchema(uuid);
		contentSchema.setName("OAI-PMH + MARCXML content schema");

		final AttributePath datafieldTag = aps
				.get("http://www.openarchives.org/OAI/2.0/metadata\u001Ehttp://www.loc.gov/MARC21/slim#record\u001Ehttp://www.loc.gov/MARC21/slim#datafield\u001Ehttp://www.loc.gov/MARC21/slim#tag");
		final AttributePath datafieldInd1 = aps
				.get("http://www.openarchives.org/OAI/2.0/metadata\u001Ehttp://www.loc.gov/MARC21/slim#record\u001Ehttp://www.loc.gov/MARC21/slim#datafield\u001Ehttp://www.loc.gov/MARC21/slim#ind1");
		final AttributePath datafieldInd2 = aps
				.get("http://www.openarchives.org/OAI/2.0/metadata\u001Ehttp://www.loc.gov/MARC21/slim#record\u001Ehttp://www.loc.gov/MARC21/slim#datafield\u001Ehttp://www.loc.gov/MARC21/slim#ind2");
		final AttributePath datafieldSubfieldCode = aps
				.get("http://www.openarchives.org/OAI/2.0/metadata\u001Ehttp://www.loc.gov/MARC21/slim#record\u001Ehttp://www.loc.gov/MARC21/slim#datafield\u001Ehttp://www.loc.gov/MARC21/slim#subfield\u001Ehttp://www.loc.gov/MARC21/slim#code");
		final AttributePath id = aps
				.get("http://www.openarchives.org/OAI/2.0/header\u001Ehttp://www.openarchives.org/OAI/2.0/identifier\u001Ehttp://www.w3.org/1999/02/22-rdf-syntax-ns#value");
		final AttributePath datafieldSubfieldValue = aps
				.get("http://www.openarchives.org/OAI/2.0/metadata\u001Ehttp://www.loc.gov/MARC21/slim#record\u001Ehttp://www.loc.gov/MARC21/slim#datafield\u001Ehttp://www.loc.gov/MARC21/slim#subfield\u001Ehttp://www.w3.org/1999/02/22-rdf-syntax-ns#value");

		contentSchema.addKeyAttributePath(datafieldTag);
		contentSchema.addKeyAttributePath(datafieldInd1);
		contentSchema.addKeyAttributePath(datafieldInd2);
		contentSchema.addKeyAttributePath(datafieldSubfieldCode);

		return fillContentSchemaAndUpdateSchema(contentSchema, id, datafieldSubfieldValue, schema);
	}

	public static Schema parseSRU11PlusPICAPlusXMLSchema(final Optional<Map<String, String>> optionalAttributePathsSAPIUUIDs,
	                                                     final Optional<String> optionalContentSchemaIdentifier) throws DMPPersistenceException {

		final Set<String> excludeAttributePathStubs = new HashSet<>();

		excludeAttributePathStubs.add("http://www.oclcpica.org/xmlns/ppxml-1.0#record\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#owner");

		// collection - to include "record" attribute into the attribute paths
		final Schema schema = parseSRU11PlusXSchema("ppxml-1.0.xsd", "PICA+ XML 'global'", SchemaUtils.SRU_11_PICAPLUSXML_GLOBAL_SCHEMA_UUID, "record", true, optionalAttributePathsSAPIUUIDs, Optional.of(excludeAttributePathStubs));

		final Map<String, AttributePath> aps = SchemaUtils.generateAttributePathMap(schema);

		final String uuid = getOrCreateContentSchemaIdentifier(optionalContentSchemaIdentifier);

		final ContentSchema contentSchema = new ContentSchema(uuid);
		contentSchema.setName("SRU 1.1 + PICA+ XML 'global' content schema");

		final AttributePath globalTagId = aps
				.get("http://www.loc.gov/zing/srw/recordData\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#record\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#global\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#tag\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#id");
		final AttributePath globalTagOcc = aps
				.get("http://www.loc.gov/zing/srw/recordData\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#record\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#global\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#tag\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#occ");
		final AttributePath globalTagSubfId = aps
				.get("http://www.loc.gov/zing/srw/recordData\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#record\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#global\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#tag\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#subf\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#id");
		final AttributePath globalTagSubfValue = aps
				.get("http://www.loc.gov/zing/srw/recordDatahttp://www.oclcpica.org/xmlns/ppxml-1.0#recordhttp://www.oclcpica.org/xmlns/ppxml-1.0#globalhttp://www.oclcpica.org/xmlns/ppxml-1.0#taghttp://www.oclcpica.org/xmlns/ppxml-1.0#subfhttp://www.w3.org/1999/02/22-rdf-syntax-ns#value");
		contentSchema.addKeyAttributePath(globalTagId);
		contentSchema.addKeyAttributePath(globalTagOcc);
		contentSchema.addKeyAttributePath(globalTagSubfId);

		return fillContentSchemaAndUpdateSchema(contentSchema, null, globalTagSubfValue, schema);
	}

	@Test
	public void parseOAIPMHPlusMARCXMLSchema() throws DMPPersistenceException {

		parseOAIPMHPlusMARCXMLSchema(Optional.empty(), Optional.empty());
	}

	/**
	 * note: creates the mabxml from the given xml schema file from scratch
	 *
	 * @throws IOException
	 * @throws DMPPersistenceException
	 */
	@Test
	public void testSchemaParsing() throws IOException, DMPPersistenceException {

		final String schemaUUID = UUIDService.getUUID(Schema.class.getSimpleName());

		final XMLSchemaParser schemaParser = GuicedTest.injector.getInstance(XMLSchemaParser.class);
		final java.util.Optional<Schema> optionalSchema = schemaParser.parse("mabxml-1.xsd", "datensatz", schemaUUID, "mabxml schema");

		Assert.assertTrue(optionalSchema.isPresent());
	}

	/**
	 * note: creates the mabxml from the given xml schema file from scratch (by optionally reutilising existing SAPIs) + adds content schema programmatically
	 *
	 * @throws IOException
	 * @throws DMPPersistenceException
	 */
	public static Schema parseMabxmlSchema(final Optional<Map<String, String>> optionalAttributePathsSAPIUUIDs,
	                                       final Optional<String> optionalContentSchemaIdentifier) throws IOException, DMPPersistenceException {

		final Schema schema = parseSchema("mabxml-1.xsd", "datensatz", SchemaUtils.MABXML_SCHEMA_UUID, "mabxml schema", optionalAttributePathsSAPIUUIDs, GuicedTest.injector.getInstance(XMLSchemaParser.class));

		final Map<String, AttributePath> aps = SchemaUtils.generateAttributePathMap(schema);

		final String uuid = getOrCreateContentSchemaIdentifier(optionalContentSchemaIdentifier);

		final ContentSchema contentSchema = new ContentSchema(uuid);
		contentSchema.setName("mab content schema");

		final AttributePath feldNr = aps
				.get("http://www.ddb.de/professionell/mabxml/mabxml-1.xsd#feldhttp://www.ddb.de/professionell/mabxml/mabxml-1.xsd#nr");
		final AttributePath feldInd = aps
				.get("http://www.ddb.de/professionell/mabxml/mabxml-1.xsd#feldhttp://www.ddb.de/professionell/mabxml/mabxml-1.xsd#ind");
		final AttributePath id = aps.get("http://www.ddb.de/professionell/mabxml/mabxml-1.xsd#id");
		final AttributePath feldValue = aps
				.get("http://www.ddb.de/professionell/mabxml/mabxml-1.xsd#feldhttp://www.w3.org/1999/02/22-rdf-syntax-ns#value");

		contentSchema.addKeyAttributePath(feldNr);
		contentSchema.addKeyAttributePath(feldInd);

		return fillContentSchemaAndUpdateSchema(contentSchema, id, feldValue, schema);
	}

	/**
	 * note: creates the PICA+ XML schema from the given xml schema file from scratch (by optionally reutilising existing SAPIs) + adds content schema programmatically
	 *
	 * @throws IOException
	 * @throws DMPPersistenceException
	 */
	public static Schema parsePicaPlusXmlSchema(final Optional<Map<String, String>> optionalAttributePathsSAPIUUIDs,
	                                            final Optional<String> optionalContentSchemaIdentifier) throws IOException, DMPPersistenceException {

		final Schema schema = parseSchema("ppxml-1.0.xsd", "record", SchemaUtils.PICAPLUSXML_SCHEMA_UUID, "PICA+ XML schema", optionalAttributePathsSAPIUUIDs, XML_SCHEMA_PARSER);

		final Map<String, AttributePath> aps = SchemaUtils.generateAttributePathMap(schema);

		final String uuid = getOrCreateContentSchemaIdentifier(optionalContentSchemaIdentifier);

		final ContentSchema contentSchema = new ContentSchema(uuid);
		contentSchema.setName("PICA+ content schema");

		final AttributePath globalTagId = aps
				.get("http://www.oclcpica.org/xmlns/ppxml-1.0#global\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#tag\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#id");
		final AttributePath globalTagOcc = aps
				.get("http://www.oclcpica.org/xmlns/ppxml-1.0#global\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#tag\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#occ");
		final AttributePath globalTagSubfId = aps.get("http://www.oclcpica.org/xmlns/ppxml-1.0#global\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#tag\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#subf\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#id");
		final AttributePath globalTagSubfValue = aps
				.get("http://www.oclcpica.org/xmlns/ppxml-1.0#global\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#tag\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#subf\u001Ehttp://www.w3.org/1999/02/22-rdf-syntax-ns#value");

		contentSchema.addKeyAttributePath(globalTagId);
		contentSchema.addKeyAttributePath(globalTagOcc);
		contentSchema.addKeyAttributePath(globalTagSubfId);

		return fillContentSchemaAndUpdateSchema(contentSchema, null, globalTagSubfValue, schema);
	}

	/**
	 * note: creates the PICA+ XML 'global' schema (only attribute paths that start with 'http://www.oclcpica.org/xmlns/ppxml-1.0#owner') from the given xml schema file from scratch (by optionally reutilising existing SAPIs) + adds content schema programmatically
	 *
	 * @throws IOException
	 * @throws DMPPersistenceException
	 */
	public static Schema parsePicaPlusXmlGlobalSchema(final Optional<Map<String, String>> optionalAttributePathsSAPIUUIDs,
	                                                  final Optional<String> optionalContentSchemaIdentifier) throws IOException, DMPPersistenceException {

		final Set<String> excludeAttributePathStubs = new HashSet<>();

		excludeAttributePathStubs.add("http://www.oclcpica.org/xmlns/ppxml-1.0#owner");

		final Schema schema = parseSchema("ppxml-1.0.xsd", "record", SchemaUtils.PICAPLUSXML_GLOBAL_SCHEMA_UUID, "PICA+ XML 'global' schema", optionalAttributePathsSAPIUUIDs, Optional.of(excludeAttributePathStubs), XML_SCHEMA_PARSER);

		final Map<String, AttributePath> aps = SchemaUtils.generateAttributePathMap(schema);

		final String uuid = getOrCreateContentSchemaIdentifier(optionalContentSchemaIdentifier);

		final ContentSchema contentSchema = new ContentSchema(uuid);
		contentSchema.setName("PICA+ content schema");

		final AttributePath globalTagId = aps
				.get("http://www.oclcpica.org/xmlns/ppxml-1.0#global\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#tag\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#id");
		final AttributePath globalTagOcc = aps
				.get("http://www.oclcpica.org/xmlns/ppxml-1.0#global\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#tag\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#occ");
		final AttributePath globalTagSubfId = aps.get("http://www.oclcpica.org/xmlns/ppxml-1.0#global\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#tag\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#subf\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#id");
		final AttributePath globalTagSubfValue = aps
				.get("http://www.oclcpica.org/xmlns/ppxml-1.0#global\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#tag\u001Ehttp://www.oclcpica.org/xmlns/ppxml-1.0#subf\u001Ehttp://www.w3.org/1999/02/22-rdf-syntax-ns#value");

		contentSchema.addKeyAttributePath(globalTagId);
		contentSchema.addKeyAttributePath(globalTagOcc);
		contentSchema.addKeyAttributePath(globalTagSubfId);

		return fillContentSchemaAndUpdateSchema(contentSchema, null, globalTagSubfValue, schema);
	}

	/**
	 * note: creates the mabxml from the given xml schema file from scratch + adds content schema programmatically
	 *
	 * @throws IOException
	 * @throws DMPPersistenceException
	 */
	@Test
	public void parseMabxmlSchema() throws IOException, DMPPersistenceException {

		parseMabxmlSchema(Optional.empty(), Optional.empty());
	}

	/**
	 * creates the PNX schema from the given XML schema file from scratch (by optionally reutilising existing SAPIs)
	 *
	 * @throws IOException
	 * @throws DMPPersistenceException
	 */
	public static Schema parsePNXSchema(final Optional<Map<String, String>> optionalAttributePathsSAPIUUIDs,
	                                    final Optional<String> optionalContentSchemaIdentifier) throws IOException, DMPPersistenceException {

		final Schema schema = parseSchema("pnx.xsd", "record", SchemaUtils.PNX_SCHEMA_UUID, "pnx schema", optionalAttributePathsSAPIUUIDs, GuicedTest.injector.getInstance(XMLSchemaParser.class));

		final Map<String, AttributePath> aps = SchemaUtils.generateAttributePathMap(schema);

		final String uuid = getOrCreateContentSchemaIdentifier(optionalContentSchemaIdentifier);

		final ContentSchema contentSchema = new ContentSchema(uuid);
		contentSchema.setName("PNX content schema");

		final AttributePath id = aps
				.get("http://www.exlibrisgroup.com/xsd/primo/primo_nm_bib#control\u001Ehttp://www.exlibrisgroup.com/xsd/primo/primo_nm_bib#sourcerecordid\u001Ehttp://www.w3.org/1999/02/22-rdf-syntax-ns#value");

		return fillContentSchemaAndUpdateSchema(contentSchema, id, null, schema);
	}

	/**
	 * creates the PNX schema from the given XML schema file from scratch
	 *
	 * @throws IOException
	 * @throws DMPPersistenceException
	 */
	@Test
	public void parsePNXSchema() throws IOException, DMPPersistenceException {

		parsePNXSchema(Optional.empty(), Optional.empty());
	}

	/**
	 * creates the Springer Journals schema from the given XML schema file from scratch (by optionally reutilising existing SAPIs)
	 *
	 * @throws IOException
	 * @throws DMPPersistenceException
	 */
	public static Schema parseSpringerJournalsSchema(final Optional<Map<String, String>> optionalAttributePathsSAPIUUIDs) throws IOException, DMPPersistenceException {

		final Schema schema = parseSchema("springer_journals.xsd", "Publisher", SchemaUtils.SPRINGER_JOURNALS_SCHEMA_UUID, "Springer Journals schema", optionalAttributePathsSAPIUUIDs, GuicedTest.injector.getInstance(XMLSchemaParser.class));

		return schema;
	}

	/**
	 * creates the Springer Journals schema from the given XML schema file from scratch
	 *
	 * @throws IOException
	 * @throws DMPPersistenceException
	 */
	@Test
	public void parseSpringerJournalsSchema() throws IOException, DMPPersistenceException {

		parseSpringerJournalsSchema(Optional.empty());
	}

	/**
	 * creates the MARC XML schema from the given XML schema file from scratch (by optionally reutilising existing SAPIs)
	 *
	 * @throws IOException
	 * @throws DMPPersistenceException
	 */
	public static Schema parseMarcXmlSchema(final Optional<Map<String, String>> optionalAttributePathsSAPIUUIDs,
	                                        final Optional<String> optionalContentSchemaIdentifier) throws IOException, DMPPersistenceException {

		final Schema schema = parseSchema("MARC21slim.xsd", "record", SchemaUtils.MARCXML_SCHEMA_UUID, "MARCXML schema", optionalAttributePathsSAPIUUIDs, GuicedTest.injector.getInstance(XMLSchemaParser.class));

		final Map<String, AttributePath> aps = SchemaUtils.generateAttributePathMap(schema);

		final String uuid = getOrCreateContentSchemaIdentifier(optionalContentSchemaIdentifier);

		final ContentSchema contentSchema = new ContentSchema(uuid);
		contentSchema.setName("MARC21 content schema");

		final AttributePath datafieldTag = aps
				.get("http://www.loc.gov/MARC21/slim#datafield\u001Ehttp://www.loc.gov/MARC21/slim#tag");
		final AttributePath datafieldInd1 = aps
				.get("http://www.loc.gov/MARC21/slim#datafield\u001Ehttp://www.loc.gov/MARC21/slim#ind1");
		final AttributePath datafieldInd2 = aps
				.get("http://www.loc.gov/MARC21/slim#datafield\u001Ehttp://www.loc.gov/MARC21/slim#ind2");
		final AttributePath datafieldSubfieldCode = aps
				.get("http://www.loc.gov/MARC21/slim#datafield\u001Ehttp://www.loc.gov/MARC21/slim#subfield\u001Ehttp://www.loc.gov/MARC21/slim#code");
		final AttributePath id = aps.get("http://www.loc.gov/MARC21/slim#id");
		final AttributePath datafieldSubfieldValue = aps
				.get("http://www.loc.gov/MARC21/slim#datafield\u001Ehttp://www.loc.gov/MARC21/slim#subfield\u001Ehttp://www.w3.org/1999/02/22-rdf-syntax-ns#value");

		contentSchema.addKeyAttributePath(datafieldTag);
		contentSchema.addKeyAttributePath(datafieldInd1);
		contentSchema.addKeyAttributePath(datafieldInd2);
		contentSchema.addKeyAttributePath(datafieldSubfieldCode);

		return fillContentSchemaAndUpdateSchema(contentSchema, id, datafieldSubfieldValue, schema);
	}

	/**
	 * creates the MARC XML schema from the given XML schema file from scratch
	 *
	 * @throws IOException
	 * @throws DMPPersistenceException
	 */
	@Test
	public void parseMarcXmlSchema() throws IOException, DMPPersistenceException {

		parseMarcXmlSchema(Optional.empty(), Optional.empty());
	}

	private static Schema parseSchema(final String xsdFileName,
	                                  final String recordIdentifier,
	                                  final String schemaUUID,
	                                  final String schemaName) throws DMPPersistenceException {


		return parseSchema(xsdFileName, recordIdentifier, schemaUUID, schemaName, Optional.empty(), XML_SCHEMA_PARSER);
	}

	private static Tuple<Schema, Map<String, AttributePathHelper>> parseSchemaSeparately(final String xsdFileName,
	                                                                                     final String recordIdentifier,
	                                                                                     final String schemaUUID,
	                                                                                     final String schemaName) throws DMPPersistenceException {

		final java.util.Optional<Tuple<Schema, Map<String, AttributePathHelper>>> optionalResult = GuicedTest.injector.getInstance(XMLSchemaParser.class).parseSeparately(xsdFileName,
				recordIdentifier, schemaUUID, schemaName);

		Assert.assertTrue(optionalResult.isPresent());

		return optionalResult.get();
	}

	private static Map<String, AttributePathHelper> composeAttributePaths(final Map<String, AttributePathHelper> rootAttributePaths,
	                                                                      final AttributePathHelper rootAttributePath,
	                                                                      final Map<String, AttributePathHelper> childAttributePaths) {

		final Set<AttributePathHelper> compoundAttributePaths = new LinkedHashSet<>();

		for (final AttributePathHelper attributePath : childAttributePaths.values()) {

			AttributePathHelperHelper.addAttributePath(attributePath, compoundAttributePaths, rootAttributePath);
		}

		final Map<String, AttributePathHelper> newAttributePaths = new LinkedHashMap<>();
		final String rootAttributePathIdentifier = rootAttributePath.toString();

		for (final Map.Entry<String, AttributePathHelper> rootAttributePathEntry : rootAttributePaths.entrySet()) {

			newAttributePaths.put(rootAttributePathEntry.getKey(), rootAttributePathEntry.getValue());

			if (rootAttributePathEntry.getKey().equals(rootAttributePathIdentifier)) {

				for (final AttributePathHelper attributePath : compoundAttributePaths) {

					newAttributePaths.put(attributePath.toString(), attributePath);
				}
			}
		}

		return newAttributePaths;
	}

	private static Set<AttributePathHelper> convertToSet(final Map<String, AttributePathHelper> attributePathsMap) {

		final Set<AttributePathHelper> attributePaths = new LinkedHashSet<>();

		for (final AttributePathHelper attributePath : attributePathsMap.values()) {

			attributePaths.add(attributePath);
		}

		return attributePaths;
	}

	private static Schema parseOAIPMHPlusXSchema(final String childSchemaFileName,
	                                             final String childSchemaName,
	                                             final String schemaUUID,
	                                             final String childRecordIdentifier,
	                                             final boolean includeRecordTag) throws DMPPersistenceException {

		return parseOAIPMHPlusXSchema(childSchemaFileName, childSchemaName, schemaUUID, childRecordIdentifier, includeRecordTag, Optional.empty());
	}

	private static Schema parseOAIPMHPlusXSchema(final String childSchemaFileName,
	                                             final String childSchemaName,
	                                             final String schemaUUID,
	                                             final String childRecordIdentifier,
	                                             final boolean includeRecordTag,
	                                             final Optional<Map<String, String>> optionalAttributePathsSAPIUUIDs) throws DMPPersistenceException {

		final Tuple<Schema, Map<String, AttributePathHelper>> result = parseSchemaSeparately("OAI-PMH.xsd", "record", schemaUUID,
				"OAI-PMH + " + childSchemaName + " schema");

		final Map<String, AttributePathHelper> rootAttributePaths = result.v2();

		final String rootAttributePathIdentifier = "http://www.openarchives.org/OAI/2.0/metadata";
		final AttributePathHelper rootAttributePath = rootAttributePaths.get(rootAttributePathIdentifier);

		final Map<String, AttributePathHelper> childAttributePaths = parseAttributePaths(childSchemaFileName, childRecordIdentifier,
				includeRecordTag, GuicedTest.injector.getInstance(XMLSchemaParser.class));

		final Map<String, AttributePathHelper> newAttributePaths = composeAttributePaths(rootAttributePaths, rootAttributePath, childAttributePaths);

		final Schema schema = result.v1();

		return addChildSchemataAttributePathsToSchema(newAttributePaths, schema, optionalAttributePathsSAPIUUIDs);
	}

	private static Schema parseSRU11PlusXSchema(final String childSchemaFileName,
	                                            final String childSchemaName,
	                                            final String schemaUUID,
	                                            final String childRecordIdentifier,
	                                            final boolean includeRecordTag,
	                                            final Optional<Map<String, String>> optionalAttributePathsSAPIUUIDs,
	                                            final Optional<Set<String>> optionalExcludeAttributePathStubs) throws DMPPersistenceException {

		final Tuple<Schema, Map<String, AttributePathHelper>> result = parseSchemaSeparately("srw-types.1_1.xsd", "record", schemaUUID,
				"SRU 1.1 + " + childSchemaName + " schema");

		final Map<String, AttributePathHelper> rootAttributePaths = result.v2();

		final String rootAttributePathIdentifier = "http://www.loc.gov/zing/srw/recordData";
		final AttributePathHelper rootAttributePath = rootAttributePaths.get(rootAttributePathIdentifier);

		final Map<String, AttributePathHelper> childAttributePaths = parseAttributePaths(childSchemaFileName, childRecordIdentifier,
				includeRecordTag, optionalExcludeAttributePathStubs, XML_SCHEMA_PARSER);

		final Map<String, AttributePathHelper> newAttributePaths = composeAttributePaths(rootAttributePaths, rootAttributePath, childAttributePaths);

		final Schema schema = result.v1();

		return addChildSchemataAttributePathsToSchema(newAttributePaths, schema, optionalAttributePathsSAPIUUIDs);
	}

	private static Schema addChildSchemataAttributePathsToSchema(final Map<String, AttributePathHelper> newAttributePaths,
	                                                             final Schema schema,
	                                                             final Optional<Map<String, String>> optionalAttributePathsSAPIUUIDs) throws DMPPersistenceException {

		final Set<AttributePathHelper> attributePaths = convertToSet(newAttributePaths);
		final Provider<AttributePathService> attributePathServiceProvider = GuicedTest.injector.getProvider(AttributePathService.class);
		final Provider<SchemaAttributePathInstanceService> schemaAttributePathInstanceServiceProvider = GuicedTest.injector.getProvider(
				SchemaAttributePathInstanceService.class);
		final Provider<AttributeService> attributeServiceProvider = GuicedTest.injector.getProvider(AttributeService.class);
		final Provider<SchemaService> schemaServiceProvider = GuicedTest.injector.getProvider(SchemaService.class);

		SchemaUtils.addAttributePaths(schema, attributePaths, attributePathServiceProvider, schemaAttributePathInstanceServiceProvider,
				attributeServiceProvider, optionalAttributePathsSAPIUUIDs);

		return SchemaUtils.updateSchema(schema, schemaServiceProvider);
	}
}
