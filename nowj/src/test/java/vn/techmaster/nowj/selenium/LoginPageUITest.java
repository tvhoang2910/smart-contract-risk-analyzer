package vn.techmaster.nowj.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class LoginPageUITest extends BaseUITest {

    @Test
    @DisplayName("Kiểm tra hiển thị form đăng nhập")
    void testLoginFormDisplay() {
        driver.get("http://localhost:8080/login");

        // Đợi form xuất hiện
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.tagName("form")));

        WebElement form = driver.findElement(By.tagName("form"));
        assertNotNull(form, "Form đăng nhập không tồn tại");

        // Kiểm tra tiêu đề trang
        assertTrue(driver.getTitle().contains("Đăng nhập - NOWJ"), "Tiêu đề trang không đúng");

        // Kiểm tra các trường nhập liệu
        assertNotNull(driver.findElement(By.id("username")), "Trường username không tồn tại");
        assertNotNull(driver.findElement(By.id("password")), "Trường password không tồn tại");

        // Kiểm tra nút submit
        assertNotNull(driver.findElement(By.cssSelector("button[type='submit']")), "Nút đăng nhập không tồn tại");
    }

    @Test
    @DisplayName("Kiểm tra đăng nhập thành công")
    void testLoginSuccess() {
        driver.get("http://localhost:8080/login");

        // Nhập thông tin hợp lệ
        driver.findElement(By.id("username")).sendKeys("thaiviethoang2910@gmail.com");
        driver.findElement(By.id("password")).sendKeys("123456");

        // Submit form
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Đợi đến khi URL chứa "dashboard"
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("dashboard"));

        // Kiểm tra URL hiện tại có chứa dashboard
        assertTrue(driver.getCurrentUrl().contains("dashboard"), "Không chuyển hướng đến dashboard");
    }

    @Test
    @DisplayName("Kiểm tra đăng nhập thất bại với tài khoản sai")
    void testLoginFail() {
        driver.get("http://localhost:8080/login");

        // Nhập thông tin sai
        driver.findElement(By.id("username")).sendKeys("wrong@example.com");
        driver.findElement(By.id("password")).sendKeys("wrongpass");

        // Submit form
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Đợi thông báo lỗi xuất hiện
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-danger")));

        // Kiểm tra nội dung lỗi
        WebElement error = driver.findElement(By.cssSelector(".alert-danger"));
        assertTrue(error.isDisplayed(), "Thông báo lỗi không hiển thị");
        assertEquals("Email hoặc mật khẩu không chính xác!", error.getText().trim());
    }
}