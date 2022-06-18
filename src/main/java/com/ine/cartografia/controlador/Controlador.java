package com.ine.cartografia.controlador;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.valves.rewrite.Substitution.StaticElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ine.cartografia.dao.IDatos;
import com.ine.cartografia.daoService.IServiceDao;
import com.ine.cartografia.model.tableSyncTemporal;

@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST })

@Controller

public class Controlador {
	
	@Value("${spring.datasource.url}")
    private String username;

	public static String NAME_TABLA = null;
//	public static String  COLUMN = null;
	public static List<String> COLUMN = new ArrayList<String>();
	public static String VALOR = null;
	public static List<String> COLUMNAS = new ArrayList<String>();
	@Autowired
	private IServiceDao UserService;
	@Autowired
	private IDatos Service;

	@GetMapping("/home2")
	public String home2(HttpServletRequest request) throws Exception {

		Collection<String> opjdatos = Service.NombreTable();
		COLUMNAS = new ArrayList<String>();
		 COLUMN = new ArrayList<String>();

		request.setAttribute("Lista", opjdatos);
		return "datos";
	}

	@GetMapping("/home")
	public String home(HttpServletRequest request) throws Exception {

		List<Map<String, Object>> opj = UserService.obtieneNombreTable();
		List<String> listAgrupada = new ArrayList<String>();
		// here is the logic
		for (int i = 0; i < opj.size(); i++) {
			StringBuffer originalString = new StringBuffer(opj.get(i).toString());
			originalString.replace(0, 12, "");
			String var = originalString.toString();
			var = var.replaceFirst(".$", "");
			System.out.println(var);
			listAgrupada.add(var);

		}
		Set<String> listado = new LinkedHashSet<String>(listAgrupada);
		listAgrupada.clear();
		listAgrupada.addAll(listado);
		request.setAttribute("Lista", listado);

		UserService.execute();

		return "datos";
	}

	@RequestMapping(value = "/sync/{NameTable}", method = RequestMethod.GET)
	public ModelAndView sync(HttpServletRequest request, @PathVariable("NameTable") String NameTable) throws Exception {
		ModelAndView model = new ModelAndView();
		try {
			NAME_TABLA = NameTable;
			UserService.executeTruncate();
			UserService.executeSync();
			List<Map<String, Object>> lista = UserService.getexecuteSync();
			List<String> listAgrupada = new ArrayList<String>();
			// here is the logic
			for (int i = 0; i < lista.size(); i++) {

				StringBuffer originalString = new StringBuffer(lista.get(i).toString());
				originalString.replace(0, 13, "");
				String var = originalString.toString();
				var = var.replaceFirst(".$", "");

				listAgrupada.add(var);

				VALOR = "" + var + "=t." + var + "";
				COLUMNAS.add(VALOR);

			}
			Set<String> listad = new LinkedHashSet<String>(listAgrupada);
			listAgrupada.clear();
			listAgrupada.addAll(listad);
			// CREATE AND POPULATE A STATIC JAVA LIST
			COLUMN.addAll(listad);
			System.out.println("COLUMN " + COLUMN);

			UserService.executeUpdate();
			
			List<Map<String, Object>> listadoDiff = UserService.getListDiff();
			List<Map<String, Object>> listado = UserService.getList();
			List<Map<String, Object>> listadoRemotos = UserService.getListRemotos();			
			request.setAttribute("ListaRemotos", listadoRemotos);
			request.setAttribute("ListaDiff", listadoDiff);
			request.setAttribute("ListaLocales", listado);
			
			
			model.setViewName("prueba");
			return model;
		} catch (Exception e) {
			e.printStackTrace();

			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Exception:2" + e.getMessage());
			model.setViewName("prueba");
			return model;
		}

	}

	@RequestMapping(value = "/TableName/{dato}", method = RequestMethod.GET)
	public ModelAndView TableName(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("dato") String dato) {
		ModelAndView model = new ModelAndView();
		try {
			tableSyncTemporal op = new tableSyncTemporal();
			op.setDetalle(dato);
			NAME_TABLA = dato;
			List<Map<String, Object>> listado = UserService.getList();
			List<Map<String, Object>> listadoRemotos = UserService.getListRemotos();
			List<Map<String, Object>> listadoDiff = UserService.getListDiff();

			request.setAttribute("ListaRemotos", listadoRemotos);
			request.setAttribute("ListaDiff", listadoDiff);
			request.setAttribute("NameTable", NAME_TABLA);
			request.setAttribute("ListaLocales", listado);

			model.setViewName("prueba");
			return model;
		} catch (Exception e) {
			e.printStackTrace();

			request.setAttribute("ListaLocales", Service.findAllDatosLocales());
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Exception:" + e.getMessage());
			model.setViewName("prueba");
			return model;
		}
	}

	@GetMapping(value = "/")
	public String mostrarProductos(HttpServletRequest request) {

		System.out.print("inicio del web");
		return "index";
	}
	
	 @RequestMapping("/welcome")
	    public String welcome(HttpServletRequest request,HttpServletResponse response,@RequestParam("DB") String DB,@RequestParam("Schema") String Schema,
	    		@RequestParam("usuario") String usuario,@RequestParam("password") String password) {
		 
		 	try {
		 		Collection<String> opjdatos = Service.NombreTable();
				COLUMNAS = new ArrayList<String>();
				 COLUMN = new ArrayList<String>();
				 	String var="jdbc:postgresql://localhost:5432/"+DB+"";
				 	username=var;
					System.out.println("=========== " + DB + " Schema "+ Schema+ " usuario " + usuario +" password"  + password+ "password "+username );
					
				request.setAttribute("Lista", opjdatos);
				return "datos";
			} catch (Exception e) {
				e.getCause();
				System.out.println(" ERROR : [" + e.getCause() + "]");
			}
			return  "datos";
			
	    }
}
