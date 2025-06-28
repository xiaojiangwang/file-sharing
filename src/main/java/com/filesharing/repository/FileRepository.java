package com.filesharing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.filesharing.model.FileEntity;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
}