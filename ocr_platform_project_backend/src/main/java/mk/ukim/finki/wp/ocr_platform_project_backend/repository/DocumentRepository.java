package mk.ukim.finki.wp.ocr_platform_project_backend.repository;

import mk.ukim.finki.wp.ocr_platform_project_backend.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByFileName(String fileName);
    List<Document> findByAuthorContainingIgnoreCase(String author);

    List<Document> findByFileNameContainingIgnoreCase(String title);

    @Query("SELECT d FROM Document d WHERE " +
            "(:author IS NULL OR LOWER(d.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
            "(:title IS NULL OR LOWER(d.fileName) LIKE LOWER(CONCAT('%', :title, '%')))")
    List<Document> searchByAuthorAndTitle(@Param("author") String author,
                                          @Param("title") String title);
    @Query("SELECT AVG(d.processingTimeMs) FROM Document d")
    Double getAverageProcessingTime();

    @Query("SELECT COUNT(d) FROM Document d")
    Long getTotalDocuments();

    @Query("SELECT new map(d.author as author, COUNT(d) as count) " +
            "FROM Document d GROUP BY d.author")
    List<Map<String,Object>> findAuthorStats();
    List<Document> findByStatus(String status);
    List<Document> findAllByFileName(String fileName);
    List<Document> findAllByFileNameAndStatus(String fileName, String status);
    @Query("SELECT d FROM Document d WHERE " +
            "(:author IS NULL OR LOWER(d.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
            "(:title IS NULL OR LOWER(d.fileName) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:model IS NULL OR LOWER(d.ocrModel) = LOWER(:model))")
    List<Document> searchByAuthorAndTitle(@Param("author") String author,
                                          @Param("title") String title,
                                          @Param("model") String model);
}