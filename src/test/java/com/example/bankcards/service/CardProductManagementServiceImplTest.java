package com.example.bankcards.service;

import com.example.bankcards.dto.request.CreateCardProductRequest;
import com.example.bankcards.dto.response.CardProductResponse;
import com.example.bankcards.entity.CardProductEntity;
import com.example.bankcards.exception.CardProductNotFound;
import com.example.bankcards.repository.CardProductRepository;
import com.example.bankcards.service.admin.impl.CardProductManagementServiceImpl;
import com.example.bankcards.util.mapper.CardProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardProductManagementServiceImplTest {

    @Mock
    private CardProductRepository repository;

    @Mock
    private CardProductMapper mapper;

    @InjectMocks
    private CardProductManagementServiceImpl cardProductService;

    private UUID productId;
    private CreateCardProductRequest createRequest;
    private CardProductEntity productEntity;
    private CardProductResponse productResponse;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();

        createRequest = new CreateCardProductRequest(
                "Gold Card",
                "Premium gold card",
                true
        );

        productEntity = CardProductEntity.builder()
                .id(productId)
                .cardName("Gold Card")
                .description("Premium gold card")
                .isActive(true)
                .build();

        productResponse = new CardProductResponse(
                productId,
                "Gold Card",
                "Premium gold card",
                true
        );
    }

    @Test
    void createCard_ShouldCreateNewCardProduct() {
         
        when(mapper.toEntity(createRequest)).thenReturn(productEntity);
        when(repository.save(productEntity)).thenReturn(productEntity);
        when(mapper.toDto(productEntity)).thenReturn(productResponse);

         
        CardProductResponse result = cardProductService.createCard(createRequest);

         
        assertThat(result).isNotNull();
        assertThat(result.cardName()).isEqualTo("Gold Card");
        assertThat(result.isActive()).isTrue();
        verify(repository).save(productEntity);
    }

    @Test
    void getAllProducts_ShouldReturnActiveProducts() {
         
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardProductEntity> productPage = new PageImpl<>(List.of(productEntity));
        when(repository.findAllByIsActive(true, pageable)).thenReturn(productPage);
        when(mapper.toDto(productEntity)).thenReturn(productResponse);

         
        Page<CardProductResponse> result = cardProductService.getAllProducts(true, pageable);

         
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(repository).findAllByIsActive(true, pageable);
    }

    @Test
    void deactivateCardProduct_ShouldDeactivateProduct() {
         
        when(repository.findById(productId)).thenReturn(Optional.of(productEntity));

         
        cardProductService.deactivateCardProduct(productId);

         
        assertThat(productEntity.getIsActive()).isFalse();
        verify(repository, never()).save(any());
    }

    @Test
    void deactivateCardProduct_WhenAlreadyInactive_ShouldDoNothing() {
         
        productEntity.setIsActive(false);
        when(repository.findById(productId)).thenReturn(Optional.of(productEntity));

         
        cardProductService.deactivateCardProduct(productId);

         
        verify(repository, never()).save(any());
    }

    @Test
    void deactivateCardProduct_WhenProductNotFound_ShouldThrowException() {
         
        when(repository.findById(productId)).thenReturn(Optional.empty());

         
        assertThatThrownBy(() -> cardProductService.deactivateCardProduct(productId))
                .isInstanceOf(CardProductNotFound.class);
    }

    @Test
    void activateCardProduct_ShouldActivateProduct() {
         
        productEntity.setIsActive(false);
        when(repository.findById(productId)).thenReturn(Optional.of(productEntity));

         
        cardProductService.activateCardProduct(productId);

         
        assertThat(productEntity.getIsActive()).isTrue();
        verify(repository, never()).save(any());
    }

    @Test
    void activateCardProduct_WhenAlreadyActive_ShouldDoNothing() {
         
        productEntity.setIsActive(true);
        when(repository.findById(productId)).thenReturn(Optional.of(productEntity));

         
        cardProductService.activateCardProduct(productId);

         
        verify(repository, never()).save(any());
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() {
         
        when(repository.existsById(productId)).thenReturn(true);
        doNothing().when(repository).deleteById(productId);

         
        cardProductService.deleteProduct(productId);

         
        verify(repository).deleteById(productId);
    }

    @Test
    void deleteProduct_WhenProductNotFound_ShouldThrowException() {
         
        when(repository.existsById(productId)).thenReturn(false);

         
        assertThatThrownBy(() -> cardProductService.deleteProduct(productId))
                .isInstanceOf(CardProductNotFound.class);

        verify(repository, never()).deleteById(any());
    }
}