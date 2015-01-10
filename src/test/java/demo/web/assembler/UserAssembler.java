package demo.web.assembler;

import com.github.lemniscate.spring.crud.web.assembler.ApiResourceAssembler;
import demo.model.User;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class UserAssembler extends ApiResourceAssembler<Long, User, User, User, User>{

    @Override
    public void addLinks(Collection<Link> links, User bean) {
        super.addLinks(links, bean);
        links.add(new Link("http://test.com"));
    }
}
