package vn.techmaster.nowj.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import vn.techmaster.nowj.model.dto.DetectedRiskDTO;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class AIDetectRisk {
    private static final String API_KEY = "AIzaSyA2EUAKX9-g1azs34sYHa6euQ2hWfrfQ6A";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent?key="
            + API_KEY;
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .build();

    public List<DetectedRiskDTO> analyzeContractRisks(String document) throws IOException {
        String prompt = buildPrompt(document);
        String jsonOutput = callGeminiAndExtractJson(prompt);

        return mapper.readValue(jsonOutput, new TypeReference<List<DetectedRiskDTO>>() {
        });
    }

    private String buildPrompt(String doc) {
        return """
                Bạn là một chuyên gia phân tích rủi ro pháp lý chuyên về hợp đồng.
                Nhiệm vụ của bạn là phân tích văn bản hợp đồng sau đây.
                Hãy xác định các rủi ro tiềm ẩn, các điểm cần cải thiện, hoặc thông tin còn thiếu.

                Đối với MỖI rủi ro hoặc điểm cần cải thiện được xác định, hãy tạo một đối tượng JSON với các trường sau:
                - "category": ... (Phân loại rủi ro, ví dụ: Thanh toán, Vi phạm, Thời hạn, Phạm vi, Chấm dứt, Bảo mật, Trách nhiệm...)
                - "description": ... (Mô tả ngắn gọn về rủi ro)
                - "severity": ... (Mức độ nghiêm trọng, chỉ sử dụng một trong các giá trị: HIGH, MEDIUM, LOW)
                - "relevantContext": ... (Đoạn văn bản chính xác trong hợp đồng liên quan trực tiếp đến rủi ro này)
                - "explanation": ... (Giải thích chi tiết lý do tại sao đoạn văn bản đó là rủi ro và hậu quả tiềm ẩn)
                - "suggestion": ... (Gợi ý cụ thể để khắc phục, chỉnh sửa hoặc bổ sung vào hợp đồng để giảm thiểu rủi ro này)

                Toàn bộ phản hồi của bạn PHẢI là một MẢNG JSON hợp lệ chứa các đối tượng rủi ro đã xác định.
                PHẢN HỒI CỦA BẠN CHỈ ĐƯỢC CHỨA MẢNG JSON NÀY.
                TUYỆT ĐỐI KHÔNG bao gồm bất kỳ văn bản giới thiệu, giải thích, kết luận, ghi chú hay định dạng (như markdown block ` ```json `) nào khác Ở BÊN NGOÀI MẢNG JSON.
                Bắt đầu phản hồi của bạn bằng ký tự '[' và kết thúc bằng ký tự ']'.

                Văn bản cần phân tích:
                """
                + doc;
    }

    private String callGeminiAndExtractJson(String prompt) throws IOException {
        String requestBodyJson = mapper.writeValueAsString(
                new GeminiPromptRequest(prompt));

        Request request = new Request.Builder()
                .url(ENDPOINT)
                .post(RequestBody.create(requestBodyJson, MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("Gemini API error: " + response.code() + " - " + response.body());
            }

            JsonNode root = mapper.readTree(response.body().string());
            String textResponse = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            return extractJsonArray(textResponse);
        }
    }

    private String extractJsonArray(String text) {
        int start = text.indexOf('[');
        int end = text.lastIndexOf(']');
        if (start != -1 && end > start) {
            return text.substring(start, end + 1);
        }
        throw new RuntimeException("Không tìm thấy JSON array hợp lệ trong phản hồi: " + text);
    }

    static class GeminiPromptRequest {
        public Content[] contents;

        public GeminiPromptRequest(String text) {
            this.contents = new Content[] { new Content(text) };
        }

        static class Content {
            public Part[] parts;

            public Content(String text) {
                this.parts = new Part[] { new Part(text) };
            }
        }

        static class Part {
            public String text;

            public Part(String text) {
                this.text = text;
            }
        }
    }
}