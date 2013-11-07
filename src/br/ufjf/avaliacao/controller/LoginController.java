package br.ufjf.avaliacao.controller;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import br.ufjf.avaliacao.business.UsuarioBusiness;
import br.ufjf.avaliacao.model.Usuario;

public class LoginController {

	private Usuario usuario = new Usuario();
	private UsuarioBusiness usuarioBusiness = new UsuarioBusiness();
	private Session session = Sessions.getCurrent();

	@Init
	public void testaLogado() throws HibernateException, Exception {
		usuarioBusiness = new UsuarioBusiness();
		usuario = (Usuario) session.getAttribute("usuario");
		if (usuarioBusiness.checaLogin(usuario)) {
			Executions.sendRedirect("/home.zul");
		} else {
			usuario = new Usuario();
		}
	}

	@Command
	public void submit() throws HibernateException, Exception {

		if (usuario != null && usuario.getEmail() != null
				&& usuario.getSenha() != null) {
			if (usuarioBusiness.login(usuario.getEmail(), usuario.getSenha())) {
				usuario = (Usuario) session.getAttribute("usuario");
				Executions.sendRedirect("/home.zul");
			} else {
				Messagebox.show("Usuário ou Senha inválidos!", "Error",
						Messagebox.OK, Messagebox.ERROR);
			}
		}
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}
