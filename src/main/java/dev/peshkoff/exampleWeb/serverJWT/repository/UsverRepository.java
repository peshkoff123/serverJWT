package dev.peshkoff.exampleWeb.serverJWT.repository;

import com.mongodb.client.model.*;
import dev.peshkoff.exampleWeb.serverJWT.model.Role;
import dev.peshkoff.exampleWeb.serverJWT.model.Usver;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UsverRepository {
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> userCollection;

    @Autowired private RoleRepository roleRepo;
    public void setRoleRepo( RoleRepository roleRepo) { this.roleRepo = roleRepo; }

    public UsverRepository() {
        mongoClient = new MongoClient("localhost", 27017);
        mongoDatabase = mongoClient.getDatabase("Peshkoff");
        userCollection = mongoDatabase.getCollection( "Users");
    }

    public List<Usver> findAll() {
        ArrayList<Usver> res = new ArrayList<>();
        try( MongoCursor<Document> cursor = userCollection.find().iterator()) {
            while( cursor.hasNext()) {
                Document doc = cursor.next();
                res.add( getUsver( doc));
                System.out.println( doc.toJson());
            }
        }
        return res;
    }
    public List<Usver> findByStrInName( String searchStr) {

        userCollection.createIndex( Indexes.text("name"));

        List<Document> listDoc = userCollection.find( Filters.text( searchStr)).into( new ArrayList<Document>());
        ArrayList<Usver> res = new ArrayList<>();
        for( Document doc : listDoc) {
            System.out.println("findByStrInName(" + doc + "); = " + doc);
            res.add( getUsver( doc));
        }
        return res;
    }

    public Usver findById( String _id) {
        Document findDoc =  userCollection.find( eq( new ObjectId( _id))).first();
        System.out.println( "findById("+_id+"); = "+findDoc);
        return getUsver( findDoc);
    }
    public Usver findByName( String name) {
        Document findDoc =  userCollection.find( eq( "name", name)).first();
        System.out.println( "findByName("+name+"); = "+findDoc);
        return getUsver( findDoc);
    }
    public Optional<Usver> findOptionalById( String _id) {
        return Optional.ofNullable( findById( _id));
    }

    public Usver save( Usver usver) {
        if( usver.get_id() == null) {
            Document doc = getDocument( usver);
            userCollection.insertOne( doc);
            usver.set_id( doc.getObjectId( "_id").toString());
            System.out.println( "save("+usver+"); doc_id = "+usver);
            return usver;
        }

        Set<String> setRole_id =  usver.getRoles().stream().map( role -> role.get_id()).collect( Collectors.toSet());
        Bson updates = Updates.combine( Updates.set( "name", usver.getName()),
                                        Updates.set( "password", usver.getPassword()),
                                        Updates.set( "isActive", usver.isActive()),
                                        Updates.set( "roles", setRole_id),
                                        //Updates.currentTimestamp("lastUpdate")
                                        Updates.set( "lastUpdate", usver.getLastUpdate()));
        UpdateResult result = userCollection.updateOne( eq( new ObjectId( usver.get_id())), updates);
        System.out.println( "save("+usver+"); updateResult = "+result);
        return usver;
    }
    public void delete( String _id) {
        DeleteResult res = userCollection.deleteOne( eq( new ObjectId( _id)));
        System.out.println( "delete("+_id+"); DeleteResult = "+ res);
    }

    public void createUsverData() {
        List<InsertOneModel<Document>> l = new ArrayList<>();
        for( Usver usver: Usver.getUsversList())
            l.add( new InsertOneModel<>( getDocument( usver)));
        BulkWriteResult res = userCollection.bulkWrite( l);
        System.out.println( "createUsverData(); BulkWriteResult = "+ res);
    }


    private Document getDocument( Usver usver) {
        if( usver == null) return null;
        Document doc = new Document();
        if( usver.get_id() != null && ObjectId.isValid( usver.get_id()))
            doc.append( "_id", new ObjectId( usver.get_id()));
        Set<String> setRole_id =  usver.getRoles().stream().map( role -> role.get_id()).collect( Collectors.toSet());
        doc.append( "name", usver.getName())
           .append( "password", usver.getPassword())
           .append( "isActive", usver.isActive())
           .append( "roles", setRole_id);
        if( usver.getLastUpdate() == null)
            usver.setLastUpdate( LocalDateTime.now());
        doc.append( "lastUpdate", usver.getLastUpdate());
        return doc;
    }
    private Usver getUsver( Document doc) {
        if( doc == null) return null;
        Usver usver = new Usver();
        usver.set_id( doc.getObjectId( "_id").toHexString());
        usver.setName( doc.getString( "name"));
        usver.setPassword( doc.getString( "password"));
        usver.setActive( doc.getBoolean( "isActive"));
        List<String> listRoles = doc.getList( "roles", String.class);
        if( listRoles != null)
            usver.setRoles( roleRepo.findAllByListId( listRoles));
        usver.setLastUpdate( LocalDateTime.ofInstant( doc.getDate( "lastUpdate").toInstant(), ZoneId.systemDefault()));
        return usver;
    }
}
