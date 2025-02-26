package Test;

import Data.DataHelper;
import Page.DashboardPage;
import Page.LoginPage;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTransferTest {
    private static final Logger logger = LoggerFactory.getLogger(MoneyTransferTest.class);

    int begBalance1;
    int begBalance2;
    int endBalance1;
    int endBalance2;
    int sum;
    DashboardPage dashboardPage;

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
        logger.info("Открыта главная страница");

        val loginPage = new LoginPage();
        val authInfo = DataHelper.getAuthInfo();
        logger.info("Данные для входа: {}", authInfo);

        val verificationPage = loginPage.validLogin(authInfo);
        logger.info("Выполнен вход, открыта страница верификации");

        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        logger.info("Код верификации: {}", verificationCode);

        dashboardPage = verificationPage.validVerify(verificationCode);
        logger.info("Верификация выполнена, открыта DashboardPage");

        begBalance1 = dashboardPage.getBalance("card1");
        begBalance2 = dashboardPage.getBalance("card2");
        logger.info("Начальные балансы: Card1={}, Card2={}", begBalance1, begBalance2);
    }

    @Test
    @DisplayName("Перевод денег со второй карты на первую")
    void shouldTransferMoneyFromSecondToFirstCard() {
        sum = begBalance2 / 2;

        val topUpPage = dashboardPage.clickTopUp("card1");
        val cardNum = DataHelper.getSecondCard().getNumber();
        val dashboardPage2 = topUpPage.successfulTopUp(Integer.toString(sum), cardNum);

        endBalance1 = dashboardPage2.getBalance("card1");
        endBalance2 = dashboardPage2.getBalance("card2");
        logger.info("Конечные балансы: Card1={}, Card2={}", endBalance1, endBalance2);

        assertEquals(begBalance1 + sum, endBalance1);
        assertEquals(begBalance2 - sum, endBalance2);
    }

    @Test
    @DisplayName("Перевод денег с первой карты на вторую")
    void shouldTransferMoneyFromFirstToSecondCard() {
        sum = begBalance1 / 2;

        val topUpPage = dashboardPage.clickTopUp("card2");
        val cardNum = DataHelper.getFirstCard().getNumber();
        val dashboardPage2 = topUpPage.successfulTopUp(Integer.toString(sum), cardNum);

        endBalance1 = dashboardPage2.getBalance("card1");
        endBalance2 = dashboardPage2.getBalance("card2");
        logger.info("Конечные балансы: Card1={}, Card2={}", endBalance1, endBalance2);

        assertEquals(begBalance1 - sum, endBalance1);
        assertEquals(begBalance2 + sum, endBalance2);
    }

    @Test
    @DisplayName("Не должен переводить больше, чем есть на карте")
    void shouldNotTransferMoreThanAvailable() {
        sum = begBalance1 + 100;
        String expectedErrorMessage = "Ошибка! Сумма превышает доступный баланс.";

        val topUpPage = dashboardPage.clickTopUp("card2");
        val cardNum = DataHelper.getFirstCard().getNumber();

        topUpPage.unsuccessfulTopUp(Integer.toString(sum), cardNum, expectedErrorMessage);

        logger.info("Попытка перевода суммы, превышающей баланс карты: {}", sum);
    }
}