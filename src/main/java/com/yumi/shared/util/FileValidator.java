package com.yumi.shared.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

public final class FileValidator {

  private static final Set<String> IMG_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
  private static final long MAX_SIZE = 5 * 1024 * 1024; // 5 MB

  private FileValidator() {
  }

  public static boolean isImage(MultipartFile file) {
    if (file == null || file.isEmpty())
      return false;
    String ext = getExtension(file.getOriginalFilename());
    return IMG_EXTENSIONS.contains(ext.toLowerCase()) && file.getSize() <= MAX_SIZE;
  }

  private static String getExtension(String filename) {
    if (filename == null)
      return "";
    int lastDot = filename.lastIndexOf('.');
    return (lastDot == -1) ? "" : filename.substring(lastDot + 1);
  }

  public static boolean compareImage(MultipartFile file1, MultipartFile file2) {
    return sha256(file1).equals(sha256(file2));
  }

  private static String sha256(MultipartFile file) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(file.getBytes());
      StringBuilder sb = new StringBuilder();
      for (byte b : hash) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();
    } catch (NoSuchAlgorithmException | IOException e) {
      throw new RuntimeException("Error calculando hash SHA-256", e);
    }
  }
}