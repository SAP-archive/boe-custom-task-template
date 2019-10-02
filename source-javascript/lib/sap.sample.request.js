/* get RESTFul url from CMS */
var getBaseUrl = function() {
	query = "SELECT top 1 si_access_url FROM  CI_APPOBJECTS WHERE si_kind='RestWebService'";
	infoObjects = oScriptMgr.getInfoStore().query(query);
   	return infoObjects.get(0).properties().getString("SI_ACCESS_URL");
}

/* 
*/
var sendRequest = function(request, method, content) {
	connection = new URL(baseUrl + "/" + request).openConnection();
	addResultDetails("request." + method, request);
	connection.setRequestMethod(method);
	connection.setRequestProperty("Accept", "application/json");
	if (logonToken !== null) {
		connection.setRequestProperty("X-SAP-LogonToken", logonToken);
        //connection.setRequestProperty("X-SAP-PVL", "de-DE");            
    }
	if (content === undefined) {
		content = null;
	}
	inStream = null;
	out = null;
	try {
		if (content === null) {
       		inStream = connection.getInputStream();
		} else {
			addResultDetails("content", content);
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json"); 
			// content = new JavaString("Header,col2;value1,v1;value2,v2");
			connection.setRequestProperty("Content-Length", JavaString.valueOf(content.getBytes(charset).length));
			connection.setRequestProperty("X-SAP-PVL", "en-US");            
			DataOutputStream = Java.type("java.io.DataOutputStream");
			out = new DataOutputStream(connection.getOutputStream());
			out.write(content.getBytes(charset));
			out.flush();
			InputStream = Java.type("java.io.InputStream");
			inStream = connection.getContent();
		}
	} catch (error) {
		addResultDetails("error", error);
		inStream = connection.getErrorStream();
		addResultDetails("error", inStream);             
	}
	if (out !== null) out.close();
	Scanner = Java.type("java.util.Scanner");
	scanner = new Scanner(inStream, charset).useDelimiter("\\A")
	response = {
		content: scanner.hasNext() ? scanner.next() : "",
		code:    connection.getResponseCode(),
		message: connection.getResponseMessage(),
		headers: connection.getHeaderFields()
	}

	addResultDetails("responseContent", response.content);
	addResultDetails("responseCode", response.code);
	addResultDetails("responseMessage", response.message);
	return response;
}
