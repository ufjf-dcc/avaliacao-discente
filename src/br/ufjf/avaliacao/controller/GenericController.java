package br.ufjf.avaliacao.controller;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;


import br.ufjf.avaliacao.business.UsuarioBusiness;
import br.ufjf.avaliacao.model.Curso;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.impl.CursoDAO;

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

	public void testaPermissao(int tipoUsuario) throws HibernateException,
			Exception {
		usuario = (Usuario) session.getAttribute("usuario");
		usuarioBusiness = new UsuarioBusiness();
		if (usuarioBusiness.checaLogin(usuario)) {
			if (usuario.getTipoUsuario() != tipoUsuario) {
				Executions.sendRedirect("/home.zul");
			}
		} else {
			Executions.sendRedirect("/index.zul");
			usuario = new Usuario();
		}
	}

	public void testaPermissaoAluno() throws HibernateException, Exception {
		usuario = (Usuario) session.getAttribute("usuario");
		usuarioBusiness = new UsuarioBusiness();
		if (usuarioBusiness.checaLogin(usuario)) {
			if (usuario.getTipoUsuario() != Usuario.ALUNO) {
				Executions.sendRedirect("/home.zul");
			}
		} else {
			Executions.sendRedirect("/index.zul");
			usuario = new Usuario();
		}
	}

	public void testaPermissaoAdmin() throws HibernateException, Exception {
		usuario = (Usuario) session.getAttribute("usuario");
		usuarioBusiness = new UsuarioBusiness();
		if (usuarioBusiness.checaLogin(usuario)) {
			if (usuario.getTipoUsuario() != Usuario.ADMIN) {
				Executions.sendRedirect("/home.zul");
			}
		} else {
			Executions.sendRedirect("/index.zul");
			usuario = new Usuario();
		}
	}

	public void testaPermissaoProf() throws HibernateException, Exception {
		usuario = (Usuario) session.getAttribute("usuario");
		usuarioBusiness = new UsuarioBusiness();
		if (usuarioBusiness.checaLogin(usuario)) {
			if (usuario.getTipoUsuario() != Usuario.PROFESSOR) {
				Executions.sendRedirect("/home.zul");
			}
		} else {
			Executions.sendRedirect("/index.zul");
			usuario = new Usuario();
		}
	}

	public void testaPermissaoCoord() throws HibernateException, Exception {
		usuario = (Usuario) session.getAttribute("usuario");
		usuarioBusiness = new UsuarioBusiness();
		if (usuarioBusiness.checaLogin(usuario)) {
			CursoDAO cDAO = new CursoDAO();
			List<Curso> cursos = new ArrayList<Curso>();
			for(int i=0;i<cursos.size();i++)
			{
				if (usuario.getTipoUsuario() != Usuario.PROFESSOR
						|| cDAO.getCursoCoordenadorOuViceUsuario(usuario)!=null) {
					
					Executions.sendRedirect("/home.zul");
				}
			}
			
		} else {
			Executions.sendRedirect("/index.zul");
			usuario = new Usuario();
		}
	}

	public String getMenu() {
		usuario = (Usuario) session.getAttribute("usuario");
		if (usuario != null) {
			int tipoUsuario = usuario.getTipoUsuario();
			if (tipoUsuario == Usuario.PROFESSOR)
			{
				CursoDAO cDAO = new CursoDAO();
				usuario.setCurso(cDAO.getCursoCoordenadorOuViceUsuario(usuario));
				if(cDAO.getCursoCoordenadorOuViceUsuario(usuario)!=null)
				{
					return "/menuCoordenador.zul";
				}
				else 
				{
					return "/menuProfessor.zul";
				}
			}
			if (tipoUsuario == Usuario.ALUNO)
			{
				return "/menuAluno.zul";
			}
			return "/menuAdministrador.zul";
		}
		return null;
	}

	@Command
	public void exit() {
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
