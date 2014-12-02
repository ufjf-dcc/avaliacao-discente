package br.ufjf.avaliacao.persistent.impl;

import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Matricula;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.GenericoDAO;
import br.ufjf.avaliacao.persistent.IMatriculaDAO;

public class MatriculaDAO extends GenericoDAO implements IMatriculaDAO{

	public List<Matricula> getMatriculas(Usuario usuario) {
		try {
			Query query = getSession().createQuery(
					"SELECT m FROM Matricula AS m WHERE m.usuario = :usuario");
			query.setParameter("usaurio", usuario);

			List<Matricula> matriculas = query.list();
			getSession().close();

			if (matriculas != null)
				return matriculas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
