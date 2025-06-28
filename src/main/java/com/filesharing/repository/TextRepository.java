package com.filesharing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filesharing.model.TextEntity;

@Repository
public interface TextRepository extends JpaRepository<TextEntity, Long> {
}