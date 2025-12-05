package com.yumi.about.application;

import com.yumi.about.application.dto.*;
import com.yumi.about.application.mapper.AboutMapper;
import com.yumi.about.infrastructure.persistence.JpaAboutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Casos de uso de la sección "Acerca de".
 * Read-only salvo que se agreguen comandos en el futuro.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AboutService {

  private final JpaAboutRepository repository;
  private final AboutMapper mapper = AboutMapper.INSTANCE;

  @Cacheable("about")
  public List<AboutResponse> getAllSections() {
    return repository.findAllOrdered().stream()
        .map(mapper::toAboutResponse)
        .toList();
  }

  @Cacheable("faqs")
  public List<FaqResponse> getFaqs() {
    return repository.findAllFaqsOrdered().stream()
        .map(mapper::toFaqResponse)
        .toList();
  }

  @Cacheable("policies")
  public List<PolicyResponse> getPolicies() {
    return repository.findAllPoliciesOrdered().stream()
        .map(mapper::toPolicyResponse)
        .toList();
  }
}