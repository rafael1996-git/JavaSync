package com.ine.cartografia.daoService;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ine.cartografia.controlador.Controlador;
import com.ine.cartografia.model.tableSyncTemporal;

@Repository
public class ServiceImpl implements IServiceDao {
	tableSyncTemporal op = new tableSyncTemporal();

	@Autowired
	private JdbcTemplate jdbcTemplate;
	public final String STATEMENTS_EXECUTE_SQL[] = { "--Inicio Query", "create extension if not exists postgres_fdw",
			"drop server if exists servidor100 cascade", "grant usage on FOREIGN DATA WRAPPER postgres_fdw to postgres",
			"CREATE SERVER servidor100 FOREIGN DATA WRAPPER postgres_fdw OPTIONS (dbname 'control_fwd', host '172.19.110.100', port '5432')",
			"CREATE USER MAPPING for postgres SERVER servidor100 OPTIONS (user 'control', password 'control')",
			"drop SCHEMA if exists public_fwd cascade",
			"CREATE SCHEMA IF NOT EXISTS public_fwd",
			"IMPORT FOREIGN SCHEMA public FROM SERVER servidor100 INTO public_fwd",

			"---Fin" };

	@Override
	public List<Map<String, Object>> obtieneNombreTable() {
		try {
			String STATEMENTS_EXECUTE_NAME_TABLE_SQL[] = {

					"SELECT table_name \r\n" + "FROM information_schema.tables \r\n"
							+ "WHERE table_schema='public' \r\n" + "AND table_type='BASE TABLE'" };

			for (String stringSql : STATEMENTS_EXECUTE_NAME_TABLE_SQL) {

				System.out
						.println("  resultado STATEMENTS_EXECUTE_NAME_TABLE_SQL : para el query: [" + stringSql + "]");
				return this.jdbcTemplate.queryForList(stringSql);

			}
		} catch (Exception e) {
			e.getCause();
			System.out.println(
					" con resultado STATEMENTS_EXECUTE_NAME_TABLE_SQL  ERROR: para el query: [" + e.getCause() + "]");
			return null;
		}

		return null;

	}

	@Override
	public List<Map<String, Object>> getList() {
		return this.jdbcTemplate.queryForList("SELECT *  FROM public." + Controlador.NAME_TABLA + " ");
	}

	@Override
	public List<Map<String, Object>> getListRemotos() {
		return this.jdbcTemplate.queryForList("SELECT *  FROM public_fwd." + Controlador.NAME_TABLA + " ");

	}

	@Override
	public List<Map<String, Object>> getListDiff() {
		return this.jdbcTemplate.queryForList("SELECT *  FROM public." + Controlador.NAME_TABLA
				+ " EXCEPT SELECT * FROM public_fwd." + Controlador.NAME_TABLA + "");

	}

	@Override
	public void execute() {

		try {
			for (String stringSql : STATEMENTS_EXECUTE_SQL) {

				System.out.println(" con resultado: para el query: [" + stringSql + "]");

				this.jdbcTemplate.execute(stringSql);
			}
		} catch (Exception e) {
			e.getCause();
			System.out.println(" con resultado ERROR: para el query: [" + e.getCause() + "]");

		}

	}

	@Override
	public void executeSync() {
		try {
			final String STATEMENTS_EXECUTE_SYNC_SQL[] = { "--Inicio Query",
					"CREATE SCHEMA IF NOT EXISTS tablaDiff;",
					"CREATE  TABLE IF NOT EXISTS tablaDiff." + Controlador.NAME_TABLA + "diff AS SELECT *  FROM public."
							+ Controlador.NAME_TABLA + " EXCEPT SELECT * FROM public_fwd." + Controlador.NAME_TABLA
							+ "",

					"---Fin" };
			for (String stringSql : STATEMENTS_EXECUTE_SYNC_SQL) {

				System.out.println("  resultado executeSync : para el query: [" + stringSql + "]");

				this.jdbcTemplate.execute(stringSql);
			}
		} catch (Exception e) {
			e.getCause();
			System.out.println(" con resultado executeSync  ERROR: para el query: [" + e.getCause() + "]");

		}

	}

	@Override
	public List<Map<String, Object>> getexecuteSync() {
		return this.jdbcTemplate.queryForList("SELECT column_name\r\n" + "  FROM information_schema.columns\r\n"
				+ " WHERE table_schema = 'public'\r\n" + "   AND table_name   = '" + Controlador.NAME_TABLA + "'");

	}

	@Override
	public void executeUpdate() {
		try {
			StringBuffer originalString = new StringBuffer(Controlador.COLUMNAS.toString());
			originalString.replace(0, 1, "");
			String Columnas = originalString.toString();
			Columnas = Columnas.replaceFirst(".$", "");
			System.out.println("==========" + Columnas);
			

			 String STATEMENTS_EXECUTE_UPDATE_SQL[] = { "--Inicio Query",
					
					"UPDATE public_fwd." + Controlador.NAME_TABLA + "\r\n" + "SET " + Columnas + "\r\n" + "FROM tablaDiff."
							+ Controlador.NAME_TABLA + "diff t \r\n" + "WHERE t." + Controlador.COLUMN.get(0)
							+ " = public_fwd."+Controlador.NAME_TABLA+"." + Controlador.COLUMN.get(0) + "",
							
					

					"---Fin" };
			 Controlador.COLUMNAS = new ArrayList<String>();

			for (String stringSql : STATEMENTS_EXECUTE_UPDATE_SQL) {

				System.out.println("  resultado executeUpdate : para el query: [" + stringSql + "]");

				this.jdbcTemplate.execute(stringSql);
			}

		} catch (Exception e) {
			e.getCause();
			System.out.println(" con resultado executeUpdate  ERROR: para el query: [" + e.getCause() + "]");

		}

	}

	@Override
	public void executeTruncate() {
		this.jdbcTemplate.execute("DROP TABLE IF EXISTS tablaDiff."+Controlador.NAME_TABLA+"diff");
		
	}

	@Override
	public void executeInset() {
		try {
		
			
			StringBuffer originalCOLUMN = new StringBuffer(Controlador.COLUMN.toString());
			originalCOLUMN.replace(0, 1, "");
			String Columna = originalCOLUMN.toString();
			Columna = Columna.replaceFirst(".$", "");
			System.out.println("==========" + Columna);

			 String STATEMENTS_EXECUTE_INSERT_SQL[] = { "--Inicio Query",
					
							
							"insert into public_fwd."+Controlador.NAME_TABLA+" ("+Columna+")\r\n"
							+ "select * from tablaDiff."+Controlador.NAME_TABLA+"diff",

					"---Fin" };
			 Controlador.COLUMN = new ArrayList<String>();

			for (String stringSql : STATEMENTS_EXECUTE_INSERT_SQL) {

				System.out.println("  resultado STATEMENTS_EXECUTE_INSERT_SQL : para el query: [" + stringSql + "]");

				this.jdbcTemplate.execute(stringSql);
			}

		} catch (Exception e) {
			e.getCause();
			System.out.println(" con resultado STATEMENTS_EXECUTE_INSERT_SQL  ERROR: para el query: [" + e.getCause() + "]");

		}		
	}



}
