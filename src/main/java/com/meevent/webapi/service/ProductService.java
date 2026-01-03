package com.meevent.webapi.service;

import com.meevent.webapi.model.Product;
import com.meevent.webapi.repository.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private IProductRepository productRepository;

    public List<Product> listarProductos() {
        return productRepository.findAll();
    }
}
