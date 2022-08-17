package edu.uchicago.adamzhang22.quarkus.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Fav {

    private String id;
    private String userEmail;
    private String anime;
    private String character;
    private String quote;
    private String comment;

    public Fav(String id, String email, String anime, String character, String quote, String comment) {
        this.id = id;
        this.userEmail = email;
        this.anime = anime;
        this.character = character;
        this.quote = quote;
        this.comment = comment;
    }

    public Fav(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAnime() {
        return anime;
    }

    public void setAnime(String anime) {
        this.anime = anime;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Fav{" +
                "id='" + id + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", anime='" + anime + '\'' +
                ", character='" + character + '\'' +
                ", quote='" + quote + '\'' +
                '}';
    }
}

