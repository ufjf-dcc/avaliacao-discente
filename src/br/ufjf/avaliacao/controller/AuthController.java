package br.ufjf.avaliacao.controller;

import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Initiator;

import br.ufjf.avaliacao.business.UsuarioBusiness;
import br.ufjf.avaliacao.model.Usuario;


public class AuthController implements Initiator {
	
	@Override
	public void doInit(Page page, Map<String, Object> args) throws Exception {
		Clients.showBusy("Aguarde o redirecionamento...");
		Session session = Sessions.getCurrent();
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		UsuarioBusiness usuarioBusiness = new UsuarioBusiness();
		if (!usuarioBusiness.checaLogin(usuario)) {
			Executions.sendRedirect("/index.zul");
			return;
		}
		Clients.clearBusy();
	}
}
