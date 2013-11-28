package br.ufjf.avaliacao.persistent.impl;

import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.persistent.GenericoDAO;
import br.ufjf.avaliacao.persistent.IPerguntaDAO;

public class PerguntaDAO extends GenericoDAO implements IPerguntaDAO {

	@Override
	public boolean existePergunta(Pergunta pergunta) {
		try {
			Query query = getSession().createQuery(
					"SELECT p FROM Pergunta AS p WHERE p = :pergunta");
			query.setParameter("p", pergunta);

			Pergunta p = (Pergunta) query.uniqueResult();
			getSession().close();

			if (p != null) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<Pergunta> getPerguntasQuestionario(Questionario questionario) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT p FROM Pergunta AS p WHERE p.questionario = :questionario");
			query.setParameter("questionario", questionario);

			@SuppressWarnings("unchecked")
			List<Pergunta> ps = (List<Pergunta>) query.list();
			getSession().close();

			if (ps != null) {
				return ps;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
