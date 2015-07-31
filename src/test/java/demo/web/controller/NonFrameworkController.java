package demo.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by dave on 3/27/15.
 */

@Controller
@RequestMapping("/foo")
public class NonFrameworkController {
    @RequestMapping("bar")
    public String bar(){
        return "bar";
    }

}
