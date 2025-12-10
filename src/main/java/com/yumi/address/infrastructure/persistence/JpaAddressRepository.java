package com.yumi.address.infrastructure.persistence;

import com.yumi.address.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaAddressRepository extends JpaRepository<Address, Long> {

  List<Address> findAllByOwnerEmail(String ownerEmail);

  Optional<Address> findByIdAndOwnerEmail(Long id, String ownerEmail);

  Optional<Address> findFirstByOwnerEmail(String ownerEmail);

  boolean existsByOwnerEmailAndCityAndStateAndZipCodeAndCountry(
      String ownerEmail, String city, String state, String zipCode, String country);

  boolean existsByOwnerEmailAndCityAndStateAndZipCodeAndCountryAndIdNot(
      String ownerEmail, String city, String state, String zipCode, String country, Long idNot);
}