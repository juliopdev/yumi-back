package com.yumi.about.application.mapper;

import com.yumi.about.application.dto.*;
import com.yumi.about.domain.About;
import com.yumi.about.domain.Card;
import com.yumi.about.domain.Faq;
import com.yumi.about.domain.Policy;

import java.util.List;

/**
 * Transformaciones entre modelo y DTO.
 */
public enum AboutMapper {
  INSTANCE;

  public AboutResponse toAboutResponse(About about) {
    List<CardResponse> cards = about.getCards().stream()
        .map(this::toCardResponse)
        .toList();
    return new AboutResponse(
        about.getKey(),
        about.getTitle(),
        about.getSubtitle(),
        cards);
  }

  public CardResponse toCardResponse(Card card) {
    return new CardResponse(card.getIcon(), card.getTitle(), card.getDescription());
  }

  public FaqResponse toFaqResponse(Faq faq) {
    return new FaqResponse(faq.getQuestion(), faq.getAnswer());
  }

  public PolicyResponse toPolicyResponse(Policy policy) {
    return new PolicyResponse(policy.getTitle(), policy.getContent());
  }
}