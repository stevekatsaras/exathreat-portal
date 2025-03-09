package com.exathreat.profile;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.exathreat.common.jpa.entity.SystemUser;
import com.exathreat.common.jpa.repository.SystemUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class SystemUserService {

	@Autowired
	private SystemUserRepository systemUserRepository;

	@Transactional(readOnly = true)
	public void initSystemUser(SystemUserForm systemUserForm, ModelMap modelMap) throws Exception {
		systemUserForm.setSystemUser(systemUserRepository.findByEmailAddress((String) modelMap.get("emailAddress")));
	}
	
	@Transactional(readOnly = false)
	public void doSystemUser(SystemUserForm systemUserForm, ModelMap modelMap)throws Exception {
		SystemUser systemUserDto = systemUserForm.getSystemUser();
		
		SystemUser systemUser = systemUserRepository.findByEmailAddress((String) modelMap.get("emailAddress"));
		systemUser.setDateFormat(systemUserDto.getDateFormat());
		systemUser.setTimeFormat(systemUserDto.getTimeFormat());
		systemUser.setTimezone(systemUserDto.getTimezone());
		systemUser.setModified(ZonedDateTime.now(ZoneOffset.UTC));
		systemUserRepository.saveAndFlush(systemUser);
	}
	
}