/**
 * Copyright (C) 2013, 2014 SLUB Dresden & Avantgarde Labs GmbH (<code@dswarm.org>)
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
package org.dswarm.persistence.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * An abstract POJO class for entities with a name and where the identifier should be generated by the database.
 *
 * @author tgaengler
 */
@XmlRootElement
@MappedSuperclass
@Cacheable(false)
public abstract class BasicDMPJPAObject extends DMPObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the entity.
	 */
	@Column(name = "NAME")
	private String name;

	public BasicDMPJPAObject(final String uuidArg) {
		super(uuidArg);
	}

	protected BasicDMPJPAObject() {

	}

	/**
	 * Gets the name of the entity.
	 *
	 * @return the name of the entity
	 */
	public String getName() {

		return name;
	}

	/**
	 * Sets the name of the entity.
	 *
	 * @param name the name of the entity
	 */
	public void setName(final String name) {

		this.name = name;
	}

	@Override
	public boolean completeEquals(final Object obj) {

		return BasicDMPJPAObject.class.isInstance(obj) && super.completeEquals(obj) && Objects.equal(((BasicDMPJPAObject) obj).getName(), getName());
	}
}
