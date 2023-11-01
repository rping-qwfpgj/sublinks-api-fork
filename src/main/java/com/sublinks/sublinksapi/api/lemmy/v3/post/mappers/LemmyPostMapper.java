package com.sublinks.sublinksapi.api.lemmy.v3.post.mappers;

import com.sublinks.sublinksapi.api.lemmy.v3.post.models.Post;
import com.sublinks.sublinksapi.api.lemmy.v3.post.models.PostAggregates;
import com.sublinks.sublinksapi.post.dto.PostAggregate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LemmyPostMapper {
    @Mapping(target = "url", source = "post.linkUrl")
    @Mapping(target = "thumbnail_url", source = "post.linkThumbnailUrl")
    @Mapping(target = "published", source = "post.createdAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSX")
    @Mapping(target = "name", source = "post.title")
    @Mapping(target = "locked", constant = "false")
    @Mapping(target = "local", constant = "true")
    @Mapping(target = "language_id", source = "post.language.id")
    @Mapping(target = "featured_local", source = "post.featured")
    @Mapping(target = "featured_community", source = "post.featuredInCommunity")
    @Mapping(target = "embed_video_url", source = "post.linkVideoUrl")
    @Mapping(target = "embed_title", source = "post.linkTitle")
    @Mapping(target = "embed_description", source = "post.linkDescription")
    @Mapping(target = "community_id", source = "post.community.id")
    @Mapping(target = "body", source = "post.postBody")
    @Mapping(target = "updated", source = "post.updatedAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSX")
    @Mapping(target = "ap_id", source = "post.activityPubId")
    Post postToPost(com.sublinks.sublinksapi.post.dto.Post post);

    @Mapping(target = "upvotes", source = "postAggregate.upVoteCount")
    @Mapping(target = "score", source = "postAggregate.score")
    @Mapping(target = "published", source = "post.createdAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSX")
    @Mapping(target = "post_id", source = "postAggregate.post.id")
    @Mapping(target = "newest_comment_time_necro", source = "post.updatedAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSX")
    @Mapping(target = "newest_comment_time", source = "post.updatedAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSX")
    @Mapping(target = "hot_rank_active", source = "postAggregate.hotRankActive")
    @Mapping(target = "hot_rank", source = "postAggregate.hotRank")
    @Mapping(target = "featured_local", source = "post.featured")
    @Mapping(target = "featured_community", source = "post.featuredInCommunity")
    @Mapping(target = "downvotes", source = "postAggregate.downVoteCount")
    @Mapping(target = "comments", source = "postAggregate.commentCount")
    PostAggregates postAggregatesToPostAggregates(PostAggregate postAggregate);
}
