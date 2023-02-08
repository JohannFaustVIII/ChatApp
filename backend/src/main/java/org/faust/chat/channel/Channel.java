package org.faust.chat.channel;


import com.datastax.oss.driver.api.core.uuid.Uuids;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Channel {

    private final UUID id = Uuids.timeBased(); // TODO: this can be harmful when reading from repository, to be fixed later

    private final String name;

    private final boolean isOpen;

    private final UUID owner;
}