package com.bizzan.bitrade.util;

import java.security.MessageDigest;

public class Md5 {
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D",
			"E", "F" };

	public static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest(s.getBytes("UTF-8"));
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		}
		catch (Exception e) {
			return null;
		}
	}

	/**
	 * 转换字节数组为16进制字串
	 * @param b 字节数组
	 * @return 16进制字串
	 */

	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
            n = 256 + n;
        }
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * MD5 摘要计算(byte[]).
	 * @param src byte[]
	 * @throws Exception
	 * @return byte[] 16 bit digest
	 */
	public static byte[] md5Digest(byte[] src) throws Exception {
		MessageDigest alg = MessageDigest.getInstance("MD5"); // MD5
																							// is
																							// 16
																							// bit
																							// message
																							// digest

		return alg.digest(src);
	}

	/**
	 * MD5 摘要计算(String).
	 * @param src String
	 * @throws Exception
	 * @return String
	 */
	public static String md5Digest(String src) throws Exception {
		return byteArrayToHexString(md5Digest(src.getBytes("UTF-8")));
	}

	/** Test crypt */
/*	public static void main(String[] args) {
		try {
			// 获得的明文数据
//			String desStr = "MERCHANTID=123456789&ORDERSEQ=20060314000001&ORDERDATE=20060314&ORDERAMOUNT=10000";
//			System.out.println("原文字符串 desStr ＝＝ " + desStr);
//			// 生成MAC
//			String MAC = md5Digest(desStr);
//			System.out.println("MAC == " + MAC);
//
//			// 使用key值生成 SIGN
//			String keyStr = "304F3A86606C585E";// 使用固定key
//			// 获得的明文数据
//			desStr = "UPTRANSEQ=20090327190924&MERCHANTID=3400000001&ORDERID=55120090327843921320&PAYMENT=1&RETNCODE=0000&RETNINFO=0000&PAYDATE=20090327";
//			// 将key值和明文数据组织成一个待签名的串
//			desStr = desStr + "&KEY=" + keyStr;
//			System.out.println("原文字符串 desStr ＝＝ " + desStr);
//			// 生成 SIGN
//			String SIGN = md5Digest(desStr);
//			System.out.println("SIGN == " + SIGN);
			String desStr ="李黎MengXiangTongXing2014";
			String desStr_after = md5Digest(desStr);
			System.out.println("111111加密后：desStr_after:"+desStr_after+" length:"+desStr_after.length());
			if(desStr_after.equalsIgnoreCase("96e79218965eb72c92a549dd5a330112")){
				System.out.println("一致");
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}*/

}
