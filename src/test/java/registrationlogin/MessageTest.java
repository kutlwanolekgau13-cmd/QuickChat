
package registrationlogin;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    public void testMessageIDIsValid() {
        Message message = new Message(
                "1234567890", 1, "+271234567", "Hi Mike tonight");
        assertTrue(message.checkMessageID());
    }

    @Test
    public void testMessageIDTooLong() {
        Message message = new Message(
                "12345678901", 1, "+271234567", "Hi Mike tonight");
        assertFalse(message.checkMessageID());
    }

    @Test
    public void testMessageLengthWithinLimit() {
        Message message = new Message(
                "1234567890", 1, "+271234567", "Hi Mike tonight");
        assertEquals("Message ready to send.",
                message.checkMessageLength());
    }

    @Test
    public void testMessageHash() {
        Message message = new Message(
                "0012345678", 1, "+271234567",
                "Hi Mike, can you join us for dinner tonight?");
        assertEquals("00:1:HITONIGHT", message.createMessageHash());
    }

    @Test
    public void testSendMessage() {
        Message message = new Message(
                "1234567890", 1, "+271234567", "Hi Mike tonight");
        assertEquals("Message successfully sent.",
                message.SentMessage("send"));
    }

    @Test
    public void testDiscardMessage() {
        Message message = new Message(
                "1234567890", 1, "+271234567", "Hi Mike tonight");
        assertEquals("Press 0 to delete the message.",
                message.SentMessage("discard"));
    }
}
