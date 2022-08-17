package edu.uchicago.adamzhang22.quotes.repository;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import edu.uchicago.adamzhang22.quotes.cache.Cache;
import edu.uchicago.adamzhang22.quotes.models.Fav;
import edu.uchicago.adamzhang22.quotes.models.FavList;
import edu.uchicago.adamzhang22.quotes.service.AsyncRestCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Repository
public class FavRepository {
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    //make sure to add your full container-url-connection-string to the IntelliJ confuguration
    //QUARKUS_BASE_URL :: https://container-service-22.9r4o895c5nvr8.us-east-2.cs.amazonlightsail.com/
    //If you are deploying to beanstalk, place in software || environmental properties.
    @Value("${QUARKUS_BASE_URL:#{'https://container-service-1.4cb8s827ojt62.us-west-2.cs.amazonlightsail.com/'}}")
    private String baseUrl;

    public void getFavoritesPaged(AsyncRestCallback<FavList> callback, int page) {

        String email = Cache.getInstance().getEmail();

        String raw = baseUrl + "favs/paged/" + email + "/%d";
        String formatted = String.format(raw, page);
        WebClient.RequestHeadersSpec<?> spec = WebClient.create().get()
                .uri(formatted);

        spec.retrieve().bodyToMono(new ParameterizedTypeReference<FavList>() {
        }).publishOn(Schedulers.fromExecutor(executorService)).subscribe(results -> callback.operationFinished(results));
    }


    public void deleteFavoriteById(AsyncRestCallback<Fav> callback, String id){

        String raw = baseUrl + "favs/" + id;
        WebClient.RequestHeadersSpec<?> spec = WebClient.create().delete().uri(raw);

        spec.retrieve().bodyToMono(Fav.class).publishOn(Schedulers.fromExecutor(executorService)).subscribe(result -> {
            final Fav movie = result;
            if (null == movie) return;
            callback.operationFinished(movie);
        });
    }

    public void editFavorite(UI ui, AsyncRestCallback<Fav> callback, Fav favoriteAdd) {

        String formatted = baseUrl + "/favs/" + favoriteAdd.getId();
        Mono<Fav> mono = WebClient.create().put()
                .uri(formatted)
                .body(Mono.just(favoriteAdd), Fav.class)
                .retrieve()
                .bodyToMono(Fav.class);

        mono
                .doOnError(throwable -> {
                    String message = "";
                    switch (((WebClientResponseException.UnsupportedMediaType) throwable).getStatusCode().value()){
                        default:
                            message = "There was an error: " + throwable.getMessage();
                    }
                    final String finalMessage = message;
                    ui.access(() -> {
                        Notification.show(finalMessage , 2000,
                                Notification.Position.BOTTOM_CENTER);
                    });
                })
                .publishOn(Schedulers.fromExecutor(executorService))
                .subscribe(results -> callback.operationFinished(results));
    }


    public void addFavorite(UI ui, AsyncRestCallback<Fav> callback, Fav favoriteAdd) {

        String formatted = baseUrl + "/favs";
        Mono<Fav> mono = WebClient.create().post()
                .uri(formatted)
                .body(Mono.just(favoriteAdd), Fav.class)
                .retrieve()
                .bodyToMono(Fav.class);

        mono
                .doOnError(throwable -> {
                    String message = "";
                    switch (((WebClientResponseException.UnsupportedMediaType) throwable).getStatusCode().value()){
                        case 415:
                            message = "This quote is already in your favorites.";
                            break;
                        default:
                            message = "There was an error: " + throwable.getMessage();

                    }
                    final String finalMessage = message;
                    ui.access(() -> {
                        Notification.show(finalMessage , 2000,
                                Notification.Position.BOTTOM_CENTER);
                        ui.navigate("favorites");
                    });

                })
                .publishOn(Schedulers.fromExecutor(executorService))
                .subscribe(results -> callback.operationFinished(results));
    }



}
