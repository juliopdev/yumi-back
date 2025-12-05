package com.yumi.about.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "about_policies")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Policy {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;
}