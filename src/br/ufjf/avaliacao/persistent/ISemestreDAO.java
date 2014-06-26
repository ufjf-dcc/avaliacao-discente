package br.ufjf.avaliacao.persistent;

import java.util.List;

import br.ufjf.avaliacao.model.Semestre;

public interface ISemestreDAO {
	
	public Semestre getSemestreAtual();
	
	public List<Semestre> getAllSemestres();

}
