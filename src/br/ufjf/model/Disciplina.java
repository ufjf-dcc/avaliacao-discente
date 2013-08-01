package br.ufjf.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * DTO da Tabela {@code Disciplina} contém os atributos e relacionamentos da
 * mesma.
 * 
 */
@Entity
@Table(name = "Disciplina")
public class Disciplina implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Campo com ID da disciplina. Relaciona com a coluna
	 * {@code idDisciplina} do banco e é gerado por autoincrement do MySQL
	 * através das anotações {@code @GeneratedValue(generator = "increment")} e
	 * {@code @GenericGenerator(name = "increment", strategy = "increment")}
	 * 
	 */
	@Id
	@Column(name = "idDisciplina", unique = true, nullable = false)
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int idDisciplina;
	
	
	/**
	 * Campo com o nome da disciplina. Relaciona com a coluna
	 * {@code nomeDisciplina} do banco através da anotação
	 * {@code @Column(name = "nomeDisciplina", length = 45, nullable = false)}.
	 */
	@Column(name = "nomeDisciplina", length = 45, nullable = false)
	private String nomeDisciplina;
	
	
	/**
	 * Relacionamento 1 para N entre disciplina e turma. Mapeada em
	 * {@link Turma} pela variável {@code disciplina} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos a {@link Disciplina} .
	 * 
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "disciplina")
	private List<Turma> turmas = new ArrayList<Turma>();

	public int getIdDisciplina() {
		return idDisciplina;
	}

	public void setIdDisciplina(int idDisciplina) {
		this.idDisciplina = idDisciplina;
	}

	public String getNomeDisciplina() {
		return nomeDisciplina;
	}

	public void setNomeDisciplina(String nomeDisciplina) {
		this.nomeDisciplina = nomeDisciplina;
	}

	public List<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(List<Turma> turmas) {
		this.turmas = turmas;
	}

}