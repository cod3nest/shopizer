package com.salesmanager.core.business.utils;

import com.salesmanager.core.model.merchant.MerchantStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CacheUtils {

	private final static String KEY_DELIMITER = "_";

	private final Cache serviceCache;

	public void putInCache(Object object, String keyName) throws Exception {
		serviceCache.put(keyName, object);
	}
	

	public Object getFromCache(String keyName) throws Exception {

		ValueWrapper vw = serviceCache.get(keyName);
		if(vw!=null) {
			return vw.get();
		}
		
		return null;
		
	}
	
	public List<String> getCacheKeys(MerchantStore store) throws Exception {
		
		  net.sf.ehcache.Cache cacheImpl = (net.sf.ehcache.Cache) serviceCache.getNativeCache();
		  List<String> returnKeys = new ArrayList<String>();
		  for (Object key: cacheImpl.getKeys()) {
		    
			  
				try {
					String sKey = (String)key;
					
					// a key should be <storeId>_<rest of the key>
					int delimiterPosition = sKey.indexOf(KEY_DELIMITER);
					
					if(delimiterPosition>0 && Character.isDigit(sKey.charAt(0))) {
					
						String keyRemaining = sKey.substring(delimiterPosition+1);
						returnKeys.add(keyRemaining);
					
					}

				} catch (Exception e) {
					LOGGER.equals("key " + key + " cannot be converted to a String or parsed");
				}  
		  }

		return returnKeys;
	}
	
	public void shutDownCache() throws Exception {
		
	}
	
	public void removeFromCache(String keyName) throws Exception {
		serviceCache.evict(keyName);
	}
	
	public void removeAllFromCache(MerchantStore store) throws Exception {
		  net.sf.ehcache.Cache cacheImpl = (net.sf.ehcache.Cache) serviceCache.getNativeCache();
		  for (Object key: cacheImpl.getKeys()) {
				try {
					String sKey = (String)key;
					
					// a key should be <storeId>_<rest of the key>
					int delimiterPosition = sKey.indexOf(KEY_DELIMITER);
					
					if(delimiterPosition>0 && Character.isDigit(sKey.charAt(0))) {
						serviceCache.evict(key);
					}

				} catch (Exception e) {
					LOGGER.equals("key " + key + " cannot be converted to a String or parsed");
				}  
		  }
	}
	


}
