package com.itworksonmymachine.eduamp.repository;

import com.itworksonmymachine.eduamp.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {

}