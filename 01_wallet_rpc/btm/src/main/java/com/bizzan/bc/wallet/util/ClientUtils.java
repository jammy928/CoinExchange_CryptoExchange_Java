package com.bizzan.bc.wallet.util;

import io.bytom.common.Configuration;
import io.bytom.exception.BytomException;
import io.bytom.http.Client;

public class ClientUtils {
	
	private static Client client = null;

	public static Client generateClient(String coreURL, String accessToken) throws BytomException {
		if(client == null) {
		    if (coreURL == null || coreURL.isEmpty()) {
		        coreURL = "http://127.0.0.1:9888/";
		    }
		    client = new Client(coreURL, accessToken);
		}
		return client;
	}
}
