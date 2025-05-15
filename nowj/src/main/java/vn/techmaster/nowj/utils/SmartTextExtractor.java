package vn.techmaster.nowj.utils;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Component
public class SmartTextExtractor {
    private static final String DEFAULT_DATA_PATH = "D:\\GitHub\\smart-contract-risk-analyzer\\nowj\\tessdata";
    private static final String DEFAULT_LANGUAGE = "vie";

    public String getTextFromImage(MultipartFile multipartFile) {
        try {
            File tempFile = convertToFile(multipartFile);
            String text = getTextFromImage(tempFile, DEFAULT_DATA_PATH, DEFAULT_LANGUAGE);
            tempFile.delete(); 
            return text;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi chuyển MultipartFile sang File: " + e.getMessage(), e);
        }
    }

    private File convertToFile(MultipartFile multipartFile) throws IOException {
        File tempFile = File.createTempFile("ocr_", "_" + multipartFile.getOriginalFilename());
        multipartFile.transferTo(tempFile);
        return tempFile;
    }

    public String getTextFromImage(File imageFile, String dataPath, String language) {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(dataPath);
        tesseract.setLanguage(language);
        try {
            return tesseract.doOCR(imageFile);
        } catch (TesseractException e) {
            throw new RuntimeException("Lỗi OCR khi xử lý ảnh: " + e.getMessage(), e);
        }
    }
}
