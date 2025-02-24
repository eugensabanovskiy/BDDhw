import com.codeborne.selenide.Condition;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import web.netology.javaqa.DataHelper;


import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static org.junit.jupiter.api.Assertions.*;


public class MoneyTransferTest {
    int begBalance1;
    int begBalance2;
    int endBalance1;
    int endBalance2;
    int sum;
    DashboardPage dashboardPage;

    @BeforeEach
    void SetUp() {
        open("http://localhost:9999");
        System.out.println("Открыта главная страница");

        val loginPage = new LoginPage();
        val authInfo = DataHelper.getAuthInfo();
        System.out.println("Данные для входа: " + authInfo);

        val verificationPage = loginPage.validLogin(authInfo);
        System.out.println("Выполнен вход, открыта страница верификации");

        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        System.out.println("Код верификации: " + verificationCode);

        dashboardPage = verificationPage.validVerify(verificationCode);
        System.out.println("Верификация выполнена, открыта DashboardPage");

        begBalance1 = dashboardPage.getBalance(dashboardPage.card1);
        begBalance2 = dashboardPage.getBalance(dashboardPage.card2);
        System.out.println("Начальные балансы: Card1=" + begBalance1 + ", Card2=" + begBalance2);
    }

    @Test
    @DisplayName("Перевод денег сo второй карты на первую")
    void shouldTransferMoneyFromSecondToFirstCard() {
        sum = 100;
        val topUpPage = dashboardPage.clickTopUp(dashboardPage.card1);
        val cardNum = DataHelper.getSecondCard().getNumber();
        val dashboardPage2 = topUpPage.successfulTopUp(Integer.toString(sum), cardNum);
        endBalance1 = dashboardPage2.getBalance(dashboardPage2.card1);
        endBalance2 = dashboardPage2.getBalance(dashboardPage2.card2);
        assertEquals(begBalance1 + sum, endBalance1);
        assertEquals(begBalance2 - sum, endBalance2);
    }

    @Test
    @DisplayName("Перевод денег с первой карты на вторую")
    void shouldTransferMoneyFromFirstToSecondCard() {
        sum = 100;
        val topUpPage = dashboardPage.clickTopUp(dashboardPage.card2);
        val cardNum = DataHelper.getFirstCard().getNumber();
        val dashboardPage2 = topUpPage.successfulTopUp(Integer.toString(sum), cardNum);
        endBalance1 = dashboardPage2.getBalance(dashboardPage2.card1);
        endBalance2 = dashboardPage2.getBalance(dashboardPage2.card2);
        assertEquals(begBalance1 - sum, endBalance1);
        assertEquals(begBalance2 + sum, endBalance2);
    }

    @Test
    @DisplayName("Не должен переводить больше, чем есть на карте")
    void shouldNotTransferMoreThanAvailable() {
        sum = begBalance1 + 100;
        val topUpPage = dashboardPage.clickTopUp(dashboardPage.card2);
        val cardNum = DataHelper.getFirstCard().getNumber();
        topUpPage.unsuccessfulTopUp(Integer.toString(sum), cardNum);
    }
}