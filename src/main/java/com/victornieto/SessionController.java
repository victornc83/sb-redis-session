package com.victornieto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * Created by victor.nieto.castan on 14/06/2017.
 */
@RestController
public class SessionController {

    @GetMapping("/")
    public String uid(HttpSession session) {
        UUID uid = (UUID) session.getAttribute("uid") ;
        if(uid == null){
            uid = UUID.randomUUID();
        }
        session.setAttribute("uid", uid);
        return "My uid: " + uid.toString() ;
    }
}
