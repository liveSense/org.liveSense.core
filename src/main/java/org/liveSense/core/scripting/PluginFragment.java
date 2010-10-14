package org.liveSense.core.scripting;

public class PluginFragment implements Comparable<PluginFragment> {
	boolean enabled;
	int		order;
	String  pluginName;
	
	public PluginFragment(String pluginName, int order, boolean enabled) {
		this.enabled = enabled;
		this.order = order;
		this.pluginName = pluginName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int compareTo(PluginFragment o) {
		return this.order - o.order;
	}

	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	
}
