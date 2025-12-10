package com.yumi.shared.util;

import org.springframework.data.convert.WritingConverter;
import org.springframework.core.convert.converter.Converter;

@WritingConverter
public class StringEncryptor implements Converter<String, String> {
  public String convert(String source) {
    return AesGcmUtil.encrypt(source);
  }
}