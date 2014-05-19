package br.ufjf.avaliacao.business;

import org.hibernate.HibernateException;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.impl.UsuarioDAO;

public class UsuarioBusiness extends GenericBusiness {

	public boolean login(String email, String senha) throws HibernateException,
			Exception {
		UsuarioDAO usuarioDAO = new UsuarioDAO();
		Usuario usuario = usuarioDAO.retornaUsuario(email, senha);

		if (usuario != null) {
			Session session = Sessions.getCurrent();
			session.setAttribute("usuario", usuario);
			return true;
		}

		return false;
	}

	public boolean checaLogin(Usuario usuario) throws HibernateException,
			Exception {
		if (usuario != null) {
			UsuarioDAO usuarioDAO = new UsuarioDAO();
			usuario = usuarioDAO.retornaUsuario(usuario.getEmail(),
					usuario.getSenha());

			if (usuario != null) {
				return true;
			}
		}

		return false;
	}

	public boolean cadastrado(String email, String nome)
			throws HibernateException, Exception {
		UsuarioDAO usuarioDAO = new UsuarioDAO();
		if (usuarioDAO.retornaUsuario(nome) != null
				|| usuarioDAO.retornaUsuarioEmail(email) != null) {
			return true;
		} else
			return false;
	}

	public boolean cadastroValido(Usuario usuario) {
		if (usuario.getTipoUsuario() != null)
			if (usuario.getTipoUsuario() == 1)
				if (campoStrValido(usuario.getNome())
						&& campoStrValido(usuario.getEmail())
						&& campoStrValido(usuario.getSenha()))
					return true;
				else
					return false;
			else if (campoStrValido(usuario.getNome())
					&& campoStrValido(usuario.getEmail())
					&& campoStrValido(usuario.getSenha())
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