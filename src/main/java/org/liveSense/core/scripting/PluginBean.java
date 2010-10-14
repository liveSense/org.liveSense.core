package org.liveSense.core.scripting;

import java.util.ArrayList;
import java.util.HashMap;

public class PluginBean {
	
	HashMap<String, Boolean> installedPlugins = new HashMap<String, Boolean>();
	String selectedPluginName;
	String selectedFragmentName;
	
	HashMap<String, PluginFragmentList> pluginFragments = new HashMap<String, PluginFragmentList>();
	
	public void setInstalledPlugin(String name) {
		installedPlugins.put(name, true);
		setSelectedPluginName(name);
	}
	
	// Return all of the installed plugins
	public HashMap<String, Boolean> getInstalledPlugins() {
		return this.installedPlugins;
	}
	
	public void setRegisterFragment(String fragmentName) {
		// If the fragments are not registered, we register it
		PluginFragmentList pfl = pluginFragments.get(fragmentName);
		if (pfl == null) {
			pfl = new PluginFragmentList(fragmentName);
		}
		pfl.add(new PluginFragment(selectedPluginName, pfl.getNextOrder(), true));
		setSelectedFragmentName(fragmentName);
	}
	
	
	// Sets the plugins order (if any name is not presents, the plugin comes disabled)
	public void setFragmentsOrder(String pluginNames) {
		// First we disable all of the plugins in the fragment
		PluginFragmentList pfl = pluginFragments.get(getSelectedFragmentName());
		if (pfl == null) {
			return;
		}
		for (int i=0; i<pfl.size(); i++) {
			pfl.get(i).setEnabled(false);
		}
		
		String[] plgNames = pluginNames.split(",");
		for (int i=0; i<plgNames.length; i++) {
			PluginFragment fragment = pfl.get(plgNames[i]);
			if (fragment != null) {
				fragment.setEnabled(true);
				fragment.setOrder(i);
			}
		}
	}
	
	public ArrayList<String> getAllFragments() {
		ArrayList<String> ret = new ArrayList<String>();
		PluginFragmentList pfl = pluginFragments.get(getSelectedFragmentName());
		if (pfl == null) {
			return ret;
		}		
		for (PluginFragment fragment : pfl) {
			ret.add(fragment.getPluginName());
		}
		return ret;
	}
	

	public String getSelectedPluginName() {
		return selectedPluginName;
	}

	public void setSelectedPluginName(String selectedPluginName) {
		this.selectedPluginName = selectedPluginName;
	}

	public String getSelectedFragmentName() {
		return selectedFragmentName;
	}

	public void setSelectedFragmentName(String selectedFragmentName) {
		this.selectedFragmentName = selectedFragmentName;
	}
	
	
}
