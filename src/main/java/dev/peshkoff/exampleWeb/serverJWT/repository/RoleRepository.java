package dev.peshkoff.exampleWeb.serverJWT.repository;

import com.mongodb.MongoClient;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import dev.peshkoff.exampleWeb.serverJWT.model.Role;
import dev.peshkoff.exampleWeb.serverJWT.model.Usver;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;


@Repository
public class RoleRepository {
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> roleCollection;

    public RoleRepository() {
        mongoClient = new MongoClient("localhost", 27017);
        mongoDatabase = mongoClient.getDatabase("Peshkoff");
        roleCollection = mongoDatabase.getCollection( "Roles");
    }

    public List<Role> findAll() {
        ArrayList<Role> res = new ArrayList<>();
        try( MongoCursor<Document> cursor = roleCollection.find().iterator()) {
            while( cursor.hasNext()) {
                Document doc = cursor.next();
                res.add( getRole( doc));
                System.out.println( doc.toJson());
            }
        }
        return res;
    }
    public Set<Role> findAllByListId( List<String> listRole_id) {
        Set<Role> res = new HashSet<>();
        List<ObjectId> listObjectId = listRole_id.stream().map( roleId -> new ObjectId( roleId)).collect(Collectors.toList());
        //List<ObjectId> listObjectId = new ArrayList<>();
        //listObjectId.add( new ObjectId( listRole_id.get( 0)));
        try( MongoCursor<Document> cursor = roleCollection.find( in( "_id", listObjectId)).iterator()) {
            while( cursor.hasNext()) {
                Document doc = cursor.next();
                res.add( getRole( doc));
                System.out.println( doc.toJson());
            }
        }
        return res;
    }
    public Role findByName( String name) {
        Document findDoc = roleCollection.find( eq( "name", name)).first();
        System.out.println( "findByName("+name+"); = "+findDoc);
        return getRole( findDoc);
    }
    public void createRoleData() {
        List<InsertOneModel<Document>> l = new ArrayList<>();
        for( Role role: Role.getRolesList())
            l.add( new InsertOneModel<>( getDocument( role)));
        BulkWriteResult res = roleCollection.bulkWrite( l);
        System.out.println( "createUsverData(); BulkWriteResult = "+ res);
    }

    private Document getDocument( Role role) {
        Document doc = new Document();
        if( role.get_id() != null && ObjectId.isValid( role.get_id()))
            doc.append( "_id", new ObjectId( role.get_id()));
        doc.append( "name", role.getName());
        doc.append( "description", role.getDescription());
        if( role.getLastUpdate() == null)
            role.setLastUpdate( LocalDateTime.now());
        doc.append( "lastUpdate", role.getLastUpdate());
        return doc;
    }
    private Role getRole( Document doc) {
        Role role = new Role();
        role.set_id( doc.getObjectId( "_id").toHexString());
        role.setName( doc.getString( "name"));
        role.setDescription( doc.getString( "description"));
        role.setLastUpdate( LocalDateTime.ofInstant( doc.getDate( "lastUpdate").toInstant(), ZoneId.systemDefault()));
        return role;
    }
}
