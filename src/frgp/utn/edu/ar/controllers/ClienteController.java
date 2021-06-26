package frgp.utn.edu.ar.controllers;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import frgp.utn.edu.ar.dto.UserSessionDto;
import frgp.utn.edu.ar.entidades.Cliente;
import frgp.utn.edu.ar.entidades.Localidad;
import frgp.utn.edu.ar.entidades.Pais;
import frgp.utn.edu.ar.entidades.Provincia;
import frgp.utn.edu.ar.entidades.Usuario;
import frgp.utn.edu.ar.negocioImpl.ClienteNegImpl;
import frgp.utn.edu.ar.negocioImpl.LocalidadNegImpl;
import frgp.utn.edu.ar.negocioImpl.PaisNegImpl;
import frgp.utn.edu.ar.negocioImpl.ProvinciaNegImpl;
import frgp.utn.edu.ar.negocioImpl.TipoCuentaNegImpl;
import frgp.utn.edu.ar.negocioImpl.UserNegImpl;
import helpers.ViewNameResolver;

@Controller
public class ClienteController {

	@RequestMapping("listadoClientes.html")
	public ModelAndView LoadListClients() {
		
		ModelAndView mv = new ModelAndView();
		
		ClienteNegImpl cliNegImpl = new ClienteNegImpl();
		
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
		
		PaisNegImpl paisNegImpl = new PaisNegImpl();
		ProvinciaNegImpl provNegImpl = new ProvinciaNegImpl();
		LocalidadNegImpl locNegImpl = new LocalidadNegImpl();
		
		
		mav.addObject("Cliente", new Cliente());
		mav.addObject("ListaPaises", paisNegImpl.obtenerListadoPaises(true));
		mav.addObject("ListaProvincias",provNegImpl.obtenerListadoProvincias(true));
		mav.addObject("ListaLocalidades",locNegImpl.obtenerListadoLocalidades(true));
	    
	    mav.setViewName(viewName);
		return mav;
	}
	
	@RequestMapping("accionCliente.html")
	public ModelAndView clickActionClient(int nroCliente, String btnModificarCli) {

		ModelAndView mv = new ModelAndView();
		ClienteNegImpl cliNegImpl = new ClienteNegImpl();
		Cliente cli = cliNegImpl.ObtenerClientexNroCliente(nroCliente);
		
		
		if(btnModificarCli != null) 
		{
			PaisNegImpl paisNegImpl = new PaisNegImpl();
			ProvinciaNegImpl provNegImpl = new ProvinciaNegImpl();
			LocalidadNegImpl locNegImpl = new LocalidadNegImpl();
			TipoCuentaNegImpl tcNegImpl = new TipoCuentaNegImpl();
			
			mv.addObject("ListaPaises", paisNegImpl.obtenerListadoPaises(true));
			mv.addObject("ListaProvincias",provNegImpl.obtenerListadoProvincias(true));
			mv.addObject("ListaLocalidades",locNegImpl.obtenerListadoLocalidades(true));
			
			//aca va
			mv.addObject("ListaTipoCuentas",tcNegImpl.obtenerListadoTipoCuentas(true));
			mv.addObject("fechaNacimiento", cli.getFechaNacimiento());
			mv.addObject("email",cli.getUsuario().getEmail());
			
			
			mv.addObject("Cliente",cli);
			mv.setViewName("modificarCliente");
		}
		else {
			
			cli.setEstadoCliente(false);
			boolean resultado = false; //cliNegImpl.GuardarCliente(cli);
			
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
	 public String createNewClient(@Validated @ModelAttribute("Cliente")Cliente cli, BindingResult result, ModelMap model, @RequestParam String cmbBoxLocalidades, @RequestParam String cmbBoxProvincias, @RequestParam String cmbBoxPaises, @RequestParam String fechaNac,@RequestParam String email) 
	{
	    if (result.hasErrors()) {
	        return "error";
	    }
	    
	    cli.setFechaNacimiento(LocalDate.parse(fechaNac));
	    cli.setPais(new Pais(Integer.parseInt(cmbBoxPaises.split("-")[0]), cmbBoxPaises.split("-")[1], true));
	    cli.setProv(new Provincia(Integer.parseInt(cmbBoxProvincias.split("-")[0]), cmbBoxProvincias.split("-")[1], true));
	    cli.setLoc(new Localidad(Integer.parseInt(cmbBoxLocalidades.split("-")[0]), cmbBoxLocalidades.split("-")[1], true));
	    cli.setEstadoCliente(true);
	    
	    String name = cli.getNombre().substring(0, 1) + cli.getApellido() + String.valueOf(cli.getDni()).substring(0, 3);
	    String pass = String.valueOf(cli.getDni());
	    Usuario usu = new Usuario(name, pass, email, "Customer", true);
	    
	    cli.setUsuario(usu);
	    
	    ClienteNegImpl cliNegImpl = new ClienteNegImpl();
	    
	    boolean resultadoGuardado = true; //cliNegImpl.GuardarCliente(cli);
	    
	    if(resultadoGuardado) {
		    List<Cliente> lista = cliNegImpl.ObtenerListadoClientes(true);
		    
		    String altaExitosa = "Usuario: " + usu.getUsername() + " - Contraseņa: "+ usu.getPassword();
	    	model.addAttribute("msgAlta", altaExitosa);
		    model.addAttribute("ListaClientes", lista);
		    
		    return "adminClientes";
	    }
	    else {
	    	String errorEnAlta = "error";
	    	
			PaisNegImpl paisNegImpl = new PaisNegImpl();
			ProvinciaNegImpl provNegImpl = new ProvinciaNegImpl();
			LocalidadNegImpl locNegImpl = new LocalidadNegImpl();
			
			model.addAttribute("ListaPaises", paisNegImpl.obtenerListadoPaises(true));
			model.addAttribute("ListaProvincias",provNegImpl.obtenerListadoProvincias(true));
			model.addAttribute("ListaLocalidades",locNegImpl.obtenerListadoLocalidades(true));
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
	    
	    cli.setFechaNacimiento(LocalDate.parse(fechaNac));
	    cli.setPais(new Pais(Integer.parseInt(cmbBoxPaises.split("-")[0]), cmbBoxPaises.split("-")[1], true));
	    cli.setProv(new Provincia(Integer.parseInt(cmbBoxProvincias.split("-")[0]), cmbBoxProvincias.split("-")[1], true));
	    cli.setLoc(new Localidad(Integer.parseInt(cmbBoxLocalidades.split("-")[0]), cmbBoxLocalidades.split("-")[1], true));
	    cli.setEstadoCliente(true);
	    
	    UserNegImpl userNegImpl = new UserNegImpl();
  
	    cli.setUsuario(userNegImpl.obtenerUsuarioClientexNroCliente(cli.getNroCliente()));
	    
	    ClienteNegImpl cliNegImpl = new ClienteNegImpl();
	    
	    boolean resultadoGuardado = false; //cliNegImpl.GuardarCliente(cli);
	    
	    if(resultadoGuardado) {
		    List<Cliente> lista = cliNegImpl.ObtenerListadoClientes(true);
		    
		    String modificacionExitosa = "correcto";
	    	model.addAttribute("msgModificacion", modificacionExitosa);
		    model.addAttribute("ListaClientes", lista);
		    
		    return "adminClientes";
	    }
	    else {
	    	String errorEnModif = "error";
	    	
			PaisNegImpl paisNegImpl = new PaisNegImpl();
			ProvinciaNegImpl provNegImpl = new ProvinciaNegImpl();
			LocalidadNegImpl locNegImpl = new LocalidadNegImpl();
			
			model.addAttribute("ListaPaises", paisNegImpl.obtenerListadoPaises(true));
			model.addAttribute("ListaProvincias",provNegImpl.obtenerListadoProvincias(true));
			model.addAttribute("ListaLocalidades",locNegImpl.obtenerListadoLocalidades(true));
			model.addAttribute("fechaNacimiento", cli.getFechaNacimiento());
			model.addAttribute("email",cli.getUsuario().getEmail());
			
			model.addAttribute("Cliente",cli);
	    	model.addAttribute("errorEnModif", errorEnModif);
	    	
	    	return "modificarCliente";
	    }
	}
	
	
	
}
