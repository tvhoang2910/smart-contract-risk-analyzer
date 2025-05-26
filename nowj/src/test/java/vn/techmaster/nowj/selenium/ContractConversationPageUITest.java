package vn.techmaster.nowj.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class ContractConversationPageUITest extends BaseUITest {

    @Test
    @DisplayName("Kiểm tra hiển thị trang hội thoại hợp đồng")
    void testContractConversationPageDisplay() {
        driver.get("http://localhost:8080/conversation");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Kiểm tra tiêu đề trang (theo tiêu đề thực tế từ HTML)
        assertTrue(driver.getTitle().contains("NOWJ"), "Tiêu đề trang không đúng");

        // Kiểm tra sidebar tồn tại
        WebElement sidebar = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("sidebar-wrapper")));
        assertNotNull(sidebar, "Sidebar không tồn tại");

        // Kiểm tra wrapper chính
        WebElement wrapper = driver.findElement(By.id("wrapper"));
        assertNotNull(wrapper, "Wrapper không tồn tại");

        // Kiểm tra khu vực nội dung chính
        WebElement content = driver.findElement(By.id("page-content-wrapper"));
        assertNotNull(content, "Page content wrapper không tồn tại");
    }
}
