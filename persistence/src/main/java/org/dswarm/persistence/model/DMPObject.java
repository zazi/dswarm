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

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.config.CacheIsolationType;

/**
 * The most abstract POJO class, i.e., this class is intended for inheritance. It only provides a getter for the identifier and
 * basic #hashCode and #equals implementations (by identifier). The identifier (ID) is generated by the utilised database.
 *
 * @author tgaengler
 */
@XmlRootElement
@MappedSuperclass
@Cacheable(false)
@Cache(isolation= CacheIsolationType.ISOLATED)
public abstract class DMPObject implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

//	/**
//	 * The db-generated identifier of the entity.
//	 * TODO: make (generated) id private
//	 */
//	@Id
//	@UniqueConstraint()
//	@Access(AccessType.FIELD)
//	@GeneratedValue(strategy = GenerationType.AUTO)
//	@Column(name = "ID")
//	protected Long id;

	/**
	 * can we make this final as well? how deals JPA with this?
	 */
	@Id
	@XmlID
	@Access(AccessType.FIELD)
	@Column(name = "UUID", columnDefinition = "VARCHAR(160)", length = 160, unique = true)
	private String uuid;

	public DMPObject(final String uuidArg) {

		uuid = uuidArg;
	}

	protected DMPObject() {

	}

	//	/**
//	 * Gets the db-generated identifier of this object.
//	 *
//	 * @return the db-generated identifier of this object
//	 */
//	public Long getId() {
//
//		return id;
//	}

	public String getUuid() {

		if(uuid == null) {

			// TODO: throw exception???
		}

		return uuid;
	}

	@Override
	public final int hashCode() {

		return Objects.hashCode(getUuid());
	}

	/**
	 * TODO: revise this according to http://www.artima.com/lejava/articles/equality.html (and probably http://stackoverflow.com/questions/27581/what-issues-should-be-considered-when-overriding-equals-and-hashcode-in-java/256447#256447)
	 *
	 * @param obj
	 * @return
	 */
	@Override
	public final boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}
		if (obj == null || !(obj instanceof DMPObject)) {

			return false;
		}

		final DMPObject other = (DMPObject) obj;

		// The ID is `null` if a DMPObject is not initialized/persisted in the DB.
		// If this is the case for both objects, treat them as non equal, since they might be
		// one of many recently `new`-ed objects and their equality cannot be proven at this point.
		// If we introduce UUIDs for every object, this can change so that `null` IDs are treated
		// as equal.
		//
		// TODO: revise this or remove it
		// note: a uuid should never be null (or?)
		if (this.getUuid() == null && other.getUuid() == null) {

			return false;
		}

		return Objects.equal(other.getUuid(), getUuid());
	}

	public boolean completeEquals(final Object obj) {

		return DMPObject.class.isInstance(obj) && Objects.equal(((DMPObject) obj).getUuid(), getUuid());
	}

	@Override
	public String toString() {

		return ToStringBuilder.reflectionToString(this);
	}
}
