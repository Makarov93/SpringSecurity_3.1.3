package makarov.springsecurity.service;

import makarov.springsecurity.model.Role;
import makarov.springsecurity.model.User;
import makarov.springsecurity.repository.RoleRepository;
import makarov.springsecurity.repository.UserRepository;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;


@Service
public class UserServiceImpl implements UserDetailsService, UserService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;


    public UserServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public void saveUser(User user) {
        // Проверка, является ли пользователь новым
        boolean isNewUser = (user.getId() == null) || !userRepository.existsById(user.getId());

        if (isNewUser) {
            // Новый пользователь, назначаем роль по умолчанию
            Role userRole = roleRepository.findById(2L)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            if (user.getRoles() == null) {
                user.setRoles(new HashSet<>());
            }
            user.getRoles().add(userRole);
        } else {
            // Существующий пользователь, сохраняем его текущие роли
            User existingUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setRoles(existingUser.getRoles());
        }
        // Сохранение пользователя
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

}