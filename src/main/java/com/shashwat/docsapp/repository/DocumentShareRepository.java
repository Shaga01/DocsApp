package com.shashwat.docsapp.repository;

import com.shashwat.docsapp.model.Document;
import com.shashwat.docsapp.model.DocumentShare;
import com.shashwat.docsapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentShareRepository extends JpaRepository<DocumentShare, Long> {
    List<DocumentShare> findBySharedWith(User user);
    boolean existsByDocumentAndSharedWith(Document document, User user);
}