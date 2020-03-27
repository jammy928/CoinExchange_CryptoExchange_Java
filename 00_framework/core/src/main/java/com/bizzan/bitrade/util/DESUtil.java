package com.bizzan.bitrade.util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Createtime 2014年12月5日 14:22:00
 * @author Lxz
 * 
 */
public class DESUtil {
	/**
	 * UTF-8编码
	 */
	public static final String ENCODED_UTF8 = "UTF-8";
	/**
	 * GBK编码
	 */
	public static final String ENCODED_GBK = "GBK";
	/**
	 * GB2312编码
	 */
	public static final String ENCODED_GB2312 = "GB2312";
	/**
	 * ISO8859-1编码
	 */
	public static final String ENCODED_ISO88591 = "ISO8859-1";
	/**
	 * ASCII编码
	 */
	public static final String ENCODED_ASCII = "ASCII";
	/**
	 * UNICODE编码
	 */
	public static final String ENCODED_UNICODE = "UNICODE";
	/**
	 * CBC加密模式
	 */
	public static final String CIPHER_INSTANCE_CBC = "DES/CBC/PKCS5Padding";
	/**
	 * ECB加密模式
	 */
	public static final String CIPHER_INSTANCE_ECB = "DES/ECB/PKCS5Padding";
	
	/**
	 * DES加密
	 * 
	 * @param HexString
	 *            字符串（16位16进制字符串）
	 * @param keyStr
	 *            密钥16个1
	 * @throws Exception
	 */
	public static String ENCRYPTMethod(String HexString, String keyStr)
			throws Exception {
		String jmstr = "";
		try {
			byte[] theKey = null;
			String jqstr = getstrByte(keyStr).substring(0,8).toUpperCase();
			theKey = jqstr.getBytes(ENCODED_ASCII);
			Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_CBC);
			DESKeySpec desKeySpec = new DESKeySpec(theKey);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
			IvParameterSpec iv = new IvParameterSpec(theKey);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			byte[] theCph = cipher.doFinal(HexString.getBytes(ENCODED_GB2312));
			jmstr = toHexString(theCph).toUpperCase();
			jmstr = toHexString(theCph);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return jmstr;
	}
	
	public static String getstrByte(String str){
		if(null == str){
			throw new IllegalArgumentException(
			"str is null!");
		}
		MessageDigest messageDigest = getMessageDigest();
		byte[] digest;
		try {
			digest = messageDigest.digest(str.getBytes("ASCII"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("ASCII not supported!");
		}
		return new String(Hex.encodeHex(digest));
	}
	
	protected static final MessageDigest getMessageDigest() {
		String algorithm = "MD5";
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("No such algorithm ["
					+ algorithm + "]");
		}
	}
	
	/**
	 * DES加密
	 * @param HexString 字符串（16位16进制字符串）
	 * @param keyStr 密钥16个1
	 * @param keyENCODED  Keybyte转换编码
	 * @param HexStringENCODED 要加密值的转换byte编码
	 * @param CipherInstanceType 需要加密类型
	 * @return
	 * @throws Exception
	 */
	public static String ENCRYPTMethod(String HexString, String keyStr,String keyENCODED,String HexStringENCODED,String CipherInstanceType)
			throws Exception {
		String jmstr = "";
		try {
			byte[] theKey = null;
			String jqstr = getstrByte(keyStr).substring(0,8).toUpperCase();
			theKey = jqstr.getBytes(keyENCODED);
			Cipher cipher = Cipher.getInstance(CipherInstanceType);
			DESKeySpec desKeySpec = new DESKeySpec(theKey);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
			IvParameterSpec iv = new IvParameterSpec(theKey);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			byte[] theCph = cipher.doFinal(HexString.getBytes(HexStringENCODED));
			jmstr = toHexString(theCph).toUpperCase();
			jmstr = toHexString(theCph);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return jmstr;
	}
	
	public static String toHexString(byte b[]) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String plainText = Integer.toHexString(0xff & b[i]);
			if (plainText.length() < 2) {
                plainText = "0" + plainText;
            }
			hexString.append(plainText);
		}

		return hexString.toString();
	}
	
	/**
	 * DES解密方法
	 * @param message 需要解密字符串
	 * @param key 解密需要的KEY
	 * @param keyENCODED  解密KEY转换编码
	 * @param HexStringENCODED  解密字符串转换编码
	 * @param CipherInstanceType 解密类型
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String message, String key,String keyENCODED,String HexStringENCODED,String CipherInstanceType) throws Exception {

		byte[] bytesrc = convertHexString(message);
		byte[] theKey = null;
		String jqstr = getstrByte(key).substring(0,
				8).toUpperCase();
		theKey = jqstr.getBytes(keyENCODED);
		Cipher cipher = Cipher.getInstance(CipherInstanceType);
		DESKeySpec desKeySpec = new DESKeySpec(theKey);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(theKey);

		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

		byte[] retByte = cipher.doFinal(bytesrc);
		return new String(retByte,HexStringENCODED);
	}
	
	/**
	 * DES解密方法
	 * @param message
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String message, String key) throws Exception {

		byte[] bytesrc = convertHexString(message);
		byte[] theKey = null;
		String jqstr = getstrByte(key).substring(0,
				8).toUpperCase();
		theKey = jqstr.getBytes(ENCODED_ASCII);
		Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_CBC);
		DESKeySpec desKeySpec = new DESKeySpec(theKey);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(theKey);

		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

		byte[] retByte = cipher.doFinal(bytesrc);
		return new String(retByte,ENCODED_GB2312);
	}

	public static byte[] convertHexString(String ss) {
		byte digest[] = new byte[ss.length() / 2];
		for (int i = 0; i < digest.length; i++) {
			String byteString = ss.substring(2 * i, 2 * i + 2);
			int byteValue = Integer.parseInt(byteString, 16);
			digest[i] = (byte) byteValue;
		}

		return digest;
	}
	
	/*public static void main(String[] args) throws Exception {
		//String key = "27650099-564A-4869-99B3-363F8129C0CD";
		String key = "27650099-564A-4869-99B3-363F8129C0CD";
		//String smac = "UBtRcCfZt2oLKJTLwGkhQFBaaTnQpoFNa";
		String smac = "B46D731E4C5EC5F5D5A9B7E7755995C7";
       *//* JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("zhang", 11);
        jsonObject1.put("san", "no");
        jsonObject.put("data", jsonObject1);
        jsonObject.put("sign", Md5.md5Digest(jsonObject1.toJSONString()+smac));
		String jiami = jsonObject.toJSONString();
		System.out.println("加密数据:" + jiami);
		String a = ENCRYPTMethod(jiami, key).toUpperCase();
		System.out.println("加密后的数据为:" + a);*//*
		String b = decrypt("9E41D516836455DEDC82B077787BED7A1DA2E73B18B44546E79AFE2F06728540A6DDFC74F0A69545C7C989233948A8174DC8A6EBC6814B075288F41C12FD202AB23CF256A10EB8F32007CF6EE04F82E012BF545E52E82CC02D30F2386741F5B35073C14381226911C87C38E45ECEE44F\n", key);
		System.out.println("解密后的数据:" + b);
        JSONObject jsonObject2 = JSONObject.parseObject(b);
        //验证签名
        if (Md5.md5Digest(jsonObject2.getJSONObject("data").toJSONString()+smac).equals(jsonObject2.getString("sign"))){
            System.out.println("验证签名成功" );
            //取得数据
			System.out.println(jsonObject2.getJSONObject("data").toJSONString() );
		}else {
			System.out.println("验证签名失败" );
		}
	}*/
}
