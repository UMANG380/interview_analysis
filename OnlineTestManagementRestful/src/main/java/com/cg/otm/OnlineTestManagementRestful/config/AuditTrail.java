package com.cg.otm.OnlineTestManagementRestful.config;

import java.util.Optional;
import java.net.*;
import org.springframework.data.domain.AuditorAware;

public class AuditTrail implements AuditorAware<String>{

	@Override
	public Optional<String> getCurrentAuditor() {
		// TODO Auto-generated method stub
		InetAddress localhost;
		try {
			localhost = InetAddress.getLocalHost();
			return Optional.of((localhost.getHostAddress()).trim());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			return Optional.of(System.getProperty("user.name"));
		}
	}

}
