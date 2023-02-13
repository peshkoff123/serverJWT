package dev.peshkoff.exampleWeb.serverJWT.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;

import com.mongodb.client.model.Filters;
import dev.peshkoff.exampleWeb.serverJWT.model.Role;
import org.bson.Document;

import dev.peshkoff.exampleWeb.serverJWT.model.Usver;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class MongoDriver {
/*    public static void initMongoDatabase() {
        mongoClient = new MongoClient("localhost", 27017);
        mongoDatabase = mongoClient.getDatabase("Peshkoff");
//        MongoCredential credential = MongoCredential.createCredential("peshkoff1", "Peshkoff1", "peshkoff1".toCharArray());
    }*/

    public static void main( String[] args) throws JsonProcessingException {
        RoleRepository roleRepo = new RoleRepository();
        List<Role> listRole = roleRepo.findAll();

        UsverRepository usverRepo = new UsverRepository();
        usverRepo.setRoleRepo( roleRepo);

        usverRepo.findByStrInName( "Peshkoff");
/*
        Usver usver = usverRepo.findByName( "user");
        usver.addRole( roleRepo.findByName( "SUPER_USER"));
        usver.addRole( roleRepo.findByName( "BOSS"));

        usver.rolesArr();

        usver.removeRole( roleRepo.findByName( "ADMIN"));
        usver.removeRole( roleRepo.findByName( "BOSS"));
        usver.removeRole( roleRepo.findByName( "USER"));
        usverRepo.save( usver);
        usver = usverRepo.findByName( "user");
*/
/*        usverRepo.createUsverData();
        usverRepo.save( new Usver( 100, "newTestName1"));
        Usver usver = usverRepo.findById( "63df7acb0c926f1cf2f6dfae");
        Usver usver = usverRepo.findByName( "newTestName1");
        usverRepo.findAll();
        usver.setName( "Update1");
        usver.setPassword( "Update1");
        usverRepo.save( usver);
        usverRepo.delete( "63de8b8baf232c75aa527e21");
*/


        /*Bson filter = Filters.and(Filters.gt("qty", 3), Filters.lt("qty", 9));
          collection.find(filter).forEach(doc -> System.out.println(doc.toJson()));
         */
/*
import static com.mongodb.client.model.Sorts.ascending;

collection.find().sort(ascending("<field name>"));
collection.find().sort(ascending("letter", "_id"));

List<Document> results = new ArrayList<>();
collection.find().sort(ascending("_id")).into(results);
*/


//        for( String name : mongoDatabase.listCollectionNames())
//            System.out.println(name);
    }
}
