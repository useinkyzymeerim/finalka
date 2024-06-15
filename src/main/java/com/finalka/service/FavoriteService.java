package com.finalka.service;

import com.finalka.dto.FavoriteDto;
import com.finalka.dto.MenuDTO;
import com.finalka.dto.MenuDetailsDto;

import java.util.List;

public interface FavoriteService {
    FavoriteDto addFavorite(Long menuId) ;
    List<MenuDetailsDto> getFavoritesForCurrentUser() ;
}
