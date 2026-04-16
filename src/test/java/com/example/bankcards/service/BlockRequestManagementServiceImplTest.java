package com.example.bankcards.service;

import com.example.bankcards.dto.request.ReasonRequest;
import com.example.bankcards.dto.response.CardBlockResponse;
import com.example.bankcards.entity.CardBlockRequestEntity;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.CardBlockRequestNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardBlockRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.details.CustomUserDetails;
import com.example.bankcards.service.admin.impl.BlockRequestManagementServiceImpl;
import com.example.bankcards.util.mapper.CardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BlockRequestManagementServiceImplTest {

    @Mock
    private CardBlockRepository cardBlockRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BlockRequestManagementServiceImpl blockRequestService;

    private UUID requestId;
    private UUID cardId;
    private UUID adminId;
    private UUID userId;
    private CustomUserDetails adminDetails;
    private CardBlockRequestEntity blockRequest;
    private CardEntity card;
    private UserEntity admin;
    private UserEntity user;
    private ReasonRequest reasonRequest;
    private CardBlockResponse blockResponse;

    @BeforeEach
    void setUp() {
        requestId = UUID.randomUUID();
        cardId = UUID.randomUUID();
        adminId = UUID.randomUUID();
        userId = UUID.randomUUID();

        adminDetails = mock(CustomUserDetails.class);
        when(adminDetails.getId()).thenReturn(adminId);

        admin = UserEntity.builder()
                .id(adminId)
                .username("admin")
                .build();

        user = UserEntity.builder()
                .id(userId)
                .username("user")
                .build();

        card = CardEntity.builder()
                .id(cardId)
                .cardStatus(CardEntity.CardStatus.ACTIVE)
                .build();

        blockRequest = CardBlockRequestEntity.builder()
                .id(requestId)
                .card(card)
                .requestedBy(user)
                .status(CardBlockRequestEntity.RequestStatus.PENDING)
                .reason("Card lost")
                .build();

        reasonRequest = new ReasonRequest("Approved by admin");

        blockResponse = CardBlockResponse.builder()
                .id(requestId)
                .status(CardBlockRequestEntity.RequestStatus.PENDING)
                .cardId(cardId)
                .build();
    }

    @Test
    void getAllCardBlockPendingRequests_ShouldReturnPage() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<CardBlockRequestEntity> requestPage = new PageImpl<>(List.of(blockRequest));
        when(cardBlockRepository.findAllByStatus(CardBlockRequestEntity.RequestStatus.PENDING, pageable))
                .thenReturn(requestPage);
        when(cardMapper.toDto(blockRequest)).thenReturn(blockResponse);


        Page<CardBlockResponse> result = blockRequestService.getAllCardBlockPendingRequests(pageable);


        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(cardBlockRepository).findAllByStatus(CardBlockRequestEntity.RequestStatus.PENDING, pageable);
    }

    @Test
    void approveBlockRequest_ShouldApproveAndBlockCard() {

        when(cardBlockRepository.findById(requestId)).thenReturn(Optional.of(blockRequest));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(cardRepository.save(card)).thenReturn(card);
        when(cardBlockRepository.save(blockRequest)).thenReturn(blockRequest);


        blockRequestService.approveBlockRequest(requestId, reasonRequest, adminDetails);


        assertThat(card.getCardStatus()).isEqualTo(CardEntity.CardStatus.BLOCKED);
        assertThat(blockRequest.getStatus()).isEqualTo(CardBlockRequestEntity.RequestStatus.APPROVED);
        assertThat(blockRequest.getProcessedBy()).isEqualTo(admin);
        assertThat(blockRequest.getProcessedAt()).isNotNull();
        assertThat(blockRequest.getReason()).isEqualTo("Approved by admin");

        verify(cardRepository).save(card);
        verify(cardBlockRepository).save(blockRequest);
    }

    @Test
    void approveBlockRequest_WhenRequestNotFound_ShouldThrowException() {

        when(cardBlockRepository.findById(requestId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> blockRequestService.approveBlockRequest(requestId, reasonRequest, adminDetails))
                .isInstanceOf(CardBlockRequestNotFoundException.class);

        verify(cardRepository, never()).save(any());
    }

    @Test
    void approveBlockRequest_WhenRequestNotPending_ShouldThrowException() {

        blockRequest.setStatus(CardBlockRequestEntity.RequestStatus.APPROVED);
        when(cardBlockRepository.findById(requestId)).thenReturn(Optional.of(blockRequest));


        assertThatThrownBy(() -> blockRequestService.approveBlockRequest(requestId, reasonRequest, adminDetails))
                .isInstanceOf(CardBlockRequestNotFoundException.class);

        verify(cardRepository, never()).save(any());
    }

    @Test
    void approveBlockRequest_WhenAdminNotFound_ShouldThrowException() {

        when(cardBlockRepository.findById(requestId)).thenReturn(Optional.of(blockRequest));
        when(userRepository.findById(adminId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> blockRequestService.approveBlockRequest(requestId, reasonRequest, adminDetails))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void rejectBlockRequest_ShouldRejectRequest() {

        when(cardBlockRepository.findById(requestId)).thenReturn(Optional.of(blockRequest));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(cardBlockRepository.save(blockRequest)).thenReturn(blockRequest);


        blockRequestService.rejectBlockRequest(requestId, reasonRequest, adminDetails);


        assertThat(blockRequest.getStatus()).isEqualTo(CardBlockRequestEntity.RequestStatus.REJECTED);
        assertThat(blockRequest.getProcessedBy()).isEqualTo(admin);
        assertThat(blockRequest.getProcessedAt()).isNotNull();
        assertThat(blockRequest.getReason()).isEqualTo("Approved by admin");


        assertThat(card.getCardStatus()).isEqualTo(CardEntity.CardStatus.ACTIVE);

        verify(cardRepository, never()).save(any());
        verify(cardBlockRepository).save(blockRequest);
    }

    @Test
    void rejectBlockRequest_WhenRequestNotFound_ShouldThrowException() {

        when(cardBlockRepository.findById(requestId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> blockRequestService.rejectBlockRequest(requestId, reasonRequest, adminDetails))
                .isInstanceOf(CardBlockRequestNotFoundException.class);
    }
}