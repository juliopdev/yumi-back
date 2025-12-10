package com.yumi.shared.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

public final class AesGcmUtil {

  private static final String ALG = "AES/GCM/NoPadding";
  private static final int TAG = 128;
  private static final int IV = 12;
  private static final SecretKey KEY = loadKey();

  private static SecretKey loadKey() {
    try {
      KeyGenerator kg = KeyGenerator.getInstance("AES");
      kg.init(256);
      return kg.generateKey();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public static String encrypt(String plain) {
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