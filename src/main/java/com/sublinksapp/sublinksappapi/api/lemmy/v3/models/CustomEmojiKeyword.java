package com.sublinksapp.sublinksappapi.api.lemmy.v3.models;

import lombok.Builder;

@Builder
public record CustomEmojiKeyword(
        Long id,
        Long custom_emoji_id,
        String keyword
) {
}