package vn.techmaster.nowj.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class SettingsPageUITest extends BaseUITest {

    @Test
    @DisplayName("Kiểm tra hiển thị trang cài đặt")
    void testSettingsPageDisplay() {
        // Bước 1: Đăng nhập
        driver.get("http://localhost:8080/login");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Nhập thông tin đăng nhập hợp lệ
        driver.findElement(By.id("username")).sendKeys("thaiviethoang2910@gmail.com");
        driver.findElement(By.id("password")).sendKeys("123456");

        // Submit form
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Cải thiện bước đợi sau đăng nhập:
        try {
            wait.until(ExpectedConditions.urlContains("dashboard"));
            // Đợi một phần tử cụ thể trên trang dashboard xuất hiện để đảm bảo đã tải hoàn
            // chỉnh
            // Thí dụ: Đợi phần tử chứa tên người dùng hiển thị
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Chào,')]")));
            System.out.println("Đăng nhập thành công và đã vào dashboard.");
        } catch (TimeoutException e) {
            String currentUrl = driver.getCurrentUrl();
            String pageSource = driver.getPageSource();
            System.err.println("Lỗi Timeout: Không thể vào trang dashboard sau đăng nhập.");
            System.err.println("Current URL: " + currentUrl);
            System.err.println(
                    "Page source (first 500 chars): " + pageSource.substring(0, Math.min(pageSource.length(), 500)));
            fail("Đăng nhập không thành công hoặc không thể tải trang dashboard.");
        }

        // Bước 2: Click vào link "Cài đặt" từ sidebar
        WebElement settingsLink = null; // Khởi tạo settingsLink để tránh lỗi "may not have been initialized"
        try {
            // Tìm và đợi link "Cài đặt" có thể click được.
            // Nếu không tìm thấy, TimeoutException sẽ được ném ra và bị bắt bởi khối catch
            // TimeoutException bên dưới.
            settingsLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/settings']")));

            // Cuộn đến phần tử trước khi cố gắng click để đảm bảo nó nằm trong viewport
            // Lưu ý: Dòng này chỉ thực thi nếu settingsLink đã được tìm thấy thành công
            // (không null)
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", settingsLink);

            // Cố gắng click thông thường
            settingsLink.click();
            System.out.println("Đã click vào link Cài đặt.");

        } catch (TimeoutException e) {
            // Xử lý khi elementToBeClickable không tìm thấy phần tử trong thời gian chờ
            System.err.println(
                    "Lỗi Timeout: Không tìm thấy hoặc không thể click vào link 'Cài đặt' trong thời gian chờ.");
            System.err.println("Current URL: " + driver.getCurrentUrl());
            fail("Không tìm thấy link 'Cài đặt' hoặc không thể click vào đó: " + e.getMessage());
        } catch (ElementClickInterceptedException e) {
            // Xử lý khi click thông thường bị chặn bởi một phần tử khác
            System.out.println("ElementClickInterceptedException caught. Attempting to click with JavascriptExecutor.");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            // Tại đây, settingsLink chắc chắn đã được gán một giá trị hợp lệ, không null
            js.executeScript("arguments[0].click();", settingsLink);
        } catch (Exception e) {
            // Bắt các ngoại lệ tổng quát khác có thể xảy ra trong quá trình tìm kiếm hoặc
            // click
            System.err.println("An unexpected error occurred while processing settings link: " + e.getMessage());
            e.printStackTrace();
            // In URL và page source để debug thêm
            System.err.println("Current URL at error: " + driver.getCurrentUrl());
            System.err.println("Page source (first 500 chars) at error: "
                    + driver.getPageSource().substring(0, Math.min(driver.getPageSource().length(), 500)));
            fail("Đã xảy ra lỗi không mong muốn khi tương tác với link 'Cài đặt': " + e.getMessage());
        }

        // Bước 3: Đợi tiêu đề chứa "Cài đặt" sau khi click
        try {
            // Đợi cho URL thay đổi thành "/settings"
            wait.until(ExpectedConditions.urlContains("/settings"));
            // Sau đó đợi tiêu đề trang Cài đặt xuất hiện
            wait.until(ExpectedConditions.titleContains("Cài đặt"));
            System.out.println("Đã chuyển đến trang Cài đặt thành công.");
        } catch (TimeoutException e) {
            String currentUrl = driver.getCurrentUrl();
            String pageSource = driver.getPageSource();
            System.err.println("Lỗi Timeout: Không thể chuyển đến trang Cài đặt.");
            System.err.println("Current URL after click: " + currentUrl);
            System.err.println("Page source (first 500 chars) after click: "
                    + pageSource.substring(0, Math.min(pageSource.length(), 500)));
            fail("Không thể tải trang Cài đặt sau khi click: " + e.getMessage());
        }

        // Bước 4: Kiểm tra các phần tử trên trang
        assertTrue(driver.getTitle().contains("Cài đặt"), "Tiêu đề trang không đúng");

        WebElement sidebar = driver.findElement(By.id("sidebar-wrapper"));
        assertNotNull(sidebar, "Sidebar không tồn tại");
        assertTrue(sidebar.isDisplayed(), "Sidebar không hiển thị");

        WebElement wrapper = driver.findElement(By.id("wrapper"));
        assertNotNull(wrapper, "Wrapper không tồn tại");

        WebElement content = driver.findElement(By.id("page-content-wrapper"));
        assertNotNull(content, "Page content wrapper không tồn tại");
        assertTrue(content.isDisplayed(), "Khu vực nội dung không hiển thị");

        // Bước 5: Kiểm tra tiêu đề chính của trang
        WebElement pageTitle = driver.findElement(By.className("mb-4")); // <h4 class="mb-4 fw-bold">Cài đặt hệ
                                                                         // thống</h4>
        assertNotNull(pageTitle, "Không tìm thấy tiêu đề chính");
        assertTrue(pageTitle.getText().contains("Cài đặt hệ thống"), "Tiêu đề chính không khớp");
    }
}