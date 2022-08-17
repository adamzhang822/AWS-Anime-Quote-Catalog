package edu.uchicago.adamzhang22.quarkus.services;

import edu.uchicago.adamzhang22.quarkus.models.Fav;
import edu.uchicago.adamzhang22.quarkus.repositories.FavMongodbRepo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;


import java.util.List;

@ApplicationScoped
public class FavsService {

    @Inject
    FavMongodbRepo favsRepository;

    public Fav add(Fav favoriteItem) {
        // Check for duplicates
        Fav dup = favsRepository.get(favoriteItem.getUserEmail(), favoriteItem.getQuote(), favoriteItem.getCharacter(), favoriteItem.getAnime());
        System.out.println(dup);
        if (dup != null) {
            throw new NotSupportedException("The quote is already in favorites!");
        }
        return favsRepository.add(favoriteItem);
    }

    public Fav get(String id) {
        Fav item = favsRepository.get(id);
        if (null == item){
            throw new NotFoundException("The FavoriteItem with id " + id + " was not found");
        }
        return item;
    }

    public Fav delete(String id) {
        Fav item = favsRepository.get(id);
        if (null == item){
            throw new NotFoundException("The FavoriteItem with id " + id + " was not found");
        }
        return favsRepository.delete(id);
    }

    public Fav update(String id, Fav fav) {
        Fav item = favsRepository.get(id);
        if (null == item) {
            throw new NotFoundException("The FavoriteItem with id " + id + " was not found");
        }
        return favsRepository.update(id, fav);

    }

    public List<Fav> paged(String userEmail, int page){
        return favsRepository.paged(userEmail, page);
    }

}