package com.tanzeem.lookup.repository;

import com.tanzeem.lookup.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
