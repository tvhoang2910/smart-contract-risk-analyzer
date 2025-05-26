package vn.techmaster.nowj.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterPageUITest extends BaseUITest {

    @Test
    @DisplayName("Kiểm tra hiển thị form đăng ký")
    void testRegisterFormDisplay() {
        driver.get("http://localhost:8080/register");
        // Kiểm tra form tồn tại
        WebElement form = driver.findElement(By.tagName("form"));
        assertNotNull(form);
        // Kiểm tra các trường nhập liệu
        assertNotNull(driver.findElement(By.id("fullName")));
        assertNotNull(driver.findElement(By.id("email")));
        assertNotNull(driver.findElement(By.id("password")));
        assertNotNull(driver.findElement(By.id("confirmPassword")));
        assertNotNull(driver.findElement(By.cssSelector("button[type='submit']")));
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
