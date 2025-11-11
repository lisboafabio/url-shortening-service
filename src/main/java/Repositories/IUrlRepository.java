package Repositories;

import Entities.Url;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUrlRepository extends JpaRepository<Url, Long> {
}
