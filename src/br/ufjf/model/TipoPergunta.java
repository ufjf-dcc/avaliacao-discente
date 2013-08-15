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
 * DTO da Tabela {@code Tipo_Pergunta} contém os atributos e relacionamentos da
 * mesma.
 * 
 */
@Entity
@Table(name = "Tipo_Pergunta")
public class TipoPergunta implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Campo com ID do tipo de pergunta. Relaciona com a coluna
	 * {@code idTipo_Pergunta} do banco e é gerado por autoincrement do MySQL
	 * através das anotações {@code @GeneratedValue(generator = "increment")} e
	 * {@code @GenericGenerator(name = "increment", strategy = "increment")}
	 * 
	 */
	@Id
	@Column(name = "idTipo_Pergunta", unique = true, nullable = false)
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int idTipoPergunta;
	
	
	/**
	 * Campo com a descrição do tipo de pergunta. Relaciona com a coluna
	 * {@code tipo_pergunta} do banco através da anotação
	 * {@code @Column(name = "tipo_pergunta", length = 45, nullable = false)}.
	 */
	@Column(name = "tipo_pergunta", length = 45, nullable = false)
	private String tipoPergunta;
	
	
	/**
	 * Relacionamento 1 para N entre tipo de pergunta e pergunta. Mapeada em
	 * {@link Pergunta} pela variável {@code tipoPergunta} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos a {@link Tipo_Pergunta} .
	 * 
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoPergunta" )
	private List<Pergunta> perguntas = new ArrayList<Pergunta>();


	public int getIdTipoPergunta() {
		return idTipoPergunta;
	}


	public void setIdTipoPergunta(int idTipoPergunta) {
		this.idTipoPergunta = idTipoPergunta;
	}


	public String getTipoPergunta() {
		return tipoPergunta;
	}


	public void setTipoPergunta(String tipoPergunta) {
		this.tipoPergunta = tipoPergunta;
	}


	public List<Pergunta> getPerguntas() {
		return perguntas;
	}


	public void setPerguntas(List<Pergunta> perguntas) {
		this.perguntas = perguntas;
	}
	
}