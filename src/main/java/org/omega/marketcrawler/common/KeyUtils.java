package org.omega.marketcrawler.common;

import java.nio.charset.Charset;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class KeyUtils {
	
	private static final String HMAC_SHA512_ALGORITHM = "HmacSHA512";
	// Mac sha512_HMAC = Mac.getInstance("HMAC/SHA512");
	public static String signWithHmacSHA512(String data, String secretKey) {
		String resu = null;
		
		try {
			// get an hmac_sha512 key from the raw key bytes
            SecretKeySpec signingKeySpec = new SecretKeySpec(secretKey.getBytes(Charset.forName("UTF-8")), HMAC_SHA512_ALGORITHM);

            // get an hmac_sha512 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance(HMAC_SHA512_ALGORITHM);
            mac.init(signingKeySpec);

            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(data.getBytes(Charset.forName("UTF-8")));

            // base64-encode the hmac
            resu = new String(Base64.encodeBase64(rawHmac), Charset.forName("UTF-8"));

//            if (log.isDebugEnabled()) {
//                log.debug(data + "'s HmacSHA512: " + result);
//            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resu;
	}
	
}
