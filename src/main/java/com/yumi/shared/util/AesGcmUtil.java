package com.yumi.shared.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public final class AesGcmUtil {

  private static final String ALG = "AES/GCM/NoPadding";
  private static final int TAG = 128;
  private static final int IV = 12;
  private static final SecretKey KEY = loadKey();

  private static SecretKey loadKey() {
    String hex = System.getProperty("AES256_KEY");
    if (hex == null || hex.length() != 64) {
      throw new IllegalStateException("AES256_KEY no existe o no es 256 bits");
    }
    byte[] keyBytes = new byte[32];
    for (int i = 0; i < 32; i++) {
      keyBytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
    }
    return new SecretKeySpec(keyBytes, "AES");
  }

  public static String encrypt(String plain) {
    if (plain == null) return null;
    try {
      byte[] iv = new byte[IV];
      new SecureRandom().nextBytes(iv);

      Cipher c = Cipher.getInstance(ALG);
      c.init(Cipher.ENCRYPT_MODE, KEY, new GCMParameterSpec(TAG, iv));

      byte[] cipherText = c.doFinal(plain.getBytes("UTF-8"));
      byte[] withIv = new byte[iv.length + cipherText.length];
      System.arraycopy(iv, 0, withIv, 0, iv.length);
      System.arraycopy(cipherText, 0, withIv, iv.length, cipherText.length);

      return Base64.getEncoder().encodeToString(withIv);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public static String decrypt(String encoded) {
    if (encoded == null) return null;
    try {
      byte[] withIv = Base64.getDecoder().decode(encoded);
      byte[] iv = new byte[IV];
      byte[] cipherText = new byte[withIv.length - IV];
      System.arraycopy(withIv, 0, iv, 0, IV);
      System.arraycopy(withIv, IV, cipherText, 0, cipherText.length);

      Cipher c = Cipher.getInstance(ALG);
      c.init(Cipher.DECRYPT_MODE, KEY, new GCMParameterSpec(TAG, iv));
      return new String(c.doFinal(cipherText), "UTF-8");
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}