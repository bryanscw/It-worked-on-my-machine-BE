package com.itworksonmymachine.eduamp.controller;

import com.itworksonmymachine.eduamp.model.Order;
import com.itworksonmymachine.eduamp.repository.OrderRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

  @Autowired
  private OrderRepository repository;

  @PostMapping("/PlaceOrder")
  public List<Order> saveOrder(@RequestBody List<Order> orders) {
    repository.saveAll(orders);
    return orders;
  }

  @GetMapping("/getOrders")
  public List<Order> getOrders() {
    return repository.findAll();
  }
  
}
