package demo.repo;


import com.github.lemniscate.spring.crud.repo.ApiResourceRepository;
import demo.model.User;

import java.util.List;

public interface UserSuppliedRepo extends ApiResourceRepository<Long, User> {
    List<User> findByName(String name);
}
