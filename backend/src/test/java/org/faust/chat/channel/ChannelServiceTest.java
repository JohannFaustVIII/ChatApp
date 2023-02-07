package org.faust.chat.channel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

class ChannelServiceTest {

    private ChannelService testedChannelService;

    @BeforeEach
    void setUp() {
        testedChannelService = new ChannelService();
    }

    @Test
    void returnNoChannelAtTheBegin() {
        // when
        Flux<Channel> channels = testedChannelService.getChannels(UUID.randomUUID());

        // then
        StepVerifier
                .create(channels)
                .verifyComplete();
    }

    @Test
    void returnAddedChannel() {
        // given
        testedChannelService.addChannel(new Channel("channel_1", true, UUID.randomUUID()));

        // when
        Flux<Channel> channels = testedChannelService.getChannels(UUID.randomUUID());

        // then
        StepVerifier
                .create(channels)
                .expectNextMatches(channel -> channel.getName().equals("channel_1"))
                .expectComplete()
                .verify();
    }
    @Test
    void returnTwoAddedChannels() {
        // given
        testedChannelService.addChannel(new Channel("channel_1", true, UUID.randomUUID()));
        testedChannelService.addChannel(new Channel("channel_2", true, UUID.randomUUID()));


        // when
        Flux<Channel> channels = testedChannelService.getChannels(UUID.randomUUID());

        // then
        StepVerifier
                .create(channels)
                .expectNextMatches(channel -> channel.getName().equals("channel_1"))
                .expectNextMatches(channel -> channel.getName().equals("channel_2"))
                .expectComplete()
                .verify();
    }

    @Test
    void returnOpenAndClosedChannelsForOwner() {
        // given
        testedChannelService.addChannel(new Channel("channel_1", true, UUID.fromString("11111111-0000-0000-0000-000000000000")));
        testedChannelService.addChannel(new Channel("channel_2", true, UUID.fromString("11111111-0000-0000-0000-000000000000")));
        testedChannelService.addChannel(new Channel("channel_3", false, UUID.fromString("11111111-0000-0000-0000-000000000000")));


        // when
        Flux<Channel> channels = testedChannelService.getChannels(UUID.fromString("11111111-0000-0000-0000-000000000000"));

        // then
        StepVerifier
                .create(channels)
                .expectNextMatches(channel -> channel.getName().equals("channel_1"))
                .expectNextMatches(channel -> channel.getName().equals("channel_2"))
                .expectNextMatches(channel -> channel.getName().equals("channel_3"))
                .expectComplete()
                .verify();
    }

    @Test
    void returnOnlyOpenChannelsForOtherUser() {
        // given
        testedChannelService.addChannel(new Channel("channel_1", true, UUID.fromString("11111111-0000-0000-0000-000000000000")));
        testedChannelService.addChannel(new Channel("channel_2", true, UUID.fromString("11111111-0000-0000-0000-000000000000")));
        testedChannelService.addChannel(new Channel("channel_3", false, UUID.fromString("11111111-0000-0000-0000-000000000000")));


        // when
        Flux<Channel> channels = testedChannelService.getChannels(UUID.fromString("22222222-0000-0000-0000-000000000000"));

        // then
        StepVerifier
                .create(channels)
                .expectNextMatches(channel -> channel.getName().equals("channel_1"))
                .expectNextMatches(channel -> channel.getName().equals("channel_2"))
                .expectComplete()
                .verify();
    }


}