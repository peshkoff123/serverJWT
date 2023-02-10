package dev.peshkoff.exampleWeb.serverJWT.model;

import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Role {
    public static String USER = "USER";
    public static String ADMIN = "ADMIN";
    public static String BOSS = "BOSS";
    public static String SUPER_USER = "SUPER_USER";
    public static String FAT_BOSS = "FAT_BOSS";

    private static ArrayList<Role> roles;
    static { roles = new ArrayList<>();
             roles.add( new Role( USER));
             roles.add( new Role(  SUPER_USER));
             roles.add( new Role( ADMIN));
             roles.add( new Role( BOSS));
             roles.add( new Role( FAT_BOSS));
    }
    public static List<Role> getRolesList() { return roles;}

    private String _id;
    private String name;
    private String description;
    private LocalDateTime lastUpdate;

    public Role( String name) { this.name = name; this.description = name+" Description"; }

    @Override
    public int hashCode() {
        return  Integer.decode( "0x"+_id.substring( _id.length() - 4));
    }
    @Override
    public boolean equals( Object var1) {
        if( this == var1) return true;
        if( !Role.class.isInstance( var1)) return false;
        return this.hashCode() == ((Role)var1).hashCode();
    }

}
