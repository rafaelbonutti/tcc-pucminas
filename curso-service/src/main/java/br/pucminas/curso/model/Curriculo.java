package br.pucminas.curso.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "TBCURRICULO")
@XmlRootElement
@ApiModel( value = "Currículo", description = "Versão curricular de um determinado curso" )
public class Curriculo implements Serializable {

	@ApiModelProperty( value = "Identificador único do currículo", required = true )
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private Long id;

	@ApiModelProperty( value = "Versão", required = false )
	@Version
	@Column(name = "VERSION")
	private int version;

	@ApiModelProperty( value = "Ano de referência", required = true )
	@Column(length = 4, name = "ANO", nullable = false)
	private Integer ano;

	@ApiModelProperty( value = "Semestre de referência", required = true )
	@Column(length = 1, name = "SEMESTRE", nullable = false)
	private Integer semestre;

	@JsonIgnoreProperties({"curriculo","curriculoAtual"})
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "FKCURSO")
	private Curso curso;

	@OneToMany(mappedBy = "curriculo", fetch=FetchType.EAGER)
	private Set<GradeCurricular> gradeCurricular = new HashSet<GradeCurricular>();

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
		if (!(obj instanceof Curriculo)) {
			return false;
		}
		Curriculo other = (Curriculo) obj;
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

	public Integer getSemestre() {
		return semestre;
	}

	public void setSemestre(Integer semestre) {
		this.semestre = semestre;
	}

	@Override
	public String toString() {
		String result = "";
		if(curso != null)
			result += curso.getNome().toUpperCase();
		if (semestre != null)
			result += " " + semestre;
		if(ano != null)
			result += "/" + ano.toString();		
		return result;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Curso getCurso() {
		return this.curso;
	}

	public void setCurso(final Curso curso) {
		this.curso = curso;
	}

	public Set<GradeCurricular> getGradeCurricular() {
		return this.gradeCurricular;
	}

	public void setGradeCurricular(final Set<GradeCurricular> gradeCurricular) {
		this.gradeCurricular = gradeCurricular;
	}

}