package br.ufjf.avaliacao.persistent.impl;

import java.util.List;

import org.hibernate.Query;

import br.ufjf.avaliacao.model.Avaliacao;
import br.ufjf.avaliacao.model.PrazoQuestionario;
import br.ufjf.avaliacao.model.Questionario;
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
	
	public boolean jaAvaliouNestePrazo(PrazoQuestionario prazo) {// procura se há alguma avaliação no que possua o prazo em questão
		try {
			Query query = getSession()
					.createQuery(
							"SELECT a FROM Avaliacao AS a LEFT JOIN FETCH a.prazoQuestionario WHERE a.prazoQuestionario = :prazo");
			query.setParameter("prazo", prazo);

			@SuppressWarnings("unchecked")
			List<Avaliacao> a = query.list();
			
			getSession().close();

			if (!a.isEmpty())// se sim retorna true
				return true;
			else					//se nao retorna false
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean jaAvaliouCoordenador(PrazoQuestionario prazo, Usuario aluno){
		try {
			Query query = getSession() // selecionar as avalições de coordenadores que estao no prazo
					.createQuery(
							"SELECT a FROM a AS a LEFT JOIN FETCH a.prazoQuestionario LEFT JOIN FETCH a.avaliado AS u WHERE  a.prazoQuestionario := prazo AND a.avaliado.tipoUsuario := tipoUsuario");
			query.setParameter("prazo", prazo);
			query.setParameter("tipoUsuario", 0);


			@SuppressWarnings("unchecked")
			List<Avaliacao> a = query.list();
			
			getSession().close();

			for(int i=0;i<a.size();i++){ // olha todas as avaliações
				if(a.get(i).getAvaliado().getIdUsuario() == aluno.getIdUsuario())// verifica se esse aluno fez uma dessas avaliações 
					return true;
			}
			return false;
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
