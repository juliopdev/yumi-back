package com.yumi.about.infrastructure.persistence;

import com.yumi.about.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA de {@link AboutWebRepository}.
 */
@Repository
public interface JpaAboutRepository extends JpaRepository<About, Integer> {

  @Query("select a from About a order by a.key")
  List<About> findAllOrdered();

  Optional<About> findByKey(String key);

  @Query("select c from Card c where c.section.key = :sectionKey order by c.id")
  List<Card> findCardsBySectionKey(String sectionKey);

  @Query("select f from Faq f order by f.id")
  List<Faq> findAllFaqsOrdered();

  @Query("select p from Policy p order by p.id")
  List<Policy> findAllPoliciesOrdered();
}