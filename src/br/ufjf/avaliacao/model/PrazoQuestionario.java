package br.ufjf.avaliacao.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

/**
 * DTO da Tabela {@code PrazoQuestionario} contém os atributos e
 * relacionamentos da mesma.
 * 
 */

@Entity
@Table(name = "PrazoQuestionario")
public class PrazoQuestionario implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Campo com ID do prazo do questionario. Relaciona com a coluna
	 * {@code idPrazoQuesionario} do banco e é gerado por autoincrement do
	 * MySQL através das anotações
	 * {@code @GeneratedValue(generator = "increment")} e
	 * {@code @GenericGenerator(name = "increment", strategy = "increment")}
	 * 
	 */
	@Id
	@Column(name = "idPrazoQuestionario", unique = true, nullable = false)
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int idPrazoQuestionario;

	/**
	 * Campo com a data inicial do questionario. Relaciona com a coluna
	 * {@code dataInicial} do banco através da anotação
	 * {@code @Column(name = "dataInicial", nullable = false)} .
	 */
	@Column(name = "dataInicial", nullable = false)
	private Date dataInicial;

	/**
	 * Campo com a data final do questionario. Relaciona com a coluna
	 * {@code dataFinal} do banco através da anotação
	 * {@code @Column(name = "dataFinal", nullable = false)} .
	 */
	@Column(name = "dataFinal", nullable = false)
	private Date dataFinal;

	/**
	 * Relacionamento 1 para N entre prazoQuestionário e avaliação. Mapeada
	 * em {@link Avaliação} pela variável {@code prazoquestionario} e retorno
	 * do tipo {@code LAZY} que indica que não será carregado automáticamente
	 * este dado quando retornarmos o {@link PrazoQuestionario} .
	 * 
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "prazoQuestionario")
	private List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();

	/**
	 * Relacionamento N para 1 entre prazoQuestionario e questionario. Mapeando
	 * {@link Questionario} na variável {@code questionario} e retorno do tipo
	 * {@code EAGER} que indica que será carregado automáticamente este dado
	 * quando retornarmos o {@link PrazoQuestionario}.
	 * 
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idQuestionario", nullable = false)
	private Questionario questionario;

	@Transient
	private String dataFinalFormatada;

	@Transient
	private String dataInicialFormatada;

	public int getIdPrazoQuestionario() {
		return idPrazoQuestionario;
	}

	public void setIdPrazoQuestionario(int idPrazoQuestionario) {
		this.idPrazoQuestionario = idPrazoQuestionario;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Questionario getQuestionario() {
		return questionario;
	}

	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	@SuppressWarnings("deprecation")
	public String getDataFinalFormatada() {
		return (dataFinal.getDate() + "/" + (dataFinal.getMonth() + 1) + "/" + (dataFinal
				.getYear() + 1900));
	}

	public void setDataFinalFormatada(String dataFinalFormatada) {
		this.dataFinalFormatada = dataFinalFormatada;
	}

	@SuppressWarnings("deprecation")
	public String getDataInicialFormatada() {
		return (dataInicial.getDate() + "/" + (dataInicial.getMonth() + 1)
				+ "/" + (dataInicial.getYear() + 1900));
	}

	public void setDataInicialFormatada(String dataInicialFormatada) {
		this.dataInicialFormatada = dataInicialFormatada;
	}

	public List<Avaliacao> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(List<Avaliacao> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

}
