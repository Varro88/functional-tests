package org.jtalks.tests.jcommune;

import org.jtalks.tests.jcommune.webdriver.action.Branches;
import org.jtalks.tests.jcommune.webdriver.action.Notifications;
import org.jtalks.tests.jcommune.webdriver.action.Topics;
import org.jtalks.tests.jcommune.webdriver.action.Users;
import org.jtalks.tests.jcommune.webdriver.entity.branch.Branch;
import org.jtalks.tests.jcommune.webdriver.entity.topic.Post;
import org.jtalks.tests.jcommune.webdriver.entity.topic.Topic;
import org.jtalks.tests.jcommune.webdriver.entity.user.User;
import org.jtalks.tests.jcommune.webdriver.exceptions.ValidationException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static org.jtalks.tests.jcommune.webdriver.JCommuneSeleniumConfig.driver;
import static org.jtalks.tests.jcommune.webdriver.page.Pages.mainPage;

/**
 * @author Andrey Ivanov
 * @author Andrey Surzhan
 * @author Pancheshenko Andrey
 */
public class TopicNotificationTest {
    private final String NOTIFICATION_BRANCH = "Notification tests";
    private final String BRANCH_NAME_TO_MOVE_TOPIC_IN = "Classical Mechanics";

    @BeforeMethod(alwaysRun = true)
    @Parameters({"appUrl"})
    public void setupCase(String appUrl) throws ValidationException {
        driver.get(appUrl);
        mainPage.logOutIfLoggedIn(driver);
    }

    // create topic

    @Test
    public void topicCreatedByOther_ifSubscribedToBranch_shouldReceiveBranchNotificationNotTopic() throws Exception {
        Topic topic = new Topic().withBranch(NOTIFICATION_BRANCH);

        User subscriber = Users.signUpAndSignIn();
        Branches.subscribe(topic.getBranch());

        Users.signUpAndSignIn();
        Topics.createTopic(topic);

        Users.logOutAndSignIn(subscriber);
        Branches.unsubscribeIgnoringFail(topic.getBranch());
        Notifications.assertNotificationOnTopicNotSentBranchSent(topic, subscriber);
    }

    @Test
    public void topicCreatedByOther_ifNotSubscribedToBranch_shouldNotReceiveNotifications() throws Exception {
        Topic topic = new Topic().withBranch(NOTIFICATION_BRANCH);

        User subscriber = Users.signUpAndSignIn();
        Branches.unsubscribe(topic.getBranch());

        Users.signUpAndSignIn();
        Topics.createTopic(topic);

        Notifications.assertNotificationsNotSent(topic, subscriber);
    }

    @Test
    public void ownTopicCreatedByUser_ifHeSubscribedToBranch_shouldNotReceiveNotifications() throws Exception {
        User creator = Users.signUpAndSignIn();
        Topic topic = new Topic().withBranch(NOTIFICATION_BRANCH);

        Branches.subscribe(topic.getBranch());
        Topics.createTopic(topic);

        Branches.unsubscribeIgnoringFail(topic.getBranch());
        Notifications.assertNotificationsNotSent(topic, creator);
    }

    // edit topic

    @Test
    public void topicEditedByOther_ifSubscribedToBothBranchAndTopic_shouldNotReceiveNotifications() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));

        User subscriber = Users.signUpAndSignIn();
        Branches.subscribe(topic.getBranch());
        Topics.subscribe(topic);

        Users.logOutAndSignIn(topicCreator);
        Topics.editPost(topic, topic.getFirstPost());

        Users.logOutAndSignIn(subscriber);
        Branches.unsubscribeIgnoringFail(topic.getBranch());

        Notifications.assertNotificationsNotSent(topic, subscriber);
    }

    @Test
    public void topicEditedByOther_ifSubscribedToBranchOnly_shouldNotReceiveNotifications() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));

        User subscriber = Users.signUpAndSignIn();
        Branches.subscribe(topic.getBranch());
        Topics.unsubscribe(topic);

        Users.logOutAndSignIn(topicCreator);
        Topics.editPost(topic, topic.getFirstPost());

        Users.logOutAndSignIn(subscriber);
        Branches.unsubscribeIgnoringFail(topic.getBranch());

        Notifications.assertNotificationsNotSent(topic, subscriber);
    }

    @Test
    public void topicEditedByOther_ifSubscribedToTopicOnly_shouldNotReceiveNotifications() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));

        User subscriber = Users.signUpAndSignIn();
        Branches.unsubscribe(topic.getBranch());
        Topics.subscribe(topic);

        Users.logOutAndSignIn(topicCreator);
        Topics.editPost(topic, topic.getFirstPost());

        Notifications.assertNotificationsNotSent(topic, subscriber);
    }

    @Test
    public void topicEditedByUser_ifHeSubscribedToBothBranchAndTopic_shouldNotReceiveNotifications() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));

        Topics.subscribe(topic);
        Branches.subscribe(topic.getBranch());
        Topics.editPost(topic, topic.getFirstPost());

        Branches.unsubscribeIgnoringFail(topic.getBranch());

        Notifications.assertNotificationsNotSent(topic, topicCreator);
    }

    // add post

    @Test
    public void otherUserPostsInTopic_ifSubscribedToBothBranchAndTopic_shouldReceiveTopicNotificationNotBranch() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));
        Topics.subscribe(topic);
        Branches.subscribe(topic.getBranch());

        Users.signUpAndSignIn();
        Topics.postAnswer(topic);

        Users.logOutAndSignIn(topicCreator);
        Branches.unsubscribeIgnoringFail(topic.getBranch());

        Notifications.assertNotificationOnTopicSentBranchNotSent(topic, topicCreator);
    }

    @Test
    public void otherUserPostsInTopic_ifSubscribedToBranchOnly_shouldNotReceiveNotifications() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));
        Topics.unsubscribe(topic);
        Branches.subscribe(topic.getBranch());

        Users.signUpAndSignIn();
        Topics.postAnswer(topic);

        Users.logOutAndSignIn(topicCreator);
        Branches.unsubscribeIgnoringFail(topic.getBranch());

        Notifications.assertNotificationsNotSent(topic, topicCreator);
    }

    @Test
    public void otherUserPostsInTopic_ifSubscribedToTopicOnly_shouldReceiveTopicNotificationNotBranch() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));
        Topics.subscribe(topic);
        Branches.unsubscribe(topic.getBranch());

        Users.signUpAndSignIn();
        Topics.postAnswer(topic);

        Notifications.assertNotificationOnTopicSentBranchNotSent(topic, topicCreator);
    }

    @Test
    public void userPostsInOwnTopic_ifHeSubscribedToBothBranchAndTopic_shouldNotReceiveNotifications() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));
        Topics.subscribe(topic);
        Branches.subscribe(topic.getBranch());
        Topics.postAnswer(topic);
        Branches.unsubscribeIgnoringFail(topic.getBranch());

        Notifications.assertNotificationsNotSent(topic, topicCreator);
    }

    @Test
    public void userPostsInOwnTopic_ifSubscribedToTopicOnly_shouldNotReceiveNotifications() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));
        Topics.subscribe(topic);
        Branches.unsubscribe(topic.getBranch());
        Topics.postAnswer(topic);

        Notifications.assertNotificationsNotSent(topic, topicCreator);
    }

    @Test
    public void userPostsInOwnTopic_ifSubscribedToBranchOnly_shouldNotReceiveNotifications() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));
        Topics.unsubscribe(topic);
        Branches.subscribe(topic.getBranch());
        Topics.postAnswer(topic);
        Branches.unsubscribeIgnoringFail(topic.getBranch());

        Notifications.assertNotificationsNotSent(topic, topicCreator);
    }

    // edit subscriber's post

    @Test
    public void subsPostEditedByOther_ifSubscribedToBothBranchAndTopic_shouldNotReceiveNotifications() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));

        User subscribedUser = Users.signUpAndSignIn();
        Post postToEdit = Topics.postAnswer(topic);
        Topics.subscribe(topic);
        Branches.subscribe(topic.getBranch());

        Users.logOutAndSignIn(topicCreator);
        Topics.editPost(topic, postToEdit);

        Users.logOutAndSignIn(subscribedUser);
        Branches.unsubscribeIgnoringFail(topic.getBranch());

        Notifications.assertNotificationsNotSent(topic, subscribedUser);
    }

    @Test
    public void subsPostEditedByOther_ifSubscribedToBranchOnly_shouldNotReceiveNotifications() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));

        User subscribedUser = Users.signUpAndSignIn();
        Post postToEdit = Topics.postAnswer(topic);
        Topics.unsubscribe(topic);
        Branches.subscribe(topic.getBranch());

        Users.logOutAndSignIn(topicCreator);
        Topics.editPost(topic, postToEdit);

        Users.logOutAndSignIn(subscribedUser);
        Branches.unsubscribeIgnoringFail(topic.getBranch());

        Notifications.assertNotificationsNotSent(topic, subscribedUser);
    }

    @Test
    public void subsPostEditedByOther_ifSubscribedToTopicOnly_shouldNotReceiveNotifications() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));

        User subscribedUser = Users.signUpAndSignIn();
        Post postToEdit = Topics.postAnswer(topic);
        Topics.subscribe(topic);
        Branches.unsubscribe(topic.getBranch());

        Users.logOutAndSignIn(topicCreator);
        Topics.editPost(topic, postToEdit);

        Notifications.assertNotificationsNotSent(topic, subscribedUser);
    }

    @Test
    public void ownPostEditedBySubscriber_ifHeSubscribedToBothBranchAndTopic_shouldNotReceiveNotifications() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));

        Topics.unsubscribe(topic);
        Post postToEdit = Topics.postAnswer(topic);

        Topics.subscribe(topic);
        Branches.subscribe(topic.getBranch());

        Topics.editPost(topic, postToEdit);
        Branches.unsubscribeIgnoringFail(topic.getBranch());

        Notifications.assertNotificationsNotSent(topic, topicCreator);
    }

    // delete post


    // delete topic

    @Test (enabled = false) // bug: receives branch notification
    public void topicDeletedByOther_ifSubscribedToBothBranchAndTopic_shouldReceiveTopicNotificationNotBranch() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));

        Topics.subscribe(topic);
        Branches.subscribe(topic.getBranch());

        Users.signUpAndSignIn();
        Topics.deleteTopic(topic);

        Users.logOutAndSignIn(topicCreator);
        Branches.unsubscribeIgnoringFail(topic.getBranch());

        Notifications.assertNotificationOnTopicSentBranchNotSent(topic, topicCreator);
    }

    @Test
    public void topicDeletedByOther_ifSubscribedToBranchOnly_shouldReceiveBranchNotificationNotTopic() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));

        Topics.unsubscribe(topic);
        Branches.subscribe(topic.getBranch());

        Users.signUpAndSignIn();
        Topics.deleteTopic(topic);

        Users.logOutAndSignIn(topicCreator);
        Branches.unsubscribeIgnoringFail(topic.getBranch());

        Notifications.assertNotificationOnTopicNotSentBranchSent(topic, topicCreator);
    }

    @Test (enabled = false) // bug: receives branch notification
    public void topicDeletedByOther_ifSubscribedToTopicOnly_shouldReceiveTopicNotificationNotBranch() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));

        Topics.subscribe(topic);
        Branches.unsubscribe(topic.getBranch());

        Users.signUpAndSignIn();
        Topics.deleteTopic(topic);

        Notifications.assertNotificationOnTopicSentBranchNotSent(topic, topicCreator);
    }

    @Test
    public void deletingOwnTopic_ifSubscribedToBothBranchAndTopic_shouldNotReceiveNotifications() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));

        Topics.subscribe(topic);
        Branches.subscribe(topic.getBranch());

        Topics.deleteTopic(topic);
        Branches.unsubscribeIgnoringFail(topic.getBranch());

        Notifications.assertNotificationsNotSent(topic, topicCreator);
    }

    // move topic

    @Test (enabled = false) // bug: receives branch notification
    public void topicMovedByOther_ifSubscribedToBothBranchAndTopic_shouldReceiveTopicNotificationNotBranch() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));

        Topics.subscribe(topic);
        Branches.subscribe(topic.getBranch());

        Users.signUpAndSignIn();
        Topics.moveTopic(topic, BRANCH_NAME_TO_MOVE_TOPIC_IN);

        Users.logOutAndSignIn(topicCreator);
        Branches.unsubscribeIgnoringFail(new Branch(NOTIFICATION_BRANCH));

        Notifications.assertNotificationOnTopicSentBranchNotSent(topic, topicCreator);
    }

    @Test
    public void topicMovedByOther_ifSubscribedToBranchOnly_shouldNotReceiveNotifications() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));

        Topics.unsubscribe(topic);
        Branches.subscribe(topic.getBranch());

        Users.signUpAndSignIn();
        Topics.moveTopic(topic, BRANCH_NAME_TO_MOVE_TOPIC_IN);

        Users.logOutAndSignIn(topicCreator);
        Branches.unsubscribeIgnoringFail(new Branch(NOTIFICATION_BRANCH));

        Notifications.assertNotificationsNotSent(topic, topicCreator);
    }

    @Test (enabled = false) // bug: receives branch notification
    public void topicMovedByOther_ifSubscribedToTopicOnly_shouldReceiveTopicNotificationNotBranch() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));

        Topics.subscribe(topic);
        Branches.unsubscribe(topic.getBranch());

        Users.signUpAndSignIn();

        Topics.moveTopic(topic, BRANCH_NAME_TO_MOVE_TOPIC_IN);

        Notifications.assertNotificationOnTopicSentBranchNotSent(topic, topicCreator);
    }

    @Test
    public void ownTopicMovedByUser_ifHeSubscribedToBothBranchAndTopic_shouldNotReceiveNotifications() throws Exception {
        User topicCreator = Users.signUpAndSignIn();
        Topic topic = Topics.createTopic(new Topic().withBranch(NOTIFICATION_BRANCH));

        Topics.subscribe(topic);
        Branches.subscribe(topic.getBranch());

        Topics.moveTopic(topic, BRANCH_NAME_TO_MOVE_TOPIC_IN);
        Branches.unsubscribeIgnoringFail(new Branch(NOTIFICATION_BRANCH));

        Notifications.assertNotificationsNotSent(topic, topicCreator);
    }
}