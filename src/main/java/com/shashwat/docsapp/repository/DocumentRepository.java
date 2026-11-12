package com.shashwat.docsapp.repository;

import com.shashwat.docsapp.model.Document;
import com.shashwat.docsapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByOwnerOrderByUpdatedAtDesc(User owner);
}