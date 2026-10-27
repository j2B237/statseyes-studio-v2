package com.statseyes.studio.infrastructure.persistence.id;

import java.security.SecureRandom;
import java.util.UUID;

public final class UuidV7 {

    ////////////////////////////////
    ///     INSTANCE VARIABLES  ///
    //////////////////////////////

    private static final SecureRandom RANDOM = new SecureRandom();

    // Singleton
    private UuidV7(){}

    public static UUID generate() {
        long timestamp = System.currentTimeMillis() & 0xFFFFFFFFFFFFL;  // 48 bits
        long rand12 = RANDOM.nextInt(1 << 12);
        long msb = (timestamp << 16) | 0x7000L | rand12;
        long lsb = (RANDOM.nextLong() & 0x3FFFFFFFFFFFFFFFL) | 0x8000000000000000L;
        return new UUID(msb, lsb);
    }
}
