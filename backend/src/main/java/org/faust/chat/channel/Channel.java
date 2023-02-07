package org.faust.chat.channel;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Channel {

    private final String name;

    private final boolean isOpen;

    private final UUID owner;
}