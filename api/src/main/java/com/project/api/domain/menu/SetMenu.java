package com.project.api.domain.menu;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 세트 메뉴 Entity
 */
@Entity
@Table(name = "set_menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SetMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "set_id")
    private Long setId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "included_menu_id", nullable = false)
    private Menu includedMenu;

    @Builder
    private SetMenu(Menu menu, Menu includedMenu) {
        this.menu = menu;
        this.includedMenu = includedMenu;
    }
} 