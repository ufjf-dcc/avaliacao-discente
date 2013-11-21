package br.ufjf.avaliacao.persistent.impl;

import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.GenericoDAO;
import br.ufjf.avaliacao.persistent.IAvalicaoDAO;

public class AvaliacaoDAO extends GenericoDAO implements IAvalicaoDAO {

	public Avaliacao retornaAvaliacaoCoord(Usuario usuario) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT a FROM Avaliacao AS a LEFT JOIN FETCH a.questionario AS q WHERE a.avaliando = :usuario AND q.tipoQuestionario = :tipoQuestionario");
			query.setParameter("avaliando", usuario);
			query.setParameter("tipoQuestionario", 0);

			Avaliacao avaliacao = (Avaliacao) query.uniqueResult();

			getSession().close();
			if (avaliacao != null)
				return avaliacao;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Avaliacao> retornaAvaliacaoProfs(Usuario avaliando) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT a FROM Avaliacao AS a LEFT JOIN FETCH a.questionario AS q WHERE a.avaliando = :avaliando AND q.tipoQuestionario = :tipoQuestionario");
			query.setParameter("avaliando", avaliando);
			query.setParameter("tipoQuestionario", 1);

			List<Avaliacao> avaliacao = query.list();

			getSession().close();
			if (avaliacao != null)
				return avaliacao;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
