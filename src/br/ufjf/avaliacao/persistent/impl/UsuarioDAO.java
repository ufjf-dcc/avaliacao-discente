package br.ufjf.avaliacao.persistent.impl;

import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.GenericoDAO;
import br.ufjf.avaliacao.persistent.IUsuarioDAO;


public class UsuarioDAO extends GenericoDAO implements IUsuarioDAO {
	
	@Override
	public Usuario retornaUsuario(String email, String senha) {
		try {
			Query query = getSession().createQuery("SELECT u FROM Usuario AS u LEFT JOIN FETCH u.curso WHERE u.email = :email AND u.senha = :senha");
			query.setParameter("email", email);
			query.setParameter("senha", senha);
			
			Usuario usuario = (Usuario) query.uniqueResult();
	
			getSession().close();
			
			if(usuario!=null)
				return usuario;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Usuario> getTodosUsuarios() {
		try {
			Query query = getSession().createQuery("SELECT u FROM Usuario AS u LEFT JOIN FETCH u.curso ORDER BY u.nome");
			
			List<Usuario> usuarios = query.list();
			getSession().close();
			
			if(usuarios!=null)
				return usuarios;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Usuario retornaUsuario(String nome) {
		try {
			Query query = getSession().createQuery("SELECT u FROM Usuario AS u LEFT JOIN FETCH u.curso WHERE u.nome = :nome");
			query.setParameter("nome", nome);
			
			Usuario usuario = (Usuario) query.uniqueResult();
			getSession().close();
			
			if (usuario!=null)
				return usuario;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Usuario> retornaProfessores() {
		try {
			Query query = getSession().createQuery("SELECT u FROM Usuario AS u WHERE u.tipoUsuario = :tipoUsuario");
			query.setParameter("tipoUsuario", 1);
			
			List<Usuario> professores = query.list();
			getSession().close();
			
			if (professores!=null)
				return professores;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Usuario retornaUsuarioEmail(String email) {
		try {
			Query query = getSession().createQuery("SELECT u FROM Usuario AS u LEFT JOIN FETCH u.curso WHERE u.email = :email");
			query.setParameter("email", email);
			
			Usuario usuario = (Usuario) query.uniqueResult();
			getSession().close();
			if (usuario!=null)
				return usuario;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Usuario> retornaProfessoresTurma(Turma turma) {
		try {
			Query query = getSession().createQuery("SELECT u FROM Turma AS t JOIN t.usuarios as u WHERE t = :turma AND u.tipoUsuario = :tipoUsuario");
			query.setParameter("turma", turma);
			query.setParameter("tipoUsuario", 1);
			
			List<Usuario> professores = query.list();
			getSession().close();
			if (professores!=null)
				return professores;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Usuario> retornaAlunosTurma(Turma turma) {
		try {
			Query query = getSession().createQuery("SELECT u FROM Turma AS t JOIN t.usuarios as u WHERE t = :turma AND u.tipoUsuario = :tipoUsuario");
			query.setParameter("turma", turma);
			query.setParameter("tipoUsuario", 2);
			
			List<Usuario> usuarios = query.list();
			
			getSession().close();
			
			if (usuarios != null)
				return usuarios;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
