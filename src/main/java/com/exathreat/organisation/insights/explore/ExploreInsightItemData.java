package com.exathreat.organisation.insights.explore;

import com.exathreat.common.jpa.entity.OrganisationDetection;
import com.exathreat.common.jpa.entity.OrganisationQuery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class ExploreInsightItemData {
	private String itemType;
	private OrganisationQuery organisationQuery;
	private OrganisationDetection organisationDetection;
	private String saveAction;	// 'new' | 'update'; flag to decide whether to insert or update in DB
}