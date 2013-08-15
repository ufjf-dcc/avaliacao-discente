package br.ufjf.controller;

import javax.servlet.http.HttpSession;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Messagebox;

import br.ufjf.model.Usuario;
import br.ufjf.persistent.impl.UsuarioDAO;


public class LoginController {
	
	Usuario usuario = new Usuario();
	UsuarioDAO usuarioDAO = new UsuarioDAO();
	private HttpSession session = (HttpSession) (Executions.getCurrent())
			.getDesktop().getSession().getNativeSession();
	private Usuario usuarioCommon;

	@Command
	public void submit() throws HibernateException, Exception {
		if (usuario != null && usuario.getEmail() != null
				&& usuario.getSenha() != null) {
			String senha = usuario.getSenha();
			usuario = login(usuario.getEmail(), senha);
			if (usuario != null) {
				Executions.sendRedirect("/home.zul");
			}
		}
	}
	
	public Usuario login(String matricula, String senha) throws HibernateException, Exception {
		Usuario usuario;
		usuarioDAO = new UsuarioDAO();
		usuario = usuarioDAO.retornaUsuario(matricula, senha);

		if (usuario != null) {
			HttpSession session = (HttpSession) (Executions.getCurrent())
					.getDesktop().getSession().getNativeSession();
			session.setAttribute("usuario", usuario);
		} else {
			usuario = new Usuario();
			Messagebox.show("Usuário ou Senha inválidos!", "Error",
					Messagebox.OK, Messagebox.ERROR);
		}
		return usuario;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public UsuarioDAO getUsuarioDAO() {
		return usuarioDAO;
	}

	public void setUsuarioDAO(UsuarioDAO usuarioDAO) {
		this.usuarioDAO = usuarioDAO;
	}
	
	
	
	
}
