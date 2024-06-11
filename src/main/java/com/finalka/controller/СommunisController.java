package com.finalka.controller;

import com.finalka.dto.*;
import com.finalka.service.*;
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

import java.util.List;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Tag(name = "MagicMenu", description = "Тут находятся все общие роуты для авторизованных пользователей")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comminis")
public class СommunisController {

    private final RecipesService recipeService;
    private final ReviewService reviewService;
    private final MenuService menuService;
    private final CartService cartService;
    private final ProductOfShopService productOfShopService;
    private final PurchaseService purchaseService;
    private final UserService userService;
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "В базе есть доступные рецепты",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RecipesDto.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Рецепта нет")
    })
    @Operation(summary = "Роут возвращает все не удаленные рецепты")
    @GetMapping("/allRecipes")
    public ResponseEntity<List<RecipesDto>> findAllRecipes() {
        try {
            return new ResponseEntity<>(recipeService.findAll(), HttpStatus.OK);
        } catch (NullPointerException nullPointerException) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "Этот роут для добовление рецептов в меню")
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
    @PostMapping("/add-to-menu")
    public ResponseEntity<String> addRecipeToMenu(@RequestBody RecipeAddProductDto menuRecipeRequestDto) {
        try {
            recipeService.addRecipeToMenu(menuRecipeRequestDto.getMenuId(), menuRecipeRequestDto.getRecipeId());
            return ResponseEntity.ok("Рецепт успешно добавлен в меню");
        } catch (Exception e) {
            log.error("Ошибка при добавлении рецепта в меню", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось добавить рецепт в меню");
        }
    }

    @Operation(summary = "Этот роут возвращает отзыв к рецепту по его айди")
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

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByRecipeId(@PathVariable Long recipeId) {
        try {
            List<ReviewDTO> reviews = reviewService.getReviewsByRecipeId(recipeId);
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
    @GetMapping("/allMenu")
    public ResponseEntity<List<MenuDTO>> findAllMenu(){
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
    @PostMapping("/createMenu")
    public ResponseEntity<String> saveMenu(@RequestBody CreateMenuDto menuDTO){
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
    @DeleteMapping("/{menuId}")
    public ResponseEntity<String> deleteMenu(@PathVariable Long id){
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
    @Operation(summary = "Роут для поиска меню по id")
    @GetMapping("/{MenuId}")
    public ResponseEntity<MenuDTO> findMenuById(@PathVariable Long id){
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
    @Operation(summary = "Роут обновляет меню, id меню передается непосредственно в модели," +
            " по ней и идет поиск, важно, что бы все поля не были пустыми иначе засетит null, но передавать создателя и " +
            "обновляющего с датами не нужно, это делает бэк")
    @PutMapping("/updateMenu")
    public ResponseEntity<MenuDTO> updateMenu(@RequestBody MenuDTO menuDTO){
        try {
            return new ResponseEntity<>(menuService.update(menuDTO), HttpStatus.OK);
        } catch (NullPointerException nullPointerException){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Этот роут для создания корзины в магазине ")
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
    @PostMapping("/saveCart")
    public ResponseEntity<String> saveCart(@RequestBody CreateCartDto createCartDto){
        try {
            cartService.createCart(createCartDto);
            return new ResponseEntity<>("Корзина успешно создана", HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>("Не удалось создать корзину", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Этот роут для обновления количество продуктов в корзине по айди корзины")
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

    @PutMapping("/{cartId}/update-product-quantity")
    public ResponseEntity<CartDetailDto> updateProductQuantityInCart(@PathVariable Long cartId,
                                                                     @RequestBody UpdateProductQuantityDto updateProductQuantityDto) {
        try {
            CartDetailDto updatedCart = cartService.updateCart(cartId, updateProductQuantityDto);
            if (updatedCart != null) {
                return ResponseEntity.ok(updatedCart);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @Operation(summary = "Этот роут возвращает корзину по айди")
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
    @GetMapping("/{cartId}")
    public ResponseEntity<CartDetailDto> getCartById(@PathVariable Long cartId) {
        CartDetailDto cartDto = cartService.findCartById(cartId);
        if (cartDto != null) {
            return new ResponseEntity<>(cartDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
    @Operation(summary = "Этот роут для добовления продукта по айди в корзину ")
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
    @PostMapping("/{cartId}/addProduct/{productId}")
    public ResponseEntity<String> addProductToCart(@PathVariable Long cartId, @PathVariable Long productId) {
        try {
            cartService.addProductToCart(cartId, productId);
            return new ResponseEntity<>("Товар успешно добавлен в корзину", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Не удалось добавить товар в корзину", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Этот роут для удаления продукта по айди из корзины")
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

    @DeleteMapping("/{cartId}/removeProduct/{productId}")
    public ResponseEntity<String> removeProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        try {
            cartService.removeProductFromCart(cartId, productId);
            return new ResponseEntity<>("Товар успешно удален из корзины", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Не удалось удалить товар из корзины", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Этот роут для получения продукта в магазине по айди ")
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
    @GetMapping("/{productIdInShop}")
    public ResponseEntity<ProductOfShopDto> getProductOfShop(@PathVariable Long productId) {
        ProductOfShopDto product = productOfShopService.getProduct(productId);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Этот роут возвращает все продукты в магазине ")
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

    @GetMapping("/allProductsOfShop")
    public ResponseEntity<List<ProductOfShopDto>> getAllProductsOfShop() {
        List<ProductOfShopDto> products = productOfShopService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Этот роут возврощяет продукт по его имени ")
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
    @GetMapping("/findByNameProduct")
    public ResponseEntity<ProductOfShopDto> getProductByName(@RequestParam String productName) {
        ProductOfShopDto productDto = productOfShopService.getProductByName(productName);
        return ResponseEntity.ok(productDto);
    }

    @Operation(summary = "Этот роут возврощяет продукт по его типу ")
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
    @GetMapping("/filterByTypeProduct")
    public ResponseEntity<List<ProductOfShopDto>> filterProductsByTypeProduct(@RequestParam String type) {
        List<ProductOfShopDto> filteredProducts = productOfShopService.filterProductsByType(type);
        return ResponseEntity.ok(filteredProducts);
    }
    @Operation(summary = "Этот роут возврощает продукты по наличию в магазине")
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
    @GetMapping("/filterProductsByAvailability")
    public ResponseEntity<List<ProductOfShopDto>> filterProductsByAvailability(@RequestParam boolean inStock) {
        List<ProductOfShopDto> filteredProducts = productOfShopService.filterProductsByAvailability(inStock);
        return ResponseEntity.ok(filteredProducts);
    }
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

    @GetMapping("/{purchaseId}")
    public ResponseEntity<PurchaseDetailsDto> getPurchaseWithProducts(@PathVariable Long id) {
        PurchaseDetailsDto purchaseDetailsDto = purchaseService.getPurchaseWithProducts(id);

        if (purchaseDetailsDto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(purchaseDetailsDto);
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestBody UpdateUserDto updateUserDto) {
        UserDto updatedUser = userService.updateUser(updateUserDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/password-reset-request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody ResetPasswordRequest request) {
        userService.generateResetToken(request.getEmail());
        return ResponseEntity.ok("Запрос на сброс пароля успешно обработан.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        try {
            userService.resetPassword(resetPasswordDto.getToken(), resetPasswordDto.getNewPassword());
            return ResponseEntity.ok("Пароль успешно обновлен.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
