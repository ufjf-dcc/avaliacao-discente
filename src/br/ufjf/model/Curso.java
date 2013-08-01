package br.ufjf.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
* DTO da Tabela {@code Curso} contém os atributos e relacionamentos da
* mesma.
* 
*/
@Entity
@Table(name = "Curso")
public class Curso implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * Campo com ID do curso. Relaciona com a coluna {@code idCurso} do banco e
	 * é gerado por autoincrement do MySQL através das anotações
	 * {@code @GeneratedValue(generator = "increment")} e
	 * {@code @GenericGenerator(name = "increment", strategy = "increment")}
	 * 
	 */
	@Id
	@Column(name = "idCurso", unique = true, nullable = false)
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int idCurso;
	
	
	/**
	 * Campo com o nome do curso. Relaciona com a coluna {@code nomeCurso} do
	 * banco através da anotação
	 * {@code @Column(name = "nomeCurso", length = 45, nullable = false)}.
	 */
	@Column(name = "nomeCurso", length = 45, nullable = false)
	private String nomeCurso;
	
	
	/**
	 * Relacionamento N para N entre curso e questionário. Mapeando
	 * {@link Questionario} na variável {@code questionarios} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos o {@link Curso}.
	 * 
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "Questionario_has_Curso", joinColumns = { @JoinColumn(name = "idCurso", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "idQuestionario", nullable = false, updatable = false) })
	private List<Questionario> questionarios = new ArrayList<Questionario>();


	public int getIdCurso() {
		return idCurso;
	}


	public void setIdCurso(int idCurso) {
		this.idCurso = idCurso;
	}


	public String getNomeCurso() {
		return nomeCurso;
	}


	public void setNomeCurso(String nomeCurso) {
		this.nomeCurso = nomeCurso;
	}


	public List<Questionario> getQuestionarios() {
		return questionarios;
	}


	public void setQuestionarios(List<Questionario> questionarios) {
		this.questionarios = questionarios;
	}

	
	
}
