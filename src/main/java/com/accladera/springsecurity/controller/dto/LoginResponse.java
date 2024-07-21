package com.accladera.springsecurity.controller.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
}
