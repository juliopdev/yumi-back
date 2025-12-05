package com.yumi.shared.util;

import org.springframework.data.convert.ReadingConverter;
import org.springframework.core.convert.converter.Converter;

@ReadingConverter
public class StringDecryptor implements Converter<String, String> {
  public String convert(String source) {
    return AesGcmUtil.decrypt(source);
  }
}