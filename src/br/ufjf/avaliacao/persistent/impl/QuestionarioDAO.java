package br.ufjf.avaliacao.persistent.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Curso;
import br.ufjf.avaliacao.model.Pergunta;
import br.ufjf.avaliacao.model.PrazoQuestionario;
import br.ufjf.avaliacao.model.Questionario;
import br.ufjf.avaliacao.model.Semestre;
import br.ufjf.avaliacao.model.Usuario;
import br.ufjf.avaliacao.persistent.GenericoDAO;
import br.ufjf.avaliacao.persistent.IQuestionarioDAO;

public class QuestionarioDAO extends GenericoDAO implements IQuestionarioDAO {

	@SuppressWarnings("unchecked")
	public List<Questionario> retornaQuestinariosCurso(Curso curso) {
		try {
			Query query = getSession().createQuery(
					"SELECT q FROM Questionario AS q WHERE q.curso = :curso");
			query.setParameter("curso", curso);

			List<Questionario> questionarios = query.list();

			getSession().close();

			if (questionarios != null)
				return questionarios;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Questionario> retornaQuestinariosCursoTipo(Curso curso,
			Integer tipoQuestionario) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT q FROM Questionario AS q WHERE q.curso = :curso AND q.tipoQuestionario = :tipoQuestionario");
			query.setParameter("curso", curso);
			query.setParameter("tipoQuestionario", tipoQuestionario);

			List<Questionario> questionarios = query.list();

			getSession().close();

			if (questionarios != null)
				return questionarios;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Questionario> getAllQuestionario()
	{
		try {
			Query query = getSession()
					.createQuery(
							"SELECT q FROM Questionario AS q");
			List<Questionario> questionarios = query.list();

			getSession().close();

			return questionarios;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Questionario retornaQuestinarioAtivo(Curso curso,
			Integer tipoQuestionario) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT q FROM Questionario AS q WHERE q.curso = :curso AND q.tipoQuestionario = :tipoQuestionario AND q.ativo = :ativo");
			query.setParameter("curso", curso);
			query.setParameter("tipoQuestionario", tipoQuestionario);
			query.setParameter("ativo", true);

			Questionario questionario = (Questionario) query.uniqueResult();

			getSession().close();

			if (questionario != null)
				return questionario;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Questionario> retornaQuestinariosParaUsuario(Usuario usuario) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT q FROM Questionario as q WHERE q.curso = :curso AND q.ativo = :ativo");
			query.setParameter("curso", usuario.getCurso());
			query.setParameter("ativo", true);

			List<Questionario> questionario = query.list();

			getSession().close();

			if (questionario != null)
				return questionario;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Questionario retornaQuestinarioParaUsuarioCoord(Usuario usuario) {
		List<Questionario> quetsCoordenador = getAllQuestionario();
		for(int i=0;i<quetsCoordenador.size();i++)
		{
			if(quetsCoordenador.get(i).getTipoQuestionario() == Questionario.COORD)
			{
				if(quetsCoordenador.get(i).getCurso().getIdentificador().equals(usuario.getCurso().getIdentificador()))
				{
					if(quetsCoordenador.get(i).isAtivo())
					{
						return quetsCoordenador.get(i);
					}
						
				}
			}
		}
		return null;
	}
	
		
	
	public Questionario retornaQuestinarioParaUsuarioAutoAvaliacao(
			Usuario usuario) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT q FROM Questionario AS q LEFT JOIN FETCH q.prazos WHERE q.curso = :curso AND q.ativo = :ativo AND q.tipoQuestionario = :tipoQuestionario");
			query.setParameter("curso", usuario.getCurso());
			query.setParameter("ativo", true);
			query.setParameter("tipoQuestionario", Questionario.AUTO);

			Questionario questionario = (Questionario) query.uniqueResult();

			getSession().close();

			if (questionario != null)
				return questionario;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Questionario retornaQuestinarioParaUsuarioInfra(Usuario usuario) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT q FROM Questionario AS q LEFT JOIN FETCH q.prazos WHERE q.curso = :curso AND q.ativo = :ativo AND q.tipoQuestionario = :tipoQuestionario");
			query.setParameter("curso", usuario.getCurso());
			query.setParameter("ativo", true);
			query.setParameter("tipoQuestionario", Questionario.INFRA);

			Questionario questionario = (Questionario) query.uniqueResult();

			getSession().close();

			if (questionario != null)
				return questionario;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Questionario retornaQuestionarioProf(Usuario usuario) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT q FROM Questionario AS q WHERE q.curso = :curso AND q.ativo = :ativo AND q.tipoQuestionario = :tipoQuestionario");
			query.setParameter("curso", usuario.getCurso());
			query.setParameter("ativo", true);
			query.setParameter("tipoQuestionario", 1);

			Questionario q = (Questionario) query.uniqueResult();

			getSession().close();

			if (q != null) {
				return q;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Questionario retornaQuestionarioPergunta(Pergunta pergunta) { // retorna
																			// o
																			// questionario
																			// de
																			// uma
																			// pergunta
		try {
			Query query = getSession()
					.createQuery(
							"SELECT q FROM Questionario AS q LEFT JOIN FETCH q.perguntas as ps WHERE ps =:pergunta");
			query.setParameter("pergunta", pergunta);

			Questionario questionario = (Questionario) query.uniqueResult();
			getSession().close();

			if (questionario != null)
				return questionario;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Questionario> retornaQuestionariosSemestre(String semestre) {// retornas
																				// os
																				// questionarios
																				// de
																				// um
																				// semestre
		PerguntaDAO perguntaDAO = new PerguntaDAO();
		List<Pergunta> perguntas = perguntaDAO
				.retornaPerguntasSemestre(semestre);
		List<Questionario> questionarios = new ArrayList<Questionario>();
		for (int i = 0; i < perguntas.size(); i++) {
			if (!questionarios.contains(retornaQuestionarioPergunta(perguntas
					.get(i))))
				questionarios
						.add(retornaQuestionarioPergunta(perguntas.get(i)));
		}
		return questionarios;

	}

	public List<Questionario> retornaQuestionariosSemestreProfessorCurso(
			String semestre, Curso curso) {// retornas os questionarios de um
											// semestre para professores
		List<Questionario> questionarios = retornaQuestionariosSemestre(semestre);
		for (int j = questionarios.size() - 1; j >= 0; j--)
			if (questionarios.get(j).getTipoQuestionario() != 1
					|| questionarios.get(j).getCurso() != curso) {
				questionarios.remove(j);
			}
		return questionarios;
	}

	public List<Questionario> retornaQuestionariosSemestreInfraestrutura(
			String semestre) {// retornas os questionarios de um semestre para
								// infraestrutura
		List<Questionario> questionarios = retornaQuestionariosSemestre(semestre);
		for (int j = questionarios.size() - 1; j >= 0; j--)
			if (questionarios.get(j).getTipoQuestionario() != 3) {
				questionarios.remove(j);
			}

		if (questionarios.size() > 0)
			return questionarios;
		return null;
	}

	public List<Questionario> retornaQuestionariosSemestreCoordenador(
			String semestre, Curso curso) {// retornas os questionarios de um
											// semestre para coordenador
		List<Questionario> questionarios = retornaQuestionariosSemestre(semestre);
		for (int j = questionarios.size() - 1; j >= 0; j--)
			if (questionarios.get(j).getTipoQuestionario() != 0
					|| questionarios.get(j).getCurso() != curso) {
				questionarios.remove(j);
			}

		if (questionarios.size() > 0)
			return questionarios;
		return null;
	}

	public List<Questionario> retornaQuestionariosSemestreAutoavaliacao(
			String semestre, Curso curso) {// retornas os questionarios de um
											// semestre para autoavaliaï¿½ï¿½o
		List<Questionario> questionarios = retornaQuestionariosSemestre(semestre);
		for (int j = questionarios.size() - 1; j >= 0; j--)
			if (questionarios.get(j).getTipoQuestionario() != 2
					|| questionarios.get(j).getCurso() != curso) {
				questionarios.remove(j);
			}

		if (questionarios.size() > 0)
			return questionarios;
		return null;
	}

	public List<Questionario> retornaQuestionariosTipo(Curso curso, int tipo) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT q FROM Questionario AS q WHERE q.curso = :curso AND q.tipoQuestionario = :tipoQuestionario");
			query.setParameter("curso", curso);
			query.setParameter("tipoQuestionario", tipo);

			List<Questionario> q = query.list();

			getSession().close();

			if (q != null) {
				return q;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public PrazoQuestionario getPrazoSemestre(Questionario questionario,Semestre semestre)//retorna o o prazo dentro do semestre
	{
		PrazoQuestionarioDAO pqDAO = new PrazoQuestionarioDAO();
		List<PrazoQuestionario> prazos = pqDAO.getPrazos(questionario);
		if(prazos.size()>0 && semestre!=null)
		{
			PrazoQuestionario auxPrazo = prazos.get(0);
			for(int i=0;i<prazos.size();i++)
			{
				if(prazos.get(i).getDataFinal().after(auxPrazo.getDataFinal()))
					auxPrazo = prazos.get(i);
			}
			
		
			Date novaData = new Date();
			novaData.setDate(novaData.getDate()-1);
			
			if(semestre.getDataFinalSemestre().after(auxPrazo.getDataFinal()))
			{
				if(auxPrazo.getDataFinal().after(novaData))
				{
					return auxPrazo;
				}
			}
		}
		return null;
	}
	
}
