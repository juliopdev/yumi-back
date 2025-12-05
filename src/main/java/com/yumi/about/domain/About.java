package com.yumi.about.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "about_sections")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class About {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @OneToMany(mappedBy = "section", fetch = FetchType.LAZY)
  @Builder.Default
  private List<Card> cards = new ArrayList<>();

  @Column(name = "key", unique = true, nullable = false)
  private String key;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String subtitle;
}