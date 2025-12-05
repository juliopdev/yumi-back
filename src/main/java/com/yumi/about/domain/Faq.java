package com.yumi.about.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "about_faqs")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Faq {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String question;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String answer;
}