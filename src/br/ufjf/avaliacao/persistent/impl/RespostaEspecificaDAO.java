package br.ufjf.avaliacao.persistent.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.RespostaEspecifica;
import br.ufjf.avaliacao.persistent.GenericoDAO;
import br.ufjf.avaliacao.persistent.IRespostaEspecifica;

public class RespostaEspecificaDAO extends GenericoDAO implements IRespostaEspecifica {
	
	// retorna as respostas especificas de uma pergunta
	@SuppressWarnings("unchecked")
	public List<RespostaEspecifica> getRespostasEspecificasPerguntas(Pergunta pergunta) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT r FROM RespostasEspecificas AS r WHERE r.pergunta = :pergunta");
			query.setParameter("pergunta", pergunta);

			List<RespostaEspecifica> rs = new ArrayList<RespostaEspecifica>();
			rs = (List<RespostaEspecifica>) query.list();
			getSession().close();

			return rs;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

}
