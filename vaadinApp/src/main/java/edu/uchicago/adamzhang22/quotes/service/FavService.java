package edu.uchicago.adamzhang22.quotes.service;

import com.vaadin.flow.component.UI;
import edu.uchicago.adamzhang22.quotes.models.Fav;
import edu.uchicago.adamzhang22.quotes.models.FavList;
import edu.uchicago.adamzhang22.quotes.repository.FavRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Service
public class FavService extends ResponseEntityExceptionHandler {
    private FavRepository favoritesRepository;


    public FavService(FavRepository favoritesRepository) {
        this.favoritesRepository = favoritesRepository;
    }


    public void getFavoritesPaged(AsyncRestCallback<FavList> callback,
                                  int page) {
        favoritesRepository.getFavoritesPaged(callback, page);
    }


    public void addFavorite(UI ui, AsyncRestCallback<Fav> callback,
                            Fav favorite)  {
        favoritesRepository.addFavorite(ui, callback, favorite);
    }

    public void deleteFavoriteById(AsyncRestCallback<Fav> callback,
                                   String id) {
        favoritesRepository.deleteFavoriteById(callback, id);
    }

    public void editComment(UI ui, AsyncRestCallback<Fav> callback,
                                   Fav favorite) {
        favoritesRepository.editFavorite(ui, callback, favorite);
    }



}