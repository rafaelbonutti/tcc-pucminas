package br.pucminas.aluno.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.pucminas.aluno.model.Aluno;

/**
 * Backing bean for Aluno entities.
 * <p/>
 * This class provides CRUD functionality for all Aluno entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class AlunoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Aluno entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Aluno aluno;

	public Aluno getAluno() {
		return this.aluno;
	}

	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}

	@Inject
	private Conversation conversation;

	@PersistenceContext(unitName = "aluno-service-persistence-unit", type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	public String create() {

		this.conversation.begin();
		this.conversation.setTimeout(1800000L);
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.conversation.isTransient()) {
			this.conversation.begin();
			this.conversation.setTimeout(1800000L);
		}

		if (this.id == null) {
			this.aluno = this.example;
		} else {
			this.aluno = findById(getId());
		}
	}

	public Aluno findById(Long id) {

		return this.entityManager.find(Aluno.class, id);
	}

	/*
	 * Support updating and deleting Aluno entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.aluno);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.aluno);
				return "view?faces-redirect=true&id=" + this.aluno.getId();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public String delete() {
		this.conversation.end();

		try {
			Aluno deletableEntity = findById(getId());

			this.entityManager.remove(deletableEntity);
			this.entityManager.flush();
			return "search?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching Aluno entities with pagination
	 */

	private int page;
	private long count;
	private List<Aluno> pageItems;

	private Aluno example = new Aluno();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Aluno getExample() {
		return this.example;
	}

	public void setExample(Aluno example) {
		this.example = example;
	}

	public String search() {
		this.page = 0;
		return null;
	}

	public void paginate() {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Aluno> root = countCriteria.from(Aluno.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Aluno> criteria = builder.createQuery(Aluno.class);
		root = criteria.from(Aluno.class);
		TypedQuery<Aluno> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Aluno> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String nome = this.example.getNome();
		if (nome != null && !"".equals(nome)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("nome")),
					'%' + nome.toLowerCase() + '%'));
		}
		Long cpf = this.example.getCpf();
		if (cpf != null && cpf.intValue() != 0) {
			predicatesList.add(builder.equal(root.get("cpf"), cpf));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Aluno> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Aluno entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Aluno> getAll() {

		CriteriaQuery<Aluno> criteria = this.entityManager.getCriteriaBuilder()
				.createQuery(Aluno.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Aluno.class))).getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final AlunoBean ejbProxy = this.sessionContext
				.getBusinessObject(AlunoBean.class);

		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context,
					UIComponent component, String value) {

				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context,
					UIComponent component, Object value) {

				if (value == null) {
					return "";
				}

				return String.valueOf(((Aluno) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Aluno add = new Aluno();

	public Aluno getAdd() {
		return this.add;
	}

	public Aluno getAdded() {
		Aluno added = this.add;
		this.add = new Aluno();
		return added;
	}
}
