package mk.ukim.finki.wp.ocr_platform_project_backend.web;

import mk.ukim.finki.wp.ocr_platform_project_backend.model.Document;
import mk.ukim.finki.wp.ocr_platform_project_backend.repository.DocumentRepository;
import mk.ukim.finki.wp.ocr_platform_project_backend.service.DocumentService;
import mk.ukim.finki.wp.ocr_platform_project_backend.service.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private OcrService ocrService;
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam("author") String author,
                                    @RequestParam("title") String title,
                                    @RequestParam("model") String model
    ) throws IOException {


        if (file.isEmpty() || author.isBlank() || title.isBlank()) {
            return ResponseEntity.badRequest().body("Фајл, автор или наслов не се внесени");
        }

        String fileName = file.getOriginalFilename();

        List<Document> existingDocs = documentRepository.findAllByFileNameAndStatus(title,"DONE");
        boolean sameModelExists = existingDocs.stream()
                .anyMatch(d -> d.getOcrModel() != null && d.getOcrModel().equalsIgnoreCase(model));

        if (sameModelExists) {
            Document existing = existingDocs.stream()
                    .filter(d -> d.getOcrModel() != null && d.getOcrModel().equalsIgnoreCase(model))
                    .findFirst().get();
            Map<String, String> body = new HashMap<>();
            String modelDisplay = switch (model.toLowerCase()) {
                case "tesseract" -> "⚡ Tesseract";
                case "googlevision" -> "\uD83C\uDF10 Google Vision";
                default -> model.toUpperCase();
            };
            body.put("message", "Документот \"" + title + "\" веќе е обработен со моделот " + modelDisplay + ".");
            body.put("link", "/document/" + existing.getId());
            return ResponseEntity.status(409).body(body);
        }

        String projectRoot = new File("").getAbsolutePath();
        File uploadPath = new File(projectRoot, "uploads");


        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        String savedFileName = title.replace(" ","-") + ".pdf";
        File savedFile = new File(uploadPath, savedFileName);
        file.transferTo(savedFile);

        Document doc = new Document();
        doc.setFileName(title);
        doc.setStatus("UPLOADED");
        doc.setAuthor(author);
        doc.setTotalPages(0);
        doc.setProcessedPages(0);
        documentRepository.save(doc);
        ocrService.processPdf(savedFile, doc, documentRepository, model);




        return ResponseEntity.ok(doc.getId().toString());
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDocument(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {

        Optional<Document> docOpt = documentRepository.findById(id);
        if (docOpt.isEmpty()) return ResponseEntity.notFound().build();

        Document doc = docOpt.get();
        doc.setExtractedText(payload.get("extractedText"));
        documentRepository.save(doc);

        return ResponseEntity.ok(Map.of(
                "message", "Документот е успешно зачуван!",
                "link", "/document/" + doc.getId()
        ));
    }

    @GetMapping
    public List<Document> getDoneDocuments() {
        return documentRepository.findByStatus("DONE");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable Long id) {
        return documentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/text")
    public Document getDocumentContent(@PathVariable Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/search")
    public List<Document> search(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String model) {
        return documentService.searchDocuments(author, title, model);
    }
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        long total = documentRepository.getTotalDocuments();
        Double avg = documentRepository.getAverageProcessingTime();
        if (avg == null) avg = 0.0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDocuments", total);
        stats.put("averageProcessingTimeMs", avg);

        return ResponseEntity.ok(stats);
    }
    @GetMapping("/author-stats")
    public ResponseEntity<List<Map<String,Object>>> getAuthorStats() {
        List<Map<String,Object>> stats = documentRepository.findAuthorStats();
        return ResponseEntity.ok(stats);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        if (!documentRepository.existsById(id)) return ResponseEntity.notFound().build();
        documentRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Документот е успешно избришан!"));
    }
}