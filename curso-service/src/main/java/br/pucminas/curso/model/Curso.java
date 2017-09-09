package br.pucminas.curso.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "TBCURSO")
@XmlRootElement
@ApiModel( value = "Curso", description = "Curso oferecido pela universidade" )
public class Curso implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty( value = "Identificador único do curso", required = true )
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private Long id;

	@ApiModelProperty( value = "Versão", required = false )
	@Version
	@Column(name = "VERSION")
	private int version;

	@ApiModelProperty( value = "Nome do curso", required = true )
	@Column(length = 100, name = "NOME", nullable = false)
	private String nome;

	@ApiModelProperty( value = "Código do curso", required = true )
	@Column(length = 3, name = "CODIGO", nullable = false)
	private String codigo;

	@ApiModelProperty( value = "Descrição detalhada do curso", required = false )
	@Lob
	@Column(length = 2147483647, name = "DESCRICAO")
	private String descricao;

	@ApiModelProperty( value = "Modalidae do curso", required = true)
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "MODALIDADE", length = 1, nullable = false)
	private Modalidade modalidade;

	@ApiModelProperty( value = "Lista de Currículos do curso", required = false )
	@JsonIgnoreProperties("curso")
	@OneToMany(targetEntity = Curriculo.class, mappedBy = "curso", fetch = FetchType.EAGER)
	private Set<Curriculo> curriculo = new HashSet<Curriculo>();

	@ApiModelProperty( value = "Currículo atual do curso", required = false )
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "FKCURRICULOATUAL")
	private Curriculo curriculoAtual;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Curso)) {
			return false;
		}
		Curso other = (Curso) obj;
		if (id != null) {
			if (!id.equals(other.id)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Modalidade getModalidade() {
		return modalidade;
	}

	public void setModalidade(Modalidade modalidade) {
		this.modalidade = modalidade;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (nome != null && !nome.trim().isEmpty())
			result += "nome: " + nome;
		if (descricao != null && !descricao.trim().isEmpty())
			result += ", descricao: " + descricao;
		return result;
	}

	public Set<Curriculo> getCurriculo() {
		return this.curriculo;
	}

	public void setCurriculo(final Set<Curriculo> curriculo) {
		this.curriculo = curriculo;
	}

	public Curriculo getCurriculoAtual() {
		return curriculoAtual;
	}

	public void setCurriculoAtual(Curriculo curriculoAtual) {
		this.curriculoAtual = curriculoAtual;
	}

}