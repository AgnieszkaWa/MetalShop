import com.github.javafaker.Faker;
import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MetalShop {

    String password = "test1";
    String username = "test12";
    static WebDriver driver = new ChromeDriver();

    @BeforeAll
    public static void setUp() {
        driver.manage().window().maximize();
        driver.get("http://serwer169007.lh.pl/autoinstalator/serwer169007.lh.pl/wordpress10772/");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));

    }

    @AfterAll
    public static void closeBrowser() {
    }

    @BeforeEach
    public void goToHomePage() {
        driver.findElement(By.linkText("Sklep")).click();
    }

    @Test
    public void emptyUsername() {

        login("", password);

        String error = driver.findElement(By.cssSelector(".woocommerce-error")).getText();
        Assertions.assertEquals("Błąd: Nazwa użytkownika jest wymagana.", error);

    }

    @Test
    public void emptyPassword() {

        login(username, "");
        String error = driver.findElement(By.cssSelector(".woocommerce-error")).getText();

        Assertions.assertEquals("Błąd: pole hasła jest puste.", error);

    }

    @Test
    public void registerSuccess() {
        driver.findElement(By.linkText("register")).click();
        Faker faker = new Faker();
        String registerUsername = faker.name().username();
        String email = registerUsername + faker.random().nextInt(10000) + "@wp.pl";
        WebElement user = driver.findElement(By.cssSelector("#user_login"));
        WebElement userEmail = driver.findElement(By.cssSelector("#user_email"));
        WebElement haslo = driver.findElement(By.cssSelector("#user_pass"));
        WebElement confirmHaslo = driver.findElement(By.cssSelector("#user_confirm_password"));
        WebElement submit = driver.findElement(By.cssSelector(".ur-submit-button"));

        user.sendKeys(registerUsername);
        userEmail.sendKeys(email);
        haslo.sendKeys(password);
        confirmHaslo.sendKeys(password);
        submit.click();

        Wait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".user-registration-message")));
        WebElement error = driver.findElement(By.cssSelector(".user-registration-message"));
        Assertions.assertEquals("User successfully registered.", error.getText());


    }

    @Test
    public void visibleElementsHomePage() {
        Assertions.assertTrue(driver.findElement(By.linkText("Softie Metal Shop")).isEnabled());
        Assertions.assertTrue(driver.findElement(By.cssSelector(".search-field")).isEnabled());
        driver.findElement(By.linkText("Moje konto")).click();
        Assertions.assertTrue(driver.findElement(By.linkText("Softie Metal Shop")).isEnabled());
        Assertions.assertTrue(driver.findElement(By.cssSelector(".search-field")).isEnabled());
    }

    @Test

    public void goToContact() {
        driver.findElement(By.linkText("Kontakt")).click();
        WebElement title = driver.findElement(By.cssSelector(".entry-title"));
        Assertions.assertEquals("Kontakt", title.getText());
    }

    @Test

    public void myAccountToHomePage() {

        driver.findElement(By.linkText("Moje konto")).click();
        driver.findElement(By.linkText("Softie Metal Shop")).click();
        WebElement title = driver.findElement(By.cssSelector(".page-title"));
        Assertions.assertEquals("Sklep", title.getText());
    }

    @Test
    public void sendMessage() {

        driver.findElement(By.linkText("Kontakt")).click();
        WebElement name = driver.findElement(By.name("your-name"));
        WebElement email = driver.findElement(By.name("your-email"));
        WebElement message = driver.findElement(By.name("your-message"));
        WebElement send = driver.findElement(By.xpath("//input[@type='submit']"));

        name.sendKeys("Agnieszka Wacholc");
        email.sendKeys("aga@wp.pl");
        message.sendKeys("Dzień dobry");
        send.click();

        WebElement sendMessage = driver.findElement(By.cssSelector(".wpcf7-response-output"));
        Wait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".wpcf7-response-output")));
        Assertions.assertTrue(sendMessage.isEnabled());

    }

    @Test
    public void shoppingCart() {
        driver.findElement(By.cssSelector(".cart-contents")).click();
        String cart = driver.findElement(By.cssSelector(".cart-empty")).getText();
        Assertions.assertEquals("Twój koszyk aktualnie jest pusty.", cart);
        driver.findElement(By.linkText("Wróć do sklepu")).click();
        driver.findElement(By.cssSelector(".post-24")).click();
        driver.findElement(By.name("add-to-cart")).click();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        WebElement addToCart = driver.findElement(By.cssSelector(".woocommerce-message"));
        Assertions.assertTrue(addToCart.isEnabled());

    }

    @Test
    public void removingProductFromCart() {
        driver.findElement(By.cssSelector(".post-24")).click();
        Wait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("add-to-cart")));
        driver.findElement(By.name("add-to-cart")).click();
        driver.findElement(By.cssSelector(".wc-forward")).click();
        driver.findElement(By.cssSelector(".remove")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".woocommerce-message")));
        String deleted = driver.findElement(By.cssSelector(".woocommerce-message")).getText();
        Assertions.assertTrue(deleted.contains("Usunięto: „Srebrna moneta 5g - UK 1980"));

    }

    @Test
    public void productsOnSale() {
        List<WebElement> elements = driver.findElements(By.cssSelector(".onsale"));

        for (int i = 0; i < elements.size(); i++) {

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
            driver.findElements(By.cssSelector(".onsale")).get(i).click();
            WebElement addToCart = driver.findElement(By.name("add-to-cart"));
            addToCart.click();
            WebElement backToSklep = driver.findElement(By.linkText("Softie Metal Shop"));
            backToSklep.click();
        }
        driver.findElement(By.cssSelector(".cart-contents")).click();
        WebElement amount = driver.findElement(By.cssSelector(".woocommerce-Price-amount"));
        Assertions.assertTrue(amount.isEnabled());


    }


    public void login(String login, String password) {
        driver.findElement(By.linkText("Moje konto")).click();
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginInput = driver.findElement(By.id("username"));
        WebElement logIn = driver.findElement(By.name("login"));
        loginInput.sendKeys(login);
        passwordInput.sendKeys(password);
        logIn.click();

    }


}





