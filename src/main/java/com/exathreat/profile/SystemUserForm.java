package com.exathreat.profile;

import javax.validation.Valid;

import com.exathreat.common.jpa.entity.SystemUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class SystemUserForm {
	@Valid
	private SystemUser systemUser;
}