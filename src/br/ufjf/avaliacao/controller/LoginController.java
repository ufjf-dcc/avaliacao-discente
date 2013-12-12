package br.ufjf.avaliacao.controller;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;

import br.ufjf.avaliacao.business.UsuarioBusiness;
import br.ufjf.avaliacao.model.Usuario;

public class LoginController {

	private Usuario usuario = new Usuario();
	private UsuarioBusiness usuarioBusiness = new UsuarioBusiness();
	private Session session = Sessions.getCurrent();
	private boolean submitListenerExists = false;

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
	public void submit(@BindingParam("panel") final Div page) {
		Clients.showBusy("Autenticando usuário...");
		if (!submitListenerExists) {
			submitListenerExists = true;
			page.addEventListener(Events.ON_CLIENT_INFO,
					new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception {
							if (usuario != null && usuario.getEmail() != null
									&& usuario.getSenha() != null) {
								if (usuarioBusiness.login(usuario.getEmail(),
										usuario.getSenha())) {
									usuario = (Usuario) session
											.getAttribute("usuario");
									switch (usuario.getTipoUsuario()) {
									case 0:
										Executions.sendRedirect("/home.zul");
										break;
									case 1:
										Executions.sendRedirect("/home.zul");
										break;
									case 2:
										Executions.sendRedirect("/homeAluno.zul");
										break;
									case 3:
										Executions.sendRedirect("/home.zul");
										break;
									default:
										;
										break;
									}
								} else {
									Clients.clearBusy();
									Messagebox.show(
											"Usuário ou Senha inválidos!",
											"Error", Messagebox.OK,
											Messagebox.ERROR,
											new EventListener<Event>() {
												@Override
												public void onEvent(Event event)
														throws Exception {
													Executions
															.sendRedirect(null);
												}
											});
								}
							} else {
								Clients.clearBusy();
								Messagebox.show("Preencha todos os campos!",
										"Error", Messagebox.OK,
										Messagebox.ERROR,
										new EventListener<Event>() {
											@Override
											public void onEvent(Event event)
													throws Exception {
												Executions.sendRedirect(null);
											}
										});
							}
						}
					});
			Events.echoEvent(Events.ON_CLIENT_INFO, page, null);
		}
	}

	/*
	 * @Command public void submit() throws HibernateException, Exception { if
	 * (usuario != null && usuario.getEmail() != null && usuario.getSenha() !=
	 * null) { if (usuarioBusiness.login(usuario.getEmail(),
	 * usuario.getSenha())) { usuario = (Usuario)
	 * session.getAttribute("usuario"); Executions.sendRedirect("/home.zul"); }
	 * else { Messagebox.show("Usuário ou Senha inválidos!", "Error",
	 * Messagebox.OK, Messagebox.ERROR); } } }
	 */

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
