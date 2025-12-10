package com.yumi.about.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "about_cards")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_key", referencedColumnName = "key", insertable = false, updatable = false)
    private About section;

    private String icon;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;
}