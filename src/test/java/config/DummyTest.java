package config;

import com.github.lemniscate.spring.crud.annotation.EnableApiResources;
import com.github.lemniscate.spring.crud.repo.ApiResourceRepository;
import com.github.lemniscate.spring.crud.svc.ApiResourceService;
import com.github.lemniscate.spring.crud.web.ApiResourceController;
import com.github.lemniscate.spring.crud.web.assembler.ApiResourceAssembler;
import demo.model.Organization;
import demo.model.Pet;
import demo.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.inject.Inject;
import javax.servlet.ServletContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DummyTest.Config.class)
public class DummyTest {

    @Inject
    private ApplicationContext ctx;

    @Inject
    private ApiResourceRepository<Long, User> userRepo;
    @Inject
    private ApiResourceService<Long, User, User, User, User> userService;

    @Inject
    private ApiResourceRepository<Long, Pet> petRepo;
    @Inject
    private ApiResourceService<Long, Pet, Pet, Pet, Pet> petService;

    @Inject
    private ApiResourceController<Long, Organization, Organization, Organization, Organization> organizationController;

    @Inject
    private ApiResourceController<Long, User, User, User, User> userController;

    @Inject
    private ApiResourceAssembler<Long, User, User, User, User> userAssembler;

    @Test
    public void foo(){
        System.out.println(ctx);

        ResponseEntity<Page<Resource<User>>> users = userController.getAll(new LinkedMultiValueMap<String, String>(), new PageRequest(0, 20));
        Assert.notEmpty( users.getBody().getContent() );
    }

    @Configuration
    @EnableApiResources(value = User.class)
    @Import(DefaultConfig.class)
    @EnableJpaRepositories(basePackages = {"demo.repo"})
    @ComponentScan(basePackages = "demo")
    public static class Config{

    }


    private ServletRequestAttributes attrs = new ServletRequestAttributes(new MockHttpServletRequest());

    @Mock
    private ServletContext servletContext;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        RequestContextHolder.setRequestAttributes(attrs);
    }
}
