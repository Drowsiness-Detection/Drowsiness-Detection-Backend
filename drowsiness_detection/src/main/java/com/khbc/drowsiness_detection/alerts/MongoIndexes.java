package com.khbc.drowsiness_detection.alerts;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class MongoIndexes implements CommandLineRunner {

    private final MongoTemplate mongo;

    @Value("${app.ttlDays:30}")
    private long ttlDays;

    @Override
    public void run(String... args) {
        IndexOperations idx = mongo.indexOps("alerts");

        idx.ensureIndex(new Index().on("createdAt", Sort.Direction.DESC));
        idx.ensureIndex(new Index().on("status", Sort.Direction.ASC)
                .on("createdAt", Sort.Direction.DESC));

        // TTL: auto xoá sau X ngày
        idx.ensureIndex(new Index().on("createdAt", Sort.Direction.ASC)
                .expire(ttlDays, TimeUnit.DAYS));
    }
}
