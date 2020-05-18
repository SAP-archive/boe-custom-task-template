package com.sap.afw.task.webi;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sap.afw.helper.RESTRequest;

public class WebIReports  {
	
	private RESTRequest rest;
	
    public WebIReports(RESTRequest rest) {
		this.rest = rest;
	}

    public void requestDetails(int docId) throws Exception {
        JSONObject document = rest.sendRequestJSON(rest.getWEBI_RWS() + "/documents/" + docId, "GET", null);
    }

	public String updateReportElement(int docId, int reportId, int reportElementId) throws Exception {
        JSONObject document = rest.sendRequestJSON(rest.getWEBI_RWS() + "/documents/" + docId + "/reports/" + reportId + "/elements/" + reportElementId, "PUT", "{\"element\": {\"@type\": \"VTable\"}}");
        return document.toString();
	}

    public Map<String, Integer> requestReportsList(int docId) throws Exception {
        Map<String, Integer> entries = new HashMap<String, Integer>();
        JSONObject document = rest.sendRequestJSON(rest.getWEBI_RWS() + "/documents/" + docId + "/reports", "GET", null);
        if (document == null) return entries;
        
		document = document.getJSONObject("reports");
		JSONArray elements = JSONHelper.getJSONArrayAlways(document, "report");
        String dpIDs = "";

		for (int i=0; i<elements.length(); i++) {
			JSONObject element = elements.getJSONObject(i);
			Integer id = element.getInt("id");
			String name = element.getString("name");

		}

        return entries;
    }

    public Map<String, Integer> requestReportList(int docId) throws Exception {
    	Map<String, Integer> resultSet = new HashMap<String, Integer>();

        JSONObject document = rest.sendRequestJSON(rest.getWEBI_RWS() +  "/documents/" + docId + "/reports", "GET", null);
        if (document == null) return null;
		document = document.getJSONObject("reports");
		JSONArray elements = JSONHelper.getJSONArrayAlways(document, "report");
		for (int i=0; i<elements.length(); i++) {
			JSONObject element = elements.getJSONObject(i);
			resultSet.put(element.getString("name"), element.getInt("id"));
		}
        return resultSet;
    }

    
    public Map<String, Integer> requestReportElementsList(int docId, int reportId, String searchType) throws Exception {
    	Map<String, Integer> resultSet = new HashMap<String, Integer>();
        JSONObject document = rest.sendRequestJSON(rest.getWEBI_RWS() + "/documents/" + docId + "/reports/" + reportId + "/elements?", "GET", null);
        if (document == null) return null;
		document = document.getJSONObject("elements");
		JSONArray elements = JSONHelper.getJSONArrayAlways(document, "element");
		for (int i=0; i<elements.length(); i++) {
			JSONObject element = elements.getJSONObject(i);
			if (searchType.contentEquals(element.getString("@type"))) {
				resultSet.put(element.getString("name"), element.getInt("id"));
			}
		}
        return resultSet;
    }
    
    public JSONObject requestReportElementDataset(int docId, int reportId, int elementId) throws Exception {
        JSONObject document = rest.sendRequestJSON(rest.getWEBI_RWS() + "/documents/" + docId + "/reports/" + reportId + "/elements/" + elementId + "/dataset?", "GET", null);
        if (document == null) return null;
		return document.getJSONObject("dataset");
    }

    public Map<Integer, JSONObject> getDatasetMetadata(JSONObject dataset) throws Exception {
    	Map<Integer, JSONObject> resultSet = new HashMap<Integer, JSONObject>();
		JSONArray values = dataset.getJSONObject("metadata").getJSONArray("value");
		for (int i=0; i<values.length(); i++) {
			JSONObject element = values.getJSONObject(i);
			resultSet.put(i, element);
		}
        return resultSet;
    }
    
    public Map<Integer, JSONObject> getDatasetRows(JSONObject dataset) throws Exception {
    	Map<Integer, JSONObject> resultSet = new HashMap<Integer, JSONObject>();
		JSONArray elements = dataset.getJSONArray("row");
		for (int i=0; i<elements.length(); i++) {
			JSONObject element = elements.getJSONObject(i);
			resultSet.put(i, element);
			JSONArray values = JSONHelper.getJSONArrayAlways(element, "value");
			/*
			for (int j=0; j<values.length(); j++) {
				if (values.get(j) instanceof Integer) {
					LOG.debug(values.getInt(j) + ",");
				} else {
					LOG.debug("\"" + values.getString(j) + "\",");
				}
			}
			*/
		}
        return resultSet;
    }


}
