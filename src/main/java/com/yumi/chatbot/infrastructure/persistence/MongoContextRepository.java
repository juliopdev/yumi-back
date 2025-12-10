package com.yumi.chatbot.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.yumi.chatbot.domain.Context;

public interface MongoContextRepository extends MongoRepository<Context, String> {
}