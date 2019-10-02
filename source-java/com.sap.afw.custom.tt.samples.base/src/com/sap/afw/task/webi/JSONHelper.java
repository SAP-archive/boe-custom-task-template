package com.sap.afw.task.webi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONHelper {

    /**
     * Get the JSONArray value associated with a key or 
     *     new JSONArray from JSONObject associated with a key.
     *
     * @param key   A key string.
     * @return      A JSONArray which is the value.
     * @throws      JSONException if the key is not found or
     *  if the value is not a JSONArray.
     */
    public static JSONArray getJSONArrayAlways(JSONObject jsonObject, String key) throws JSONException {
        Object object = jsonObject.get(key);
        if (object instanceof JSONArray) {
			return (JSONArray)object;
		} else if (object instanceof JSONObject) {
			return new JSONArray().put(object);
		}
	    throw new JSONException("JSONObject[" + key +
                "] is not a JSONArray.");
    }

}
