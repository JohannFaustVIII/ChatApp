package org.faust.chat.channel;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChannelService {

    private List<Channel> channels = new ArrayList<>(); // TODO: to be replaced by repository, for now it is made to be used for TDD

    public Flux<Channel> getChannels(UUID readerId) {
        return Flux.fromIterable(channels)
                .filter(channel -> channel.isOpen() || channel.getOwner().equals(readerId));
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public Flux<Channel> getUserChannels(UUID ownerId) {
        return Flux.fromIterable(channels)
                .filter(channel ->  channel.getOwner().equals(ownerId));
    }

    public void removeChannel(UUID removerId, UUID channelId) {
        channels.stream()
                .filter(channel -> channel.getId().equals(channelId) && channel.getOwner().equals(removerId))
                .findFirst()
                .ifPresentOrElse(channels::remove, () -> {
                    throw new ChannelServiceUnownedRemovalException();
                });
    }
}
