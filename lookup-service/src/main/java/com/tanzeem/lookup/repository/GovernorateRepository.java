package com.tanzeem.lookup.repository;

import com.tanzeem.lookup.entity.Governorate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GovernorateRepository extends JpaRepository<Governorate, Long> {
    List<Governorate> findByCountryId(Long countryId);
}