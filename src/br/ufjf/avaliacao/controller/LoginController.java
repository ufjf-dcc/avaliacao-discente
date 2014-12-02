package br.ufjf.avaliacao.controller;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Row;

import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;

import br.ufjf.avaliacao.business.IntegraBusiness;
import br.ufjf.avaliacao.business.UsuarioBusiness;
import br.ufjf.avaliacao.model.Grafico;
import br.ufjf.avaliacao.model.Matricula;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.impl.MatriculaDAO;
import br.ufjf.avaliacao.persistent.impl.UsuarioDAO;

public class LoginController {

	private Usuario usuario = new Usuario();
	private UsuarioBusiness usuarioBusiness = new UsuarioBusiness();
	private Session session = Sessions.getCurrent();
	private boolean submitListenerExists = false;

	@Init
	public void testaLogado() throws HibernateException, Exception {
		usuarioBusiness = new UsuarioBusiness();
		usuario = (Usuario) session.getAttribute("usuario");
		Grafico grafico = new Grafico("grafico"," "," ");//pre processamento para funcionar os frames dos graficos
		session.setAttribute("grafico", grafico);
		session.setAttribute("problema_duplo_submit",0);//n√£o sei por que quando h√° um submit a fun√ß√£o ocorre 2x
//		if (usuarioBusiness.checaLogin(usuario)) {
//			Executions.sendRedirect("/home.zul");
//		} else {
//			usuario = new Usuario();
//		}
	}

		
	@Command
	public void submit(@BindingParam("panel") final Div page,@BindingParam("row") final Row row,
			@BindingParam("login") final String login,@BindingParam("senha") final String senha,
			@BindingParam("botao") final Cell botaoCadastro,@BindingParam("botao2") final Button botaoLogin) {
//		Clients.showBusy("Autenticando usu·rio...");
		if (!submitListenerExists) {
			submitListenerExists = true;
			page.addEventListener(Events.ON_CLIENT_INFO,
					new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception {
							if (login != null && senha != null) 
							{//verifica se usuario e senha sao validos
								if (usuarioBusiness.login(login,senha)) 
								{//verifica se existe e tem no banco de dados
								
									switch (((Usuario)session.getAttribute("usuario")).getTipoUsuario()) {
									case 1:
										Executions.sendRedirect("/questionarios.zul");
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
									Usuario usuarioAux = IntegraBusiness.getusuarioIntegra(login, senha);
									if(usuarioAux!=null)									
									{//verifica se existe o usuario no integra
										if(((int)session.getAttribute("problema_duplo_submit"))!=1)
										{
											session.setAttribute("problema_duplo_submit",1);
											
											for(int i=0;i<usuarioAux.getMatriculas().size();i++)
											{
												((Combobox) row.getChildren().get(1)).appendItem(usuarioAux.getMatriculas().get(i).getMatricula());
											}
											row.setVisible(true);
											botaoCadastro.setVisible(true);
											botaoLogin.setVisible(false);
										}	
											
									}
									else{
										Clients.clearBusy();
										Messagebox.show(
											"Usu·rio ou Senha inv·lidos!",
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

	@Command
	public void cadastrar(@BindingParam("panel") final Div page,@BindingParam("row") final Row row,
			@BindingParam("login") final String login,@BindingParam("senha") final String senha,
			@BindingParam("combo") final String combo)//realiza o cadastro de novos usuarios
	{
//		Clients.showBusy("Autenticando usu·rio...");
		Usuario usuarioAux = IntegraBusiness.getusuarioIntegra(login, senha);
		Matricula matriculaValida = null;
		for(int i=0;i<usuarioAux.getMatriculas().size();i++)
		{
			if(usuarioAux.getMatriculas().get(i).getMatricula()==combo)
			{
				matriculaValida = usuarioAux.getMatriculas().get(i);
				break;
			}
			
		}
		
		if(matriculaValida!=null)
		{
			MatriculaDAO mDAO = new MatriculaDAO();
			for(int i=0;i<usuarioAux.getMatriculas().size();i++)
			{
				usuarioAux.getMatriculas().get(i).setUsuario(usuarioAux);
				mDAO.salvar(usuarioAux.getMatriculas().get(i));
			}
			usuario.setMatriculaAtiva(matriculaValida);
			UsuarioDAO uDAO = new UsuarioDAO();
			uDAO.salvar(usuarioAux);
			///TEM QUE VER O PROBLEMA DA ZONA CRITICA E DAR UM REFRESH PRA LOGAR
			//FINALMENTE, CRIAR UMA PARTE PARA MUDAR A MATRICULA ATIVA, TIPO O 
		}
		
		
	}
	/*
	 * @Command public void submit() throws HibernateException, Exception { if
	 * (usuario != null && usuario.getEmail() != null && usuario.getSenha() !=
	 * null) { if (usuarioBusiness.login(usuario.getEmail(),
	 * usuario.getSenha())) { usuario = (Usuario)
	 * session.getAttribute("usuario"); Executions.sendRedirect("/home.zul"); }
	 * else { Messagebox.show("Usu√°rio ou Senha inv√°lidos!", "Error",
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
