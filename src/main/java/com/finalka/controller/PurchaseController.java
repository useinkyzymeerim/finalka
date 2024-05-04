package com.finalka.controller;

import com.finalka.dto.PurchaseDTO;
import com.finalka.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping("/{cartId}")
    public ResponseEntity<String> purchaseProducts(@PathVariable Long cartId) {
        try {
            purchaseService.purchaseProductsFromCart(cartId);
            return ResponseEntity.ok("Покупка успешно завершена.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при выполнении покупки.");
        }
    }

}