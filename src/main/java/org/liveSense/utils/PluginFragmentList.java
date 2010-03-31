package org.liveSense.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class PluginFragmentList extends ArrayList<PluginFragment> { //TreeSet<PluginFragment> {
	
	HashMap<String, PluginFragment> hash;
	String fragmentName;
	
	
	public PluginFragmentList(String fragmentName) {
		super();
		hash = new HashMap<String, PluginFragment>();
		this.fragmentName = fragmentName;
	}
	
	@Override
	public void add(int index, PluginFragment e) {
		hash.put(e.getPluginName(), e);
		super.add(index, e);
	}
	
	@Override
	public boolean add(PluginFragment e) {
		hash.put(e.getPluginName(), e);
		return super.add(e);
	}
	
	public PluginFragment get(String pluginName) {
		return hash.get(pluginName);
	}
	
	public int getOrder(String pluginName) {
		PluginFragment pf = get(pluginName);
		if (pf == null) return -1;
		return pf.getOrder();
	}
	
	public void sortByFragmentOrder() {
		Collections.sort(this);
	}

	public int getNextOrder() {
		return this.size();
	}

}
