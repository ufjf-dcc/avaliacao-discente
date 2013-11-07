package br.ufjf.avaliacao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

/**
 * DTO da Tabela {@code Pergunta} contém os atributos e relacionamentos da
 * mesma.
 * 
 */
@Entity
@Table(name = "Pergunta")
public class Pergunta implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Campo com ID da pergunta. Relaciona com a coluna
	 * {@code idPergunta} do banco e é gerado por autoincrement do MySQL
	 * através das anotações {@code @GeneratedValue(generator = "increment")} e
	 * {@code @GenericGenerator(name = "increment", strategy = "increment")}
	 * 
	 */
	@Id
	@Column(name = "idPergunta", unique = true, nullable = false)
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int idPergunta;
	
	
	/**
	 * Campo com o título da pergunta. Relaciona com a coluna
	 * {@code pergunta} do banco através da anotação
	 * {@code @Column(name = "pergunta", length = 45, nullable = false)}.
	 */
	@Column(name = "tituloPergunta", length = 120, nullable = false)
	private String tituloPergunta;

	
	@Column(name = "tipoPergunta", nullable = false)
	private Integer tipoPergunta;

	
	/**
	 * Relacionamento N para 1 entre pergunta e questionário. Mapeando
	 * {@link Questionario} na variável {@code questionario} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos a {@link Pergunta}.
	 * 
	 */	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idQuestionario", nullable = false)
	private Questionario questionario;

	@Transient
	private String nomeTipoPergunta;

	public int getIdPergunta() {
		return idPergunta;
	}


	public void setIdPergunta(int idPergunta) {
		this.idPergunta = idPergunta;
	}


	public String getTituloPergunta() {
		return tituloPergunta;
	}


	public void setTituloPergunta(String tituloPergunta) {
		this.tituloPergunta = tituloPergunta;
	}


	public Integer getTipoPergunta() {
		return tipoPergunta;
	}


	public void setTipoPergunta(Integer tipoPergunta) {
		this.tipoPergunta = tipoPergunta;
	}


	public Questionario getQuestionario() {
		return questionario;
	}


	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}


	public String getNomeTipoPergunta() {
		if (tipoPergunta==0)
			return "Texto";
		else if (tipoPergunta==1)
			return "Escala numérica";
		else if (tipoPergunta == 2)
			return "Escala conceitual";
		else return "Sim/Não";
	}

	
	public void setNomeTipoPergunta(String nomeTipoPergunta) {
		this.nomeTipoPergunta = nomeTipoPergunta;
	}
	
	
	
}