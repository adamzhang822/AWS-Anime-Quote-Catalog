package edu.uchicago.adamzhang22.quotes.service;



import edu.uchicago.adamzhang22.quotes.models.Quote;
import edu.uchicago.adamzhang22.quotes.repository.QuoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class QuoteService {

    public static final int MAX_RESULTS = 10;
    private QuoteRepository quoteRepository;

    public QuoteService(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    //READ paged
    public void getQuotes(AsyncRestCallback<List<Quote>> callback, String searchTerm, int page) {

        System.out.println("fetching quotes page: -> " + page);

        quoteRepository.getQuotes(callback, searchTerm, page);


    }

}
