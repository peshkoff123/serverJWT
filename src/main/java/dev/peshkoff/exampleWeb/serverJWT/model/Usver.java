package dev.peshkoff.exampleWeb.serverJWT.model;

import lombok.*;

import java.util.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Usver {
    private static ArrayList<Usver> usvers;
    static { usvers = new ArrayList<>();
             usvers.add( new Usver( "user"));
             usvers.add( new Usver( "admin"));
             usvers.add( new Usver( "Leshka"));
             usvers.add( new Usver( "Peshkoff"));
             usvers.add( new Usver( "Lobotryas"));
           }

    public static List<Usver> getUsversList() { return usvers;}
/*    private static long counter;
    public static long getNextId() { return counter++;}

    public static Usver getUsverById( long Id) { return usvers.stream().filter( usver-> usver.getId() == Id).findFirst().orElse( null); }
    public static Optional getOptionalById(long Id) { return usvers.stream().filter(usver-> usver.getId() == Id).findFirst(); }
    public static Usver getUsverByName( String usverName) { return usvers.stream().filter( usver-> usver.getName().equals( usverName)).findFirst().orElse( null); }

    public static Usver deleteUsverById( long Id) {
        Usver usver = getUsverById( Id);
        usvers.remove( usver);
        return usver; }

    public static Usver addUsver( Usver usver) {
        usver.setId( getNextId());
        usvers.add( usver);
        return usver;
    }
    public static Usver updateUsver( Usver updUsver) {
        Usver usver = getUsverById( updUsver.getId());
        usver.setName( updUsver.getName());
        return usver;
    }
    public static Usver deleteUsver( long Id) {
        Usver usver = getUsverById( Id);
        return usver;
    }
*/
    private String _id;
    private String name;
    private String password;
    private boolean isActive;
    private LocalDateTime lastUpdate;
    private Set<Role> roles;

    public Usver() { this( null); }
    public Usver( String name) { this( null, name);}
    public Usver( String _id, String name) {
        this._id = _id;
        this.name = name;
        this.password = name;
        isActive = true;
        roles = new HashSet<>();
    }

    public void addRole( Role newRole) { roles.add( newRole); }
    public void removeRole( Role newRole) { roles.remove( newRole); }
    public String[] rolesArr() { return roles.stream().map( role -> role.getName()).toArray( String[] :: new); }
}

