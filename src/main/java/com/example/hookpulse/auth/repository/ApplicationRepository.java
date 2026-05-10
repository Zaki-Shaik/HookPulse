package com.example.hookpulse.auth.repository;

import com.example.hookpulse.auth.domain.Application;
import org.springframework.data.repository.CrudRepository;

public interface ApplicationRepository extends CrudRepository<Application, Long> {
}
