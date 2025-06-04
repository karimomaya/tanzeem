package com.tanzeem.common.client;

import com.tanzeem.common.dto.CountryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "lookup-service", url = "http://localhost:8087/api")
public interface LookupClient {
    @RequestMapping(method = RequestMethod.GET, value = "/countries/{code}")
    public CountryResponse getCountryByCode(@PathVariable String code);
}
