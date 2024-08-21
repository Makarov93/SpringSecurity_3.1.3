package makarov.springsecurity.config;

import makarov.springsecurity.model.Role;
import makarov.springsecurity.model.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Set;

@Component
public class DbInit implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (entityManager.createQuery("SELECT u FROM User u WHERE u.username = 'admin'", User.class).getResultList().isEmpty()) {

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin");
            admin.setEmail("admin@mail.com");
            admin.setRoles(Set.of(new Role(1L)));
            entityManager.persist(admin);
        }

        if (entityManager.createQuery("SELECT u FROM User u WHERE u.username = 'user'", User.class).getResultList().isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setPassword("user");
            user.setEmail("user@mail.com");
            user.setRoles(Set.of(new Role(2L)));
            entityManager.persist(user);
        }

        entityManager.flush();
    }
}