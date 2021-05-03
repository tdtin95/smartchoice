package com.smartchoice.audit.service.repository;


import com.smartchoice.audit.service.entity.History;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "customer-history", path = "customer-history")
public interface HistoryRepository extends MongoRepository<History, String> {
}
