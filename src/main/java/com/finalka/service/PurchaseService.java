package com.finalka.service;

import com.finalka.dto.PurchaseDTO;
import com.finalka.dto.PurchaseDetailsDto;

public interface PurchaseService {
   void purchaseProductsFromCart(Long cartId);
   PurchaseDetailsDto getPurchaseWithProducts(Long purchaseId);
}
