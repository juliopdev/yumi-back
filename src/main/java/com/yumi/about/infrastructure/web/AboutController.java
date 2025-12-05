package com.yumi.about.infrastructure.web;

import com.yumi.about.application.AboutService;
import com.yumi.about.application.dto.*;
import com.yumi.shared.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints públicos para la sección "Acerca de".
 */
@RestController
@RequestMapping(AboutController.BASE_PATH)
@RequiredArgsConstructor
public class AboutController {

  public static final String BASE_PATH = "/api/about";
  private final AboutService service;

  @GetMapping
  public ApiResponse<List<AboutResponse>> getAll(HttpServletRequest request) {
    return ApiResponse.ok(service.getAllSections(), request);
  }

  @GetMapping("/faqs")
  public ApiResponse<List<FaqResponse>> getFaqs(HttpServletRequest request) {
    return ApiResponse.ok(service.getFaqs(), request);
  }

  @GetMapping("/policies")
  public ApiResponse<List<PolicyResponse>> getPolicies(HttpServletRequest request) {
    return ApiResponse.ok(service.getPolicies(), request);
  }
}