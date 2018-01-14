package space.paperless.domain;

import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "name", "leaves" })
public class IndexElement implements Comparable<IndexElement> {

	private String id;
	private String name;
	private Set<IndexElement> leaves;

	public IndexElement() {
		super();
	}

	public IndexElement(String id, String name, Set<IndexElement> leaves) {
		super();
		this.id = id;
		this.name = name;
		this.leaves = leaves;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<IndexElement> getLeaves() {
		return leaves;
	}

	public void setLeaves(Set<IndexElement> leaves) {
		if (leaves != null && !(leaves instanceof TreeSet)) {
			this.leaves = new TreeSet<>(leaves);
		} else {
			this.leaves = leaves;
		}
	}

	public void addLeaf(IndexElement leaf) {
		if (leaves == null) {
			leaves = new TreeSet<>();
		}

		leaves.add(leaf);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		IndexElement element = (IndexElement) o;

		return getId() != null ? getId().equals(element.getId()) : element.getId() == null;
	}

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : 0;
	}

	@Override
	public String toString() {
		return "Element{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", leaves=" + leaves + '}';
	}

	@Override
	public int compareTo(IndexElement o) {
		return id.compareTo(o.id);
	}
}
