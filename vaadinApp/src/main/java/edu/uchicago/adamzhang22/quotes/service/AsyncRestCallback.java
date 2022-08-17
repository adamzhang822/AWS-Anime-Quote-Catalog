package edu.uchicago.adamzhang22.quotes.service;

@FunctionalInterface
public interface AsyncRestCallback<T> {
    void operationFinished(T results);
}
