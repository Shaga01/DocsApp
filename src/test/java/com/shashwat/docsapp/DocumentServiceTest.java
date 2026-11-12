package com.shashwat.docsapp;

import com.shashwat.docsapp.exception.BadRequestException;
import com.shashwat.docsapp.exception.ForbiddenException;
import com.shashwat.docsapp.model.Document;
import com.shashwat.docsapp.model.User;
import com.shashwat.docsapp.repository.UserRepository;
import com.shashwat.docsapp.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

// uses in-memory db so tests dont touch the real data
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:testdb")
@Transactional
public class DocumentServiceTest {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void sharingAndAccessRules() {
        User shashwat = userRepository.findByUsername("shashwat").get();
        User gautam = userRepository.findByUsername("gautam").get();
        User ajaia = userRepository.findByUsername("ajaia").get();

        Document doc = documentService.createDocument("Test doc", "<p>hello</p>", shashwat);

        // owner can open
        assertNotNull(documentService.getDocumentForUser(doc.getId(), shashwat));

        // gautam cannot open before sharing
        assertThrows(ForbiddenException.class,
                () -> documentService.getDocumentForUser(doc.getId(), gautam));

        // share with gautam, now he can open
        documentService.shareDocument(doc.getId(), "gautam", shashwat);
        assertNotNull(documentService.getDocumentForUser(doc.getId(), gautam));
        assertEquals(1, documentService.getSharedDocuments(gautam).size());

        // gautam is not owner so he cannot share it further
        assertThrows(ForbiddenException.class,
                () -> documentService.shareDocument(doc.getId(), "ajaia", gautam));

        // sharing twice gives error
        assertThrows(BadRequestException.class,
                () -> documentService.shareDocument(doc.getId(), "gautam", shashwat));

        // ajaia still has no access
        assertThrows(ForbiddenException.class,
                () -> documentService.getDocumentForUser(doc.getId(), ajaia));
    }
}