package com.svtsygankov.test_system.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Entity {

    private long id;
    private String login;
    private String password;
    private Role role;

    @Override
    public long getId() {
        return id;
    }
}
