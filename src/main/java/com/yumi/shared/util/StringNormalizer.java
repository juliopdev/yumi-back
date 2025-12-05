package com.yumi.shared.util;

import java.text.Normalizer;
import java.util.Locale;

public final class StringNormalizer {

  private StringNormalizer() {
  }

  /**
   * Devuelve un slug URL-friendly: minúsculas, sin acentos, sin espacios dobles,
   * caracteres alfanuméricos y guiones.
   */
  public static String toSlug(String input) {
    if (input == null || input.isBlank())
      return "";
    String trimmed = input.trim().replaceAll("\\s+", " ");
    String noAccents = Normalizer.normalize(trimmed, Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "");
    return noAccents
        .toLowerCase(Locale.ROOT)
        .replaceAll("[^a-z0-9\\s]", "")
        .replace(" ", "-");
  }

  /**
   * Normaliza nombres propios: capitaliza palabras y elimina espacios dobles.
   */
  public static String toProperName(String input) {
    if (input == null || input.isBlank())
      return "";
    String[] words = input.trim().replaceAll("\\s+", " ").split(" ");
    StringBuilder out = new StringBuilder();
    for (String w : words) {
      if (w.length() > 2) {
        out.append(Character.toUpperCase(w.charAt(0)))
            .append(w.substring(1).toLowerCase(Locale.ROOT));
      } else {
        out.append(w.toLowerCase(Locale.ROOT));
      }
      out.append(' ');
    }
    return out.toString().trim();
  }
}