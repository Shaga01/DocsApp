package com.shashwat.docsapp.controller;

import com.shashwat.docsapp.model.Document;
import com.shashwat.docsapp.model.User;
import com.shashwat.docsapp.service.AuthService;
import com.shashwat.docsapp.service.DocumentService;
import com.shashwat.docsapp.service.FileImportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final AuthService authService;
    private final FileImportService fileImportService;

    public DocumentController(DocumentService documentService, AuthService authService,
                              FileImportService fileImportService) {
        this.documentService = documentService;
        this.authService = authService;
        this.fileImportService = fileImportService;
    }

    @GetMapping
    public Map<String, Object> listDocuments(HttpSession session) {
        User user = authService.getCurrentUser(session);

        List<Map<String, Object>> owned = new ArrayList<>();
        List<Document> ownedDocs = documentService.getOwnedDocuments(user);
        for (int i = 0; i < ownedDocs.size(); i++) {
            owned.add(toSummary(ownedDocs.get(i)));
        }

        List<Map<String, Object>> shared = new ArrayList<>();
        List<Document> sharedDocs = documentService.getSharedDocuments(user);
        for (int i = 0; i < sharedDocs.size(); i++) {
            shared.add(toSummary(sharedDocs.get(i)));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("owned", owned);
        result.put("shared", shared);
        return result;
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody Map<String, String> body, HttpSession session) {
        User user = authService.getCurrentUser(session);
        Document doc = documentService.createDocument(body.get("title"), body.get("content"), user);
        return toFull(doc, user);
    }

    // upload a .txt or .md file and create a new document from it
    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file, HttpSession session) {
        User user = authService.getCurrentUser(session);
        String html = fileImportService.convertToHtml(file);
        String title = fileImportService.titleFromFileName(file.getOriginalFilename());
        Document doc = documentService.createDocument(title, html, user);
        return toFull(doc, user);
    }

    @GetMapping("/{id}")
    public Map<String, Object> getOne(@PathVariable Long id, HttpSession session) {
        User user = authService.getCurrentUser(session);
        Document doc = documentService.getDocumentForUser(id, user);
        return toFull(doc, user);
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Map<String, String> body,
                                      HttpSession session) {
        User user = authService.getCurrentUser(session);
        Document doc = documentService.updateDocument(id, body.get("title"), body.get("content"), user);
        return toFull(doc, user);
    }

    @PostMapping("/{id}/share")
    public Map<String, String> share(@PathVariable Long id, @RequestBody Map<String, String> body,
                                     HttpSession session) {
        User user = authService.getCurrentUser(session);
        documentService.shareDocument(id, body.get("username"), user);
        Map<String, String> res = new HashMap<>();
        res.put("message", "Shared with " + body.get("username"));
        return res;
    }

    // for the list page, no content needed
    private Map<String, Object> toSummary(Document doc) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", doc.getId());
        m.put("title", doc.getTitle());
        m.put("owner", doc.getOwner().getUsername());
        m.put("updatedAt", doc.getUpdatedAt().toString());
        return m;
    }

    // for the editor page
    private Map<String, Object> toFull(Document doc, User user) {
        Map<String, Object> m = toSummary(doc);
        m.put("content", doc.getContent());
        m.put("isOwner", documentService.isOwner(doc, user));
        return m;
    }
}