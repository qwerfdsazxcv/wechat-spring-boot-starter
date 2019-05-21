package com.mili.wechat.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;
import java.util.Random;

public class AESUtils {

	/**
	 * AES加密解密算法
	 */
	public static final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	//数据填充方式
	public static final String algorithmCBC_PKCS7 = "AES/CBC/PKCS7Padding";
	public static final String algorithmCBC_PKCS5 = "AES/CBC/PKCS5Padding";
	public static boolean initialized = false;
	/**
	 * 返回一个定长的带因子的固定的随机字符串(只包含大小写字母、数字)
	 * @param length 随机字符串长度
	 * @return 随机字符串
	 */
	public static String generateStringByKey(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
		}
		return sb.toString();
	}

	public static String Encrypt(String key, String text){
		try{
			byte []cipher = Encrypt(key.getBytes(), text.getBytes());
			return new BASE64Encoder().encode(cipher);
		}catch(Exception e){
			return "";
		}
	}

	public static String Decrypt(String key, String cipher){
		try{
			BASE64Decoder base64de = new BASE64Decoder();
			byte []cipherBytes =  base64de.decodeBuffer(cipher);
			byte []text = Decrypt(key.getBytes(), cipherBytes);
			return new String(text);
		}catch(Exception e){
			return "";
		}
	}

	static byte[] Encrypt(byte []key, byte []text){
		try{
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] aesKeyBytes = new byte[32];

			int len = key.length > 32 ? 32 : key.length;
			System.arraycopy(key, 0, aesKeyBytes, 0, len);

			SecretKeySpec keySpec = new SecretKeySpec(aesKeyBytes, "AES");

			byte[] iv = new byte[cipher.getBlockSize()];

			Random ran = new Random();
			ran.nextBytes(iv);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

			byte[] results = cipher.doFinal(text);

			byte[] retBytes = new byte[iv.length + results.length];
			System.arraycopy(iv, 0, retBytes, 0, iv.length);
			System.arraycopy(results, 0, retBytes, iv.length, results.length);

			return retBytes;

		}catch(Exception e){
			return null;
		}
	}

	static byte[] Decrypt(byte []key, byte []cipher){
		try{
			if (cipher.length < 32 || cipher.length % 16 != 0){
				return null;
			}

			Cipher cp = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] aesKeyBytes = new byte[32];	//32 bytes for AES-256

			int len = key.length > 32 ? 32 : key.length;
			System.arraycopy(key, 0, aesKeyBytes, 0, len);

			SecretKeySpec keySpec = new SecretKeySpec(aesKeyBytes, "AES");

			byte[] iv = new byte[cp.getBlockSize()];

			System.arraycopy(cipher, 0, iv, 0, iv.length);

			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			cp.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

			byte []cip = new byte[cipher.length - iv.length];

			System.arraycopy(cipher, iv.length, cip, 0, cip.length);
			byte[] results = cp.doFinal(cip);

			return results;
		}catch(Exception e){
			return null;
		}
	}

	/*
		* AES加密
	 * 填充模式AES/CBC/PKCS7Padding
	 * 解密模式128
	 * @param originalContent
	 * @param encryptKey
	 * @param ivByte
	 * @return
	 */
	public static byte[] encrypt(byte[] originalContent, byte[] encryptKey, byte[] ivByte) {
		Security.addProvider(new BouncyCastleProvider());
		try {
			Cipher cipher = Cipher.getInstance(algorithmCBC_PKCS7);
			SecretKeySpec skeySpec = new SecretKeySpec(encryptKey, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(ivByte));
			byte[] encrypted = cipher.doFinal(originalContent);
			return encrypted;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * AES解密
	 * 填充模式AES/CBC/PKCS7Padding
	 * 解密模式128
	 * @param content
	 *            目标密文
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] content, byte[] aesKey, byte[] ivByte) {
		initialize();
		try {
			Cipher cipher = Cipher.getInstance(algorithmCBC_PKCS7,"BC");
			Key sKeySpec = new SecretKeySpec(aesKey, "AES");
			cipher.init(Cipher.DECRYPT_MODE, sKeySpec, generateIV(ivByte));// 初始化
			byte[] result = cipher.doFinal(content);
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static void initialize() {
		if (initialized)
			return;
		Security.addProvider(new BouncyCastleProvider());
		initialized = true;
	}

	// 生成iv
	public static AlgorithmParameters generateIV(byte[] iv) throws Exception {
		AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
		params.init(new IvParameterSpec(iv));
		return params;
	}
}
