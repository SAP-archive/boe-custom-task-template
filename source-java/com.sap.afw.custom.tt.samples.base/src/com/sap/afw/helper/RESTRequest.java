package com.sap.afw.helper;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONObject;

import com.businessobjects.foundation.logging.ILogger;
import com.businessobjects.foundation.logging.LoggerManager;
import com.crystaldecisions.sdk.occa.infostore.IInfoObject;
import com.crystaldecisions.sdk.occa.infostore.IInfoObjects;

public class RESTRequest {

	private static final ILogger LOG = LoggerManager.getLogger(RESTRequest.class);

	private static enum TOKEN_TYPE {
		token, serializedSession
	}

    private String logonToken;
    private String baseUrl;
    private String responseContent;
    private int responseCode;
    private Map<String, List<String>> responseHeaders;
	private String errorCode;
	private String errorMessage = null;

    public RESTRequest() {
    	
    }
    
    public String getWEBI_RWS() {
    	return "/raylight/v1";
    }


    /**
     * 
     * @param session
     * @throws Exception
     */
    public void connect(BIPJavaSDK session) throws Exception {
    	// query the url from the CMS
    	IInfoObjects infoObjects = session.getInfoStore().query("SELECT top 1 si_access_url FROM  CI_APPOBJECTS WHERE si_kind='RestWebService'");
    	IInfoObject infoObject = (IInfoObject)infoObjects.get(0);
    	baseUrl = infoObject.properties().getString("SI_ACCESS_URL");
    	logon(session.getEnterpriseSession().getSerializedSession());
    }

    /**
     * Send request to logon with session token
     * @param sessionToken
     * @throws Exception
     */
    private void logon(String sessionToken) throws Exception {
    	send(baseUrl + "/logon/token", "GET", null, false);
    	TOKEN_TYPE tokenType = TOKEN_TYPE.serializedSession;
       	sessionToken = StringEscapeUtils.escapeXml(sessionToken);
        // Sets logon information
        Map<String, String> map = new HashMap<String, String>();
        map.put("//attr[@name='tokenType']", tokenType.toString());
        map.put("//attr[@name='logonToken']", sessionToken);
		String filledLogonResponse = responseContent.replaceFirst("<attr name=\"logonToken\" type=\"string\"></attr>", 
				                                                  "<attr name=\"logonToken\" type=\"string\">" + sessionToken + "</attr>");
		// must specify the correct token type
		filledLogonResponse = filledLogonResponse.replaceFirst(">token</attr>", ">" + tokenType.toString() + "</attr>");
        send(baseUrl + "/logon/token", "POST", filledLogonResponse, false);
        
        logonToken = responseHeaders.get("X-SAP-LogonToken").get(0);
    }
    
    /**
     * send request using JSON
     * @param requestUrl
     * @param method
     * @param jsonContent
     * @return
     * @throws Exception
     */
	public JSONObject sendRequestJSON(String requestUrl, String method, String jsonContent) throws Exception {
		if (LOG.isDebugEnabled()) {
        	LOG.debug("sendRequest: method=" + method + " url=" + requestUrl);
		}
       	send(baseUrl + requestUrl, method, jsonContent, true);
        JSONObject document = new JSONObject(responseContent);
        if (responseCode == HTTP_OK) return document;
        if (responseCode == HTTP_NOT_FOUND) {
            // If the request returns the response error code "400"
            errorCode = document.getString("error_code");
            errorMessage = document.optString("message");
        } else {
            throw new IllegalArgumentException(document.toString());
        }
        return null;
	}

    /**
     * 
     * @param url
     * @param method
     * @param content
     * @param useJSON
     * @throws Exception
     */
    public void send(String url, String method, String content, boolean useJSON) throws Exception {
    	String accept = (useJSON ? "application/json" : "application/xml");
        responseContent = null;
        responseHeaders = null;
        responseCode = 0;
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method);

        if (accept != null) {
            connection.setRequestProperty("Accept", accept);
        }

        if (this.logonToken != null) {
            connection.setRequestProperty("X-SAP-LogonToken", this.logonToken);
            // connection.setRequestProperty("X-SAP-PVL", "de-DE");            
        }

        InputStream in = null;
        DataOutputStream out = null;
        try {
            // reads response
        	if (content == null) {
        		in = connection.getInputStream();
            // sets content and reads response
        	} else {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", accept);   // "application/xml");
                connection.setRequestProperty("Content-Length", String.valueOf(content.getBytes("UTF-8").length));
                connection.setRequestProperty("X-SAP-PVL", "en-US");            
                out = new DataOutputStream(connection.getOutputStream());
                out.write(content.getBytes("UTF-8"));
                out.flush();
                in = (InputStream) connection.getContent();
        	}
        } catch (IOException e) {
            in = connection.getErrorStream();
        } finally {
        	if(out != null) out.close();
        }
        
        try{
        	if (in == null)
        		throw new Exception("Connection to " + url + " failed");

        	Scanner scanner = new Scanner(in, "UTF-8").useDelimiter("\\A");
        	responseContent = scanner.hasNext() ? scanner.next() : "";
        	responseHeaders = connection.getHeaderFields();
            responseCode = connection.getResponseCode();
        } finally {
        	if(in != null) in.close();
        	if(connection != null) connection.disconnect();
        }
    }

}
