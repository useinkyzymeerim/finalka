package com.finalka.controller;

import com.finalka.dto.CreateCartDto;
import com.finalka.dto.CreateProductOfShopDto;
import com.finalka.dto.ProductOfShopDto;
import com.finalka.service.ProductOfShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/productOfShop")
public class ProductOfShopController {
    private final ProductOfShopService productOfShopService;

    @PostMapping("/create")
    public ResponseEntity<CreateProductOfShopDto> save(@RequestBody CreateProductOfShopDto createProductOfShopDto){
        try {
            return new ResponseEntity<>(productOfShopService.createProduct(createProductOfShopDto), HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<CreateProductOfShopDto> updateProduct(@PathVariable Long productId, @RequestBody CreateProductOfShopDto productOfShopDto) {
        CreateProductOfShopDto updatedProduct = productOfShopService.updateProduct(productId, productOfShopDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productOfShopService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductOfShopDto> getProduct(@PathVariable Long productId) {
        ProductOfShopDto product = productOfShopService.getProduct(productId);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductOfShopDto>> getAllProducts() {
        List<ProductOfShopDto> products = productOfShopService.getAllProducts();
        return ResponseEntity.ok(products);
    }


        @GetMapping("/filterByType")
        public ResponseEntity<List<ProductOfShopDto>> filterProductsByType(@RequestParam String type) {
            List<ProductOfShopDto> filteredProducts = productOfShopService.filterProductsByType(type);
            return ResponseEntity.ok(filteredProducts);
        }

    @GetMapping("/filterByAvailability")
    public ResponseEntity<List<ProductOfShopDto>> filterProductsByAvailability(@RequestParam boolean inStock) {
        List<ProductOfShopDto> filteredProducts = productOfShopService.filterProductsByAvailability(inStock);
        return ResponseEntity.ok(filteredProducts);
    }
}
