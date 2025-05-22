package vn.techmaster.nowj.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.techmaster.nowj.model.dto.DetectedRiskDTO;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIDetectRiskTest {
    @InjectMocks AIDetectRisk aiDetectRisk;

    @Test
    void extractJsonArray_validJson() {
        String text = "Some text before [ {\"category\":\"A\"} ] some after";
        String json = aiDetectRisk.extractJsonArray(text);
        assertEquals("[{\"category\":\"A\"}]", json.replaceAll("\\s", ""));
    }

    @Test
    void extractJsonArray_invalidJson_throws() {
        String text = "No array here";
        assertThrows(RuntimeException.class, () -> aiDetectRisk.extractJsonArray(text));
    }
}
