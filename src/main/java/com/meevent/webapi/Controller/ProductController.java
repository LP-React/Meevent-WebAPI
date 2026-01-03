package com.meevent.webapi.Controller;

import com.meevent.webapi.model.Product;
import com.meevent.webapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> obtenerTodos() {
        return ResponseEntity.ok(productService.listarProductos());
    }
}
