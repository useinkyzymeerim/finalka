package com.finalka.service;

import com.finalka.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    ProductDTO findById(Long id);

    String delete(Long id);

    List<ProductDTO> findAll() throws Exception;
    ProductDTO update(ProductDTO productDTO);
}