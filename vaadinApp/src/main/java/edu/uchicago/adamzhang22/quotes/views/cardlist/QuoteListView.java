package edu.uchicago.adamzhang22.quotes.views.cardlist;

import com.vaadin.flow.component.ClickEvent;
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
import edu.uchicago.adamzhang22.quotes.models.Quote;
import edu.uchicago.adamzhang22.quotes.service.AsyncRestCallback;
import edu.uchicago.adamzhang22.quotes.service.FavService;
import edu.uchicago.adamzhang22.quotes.service.QuoteService;
import edu.uchicago.adamzhang22.quotes.views.MainLayout;
import elemental.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Card List")
@Route(value = "card-list", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class QuoteListView extends Div implements AfterNavigationObserver {

    private Notification loading = new Notification("loading...", 500, Notification.Position.TOP_CENTER);
    private Notification done = new Notification("done fetching", 500, Notification.Position.BOTTOM_END);

    private FavService favService;
    private QuoteService quoteService;
    private Grid<Quote> grid = new Grid<>();
    private int startPage = 1;
    private boolean isLoading = false;

    private List<Quote> items = new ArrayList<>();
    private TextField textField;
    private String searchTerm;

    public QuoteListView(QuoteService quoteService, FavService favService) {
        this.quoteService = quoteService;
        this.favService = favService;
        textField = new TextField();
        textField.setLabel("Search Term");
        textField.setPlaceholder("search anime name (try these 3: 'Naruto', 'Bleach', 'One Piece'");
        textField.setAutofocus(true);
        textField.setWidthFull();
        textField.addKeyDownListener(keyDownEvent -> {
                    String keyStroke = keyDownEvent.getKey().getKeys().toString();
                    if (keyStroke.equals("[Enter]") && !isLoading) {
                        System.out.println(textField.getValue());
                        searchTerm = textField.getValue();
                        startPage = 1;
                        items.clear();
                        getQuotes(searchTerm);
                    }
                }
        );
        addClassName("card-list-view");
        setSizeFull();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(item -> createCard(item));
        grid.addItemClickListener(new ComponentEventListener<ItemClickEvent<Quote>>() {
            @Override
            public void onComponentEvent(ItemClickEvent<Quote> itemItemClickEvent) {
                System.out.println(itemItemClickEvent.getItem());
                Fav newFav = new Fav();
                newFav.setAnime(itemItemClickEvent.getItem().getAnime());
                newFav.setCharacter(itemItemClickEvent.getItem().getCharacter());
                newFav.setQuote(itemItemClickEvent.getItem().getQuote());
                newFav.setUserEmail(Cache.getInstance().getEmail());
                newFav.setComment("My favorite!");
                favService.addFavorite(UI.getCurrent(), m -> {
                    getUI().get().access(() -> {
                        Notification.show("Added to Favorites!" , 2000,
                                Notification.Position.BOTTOM_CENTER);
                    });
                }, newFav);
            }
        });

        add(textField, withClientsideScrollListener(grid));
    }

    private void getQuotes(String searchTerm) {
        isLoading = true;
        loading.open();
        AsyncRestCallback<List<Quote>> quoteListCallback = quoteResponse -> {

            //we must fire this callback on the UI thread and that is why we use getUi().get().acccess(() ->
            getUI().get().access(() -> {

                //this is the callback result, so volumesResponse is the volumesResponse returned from
                //      void operationFinished(T results);
                items.addAll(quoteResponse);
                grid.setItems(items);
                startPage += 1;
                isLoading = false;
                done.open();
                //https://vaadin.com/docs/v14/flow/advanced/tutorial-push-access
                //we need to notify the browser when we are done. Note that the parent-view MainView is annotated with
                //the @Push annotation, which will force the views to refresh themselves, including the grid.
                getUI().get().push();

            });
        };

        //the callback is expressed as a lambda. We hae access to the members of this class, such as grid, items, etc.
        quoteService.getQuotes(quoteListCallback, searchTerm, startPage);

    }

    //we wrap the Vaadin grid with this so that it emits this event at every gridScroll.
    private Grid<Quote> withClientsideScrollListener(Grid<Quote> grid) {
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
                getQuotes(searchTerm);
            }
            grid.scrollToIndex(items.size() / 2);

        }

    }

    private HorizontalLayout createCard(Quote quote) {
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

        Span anime = new Span("Anime: " + getAnime(quote));
        anime.addClassName("name");

        Span character = new Span("Character: " + getCharacter(quote));
        character.addClassName("name");

        Span quoteContent = new Span("Quote: " + getQuote(quote));
        quoteContent.addClassName("test");
        header.add(anime, character);
        description.add(header, quoteContent);
        card.add(description);

        Span prompt = new Span("Click to add to favorite!");
        prompt.addClassName("name");
        card.add(prompt);

        return card;
    }

    private String getAnime(Quote quote) {
        if (null == quote.getAnime()) {
            return "";
        }
        return quote.getAnime();
    }

    private String getCharacter(Quote quote) {
        if (null == quote.getCharacter()) {
            return "";
        }
        return quote.getCharacter();
    }

    private String getQuote(Quote quote) {
        if (null == quote.getQuote()) {
            return "";
        }
        return quote.getQuote();
    }


    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // we don't need to do anything here now. Possibly remove interface and this contract-method.
    }

}
