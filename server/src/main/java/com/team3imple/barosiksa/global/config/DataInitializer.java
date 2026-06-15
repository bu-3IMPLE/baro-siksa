package com.team3imple.barosiksa.global.config;

import com.team3imple.barosiksa.domain.ingredients.entity.Ingredient;
import com.team3imple.barosiksa.domain.ingredients.repository.IngredientRepository;
import com.team3imple.barosiksa.domain.member.entity.Member;
import com.team3imple.barosiksa.domain.member.entity.Role;
import com.team3imple.barosiksa.domain.member.repository.MemberRepository;
import com.team3imple.barosiksa.domain.menus.entity.Menu;
import com.team3imple.barosiksa.domain.menus.repository.MenuRepository;
import com.team3imple.barosiksa.domain.restaurants.entity.Restaurant;
import com.team3imple.barosiksa.domain.restaurants.entity.RestaurantCategory;
import com.team3imple.barosiksa.domain.restaurants.repository.RestaurantRepository;
import com.team3imple.barosiksa.domain.tables.entity.RestaurantTable;
import com.team3imple.barosiksa.domain.tables.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

/**
 * 애플리케이션 시작 시 샘플 데이터를 자동으로 삽입한다.
 * 이미 회원 데이터가 존재하면 실행되지 않는다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final RestaurantRepository restaurantRepository;
    private final TableRepository tableRepository;
    private final MenuRepository menuRepository;
    private final IngredientRepository ingredientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (memberRepository.count() > 0) {
            log.info("[DataInitializer] 기존 데이터가 존재합니다. 샘플 데이터 삽입을 건너뜁니다.");
            return;
        }

        log.info("[DataInitializer] 샘플 데이터 삽입을 시작합니다...");

        List<Member> members = seedMembers();
        seedIngredients();
        seedRestaurants(members);

        log.info("[DataInitializer] 샘플 데이터 삽입이 완료되었습니다.");
    }

    // ─────────────────────────────────────────────────────────────
    // 회원
    // ─────────────────────────────────────────────────────────────
    private List<Member> seedMembers() {
        String userPw  = passwordEncoder.encode("test1234");
        String ownerPw = passwordEncoder.encode("owner1234");
        String adminPw = passwordEncoder.encode("admin1234");

        List<Member> members = memberRepository.saveAll(List.of(
            Member.builder().username("관리자").email("admin@barosiksa.com").password(adminPw).role(Role.ADMIN).build(),
            Member.builder().username("김사장").email("owner1@barosiksa.com").password(ownerPw).role(Role.OWNER).build(),
            Member.builder().username("이사장").email("owner2@barosiksa.com").password(ownerPw).role(Role.OWNER).build(),
            Member.builder().username("박사장").email("owner3@barosiksa.com").password(ownerPw).role(Role.OWNER).build(),
            Member.builder().username("홍길동").email("user1@test.com").password(userPw).role(Role.USER).build(),
            Member.builder().username("김민지").email("user2@test.com").password(userPw).role(Role.USER).build(),
            Member.builder().username("이준호").email("user3@test.com").password(userPw).role(Role.USER).build(),
            Member.builder().username("박소연").email("user4@test.com").password(userPw).role(Role.USER).build(),
            Member.builder().username("최현우").email("user5@test.com").password(userPw).role(Role.USER).build(),
            Member.builder().username("정다은").email("user6@test.com").password(userPw).role(Role.USER).build()
        ));
        log.info("[DataInitializer] 회원 {}명 생성 완료", members.size());
        return members;
    }

    // ─────────────────────────────────────────────────────────────
    // 재료
    // ─────────────────────────────────────────────────────────────
    private void seedIngredients() {
        ingredientRepository.saveAll(List.of(
            Ingredient.builder().name("밀").isAllergenic(true).build(),
            Ingredient.builder().name("우유").isAllergenic(true).build(),
            Ingredient.builder().name("달걀").isAllergenic(true).build(),
            Ingredient.builder().name("새우").isAllergenic(true).build(),
            Ingredient.builder().name("돼지고기").isAllergenic(true).build(),
            Ingredient.builder().name("쇠고기").isAllergenic(true).build(),
            Ingredient.builder().name("닭고기").isAllergenic(true).build(),
            Ingredient.builder().name("대두").isAllergenic(true).build(),
            Ingredient.builder().name("땅콩").isAllergenic(true).build(),
            Ingredient.builder().name("치즈").isAllergenic(true).build(),
            Ingredient.builder().name("쌀").isAllergenic(false).build(),
            Ingredient.builder().name("마늘").isAllergenic(false).build(),
            Ingredient.builder().name("양파").isAllergenic(false).build(),
            Ingredient.builder().name("감자").isAllergenic(false).build(),
            Ingredient.builder().name("토마토").isAllergenic(false).build(),
            Ingredient.builder().name("참기름").isAllergenic(false).build(),
            Ingredient.builder().name("고추").isAllergenic(false).build(),
            Ingredient.builder().name("버터").isAllergenic(true).build()
        ));
    }

    // ─────────────────────────────────────────────────────────────
    // 식당 + 테이블 + 메뉴
    // ─────────────────────────────────────────────────────────────
    private void seedRestaurants(List<Member> members) {
        Member owner1 = members.get(1); // 김사장
        Member owner2 = members.get(2); // 이사장
        Member owner3 = members.get(3); // 박사장

        // ── 1. 백석 순댓국 (KOREAN) ──────────────────────────────
        Restaurant r1 = save(Restaurant.builder()
                .member(owner1).name("백석 순댓국").category(RestaurantCategory.KOREAN)
                .address("충남 천안시 서북구 백석로 62")
                .latitude(new BigDecimal("36.84100")).longitude(new BigDecimal("127.18250"))
                .phoneNumber("041-555-1001")
                .description("30년 전통의 진한 국물 순댓국집. 직접 만든 순대와 수육이 인기입니다.")
                .openTime(LocalTime.of(7, 0)).closeTime(LocalTime.of(21, 0))
                .closedDays("매주 일요일").build());
        saveTables(r1, "A1,2", "A2,2", "A3,4", "B1,4", "B2,6", "B3,6");
        saveMenus(r1,
                menu("순댓국", 8000, "30년 비법 사골 육수에 순대, 머릿고기, 선지를 듬뿍"),
                menu("뼈해장국", 9000, "진한 도가니 사골 국물. 숙취 해소에 최고"),
                menu("순대 한 접시", 9000, "쫄깃한 찹쌀 순대. 내장과 함께 제공"),
                menu("수육", 15000, "부드러운 보쌈 수육. 새우젓과 함께"),
                menu("소주", 5000, "참이슬/처음처럼 선택 가능"),
                menu("막걸리", 4000, "서울 탁주 막걸리"));

        // ── 2. 목포 갈치조림 (KOREAN) ────────────────────────────
        Restaurant r2 = save(Restaurant.builder()
                .member(owner1).name("목포 갈치조림").category(RestaurantCategory.KOREAN)
                .address("충남 천안시 서북구 성환읍 성환로 15")
                .latitude(new BigDecimal("36.83800")).longitude(new BigDecimal("127.18550"))
                .phoneNumber("041-555-1002")
                .description("신선한 제철 갈치를 사용한 갈치조림 전문점. 밑반찬이 풍성합니다.")
                .openTime(LocalTime.of(11, 0)).closeTime(LocalTime.of(22, 0))
                .breakStartTime(LocalTime.of(15, 0)).breakEndTime(LocalTime.of(17, 0))
                .closedDays("매주 월요일").build());
        saveTables(r2, "A1,2", "A2,2", "A3,4", "B1,4", "B2,6", "VIP,8");
        saveMenus(r2,
                menu("갈치조림 (소)", 28000, "제철 은갈치 2토막 조림. 무·고추 포함"),
                menu("갈치조림 (대)", 45000, "제철 은갈치 4토막 조림. 2인 이상 추천"),
                menu("갈치구이", 18000, "담백하게 구운 은갈치 1마리"),
                menu("조기구이", 14000, "참조기 2마리 구이"),
                menu("된장찌개", 8000, "직접 담근 된장으로 끓인 구수한 찌개"),
                menu("공기밥", 1000, "추가 공기밥"));

        // ── 3. 천안 삼겹살 거리 (KOREAN) ─────────────────────────
        Restaurant r3 = save(Restaurant.builder()
                .member(owner1).name("천안 삼겹살 거리").category(RestaurantCategory.KOREAN)
                .address("충남 천안시 동남구 신부동 432-1")
                .latitude(new BigDecimal("36.83650")).longitude(new BigDecimal("127.18200"))
                .phoneNumber("041-555-1003")
                .description("국내산 생삼겹살 직화구이. 된장찌개 무한리필.")
                .openTime(LocalTime.of(16, 0)).closeTime(LocalTime.of(23, 59)).build());
        saveTables(r3, "A1,4", "A2,4", "A3,4", "B1,6", "B2,6", "B3,8");
        saveMenus(r3,
                menu("국내산 삼겹살 (200g)", 14000, "국내산 냉장 삼겹살. 숯불 직화구이"),
                menu("국내산 목살 (200g)", 14000, "국내산 냉장 목살. 마늘과 함께"),
                menu("차돌박이 (150g)", 13000, "얇게 썬 차돌박이. 상추쌈 추가 무료"),
                menu("냉면", 7000, "물냉면/비빔냉면 선택"),
                menu("소주", 5000, "참이슬"),
                menu("맥주", 6000, "카스/하이트 선택"));

        // ── 4. 한옥 칼국수 (KOREAN) ──────────────────────────────
        Restaurant r4 = save(Restaurant.builder()
                .member(owner1).name("한옥 칼국수").category(RestaurantCategory.KOREAN)
                .address("충남 천안시 서북구 백석1길 28")
                .latitude(new BigDecimal("36.84250")).longitude(new BigDecimal("127.18450"))
                .phoneNumber("041-555-1004")
                .description("직접 반죽한 손칼국수와 만두. 사골 육수를 매일 새로 끓입니다.")
                .openTime(LocalTime.of(10, 0)).closeTime(LocalTime.of(20, 0))
                .breakStartTime(LocalTime.of(14, 30)).breakEndTime(LocalTime.of(16, 0))
                .closedDays("매주 화요일").build());
        saveTables(r4, "A1,2", "A2,4", "A3,4", "B1,6", "B2,6");
        saveMenus(r4,
                menu("손칼국수", 9000, "직접 반죽 밀면. 사골 멸치 혼합 육수"),
                menu("들깨 칼국수", 10000, "고소한 들깨가루 칼국수. 진국 국물"),
                menu("해물 칼국수", 12000, "오징어·새우·조개가 들어간 시원한 해물 국물"),
                menu("왕만두 (5개)", 7000, "직접 빚은 손만두. 고기·김치 선택"),
                menu("비빔국수", 8000, "새콤달콤 양념장 비빔국수"));

        // ── 5. 스시 오마카세 천안 (JAPANESE) ─────────────────────
        Restaurant r5 = save(Restaurant.builder()
                .member(owner2).name("스시 오마카세 천안").category(RestaurantCategory.JAPANESE)
                .address("충남 천안시 서북구 두정동 1011")
                .latitude(new BigDecimal("36.84000")).longitude(new BigDecimal("127.18700"))
                .phoneNumber("041-555-2001")
                .description("제철 해산물로 구성한 오마카세 코스. 일본 본토 스타일의 정통 스시.")
                .openTime(LocalTime.of(11, 30)).closeTime(LocalTime.of(22, 0))
                .breakStartTime(LocalTime.of(15, 0)).breakEndTime(LocalTime.of(17, 0))
                .closedDays("매주 월요일").build());
        saveTables(r5, "C1,2", "C2,2", "C3,4", "VIP,6");
        saveMenus(r5,
                menu("런치 오마카세 (8피스)", 45000, "시즌 제철 생선 8종. 점심 한정 코스"),
                menu("디너 오마카세 (15피스)", 95000, "최상급 제철 재료 15종 풀 코스. 미소시루 포함"),
                menu("연어 니기리 (2피스)", 12000, "노르웨이산 생연어 니기리"),
                menu("참치 뱃살 니기리 (2피스)", 18000, "일본산 혼마구로 대뱃살"),
                menu("성게 군함말이", 15000, "북해도산 생 성게"),
                menu("사케(日本酒) 1홉", 12000, "다이긴죠 차가운 사케"));

        // ── 6. 라멘 하우스 (JAPANESE) ────────────────────────────
        Restaurant r6 = save(Restaurant.builder()
                .member(owner2).name("라멘 하우스").category(RestaurantCategory.JAPANESE)
                .address("충남 천안시 서북구 쌍용동 700")
                .latitude(new BigDecimal("36.83500")).longitude(new BigDecimal("127.18400"))
                .phoneNumber("041-555-2002")
                .description("돈코츠·쇼유·미소 세 가지 라멘. 차슈는 직접 저온 조리합니다.")
                .openTime(LocalTime.of(11, 0)).closeTime(LocalTime.of(21, 30))
                .closedDays("명절 당일").build());
        saveTables(r6, "A1,1", "A2,1", "A3,1", "B1,2", "B2,4", "B3,4");
        saveMenus(r6,
                menu("돈코츠 라멘", 11000, "12시간 우린 돼지뼈 육수. 차슈 2장 기본"),
                menu("쇼유 라멘", 10000, "닭육수 간장 베이스. 담백하고 깔끔"),
                menu("미소 라멘", 11000, "홋카이도 미소 된장. 버터 토핑 포함"),
                menu("탄탄멘", 12000, "마라 베이스 참깨 소스. 매콤한 대만식"),
                menu("차슈 추가", 3000, "직접 만든 저온 조리 차슈 2장 추가"),
                menu("교자 (5개)", 6000, "바삭한 군만두. 라멘과 세트 추천"));

        // ── 7. 나고야 돈카츠 (JAPANESE) ─────────────────────────
        Restaurant r7 = save(Restaurant.builder()
                .member(owner2).name("나고야 돈카츠").category(RestaurantCategory.JAPANESE)
                .address("충남 천안시 동남구 안서동 325")
                .latitude(new BigDecimal("36.84300")).longitude(new BigDecimal("127.18000"))
                .phoneNumber("041-555-2003")
                .description("두툼한 흑돼지 등심 돈카츠. 자체 제작 소스가 시그니처입니다.")
                .openTime(LocalTime.of(11, 30)).closeTime(LocalTime.of(21, 0))
                .breakStartTime(LocalTime.of(15, 0)).breakEndTime(LocalTime.of(17, 0))
                .closedDays("매주 수요일").build());
        saveTables(r7, "A1,2", "A2,2", "A3,4", "B1,4", "B2,6");
        saveMenus(r7,
                menu("흑돼지 등심 돈카츠", 16000, "제주 흑돼지 등심 180g. 수제 소스와 함께"),
                menu("안심 돈카츠", 15000, "부드러운 안심 160g. 어린이 추천"),
                menu("새우 돈카츠 (4미)", 17000, "왕새우 4마리 튀김. 타르타르 소스 포함"),
                menu("카츠동", 13000, "돈카츠 덮밥. 반숙 계란 소스"),
                menu("모둠 돈카츠", 22000, "등심+안심+새우 모둠. 2인 추천"));

        // ── 8. 파스타 공화국 (WESTERN) ───────────────────────────
        Restaurant r8 = save(Restaurant.builder()
                .member(owner3).name("파스타 공화국").category(RestaurantCategory.WESTERN)
                .address("충남 천안시 서북구 백석대길 1")
                .latitude(new BigDecimal("36.83900")).longitude(new BigDecimal("127.18100"))
                .phoneNumber("041-555-3001")
                .description("이탈리아 유학파 셰프의 정통 파스타와 피자. 와인 페어링 추천.")
                .openTime(LocalTime.of(11, 30)).closeTime(LocalTime.of(22, 0))
                .breakStartTime(LocalTime.of(15, 30)).breakEndTime(LocalTime.of(17, 30))
                .closedDays("매주 월요일").build());
        saveTables(r8, "T1,2", "T2,2", "T3,4", "T4,4", "T5,6", "VIP,8");
        saveMenus(r8,
                menu("봉골레 파스타", 16000, "싱싱한 바지락과 화이트 와인 소스. 알리오 올리오 스타일"),
                menu("카르보나라", 17000, "생크림 없는 정통 로마식 카르보나라. 판체타 사용"),
                menu("아라비아타", 15000, "매콤한 토마토 소스. 엔초비 풍미 추가"),
                menu("마르게리타 피자", 20000, "나폴리 스타일. 산 마르자노 토마토·모짜렐라·바질"),
                menu("콰트로 포르마지", 22000, "4종 치즈 피자. 꿀 소스와 함께"),
                menu("티라미수", 9000, "마스카포네 티라미수. 에스프레소 적신 레이디핑거"),
                menu("와인 (하우스)", 15000, "이탈리아 하우스 와인 1잔"));

        // ── 9. 브루클린 버거 (WESTERN) ───────────────────────────
        Restaurant r9 = save(Restaurant.builder()
                .member(owner3).name("브루클린 버거").category(RestaurantCategory.WESTERN)
                .address("충남 천안시 서북구 성성동 555")
                .latitude(new BigDecimal("36.83700")).longitude(new BigDecimal("127.18600"))
                .phoneNumber("041-555-3002")
                .description("미국식 수제 버거와 크리스피 감자튀김. 맥주와 함께하면 최고.")
                .openTime(LocalTime.of(11, 0)).closeTime(LocalTime.of(23, 0)).build());
        saveTables(r9, "A1,2", "A2,2", "A3,4", "B1,4", "B2,6", "B3,6");
        saveMenus(r9,
                menu("클래식 버거", 13000, "180g 수제 패티·체다·토마토·피클. 기본 세트"),
                menu("더블 스모크 버거", 17000, "더블 패티에 스모크 체다. 베이컨 추가"),
                menu("어니언링 버거", 15000, "패티 위에 튀긴 양파링. 바베큐 소스"),
                menu("감자튀김 (L)", 5000, "두꺼운 웨지 감자튀김. 사워크림 딥소스"),
                menu("어니언링", 6000, "바삭한 어니언링 8개. 머스타드 소스"),
                menu("밀크쉐이크", 7000, "바닐라/초콜릿/딸기 선택"),
                menu("생맥주", 6000, "크래프트 생맥주 500ml"));

        // ── 10. 카페 봄날 (CAFE) ─────────────────────────────────
        Restaurant r10 = save(Restaurant.builder()
                .member(owner3).name("카페 봄날").category(RestaurantCategory.CAFE)
                .address("충남 천안시 서북구 백석공원로 8")
                .latitude(new BigDecimal("36.84150")).longitude(new BigDecimal("127.18350"))
                .phoneNumber("041-555-3003")
                .description("자체 로스팅 원두 핸드드립 커피. 시즌 케이크와 디저트 전문.")
                .openTime(LocalTime.of(8, 0)).closeTime(LocalTime.of(22, 0)).build());
        saveTables(r10, "W1,2", "W2,2", "W3,2", "W4,4", "S1,4", "S2,6");
        saveMenus(r10,
                menu("아메리카노", 5000, "자체 로스팅 에티오피아 싱글 오리진"),
                menu("라떼", 6000, "풍부한 우유 거품의 플랫 화이트 스타일"),
                menu("핸드드립 (V60)", 9000, "당일 추출 싱글 오리진 핸드드립. 테이스팅 노트 제공"),
                menu("시즌 케이크", 8000, "당일 제조 시즌 케이크 1조각. 재고 소진 시 품절"),
                menu("쑥 크림 라떼", 7500, "국내산 쑥 시럽과 우유 거품의 시그니처 메뉴"),
                menu("플레인 스콘", 5500, "버터 향 가득한 플레인 스콘. 클로티드 크림 포함"),
                menu("얼그레이 티", 6000, "다즐링 향 가득한 얼그레이 포트 티"));

        log.info("[DataInitializer] 식당 10곳 생성 완료");
    }

    // ─────────────────────────────────────────────────────────────
    // 헬퍼
    // ─────────────────────────────────────────────────────────────
    private Restaurant save(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    /** "번호,수용인원" 형식으로 테이블을 일괄 생성. 예) "A1,4" */
    private void saveTables(Restaurant restaurant, String... specs) {
        for (String spec : specs) {
            String[] parts = spec.split(",");
            tableRepository.save(
                RestaurantTable.builder()
                    .restaurant(restaurant)
                    .tableNumber(parts[0])
                    .capacity(Integer.parseInt(parts[1]))
                    .build()
            );
        }
    }

    private void saveMenus(Restaurant restaurant, Menu... menus) {
        for (Menu menu : menus) {
            menuRepository.save(
                Menu.builder()
                    .restaurant(restaurant)
                    .name(menu.getName())
                    .price(menu.getPrice())
                    .description(menu.getDescription())
                    .build()
            );
        }
    }

    private Menu menu(String name, int price, String description) {
        return Menu.builder().name(name).price(price).description(description).build();
    }
}
