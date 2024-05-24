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

    @PostMapping("/")
    public ResponseEntity<String> save(@RequestBody CreateProductOfShopDto createProductOfShopDto){
        try {
            productOfShopService.createProduct(createProductOfShopDto);
            return new ResponseEntity<>("Продукт успешно создана", HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>("Не удалось создать продукт", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<CreateProductOfShopDto> updateProduct(@PathVariable Long productId, @RequestBody CreateProductOfShopDto productOfShopDto) {
        CreateProductOfShopDto updatedProduct = productOfShopService.updateProduct(productId, productOfShopDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        try {
            productOfShopService.deleteProduct(productId);
            return new ResponseEntity<>("Товар успешно удален", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Не удалось удалить товар", HttpStatus.BAD_REQUEST);
        }
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

