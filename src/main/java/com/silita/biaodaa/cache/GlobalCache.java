package com.silita.biaodaa.cache;

import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.model.CertBasic;
import com.silita.biaodaa.model.TbSafetyCertificate;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局缓存.
 *
 */
public class GlobalCache {

	protected Logger logger = Logger.getLogger(getClass());

	private static GlobalCache globalCache = new GlobalCache();

	private Map<String,List<Map<String, Object>>> analyzeRangeByFieldMap;

	private Map<String,CertBasic> certBasicMap;

	private Map<String,TbSafetyCertificate> safetyCertMap;

	private Map<String,PageInfo> personMap;

	private Map<String,Long> vaildTime;

	private Map<String,String> provinceMap;

	private GlobalCache() {
		analyzeRangeByFieldMap = new ConcurrentHashMap<>();
		certBasicMap = new ConcurrentHashMap<>();
		safetyCertMap = new ConcurrentHashMap<>();
		personMap = new ConcurrentHashMap<>();
		vaildTime = new ConcurrentHashMap<>();
		provinceMap = new ConcurrentHashMap<>();
	}


	public static GlobalCache getGlobalCache() {
		return globalCache;
	}

	public Map<String, List<Map<String, Object>>> getAnalyzeRangeByFieldMap() {
		return analyzeRangeByFieldMap;
	}

	public void setAnalyzeRangeByFieldMap(Map<String, List<Map<String, Object>>> analyzeRangeByFieldMap) {
		this.analyzeRangeByFieldMap = analyzeRangeByFieldMap;
	}

	public Map<String, CertBasic> getCertBasicMap() {
		return certBasicMap;
	}

	public void setCertBasicMap(Map<String, CertBasic> certBasicMap) {
		this.certBasicMap = certBasicMap;
	}

	public Map<String, TbSafetyCertificate> getSafetyCertMap() {
		return safetyCertMap;
	}

	public void setSafetyCertMap(Map<String, TbSafetyCertificate> safetyCertMap) {
		this.safetyCertMap = safetyCertMap;
	}

	public Map<String, Long> getVaildTime() {
		return vaildTime;
	}

	public void setVaildTime(Map<String, Long> vaildTime) {
		this.vaildTime = vaildTime;
	}

	public Map<String, PageInfo> getPersonMap() {
		return personMap;
	}

	public void setPersonMap(Map<String, PageInfo> personMap) {
		this.personMap = personMap;
	}

	public Map<String, String> getProvinceMap() {
		return provinceMap;
	}

	public void setProvinceMap(Map<String, String> provinceMap) {
		this.provinceMap = provinceMap;
	}
}
