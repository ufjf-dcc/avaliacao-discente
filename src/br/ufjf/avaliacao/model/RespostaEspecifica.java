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

import org.hibernate.annotations.GenericGenerator;

/**
 * DTO da Tabela {@code RespostaEspecifica} contém os atributos e
 * relacionamentos da mesma.
 * 
 */
@Entity
@Table(name = "RespostaEspecifica")
public class RespostaEspecifica implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Campo com ID da resposta especifica. Relaciona com a coluna
	 * {@code idRespostaEspecifica} do banco e é gerado por autoincrement do MySQL
	 * através das anotações {@code @GeneratedValue(generator = "increment")} e
	 * {@code @GenericGenerator(name = "increment", strategy = "increment")}
	 * 
	 */
	@Id
	@Column(name = "idRespostaEspecifica", unique = true, nullable = false)
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int idRespostaEspecifica;
	
	/**
	 * Coluna com a opção de resposta personalizada
	 * criada pelo usuário.
	 * 
	 */
	
	@Column(name = "respostaEspecifica", length = 60, nullable = false)
	private String respostaEspecifica;
	
	
	/**
	 * Relacionamento N para 1 entre resposta especifica e pergunta. Mapeando
	 * {@link Pergunta} na variável {@code pergunta} e retorno do tipo
	 * {@code EAGER} que indica que será carregado automaticamente este dado
	 * quando retornarmos a {@link RespostaEspecifica}.
	 * 
	 */	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idPergunta", nullable = false)
	private Pergunta pergunta;


	public int getIdRespostaEspecifica() {
		return idRespostaEspecifica;
	}
	public void setIdRespostaEspecifica(int idRespostaEspecifica) {
		this.idRespostaEspecifica = idRespostaEspecifica;
	}
	public String getRespostaEspecifica() {
		return respostaEspecifica;
	}
	public void setRespostaEspecifica(String respostaEspecifica) {
		this.respostaEspecifica = respostaEspecifica;
	}
	public Pergunta getPergunta() {
		return pergunta;
	}
	public void setPergunta(Pergunta pergunta) {
		this.pergunta = pergunta;
	}
	
	
	
		
	
	
}