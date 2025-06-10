package com.project.api.adapter.in.web;

import com.project.core.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/delivery/api/test", produces = MediaType.APPLICATION_JSON_VALUE)
public class WebController {

  @GetMapping
  void abc() {
    StringUtils.print("asd");
  }
}
