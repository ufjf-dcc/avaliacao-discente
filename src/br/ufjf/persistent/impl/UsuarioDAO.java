package br.ufjf.persistent.impl;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

import br.ufjf.persistent.GenericoDAO;
import br.ufjf.persistent.IUsuarioDAO;
import br.ufjf.model.Usuario;

public class UsuarioDAO extends GenericoDAO implements IUsuarioDAO {
	
	@SuppressWarnings("finally")
	@Override
	public Usuario retornaUsuario(String email, String senha) throws HibernateException, Exception {
		Usuario usuario = null;
		try {

			Criteria criteria = getSession()
					.createCriteria(Usuario.class, "usuario")
					.add(Restrictions.eq("usuario.email", email))
					.add(Restrictions.eq("usuario.senha", senha))
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

			usuario = (Usuario) criteria.uniqueResult();
		} catch (HibernateException e) {
			System.err.println(e.fillInStackTrace());
		} finally {
			getSession().close();
			return usuario;
		}
	}

}
