package de.avgl.dmp.controller.resources.job.test;

import java.util.LinkedList;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.avgl.dmp.controller.resources.job.test.utils.ComponentsResourceTestUtils;
import de.avgl.dmp.controller.resources.job.test.utils.FiltersResourceTestUtils;
import de.avgl.dmp.controller.resources.job.test.utils.FunctionsResourceTestUtils;
import de.avgl.dmp.controller.resources.job.test.utils.MappingsResourceTestUtils;
import de.avgl.dmp.controller.resources.job.test.utils.TransformationsResourceTestUtils;
import de.avgl.dmp.controller.resources.schema.test.utils.AttributePathsResourceTestUtils;
import de.avgl.dmp.controller.resources.schema.test.utils.AttributesResourceTestUtils;
import de.avgl.dmp.controller.resources.schema.test.utils.MappingAttributePathInstancesResourceTestUtils;
import de.avgl.dmp.controller.resources.test.BasicResourceTest;
import de.avgl.dmp.persistence.model.job.Component;
import de.avgl.dmp.persistence.model.job.Filter;
import de.avgl.dmp.persistence.model.job.Function;
import de.avgl.dmp.persistence.model.job.Mapping;
import de.avgl.dmp.persistence.model.job.Transformation;
import de.avgl.dmp.persistence.model.job.proxy.ProxyMapping;
import de.avgl.dmp.persistence.model.schema.Attribute;
import de.avgl.dmp.persistence.model.schema.AttributePath;
import de.avgl.dmp.persistence.model.schema.MappingAttributePathInstance;
import de.avgl.dmp.persistence.service.job.MappingService;
import de.avgl.dmp.persistence.service.job.test.utils.MappingServiceTestUtils;
import de.avgl.dmp.persistence.util.DMPPersistenceUtil;

public class MappingsResourceTest extends
		BasicResourceTest<MappingsResourceTestUtils, MappingServiceTestUtils, MappingService, ProxyMapping, Mapping, Long> {

	private final FiltersResourceTestUtils filtersResourceTestUtils;

	private final FunctionsResourceTestUtils functionsResourceTestUtils;

	private final TransformationsResourceTestUtils transformationsResourceTestUtils;

	private final ComponentsResourceTestUtils componentsResourceTestUtils;

	private final AttributesResourceTestUtils attributesResourceTestUtils;

	private final AttributePathsResourceTestUtils attributePathsResourceTestUtils;

	private final MappingsResourceTestUtils mappingsResourceTestUtils;

	private final MappingAttributePathInstancesResourceTestUtils mappingAttributePathInstancesResourceTestUtils;

	private Function function;

	private Function updateFunction;

	private Filter updateFilter;

	private Component component;

	private Transformation transformation;

	private Component transformationComponent;

	private Component updateTransformationComponent;

	private AttributePath updateInputAttributePath;

	final Map<Long, Attribute> attributes = Maps.newHashMap();

	final Map<Long, AttributePath> attributePaths = Maps.newLinkedHashMap();

	final Map<Long, MappingAttributePathInstance> mappingAttributePathInstances = Maps.newLinkedHashMap();

	public MappingsResourceTest() {

		super(Mapping.class, MappingService.class, "mappings", "mapping.json", new MappingsResourceTestUtils());

		filtersResourceTestUtils = new FiltersResourceTestUtils();
		functionsResourceTestUtils = new FunctionsResourceTestUtils();
		componentsResourceTestUtils = new ComponentsResourceTestUtils();
		attributesResourceTestUtils = new AttributesResourceTestUtils();
		attributePathsResourceTestUtils = new AttributePathsResourceTestUtils();
		transformationsResourceTestUtils = new TransformationsResourceTestUtils();
		mappingsResourceTestUtils = new MappingsResourceTestUtils();
		mappingAttributePathInstancesResourceTestUtils = new MappingAttributePathInstancesResourceTestUtils();
	}

	@Override
	public void prepare() throws Exception {

		super.prepare();

		createAttribute("attribute1.json");
		createAttribute("attribute6.json");

		final AttributePath inputAttributePath = createAttributePath("attribute_path4.json");
		final AttributePath outputAttributePath = createAttributePath("attribute_path5.json");

		final MappingAttributePathInstance inputMappingAttributePathInstance = createMappingAttributePathInstance(
				"input_mapping_attribute_path_instance.json", inputAttributePath);
		final MappingAttributePathInstance outputMappingAttributePathInstance = createMappingAttributePathInstance(
				"output_mapping_attribute_path_instance.json", outputAttributePath);

		function = functionsResourceTestUtils.createObject("function.json");

		// prepare component json for function manipulation
		String componentJSONString = DMPPersistenceUtil.getResourceAsString("component.json");
		final ObjectNode componentJSON = objectMapper.readValue(componentJSONString, ObjectNode.class);

		final String finalFunctionJSONString = objectMapper.writeValueAsString(function);

		Assert.assertNotNull("the function JSON string shouldn't be null", finalFunctionJSONString);

		final ObjectNode finalFunctionJSON = objectMapper.readValue(finalFunctionJSONString, ObjectNode.class);

		Assert.assertNotNull("the function JSON shouldn't be null", finalFunctionJSON);

		componentJSON.put("function", finalFunctionJSON);

		// re-init expect component
		componentJSONString = objectMapper.writeValueAsString(componentJSON);
		final Component expectedComponent = objectMapper.readValue(componentJSONString, Component.class);

		component = componentsResourceTestUtils.createObject(componentJSONString, expectedComponent);

		// prepare transformation json for component manipulation
		String transformationJSONString = DMPPersistenceUtil.getResourceAsString("transformation.json");
		final ObjectNode transformationJSON = objectMapper.readValue(transformationJSONString, ObjectNode.class);

		final String finalComponentJSONString = objectMapper.writeValueAsString(component);

		Assert.assertNotNull("the component JSON string shouldn't be null", finalComponentJSONString);

		final ObjectNode finalComponentJSON = objectMapper.readValue(finalComponentJSONString, ObjectNode.class);

		Assert.assertNotNull("the component JSON shouldn't be null", finalComponentJSON);

		final ArrayNode componentsJSONArray = objectMapper.createArrayNode();

		componentsJSONArray.add(finalComponentJSON);

		transformationJSON.put("components", componentsJSONArray);

		// re-init expect transformation
		transformationJSONString = objectMapper.writeValueAsString(transformationJSON);
		final Transformation expectedTransformation = objectMapper.readValue(transformationJSONString, Transformation.class);

		transformation = transformationsResourceTestUtils.createObject(transformationJSONString, expectedTransformation);

		// prepare transformation component json for function manipulation
		String transformationComponentJSONString = DMPPersistenceUtil.getResourceAsString("transformation_component.json");
		final ObjectNode transformationComponentJSON = objectMapper.readValue(transformationComponentJSONString, ObjectNode.class);

		final String finalTransformationJSONString = objectMapper.writeValueAsString(transformation);

		Assert.assertNotNull("the transformation JSON string shouldn't be null", finalTransformationJSONString);

		final ObjectNode finalTransformationJSON = objectMapper.readValue(finalTransformationJSONString, ObjectNode.class);

		Assert.assertNotNull("the Transformation JSON shouldn't be null", finalTransformationJSON);

		transformationComponentJSON.put("function", finalTransformationJSON);

		// re-init expect transformation component
		transformationComponentJSONString = objectMapper.writeValueAsString(transformationComponentJSON);
		final Component expectedTransformationComponent = objectMapper.readValue(transformationComponentJSONString, Component.class);

		transformationComponent = componentsResourceTestUtils.createObject(transformationComponentJSONString, expectedTransformationComponent);

		// prepare mapping json for transformation, input attribute paths and output attribute path manipulation
		final ObjectNode objectJSON = objectMapper.readValue(objectJSONString, ObjectNode.class);

		final String finalTransformationComponentJSONString = objectMapper.writeValueAsString(transformationComponent);

		Assert.assertNotNull("the transformation component JSON string shouldn't be null", finalTransformationComponentJSONString);

		final ObjectNode finalTransformationComponentJSON = objectMapper.readValue(finalTransformationComponentJSONString, ObjectNode.class);

		Assert.assertNotNull("the transformation component JSON shouldn't be null", finalTransformationComponentJSON);

		objectJSON.put("transformation", finalTransformationComponentJSON);

		final String finalInputAttributePathJSONString = objectMapper.writeValueAsString(inputMappingAttributePathInstance);

		Assert.assertNotNull("the input attribute path JSON string shouldn't be null", finalInputAttributePathJSONString);

		final ObjectNode finalInputAttributePathJSON = objectMapper.readValue(finalInputAttributePathJSONString, ObjectNode.class);

		Assert.assertNotNull("the input attribute path JSON shouldn't be null", finalInputAttributePathJSON);

		final ArrayNode inputAttributePathsJSON = objectMapper.createArrayNode();

		inputAttributePathsJSON.add(finalInputAttributePathJSON);

		objectJSON.put("input_attribute_paths", inputAttributePathsJSON);

		final String finalOutputAttributePathJSONString = objectMapper.writeValueAsString(outputMappingAttributePathInstance);

		Assert.assertNotNull("the output attribute path JSON string shouldn't be null", finalOutputAttributePathJSONString);

		final ObjectNode finalOutputAttributePathJSON = objectMapper.readValue(finalOutputAttributePathJSONString, ObjectNode.class);

		Assert.assertNotNull("the output attribute path JSON shouldn't be null", finalOutputAttributePathJSON);

		objectJSON.put("output_attribute_path", finalOutputAttributePathJSON);

		// re-init expect object
		objectJSONString = objectMapper.writeValueAsString(objectJSON);
		expectedObject = objectMapper.readValue(objectJSONString, pojoClass);

		System.out.println("mapping json = '" + objectJSONString + "'");
	}

	@Override
	public void testPUTObject() throws Exception {

		super.testPUTObject();

		for (final MappingAttributePathInstance mappingAttributePathInstance : mappingAttributePathInstances.values()) {

			mappingAttributePathInstancesResourceTestUtils.deleteObject(mappingAttributePathInstance);
		}

		filtersResourceTestUtils.deleteObject(updateFilter);
		componentsResourceTestUtils.deleteObject(transformationComponent);
		componentsResourceTestUtils.deleteObject(updateTransformationComponent);

		if (!function.equals(updateFunction)) {

			functionsResourceTestUtils.deleteObject(updateFunction);
		}

		attributePathsResourceTestUtils.deleteObject(updateInputAttributePath);
	}

	@After
	public void tearDown2() throws Exception {

		for (final MappingAttributePathInstance mappingAttributePathInstance : mappingAttributePathInstances.values()) {

			mappingAttributePathInstancesResourceTestUtils.deleteObject(mappingAttributePathInstance);
		}

		for (final AttributePath attributePath : attributePaths.values()) {

			attributePathsResourceTestUtils.deleteObject(attributePath);
		}

		for (final Attribute attribute : attributes.values()) {

			attributesResourceTestUtils.deleteObject(attribute);
		}

		transformationsResourceTestUtils.deleteObject(transformation);

		functionsResourceTestUtils.deleteObject(function);
	}

	@Override
	protected Mapping updateObject(final Mapping persistedMapping) throws Exception {

		// update name
		persistedMapping.setName(persistedMapping.getName() + " update");

		// update filter
		updateFilter = filtersResourceTestUtils.createObject("filter2.json");

		// update component
		updateTransformationComponent = componentsResourceTestUtils.createObject("component.json");

		persistedMapping.setTransformation(updateTransformationComponent);

		updateFunction = updateTransformationComponent.getFunction();

		// update input attribute paths
		updateInputAttributePath = createAttributePath("attribute_path6.json");

		for (final Attribute attribute : updateInputAttributePath.getAttributes()) {

			attributes.put(attribute.getId(), attribute);
		}

		persistedMapping.getInputAttributePaths().iterator().next().setAttributePath(updateInputAttributePath);
		persistedMapping.getInputAttributePaths().iterator().next().setFilter(updateFilter);

		final String updateMappingJSONString = objectMapper.writeValueAsString(persistedMapping);
		expectedObject = objectMapper.readValue(updateMappingJSONString, pojoClass);

		final Mapping updateMapping = mappingsResourceTestUtils.updateObject(updateMappingJSONString, expectedObject);

		Assert.assertNotNull("the mapping JSON string shouldn't be null", updateMapping);
		Assert.assertEquals("mapping name shoud be equal", expectedObject.getName(), updateMapping.getName());
		Assert.assertEquals(updateMapping.getInputAttributePaths().iterator().next().getAttributePath(), updateInputAttributePath);
		Assert.assertEquals(updateMapping.getInputAttributePaths().iterator().next().getFilter(), updateFilter);

		return updateMapping;
	}

	private Attribute createAttribute(final String attributeJSONFileName) throws Exception {

		final Attribute actualAttribute = attributesResourceTestUtils.createObject(attributeJSONFileName);

		attributes.put(actualAttribute.getId(), actualAttribute);

		return actualAttribute;
	}

	private AttributePath createAttributePath(final String attributePathJSONFileName) throws Exception {

		String attributePathJSONString = DMPPersistenceUtil.getResourceAsString(attributePathJSONFileName);
		final AttributePath attributePath = objectMapper.readValue(attributePathJSONString, AttributePath.class);

		final LinkedList<Attribute> attributes = attributePath.getAttributePath();
		final LinkedList<Attribute> newAttributes = Lists.newLinkedList();

		for (final Attribute attribute : attributes) {

			for (final Attribute newAttribute : this.attributes.values()) {

				if (attribute.getUri().equals(newAttribute.getUri())) {

					newAttributes.add(newAttribute);

					break;
				}
			}
		}

		if (attributes.size() > 0 && attributes.size() != newAttributes.size()) {

			newAttributes.addAll(attributes);
		}

		attributePath.setAttributePath(newAttributes);

		attributePathJSONString = objectMapper.writeValueAsString(attributePath);
		final AttributePath expectedAttributePath = objectMapper.readValue(attributePathJSONString, AttributePath.class);
		final AttributePath actualAttributePath = attributePathsResourceTestUtils.createObject(attributePathJSONString, expectedAttributePath);

		attributePaths.put(actualAttributePath.getId(), actualAttributePath);

		return actualAttributePath;
	}

	private MappingAttributePathInstance createMappingAttributePathInstance(final String mappingAttributePathInstanceFileName,
			final AttributePath attributePath) throws Exception {

		String mappingAttributePathInstanceJSONString = DMPPersistenceUtil.getResourceAsString(mappingAttributePathInstanceFileName);
		final MappingAttributePathInstance mappingAttributePathInstanceFromJSON = objectMapper.readValue(mappingAttributePathInstanceJSONString,
				MappingAttributePathInstance.class);

		mappingAttributePathInstanceFromJSON.setAttributePath(attributePath);

		mappingAttributePathInstanceJSONString = objectMapper.writeValueAsString(mappingAttributePathInstanceFromJSON);
		final MappingAttributePathInstance expectedMappingAttributePathInstance = objectMapper.readValue(mappingAttributePathInstanceJSONString,
				MappingAttributePathInstance.class);
		final MappingAttributePathInstance actualMappingAttributePathInstance = mappingAttributePathInstancesResourceTestUtils.createObject(
				mappingAttributePathInstanceJSONString, expectedMappingAttributePathInstance);

		mappingAttributePathInstances.put(actualMappingAttributePathInstance.getId(), actualMappingAttributePathInstance);

		return actualMappingAttributePathInstance;
	}
}
