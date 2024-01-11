package com.replication.practice.logic.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.replication.practice.logic.entity.SimpleEntity;

public interface SimpleJpaRepository extends JpaRepository<SimpleEntity, Long> {
}
