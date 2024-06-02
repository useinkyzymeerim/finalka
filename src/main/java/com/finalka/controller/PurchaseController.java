package com.finalka.controller;

import com.finalka.dto.*;
import com.finalka.service.ProductService;
import com.finalka.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @Operation(summary = "Этот роут для покупки")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = MenuWithRecipeDTO.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })
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

    @Operation(summary = "Этот роут возвращает купленые  продукты по айди покупки")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = MenuWithRecipeDTO.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDetailsDto> getPurchaseWithProducts(@PathVariable Long id) {
        PurchaseDetailsDto purchaseDetailsDto = purchaseService.getPurchaseWithProducts(id);

        if (purchaseDetailsDto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(purchaseDetailsDto);
    }
}