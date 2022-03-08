package com.learn.support.service.impl;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.learn.support.constant.FileConstant;
import com.learn.support.constant.UserImplConstant;
import com.learn.support.domain.User;
import com.learn.support.domain.UserPrincipal;
import com.learn.support.enumeration.Role;
import com.learn.support.exception.model.EmailExistException;
import com.learn.support.exception.model.EmailNotFoundException;
import com.learn.support.exception.model.UserNotFoundException;
import com.learn.support.exception.model.UsernameExistException;
import com.learn.support.repository.UserRepository;
import com.learn.support.service.LoginAttemptService;
import com.learn.support.service.UserService;

@Service
@Transactional // manage propagation whenever the service is dealing more transactions
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

	private Logger LOGGER = LoggerFactory.getLogger(getClass()); // UserServiceImpl.class()
	private UserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;
	private LoginAttemptService loginAttemptService;
	private EmailService emailService;
	private CopyOption REPLACE_EXISTING; 
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, 
			LoginAttemptService loginAttemptService, EmailService emailService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.loginAttemptService = loginAttemptService;
		this.emailService = emailService;
	}
	
	/**
	* The loadUserByUsername method gets called whenever Spring Security is trying to check the
	* authentication of the user. The method needs to be overridden. 	
	*/
	@SuppressWarnings("static-access")
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findUserByUsername(username);
		if (user == null) {
			LOGGER.error(UserImplConstant.NO_USER_FOUND_BY_USERNAME + username);
			throw new UsernameNotFoundException(UserImplConstant.NO_USER_FOUND_BY_USERNAME + username);
		} else {
			validateLoginAttempt(user);
			user.setLastLoginDateDisplay(user.getLastLoginDate());
			user.setLastLoginDate(new Date());
			userRepository.save(user);
			UserPrincipal userPrincipal = new UserPrincipal(user); // because UserPrincipal implements UserDetails I can return userPrincipal as UserDetails
			LOGGER.info(UserImplConstant.RETURNING_FOUND_USER_BY_USERNAME + username);
			return userPrincipal;
		}
	}
	
	private void validateLoginAttempt(User user) {
		if (user.isNotLocked()) {
			if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
				user.setNotLocked(false);
			} else {
				user.setNotLocked(true);
			}
		} else {
			loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
		}
	}
	
	@Override
	public User register(String firstName, String lastName, 
			String username, String email) throws UsernameExistException, UserNotFoundException, EmailExistException {
		validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
		User user = new User();
		user.setUserId(generateUserId());
		String password = generatePassword();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUsername(username);
		user.setEmail(email);
		user.setJoinDate(new Date());
		user.setPassword(encodePassword(password));
		user.setActive(true);
		user.setNotLocked(true);
		user.setRole(Role.ROLE_USER.name()); // setRole() takes a String. ROLE_USER is an enum and name() method will convert the enum into a String.
		user.setAuthorities(Role.ROLE_USER.getAuthorities());
		user.setProfileImageUrl(getTemporaryProfileImageUrl(username).toString());
		userRepository.save(user); // call userRepository to save the user in the database
		emailService.sendNewPasswordEmail(firstName, lastName, password, email);
		// LOGGER.info("SEND A MESSAGE TO THE CONSOLE");
		return user;
	}
	
	@Override
	public User addNewUser(String firstName, String lastName, String username, String email, String role,
			boolean isNotLocked, boolean isActive) throws UsernameExistException, UserNotFoundException, EmailExistException {
		validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
		User user = new User();
		user.setUserId(generateUserId());
		String password = generatePassword();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUsername(username);
		user.setEmail(email);
		user.setJoinDate(new Date());
		user.setPassword(encodePassword(password));
		user.setActive(true);
		user.setNotLocked(true);
		user.setRole(getRoleEnumName(role).name());
		user.setAuthorities(getRoleEnumName(role).getAuthorities());
		user.setProfileImageUrl(getTemporaryProfileImageUrl(username).toString());
		userRepository.save(user); 
		// saveProfileImage(user, profileImage); // Not saving the image in the system unless all user's properties pass and user is saved in the database in userRepository.save(user).
		emailService.sendNewPasswordEmail(firstName, lastName, password, email);
		return user;
	}

	@Override
	public User updateUser(String currentUsername, String firstName, String lastName, String username, String email,
			String role, boolean isNotLocked, boolean isActive) throws UsernameExistException, UserNotFoundException, EmailExistException {
		User currentUser = validateNewUsernameAndEmail(currentUsername, username, email);
		currentUser.setFirstName(firstName);
		currentUser.setLastName(lastName);
		currentUser.setUsername(username);
		currentUser.setEmail(email);
		currentUser.setActive(true);
		currentUser.setNotLocked(true);
		currentUser.setRole(getRoleEnumName(role).name());
		currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
		userRepository.save(currentUser); 
		// saveProfileImage(currentUser, profileImage);
		return currentUser;		
	}

	@Override
	public void deleteUser(long id) {
		userRepository.deleteById(id);
	}
	
	/**
	* resetPassword() method is temporarily renewing the user's password by generating a 
	* random password. 
	* @param email the user email
	*/
	@Override
	public void resetPassword(String email) throws EmailNotFoundException {
		User user = userRepository.findUserByEmail(email);
		if (user == null) {
			throw new EmailNotFoundException(UserImplConstant.NO_USER_FOUND_BY_EMAIL + email);
		} 
		String password = generatePassword();
		user.setPassword(encodePassword(password));
		userRepository.save(user);
		emailService.sendNewPasswordEmail(user.getFirstName(), user.getLastName(), password, user.getEmail());
	}

	@Override
	public User updateProfileImage(String username, MultipartFile profileImage) throws UsernameExistException, UserNotFoundException, EmailExistException, IOException {
		User user = validateNewUsernameAndEmail(username, null, null);
		saveProfileImage(user, profileImage);
		return user;
	}
	
	@Override
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@Override
	public User findUserByUsername(String username) {
		return userRepository.findUserByUsername(username);
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}
	
	private Role getRoleEnumName(String role) {
		// "user" -> "user".toUpperCase() -> "USER" -> ROLE_USER(Authority.USER_AUTHORITIES) -> { "user:read" }
		return Role.valueOf(role.toUpperCase());
	}
	
	private void saveProfileImage(User user, MultipartFile profileImage) throws IOException {
		if (profileImage != null) {
			// user/home/support/user/username
			Path userFolder = Paths.get(FileConstant.USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();			
			if (!Files.exists(userFolder)) {
				Files.createDirectories(userFolder);
				LOGGER.info(FileConstant.DIRECTORY_CREATED + userFolder);
			}
			// user/home/support/user/username/username.jpg 
			Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + FileConstant.DOT + FileConstant.JPG_EXTENSION));
			
			/**
			 * More Details -
			 * .copy(): Copies all bytes from an input stream to a file.
			 * .getInputStream(): Return an InputStream to read the contents of the file from. Returns the contents 
			 * of the file as stream, or an empty stream if empty.
			 * .resolve(): REPLACE_EXISTING field in the resolve() method is replacing an existing picture for one of the 
			 * same name. I could've used either .deleteIfExists or REPLACE_EXISTING - I'm making sure the old picture will be deleted or
			 * replaced.
			 */
			Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + FileConstant.DOT + FileConstant.JPG_EXTENSION), REPLACE_EXISTING);
			user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
			userRepository.save(user);
			LOGGER.info(FileConstant.FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
		}
	}
	
	private String setProfileImageUrl(String username) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(FileConstant.USER_IMAGE_PATH + username + 
				FileConstant.FORWARD_SLASH + username + FileConstant.DOT + FileConstant.JPG_EXTENSION).toUriString();
	}

	private Object getTemporaryProfileImageUrl(String username) {
		/**
		 * More Details -
		 * ServletUriComponentsBuilder will return whatever the URL is for the service.
		 * e.g.: if the application is deployed to Google.com, the based URL will be
		 * www.google.com/
		 */
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(FileConstant.DEFAULT_USER_IMAGE_PATH + username).toUriString();
	}

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	private String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(10);
	}

	private String generateUserId() {
		return RandomStringUtils.randomNumeric(10);
	}
	
	// This method will be very generic to be applicable for when someone is either creating a new acccount 
	// or updating their account.
	@SuppressWarnings("static-access")
	private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String email) throws UsernameExistException, UserNotFoundException, EmailExistException {
		User userByNewUsername = findUserByUsername(newUsername);
		User userByNewEmail = findUserByEmail(email);
		if (StringUtils.isNotBlank(currentUsername)) {
			User currentUser = findUserByUsername(currentUsername);
			if (currentUser == null) {
				throw new UserNotFoundException(UserImplConstant.NO_USER_FOUND_BY_USERNAME + currentUsername);
			}
			if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
				throw new UsernameExistException(UserImplConstant.USERNAME_ALREADY_EXISTS);
			}
			if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
				throw new EmailExistException(UserImplConstant.EMAIL_ALREADY_EXIST);
			}
			return currentUser;
		} else {
			if (userByNewUsername != null) {
				throw new UsernameExistException(UserImplConstant.USERNAME_ALREADY_EXISTS);
			}
			if (userByNewEmail != null) {
				throw new EmailExistException(UserImplConstant.EMAIL_ALREADY_EXIST);
			}
			return null;
		}
	}


}
