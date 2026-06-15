package com.team3imple.barosiksa.domain.tables.service;

import com.team3imple.barosiksa.domain.member.entity.Member;
import com.team3imple.barosiksa.domain.member.entity.Role;
import com.team3imple.barosiksa.domain.member.repository.MemberRepository;
import com.team3imple.barosiksa.domain.restaurants.entity.Restaurant;
import com.team3imple.barosiksa.domain.restaurants.repository.RestaurantRepository;
import com.team3imple.barosiksa.domain.tables.dto.TableCreateRequest;
import com.team3imple.barosiksa.domain.tables.dto.TableResponse;
import com.team3imple.barosiksa.domain.tables.dto.TableStatusUpdateRequest;
import com.team3imple.barosiksa.domain.tables.entity.RestaurantTable;
import com.team3imple.barosiksa.domain.tables.entity.TableStatus;
import com.team3imple.barosiksa.domain.tables.repository.TableRepository;
import com.team3imple.barosiksa.global.error.CustomException;
import com.team3imple.barosiksa.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TableService {

    private final TableRepository tableRepository;
    private final RestaurantRepository restaurantRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public List<TableResponse> getTables(Long restaurantId) {
        return tableRepository.findByRestaurantIdOrderByTableNumber(restaurantId)
                .stream()
                .map(TableResponse::new)
                .toList();
    }

    @Transactional
    public Long addTable(Long memberId, Long restaurantId, TableCreateRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        if (!restaurant.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        RestaurantTable table = RestaurantTable.builder()
                .restaurant(restaurant)
                .tableNumber(request.tableNumber())
                .capacity(request.capacity())
                .build();

        return tableRepository.save(table).getId();
    }

    @Transactional
    public void deleteTable(Long memberId, Long restaurantId, Long tableId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        if (!restaurant.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        tableRepository.delete(table);
    }

    @Transactional
    public void updateTableStatus(Long memberId, Long restaurantId, Long tableId,
                                   TableStatusUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        if (member.getRole() != Role.ADMIN
                && !restaurant.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        TableStatus newStatus;
        try {
            newStatus = TableStatus.valueOf(request.status().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (newStatus == TableStatus.RESERVED) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        table.updateStatus(newStatus);
        broadcastTableStatus(restaurantId, table);
    }

    /** 예약 생성/취소/완료 시 내부에서 호출 - 상태 변경 후 WebSocket 브로드캐스트 */
    @Transactional
    public void broadcastTableStatus(Long restaurantId, RestaurantTable table) {
        TableResponse updated = new TableResponse(table);
        messagingTemplate.convertAndSend(
                "/topic/restaurant/" + restaurantId + "/tables",
                updated
        );
    }
}
