package br.ufjf.avaliacao.persistent.impl;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.GenericoDAO;
import br.ufjf.avaliacao.persistent.IAvalicaoDAO;

public class AvaliacaoDAO extends GenericoDAO implements IAvalicaoDAO {

	public boolean jaAvaliou(Usuario usuario, Turma turma) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT a FROM Avaliacao AS a LEFT JOIN FETCH a.turma AS t WHERE a.avaliando = :usuario AND t = :turma");
			query.setParameter("turma", turma);
			query.setParameter("usuario", usuario);

			Avaliacao a = (Avaliacao) query.uniqueResult();

			getSession().close();

			if (a != null)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}