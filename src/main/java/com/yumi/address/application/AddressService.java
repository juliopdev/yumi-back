package com.yumi.address.application;

import com.yumi.address.application.dto.AddressRequest;
import com.yumi.address.application.dto.AddressResponse;
import com.yumi.address.application.mapper.AddressMapper;
import com.yumi.address.domain.Address;
import com.yumi.address.infrastructure.persistence.JpaAddressRepository;
import com.yumi.auth.domain.User;
import com.yumi.auth.infrastructure.security.AuthContext;
import com.yumi.shared.exception.DuplicateResourceException;
import com.yumi.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Casos de uso de la entidad {@link Address}.
 * Todas las operaciones están restringidas al usuario autenticado.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AddressService {

  private final JpaAddressRepository repository;
  private final AuthContext authContext;

  public List<AddressResponse> getMyAddresses() {
    String email = currentUserEmail();
    return repository.findAllByOwnerEmail(email)
        .stream()
        .map(AddressMapper::toResponse)
        .toList();
  }

  @Transactional
  public AddressResponse createAddress(AddressRequest req) {
    String email = currentUserEmail();
    if (repository.existsByOwnerEmailAndCityAndStateAndZipCodeAndCountry(
        email, req.city(), req.state(), req.zipCode(), req.country())) {
      throw new DuplicateResourceException("Ya tienes una dirección idéntica registrada");
    }
    Address ad = repository.save(AddressMapper.toEntity(req, email));
    return AddressMapper.toResponse(ad);
  }

  @Transactional
  public AddressResponse updateAddress(Long id, AddressRequest req) {
    String email = currentUserEmail();
    Address ad = repository.findByIdAndOwnerEmail(id, email)
        .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada"));

    if (repository.existsByOwnerEmailAndCityAndStateAndZipCodeAndCountryAndIdNot(
        email, req.city(), req.state(), req.zipCode(), req.country(), id)) {
      throw new DuplicateResourceException("Ya tienes una dirección idéntica registrada");
    }
    ad.update(req.city().trim(), req.state().trim(), req.zipCode().trim(), req.country().trim(), req.street().trim());
    return AddressMapper.toResponse(ad);
  }

  @Transactional
  public void deleteAddress(Long id) {
    String email = currentUserEmail();
    Address ad = repository.findByIdAndOwnerEmail(id, email)
        .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada"));
    repository.delete(ad);
  }

  /* ---------- helpers ---------- */
  private String currentUserEmail() {
    return authContext.currentUser()
        .map(User::getEmail)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
  }
}