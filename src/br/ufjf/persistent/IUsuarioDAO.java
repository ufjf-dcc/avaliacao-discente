package br.ufjf.persistent;

import org.hibernate.HibernateException;

import br.ufjf.model.Usuario;


public interface IUsuarioDAO {
	public Usuario retornaUsuario (String email, String senha) throws HibernateException, Exception;
}
