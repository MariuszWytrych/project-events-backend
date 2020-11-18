package pl.sdacademy.projecteventsbackend.user;

import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sdacademy.projecteventsbackend.component.userContext.UserContext;
import pl.sdacademy.projecteventsbackend.user.dto.EditUserRequest;
import pl.sdacademy.projecteventsbackend.user.dto.RegisterUserRequest;
import pl.sdacademy.projecteventsbackend.user.dto.UserResponse;
import pl.sdacademy.projecteventsbackend.user.model.UserEntity;
import pl.sdacademy.projecteventsbackend.user.model.UserRole;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserContext userContext;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, UserContext userContext) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userContext = userContext;
    }

    public UserResponse registerUser(RegisterUserRequest newUser) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(newUser.getName());
        userEntity.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userEntity.setMail(newUser.getMail());
        userEntity.setAccountNonExpired(true);
        userEntity.setAccountNonLocked(true);
        userEntity.setCreatedUserDate(LocalDateTime.now());
        userEntity.setCredentialsNonExpired(true);
        userEntity.setEnabled(true);
        userEntity.setRoles(Collections.singleton(UserRole.USER));
        userEntity.setUpdatedOn(LocalDateTime.now());
        userEntity.setDateOfBirth(newUser.getDateOfBirth());

        userRepository.save(userEntity);

        UserResponse response = new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getMail(), userEntity.getDateOfBirth());
        return response;
    }

    public UserResponse getUserById(long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getMail(), userEntity.getDateOfBirth());
    }

    @Transactional
    public UserResponse updateUserByUserId(long userId, EditUserRequest editedData) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserEntity currentUser = userContext.getCurrentUser();

        if(!currentUser.equals(userEntity)) {
            throw new AccessDeniedException("Can't do that");
        }
            userEntity.setUsername(editedData.getName());
            userEntity.setMail(editedData.getMail());
            userEntity.setDateOfBirth(editedData.getDateOfBirth());
            userEntity.setUpdatedOn(LocalDateTime.now());
            userRepository.save(userEntity);
            UserResponse response = new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getMail(), userEntity.getDateOfBirth());
            return response;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findUserEntityByUsername(s)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public HttpStatus deleteUserById(long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userEntity.setEnabled(false);
        userEntity.setAccountNonExpired(false);
        userEntity.setAccountNonLocked(false);
        userEntity.setCredentialsNonExpired(false);
        userEntity.setUpdatedOn(LocalDateTime.now());
        userRepository.save(userEntity);
        return HttpStatus.OK;
    }
}