package com.finalka.service.impl;

import com.finalka.dto.FavoriteDto;
import com.finalka.dto.MenuDTO;
import com.finalka.dto.MenuDetailsDto;
import com.finalka.entity.Favorite;
import com.finalka.entity.Menu;
import com.finalka.entity.User;
import com.finalka.exception.MenuNotFoundException;
import com.finalka.exception.UserNotFoundException;
import com.finalka.repo.FavoriteRepo;
import com.finalka.repo.MenuRepo;
import com.finalka.repo.UserRepo;
import com.finalka.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepo favoriteRepo;
    private final MenuRepo menuRepo;
    private final UserRepo userRepo;

    @Transactional
    public FavoriteDto addFavorite(Long menuId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Menu menu = menuRepo.findById(menuId)
                .orElseThrow(() -> new MenuNotFoundException("Menu not found"));

        if (favoriteRepo.existsByUserAndMenu(user, menu)) {
            throw new RuntimeException("Это меню уже добавлено в избранное");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setMenu(menu);
        favorite.setAddedAt(new Timestamp(System.currentTimeMillis()));

        Favorite savedFavorite = favoriteRepo.save(favorite);

        return new FavoriteDto(savedFavorite.getId(), menu.getId(), savedFavorite.getAddedAt());
    }

    @Override
    public List<MenuDetailsDto> getFavoritesForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();
        if (currentUser == null) {
            log.error("Пользователь не найден по токену");
            throw new UsernameNotFoundException("Пользователь не найден по токену");
        }

        User user = userRepo.findByUsername(currentUser)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        List<Favorite> favorites = favoriteRepo.findByUser(user);
        return favorites.stream()
                .map(favorite -> MenuDetailsDto.builder()
                        .id(favorite.getMenu().getId())
                        .nameOfMenu(favorite.getMenu().getNameOfMenu())
                        .createdBy(favorite.getMenu().getCreatedBy())
                        .createdAt(favorite.getMenu().getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}