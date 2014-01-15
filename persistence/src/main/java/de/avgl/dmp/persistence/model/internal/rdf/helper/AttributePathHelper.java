package de.avgl.dmp.persistence.model.internal.rdf.helper;

import java.util.LinkedList;

import com.google.common.collect.Lists;

import de.avgl.dmp.init.util.DMPStatics;


public class AttributePathHelper {

	private LinkedList<String> attributePath = Lists.newLinkedList();
	
	public void addAttribute(final String attribute) {
		
		attributePath.add(attribute);
	}
	
	public void setAttributePath(final LinkedList<String> attributePathArg) {
		
		attributePath.clear();
		attributePath.addAll(attributePathArg);
	}
	
	public LinkedList<String> getAttributePath() {
		
		return attributePath;
	}
	
	public int length() {
		
		return attributePath.size();
	}

	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
		
		for( int i = 0; i < attributePath.size(); i++) {
			
			final String attribute = attributePath.get(i);
			
			sb.append(attribute);
			
			if(i < (attributePath.size() - 1)) {
				
				sb.append(DMPStatics.ATTRIBUTE_DELIMITER);
			}
		}
		
		return sb.toString();
	}

	@Override
	public int hashCode() {
		
		return this.toString().hashCode();
	}

	@Override
	public boolean equals(java.lang.Object obj) {
		
		if(obj == null) {
			
			return false;
		}
		
		if(this.toString().equals(obj.toString())) {
			
			return true;
		}
		
		return false;
	}
}
