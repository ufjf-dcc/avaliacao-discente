package br.ufjf.controller;

import javax.servlet.http.HttpSession;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;

import br.ufjf.model.Usuario;
import br.ufjf.controller.LoginController;

public class HomeController {
	
	private Usuario usuarioCommon;
	private HttpSession session = (HttpSession) (Executions.getCurrent())
			.getDesktop().getSession().getNativeSession();
	private LoginController loginController;
	
	@Init
	public void testaLogado() throws HibernateException, Exception {
		usuarioCommon = (Usuario) session.getAttribute("usuario");
		if (usuarioCommon != null) {
			loginController = new LoginController();
			usuarioCommon = loginController.login(usuarioCommon.getEmail(),
					usuarioCommon.getSenha());
			if (usuarioCommon != null) {
				return;
			}
		}
		usuarioErro();
	}

	private void usuarioErro() throws InterruptedException {
		Executions.sendRedirect("/index.zul");
		usuarioCommon = new Usuario();
	}
	
}
