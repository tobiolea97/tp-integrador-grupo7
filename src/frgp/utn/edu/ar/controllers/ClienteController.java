package frgp.utn.edu.ar.controllers;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import frgp.utn.edu.ar.dto.UserSessionDto;
import frgp.utn.edu.ar.entidades.Cliente;
import frgp.utn.edu.ar.entidades.Cuenta;
import frgp.utn.edu.ar.entidades.Localidad;
import frgp.utn.edu.ar.entidades.Pais;
import frgp.utn.edu.ar.entidades.Provincia;
import frgp.utn.edu.ar.entidades.TipoCuenta;
import frgp.utn.edu.ar.entidades.Usuario;
import frgp.utn.edu.ar.negocioImpl.ClienteNegImpl;
import frgp.utn.edu.ar.negocioImpl.CuentaNegImpl;
import frgp.utn.edu.ar.negocioImpl.LocalidadNegImpl;
import frgp.utn.edu.ar.negocioImpl.PaisNegImpl;
import frgp.utn.edu.ar.negocioImpl.ProvinciaNegImpl;
import frgp.utn.edu.ar.negocioImpl.TipoCuentaNegImpl;
import frgp.utn.edu.ar.negocioImpl.TransaccionNegImpl;
import frgp.utn.edu.ar.negocioImpl.UserNegImpl;
import helpers.ViewNameResolver;

@Controller
public class ClienteController {
	
	static ApplicationContext appContext = new ClassPathXmlApplicationContext("frgp/utn/edu/ar/resources/Beans.xml");

	@RequestMapping("listadoClientes.html")
	public ModelAndView LoadListClients(HttpSession httpSession, HttpServletRequest request) {
		
		ModelAndView mv = new ModelAndView();
		String viewName = ViewNameResolver.resolveViewName(
			(UserSessionDto)httpSession.getAttribute("userSession"),
			request.getServletPath()
		);
		
		if(viewName.contains("redirect"))
		{
			mv.setViewName(viewName);
			return mv;
		}
		
		ClienteNegImpl cliNegImpl = (ClienteNegImpl)appContext.getBean("clienteNegImpl");
		
		List<Cliente> lista = cliNegImpl.ObtenerListadoClientes(true);
		
		mv.addObject("ListaClientes", lista);
		
		mv.setViewName("adminClientes");
		
		return mv;
	}
	
	@RequestMapping(value="crearCliente.html")
	public ModelAndView eventClickCrearCliente(HttpSession httpSession, HttpServletRequest request) {
		
		ModelAndView mav = new ModelAndView();
		String viewName = ViewNameResolver.resolveViewName(
			(UserSessionDto)httpSession.getAttribute("userSession"),
			request.getServletPath()
		);
		
		PaisNegImpl paisNegImpl = (PaisNegImpl)appContext.getBean("paisNegImpl");
		ProvinciaNegImpl provNegImpl = (ProvinciaNegImpl)appContext.getBean("provinciaNegImpl");
		LocalidadNegImpl locNegImpl = (LocalidadNegImpl)appContext.getBean("localidadNegImpl");
		TipoCuentaNegImpl tipoCuentaNegImpl = (TipoCuentaNegImpl)appContext.getBean("tipoCuentaNegImpl");
		
		Cliente cliente = (Cliente)appContext.getBean("cliente");
		
		mav.addObject("Cliente", cliente);
		mav.addObject("ListaPaises", paisNegImpl.obtenerListadoPaises(true));
		mav.addObject("ListaProvincias",provNegImpl.obtenerListadoProvincias(true));
		mav.addObject("ListaLocalidades",locNegImpl.obtenerListadoLocalidades(true));
		mav.addObject("ListaTiposCuenta",tipoCuentaNegImpl.ObtenerListadoTiposCuenta(true));
	    
	    mav.setViewName(viewName);
		return mav;
	}
	
	@RequestMapping("accionCliente.html")
	public ModelAndView clickActionClient(int nroCliente, String btnModificarCli, HttpSession httpSession, HttpServletRequest request, Model model) {

		ModelAndView mv = new ModelAndView();
		String viewName = ViewNameResolver.resolveViewName(
			(UserSessionDto)httpSession.getAttribute("userSession"),
			request.getServletPath()
		);
		
		if(viewName.contains("redirect"))
		{
			mv.setViewName(viewName);
			return mv;
		}
		
		ClienteNegImpl cliNegImpl = (ClienteNegImpl)appContext.getBean("clienteNegImpl");
		CuentaNegImpl cuentaNegImpl = (CuentaNegImpl)appContext.getBean("cuentaNegImpl");
		TipoCuentaNegImpl tipoCuentaNegImpl = (TipoCuentaNegImpl)appContext.getBean("tipoCuentaNegImpl");
		Cliente cli = cliNegImpl.ObtenerClientexNroCliente(nroCliente);
		
		String avisoSuccess = (String)httpSession.getAttribute("avisoSuccess");
		String avisoError = (String)httpSession.getAttribute("avisoError");
		httpSession.removeAttribute("avisoSuccess");
		httpSession.removeAttribute("avisoError");
		model.addAttribute("avisoSuccess", avisoSuccess);
		model.addAttribute("avisoError", avisoError);
		
		
		if(btnModificarCli != null) 
		{
			PaisNegImpl paisNegImpl = (PaisNegImpl)appContext.getBean("paisNegImpl");
			ProvinciaNegImpl provNegImpl = (ProvinciaNegImpl)appContext.getBean("provinciaNegImpl");
			LocalidadNegImpl locNegImpl = (LocalidadNegImpl)appContext.getBean("localidadNegImpl");
			TipoCuentaNegImpl tcNegImpl = (TipoCuentaNegImpl)appContext.getBean("tipoCuentaNegImpl");
			
			mv.addObject("ListaPaises", paisNegImpl.obtenerListadoPaises(true));
			mv.addObject("ListaProvincias",provNegImpl.obtenerListadoProvincias(true));
			mv.addObject("ListaLocalidades",locNegImpl.obtenerListadoLocalidades(true));
			
			mv.addObject("ListaTipoCuentas",tcNegImpl.ObtenerListadoTiposCuenta(true));
			mv.addObject("fechaNacimiento", cli.getFechaNacimiento());
			mv.addObject("email",cli.getUsuario().getEmail());
			
			List<Cuenta> cuentas = cuentaNegImpl.ObtenerListadoCuentasxCliente(cli);
			mv.addObject("ListaCuentas", cuentas);
			
			List<TipoCuenta> tiposCuenta = tipoCuentaNegImpl.ObtenerListadoTiposCuenta(true);
			mv.addObject("tiposCuenta", tiposCuenta);
			
			mv.addObject("Cliente",cli);
			mv.setViewName("modificarCliente");
		}
		else {
			
			cli.setEstadoCliente(false);
			boolean resultado = cliNegImpl.GuardarCliente(cli);
			
			if(resultado) {
				String eliminacionExitosa = "correcto";
				mv.addObject("eliminacionExitosa",eliminacionExitosa);
			}
			else {
				String eliminacionFallida = "fallo";
				mv.addObject("eliminacionFallida",eliminacionFallida);
			}
			
			List<Cliente> lista = cliNegImpl.ObtenerListadoClientes(true);
			
			mv.addObject("ListaClientes", lista);
			
			mv.setViewName("adminClientes");
		}
		
		return mv;
	}
	
	@RequestMapping("altaCliente.html")
	 public String createNewClient(
			 @Validated @ModelAttribute("Cliente")Cliente cli,
			 BindingResult result,
			 ModelMap model,
			 @RequestParam String cmbBoxLocalidades,
			 @RequestParam String cmbBoxProvincias,
			 @RequestParam String cmbBoxPaises,
			 @RequestParam String fechaNac,
			 @RequestParam String email,
			 @RequestParam String cuentaNombre,
			 @RequestParam String tipoCuenta
	 ) 
	{
	    if (result.hasErrors()) {
	        return "error";
	    }
	    
	    ClienteNegImpl cliNegImpl = (ClienteNegImpl)appContext.getBean("clienteNegImpl");
	    TipoCuentaNegImpl tipoCuentaNegImpl = (TipoCuentaNegImpl)appContext.getBean("tipoCuentaNegImpl");
	    CuentaNegImpl cuentaNegImpl = (CuentaNegImpl)appContext.getBean("cuentaNegImpl");
	    
	    if(cmbBoxLocalidades != null)
	    {
	    	model.addAttribute("localidadSeleccionada",cmbBoxLocalidades.split("-")[0]);
	    }
	    if(cmbBoxProvincias != null)
	    {
	    	model.addAttribute("provinciaSeleccionada",cmbBoxProvincias.split("-")[0]);
	    }
	    if(cmbBoxPaises != null)
	    {
	    	model.addAttribute("paisSeleccionado",cmbBoxPaises.split("-")[0]);
	    }
	    if(fechaNac != null)
	    {
	    	model.addAttribute("fechaSeleccionada",fechaNac);
	    }
	    if(email != null)
	    {
	    	model.addAttribute("emailSeteado",email);
	    }
	   
	    if(cli.getApellido().equals("") || cli.getNombre().equals("") || cli.getSexo() == null) {
    		String errorEnAlta = "error";
	    	
	    	PaisNegImpl paisNegImpl = (PaisNegImpl)appContext.getBean("paisNegImpl");
			ProvinciaNegImpl provNegImpl = (ProvinciaNegImpl)appContext.getBean("provinciaNegImpl");
			LocalidadNegImpl locNegImpl = (LocalidadNegImpl)appContext.getBean("localidadNegImpl");
			
			model.addAttribute("ListaPaises", paisNegImpl.obtenerListadoPaises(true));
			model.addAttribute("ListaProvincias",provNegImpl.obtenerListadoProvincias(true));
			model.addAttribute("ListaLocalidades",locNegImpl.obtenerListadoLocalidades(true));
			model.addAttribute("ListaTiposCuenta",tipoCuentaNegImpl.ObtenerListadoTiposCuenta(true));
	    	model.addAttribute("errorFaltanCampos", "*Algunos de lo siguientes campos (Nombre,Apellido o Sexo) no fue completado.");
	    	
	    	return "crearCliente";
	    }
	    
	    
	    Cliente cliente = cliNegImpl.ObtenerClientexDNI(cli.getDni(), null);
	    
	    if(cliente != null)
	    {
    		String errorEnAlta = "error";
	    	
	    	PaisNegImpl paisNegImpl = (PaisNegImpl)appContext.getBean("paisNegImpl");
			ProvinciaNegImpl provNegImpl = (ProvinciaNegImpl)appContext.getBean("provinciaNegImpl");
			LocalidadNegImpl locNegImpl = (LocalidadNegImpl)appContext.getBean("localidadNegImpl");
			
			model.addAttribute("ListaPaises", paisNegImpl.obtenerListadoPaises(true));
			model.addAttribute("ListaProvincias",provNegImpl.obtenerListadoProvincias(true));
			model.addAttribute("ListaLocalidades",locNegImpl.obtenerListadoLocalidades(true));
			model.addAttribute("ListaTiposCuenta",tipoCuentaNegImpl.ObtenerListadoTiposCuenta(true));
	    	model.addAttribute("dniMessage", "Este DNI ya se encuentra asociado a un cliente");
	    	
	    	return "crearCliente";
	    }
	    
	    Pais pais = (Pais)appContext.getBean("pais");
	    pais.setIdPais(Integer.parseInt(cmbBoxPaises.split("-")[0]));
	    pais.setNombre(cmbBoxPaises.split("-")[1]);
	    pais.setEstadoPais(true);
	    
	    Provincia provincia = (Provincia)appContext.getBean("provincia");
	    provincia.setIdProvincia(Integer.parseInt(cmbBoxProvincias.split("-")[0]));
	    provincia.setNombre(cmbBoxProvincias.split("-")[1]);
	    provincia.setEstadoProvincia(true);
	    
	    Localidad localidad = (Localidad)appContext.getBean("localidad");
	    localidad.setIdLocalidad(Integer.parseInt(cmbBoxLocalidades.split("-")[0]));
	    localidad.setNombre(cmbBoxLocalidades.split("-")[1]);
	    localidad.setEstadoLocalidad(true);
	    
	    cli.setFechaNacimiento(LocalDate.parse(fechaNac));
	    cli.setPais(pais);
	    cli.setProv(provincia);
	    cli.setLoc(localidad);
	    cli.setEstadoCliente(true);
	    
	    String name = String.valueOf(cli.getDni()) + ".utnBank";
	    String pass = String.valueOf(cli.getDni());
	    
	    Usuario usu = (Usuario)appContext.getBean("usuario");
	    usu.setUsername(name);
	    usu.setPassword(pass);
	    usu.setEmail(email);
	    usu.setTipo("Customer");
	    usu.setActivo(true);
	    
	    cli.setUsuario(usu);
	    
	    boolean resultadoGuardado = cliNegImpl.GuardarCliente(cli);
	    
	    if(resultadoGuardado) {
		    List<Cliente> lista = cliNegImpl.ObtenerListadoClientes(true);
		    
		    if(cuentaNombre != null && !cuentaNombre.equals(""))
		    {
		    	Cuenta cuenta = (Cuenta)appContext.getBean("cuenta");
			    TipoCuenta tipoCuentaObj = tipoCuentaNegImpl.ObtenerTipoCuenta(tipoCuenta);
			    cuenta.setCliente(cli);
			    cuenta.setTipoCuenta(tipoCuentaObj);
			    cuenta.setNombre(cuentaNombre);
			    boolean resultado = cuentaNegImpl.GuardarCuenta(cuenta);
			    
			    if(resultado==true) 
			    {
			    	TransaccionNegImpl transaccionNegImpl = (TransaccionNegImpl)appContext.getBean("transaccionNegImpl");
			    	transaccionNegImpl.GenerarTransferenciaInicial(cuenta, 10000);
			    }
			    
		    }
		    
		    String altaExitosa = "Usuario: " + usu.getUsername() + " - Contrase�a: "+ usu.getPassword();
	    	model.addAttribute("msgAlta", altaExitosa);
		    model.addAttribute("ListaClientes", lista);
		    
		    return "adminClientes";
	    }
	    else {
	    	String errorEnAlta = "error";
	    	
	    	PaisNegImpl paisNegImpl = (PaisNegImpl)appContext.getBean("paisNegImpl");
			ProvinciaNegImpl provNegImpl = (ProvinciaNegImpl)appContext.getBean("provinciaNegImpl");
			LocalidadNegImpl locNegImpl = (LocalidadNegImpl)appContext.getBean("localidadNegImpl");
			
			model.addAttribute("ListaPaises", paisNegImpl.obtenerListadoPaises(true));
			model.addAttribute("ListaProvincias",provNegImpl.obtenerListadoProvincias(true));
			model.addAttribute("ListaLocalidades",locNegImpl.obtenerListadoLocalidades(true));
			model.addAttribute("ListaTiposCuenta",tipoCuentaNegImpl.ObtenerListadoTiposCuenta(true));
	    	model.addAttribute("msgError", errorEnAlta);
	    	
	    	return "crearCliente";
	    }
	}
	
	@RequestMapping("modificarCliente.html")
	 public String updateClient(@Validated @ModelAttribute("Cliente")Cliente cli, BindingResult result, ModelMap model, @RequestParam String cmbBoxLocalidades, @RequestParam String cmbBoxProvincias, @RequestParam String cmbBoxPaises, @RequestParam String fechaNac,@RequestParam String email) 
	{
	    if (result.hasErrors()) {
	        return "error";
	    }
	    
	    ClienteNegImpl cliNegImpl = (ClienteNegImpl)appContext.getBean("clienteNegImpl");
	    Cliente clienteActual = cliNegImpl.ObtenerClientexNroCliente(cli.getNroCliente());
	    
	    TipoCuentaNegImpl tipoCuentaNegImpl = (TipoCuentaNegImpl)appContext.getBean("tipoCuentaNegImpl");
	    
	    Pais pais = (Pais)appContext.getBean("pais");
	    pais.setIdPais(Integer.parseInt(cmbBoxPaises.split("-")[0]));
	    pais.setNombre(cmbBoxPaises.split("-")[1]);
	    pais.setEstadoPais(true);
	    
	    Provincia provincia = (Provincia)appContext.getBean("provincia");
	    provincia.setIdProvincia(Integer.parseInt(cmbBoxProvincias.split("-")[0]));
	    provincia.setNombre(cmbBoxProvincias.split("-")[1]);
	    provincia.setEstadoProvincia(true);
	    
	    Localidad localidad = (Localidad)appContext.getBean("localidad");
	    localidad.setIdLocalidad(Integer.parseInt(cmbBoxLocalidades.split("-")[0]));
	    localidad.setNombre(cmbBoxLocalidades.split("-")[1]);
	    localidad.setEstadoLocalidad(true);
	    
	    clienteActual.setFechaNacimiento(LocalDate.parse(fechaNac));
	    clienteActual.setPais(pais);
	    clienteActual.setProv(provincia);
	    clienteActual.setLoc(localidad);
	    clienteActual.setEstadoCliente(true);
	    clienteActual.setDni(cli.getDni());
	    clienteActual.setNombre(cli.getNombre());
	    clienteActual.setApellido(cli.getApellido());
	    
	    if(cli.getApellido().equals("") || cli.getNombre().equals("") || cli.getSexo() == null) {
	    	PaisNegImpl paisNegImpl = (PaisNegImpl)appContext.getBean("paisNegImpl");
			ProvinciaNegImpl provNegImpl = (ProvinciaNegImpl)appContext.getBean("provinciaNegImpl");
			LocalidadNegImpl locNegImpl = (LocalidadNegImpl)appContext.getBean("localidadNegImpl");
			
			model.addAttribute("ListaPaises", paisNegImpl.obtenerListadoPaises(true));
			model.addAttribute("ListaProvincias",provNegImpl.obtenerListadoProvincias(true));
			model.addAttribute("ListaLocalidades",locNegImpl.obtenerListadoLocalidades(true));
			model.addAttribute("ListaTiposCuenta",tipoCuentaNegImpl.ObtenerListadoTiposCuenta(true));
	    	model.addAttribute("errorFaltanCampos", "*Algunos de lo siguientes campos (Nombre,Apellido o Sexo) no fue completado.");
	    	model.addAttribute("fechaNacimiento", clienteActual.getFechaNacimiento());
	    	model.addAttribute("email", clienteActual.getUsuario().getEmail());
	    	model.addAttribute("Cliente",clienteActual);
	    	
	    	return "modificarCliente";
	    }
	    
	    Cliente cliente = cliNegImpl.ObtenerClientexDNI(cli.getDni(), null);
	    
	    if(cliente != null && cli.getNroCliente() != cliente.getNroCliente())
	    {
    		String errorEnAlta = "error";
	    	
	    	PaisNegImpl paisNegImpl = (PaisNegImpl)appContext.getBean("paisNegImpl");
			ProvinciaNegImpl provNegImpl = (ProvinciaNegImpl)appContext.getBean("provinciaNegImpl");
			LocalidadNegImpl locNegImpl = (LocalidadNegImpl)appContext.getBean("localidadNegImpl");
			
			model.addAttribute("ListaPaises", paisNegImpl.obtenerListadoPaises(true));
			model.addAttribute("ListaProvincias",provNegImpl.obtenerListadoProvincias(true));
			model.addAttribute("ListaLocalidades",locNegImpl.obtenerListadoLocalidades(true));
			model.addAttribute("ListaTiposCuenta",tipoCuentaNegImpl.ObtenerListadoTiposCuenta(true));
			model.addAttribute("Cliente",clienteActual);
			model.addAttribute("fechaNacimiento", clienteActual.getFechaNacimiento());
	    	model.addAttribute("email", clienteActual.getUsuario().getEmail());
	    	model.addAttribute("dniMessage", "Este DNI ya se encuentra asociado a un cliente");
	    	
	    	return "modificarCliente";
	    }
	    
	    cli.setFechaNacimiento(LocalDate.parse(fechaNac));
	    cli.setPais(pais);
	    cli.setProv(provincia);
	    cli.setLoc(localidad);
	    cli.setEstadoCliente(true);	    
	    
	    UserNegImpl userNegImpl = (UserNegImpl)appContext.getBean("userNegImpl");
  
	    cli.setUsuario(userNegImpl.obtenerUsuarioClientexNroCliente(cli.getNroCliente()));
	    cli.getUsuario().setEmail(email);

	    boolean resultadoGuardado = cliNegImpl.GuardarCliente(cli);
	    
	    if(resultadoGuardado) {
		    List<Cliente> lista = cliNegImpl.ObtenerListadoClientes(true);
		    
		    String modificacionExitosa = "correcto";
	    	model.addAttribute("msgModificacion", modificacionExitosa);
		    model.addAttribute("ListaClientes", lista);
		    
		    return "adminClientes";
	    }
	    else {
	    	String errorEnModif = "error";
	    	
			PaisNegImpl paisNegImpl = (PaisNegImpl)appContext.getBean("paisNegImpl");
			ProvinciaNegImpl provNegImpl = (ProvinciaNegImpl)appContext.getBean("provinciaNegImpl");
			LocalidadNegImpl locNegImpl = (LocalidadNegImpl)appContext.getBean("localidadNegImpl");
			
			model.addAttribute("ListaPaises", paisNegImpl.obtenerListadoPaises(true));
			model.addAttribute("ListaProvincias",provNegImpl.obtenerListadoProvincias(true));
			model.addAttribute("ListaLocalidades",locNegImpl.obtenerListadoLocalidades(true));
			model.addAttribute("fechaNacimiento", cli.getFechaNacimiento());
			model.addAttribute("ListaTiposCuenta",tipoCuentaNegImpl.ObtenerListadoTiposCuenta(true));
			model.addAttribute("email",cli.getUsuario().getEmail());
			
			model.addAttribute("Cliente",cli);
	    	model.addAttribute("errorEnModif", errorEnModif);
	    	
	    	return "modificarCliente";
	    }
	}
	
	
	
}
