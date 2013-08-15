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
 * DTO da Tabela {@code Tipo_Questionario} contém os atributos e relacionamentos da
 * mesma.
 * 
 */
@Entity
@Table(name = "Tipo_Questionario")
public class TipoQuestionario implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Campo com ID do tipo de questionário. Relaciona com a coluna
	 * {@code idTipo_Questionario} do banco e é gerado por autoincrement do MySQL
	 * através das anotações {@code @GeneratedValue(generator = "increment")} e
	 * {@code @GenericGenerator(name = "increment", strategy = "increment")}
	 * 
	 */
	@Id
	@Column(name = "idTipo_Questionario", unique = true, nullable = false)
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int idTipoQuestionario;
	
	/**
	 * Campo com a descrição do tipo de questionário. Relaciona com a coluna
	 * {@code tipo_questionario} do banco através da anotação
	 * {@code @Column(name = "tipo_questionario", length = 45, nullable = false)}.
	 */
	@Column(name = "tipo_questionario", length = 45, nullable = false)
	private String tipoQuestionario;
	
	/**
	 * Relacionamento 1 para N entre tipo de questionário e questionário. Mapeada em
	 * {@link Questionário} pela variável {@code tipoQuestionario} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos o {@link TipoQuestionario} .
	 * 
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoQuestionario")
	private List<Questionario> questionarios = new ArrayList<Questionario>();

	public int getIdTipoQuestionario() {
		return idTipoQuestionario;
	}

	public void setIdTipoQuestionario(int idTipoQuestionario) {
		this.idTipoQuestionario = idTipoQuestionario;
	}

	public String getTipoQustionario() {
		return tipoQuestionario;
	}

	public void setTipoQuestionario(String tipoQustionario) {
		this.tipoQuestionario = tipoQustionario;
	}

	public List<Questionario> getQuestionarios() {
		return questionarios;
	}

	public void setQuestionarios(List<Questionario> questionarios) {
		this.questionarios = questionarios;
	}
	
	
}
