package com.ine.cartografia.daoService;


import java.util.List;
import java.util.Map;



public interface IServiceDao {
	
	public List<Map<String, Object>> obtieneNombreTable();
	public List<Map<String, Object>> getList();
	public List<Map<String, Object>> getListRemotos();
	public List<Map<String, Object>> getListDiff();
	public List<Map<String, Object>> getexecuteSync();
	public void execute();
	public void executeSync();
	public void executeUpdate();
	public void executeTruncate();
	public void executeInset();
}
