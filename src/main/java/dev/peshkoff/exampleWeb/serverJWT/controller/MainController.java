package dev.peshkoff.exampleWeb.serverJWT.controller;

import dev.peshkoff.exampleWeb.serverJWT.model.Role;
import dev.peshkoff.exampleWeb.serverJWT.model.Usver;
import dev.peshkoff.exampleWeb.serverJWT.repository.RoleRepository;
import dev.peshkoff.exampleWeb.serverJWT.repository.UsverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping("usver")
public class MainController {
    @Autowired UsverRepository usverRepository;
    @Autowired RoleRepository roleRepository;

    @GetMapping( "/roles")
    @PreAuthorize( "hasAnyRole('USER','ADMIN','SUPER_USER')")
    public List<Role> getRoleList() {
        return roleRepository.findAll();
    }


    @GetMapping
    @PreAuthorize( "hasAnyRole('USER','ADMIN','SUPER_USER')")
    public List<Usver> getUsverList() {
        return usverRepository.findAll();
    }

    @GetMapping( "/byName")
    @PreAuthorize( "hasAnyRole('USER','ADMIN','SUPER_USER')")
    public ResponseEntity<?> getUsverByName( @RequestParam(required=true) String name) {
        Usver u = usverRepository.findByName( name);
        if( u == null)
            return new ResponseEntity<>("Invalid userName ", HttpStatus.NOT_FOUND);
        return ResponseEntity.ok( u);
    }

    @GetMapping( "{Id}")
    @PreAuthorize( "hasAnyRole('USER','ADMIN','SUPER_USER')")
    public ResponseEntity<?> getUsverById( @PathVariable String Id) {
        Usver u = usverRepository.findById( Id);
        if( u == null)
            return new ResponseEntity<>("Invalid user Id ", HttpStatus.NOT_FOUND);
        return ResponseEntity.ok( u);
    }

    @PostMapping
    @PreAuthorize( "hasRole('ADMIN')")
    public Usver addUsver( @RequestBody Usver newUsver) {
        System.out.println( "Add newUsver="+newUsver);
        return usverRepository.save( newUsver);
    }

    @PutMapping
    @PreAuthorize( "hasRole('ADMIN')")
    public Usver updateUsver( @RequestBody Usver updUsver) {
        System.out.println( "Update updUsver="+updUsver);
        return usverRepository.save( updUsver);
    }

    @DeleteMapping( "{Id}")
    @PreAuthorize( "hasRole('ADMIN')")
    public void deleteUsver( @PathVariable String Id) {
        usverRepository.delete( Id);
        System.out.println( "Delete usver Id="+Id);
    }

/*
    @ExceptionHandler( RuntimeException.class)
    public final ResponseEntity<Exception> handleAllExceptions( RuntimeException ex) {
        return new ResponseEntity<Exception>( ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }
*/

    @GetMapping("/bla")
    @ResponseStatus( value=HttpStatus.CONFLICT, reason="Are You Insane?")
    public void retSpecialResponseStatus(HttpServletResponse resp) { ; }
}

/* TODO: Log,
//get all
fetch('/usver').then(response => response.json().then(console.log))
//get user by Id
fetch('/usver/1')
fetch('/usver/byId?Id=1', { method: 'GET'}
                          }).then( result => result.json().then( console.log))
fetch('/usver/byId', { method: 'GET', headers: { 'Id': '1' }
                     }).then( result => result.json().then( console.log))
//add user
fetch('/usver', { method: 'POST',
                  headers: { 'Content-Type': 'application/json' },
                  body: JSON.stringify({ id: -1, name: 'BlaBla' })
                }).then( result => result.json().then( console.log))
//update user
fetch('/usver', { method: 'PUT',
                  headers: { 'Content-Type': 'application/json' },
                  body: JSON.stringify({ id: 3, name: 'BlaBla_Updated' })
                }).then( result => result.json().then( console.log))
//delete user
fetch('/usver/3', { method: 'DELETE'}).then( result => result.json().then( console.log))
 */

