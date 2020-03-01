package com.itworksonmymachine.eduamp.unit.examples;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itworksonmymachine.eduamp.model.Order;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class OrderControllerTest {

  private List<Order> orders = null;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  private void setup() {
    this.orders = Stream.of(
        new Order(101, "Mobile", 1, 15000),
        new Order(102, "laptop", 1, 75000)
    ).collect(Collectors.toList());
  }

  @Test
  public void should_allowAddOrder_always() throws Exception {
    String ordersJson = new ObjectMapper().writeValueAsString(this.orders);
    mockMvc.perform(post("/PlaceOrder")
        .content(ordersJson)
        .contentType("application/json")).andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(ordersJson))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  public void should_allowGetOrders_always() throws Exception {
    mockMvc.perform(get("/getOrders")
        .contentType("application/json")).andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(orders)))
        .andDo(document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

}