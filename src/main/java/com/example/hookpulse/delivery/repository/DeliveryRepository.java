package com.example.hookpulse.delivery.repository;

import com.example.hookpulse.delivery.domain.Delivery;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends CrudRepository<Delivery, Long> {
}
