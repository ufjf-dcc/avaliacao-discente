package br.ufjf.avaliacao.persistent.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.model.Turma;
import br.ufjf.avaliacao.model.Usuario;
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

	@SuppressWarnings("unchecked")
	@Override
	public List<Pergunta> getPerguntasQuestionario(Questionario questionario) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT p FROM Pergunta AS p LEFT JOIN FETCH p.questionario AS q WHERE q = :questionario");
			query.setParameter("questionario", questionario);

			List<Pergunta> ps = new ArrayList<Pergunta>();
			ps = (List<Pergunta>) query.list();
			getSession().close();

			return ps;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Pergunta> retornaPerguntasSemestre(String semestre) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT p FROM Pergunta AS p LEFT JOIN FETCH p.respostas AS res WHERE res.semestre =:semestre");
			query.setParameter("semestre", semestre);

			List<Pergunta> ps = new ArrayList<Pergunta>();
			ps = (List<Pergunta>) query.list();
			getSession().close();


			List<Pergunta> perguntas = new ArrayList<Pergunta>();
			for(int i=0;i<ps.size();i++){
				if(!perguntas.contains(ps.get(i)))
					perguntas.add(ps.get(i));
			}

			return perguntas;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public List<Pergunta> retornaPerguntasUsuarioTurmaSemestre(Usuario usuario,Turma turma, String semestre){
		AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
		List<Avaliacao> avaliacoes = avaliacaoDAO.retornaAvaliacoesUsuarioTurmaSemestre(usuario, turma, semestre);
		List<Pergunta> perguntas = new ArrayList<Pergunta>();
		for(int i=0;i<avaliacoes.size();i++)
			perguntas.addAll(avaliacoes.get(i).getPrazoQuestionario().getQuestionario().getPerguntas());
		return perguntas;
	}
}