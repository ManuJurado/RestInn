package RestInn.repositories;

import RestInn.entities.Habitacion;
import RestInn.entities.enums.H_Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {

    Optional<Habitacion> findByNumero(String numero);

    @Query (value ="select * from habitacion where activo = true and estado = ?1",
            nativeQuery = true)
    List<Habitacion> findByH_Estado(H_Estado estado);

    @Query (value ="select * from habitacion where activo = true and cantidad_camas = ?1",
            nativeQuery = true)
    List<Habitacion> findByCantCamas(Integer cantCamas);

    @Query (value ="select * from habitacion where activo = true and capacidad = ?1",
            nativeQuery = true)
    List<Habitacion> findByCapacidad(Integer capacidad);

    @Query (value ="select * from habitacion where activo = true and tipo = ?1",
            nativeQuery = true)
    List<Habitacion> findByTipo(String tipo);

    @Query (value ="select * from habitacion where activo = true and estado = ?1",
            nativeQuery = true)
    List<Habitacion> findByNumeroAndTipo(String numero, String tipo);

    @Query (value ="select * from habitacion where numero = ?1",
            nativeQuery = true
    )
    Habitacion getHabitacionByNumero(String numero);

    @Transactional
    @Modifying
    @Query (value = "update tbl_customer set first_name = ?1 where email_address = ?2",
            nativeQuery = true
    )
    void updateCustomerNameByEmail(String name, String email);

}