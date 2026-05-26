package com.team3imple.barosiksa.domain.menus.repository;

import com.team3imple.barosiksa.domain.menus.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long>, MenuRepositoryCustom {
}
