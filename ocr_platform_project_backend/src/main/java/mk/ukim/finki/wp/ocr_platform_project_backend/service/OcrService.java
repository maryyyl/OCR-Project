package mk.ukim.finki.wp.ocr_platform_project_backend.service;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import mk.ukim.finki.wp.ocr_platform_project_backend.model.Document;
import mk.ukim.finki.wp.ocr_platform_project_backend.repository.DocumentRepository;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;


import java.util.concurrent.CompletableFuture;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;


import java.util.List;


@Service
public class OcrService {
    @Async
    public CompletableFuture<Void> processPdf(File pdfFile,
                                              Document doc,
                                              DocumentRepository repo,
                                              String model) {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFRenderer renderer = new PDFRenderer(document);
            int totalPages = document.getNumberOfPages();

            doc.setTotalPages(totalPages);
            doc.setProcessedPages(0);
            doc.setStatus("PROCESSING");
            repo.save(doc);

            StringBuilder fullText = new StringBuilder();
            long start = System.currentTimeMillis();

            for (int page = 0; page < totalPages; page++) {
                BufferedImage image = renderer.renderImageWithDPI(page, 300);

                String pageText;
                switch (model.toLowerCase()) {
                    case "tesseract":
                        pageText = processWithTesseract(image);
                        break;
                    case "googlevision":
                        pageText = processWithGoogleVision(image);
                        break;
                    default:
                        pageText = "[Unknown OCR model: " + model + "]";
                }

                fullText.append(pageText).append("\n\n");

                doc.setProcessedPages(page + 1);
                repo.save(doc);
            }

            long end = System.currentTimeMillis();
            doc.setProcessingTimeMs(end - start);
            doc.setExtractedText(fullText.toString());
            doc.setStatus("DONE");
            doc.setOcrModel(model);
            repo.save(doc);

        } catch (Exception e) {
            doc.setStatus("FAILED");
            doc.setOcrModel(model);
            repo.save(doc);
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(null);
    }
    private String processWithTesseract(BufferedImage image) throws TesseractException {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/");
        tesseract.setLanguage("mkd");
        return tesseract.doOCR(image);
    }
    private String processWithGoogleVision(BufferedImage image) throws Exception {
        System.out.println("GOOGLE_APPLICATION_CREDENTIALS: " + System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
        System.out.println("Starting Google Vision OCR...");
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            ByteString imgBytes;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(image, "png", baos);
                imgBytes = ByteString.copyFrom(baos.toByteArray());
            }

            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder()
                    .setType(Feature.Type.DOCUMENT_TEXT_DETECTION)
                    .build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();

            List<AnnotateImageResponse> responses = vision
                    .batchAnnotateImages(List.of(request))
                    .getResponsesList();

            StringBuilder sb = new StringBuilder();
            for (AnnotateImageResponse res : responses) {
                sb.append(res.getFullTextAnnotation().getText());
            }
            return sb.toString();
        }
    }


}