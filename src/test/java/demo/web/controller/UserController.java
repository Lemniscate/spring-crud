package demo.web.controller;

import com.github.lemniscate.spring.crud.annotation.AssembleWith;
import com.github.lemniscate.spring.crud.mapping.ApiResourceMapping;
import com.github.lemniscate.spring.crud.util.ApiResourceUtil;
import com.github.lemniscate.spring.crud.web.ApiResourceController;
import com.github.lemniscate.spring.crud.web.assembler.ApiResourceAssembler;
import demo.model.Pet;
import demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import java.util.List;

// TODO add support for tagging these with controller annotations
@Component
public class UserController extends ApiResourceController<Long, User, User, User, User> {

    @Inject
    private ApiResourceAssembler<Long, Pet, Pet, Pet, Pet> petAssembler;

//    public UserController() {
//        super( ApiResourceUtil.<Long, User, User, User, User> from(User.class) );
//    }

    @AssembleWith(value = "pets", domainClass = User.class)
    @RequestMapping(value="/{parentId}/pets", method= RequestMethod.GET)
    public ResponseEntity<Page<Resource<Pet>>> getPets(@PathVariable Long parentId){
        User parent = service.findOne(parentId);
        Assert.notNull(parent, "Couldn't locate the user");

        List<Pet> entities = parent.getPets();
        List<Resource<Pet>> resources = petAssembler.toResources(entities);
        Page<Resource<Pet>> pagedResources = new PageImpl<Resource<Pet>>(resources);
        ResponseEntity<Page<Resource<Pet>>> response = new ResponseEntity<Page<Resource<Pet>>>(pagedResources, HttpStatus.OK);
        return response;
    }

}
