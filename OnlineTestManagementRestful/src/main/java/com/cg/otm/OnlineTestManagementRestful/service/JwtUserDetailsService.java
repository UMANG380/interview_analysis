
package com.cg.otm.OnlineTestManagementRestful.service;
import com.cg.otm.OnlineTestManagementRestful.dto.MyUserDetails;
import com.cg.otm.OnlineTestManagementRestful.dto.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cg.otm.OnlineTestManagementRestful.model.UserDTO;
import com.cg.otm.OnlineTestManagementRestful.repository.UserRepository;


@Service
public class JwtUserDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository repository;

	@Autowired
	private PasswordEncoder bcryptEncoder;
	
	List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Optional<User> user = repository.findByUserName(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		
		return  user.map(MyUserDetails::new).get();
	}
	
	public User save(UserDTO user) {
		User newUser = new User();
		newUser.setUserName(user.getUsername());
		newUser.setUserPassword(bcryptEncoder.encode(user.getPassword()));
		newUser.setIsAdmin(user.getIsAdmin());
		newUser.setIsDeleted(user.getIsDeleted());
		return repository.save(newUser);
	}
}
