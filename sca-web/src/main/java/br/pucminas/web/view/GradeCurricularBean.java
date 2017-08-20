package br.pucminas.web.view;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.Service;

import br.pucminas.web.model.Curriculo;
import br.pucminas.web.model.GradeCurricular;

/**
 * Backing bean for GradeCurricular entities.
 * <p/>
 * This class provides CRUD functionality for all GradeCurricular entities. It
 * focuses purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt>
 * for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class GradeCurricularBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving GradeCurricular entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private GradeCurricular gradeCurricular;

	public GradeCurricular getGradeCurricular() {
		return this.gradeCurricular;
	}

	public void setGradeCurricular(GradeCurricular gradeCurricular) {
		this.gradeCurricular = gradeCurricular;
	}

	@Inject
	private Conversation conversation;

	@PersistenceContext(unitName = "curso-service-persistence-unit", type = PersistenceContextType.EXTENDED)
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
			this.gradeCurricular = this.example;
		} else {
			this.gradeCurricular = findById(getId());
		}
	}

	public GradeCurricular findById(Long id) {

		return this.entityManager.find(GradeCurricular.class, id);
	}

	/*
	 * Support updating and deleting GradeCurricular entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.gradeCurricular);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.gradeCurricular);
				return "view?faces-redirect=true&id="
						+ this.gradeCurricular.getId();
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
			GradeCurricular deletableEntity = findById(getId());
			Curriculo curriculo = deletableEntity.getCurriculo();
			curriculo.getGradeCurricular().remove(deletableEntity);
			deletableEntity.setCurriculo(null);
			this.entityManager.merge(curriculo);
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
	 * Support searching GradeCurricular entities with pagination
	 */

	private int page;
	private long count;
	private List<GradeCurricular> pageItems;

	private GradeCurricular example = new GradeCurricular();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public GradeCurricular getExample() {
		return this.example;
	}

	public void setExample(GradeCurricular example) {
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
		Root<GradeCurricular> root = countCriteria.from(GradeCurricular.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<GradeCurricular> criteria = builder
				.createQuery(GradeCurricular.class);
		root = criteria.from(GradeCurricular.class);
		TypedQuery<GradeCurricular> query = this.entityManager
				.createQuery(criteria.select(root).where(
						getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<GradeCurricular> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		Curriculo curriculo = this.example.getCurriculo();
		if (curriculo != null) {
			predicatesList.add(builder.equal(root.get("curriculo"), curriculo));
		}
		Long disciplina = this.example.getDisciplina();
		if (disciplina != null && disciplina.intValue() != 0) {
			predicatesList
					.add(builder.equal(root.get("disciplina"), disciplina));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<GradeCurricular> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}
	
	public String discoverServiceURI(String name) {

		ConsulClient client = getConsulClient();
		Map<String, Service> agentServices = client.getAgentServices().getValue();

		Service match = null;

		for (Map.Entry<String, Service> entry : agentServices.entrySet()) {
			if(entry.getValue().getService().equals(name)) {
				match = entry.getValue();
				break;
			}
		}

		if(null==match)
			throw new RuntimeException("Service '"+name+"' cannot be found!");
		
		try {
			URL url = new URL("http://"+match.getAddress()+":"+match.getPort());
			return url.toExternalForm();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private ConsulClient getConsulClient() {
		String consulHost = System.getProperty("consul.host", "127.0.0.1"); // DOCKER
		ConsulClient client = new ConsulClient(consulHost);
		return client;
	}

	/*
	 * Support listing and POSTing back GradeCurricular entities (e.g. from
	 * inside an HtmlSelectOneMenu)
	 */

	public List<GradeCurricular> getAll() {

		CriteriaQuery<GradeCurricular> criteria = this.entityManager
				.getCriteriaBuilder().createQuery(GradeCurricular.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(GradeCurricular.class)))
				.getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final GradeCurricularBean ejbProxy = this.sessionContext
				.getBusinessObject(GradeCurricularBean.class);

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

				return String.valueOf(((GradeCurricular) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private GradeCurricular add = new GradeCurricular();

	public GradeCurricular getAdd() {
		return this.add;
	}

	public GradeCurricular getAdded() {
		GradeCurricular added = this.add;
		this.add = new GradeCurricular();
		return added;
	}
}
