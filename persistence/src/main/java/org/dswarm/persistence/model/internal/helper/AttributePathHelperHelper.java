package org.dswarm.persistence.model.internal.helper;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.dswarm.init.util.DMPStatics;
import org.hamcrest.Matchers;

import ch.lambdaj.Lambda;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

public class AttributePathHelperHelper {

	public static AttributePathHelper addAttributePath(final JsonNode unnormalizedSchema, final Set<AttributePathHelper> attributePaths,
			final AttributePathHelper attributePath) {

		final String attribute = unnormalizedSchema.asText();

		return AttributePathHelperHelper.addAttributePath(attribute, attributePaths, attributePath);
	}

	public static AttributePathHelper addAttributePath(final String attribute, final Set<AttributePathHelper> attributePaths,
			final AttributePathHelper attributePath) {

		final LinkedList<String> currentAttributePath = Lists.newLinkedList(attributePath.getAttributePath());
		currentAttributePath.add(attribute);
		final AttributePathHelper schemaNormalizerHelper = new AttributePathHelper();
		schemaNormalizerHelper.setAttributePath(currentAttributePath);
		attributePaths.add(schemaNormalizerHelper);

		return schemaNormalizerHelper;
	}

	public static boolean levelAsArray(final List<AttributePathHelper> attributePaths, final String levelCurrentRootAttributePath) {

		boolean levelAsArray = false;

		for (final AttributePathHelper attributePathHelper : attributePaths) {

			if (!attributePathHelper.toString().startsWith(levelCurrentRootAttributePath)) {

				levelAsArray = true;

				break;
			}
		}

		return levelAsArray;
	}

	public static String determineLevelRootAttributePath(final AttributePathHelper attributePathHelper, final int level) {

		String levelCurrentRootAttributePath = "";

		int currentLevel = 0;

		final Iterator<String> iter = attributePathHelper.getAttributePath().iterator();

		while ((level > currentLevel) && iter.hasNext()) {

			if (currentLevel > 0) {

				levelCurrentRootAttributePath += DMPStatics.ATTRIBUTE_DELIMITER;
			}

			levelCurrentRootAttributePath += iter.next();

			currentLevel++;
		}

		if (level == 1) {

			if (levelCurrentRootAttributePath.contains(DMPStatics.ATTRIBUTE_DELIMITER.toString())) {

				levelCurrentRootAttributePath = levelCurrentRootAttributePath.substring(0,
						levelCurrentRootAttributePath.indexOf(DMPStatics.ATTRIBUTE_DELIMITER.toString()));
			}
		}

		return levelCurrentRootAttributePath;
	}

	public static List<AttributePathHelper> prepareAttributePathHelpers(final List<AttributePathHelper> attributePaths, final int level) {

		// only relevant attribute paths
		final List<AttributePathHelper> filteredAttributePaths = Lambda.filter(
				Lambda.having(Lambda.on(AttributePathHelper.class).length(), Matchers.greaterThanOrEqualTo(level)), attributePaths);

		if (filteredAttributePaths == null || filteredAttributePaths.isEmpty()) {

			return null;
		}

		// sort
		return Lambda.sort(filteredAttributePaths, Lambda.on(AttributePathHelper.class).length());
	}

	public static List<AttributePathHelper> getNextAttributePathHelpersForLevelRootAttributePath(final List<AttributePathHelper> attributePaths,
			final String levelRootAttributePath, final int level) {

		// only attribute paths for level root attribute path
		final List<AttributePathHelper> levelRootAttributePaths = Lambda.filter(
				Lambda.having(Lambda.on(AttributePathHelper.class).toString(), Matchers.startsWith(levelRootAttributePath)), attributePaths);

		if (levelRootAttributePaths == null || levelRootAttributePaths.isEmpty()) {

			return null;
		}

		// only level attribute paths for next level
		return AttributePathHelperHelper.prepareAttributePathHelpers(levelRootAttributePaths, level + 1);
	}

	public static boolean hasNextLevel(final List<AttributePathHelper> attributePaths, final int level) {

		final List<AttributePathHelper> nextLevelAttributePaths = AttributePathHelperHelper.prepareAttributePathHelpers(attributePaths, level + 1);

		return nextLevelAttributePaths != null && !nextLevelAttributePaths.isEmpty();
	}
}
