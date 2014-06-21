package br.ufjf.avaliacao.business;

import org.hibernate.HibernateException;

import br.ufjf.avaliacao.model.Disciplina;
import br.ufjf.avaliacao.persistent.impl.TurmaDAO;

public class TurmaBusiness extends GenericBusiness {
	public boolean cadastrado(String letraTurma, String semestre,
			Disciplina disciplina) throws HibernateException, Exception {
		TurmaDAO turmaDAO = new TurmaDAO();
		if ((turmaDAO.retornaTurma(letraTurma, semestre, disciplina) != null))
			return true;
		else
			return false;
	}

	// Verifica a validade do cadastro todo de uma vez com a função
	// campoStrValido da classe GenericBusiness
	public boolean cadastroValido(String letraTurma, String semestre) {
		if (campoStrValido(letraTurma) && campoStrValido(semestre))
			return true;
		else
			return false;
	}
}
