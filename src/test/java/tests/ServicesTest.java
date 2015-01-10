package tests;

import com.github.lemniscate.spring.crud.annotation.EnableApiResources;
import com.github.lemniscate.spring.crud.repo.ApiResourceRepository;
import com.github.lemniscate.spring.crud.svc.ApiResourceService;
import com.github.lemniscate.spring.crud.svc.ApiResourceServices;
import com.github.lemniscate.spring.crud.util.ApiResourceRegistry;
import com.github.lemniscate.spring.crud.web.ApiResourceController;
import com.github.lemniscate.spring.crud.web.assembler.ApiResourceAssembler;
import com.github.lemniscate.spring.crud.web.assembler.ApiResourceAssemblers;
import config.DefaultConfig;
import demo.model.Organization;
import demo.model.Pet;
import demo.model.User;
import demo.web.assembler.UserAssembler;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.inject.Inject;
import javax.servlet.ServletContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ServicesTest.Config.class)
public class ServicesTest {

    @Inject
    private ApiResourceServices services;

    @Test
    public void initialTests(){
        User user = services.findOne(User.class, 1L);
        Assert.notNull(user);

        Page<User> users = services.findForRead(User.class, new PageRequest(0, 100));
        Assert.notEmpty(users.getContent());
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
