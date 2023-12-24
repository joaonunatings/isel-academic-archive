package pt.isel.tsma.entity.proxy;

import org.springframework.data.repository.Repository;

public interface Proxy<E, T extends Repository<?, ?>> {
	E build();

	void save(T repository);
}
