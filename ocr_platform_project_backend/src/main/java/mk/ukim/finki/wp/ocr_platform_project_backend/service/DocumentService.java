package mk.ukim.finki.wp.ocr_platform_project_backend.service;

import mk.ukim.finki.wp.ocr_platform_project_backend.model.Document;
import mk.ukim.finki.wp.ocr_platform_project_backend.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository repo;

    public DocumentService(DocumentRepository repo) {
        this.repo = repo;
    }

    public List<Document> getAllDocuments() {
        return repo.findAll();
    }

    public List<Document> searchDocuments(String author, String title,String model) {
        return repo.searchByAuthorAndTitle(author, title,model);
    }
}