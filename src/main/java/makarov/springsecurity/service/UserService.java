package makarov.springsecurity.service;

import makarov.springsecurity.model.User;

import java.util.List;

public interface UserService {
    void deleteUser(Long id);

    void saveUser(User user);

    User getUserById(Long id);

    List<User> getAllUsers();

    User getUserByUsername(String username);
}
