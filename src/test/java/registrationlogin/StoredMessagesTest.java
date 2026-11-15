package registrationlogin;

import java.io.File;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Part 3 unit tests: the five test messages from the brief are loaded through the
// real SentMessage method, so the arrays are populated by the code, not hard-coded.
public class StoredMessagesTest {

    private Message message1, message2, message3, message4, message5;

    @BeforeEach
    public void setUp() {
        // use a separate file so the real messages.json is never touched
        Message.setStorageFile("test-messages.json");
        Message.clearArrays();
        new File("test-messages.json").delete();

        message1 = new Message("0000000001", 1, "+27834557896", "Did you get the cake?");
        message2 = new Message("0000000002", 2, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");
        message3 = new Message("0000000003", 3, "+27834484567", "Yohoooo, I am at your gate.");
        // test message 4 uses a number without an international code, as in the brief
        message4 = new Message("0838884567", 4, "0838884567", "It is dinner time!");
        message5 = new Message("0000000005", 5, "+27838884567", "Ok, I am leaving without you.");

        message1.SentMessage("send");
        message2.SentMessage("store");
        message3.SentMessage("discard");
        message4.SentMessage("send");
        message5.SentMessage("store");
    }

    @AfterEach
    public void tearDown() {
        new File("test-messages.json").delete();
        Message.setStorageFile("messages.json");
        Message.clearArrays();
    }

    @Test
    public void sentMessagesArrayCorrectlyPopulated() {
        List<String> expected = List.of("Did you get the cake?", "It is dinner time!");
        assertEquals(expected, Message.getSentMessages());
    }

    @Test
    public void displayTheLongestMessage() {
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                Message.longestStoredMessage());
    }

    @Test
    public void searchForMessageID() {
        assertEquals("It is dinner time!", Message.searchByMessageID("0838884567"));
    }

    @Test
    public void searchForMessageIDNotFound() {
        assertEquals("Message ID not found.", Message.searchByMessageID("9999999999"));
    }

    @Test
    public void searchAllMessagesForARecipient() {
        List<String> expected = List.of(
                "Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you.");
        assertEquals(expected, Message.searchByRecipient("+27838884567"));
    }

    @Test
    public void deleteAMessageUsingTheMessageHash() {
        String expected = "Message: \"" + message2.getMessage() + "\" successfully deleted.";
        assertEquals(expected, Message.deleteByHash(message2.getMessageHash()));
    }

    @Test
    public void deletedMessageIsRemovedFromTheArrays() {
        Message.deleteByHash(message2.getMessageHash());
        assertEquals(List.of("Ok, I am leaving without you."), Message.getStoredMessages());
        assertEquals(4, Message.getMessageHashes().size());
    }

    @Test
    public void displayReportOfSentMessages() {
        String expected = reportEntry(message1) + reportEntry(message4);
        assertEquals(expected, Message.displayReport("Sent"));
    }

    // builds one report entry in the same layout as displayReport
    private String reportEntry(Message m) {
        return "Message ID: " + m.getMessageID()
                + "\nMessage Hash: " + m.getMessageHash()
                + "\nRecipient: " + m.getRecipient()
                + "\nMessage: " + m.getMessage() + "\n\n";
    }
}