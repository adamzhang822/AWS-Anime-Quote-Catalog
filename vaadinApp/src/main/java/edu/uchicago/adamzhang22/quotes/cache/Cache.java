package edu.uchicago.adamzhang22.quotes.cache;

//import com.us.broadreach.stack.models.FavoriteItem;

import edu.uchicago.adamzhang22.quotes.models.Fav;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Cache {

    private String keyword = "";
    private List<Fav> items = new ArrayList<>();
    private Fav detailItem;
    private int offset;
    private boolean favMode;
    private String email = "default@default.com";

    private static Cache cache;

    private Cache() {
    }

    public static Cache getInstance() {
        if (null == cache) {
            cache = new Cache();
        }
        return cache;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;

    }


    public Stream<Fav> streamItems() {
        return items.stream();
    }

    public void addItems(List<Fav> items) {
        this.items.addAll(items);
    }

    public void clearItems() {
        this.items.clear();
    }

    public int itemsSize() {
        return items.size();
    }

    public Fav getDetailItem() {
        return detailItem;
    }

    public void setDetailItem(Fav detailItem) {
        this.detailItem = detailItem;
    }

    public boolean isFavMode() {
        return favMode;
    }

    public void setFavMode(boolean favMode) {
        this.favMode = favMode;
    }


    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}