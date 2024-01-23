package com.fiap.techchallenge.followup.gateway.port;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CachePort {

    void clearAllCaches();

    Set<String> getAllKeysByNamePattern(String keyNamePattern);

    List<Object> getAllDataByKeys(Set<String> keys);

    void setKeyWithExpirationTimeInMinutes(String key, Object value, Integer timeExpiration);

    void setKeyWithoutExpirationTime(String key, Object value);

    void setMultiKeyWithoutExpirationTime(Map<? extends String, ? extends Object> keyValueList);

}
