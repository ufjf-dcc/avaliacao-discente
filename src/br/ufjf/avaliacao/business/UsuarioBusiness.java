package br.ufjf.avaliacao.business;

import org.hibernate.HibernateException;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.impl.UsuarioDAO;

public class UsuarioBusiness extends GenericBusiness {

	
	public boolean login(String login, String senha) throws HibernateException,//recebe o usuario do integra e faz as devidas operações
			Exception {
		Usuario usuario = IntegraBusiness.getusuarioIntegra(login, senha);//pega informaçoes do usuario do integra
		if(usuario!=null)
		{
						
			UsuarioDAO uDAO = new UsuarioDAO();
			Usuario usuarioAux = uDAO.retornaUsuarioCPF(login);//verifica se ja foi cadastrado
			if(usuarioAux != null)
			{
				usuarioAux.setEmail(usuario.getEmail());
				uDAO.editar(usuarioAux);//atualizando email, caso tenha mudado	
	
				Session session = Sessions.getCurrent();
				session.setAttribute("usuario", usuarioAux);
				return true;
			}
		}

		return false;
	}
	
	public boolean checaLogin(Usuario usuario) throws HibernateException,
			Exception {
		if (usuario != null) {
			return true;
		}
		return false;
	}

	public boolean cadastrado(String email, String nome)
			throws HibernateException, Exception {
		UsuarioDAO usuarioDAO = new UsuarioDAO();
		if (usuarioDAO.retornaUsuarioNome(nome) != null
				|| usuarioDAO.retornaUsuarioEmail(email) != null) {
			return true;
		} else
			return false;
	}

	public boolean cadatroVlido(Usuario usuario) {
		if (usuario.getTipoUsuario() != null)
			if (usuario.getTipoUsuario() == 1)
				if (campoStrValido(usuario.getNome())
						&& campoStrValido(usuario.getCPF())
						&& campoStrValido(usuario.getMatriculaAtiva().getMatricula()))
					return true;
				else
					return false;
			else if (campoStrValido(usuario.getNome())
					&& campoStrValido(usuario.getEmail())
					&& campoStrValido(usuario.getMatriculaAtiva().getMatricula())
					&& (usuario.getCurso() != null))
				return true;
			else
				return false;
		return false;
	}

	public boolean usuarioUsado(Usuario usuario) {
		if (usuario.getTurmas().isEmpty())
			return false;
		return true;
	}

}