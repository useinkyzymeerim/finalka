package com.finalka.controller;

import com.finalka.dto.*;
import com.finalka.exception.*;
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
import org.springframework.web.server.ResponseStatusException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Tag(name = "Authorized API", description = "Тут находятся все общие роуты для авторизованных пользователей")
@ApiResponses()
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
    private final CardService cardService;
    private final FavoriteService favoriteService;


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
    public List<RecipesDto> findAllRecipes() {
        return recipeService.findAll();
    }

    @Operation(summary = "Этот роут для добовление рецептов в меню")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = String.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })
    @PostMapping("/add-to-menu")
    public String addRecipeToMenu(@RequestBody RecipeAddProductDto menuRecipeRequestDto) {
        return recipeService.addRecipeToMenu(menuRecipeRequestDto);
    }
    @Operation(summary = "Этот роут возвращает отзыв к рецепту по его айди")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ReviewDTO.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })

    @GetMapping("/recipe/{recipeId}")
    public List<ReviewDTO> getReviewsByRecipeId(@PathVariable Long recipeId) {
        return reviewService.getReviewsByRecipeId(recipeId);
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
    public List<MenuDTO> findAllMenu(){
        try {
            return menuService.findAll();
        } catch (NullPointerException nullPointerException){
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Произошла ошибка при выполнении запроса поиска меню", e);
            throw new RuntimeException("Произошла ошибка при выполнении запроса поиска меню", e);
        }
    }
    @Operation(summary = "Получение меню с рецептами по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RecipesDto.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Меню с рецептами не найдено")
    })

    @GetMapping("/menu/{menuId}/recipes")
    public List<RecipesDto> getRecipesByMenuId(@PathVariable Long menuId) {
        return menuService.getRecipesByMenuId(menuId);
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Меню создан успешно ",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Long.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Меню не был добавлен в базу")
    })
    @Operation(summary = "Роут для создание меню")
    @PostMapping("/createMenu")
    public ResponseEntity<Long> saveMenu(@RequestBody CreateMenuDto menuDTO){
        try {
            CreateMenuDto savedMenuDTO = menuService.save(menuDTO);
            return new ResponseEntity<>(savedMenuDTO.getId(), HttpStatus.CREATED);
        } catch (MenuSaveException e) {
            log.error("Не удалось создать меню: {}");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
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
    @DeleteMapping("/deleteMenu/{menuId}")
    public String deleteMenu(@PathVariable Long id) {
        try {
            return menuService.delete(id);
        } catch (MenuNotFoundException e) {
            return "Меню не найдено: " + e.getMessage();
        } catch (Exception e) {
            return "Произошла ошибка при удалении меню: " + e.getMessage();
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
    @GetMapping("/menu/{menuId}")
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
    public MenuDTO updateMenu(@RequestBody MenuDTO menuDTO) {
        try {
            return menuService.update(menuDTO);
        } catch (MenuNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Произошла ошибка при обновлении меню", e);
        }
    }

    @Operation(summary = "Этот роут для обновления количество продуктов в корзине по айди корзины")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CartDetailDto.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })

    @PutMapping("/{cartId}/update-product-quantity")
    public CartDetailDto updateProductQuantityInCart(@PathVariable Long cartId,
                                                     @RequestBody UpdateProductQuantityDto updateProductQuantityDto) {
        try {
            return cartService.updateCart(cartId, updateProductQuantityDto);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (CartNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ProductNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось обновить корзину", e);
        }
    }
    @Operation(summary = "Этот роут возвращает корзину по айди")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CartDetailDto.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })
    @GetMapping("/cart/{cartId}")
    public CartDetailDto getCartById(@PathVariable Long cartId) {
        try {
            return cartService.findCartById(cartId);
        } catch (CartNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось получить корзину", e);
        }
    }
    @Operation(summary = "Этот роут для добовления продукта по айди в корзину ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = String.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })
    @PostMapping("/{cartId}/addProduct/{productId}")
    public String addProductToCart(@PathVariable Long cartId, @PathVariable Long productId) {
        try {
            cartService.addProductToCart(cartId, productId);
            return "Товар успешно добавлен в корзину";
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Не удалось добавить товар в корзину", e);
        }
    }

    @Operation(summary = "Этот роут для удаления продукта по айди из корзины")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = void.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })

    @DeleteMapping("/{cartId}/removeProduct/{productId}")
    public void removeProductFromCart(Long cartId, Long productId) {
        cartService.removeProductFromCart(cartId, productId);
    }


    @Operation(summary = "Этот роут для получения продукта в магазине по айди ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductOfShopDto.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })
    @GetMapping("/getProductById/{productIdInShop}")
    public ProductOfShopDto getProductOfShop(@PathVariable Long productId) {
        return productOfShopService.getProduct(productId);
    }


    @Operation(summary = "Этот роут возвращает все продукты в магазине ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductOfShopDto.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })

    @GetMapping("/allProductsOfShop")
    public List<ProductOfShopDto> getAllProductsOfShop() {
        return productOfShopService.getAllProducts();
    }


    @Operation(summary = "Этот роут возврощяет продукт по его имени ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductOfShopDto.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })
    @GetMapping("/findByName")
    public ResponseEntity<List<ProductOfShopDto>> getProductsByName(@RequestParam String productName) {
        List<ProductOfShopDto> productDtos = productOfShopService.getProductsByName(productName);
        return ResponseEntity.ok(productDtos);
    }


    @Operation(summary = "Этот роут возврощяет продукт по его типу ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductOfShopDto.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })
    @GetMapping("/filterByTypeProduct")
    public List<ProductOfShopDto> filterProductsByTypeProduct(@RequestParam String type) {
        String decodedType = URLDecoder.decode(type, StandardCharsets.UTF_8);
        return productOfShopService.filterProductsByType(decodedType);
    }


    @Operation(summary = "Этот роут возврощает продукты по наличию в магазине")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductOfShopDto.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })
    @GetMapping("/filterProductsByAvailability")
    public List<ProductOfShopDto> filterProductsByAvailability(@RequestParam boolean inStock) {
        return productOfShopService.filterProductsByAvailability(inStock);
    }


    @Operation(summary = "Этот роут для покупки")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = void.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })
    @PostMapping("/purchase/{cartId}")
    public void purchaseProducts(@PathVariable Long cartId) {
        purchaseService.purchaseProductsFromCart(cartId);
    }


    @Operation(summary = "Этот роут возвращает купленые  продукты по айди покупки")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PurchaseDetailsDto.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })

    @GetMapping("/{purchaseId}")
    public PurchaseDetailsDto getPurchaseWithProducts(@PathVariable Long id) {
        return purchaseService.getPurchaseWithProducts(id);
    }


    @Operation(summary = "Этот роут для обновления данных пользователя")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })
    @PutMapping("/update")
    public UserDto updateUser(@RequestBody UpdateUserDto updateUserDto) throws InvalidUserDataException, UnauthorizedException {
        return userService.updateUser(updateUserDto);
    }

    @Operation(summary = "Этот роут для запроса сброса пароля ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = void.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })

    @PostMapping("/password-reset-request")
    public void requestPasswordReset(@RequestBody ResetPasswordRequest request) {
        userService.generateResetToken(request.getEmail());
    }

    @Operation(summary = "Этот роут для сброса пароля ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = void.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })
    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        userService.resetPassword(resetPasswordDto.getToken(), resetPasswordDto.getNewPassword());
    }

    @Operation(summary = "Этот роут для привязки банковской карты")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = String.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })
    @PostMapping("/linkCard")
    public String linkCardToUser(@RequestBody CardLinkRequest request) {
        String result = cardService.linkCardToUser(request.getCardNumber(),
                request.getCardHolderName(), request.getExpiryDate(), request.getCvv());
        return result;
    }

    @Operation(summary = "Этот роут возвращает купленые  продукты по айди покупки")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CardDto.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })
    @GetMapping("/getCard")
    public List<CardDto> getLinkedCards() {
        List<CardDto> cards = cardService.getLinkedCards();
        return cards ;
    }



    @Operation(summary = "Этот роут для отвязки карты ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = String.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })
    @DeleteMapping("/unlinkCard/{cardId}")
    public String unlinkCard(@PathVariable Long cardId) {
        String result = cardService.unlinkCard(cardId);
        return result;
    }


    @Operation(summary = "Этот роут для добовления меню в избранное ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FavoriteDto.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })
    @PostMapping("/addFavoriteMenu")
    public FavoriteDto addFavorite(@RequestParam Long menuId) {
            FavoriteDto favoriteDto = favoriteService.addFavorite(menuId);
            return favoriteDto;
    }

    @Operation(summary = "Этот роут возвращает список меню в избранном")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная операция",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = MenuDetailsDto.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найдено")
    })

    @GetMapping("/getFavoriteMenu")
    public List<MenuDetailsDto> getFavoritesForCurrentUser() {
            log.info("Получение избранных меню для текущего пользователя");
            List<MenuDetailsDto> favoriteMenus = favoriteService.getFavoritesForCurrentUser();
            return favoriteMenus;

    }
}
