package edu.uchicago.adamzhang22.quotes.repository;


import edu.uchicago.adamzhang22.quotes.models.Quote;
import edu.uchicago.adamzhang22.quotes.models.QuoteList;
import edu.uchicago.adamzhang22.quotes.service.AsyncRestCallback;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Repository
public class QuoteRepository {



    //READ paged
    public void getQuotes(AsyncRestCallback<List<Quote>> callback, String searchTerm, int page) {

        String raw = "https://animechan.vercel.app/api/quotes/anime?title=%s&page=%d";
        String formatted = String.format(raw, searchTerm, page);
        WebClient.RequestHeadersSpec<?> spec = WebClient.create().get().uri(formatted);

        spec.retrieve().toEntity(QuoteList.class).subscribe(result -> {

            final List<Quote>  quoteResponse = result.getBody();

            if (null == quoteResponse || quoteResponse.size() == 0) return;
            callback.operationFinished(quoteResponse);

        });

    }
}
