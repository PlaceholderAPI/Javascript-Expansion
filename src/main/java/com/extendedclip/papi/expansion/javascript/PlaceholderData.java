package com.extendedclip.papi.expansion.javascript;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderData {
	
	private Map<String, Object> map = new HashMap<>();
	
	public PlaceholderData(Map<String, Object> data) {
		this.map = data;
	}
	
	public PlaceholderData() {
	}
	
	public Map<String, Object> getData() {
		return map;
	}
	
	public void clear() {
		map.clear();
	}
	
	public boolean exists(String key) {
		return map.containsKey(key);
	}
	
	public Object get(String key) {
		return map.get(key);
	}
	
	public boolean remove(String key) {
		return map.remove(key) != null;
	}
	
	public void set(String key, Object value) {
		map.put(key, value);
	}
	
	public boolean isEmpty() {
		return map.isEmpty();
	}
}
