package com.finalka.controller;

import com.finalka.dto.CreateMenuDto;
import com.finalka.dto.MenuDTO;
import com.finalka.dto.MenuWithRecipeDTO;
import com.finalka.dto.RecipesDto;
import com.finalka.entity.Products;
import com.finalka.enums.Units;
import com.finalka.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "WeeklyMenu", description = "Тут находятся все роуты связанные для работы с меню")
@RequiredArgsConstructor
@RestController
@RequestMapping("/menus")
public class MenuController {
    private final MenuService menuService;

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "В базе есть доступные меню",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = MenuDTO.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Меню нет")
    })
    @Operation(summary = "Роут возвращает все не удаленные меню")
    @GetMapping("/all")
    public ResponseEntity<List<MenuDTO>> findAll(){
        try {
            return new ResponseEntity<>(menuService.findAll(), HttpStatus.OK);
        } catch (NullPointerException nullPointerException){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "Получение меню с рецептами по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = MenuWithRecipeDTO.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Меню с рецептами не найдено")
    })

    @GetMapping("/{menuId}/recipes")
    public ResponseEntity<List<RecipesDto>> getRecipesByMenuId(@PathVariable Long menuId) {
        List<RecipesDto> recipes = menuService.getRecipesByMenuId(menuId);
        return ResponseEntity.ok(recipes);
    }
    @Operation(summary = "Этот роут возвращает количество всех продуктов в одном меню по айди")
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

    @GetMapping("/{menuId}/requiredProducts")
    public ResponseEntity<?> getRequiredProductsForMenu(@PathVariable Long menuId) {
        try {
            Map<Products, Map.Entry<Integer, Units>> productQuantityMap = menuService.calculateRequiredProductsForMenu(menuId);

            List<Map<String, Object>> productQuantityList = new ArrayList<>();
            for (Map.Entry<Products, Map.Entry<Integer, Units>> entry : productQuantityMap.entrySet()) {
                Map<String, Object> productInfo = new HashMap<>();
                productInfo.put("productName", entry.getKey().getProductName());
                productInfo.put("quantity", entry.getValue().getKey());
                productInfo.put("unit", entry.getValue().getValue().toString());
                productQuantityList.add(productInfo);
            }

            return new ResponseEntity<>(productQuantityList, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Failed to calculate required products: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Меню создан успешно ",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MenuDTO.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Меню не был добавлен в базу")
    })
    @Operation(summary = "Роут для создание меню")
    @PostMapping
    public ResponseEntity<String> save(@RequestBody CreateMenuDto menuDTO){
        try {
            menuService.save(menuDTO);
            return new ResponseEntity<>("Меню успешно создана", HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>("Не удалось создать меню", HttpStatus.BAD_REQUEST);
        }
    }
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Меню найден и успешно удален",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Меню не найден")
    })
    @Operation(summary = "Роут удаляет меню по id")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        try {
            return new ResponseEntity<>(menuService.delete(id), HttpStatus.OK);
        } catch (NullPointerException nullPointerException){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Меню найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MenuDTO.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Меню не найден")
    })
    @Operation(summary = "Роуд для поиска меню по id")
    @GetMapping("/{id}")
    public ResponseEntity<MenuDTO> findById(@PathVariable Long id){
        try {
            return new ResponseEntity<>(menuService.findById(id), HttpStatus.OK);
        } catch (NullPointerException nullPointerException){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Меню найден и успешно обновлен",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MenuDTO.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Меню не найден")
    })
    @Operation(summary = "Роут обновляет меню, id отдела передается непосредственно в модели," +
            " по ней и идет поиск, важно, что бы все поля не были пустыми иначе засетит null, но передавать создателя и " +
            "обновляющего с датами не нужно, это делает бэк")
    @PutMapping
    public ResponseEntity<MenuDTO> update(@RequestBody MenuDTO menuDTO){
        try {
            return new ResponseEntity<>(menuService.update(menuDTO), HttpStatus.OK);
        } catch (NullPointerException nullPointerException){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

