package registrationlogin;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;

public class Message {

    private String messageID;
    private int numMessagesSent;
    private String recipient;
    private String message;
    private String messageHash;

    private static final List<Message> messages = new ArrayList<>();
    private static int totalMessagesSent = 0;
// Message class for QuickChat: holds one message and checks its ID, recipient, length and hash
    // Arrays required for Part 3 (filled while the app runs, nothing hard-coded)
    private static final List<String> sentMessages = new ArrayList<>();
    private static final List<String> disregardedMessages = new ArrayList<>();
    private static final List<String> storedMessages = new ArrayList<>();

    // Parallel arrays: index 0 in each list belongs to the same message
    private static final List<String> messageIDs = new ArrayList<>();
    private static final List<String> messageHashes = new ArrayList<>();
    private static final List<String> recipients = new ArrayList<>();
    private static final List<String> messageTexts = new ArrayList<>();
    private static final List<String> messageFlags = new ArrayList<>();

    public Message(String messageID, int numMessagesSent,
            String recipient, String message) {
        this.messageID = messageID;
        this.numMessagesSent = numMessagesSent;
        this.recipient = recipient;
        this.message = message;
        this.messageHash = createMessageHash();
    }

    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    public String checkRecipientCell() {
        boolean valid = false;

        // must start with + and be 12 characters or less
        if (recipient != null && recipient.startsWith("+") && recipient.length() <= 12) {
            valid = true;

            // everything after the + must be a digit
            for (int i = 1; i < recipient.length(); i++) {
                if (!Character.isDigit(recipient.charAt(i))) {
                    valid = false;
                }
            }
        }

        if (valid) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    public String createMessageHash() {
        if (messageID == null || messageID.length() < 2
                || message == null || message.trim().isEmpty()) {
            return "";
        }

        String[] words = message.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "");
        String lastWord = words[words.length - 1]
                .replaceAll("[^a-zA-Z0-9]", "");

        return (messageID.substring(0, 2) + ":"
                + numMessagesSent + ":"
                + firstWord + lastWord).toUpperCase();
    }

    public String checkMessageLength() {
        if (message == null) {
            return "Message is empty.";
        }

        if (message.length() <= 250) {
            return "Message ready to send.";
        }

        return "Message exceeds 250 characters by "
                + (message.length() - 250)
                + "; please reduce the size.";
    }

    public String SentMessage(String choice) {
        if (choice == null) {
            return "Invalid option.";
        }

        switch (choice.toLowerCase()) {
            case "send":
                messages.add(this);
                totalMessagesSent++;
                recordMessage(sentMessages, "Sent");
                return "Message successfully sent.";

            case "discard":
                recordMessage(disregardedMessages, "Disregard");
                return "Press 0 to delete the message.";

            case "store": {
                String result = storeMessage();
                if (result.equals("Message successfully stored.")) {
                    recordMessage(storedMessages, "Stored");
                }
                return result;
            }

            default:
                return "Invalid option.";
        }
    }

    public String printMessages() {
        StringBuilder result = new StringBuilder();

        for (Message msg : messages) {
            result.append("Message ID: ")
                    .append(msg.messageID)
                    .append("\nMessage Hash: ")
                    .append(msg.messageHash)
                    .append("\nRecipient: ")
                    .append(msg.recipient)
                    .append("\nMessage: ")
                    .append(msg.message)
                    .append("\n\n");
        }

        return result.toString();
    }

    public int returnTotalMessages() {
        return totalMessagesSent;
    }

    public String storeMessage() {
        List<String> records = new ArrayList<>();

        java.io.File file = new java.io.File("messages.json");

        // Read existing JSON records.
        if (file.exists()) {
            try (java.util.Scanner reader = new java.util.Scanner(file)) {
                String content = reader.useDelimiter("\\Z").hasNext()
                        ? reader.next().trim() : "";

                if (!content.isEmpty() && content.startsWith("[")) {
                    content = content.substring(1, content.length() - 1).trim();

                    if (!content.isEmpty()) {
                        records.add(content);
                    }
                } else if (!content.isEmpty()) {
                    // Preserve the existing record as a JSON object.
                    for (String line : content.split("\\R")) {
                        if (!line.trim().isEmpty()) {
                            records.add(line.trim().replaceAll(",$", ""));
                        }
                    }
                }
            } catch (IOException e) {
                return "Error reading stored messages: " + e.getMessage();
            }
        }

        String json = "{"
                + "\"messageID\":\"" + escapeJson(messageID) + "\","
                + "\"messageHash\":\"" + escapeJson(messageHash) + "\","
                + "\"recipient\":\"" + escapeJson(recipient) + "\","
                + "\"message\":\"" + escapeJson(message) + "\""
                + "}";

        records.add(json);

        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write("[\n");

            for (int i = 0; i < records.size(); i++) {
                writer.write("  " + records.get(i));

                if (i < records.size() - 1) {
                    writer.write(",");
                }

                writer.write("\n");
            }

            writer.write("]\n");
            return "Message successfully stored.";
        } catch (IOException e) {
            return "Error storing message: " + e.getMessage();
        }
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public String getMessageID() {
        return messageID;
    }

    public int getNumMessagesSent() {
        return numMessagesSent;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageHash() {
        return messageHash;
    }

    // Adds this message to its category list and to every parallel list at once,
    // so the indexes always stay lined up
    private void recordMessage(List<String> categoryList, String flag) {
        categoryList.add(message);
        messageIDs.add(messageID);
        messageHashes.add(messageHash);
        recipients.add(recipient);
        messageTexts.add(message);
        messageFlags.add(flag);
    }

    public static List<String> getSentMessages() {
        return sentMessages;
    }

    public static List<String> getDisregardedMessages() {
        return disregardedMessages;
    }

    public static List<String> getStoredMessages() {
        return storedMessages;
    }

    public static List<String> getMessageIDs() {
        return messageIDs;
    }

    public static List<String> getMessageHashes() {
        return messageHashes;
    }

    public static List<String> getRecipients() {
        return recipients;
    }

    public static List<String> getMessageTexts() {
        return messageTexts;
    }

    // Empties every array; used by the unit tests so each test starts clean
    public static void clearArrays() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageIDs.clear();
        messageHashes.clear();
        recipients.clear();
        messageTexts.clear();
        messageFlags.clear();
    }

    // Reads messages.json into the stored array and the parallel arrays.
    // Source: Gson library, https://github.com/google/gson
    // (replace this line with the exact page or tutorial you used)
    public static String loadStoredMessages() {
        File file = new File("messages.json");
        if (!file.exists()) {
            return "No stored messages found.";
        }

        try (FileReader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<StoredRecord>>() {
            }.getType();
            List<StoredRecord> records = new Gson().fromJson(reader, listType);

            if (records == null || records.isEmpty()) {
                return "No stored messages found.";
            }

            for (StoredRecord record : records) {
                storedMessages.add(record.message);
                messageIDs.add(record.messageID);
                messageHashes.add(record.messageHash);
                recipients.add(record.recipient);
                messageTexts.add(record.message);
                messageFlags.add("Stored");
            }
            return records.size() + " stored message(s) loaded.";
        } catch (Exception e) {
            return "Error reading stored messages: " + e.getMessage();
        }
    }

    // Shape of one record in messages.json (the field names match the JSON keys)
    private static class StoredRecord {

        String messageID;
        String messageHash;
        String recipient;
        String message;
    }

    // a. Sender and recipient of all stored messages
    public static String displaySenderAndRecipient(String sender) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < messageIDs.size(); i++) {
            if (messageFlags.get(i).equals("Stored")) {
                result.append("Sender: ").append(sender)
                        .append(" | Recipient: ").append(recipients.get(i)).append("\n");
            }
        }
        if (result.length() == 0) {
            return "No stored messages.";
        }
        return result.toString();
    }

    // b. Longest stored message
    public static String longestStoredMessage() {
        String longest = "";
        for (String text : storedMessages) {
            if (text.length() > longest.length()) {
                longest = text;
            }
        }
        if (longest.isEmpty()) {
            return "No stored messages.";
        }
        return longest;
    }

    // c. Search by message ID: returns the message text
    public static String searchByMessageID(String id) {
        int index = messageIDs.indexOf(id);
        if (index == -1) {
            return "Message ID not found.";
        }
        return messageTexts.get(index);
    }

    // c. The recipient that goes with that message ID
    public static String getRecipientByMessageID(String id) {
        int index = messageIDs.indexOf(id);
        if (index == -1) {
            return "Message ID not found.";
        }
        return recipients.get(index);
    }

    // d. All sent or stored messages for one recipient (disregarded ones are left out)
    public static List<String> searchByRecipient(String recipient) {
        List<String> found = new ArrayList<>();
        for (int i = 0; i < recipients.size(); i++) {
            if (recipients.get(i).equals(recipient)
                    && !messageFlags.get(i).equals("Disregard")) {
                found.add(messageTexts.get(i));
            }
        }
        return found;
    }

    // e. Delete a message using its hash: removes the same index from every array
    public static String deleteByHash(String hash) {
        int index = messageHashes.indexOf(hash);
        if (index == -1) {
            return "Message hash not found.";
        }

        String text = messageTexts.get(index);
        String flag = messageFlags.get(index);

        // remove from the category list the message belongs to
        if (flag.equals("Sent")) {
            sentMessages.remove(text);
            messages.removeIf(m -> m.messageHash.equals(hash));
        } else if (flag.equals("Stored")) {
            storedMessages.remove(text);
        } else {
            disregardedMessages.remove(text);
        }

        // remove from every parallel array so the indexes stay lined up
        messageIDs.remove(index);
        messageHashes.remove(index);
        recipients.remove(index);
        messageTexts.remove(index);
        messageFlags.remove(index);

        // a deleted stored message must also leave messages.json
        if (flag.equals("Stored")) {
            rewriteStoredFile();
        }

        return "Message: \"" + text + "\" successfully deleted.";
    }

    // Rewrites messages.json from the stored entries still in the arrays.
    // Source: Gson library, https://github.com/google/gson
    private static void rewriteStoredFile() {
        List<StoredRecord> records = new ArrayList<>();
        for (int i = 0; i < messageIDs.size(); i++) {
            if (messageFlags.get(i).equals("Stored")) {
                StoredRecord record = new StoredRecord();
                record.messageID = messageIDs.get(i);
                record.messageHash = messageHashes.get(i);
                record.recipient = recipients.get(i);
                record.message = messageTexts.get(i);
                records.add(record);
            }
        }

        try (FileWriter writer = new FileWriter("messages.json", false)) {
            new Gson().toJson(records, writer);
        } catch (IOException e) {
            System.out.println("Error updating stored messages: " + e.getMessage());
        }
    }

    // f. Report with the full details of every message with the given flag
    //    ("Stored" for the menu report, "Sent" for the sent-messages report)
    public static String displayReport(String flag) {
        StringBuilder report = new StringBuilder();
        for (int i = 0; i < messageIDs.size(); i++) {
            if (messageFlags.get(i).equals(flag)) {
                report.append("Message ID: ").append(messageIDs.get(i))
                        .append("\nMessage Hash: ").append(messageHashes.get(i))
                        .append("\nRecipient: ").append(recipients.get(i))
                        .append("\nMessage: ").append(messageTexts.get(i))
                        .append("\n\n");
            }
        }
        if (report.length() == 0) {
            return "No " + flag.toLowerCase() + " messages.";
        }
        return report.toString();
    }
}
