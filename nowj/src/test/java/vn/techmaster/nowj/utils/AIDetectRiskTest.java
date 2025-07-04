package vn.techmaster.nowj.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AIDetectRiskTest {
    @InjectMocks
    AIDetectRisk aiDetectRisk;

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