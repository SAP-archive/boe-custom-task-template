package com.sap.afw.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sap.bong.common.coretask.service.TaskConstant;
import com.sap.bong.task.custom.sdk.CustomTaskInputValue;

public class Framework {

	public static final String CUID = "cuid";
	public static final String ID = "id";

	public static final String COL_DELIMITER = ",";
	public static final String ROW_DELIMITER_CHAR = ";";
	public static final String ROW_DELIMITER_NEW_LINE = "\r\n";

	
	private Set<String> cuidSet;
	private Set<Integer> idSet;
	
	/**
	 * Work on the entries of a list and cache the JSON in a set of CUIDs and a set of IDs
	 * @param list
	 * @return
	 * @throws JSONException
	 */
	public void buildSets(JSONArray list) throws JSONException {
		cuidSet = new HashSet<String>();
		idSet = new HashSet<Integer>();
		// all entries of the list
		for (int i=0; i<list.length(); i++) {
			JSONObject entry = list.getJSONObject(i);
			// if entry has CUID, id will be ignored
			if (entry.has(CUID)) {
				cuidSet.add(entry.getString(CUID));
			} else if (entry.has(ID)) {
				if (entry.get(ID) instanceof Integer) {
					idSet.add(entry.getInt(ID));
				} else {
					idSet.add(Integer.valueOf(entry.getString(ID)));
				}
			}
		}
	}
	
	/**
	 * build a cms query condition from the set values
	 * @return
	 */
	public String getCondition(String key) {
		String condition = "";
		String separator = "";
		if (key.contentEquals(CUID)) {
			if (cuidSet.size() == 0) return "";
			for (String value : cuidSet) {
				condition += separator + "'" + value + "'";
				separator = ",";
			}
			return "(SI_CUID in (" + condition + "))";
		}
		if (key.contentEquals(ID)) {
			if (idSet.size() == 0) return "";
			for (Integer value : idSet) {
				condition += separator + value;
				separator = ",";
			}
			return "(SI_ID in (" + condition + "))";
		}
		return "";
	}
	
	/**
	 * build a cms query condition from String set values
	 * @param valueSet
	 * @return String with value query format: in ("v1","v2")
	 */
	public static String getInValues(Set<String> valueSet) {
		String condition = "";
		String separator = "";
		if (valueSet.size() == 0) return "";
		for (String value : valueSet) {
			condition += separator + "'" + value + "'";
			separator = ",";
		}
		return " (" + condition + ")";
	}

	/**
	 * 
	 * @param original
	 * @return
	 */
	public static String replaceDelimiterCharacter(String original){
		return original.replace(ROW_DELIMITER_CHAR, ":").replace(COL_DELIMITER, "-");
	}

	/**
	 * Header contains the index for each key
	 * @param inputValue
	 * @param parameter
	 * @return
	 * @throws JSONException
	 */
	public static String toCSVwithMetadata(CustomTaskInputValue inputValue, String parameter) throws JSONException {
		JSONObject header = inputValue.getJSONObject(parameter + TaskConstant.PARAM_NAME_POSTFIX_METADATA);
		JSONArray csvJson = inputValue.optJSONArray(parameter);
		StringBuilder csv = new StringBuilder();
		Map<Integer, String> indexMap = new HashMap<Integer, String>();
		String delimiter = "";
		for (int i=0; i<header.length(); i++) {
			indexMap.put(i, header.getString("" + i));
			csv.append(delimiter + header.getString("" + i));
			delimiter = COL_DELIMITER;
		}
		for(int i=0; i<csvJson.length(); i++){
			csv.append(ROW_DELIMITER_NEW_LINE);
			JSONObject jsonRow = csvJson.getJSONObject(i);
			delimiter = "";
			for (Integer key : indexMap.keySet()) {
				csv.append(delimiter + jsonRow.get(indexMap.get(key)));
				delimiter = COL_DELIMITER;
			}
		}
		return csv.toString();
	}


}
