package edu.uchicago.adamzhang22.quarkus.repositories;


import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import edu.uchicago.adamzhang22.quarkus.models.Fav;
import io.quarkus.runtime.StartupEvent;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

@ApplicationScoped
public class FavMongodbRepo {


    @Inject
    MongoClient mongoClient;


    private Gson gson = new Gson();

    public static final int PAGE_SIZE = 20;

    public static  final String DEFAULT_EMAIL = "adamzhang22@uchicago.edu";


    //this will get fired when the quarkus microservice starts
    void onStart(@Observes StartupEvent ev) {

        long collectionSize = getCollection().countDocuments();
        if (collectionSize > 200) return;

        Faker faker = new Faker();
        getCollection().insertMany(
                Stream.generate(
                                () ->   new Document()
                                        .append("userEmail", DEFAULT_EMAIL)
                                        .append("anime", faker.beer().name())
                                        .append("character", faker.company().name())
                                        .append("quote", faker.company().buzzword())
                                        .append("comment", faker.beer().name()))
                        .peek(fav -> System.out.println(fav))
                        .limit(100).collect(Collectors.toList())
        );
    }

    public Fav add(Fav favoriteItem) {
        Document document = new Document()
                .append("userEmail", favoriteItem.getUserEmail())
                .append("anime", favoriteItem.getAnime())
                .append("character", favoriteItem.getCharacter())
                .append("quote", favoriteItem.getQuote())
                .append("comment", favoriteItem.getComment());
        getCollection().insertOne(document);
        return  favoriteItem;
    }

    public Fav get(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        FindIterable<Document> documents = getCollection().find(query);

        List<Fav> items = new ArrayList<>();
        for (Document document : documents) {
            items.add(doc2item(document));
        }

        //this will produce a 404 not found
        if (items.size() != 1) return null;

        return items.get(0);

    }

    public Fav get(String userEmail, String quote, String character, String anime) {
        BasicDBObject query = new BasicDBObject();
        query.put("userEmail", userEmail);
        query.put("quote", quote);
        query.put("anime", anime);
        query.put("character", character);
        FindIterable<Document> documents = getCollection().find(query);
        List<Fav> items = new ArrayList<>();
        for (Document document : documents) {
            items.add(doc2item(document));
        }

        if (items.size() == 0) return null;

        return items.get(0);
    }


    public Fav delete(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        FindIterable<Document> documents = getCollection().find(query);

        Document firstDocument = null;
        for (Document document : documents) {
            firstDocument = document;
            break;
        }
        getCollection().deleteOne(firstDocument);
        return doc2item(firstDocument);
    }

    public Fav update(String id, Fav fav) {

        BasicDBObject query = new BasicDBObject();
        Bson filter = eq("_id", new ObjectId(id));
        Bson updateOperation = set("comment", fav.getComment());
        getCollection().updateOne(filter, updateOperation);

        query.put("_id", new ObjectId(id));
        FindIterable<Document> documents= getCollection().find(query);

        Document firstDocument = null;
        for (Document document : documents) {
            firstDocument = document;
            break;
        }

        return doc2item(firstDocument);
    }

    //https://www.technicalkeeda.com/java-mongodb-tutorials/java-mongodb-driver-3-3-0-pagination-example
    public List<Fav> paged(String userEmail, int page){
        BasicDBObject query = new BasicDBObject();
        query.put("userEmail", userEmail);
        List<Fav> favs = new ArrayList<>();
        try {
            MongoCursor<Document> cursor =
                    getCollection().find(query).skip(PAGE_SIZE * (page - 1)).limit(PAGE_SIZE).iterator();
            while (cursor.hasNext()) {
                Document document = cursor.next();
                favs.add(doc2item(document));
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return favs;
    }


    //define the collection
    private MongoCollection getCollection() {
        return mongoClient.getDatabase("favs_db").getCollection("favs_collection");
    }

    //local transform ops
    private Fav doc2item(org.bson.Document document) {
        Fav fav = new Fav();
        if (document != null && !document.isEmpty()) {
            fav.setId(document.getObjectId("_id").toHexString());
            fav.setUserEmail(document.getString("userEmail"));
            fav.setAnime(document.getString("anime"));
            fav.setCharacter(document.getString("character"));
            fav.setQuote(document.getString("quote"));
            fav.setComment(document.getString("comment"));
        }
        return fav;
    }
}