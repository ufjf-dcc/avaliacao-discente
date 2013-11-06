package br.ufjf.avaliacao.controller;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import br.ufjf.avaliacao.business.UsuarioBusiness;
import br.ufjf.avaliacao.model.Usuario;

public class GenericController {
	
	
	protected Session session = Sessions.getCurrent();
	protected Usuario usuario = (Usuario) session.getAttribute("usuario");
	protected UsuarioBusiness usuarioBusiness;
	
	
	public boolean testaLogado() throws HibernateException, Exception {
		usuario = (Usuario) session.getAttribute("usuario");
		usuarioBusiness = new UsuarioBusiness();
		if (!usuarioBusiness.checaLogin(usuario)) {
			Executions.sendRedirect("/index.zul");
			usuario = new Usuario();
			return false;
		}
		return true;
	}
	
	
	public void testaPermissao(int tipoUsuario) throws HibernateException, Exception {
		usuario = (Usuario) session.getAttribute("usuario");
		usuarioBusiness = new UsuarioBusiness();
		if (usuarioBusiness.checaLogin(usuario)) {
			if (usuario.getTipoUsuario() != tipoUsuario) {
				Executions.sendRedirect("/home.zul");
			}
		}
		else{
			Executions.sendRedirect("/index.zul");
			usuario = new Usuario();
		}
	}
	
	public String getMenu() {
		usuario = (Usuario) session.getAttribute("usuario");
		if (usuario!=null) {
		int tipoUsuario = usuario.getTipoUsuario();
		if (tipoUsuario == 0)
			return "/menuCoordenador.zul";
		if (tipoUsuario == 1)
			return "/menuProfessor.zul";
		if (tipoUsuario == 2)
			return "/menuAluno.zul";
		return "/menuAdministrador.zul";
		}
		return null;
	} 
	
	@Command
	public void exit(){
		session.invalidate();
		Executions.sendRedirect("/index.zul");
	}
	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
}
