package com.sap.afw.helper;

import com.businessobjects.foundation.logging.ILogger;
import com.businessobjects.foundation.logging.LoggerManager;
import com.crystaldecisions.sdk.exception.SDKException;
import com.crystaldecisions.sdk.framework.CrystalEnterprise;
import com.crystaldecisions.sdk.framework.IEnterpriseSession;
import com.crystaldecisions.sdk.framework.ISessionMgr;
import com.crystaldecisions.sdk.occa.infostore.IInfoObject;
import com.crystaldecisions.sdk.occa.infostore.IInfoStore;
import com.crystaldecisions.sdk.occa.infostore.IRemoteFile;
import com.crystaldecisions.sdk.occa.infostore.IStreamingDownloadFile;

public class BIPJavaSDK {

	private static final ILogger LOG = LoggerManager.getLogger(BIPJavaSDK.class);
			
	private static IEnterpriseSession eSession = null;
	private ISessionMgr sessionMgr = null;
    private IInfoStore infoStore;

    public BIPJavaSDK(String serializedSession) throws SDKException {
		sessionMgr = CrystalEnterprise.getSessionMgr();
		eSession = sessionMgr.getSession(serializedSession);
		infoStore = (IInfoStore)getEnterpriseSession().getService("InfoStore");
    }
    
    /**
     * Get the Enterprise session object instance
     */
    public IEnterpriseSession getEnterpriseSession() {
    	return (eSession);
    }


	public IInfoStore getInfoStore() {
		return infoStore;
	}

	/**
	 * Read the content if the file of this infoobject from FRS
	 * @param infoObject
	 * @return
	 */
	public String readFile(IInfoObject infoObject) {
		StringBuilder sb = new StringBuilder();
		IStreamingDownloadFile sdf = null;
		try {
			IRemoteFile rf = (IRemoteFile)infoObject.getFiles().get(0);
			sdf = rf.getStreamingDownloadFile(1024);
			sdf.openFile();
			// assertEquals("remote file", 1187, sdf.getSize());
			
			byte[] bytes = new byte[1024];
			while (sdf.hasNextChunk()) {
				sb.append(new String(sdf.nextChunk(), "UTF-8"));  // TODO: JLIN
			}
			if (LOG.isInfoEnabled()) {
				LOG.info(sb.toString());
			}
	
		} catch (Exception e) {
			LOG.error(e);
		} finally {
			if (sdf != null) {
				try {
					sdf.closeFile();
				} catch (SDKException e) {
					// $JL-EXC$
					LOG.error(e);
				}
			}
		}
		return sb.toString();
	}

}
