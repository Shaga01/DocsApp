package com.shashwat.docsapp.service;

import com.shashwat.docsapp.exception.BadRequestException;
import com.shashwat.docsapp.exception.ForbiddenException;
import com.shashwat.docsapp.exception.NotFoundException;
import com.shashwat.docsapp.model.Document;
import com.shashwat.docsapp.model.DocumentShare;
import com.shashwat.docsapp.model.User;
import com.shashwat.docsapp.repository.DocumentRepository;
import com.shashwat.docsapp.repository.DocumentShareRepository;
import com.shashwat.docsapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentShareRepository shareRepository;
    private final UserRepository userRepository;

    public DocumentService(DocumentRepository documentRepository,
                           DocumentShareRepository shareRepository,
                           UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.shareRepository = shareRepository;
        this.userRepository = userRepository;
    }

    public boolean isOwner(Document doc, User user) {
        // compare ids, not objects
        return doc.getOwner().getId().equals(user.getId());
    }

    public Document getDocumentForUser(Long docId, User user) {
        Document doc = documentRepository.findById(docId)
                .orElseThrow(() -> new NotFoundException("Document not found"));

        if (isOwner(doc, user)) {
            return doc;
        }
        if (shareRepository.existsByDocumentAndSharedWith(doc, user)) {
            return doc;
        }
        throw new ForbiddenException("You do not have access to this document");
    }

    public List<Document> getOwnedDocuments(User user) {
        return documentRepository.findByOwnerOrderByUpdatedAtDesc(user);
    }

    public List<Document> getSharedDocuments(User user) {
        List<DocumentShare> shares = shareRepository.findBySharedWith(user);
        List<Document> docs = new ArrayList<>();
        for (int i = 0; i < shares.size(); i++) {
            docs.add(shares.get(i).getDocument());
        }
        return docs;
    }

    public Document createDocument(String title, String content, User owner) {
        if (title == null || title.trim().isEmpty()) {
            title = "Untitled document";
        }
        if (content == null) {
            content = "";
        }
        Document doc = new Document(title.trim(), content, owner);
        return documentRepository.save(doc);
    }

    public Document updateDocument(Long id, String title, String content, User user) {
        Document doc = getDocumentForUser(id, user);

        if (title != null) {
            if (title.trim().isEmpty()) {
                throw new BadRequestException("Title cannot be empty");
            }
            doc.setTitle(title.trim());
        }
        if (content != null) {
            doc.setContent(content);
        }
        doc.setUpdatedAt(LocalDateTime.now());
        return documentRepository.save(doc);
    }

    public void shareDocument(Long id, String username, User currentUser) {
        Document doc = getDocumentForUser(id, currentUser);

        if (!isOwner(doc, currentUser)) {
            throw new ForbiddenException("Only the owner can share this document");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new BadRequestException("Username is required");
        }

        User target = userRepository.findByUsername(username.trim().toLowerCase())
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        if (target.getId().equals(currentUser.getId())) {
            throw new BadRequestException("You already own this document");
        }
        if (shareRepository.existsByDocumentAndSharedWith(doc, target)) {
            throw new BadRequestException("Already shared with " + target.getUsername());
        }

        shareRepository.save(new DocumentShare(doc, target));
    }
}