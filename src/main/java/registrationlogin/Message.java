
package registrationlogin;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Message {

    private String messageID;
    private int numMessagesSent;
    private String recipient;
    private String message;
    private String messageHash;

    private static final List<Message> messages = new ArrayList<>();
    private static int totalMessagesSent = 0;
// Message class for QuickChat: holds one message and checks its ID, recipient, length and hash
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
                return "Message successfully sent.";

            case "discard":
                return "Press 0 to delete the message.";

            case "store":
                return storeMessage();

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
}
