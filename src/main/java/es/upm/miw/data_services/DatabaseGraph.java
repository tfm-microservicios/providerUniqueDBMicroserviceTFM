package es.upm.miw.data_services;

import java.util.ArrayList;
import java.util.List;

import es.upm.miw.documents.Provider;

public class DatabaseGraph {

	private List<Provider> providerList;

	public DatabaseGraph() {
		this.providerList = new ArrayList<>();
	}

	public List<Provider> getProviderList() {
		return providerList;
	}

	public void setProviderList(List<Provider> providerList) {
		this.providerList = providerList;
	}
}
