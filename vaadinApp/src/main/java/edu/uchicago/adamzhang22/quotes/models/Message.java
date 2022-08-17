package edu.uchicago.adamzhang22.quotes.models;

public class Message {

    private String subject;
    private String body;
    private String email;

    public Message(String subject, String body, String email) {
        this.subject = subject;
        this.body = body;
        this.email = email;
    }

    public Message() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Message{" +
                "subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
