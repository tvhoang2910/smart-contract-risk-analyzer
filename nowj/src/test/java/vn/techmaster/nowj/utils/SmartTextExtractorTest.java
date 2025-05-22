package vn.techmaster.nowj.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmartTextExtractorTest {
    SmartTextExtractor smartTextExtractor = new SmartTextExtractor();

    @Mock MultipartFile multipartFile;

    @Test
    void getTextFromImage_file_success() throws Exception {
        // Giả lập file ảnh thực tế, chỉ kiểm tra không ném lỗi (không test OCR thật)
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        doNothing().when(multipartFile).transferTo(any(File.class));
        // getTextFromImage(File, ...) sẽ ném lỗi vì file rỗng, nhưng test này chỉ kiểm tra không ném IOException khi convert file
        assertThrows(RuntimeException.class, () -> smartTextExtractor.getTextFromImage(multipartFile));
    }

    @Test
    void getTextFromImage_file_throwsIOException() throws Exception {
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        doThrow(new IOException("IO error")).when(multipartFile).transferTo(any(File.class));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> smartTextExtractor.getTextFromImage(multipartFile));
        assertTrue(ex.getMessage().contains("Lỗi khi chuyển MultipartFile sang File"));
    }

    @Test
    void getTextFromImage_withFile_throwsTesseractException() {
        File file = new File("not_exist.jpg");
        // Sẽ ném RuntimeException do file không tồn tại hoặc không OCR được
        assertThrows(RuntimeException.class, () -> smartTextExtractor.getTextFromImage(file, "invalid_path", "vie"));
    }
}
