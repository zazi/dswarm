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
package org.dswarm.persistence.model.job.utils;

import org.dswarm.persistence.model.job.Function;
import org.dswarm.persistence.model.job.Transformation;
import org.dswarm.persistence.util.DMPPersistenceUtil;

/**
 * @author tgaengler
 */
public final class FunctionUtils extends BasicFunctionUtils<Function> {

	@Override
	public boolean completeEquals(final Function existingObject, final Function newObject) {

		if (Transformation.class.isInstance(existingObject) && Transformation.class.isInstance(newObject)) {

			return DMPPersistenceUtil.getTransformationUtils().completeEquals((Transformation) existingObject, (Transformation) newObject);
		}

		return super.completeEquals(existingObject, newObject);
	}
}
