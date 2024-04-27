package com.finalka.controller;
import com.finalka.dto.MenuDTO;
import com.finalka.dto.ProductDTO;
import com.finalka.service.ProductService;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductsController {
    private final ProductService productService;

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "В базе есть доступные продукты",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Продукта нет")
    })
    @Operation(summary = "Роуд возвращает все не удаленные продукты")
    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> findAll() {
        try {
            return new ResponseEntity<>(productService.findAll(), HttpStatus.OK);
        } catch (NullPointerException nullPointerException) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Продукт создан успешно ",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDTO.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Продукт не был добавлен в базу")
    })
    @Operation(summary = "Роут для создание меню")
    @PostMapping
    public ResponseEntity<ProductDTO> save(@RequestBody ProductDTO productDTO){
        try {
            return new ResponseEntity<>(productService.save(productDTO), HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Продукт найден и успешно удален",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Продукт не найден")
    })
    @Operation(summary = "Роуд удаляет продукт по id")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        try {
            return new ResponseEntity<>(productService.delete(id), HttpStatus.OK);
        } catch (NullPointerException nullPointerException){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Продукт найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDTO.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Продукт не найден")
    })
    @Operation(summary = "Роуд для поиска продукт по id")
    @GetMapping("/{id}")

    public ResponseEntity<ProductDTO> findById(@PathVariable Long id){
        try {
            return new ResponseEntity<>(productService.findById(id), HttpStatus.OK);
        } catch (NullPointerException nullPointerException) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        }
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Продукт найден и успешно обновлен",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MenuDTO.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Продукт не найден")
    })
    @Operation(summary = "Роуд обновляет продукт, id отдела передается непосредственно в модели," +
            " по ней и идет поиск, важно, что бы все поля не были пустыми иначе засетит null, но передавать создателя и " +
            "обновляющего с датами не нужно, это делает бэк")
    @PutMapping
    public ResponseEntity<ProductDTO> update(@RequestBody ProductDTO productDTO){
        try {
            return new ResponseEntity<>(productService.update(productDTO), HttpStatus.OK);
        } catch (NullPointerException nullPointerException){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    }


