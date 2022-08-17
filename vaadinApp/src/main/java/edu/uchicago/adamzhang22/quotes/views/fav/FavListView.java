package edu.uchicago.adamzhang22.quotes.views.fav;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import edu.uchicago.adamzhang22.quotes.cache.Cache;
import edu.uchicago.adamzhang22.quotes.models.Fav;
import edu.uchicago.adamzhang22.quotes.models.FavList;
import edu.uchicago.adamzhang22.quotes.models.Quote;
import edu.uchicago.adamzhang22.quotes.service.AsyncRestCallback;
import edu.uchicago.adamzhang22.quotes.service.FavService;
import edu.uchicago.adamzhang22.quotes.service.QuoteService;
import edu.uchicago.adamzhang22.quotes.views.MainLayout;
import elemental.json.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Fav List")
@Route(value = "favorites", layout = MainLayout.class)
@RouteAlias(value = "fav", layout = MainLayout.class)
public class FavListView extends Div implements AfterNavigationObserver {

    private Notification loading = new Notification("loading...", 500, Notification.Position.TOP_CENTER);
    private Notification done = new Notification("done fetching", 500, Notification.Position.BOTTOM_END);

    private FavService favService;
    private Grid<Fav> grid = new Grid<>();
    private int page= 1;
    private boolean isLoading = false;

    private List<Fav> items = new ArrayList<>();

    public FavListView(FavService favService) {
        this.favService = favService;
        getFavs();
        addClassName("card-list-view");
        setSizeFull();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(item -> createCard(item));
        grid.addItemClickListener(new ComponentEventListener<ItemClickEvent<Fav>>() {
            @Override
            public void onComponentEvent(ItemClickEvent<Fav> itemItemClickEvent) {
                System.out.println(itemItemClickEvent.getItem());
                favService.deleteFavoriteById(m -> {
                    getUI().get().access(() -> {
                        page = 1;
                        getFavs();
                    }
                    );}, itemItemClickEvent.getItem().getId());
            }
        });

        add(withClientsideScrollListener(grid));
    }

    private void getFavs() {
        isLoading = true;
        loading.open();
        AsyncRestCallback<FavList> quoteListCallback = quoteResponse -> {

            //we must fire this callback on the UI thread and that is why we use getUi().get().acccess(() ->
            getUI().get().access(() -> {

                //this is the callback result, so volumesResponse is the volumesResponse returned from
                //      void operationFinished(T results);
                if (page == 1) {
                    items = quoteResponse;
                } else {
                    items.addAll(quoteResponse);
                }
                grid.setItems(items);
                page += 1;
                isLoading = false;
                done.open();
                //https://vaadin.com/docs/v14/flow/advanced/tutorial-push-access
                //we need to notify the browser when we are done. Note that the parent-view MainView is annotated with
                //the @Push annotation, which will force the views to refresh themselves, including the grid.
                getUI().get().push();

            });
        };

        //the callback is expressed as a lambda. We hae access to the members of this class, such as grid, items, etc.
        favService.getFavoritesPaged(quoteListCallback, page);

    }

    //we wrap the Vaadin grid with this so that it emits this event at every gridScroll.
    private Grid<Fav> withClientsideScrollListener(Grid<Fav> grid) {
        grid.getElement().executeJs(
                "this.$.scroller.addEventListener('scroll', (scrollEvent) => " +
                        "{requestAnimationFrame(" +
                        "() => $0.$server.onGridScroll({sh: this.$.table.scrollHeight, " +
                        "ch: this.$.table.clientHeight, " +
                        "st: this.$.table.scrollTop}))},true)",
                getElement());
        return grid;
    }

    //this is called by the javaScript above on the server, which forces the grid fetch records and scroll back to 1/2
    @ClientCallable
    public void onGridScroll(JsonObject scrollEvent) {
        int scrollHeight = (int) scrollEvent.getNumber("sh");
        int clientHeight = (int) scrollEvent.getNumber("ch");
        int scrollTop = (int) scrollEvent.getNumber("st");

        double percentage = (double) scrollTop / (scrollHeight - clientHeight);
        //reached the absolute bottom of the scroll
        if (percentage == 1.0) {

            if (!isLoading) {
                getFavs();
            }
            grid.scrollToIndex(items.size() / 2);

        }

    }

    private HorizontalLayout createCard(Fav fav) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        VerticalLayout description = new VerticalLayout();
        description.addClassName("description");
        description.setSpacing(false);
        description.setPadding(false);

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setSpacing(false);
        header.getThemeList().add("spacing-s");

        Span anime = new Span("Anime: " + getAnime(fav));
        anime.addClassName("name");

        Span character = new Span("Character: "  + getCharacter(fav));
        character.addClassName("name");

        Span quoteContent = new Span("Quote: " + getQuote(fav));
        quoteContent.addClassName("test");

        Span commentContent = new Span("Comment: " + getComment(fav));
        commentContent.addClassName("test");

        HorizontalLayout editForm = new HorizontalLayout();
        TextField comment = new TextField("Edit comment");
        Button edit = new Button("Update comment");
        edit.addClickListener(e -> {
            if (comment.isEmpty()) {
                Notification notification = new Notification(
                        "Comment can't be empty!", 50000, Notification.Position.BOTTOM_CENTER);
                notification.open();
            } else {
                Fav newFav = new Fav();
                newFav.setId(fav.getId());
                newFav.setUserEmail((fav.getUserEmail()));
                newFav.setCharacter(fav.getCharacter());
                newFav.setAnime(fav.getAnime());
                newFav.setQuote(fav.getQuote());
                newFav.setComment(comment.getValue());
                favService.editComment(UI.getCurrent(), m -> {
                    getUI().get().access(() -> {
                        Notification.show("Edited comments!" , 2000,
                                Notification.Position.BOTTOM_CENTER);
                        comment.setValue("");
                        page = 1;
                        getFavs();
                    });
                }, newFav);
            }
        });

        header.add(anime, character);
        description.add(header, quoteContent, commentContent, comment, edit);
        card.add(description);

        Span prompt = new Span("Click to delete from favorite!");
        prompt.addClassName("name");
        card.add(prompt);
        return card;
    }

    private String getAnime(Fav quote) {
        if (null == quote.getAnime()) {
            return "";
        }
        return quote.getAnime();
    }

    private String getCharacter(Fav quote) {
        if (null == quote.getCharacter()) {
            return "";
        }
        return quote.getCharacter();
    }

    private String getQuote(Fav quote) {
        if (null == quote.getQuote()) {
            return "";
        }
        return quote.getQuote();
    }

    private String getComment(Fav quote) {
        if (null == quote.getComment()) {
            return "";
        }
        return quote.getComment();
    }

    public void openWarning(String errorMsg) {
        Notification notification = new Notification(
                errorMsg, 50000, Notification.Position.BOTTOM_CENTER);
        notification.open();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // we don't need to do anything here now. Possibly remove interface and this contract-method.
    }

}
