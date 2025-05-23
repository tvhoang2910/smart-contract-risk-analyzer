package vn.techmaster.nowj.selenium;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterPageUITest {
    private static WebDriver driver;

    @BeforeAll
    static void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    @DisplayName("Kiểm tra hiển thị form đăng ký")
    void testRegisterFormDisplay() {
        driver.get("http://localhost:8080/register");
        WebElement form = driver.findElement(By.tagName("form"));
        assertNotNull(form);
        assertTrue(form.getText().contains("Đăng Ký"));
        assertNotNull(driver.findElement(By.id("fullName")));
        assertNotNull(driver.findElement(By.id("email")));
        assertNotNull(driver.findElement(By.id("password")));
        assertNotNull(driver.findElement(By.id("confirmPassword")));
    }

    @Test
    @DisplayName("Kiểm thử validation khi thiếu thông tin")
    void testRegisterValidation() {
        driver.get("http://localhost:8080/register");
        WebElement submit = driver.findElement(By.cssSelector("button[type='submit']"));
        submit.click();
        WebElement fullNameInput = driver.findElement(By.id("fullName"));
        assertEquals("", fullNameInput.getAttribute("value"));
        // Có thể kiểm tra thêm class is-invalid nếu có
    }
}
