package br.ufjf.avaliacao.persistent.impl;

import java.util.Date;
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

	
	//se ja avaliou todos os professores daquela turma retorna true
	public boolean jaAvaliou(Usuario usuario, Turma turma) {
			AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
			if(avaliacaoDAO.jaAvaliouTodosProfessoresTurma(usuario, turma))
				return true;
			return false;

	}

	// procura se há alguma avaliação no que possua o prazo em questão
	public boolean jaAvaliouNestePrazo(PrazoQuestionario prazo, Usuario aluno) {
		try {
			Query query = getSession()
					.createQuery(
							"SELECT a FROM Avaliacao AS a LEFT JOIN FETCH a.prazoQuestionario AS p WHERE p = :prazo AND a.avaliando = :aluno");
			query.setParameter("prazo", prazo);
			query.setParameter("aluno", aluno);


			@SuppressWarnings("unchecked")
			List<Avaliacao> a = query.list();
			
			getSession().close();

			if (!a.isEmpty()){// se sim retorna true
				return true;
			}
			else					//se nao retorna false
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// verifica se o coordenador ja foi avaliado nesse prazo
	public boolean jaAvaliouCoordenadorDataAtual(Usuario aluno){
		try {
			Query query = getSession() // carrega as avaliaçoes que esse aluno ja fez
					.createQuery(
							"SELECT a FROM Avaliacao AS a LEFT JOIN FETCH a.avaliado LEFT JOIN FETCH a.prazoQuestionario AS p WHERE :dataAtual BETWEEN p.dataInicial AND p.dataFinal AND a.avaliando = :aluno");
			query.setParameter("dataAtual", new Date());
			query.setParameter("aluno", aluno);
			//verifica as avaliaçoes(avaliaçoes de coordenador) com esse usuario que estao ativas agora

			@SuppressWarnings("unchecked")
			List<Avaliacao> a = query.list();
			
			getSession().close();

			if (!a.isEmpty()){// verific se esta vazio
				for(int i=0;i<a.size();i++){ // verifica se alguma foi feita para um coordenador
					if(a.get(i).getAvaliado().getTipoUsuario()==0) //se sim retorna true
						return true;
				}
			}
			else					//se nao retorna false
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// verifica se o usuario ja se avaliou com o prazo dentro da data atual.
	public boolean jaSeAvaliorDataAtual(Usuario aluno){
		try {
			Query query = getSession() // carrega as avaliaçoes que esse aluno ja fez
					.createQuery(
							"SELECT a FROM Avaliacao AS a LEFT JOIN FETCH a.avaliado LEFT JOIN FETCH a.prazoQuestionario AS p WHERE :dataAtual BETWEEN p.dataInicial AND p.dataFinal AND a.avaliando = :aluno");
			query.setParameter("dataAtual", new Date());
			query.setParameter("aluno", aluno);
			//verifica as avaliaçoes(avaliaçoes de coordenador) com esse usuario que estao ativas agora

			@SuppressWarnings("unchecked")
			List<Avaliacao> a = query.list();
			
			getSession().close();

			if (!a.isEmpty()){// verific se esta vazio
				for(int i=0;i<a.size();i++){ // verifica se alguma foi feita para um coordenador
					if(a.get(i).getAvaliado().getTipoUsuario()==2) //se sim retorna true
						return true;
				}
			}
			else					//se nao retorna false
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// verifica se a infraestrutura ja foi avaliado nesse prazo
	public boolean jaAvaliouInfraestruturaDataAtual(Usuario aluno){
		try {
			Query query = getSession() // carrega as avaliaçoes que esse aluno ja fez
					.createQuery(
							"SELECT a FROM Avaliacao AS a LEFT JOIN FETCH a.avaliado LEFT JOIN FETCH a.prazoQuestionario AS p WHERE :dataAtual BETWEEN p.dataInicial AND p.dataFinal AND a.avaliando = :aluno");
			query.setParameter("dataAtual", new Date());
			query.setParameter("aluno", aluno);
			//verifica as avaliaçoes(avaliaçoes de coordenador) com esse usuario que estao ativas agora

			@SuppressWarnings("unchecked")
			List<Avaliacao> a = query.list();
			
			getSession().close();

			if (!a.isEmpty()){// verific se esta vazio
				for(int i=0;i<a.size();i++){ // verifica se alguma foi feita para um coordenador
					if(a.get(i).getAvaliado()==null) //se sim retorna true
						return true;
				}
			}
			else					//se nao retorna false
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	
	//utilizado pra verificar se um prazo pode ser excluido, se alguem ja avaliou nesse prazo, não pode excluir o prazo
	public boolean alguemJaAvaliou(Questionario questionario){
		try {
			Query query = getSession() // carrega as avaliações daquele questionario naquela data
					.createQuery(
							"SELECT a FROM Avaliacao AS a  LEFT JOIN FETCH a.prazoQuestionario AS p LEFT JOIN FETCH p.questionario WHERE :dataAtual BETWEEN p.dataInicial AND p.dataFinal AND p.questionario = :questionario");
			query.setParameter("dataAtual", new Date());
			query.setParameter("questionario", questionario);


			@SuppressWarnings("unchecked")
			List<Avaliacao> a = query.list();
			
			getSession().close();

			if (!a.isEmpty()){// verific se esta vazio
						return true;
			}
			else					//se nao retorna false
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public List<Avaliacao> avaliacoesTurma(Turma turma) {
		try {
			Query query = getSession().createQuery("SELECT a FROM Avaliacao AS a LEFT JOIN FETCH a.turma AS t WHERE t =: turma");
			query.setParameter("turma", turma);
			
			List<Avaliacao> as = query.list();
			
			getSession().close();
			return as;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	//verifica se um professor ja foi avaliado por esse questionario para avaliar mais de um professor por turma
		public boolean alguemJaAvaliouEsteProfessot(Questionario questionario, Usuario professor){
			try {
				Query query = getSession() // carrega as avaliações daquele questionario com o professor especifico
						.createQuery(
								"SELECT a FROM Avaliacao AS a  LEFT JOIN FETCH a.prazoQuestionario AS p LEFT JOIN LEFT JOIN FETCH p.questionario FETCH a.avaliado  WHERE a.avaliado = :professor AND p.questionario = :questionario");
				query.setParameter("questionario", questionario);
				query.setParameter("professor", professor);

				@SuppressWarnings("unchecked")
				List<Avaliacao> a = query.list();
				
				getSession().close();

				if (!a.isEmpty()){// verific se esta vazio
							return true;
				}
				else					//se nao retorna false
					return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		
		//utilizado pra verificar se uma pessoa ja avaliou outra
		public boolean avaliadoEAvaliando(Usuario avaliado, Usuario avaliando){
			try {
				Query query = getSession() // carrega as avaliações daquele questionario com o professor especifico
						.createQuery(
								"SELECT a FROM Avaliacao AS a LEFT JOIN FETCH a.avaliado WHERE a.avaliando = :avaliando AND a.avaliado = :avaliado");
				query.setParameter("avaliando", avaliando);
				query.setParameter("avaliado", avaliado);
				
				@SuppressWarnings("unchecked")
				List<Avaliacao> a = query.list();
				getSession().close();
				
				if (!a.isEmpty()){// verific se esta vazio
					return true;
				}
				else					//se nao retorna false
					return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		
		//verifica se o aluno ja avaliou todos os professores da turma em questão
		public boolean jaAvaliouTodosProfessoresTurma(Usuario aluno, Turma turma){
			UsuarioDAO usuarioDAO = new UsuarioDAO();
			List<Usuario> professores = usuarioDAO.retornaProfessoresTurma(turma);
			
			for(int i=0;i<professores.size();i++){
				if(!alunoJaAvaliouEsteProfessor(aluno, professores.get(i), turma))
					return false;
			}
			return true;
		}
		
		public List<Usuario> retornaProfessoresNaoAvaliados(Usuario aluno,Turma turma){
			UsuarioDAO usuarioDAO = new UsuarioDAO();
			List<Usuario> professores = usuarioDAO.retornaProfessoresTurma(turma);
			
			for(int i=0;i<professores.size();i++){
				if(alunoJaAvaliouEsteProfessor(aluno, professores.get(i), turma))
					professores.remove(i);
			}
			return professores;
		}

		public boolean alunoJaAvaliouEsteProfessor(Usuario aluno,Usuario professor,Turma turma){
			try {
				Query query = getSession() // carrega as avaliações daquele questionario com o professor especifico
						.createQuery(
								"SELECT a FROM Avaliacao AS a  LEFT JOIN FETCH a.avaliado LEFT JOIN FETCH a.turma WHERE a.avaliado = :professor AND a.avaliando = :aluno AND a.turma = :turma ");
				query.setParameter("turma", turma);
				query.setParameter("professor", professor);
				query.setParameter("aluno", aluno);


				@SuppressWarnings("unchecked")
				List<Avaliacao> a = query.list();
				
				getSession().close();

				if (!a.isEmpty()){// verific se esta vazio
							return true;
				}
				else					//se nao retorna false
					return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		
}

