package org.jtalks.tests.jcommune.tests.topic;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.List;

import static org.jtalks.tests.jcommune.common.JCommuneSeleniumTest.*;


/**
 * author: erik
 */
public class JC120PaginationTopicsRegisteredUser {

    @BeforeMethod
    @Parameters({"app-url", "uUsername", "uPassword"})
    public void setUp(String appUrl, String username, String password) {
        signIn(username, password);
        driver.get(appUrl);
        clickOnRandomBranch();
    }

    @AfterMethod
    @Parameters({"app-url"})
    public void logout(String appUrl) {
        logOut(appUrl);
    }

    @Test
    public void paginationTest() {
        List<WebElement> topicsButtons = topicPage.getTopicsButtons();

        if (topicPage.getTopicsList().size() != 5) {
            Assert.fail("Topic's count doesn't equal 5");
        }

        if (topicsButtons.size() == 0) {
            Assert.fail("Pagination bar doesn't present");
        }

        topicsButtons.get(1).click();

        if (topicPage.getTopicsList().size() != 5) {
            Assert.fail("Topic's count doesn't equal 5");
        }
    }
}
