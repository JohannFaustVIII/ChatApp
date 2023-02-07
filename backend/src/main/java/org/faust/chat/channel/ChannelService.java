package org.faust.chat.channel;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChannelService {

    private List<Channel> channels = new ArrayList<>(); // TODO: to be replaced by repository, for now it is made to be used for TDD

    public Flux<Channel> getChannels(UUID readerUUID) {
        return Flux.fromIterable(channels)
                .filter(channel -> channel.isOpen() || channel.getOwner().equals(readerUUID));
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
    }
}
