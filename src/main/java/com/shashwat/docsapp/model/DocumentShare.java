package com.shashwat.docsapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "document_shares",
        uniqueConstraints = @UniqueConstraint(columnNames = {"document_id", "user_id"}))
public class DocumentShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "document_id")
    private Document document;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User sharedWith;

    public DocumentShare() {}

    public DocumentShare(Document document, User sharedWith) {
        this.document = document;
        this.sharedWith = sharedWith;
    }

    public Long getId() { return id; }
    public Document getDocument() { return document; }
    public User getSharedWith() { return sharedWith; }
}