package mk.ukim.finki.wp.ocr_platform_project_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String status; // UPLOADED / PROCESSING / DONE / FAILED

    private int totalPages;
    private int processedPages;
    private String author;
    @Lob
    private String extractedText;
    @Column(name = "processing_time_ms")
    private Long processingTimeMs;
    private String ocrModel; // tesseract / googlevison /


}