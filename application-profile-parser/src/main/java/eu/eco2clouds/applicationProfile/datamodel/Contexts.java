package eu.eco2clouds.applicationProfile.datamodel;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName(value = "contexts")
public class Contexts {
	private Map<String,Object> contextThings = new HashMap<String,Object>();
	
	@JsonAnySetter
	public void setContextThings(String key, Object value) {
		contextThings.put(key, value);
	}
	
	@JsonAnyGetter
	public Map<String,Object> getContextThings() {
		return contextThings;
	}
}
