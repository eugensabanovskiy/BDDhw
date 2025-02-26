package Page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DashboardPage {
    private SelenideElement heading = $("[data-test-id=dashboard]");
    private ElementsCollection cards = $$("div[data-test-id]");

    public DashboardPage() {
        heading.shouldBe(visible);
    }

    public TopUpPage clickTopUp(String cardTestId) {
        $("div[data-test-id='" + cardTestId + "']")
                .find("button[data-test-id=action-deposit]")
                .click();
        return new TopUpPage();
    }

    public int getBalance(int cardIndex) {
        String cardText = cards.get(cardIndex).innerText();
        String[] text = cardText.split(" ");
        return Integer.parseInt(text[5]);
    }

    public int getBalance(String cardNumber) {
        SelenideElement card = cards.findBy(Condition.text(cardNumber));
        String[] text = card.innerText().split(" ");
        return Integer.parseInt(text[5]);
    }
}