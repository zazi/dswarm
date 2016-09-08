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
package org.dswarm.controller.resources.job.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dswarm.persistence.model.resource.utils.ConfigurationStatics;

public class TasksResourceTestDD1387 extends TasksResourceTestDD538 {

	private static final Logger LOG = LoggerFactory.getLogger(TasksResourceTestDD1387.class);

	public TasksResourceTestDD1387() {

		super("dd-1387/task.json",
				"dd-1387/input.xml",
				"Publisher",
				ConfigurationStatics.XML_STORAGE_TYPE,
				"dd-1387/result.xml",
				"01",
				true);
	}
}
