package br.ufjf.avaliacao.persistent.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.model.RespostaEspecifica;
import br.ufjf.avaliacao.persistent.GenericoDAO;
import br.ufjf.avaliacao.persistent.IRespostaEspecifica;

public class RespostaEspecificaDAO extends GenericoDAO implements IRespostaEspecifica {
	
	// retorna as respostas especificas de uma pergunta
	public List<RespostaEspecifica> getRespostasEspecificasPerguntas(Pergunta pergunta) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT r FROM RespostasEspecificas AS re LEFT JOIN FETCH re.pergunta AS rep  WHERE rep = :pergunta");
			query.setParameter("pergunta", pergunta);

			List<RespostaEspecifica> r = new ArrayList<RespostaEspecifica>();
			r = (List<RespostaEspecifica>) query.list();
			getSession().close();

			return r;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

}
