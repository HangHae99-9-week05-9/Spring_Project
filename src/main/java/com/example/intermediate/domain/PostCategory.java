package com.example.intermediate.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PostCategory {
    GAME("게임"),
    CLOTHE("의류");

    private  String value;

    PostCategory(String value) {
        this.value = value;
    }

    public static PostCategory fromCode(String dbData){
        return Arrays.stream(PostCategory.values())
                .filter(v -> v.getValue().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("포스트 카테고리에 %s가 존재하지 않습니다.", dbData)));
    }
}

